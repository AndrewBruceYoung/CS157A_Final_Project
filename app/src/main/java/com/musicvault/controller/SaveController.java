package com.musicvault.controller;

import com.musicvault.service.SaveService;
import com.musicvault.service.UserService;
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
@RequestMapping("/saves")
public class SaveController {

    private final SaveService saveService;
    private final UserService userService;

    public SaveController(SaveService saveService, UserService userService) {
        this.saveService = saveService;
        this.userService = userService;
    }

    @GetMapping
    public String mySaves(@RequestParam(required = false) String status, Model model, Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("statuses", SaveService.STATUSES);
        model.addAttribute("selectedStatus", status == null ? "" : status);
        model.addAttribute("saves", saveService.listSaves(userId.get(), blankToNull(status)));
        return "saves/list";
    }

    @PostMapping("/albums/{albumId}/add")
    public String addSave(
            @PathVariable Integer albumId,
            @RequestParam String status,
            @RequestParam(required = false) String redirect,
            Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> saveService.addSave(userId, albumId, status));
        return redirectOrAlbum(redirect, albumId);
    }

    @PostMapping("/albums/{albumId}/remove")
    public String removeSave(
            @PathVariable Integer albumId,
            @RequestParam String status,
            @RequestParam(required = false) String redirect,
            Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> saveService.removeSave(userId, albumId, status));
        return redirectOrAlbum(redirect, albumId);
    }

    private String redirectOrAlbum(String redirect, Integer albumId) {
        if ("saves".equals(redirect)) {
            return "redirect:/saves";
        }
        return "redirect:/albums/" + albumId;
    }

    private Optional<Integer> currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return userService.findUserIdByUsername(authentication.getName());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
