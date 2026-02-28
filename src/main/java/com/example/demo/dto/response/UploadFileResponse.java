package com.example.demo.dto.response;

public record UploadFileResponse(String fileName, String fileDownloadUri, String fileType, Long file) {
}
