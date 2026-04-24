package com.insurance.interfaceplatform.infrastructure.adapter.filetransfer;

import com.insurance.interfaceplatform.domain.adapter.FileTransferAdapter;
import com.insurance.interfaceplatform.domain.adapter.FileTransferCommand;
import com.insurance.interfaceplatform.domain.adapter.FileTransferResult;
import com.insurance.interfaceplatform.domain.common.FileTransferStatus;
import com.insurance.interfaceplatform.domain.common.TransferDirection;
import com.insurance.interfaceplatform.support.error.CoreException;
import com.insurance.interfaceplatform.support.error.ErrorType;
import java.security.DigestInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

@Component
public class LocalStreamingFileTransferAdapter implements FileTransferAdapter {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    @Override
    public FileTransferResult upload(final FileTransferCommand command) {
        validateDirection(command, TransferDirection.UPLOAD);
        final Path remoteDirectory = Path.of(command.remotePath());
        final Path tempRemotePath = remoteDirectory.resolve(command.tempFileName());
        final Path finalRemotePath = remoteDirectory.resolve(command.finalFileName());
        try {
            Files.createDirectories(remoteDirectory);
            final String checksum = copyWithChecksum(command.stagingPath(), tempRemotePath, command.checksumAlgorithm(), command.streamBufferSize());
            Files.move(tempRemotePath, finalRemotePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return new FileTransferResult(FileTransferStatus.TRANSFERRED, command.finalFileName(), finalRemotePath.toString(),
                    Files.size(finalRemotePath), command.checksumAlgorithm(), checksum);
        } catch (IOException exception) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "파일 스트림 업로드에 실패했습니다.");
        }
    }

    @Override
    public FileTransferResult download(final FileTransferCommand command) {
        validateDirection(command, TransferDirection.DOWNLOAD);
        final Path remoteFilePath = Path.of(command.remotePath()).resolve(command.finalFileName());
        final Path stagingPath = command.stagingPath();
        try {
            Files.createDirectories(stagingPath.getParent());
            final String checksum = copyWithChecksum(remoteFilePath, stagingPath, command.checksumAlgorithm(), command.streamBufferSize());
            return new FileTransferResult(FileTransferStatus.TRANSFERRED, stagingPath.getFileName().toString(), remoteFilePath.toString(),
                    Files.size(stagingPath), command.checksumAlgorithm(), checksum);
        } catch (IOException exception) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "파일 스트림 다운로드에 실패했습니다.");
        }
    }

    private void validateDirection(final FileTransferCommand command, final TransferDirection expectedDirection) {
        if (command.transferDirection() != expectedDirection) {
            throw new CoreException(ErrorType.BAD_REQUEST, "파일 전송 방향이 올바르지 않습니다.");
        }
    }

    private String copyWithChecksum(
            final Path source,
            final Path target,
            final String checksumAlgorithm,
            final int streamBufferSize
    ) throws IOException {
        final MessageDigest messageDigest = messageDigest(checksumAlgorithm);
        final int bufferSize = streamBufferSize <= 0 ? DEFAULT_BUFFER_SIZE : streamBufferSize;
        try (InputStream inputStream = new DigestInputStream(Files.newInputStream(source), messageDigest);
             OutputStream outputStream = Files.newOutputStream(target)) {
            final byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(messageDigest.digest());
    }

    private MessageDigest messageDigest(final String checksumAlgorithm) {
        try {
            return MessageDigest.getInstance(checksumAlgorithm == null ? "SHA-256" : checksumAlgorithm);
        } catch (NoSuchAlgorithmException exception) {
            throw new CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 checksum 알고리즘입니다.");
        }
    }
}
