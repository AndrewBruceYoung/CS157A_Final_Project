package com.musicvault.controller;

import com.musicvault.service.FollowService;
import com.musicvault.service.ListService;
import com.musicvault.service.UserService;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final FollowService followService;
    private final ListService listService;
    private final UserService userService;

    public UserController(FollowService followService, ListService listService, UserService userService) {
        this.followService = followService;
        this.listService = listService;
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model, Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        model.addAttribute("users", followService.listUsers(userId.orElse(null)));
        model.addAttribute("currentUserId", userId.orElse(null));
        return "users/list";
    }

    @GetMapping("/{id}")
    public String userProfile(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<Integer> currentUserId = currentUserId(authentication);
        return followService.getProfile(id, currentUserId.orElse(null))
                .map(profile -> {
                    model.addAttribute("profile", profile);
                    model.addAttribute("lists", listService.listsOwnedBy(id));
                    return "users/profile";
                })
                .orElse("redirect:/users");
    }

    @PostMapping("/{id}/follow")
    public String follow(@PathVariable Integer id, Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> followService.follow(userId, id));
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/unfollow")
    public String unfollow(@PathVariable Integer id, Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> followService.unfollow(userId, id));
        return "redirect:/users/" + id;
    }

    private Optional<Integer> currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return userService.findUserIdByUsername(authentication.getName());
    }
}
