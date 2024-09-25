package edu.duan.app.docs.service;

import edu.duan.app.docs.api.Document;
import edu.duan.app.docs.api.DocumentType;
import edu.duan.app.docs.api.User;
import edu.duan.app.docs.data.*;
import edu.duan.app.docs.exception.DocumentNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DocumentsService {
    private DocumentsRepository documentsRepository;
    private UsersRepository usersRepository;

    public DocumentsService(DocumentsRepository documentsRepository, UsersRepository usersRepository) {
        this.documentsRepository = documentsRepository;
        this.usersRepository = usersRepository;
    }

    public Document get(String id) {
        return documentsRepository.findById(id).map(this::convertToApi).orElseThrow(documentNotFoundException(id));
    }

    public List<Document> getAllForUser(String userLogin) {
        return documentsRepository.findAllByUserLogin(userLogin).stream().map(this::convertToApi).toList();
    }

    public List<Document> getByCreatedDate(long from, long to) {
        return documentsRepository.findAllByCreatedDateBetween(new java.sql.Date(from), new java.sql.Date(to))
                .stream().map(this::convertToApi).toList();
    }

    public List<Document> getAllForUserBySign(String userLogin, boolean signed) {
        return documentsRepository.findAllByUserLoginAndSignedFlag(userLogin, signed)
                .stream().map(this::convertToApi).toList();
    }


    public Document addDocument(Document document) {
        return convertToApi(documentsRepository.save(
                convertToDomain(document, getUserEntity(document.getUser()))
        ));
    }

    private UserEntity getUserEntity(User user) {
        return usersRepository
                .findByLogin(user.getLogin())
                .orElse(createUserEntity(user.getLogin()));
    }

    private UserEntity createUserEntity(String login) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(login);
        return userEntity;
    }

    @Transactional
    public Document updateDocument(Document document) {
        return documentsRepository.findById(document.getId()).map(documentEntity -> {
            documentEntity.setName(document.getName());
            documentEntity.setContent(document.getContent());
            documentEntity.setDocumentType(DocumentTypeEntity.valueOf(document.getDocumentType().name()));
            documentEntity.setUser(getUserEntity(document.getUser()));
            return documentEntity;
        }).map(this::convertToApi).orElseThrow(documentNotFoundException(document.getId()));
    }

    @Transactional
    public Document signDocument(String id) {
        return documentsRepository.findById(id).map(documentEntity -> {
            documentEntity.setSignedDate(new Timestamp(System.currentTimeMillis()));
            return documentEntity;
        }).map(this::convertToApi).orElseThrow(documentNotFoundException(id));
    }

    @Transactional
    public void delete(String id) {
        if (documentsRepository.existsById(id)) {
            documentsRepository.deleteById(id);
        } else throw documentNotFoundException(id).get();
    }

    private Document convertToApi(DocumentEntity documentEntity) {
        Document document = new Document();
        document.setId(documentEntity.getId());
        document.setName(documentEntity.getName());
        document.setContent(documentEntity.getContent());
        if (documentEntity.getDocumentType() != null) {
            document.setDocumentType(DocumentType.valueOf(documentEntity.getDocumentType().name()));
        }
        if (documentEntity.getCreatedDate() != null) {
            document.setCreatedDate(new Date(documentEntity.getCreatedDate().getTime()));
        }
        if (documentEntity.getSignedDate() != null) {
            document.setSignedDate(new Date(documentEntity.getSignedDate().getTime()));
        }
        if (documentEntity.getUser() != null) {
            User user = new User();
            user.setLogin(documentEntity.getUser().getLogin());
            document.setUser(user);
        }
        return document;
    }

    private DocumentEntity convertToDomain(Document document, UserEntity user) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setName(document.getName());
        documentEntity.setContent(document.getContent());
        documentEntity.setDocumentType(DocumentTypeEntity.valueOf(document.getDocumentType().name()));
        documentEntity.setUser(user);
        return documentEntity;
    }

    private static Supplier<DocumentNotFoundException> documentNotFoundException(String id) {
        return () -> new DocumentNotFoundException("Document with `" + id + "` not found");
    }
}
