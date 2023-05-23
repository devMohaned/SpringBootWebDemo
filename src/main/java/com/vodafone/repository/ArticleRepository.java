package com.vodafone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vodafone.model.Article;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>
{
    Optional<Article> findByName(String name);
    List<Article> findByAuthor(String author);
    List<Article> findByAuthorContains(String author);
}