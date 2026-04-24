package com.insurance.interfaceplatform.infrastructure.adapter.filetransfer;

import com.insurance.interfaceplatform.domain.adapter.FileTransferCommand;
import com.insurance.interfaceplatform.domain.adapter.FileTransferResult;
import com.insurance.interfaceplatform.domain.common.FileTransferStatus;
import com.insurance.interfaceplatform.domain.common.TransferDirection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class LocalStreamingFileTransferAdapterTest {

    @TempDir
    private Path tempDir;

    private final LocalStreamingFileTransferAdapter adapter = new LocalStreamingFileTransferAdapter();

    @Test
    @DisplayName("upload_스테이징파일_스트림으로임시파일전송후최종파일명으로변경한다")
    void upload_stagingFile_streamsToTempThenRenames() throws Exception {
        final Path stagingFile = tempDir.resolve("contract-statistics.csv");
        final Path remoteDirectory = tempDir.resolve("remote");
        Files.writeString(stagingFile, "contractId,premium\n1,1000\n", StandardCharsets.UTF_8);
        final FileTransferCommand command = new FileTransferCommand(1L, "EXE-20260424-000001", TransferDirection.UPLOAD,
                stagingFile, remoteDirectory.toString(), "contract-statistics.csv.part", "contract-statistics.csv", "SHA-256", 4);

        final FileTransferResult result = adapter.upload(command);

        assertThat(result.status()).isEqualTo(FileTransferStatus.TRANSFERRED);
        assertThat(Files.exists(remoteDirectory.resolve("contract-statistics.csv"))).isTrue();
        assertThat(Files.exists(remoteDirectory.resolve("contract-statistics.csv.part"))).isFalse();
        assertThat(result.checksumValue()).isNotBlank();
    }

    @Test
    @DisplayName("download_원격파일_스트림으로스테이징파일에저장한다")
    void download_remoteFile_streamsToStagingFile() throws Exception {
        final Path remoteDirectory = tempDir.resolve("remote");
        Files.createDirectories(remoteDirectory);
        Files.writeString(remoteDirectory.resolve("inbound.csv"), "contractId,status\n1,ACTIVE\n", StandardCharsets.UTF_8);
        final Path stagingFile = tempDir.resolve("staging").resolve("inbound.csv");
        final FileTransferCommand command = new FileTransferCommand(1L, "EXE-20260424-000002", TransferDirection.DOWNLOAD,
                stagingFile, remoteDirectory.toString(), "inbound.csv.part", "inbound.csv", "SHA-256", 4);

        final FileTransferResult result = adapter.download(command);

        assertThat(result.status()).isEqualTo(FileTransferStatus.TRANSFERRED);
        assertThat(Files.readString(stagingFile)).contains("ACTIVE");
        assertThat(result.checksumValue()).isNotBlank();
    }
}
