package com.vodafone.service;

import com.vodafone.errorhandlling.NotFoundException;
import com.vodafone.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vodafone.model.Author;
import com.vodafone.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService
{
    @Autowired
    AuthorRepository repo;

    @Override
    public List<Author> getAllAuthors() {
        return repo.findAll();
    }

    @Override
    public Author getAuthorById(Integer id) 
    {
        Optional<Author> author = repo.findById(id);
        if (author.isPresent())
            return author.get();


        throw new NotFoundException(String.format("The Author with id '%s' was not found", id));
    }

    @Override
    public Author addAuthor(Author author) {
        return repo.save(author);
    }

}
