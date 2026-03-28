package com.proptech.backend.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AwsS3Service {

    @Value("${aws.s3.bucket-name:mock-bucket-proptech}")
    private String bucketName;

    @Value("${aws.region:us-east-1}")
    private String region;

    /**
     * Genera una URL Prefirmada MOCK para validar el componente Frontend 
     * sin depender de Credenciales Reales de AWS ni facturación de S3 en etapa inicial.
     */
    public String generatePresignedUrl(String extension, String contentType) {
        String fileName = UUID.randomUUID() + "." + extension;

        // MOCK AWS SIGNATURE: Genera una estructura falsa de presigned url S3
        return String.format("https://%s.s3.%s.amazonaws.com/properties/%s?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=MOCK_CREDENTIAL&X-Amz-Date=20260328T000000Z&X-Amz-Expires=900&X-Amz-SignedHeaders=content-type%%3Bhost&X-Amz-Signature=mock_signature_123",
                bucketName, region, fileName);
    }
}
