package com.vodafone.service;

import com.vodafone.errorhandlling.ConflictException;
import com.vodafone.errorhandlling.NotFoundException;
import com.vodafone.model.Article;
import com.vodafone.repository.ArticleRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTests {

    public ArticleService articleService;

    public ArticleRepository repository;

    @Before
    public void setup(){
        repository = Mockito.mock(ArticleRepository.class);
        articleService = new ArticleServiceImpl(repository);
    }


    @Test
    public void should_GetAllArticles_WhenArticlesFromRepositoryDataLayerIsNotEmpty()
    {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article());
        articles.add(new Article());
        articles.add(new Article());

        when(repository.findAll()).thenReturn(articles);

        List<Article> articleList =  articleService.getAllArticles();


        Assert.assertEquals(articleList.size(), 3);
    }

    @Test
    public void should_GetAllArticles_WhenArticlesRepositoryDataLayerIsEmpty()
    {
        List<Article> articles = new ArrayList<>();
        when(repository.findAll()).thenReturn(articles);

        List<Article> articleList =  articleService.getAllArticles();


        Assert.assertEquals(articleList.size(), 0);
    }

    // Get Article By ID (ID Exists)
    @Test
    public void should_GetArticleByID_WhenValidArticleIDIsProvidedAsInput()
    {
        int validId = 1;
        Article expectedArticle = new Article();
        when(repository.findById(validId)).thenReturn(Optional.of(expectedArticle));

        Article article = articleService.getArticleById(validId);

        Assert.assertNotNull(article);
        Assert.assertSame(expectedArticle,article);

    }
    // Get Article By ID (ID DOES NOT EXIST)
    @Test(expected = NotFoundException.class)
    public void should_GetArticleByID_WhenInValidArticleIDIsProvidedAsInput()
    {
        int invalidId = 1;
        Article expectedArticle = new Article();
        when(repository.findById(invalidId)).thenReturn(Optional.empty());

        Article article = articleService.getArticleById(invalidId);  // Throws exception
    }

    // Get Article By Name (Name Exists)
    @Test
    public void should_GetArticleByName_WhenValidArticleNameIsProvidedAsInput()
    {
        String validName = "ValidName";
        Article expectedArticle = new Article();
        when(repository.findByName(validName)).thenReturn(Optional.of(expectedArticle));

        Article article = articleService.getArticleByName(validName);

        Assert.assertNotNull(article);
        Assert.assertSame(expectedArticle,article);

    }
    // Get Article By Name (Name Does Not Exist)
    @Test(expected = NotFoundException.class)
    public void should_GetArticleByName_WhenInValidArticleNameIsProvidedAsInput()
    {
        String invalidName = "WrongName";
        Article expectedArticle = new Article();
        when(repository.findByName(invalidName)).thenReturn(Optional.empty());

        Article article = articleService.getArticleByName(invalidName); // Throws exception
    }

    // Check If doesArticle Exist By Name (Exist)
    @Test
    public void should_DoesArticleExists_WhenAlreadyExistingNameProvided()
    {
        String validName = "existingName";
        Article expectedArticle = new Article();
        when(repository.findByName(validName)).thenReturn(Optional.of(expectedArticle));

        boolean doesArticleExist = articleService.doesArticleExist(validName);

        Assert.assertTrue(doesArticleExist);
    }
    // Check If doesArticle Exist By Name (Does Not Exist)
    @Test
    public void should_DoesArticleExists_WhenNewValidNameProvided()
    {
        String validName = "existingName";
        when(repository.findByName(validName)).thenReturn(Optional.empty());

        boolean doesArticleExist = articleService.doesArticleExist(validName);

        Assert.assertFalse(doesArticleExist);
    }

    // Get Articles By Author Name (AuthorName does not exist)
    @Test
    public void should_GetNoArticlesByAuthorName_WhenProvidedAuthorThatDoesNotExist()
    {
        String notAuthorName = "notAnExistingAuthorName";
        when(repository.findByAuthorContains(notAuthorName)).thenReturn(new ArrayList<>());

       List<Article> articles = articleService.getArticlesByAuthorName(notAuthorName);
       assertEquals(articles.size(), 0);
    }
    // Get Articles by Author Name (AuthorName exists)
    @Test
    public void should_GetArticlesByAuthorName_WhenProvidedAuthorThatDoesExist()
    {
        String existingAuthorName = "AnExistingAuthorName";
        List<Article> exitingListOfArticlesWithAuthors = new ArrayList<>();
        Article articleWithExistingAuthor = new Article();
        articleWithExistingAuthor.setName(existingAuthorName);
        exitingListOfArticlesWithAuthors.add(articleWithExistingAuthor);
        when(repository.findByAuthorContains(existingAuthorName)).thenReturn(exitingListOfArticlesWithAuthors);

        List<Article> articles = articleService.getArticlesByAuthorName(existingAuthorName);
        assertEquals(articles.size(), 1);
    }
    // Add Articles (Article Valid)
    @Test
    public void should_addArticle_WhenArticleIsValid()
    {
        Article validArticle = new Article();
        validArticle.setName("Name");
        validArticle.setAuthor("AuthorName");
        Article successfullArticleResult = new Article();
        when(repository.save(validArticle)).thenReturn(successfullArticleResult);

        Article article = articleService.addArticle(validArticle);

        assertEquals(article, successfullArticleResult);
    }
    // Add Article (Article Invalid)
    @Test(expected = ConflictException.class)
    public void should_ThrowExceptionWhenAddArticle_WhenArticleIsAlreadyExists()
    {
        Article invalidArticle = new Article();
        when(repository.save(invalidArticle)).thenThrow(ConflictException.class);

        Article article = articleService.addArticle(invalidArticle);

    }




    // Update Article With Not Valid ID

    @Test(expected = NotFoundException.class)
    public void should_UpdateArticleThrowException_WhenWrongID(){
        int wrongId = -1;
        when(articleService.getArticleById(wrongId)).thenThrow(NotFoundException.class);

        var result = articleService.updateArticle(wrongId,new Article());

    }
    // Update Article With Valid ID
    @Test
    public void should_UpdateArticleOfSameID_WhenProvidedValidID(){
        Article validArticle = new Article();
        int validId = 1;
        validArticle.setId(validId);
        validArticle.setName("Name");
        validArticle.setAuthor("AuthorName");
        Article successfullArticleResult = new Article();
        when(repository.save(validArticle)).thenReturn(validArticle);
        when(repository.findById(validId)).thenReturn(Optional.of(validArticle));

        Article article = articleService.addArticle(successfullArticleResult);

        var result = articleService.updateArticle(validId,validArticle);

        assertEquals(result,validArticle);

    }



    // Delete Article With Valid ID
    @Test(expected = NotFoundException.class)
    public void should_DeleteArticleThrowException_WhenWrongID(){
        int wrongId = -1;
        when(articleService.getArticleById(wrongId)).thenThrow(NotFoundException.class);

        articleService.deleteArticle(wrongId);

    }
    // Delete Article With Not Valid ID
    @Test
    public void should_DeleteArticleOfSameID_WhenProvidedValidID(){
        Article validArticle = new Article();
        int validId = 1;
        validArticle.setId(validId);
        validArticle.setName("Name");
        validArticle.setAuthor("AuthorName");
        Article successfullArticleResult = new Article();
        when(repository.save(validArticle)).thenReturn(validArticle);
        when(repository.findById(validId)).thenReturn(Optional.of(validArticle));

        Article article = articleService.addArticle(successfullArticleResult);

        articleService.deleteArticle(validId);

        verify(repository,times(1)).deleteById(validId);
    }



}