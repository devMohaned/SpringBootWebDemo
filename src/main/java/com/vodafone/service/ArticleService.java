package com.vodafone.service;

import com.vodafone.model.Article;

import java.util.List;

public interface ArticleService {
    List<Article> getAllArticles();
    Article getArticleById(Integer id);
    Article getArticleByName(String name);

    boolean doesArticleExist(String name);

    List<Article> getArticlesByAuthorName(String authorName);
    Article addArticle(Article article);

    void deleteArticle(Integer id);

    Article updateArticle(Integer id, Article article);
}
