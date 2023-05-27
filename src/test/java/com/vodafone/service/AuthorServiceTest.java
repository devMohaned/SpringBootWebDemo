package com.vodafone.service;

import com.vodafone.errorhandlling.ConflictException;
import com.vodafone.errorhandlling.NotFoundException;
import com.vodafone.model.Article;
import com.vodafone.model.Author;
import com.vodafone.repository.ArticleRepository;
import com.vodafone.repository.AuthorRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AuthorServiceTest {

    public AuthorService authorService;

    public AuthorRepository repository;

    @Before
    public void setup() {
        repository = Mockito.mock(AuthorRepository.class);
        authorService = new AuthorServiceImpl(repository);
    }


    @Test
    public void should_GetAllAuthors_WhenAuthorFromRepositoryDataLayerIsNotEmpty() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author());
        authors.add(new Author());
        authors.add(new Author());
        when(repository.findAll()).thenReturn(authors);

        List<Author> authorsList = authorService.getAllAuthors();

        Assert.assertEquals(authorsList.size(), 3);
    }

    @Test
    public void should_GetAllAuthors_WhenAuthorsRepositoryDataLayerIsEmpty() {
        List<Author> authors = new ArrayList<>();
        when(repository.findAll()).thenReturn(authors);

        List<Author> authorList = authorService.getAllAuthors();


        Assert.assertEquals(authorList.size(), 0);
    }

    // Get Author By ID (ID Exists)
    @Test
    public void should_GetAuthorByID_WhenValidAuthorIDIsProvidedAsInput() {
        int validId = 1;
        Author expectedAuthor = new Author();
        when(repository.findById(validId)).thenReturn(Optional.of(expectedAuthor));

        Author author = authorService.getAuthorById(validId);

        Assert.assertNotNull(author);
        Assert.assertSame(expectedAuthor, author);

    }

    // Get Author By ID (ID DOES NOT EXIST)
    @Test(expected = NotFoundException.class)
    public void should_GetAuthorByID_WhenInValidAuthorIDIsProvidedAsInput() {
        int invalidId = 1;
        when(repository.findById(invalidId)).thenReturn(Optional.empty());

        Author author = authorService.getAuthorById(invalidId);  // Throws exception
    }


    // Add Author (Author Valid)
    @Test
    public void should_addAuthor_WhenAuthorIsValid()
    {
        Author validAuthor = new Author();
        validAuthor.setName("Name");
        validAuthor.setId(1);
        Author successfulAuthorResult = new Author();
        when(repository.save(validAuthor)).thenReturn(successfulAuthorResult);

        Author author = authorService.addAuthor(validAuthor);

        assertEquals(author, successfulAuthorResult);
    }



}