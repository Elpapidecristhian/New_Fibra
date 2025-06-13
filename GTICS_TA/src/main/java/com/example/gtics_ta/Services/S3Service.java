//package com.example.gtics_ta.Services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.util.UUID;
//
//@Service
//public class S3Service {
////
//    @Autowired
//    private S3Client s3Client;
//
//    @Value("${aws.s3.bucket.name}")
//    private String bucketName;
//
//    @Value("${aws.s3.url.expiration.minutes}")
//    private int urlExpirationMinutes;
//
//    /**
//     * Sube un archivo a S3 y retorna la URL pública
//     */
//    public String uploadFile(MultipartFile file, String folder) throws IOException {
//        // Generar nombre único para el archivo
//        String fileName = generateUniqueFileName(file.getOriginalFilename());
//        String key = folder + "/" + fileName;
//
//        // Configurar metadatos
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .contentType(file.getContentType())
//                .contentLength(file.getSize())
//                .build();
//
//        // Subir archivo
//        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//
//        // Retornar URL pública
//        return getPublicUrl(key);
//    }
//
//    /**
//     * Elimina un archivo de S3
//     */
//    public void deleteFile(String fileUrl) {
//        try {
//            String key = extractKeyFromUrl(fileUrl);
//            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(key)
//                    .build();
//
//            s3Client.deleteObject(deleteObjectRequest);
//        } catch (Exception e) {
//            // Log error pero no fallar la operación
//            System.err.println("Error eliminando archivo de S3: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Genera URL presignada para acceso temporal
//     */
//    public String generatePresignedUrl(String key) {
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//
//        return s3Client.utilities().getUrl(GetUrlRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build()).toString();
//    }
//
//    /**
//     * Obtiene URL pública del archivo
//     */
//    private String getPublicUrl(String key) {
//        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
//    }
//
//    /**
//     * Genera nombre único para el archivo
//     */
//    private String generateUniqueFileName(String originalFileName) {
//        String extension = "";
//        if (originalFileName != null && originalFileName.contains(".")) {
//            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//        }
//        return UUID.randomUUID().toString() + extension;
//    }
//
//    /**
//     * Extrae la key del archivo desde la URL
//     */
//    private String extractKeyFromUrl(String url) {
//        if (url == null || url.isEmpty()) {
//            return "";
//        }
//
//        // Formato: https://bucket-name.s3.amazonaws.com/folder/filename
//        String[] parts = url.split(".s3.amazonaws.com/");
//        if (parts.length > 1) {
//            return parts[1];
//        }
//        return "";
//    }
//
//    /**
//     * Verifica si el bucket existe, si no lo crea
//     */
//    public void ensureBucketExists() {
//        try {
//            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
//        } catch (NoSuchBucketException e) {
//            // Crear bucket si no existe
//            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
//        }
//    }
//}
