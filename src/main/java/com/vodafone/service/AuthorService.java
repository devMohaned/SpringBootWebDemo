package com.vodafone.service;

import com.vodafone.model.Article;
import com.vodafone.model.Author;

import java.util.List;

public interface AuthorService {
    List<Author> getAllAuthors();

    Author getAuthorById(Integer id);
    Author addAuthor(Author author);
}
