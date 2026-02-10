package com.example.demo.DTO.response;

public record UploadFileResponse(String fileName, String fileDownloadUri, String fileType, Long file) {
}
