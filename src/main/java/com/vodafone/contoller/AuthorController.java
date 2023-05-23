package com.vodafone.contoller;

import com.vodafone.model.Article;
import com.vodafone.model.Author;
import com.vodafone.service.AuthorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping(value = "/authors/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable(name = "id") Integer id){
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping(value = "/authors")
    public ResponseEntity<List<Author>> getAuthors(){
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @PostMapping(value = "/authors", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Author> addAuthor(@RequestBody Author author) {
        author = authorService.addAuthor(author);
        return new ResponseEntity<>(author, HttpStatus.CREATED);
    }
}
