package bo.ucb.edu.mabfileuploader.bl;

import bo.ucb.edu.mabfileuploader.dao.S3Object;
import bo.ucb.edu.mabfileuploader.dao.S3ObjectRepository;
import bo.ucb.edu.mabfileuploader.dto.FileDto;
import bo.ucb.edu.mabfileuploader.dto.NewFileDto;
import bo.ucb.edu.mabfileuploader.service.MinioService;
import feign.Headers;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@Service
public class FilesBl {

    @Autowired
    private MinioService minioService;
    @Autowired
    private S3ObjectRepository s3ObjectRepository;

    Logger logger = Logger.getLogger(FilesBl.class.getName());

    @Headers("Content-Type: multipart/form-data")
    public FileDto uploadFile(MultipartFile file, String bucket, boolean customFilename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        logger.info("Uploading file to bucket: " + bucket);
        NewFileDto newFileDto = minioService.uploadFile(file, bucket, customFilename);
        logger.info("File uploaded to bucket: " + bucket);
        S3Object s3Object = new S3Object();
        s3Object.setBucket(bucket);
        s3Object.setFileName(newFileDto.getFileName());
        s3Object.setContentType(newFileDto.getContentType());
        s3ObjectRepository.save(s3Object);
        S3Object savedS3Object = s3ObjectRepository.save(s3Object);
        return new FileDto(savedS3Object.getS3ObjectId(), savedS3Object.getContentType(), savedS3Object.getBucket(), savedS3Object.getFileName(), savedS3Object.getStatus());
    }

    public String getFile(String bucket, String fileName){
        return minioService.getFile(bucket, fileName);
    }

}
