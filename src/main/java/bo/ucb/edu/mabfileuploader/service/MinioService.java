package bo.ucb.edu.mabfileuploader.service;

import bo.ucb.edu.mabfileuploader.dto.NewFileDto;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;

    public NewFileDto uploadFile(MultipartFile file, String bucket, boolean customFilename) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fileName = file.getOriginalFilename();
        if(!customFilename){
            //Convert the filename Into a UUID
            fileName = UUID.randomUUID() + "." + fileName.split("\\.")[fileName.split("\\.").length - 1];
        }
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(bucket)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build()
        );
        return new NewFileDto(fileName, file.getContentType(), bucket);


    }
}
