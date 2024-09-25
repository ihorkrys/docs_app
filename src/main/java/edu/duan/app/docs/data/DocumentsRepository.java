package edu.duan.app.docs.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface DocumentsRepository extends JpaRepository<DocumentEntity, String> {

    List<DocumentEntity> findAllByUserLogin(String userLogin);
    List<DocumentEntity> findAllByCreatedDateBetween(Date startDate, Date endDate);
    List<DocumentEntity> findAllByUserLoginAndSignedDateIsNull(String userLogin);
    List<DocumentEntity> findAllByUserLoginAndSignedDateIsNotNull(String userLogin);

    default List<DocumentEntity> findAllByUserLoginAndSignedFlag(String userLogin, boolean signed) {
        if (signed) {
            return findAllByUserLoginAndSignedDateIsNotNull(userLogin);
        } else {
            return findAllByUserLoginAndSignedDateIsNull(userLogin);
        }
    }
}
