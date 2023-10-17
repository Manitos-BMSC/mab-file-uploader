package bo.ucb.edu.mabfileuploader.dto;

public class FileDto {
    Long s3ObjectId;
    String contentType;
    String bucket;
    String fileName;
    Boolean status = true;


    public FileDto(Long s3ObjectId, String contentType, String bucket, String fileName, Boolean status) {
        this.s3ObjectId = s3ObjectId;
        this.contentType = contentType;
        this.bucket = bucket;
        this.fileName = fileName;
        this.status = status;
    }

    public Long getS3ObjectId() {
        return s3ObjectId;
    }

    public void setS3ObjectId(Long s3ObjectId) {
        this.s3ObjectId = s3ObjectId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
