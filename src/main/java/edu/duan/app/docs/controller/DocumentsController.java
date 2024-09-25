package edu.duan.app.docs.controller;

import edu.duan.app.docs.api.Document;
import edu.duan.app.docs.service.DocumentsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DocumentsController {
    private DocumentsService documentsService;

    public DocumentsController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @GetMapping(path = "/{id}")
    public Document get(@PathVariable String id) {
        return documentsService.get(id);
    }

    @GetMapping("/for/user")
    public List<Document> getAllForUser(@RequestParam String userLogin) {
        return documentsService.getAllForUser(userLogin);
    }

    @GetMapping(path = "/for/user/by/sign")
    public List<Document> getAllForUserBySign(@RequestParam String userLogin, @RequestParam boolean signed) {
        return documentsService.getAllForUserBySign(userLogin, signed);
    }

    @GetMapping(path = "/by/date")
    public List<Document> getByCreatedDate(@RequestParam long from, @RequestParam long to) {
        return documentsService.getByCreatedDate(from, to);
    }

    @PostMapping()
    public @ResponseBody Document addDocument(@RequestBody Document document) {
        return documentsService.addDocument(document);
    }

    @PutMapping()
    public Document updateDocument(@RequestBody Document document) {
        return documentsService.updateDocument(document);
    }

    @PutMapping("/{id}/sign")
    public Document signDocument(@PathVariable String id) {
        return documentsService.signDocument(id);
    }

    @DeleteMapping(path = "{id}")
    public void deleteById(@PathVariable String id) {
        documentsService.delete(id);
    }
}
