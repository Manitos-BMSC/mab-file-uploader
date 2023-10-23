package bo.ucb.edu.mabfileuploader.api;

import bo.ucb.edu.mabfileuploader.bl.FilesBl;
import bo.ucb.edu.mabfileuploader.dto.FileDto;
import bo.ucb.edu.mabfileuploader.dto.ResponseDto;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/files")
public class FilesController {

    @Autowired
    FilesBl filesBl;

    Logger logger = Logger.getLogger(FilesController.class.getName());

    @PostMapping(consumes = {"multipart/form-data"})
    public FileDto uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("bucket") String bucket,
                             @RequestParam(value = "customFilename",required = false, defaultValue = "false") boolean customFilename
    ) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        logger.info("POST /api/v1/files");
        FileDto fileDto = filesBl.uploadFile(file, bucket, customFilename);
        logger.info("File uploaded and saved on database");
        return fileDto;
    }

    @GetMapping()
    public ResponseDto<String> getFile(@RequestParam String bucket, @RequestParam String fileName){
        logger.info("GET /api/v1/files");
        String responseDto = filesBl.getFile(bucket, fileName);
        ResponseDto<String> response = new ResponseDto<>();
        response.setSuccess(true);
        response.setMessage("OK");
        response.setData(responseDto);
        logger.info(responseDto);

        return  response;
    }
}
