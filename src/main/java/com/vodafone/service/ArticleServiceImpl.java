package com.vodafone.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vodafone.errorhandlling.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vodafone.contoller.ArticlesController;
import com.vodafone.contoller.AuthorController;
import com.vodafone.errorhandlling.NotFoundException;
import com.vodafone.model.*;
import com.vodafone.repository.ArticleRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            addLinks(article);
        }
        return articles;
    }

    @Override
    public Article getArticleById(Integer id) {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isPresent()) {
            addLinks(article.get());
            return article.get();
        }

        throw new NotFoundException(String.format("The Article with id '%s' was not found", id));
    }

    @Override
    public Article getArticleByName(String name) {
        Optional<Article> article = articleRepository.findByName(name);
        if (article.isPresent())
            return article.get();

        throw new NotFoundException(String.format("The Article with name '%s' was not found", name));
    }

    @Override
    public boolean doesArticleExist(String name) {
        return articleRepository.findByName(name).isPresent();
    }

    @Override
    public List<Article> getArticlesByAuthorName(String authorName) {
        List<Article> articles = articleRepository.findByAuthorContains(authorName);
        for (Article article : articles) addLinks(article);
        return articles;
    }

    @Override
    public Article addArticle(Article article) {
        if (!doesArticleExist(article.getName()))
            return articleRepository.save(article);

        throw new ConflictException(String.format("Article with name %s already exists", article.getName()));
    }

    @Override
    public void deleteArticle(Integer id) {
        if (getArticleById(id) == null) // This already throws NotFoundException
            throw new NotFoundException("Delete Article Not Found ID");

        articleRepository.deleteById(id);
    }

    @Override
    public Article updateArticle(Integer id, Article article) {
        if (getArticleById(id) == null) // This already throws NotFoundException
            throw new NotFoundException("Updating Article Not Found ID");

        article.setId(id);
        return articleRepository.save(article);
    }

    private Article addLinks(Article article) {
        List<Links> links = new ArrayList<>();
        Links self = new Links();

        Link selfLink = linkTo(methodOn(ArticlesController.class)
                .getArticle(article.getId())).withRel("self");

        self.setRel("self");
        self.setHref(selfLink.getHref());

        Links authorLink = new Links();
        Link authLink = linkTo(methodOn(AuthorController.class)
                .getAuthorById(article.getAuthorId())).withRel("author");
        authorLink.setRel("author");
        authorLink.setHref(authLink.getHref());

        links.add(self);
        links.add(authorLink);
        article.setLinks(links);
        return article;
    }
}
