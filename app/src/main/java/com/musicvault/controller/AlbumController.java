package com.musicvault.controller;

import com.musicvault.dto.AlbumSummaryDto;
import com.musicvault.service.AlbumService;
import com.musicvault.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final UserService userService;

    public AlbumController(AlbumService albumService, UserService userService) {
        this.albumService = albumService;
        this.userService = userService;
    }

    @GetMapping
    public String listAlbums(@RequestParam(required = false) String q, Model model) {
        List<AlbumSummaryDto> albums = albumService.browseAlbums(q);
        model.addAttribute("albums", albums);
        model.addAttribute("query", q == null ? "" : q);
        return "albums/list";
    }

    @GetMapping("/{id}")
    public String albumDetail(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer editReview,
            Authentication authentication,
            Model model) {
        return albumService.getAlbumDetail(id, currentUserId(authentication).orElse(null), editReview)
                .map(album -> {
                    model.addAttribute("album", album);
                    return "albums/detail";
                })
                .orElse("redirect:/albums");
    }

    @PostMapping("/{id}/rating")
    public String saveRating(
            @PathVariable Integer id,
            @RequestParam Double score,
            Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isPresent()) {
            albumService.saveRating(id, userId.get(), score);
        }
        return "redirect:/albums/" + id;
    }

    @PostMapping("/{id}/rating/delete")
    public String deleteRating(@PathVariable Integer id, Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isPresent()) {
            albumService.deleteRating(id, userId.get());
        }
        return "redirect:/albums/" + id;
    }

    @PostMapping("/{id}/reviews")
    public String saveReview(
            @PathVariable Integer id,
            @RequestParam String content,
            @RequestParam(required = false) Integer reviewId,
            Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isPresent() && content != null && !content.isBlank()) {
            if (reviewId == null) {
                albumService.createReview(id, userId.get(), content.trim());
            } else {
                albumService.updateReview(reviewId, id, userId.get(), content.trim());
            }
        }
        return "redirect:/albums/" + id;
    }

    @PostMapping("/{id}/reviews/{reviewId}/delete")
    public String deleteReview(
            @PathVariable Integer id,
            @PathVariable Integer reviewId,
            Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isPresent()) {
            albumService.deleteReview(reviewId, id, userId.get());
        }
        return "redirect:/albums/" + id;
    }

    private Optional<Integer> currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return userService.findUserIdByUsername(authentication.getName());
    }
}
