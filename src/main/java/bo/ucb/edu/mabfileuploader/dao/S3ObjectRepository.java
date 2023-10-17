package bo.ucb.edu.mabfileuploader.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface S3ObjectRepository extends JpaRepository<S3Object, Long> {
}
