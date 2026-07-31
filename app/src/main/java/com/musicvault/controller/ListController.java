package com.musicvault.controller;

import com.musicvault.dto.AlbumSummaryDto;
import com.musicvault.service.AlbumService;
import com.musicvault.service.ListService;
import com.musicvault.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lists")
public class ListController {

    private final ListService listService;
    private final AlbumService albumService;
    private final UserService userService;

    public ListController(ListService listService, AlbumService albumService, UserService userService) {
        this.listService = listService;
        this.albumService = albumService;
        this.userService = userService;
    }

    @GetMapping
    public String myLists(Model model, Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("lists", listService.listForUser(userId.get()));
        return "lists/index";
    }

    @GetMapping("/new")
    public String newListForm() {
        return "lists/form";
    }

    @PostMapping
    public String createList(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isEmpty() || name == null || name.isBlank()) {
            return "redirect:/lists";
        }
        Integer listId = listService.createList(userId.get(), name.trim(), description);
        return "redirect:/lists/" + listId;
    }

    @GetMapping("/{id}")
    public String listDetail(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        return listService.getListDetail(id, userId.orElse(null))
                .map(list -> {
                    model.addAttribute("list", list);
                    if (list.isOwnedByCurrentUser()) {
                        Set<Integer> inList = list.getAlbums().stream()
                                .map(item -> item.album().getAlbumId())
                                .collect(Collectors.toSet());
                        List<AlbumSummaryDto> available = albumService.browseAlbums(null).stream()
                                .filter(album -> !inList.contains(album.getAlbumId()))
                                .toList();
                        model.addAttribute("availableAlbums", available);
                    }
                    return "lists/detail";
                })
                .orElse("redirect:/lists");
    }

    @GetMapping("/{id}/edit")
    public String editListForm(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isEmpty()) {
            return "redirect:/login";
        }
        return listService.getListDetail(id, userId.get())
                .filter(list -> list.isOwnedByCurrentUser())
                .map(list -> {
                    model.addAttribute("list", list);
                    model.addAttribute("editMode", true);
                    return "lists/form";
                })
                .orElse("redirect:/lists");
    }

    @PostMapping("/{id}")
    public String updateList(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        Optional<Integer> userId = currentUserId(authentication);
        if (userId.isPresent() && name != null && !name.isBlank()) {
            listService.updateList(id, userId.get(), name.trim(), description);
        }
        return "redirect:/lists/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteList(@PathVariable Integer id, Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> listService.deleteList(id, userId));
        return "redirect:/lists";
    }

    @PostMapping("/add-album")
    public String addAlbumToSelectedList(
            @RequestParam Integer listId,
            @RequestParam Integer albumId,
            @RequestParam(required = false) String redirect,
            Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> listService.addAlbum(listId, userId, albumId));
        if ("album".equals(redirect)) {
            return "redirect:/albums/" + albumId;
        }
        return "redirect:/lists/" + listId;
    }

    @PostMapping("/{id}/albums")
    public String addAlbum(
            @PathVariable Integer id,
            @RequestParam Integer albumId,
            @RequestParam(required = false) String redirect,
            Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> listService.addAlbum(id, userId, albumId));
        if ("album".equals(redirect)) {
            return "redirect:/albums/" + albumId;
        }
        return "redirect:/lists/" + id;
    }

    @PostMapping("/{id}/albums/{albumId}/remove")
    public String removeAlbum(
            @PathVariable Integer id,
            @PathVariable Integer albumId,
            Authentication authentication) {
        currentUserId(authentication).ifPresent(userId -> listService.removeAlbum(id, userId, albumId));
        return "redirect:/lists/" + id;
    }

    private Optional<Integer> currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return userService.findUserIdByUsername(authentication.getName());
    }
}
