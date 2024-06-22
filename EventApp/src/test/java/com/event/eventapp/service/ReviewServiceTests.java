package com.event.eventapp.service;

import com.event.eventapp.model.Review;
import com.event.eventapp.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void testSaveReview() {
        Review review = new Review();
        review.setId(1L);

        reviewService.saveReview(review);

        verify(reviewRepository).save(review);
    }
}