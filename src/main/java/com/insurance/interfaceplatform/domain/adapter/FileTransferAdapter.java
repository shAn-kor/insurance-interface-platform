package com.insurance.interfaceplatform.domain.adapter;

public interface FileTransferAdapter {

    FileTransferResult upload(FileTransferCommand command);

    FileTransferResult download(FileTransferCommand command);
}
