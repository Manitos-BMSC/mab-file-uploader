package bo.ucb.edu.mabfileuploader.service;

import bo.ucb.edu.mabfileuploader.dto.NewFileDto;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class MinioService {


    @Autowired
    private MinioClient minioClient;

   private Logger logger = Logger.getLogger(MinioService.class.getName());

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

    public String getFile(String bucket, String fileName){
        logger.info("Getting file from bucket: " + bucket);
        logger.info("File name: " + fileName);
        try {
               return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(io.minio.http.Method.GET)
                        .bucket(bucket)
                        .object(fileName)
                        .build());
        }
        catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }
    }
}
