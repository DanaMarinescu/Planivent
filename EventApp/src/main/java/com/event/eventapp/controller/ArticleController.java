package com.event.eventapp.controller;

import com.event.eventapp.model.Article;
import com.event.eventapp.model.Category;
import com.event.eventapp.model.User;
import com.event.eventapp.repository.RoleRepository;
import com.event.eventapp.repository.UserRepository;
import com.event.eventapp.service.ArticleService;
import com.event.eventapp.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/blog")
public class ArticleController {

    private final CategoryService categoryService;
    private final ArticleService articleService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public ArticleController(CategoryService categoryService, ArticleService articleService, UserRepository userRepository, RoleRepository roleRepository) {
        this.categoryService = categoryService;
        this.articleService = articleService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @ModelAttribute("allCategories")
    public List<Category> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping
    public String showAllArticles(Model model) {
        List<Article> articles = articleService.findAllArticles();
        model.addAttribute("articles", articles);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername());
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("roles", roleRepository.findAll());
                return "blog";
            } else {
                model.addAttribute("error", "Utilizator negăsit!");
            }
            return "error-page";
        } else {
        model.addAttribute("error", "Eroare de încărcare a blogului!");
        return "error-page";
    }
    }

    @GetMapping("/article/{id}")
    public String showArticle(@PathVariable("id") Long id, Model model) {
        Article article = articleService.getArticleById(id);
        List<Article> recentArticles = articleService.getRecentArticles(3);
        model.addAttribute("article", article);
        model.addAttribute("recentArticles", recentArticles);
        return "article";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('Provider')")
    public String addArticle(@ModelAttribute Article article, Authentication authentication) {
        if (authentication != null) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByEmail(username);
            if (user != null) {
                article.setUser(user);
            } else {
                return "redirect:/error-page";
            }
        }
        articleService.saveArticle(article);
        return "redirect:/blog";
    }
}
