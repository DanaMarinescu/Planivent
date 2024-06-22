package com.event.eventapp.repository;

import com.event.eventapp.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a ORDER BY a.createdDate DESC")
    List<Article> findTopRecentArticles(int count);
}
