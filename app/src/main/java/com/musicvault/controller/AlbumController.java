package com.musicvault.controller;

import com.musicvault.dto.AlbumSummaryDto;
import com.musicvault.service.AlbumService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public String listAlbums(@RequestParam(required = false) String q, Model model) {
        List<AlbumSummaryDto> albums = albumService.browseAlbums(q);
        model.addAttribute("albums", albums);
        model.addAttribute("query", q == null ? "" : q);
        return "albums/list";
    }

    @GetMapping("/{id}")
    public String albumDetail(@PathVariable Integer id, Model model) {
        return albumService.getAlbumDetail(id)
                .map(album -> {
                    model.addAttribute("album", album);
                    return "albums/detail";
                })
                .orElse("redirect:/albums");
    }
}
