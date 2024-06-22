package com.event.eventapp.service;

import com.event.eventapp.model.Article;
import com.event.eventapp.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }

    public void saveArticle(Article article) {
        articleRepository.save(article);
    }

    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    public Article getArticleById(Long id) {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isPresent()) {
            return article.get();
        } else {
            throw new RuntimeException("Articolul nu a fost găsit: " + id);
        }
    }

    public List<Article> getRecentArticles(int nrOfRecentArticles) {
        return articleRepository.findTopRecentArticles(nrOfRecentArticles);
    }
}
