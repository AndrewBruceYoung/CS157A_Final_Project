package com.musicvault.controller;

import com.musicvault.service.AdminService;
import com.musicvault.service.UserService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    // --- Genres ---

    @GetMapping("/genres")
    public String genres(Model model) {
        model.addAttribute("items", adminService.listGenres());
        model.addAttribute("section", "genres");
        model.addAttribute("title", "Genres");
        return "admin/lookup";
    }

    @PostMapping("/genres")
    public String createGenre(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            adminService.createGenre(name);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Could not create genre (maybe duplicate name).");
        }
        return "redirect:/admin/genres";
    }

    @PostMapping("/genres/{id}")
    public String updateGenre(@PathVariable Integer id, @RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.updateGenre(id, name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not update genre.");
        }
        return "redirect:/admin/genres";
    }

    @PostMapping("/genres/{id}/delete")
    public String deleteGenre(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            adminService.deleteGenre(id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not delete genre (it may be in use).");
        }
        return "redirect:/admin/genres";
    }

    // --- Labels ---

    @GetMapping("/labels")
    public String labels(Model model) {
        model.addAttribute("items", adminService.listLabels());
        model.addAttribute("section", "labels");
        model.addAttribute("title", "Record Labels");
        return "admin/lookup";
    }

    @PostMapping("/labels")
    public String createLabel(@RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.createLabel(name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not create label.");
        }
        return "redirect:/admin/labels";
    }

    @PostMapping("/labels/{id}")
    public String updateLabel(@PathVariable Integer id, @RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.updateLabel(id, name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not update label.");
        }
        return "redirect:/admin/labels";
    }

    @PostMapping("/labels/{id}/delete")
    public String deleteLabel(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            adminService.deleteLabel(id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not delete label (it may be in use).");
        }
        return "redirect:/admin/labels";
    }

    // --- Release types ---

    @GetMapping("/release-types")
    public String releaseTypes(Model model) {
        model.addAttribute("items", adminService.listReleaseTypes());
        model.addAttribute("section", "release-types");
        model.addAttribute("title", "Release Types");
        return "admin/lookup";
    }

    @PostMapping("/release-types")
    public String createReleaseType(@RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.createReleaseType(name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not create release type.");
        }
        return "redirect:/admin/release-types";
    }

    @PostMapping("/release-types/{id}")
    public String updateReleaseType(@PathVariable Integer id, @RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.updateReleaseType(id, name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not update release type.");
        }
        return "redirect:/admin/release-types";
    }

    @PostMapping("/release-types/{id}/delete")
    public String deleteReleaseType(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            adminService.deleteReleaseType(id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not delete release type (it may be in use).");
        }
        return "redirect:/admin/release-types";
    }

    // --- Streaming services ---

    @GetMapping("/streaming-services")
    public String streamingServices(Model model) {
        model.addAttribute("items", adminService.listStreamingServices());
        model.addAttribute("section", "streaming-services");
        model.addAttribute("title", "Streaming Services");
        return "admin/lookup";
    }

    @PostMapping("/streaming-services")
    public String createStreamingService(@RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.createStreamingService(name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not create streaming service.");
        }
        return "redirect:/admin/streaming-services";
    }

    @PostMapping("/streaming-services/{id}")
    public String updateStreamingService(@PathVariable Integer id, @RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.updateStreamingService(id, name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not update streaming service.");
        }
        return "redirect:/admin/streaming-services";
    }

    @PostMapping("/streaming-services/{id}/delete")
    public String deleteStreamingService(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            adminService.deleteStreamingService(id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not delete streaming service (it may be in use).");
        }
        return "redirect:/admin/streaming-services";
    }

    // --- Artists ---

    @GetMapping("/artists")
    public String artists(Model model) {
        model.addAttribute("items", adminService.listArtists());
        model.addAttribute("section", "artists");
        model.addAttribute("title", "Artists");
        return "admin/lookup";
    }

    @PostMapping("/artists")
    public String createArtist(@RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.createArtist(name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not create artist.");
        }
        return "redirect:/admin/artists";
    }

    @PostMapping("/artists/{id}")
    public String updateArtist(@PathVariable Integer id, @RequestParam String name, RedirectAttributes ra) {
        try {
            adminService.updateArtist(id, name);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not update artist.");
        }
        return "redirect:/admin/artists";
    }

    @PostMapping("/artists/{id}/delete")
    public String deleteArtist(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            adminService.deleteArtist(id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not delete artist (it may be in use).");
        }
        return "redirect:/admin/artists";
    }

    // --- Albums ---

    @GetMapping("/albums")
    public String albums(Model model) {
        model.addAttribute("albums", adminService.listAlbums());
        return "admin/albums";
    }

    @GetMapping("/albums/new")
    public String newAlbum(Model model) {
        model.addAttribute("labels", adminService.listLabels());
        model.addAttribute("types", adminService.listReleaseTypes());
        return "admin/album-form";
    }

    @PostMapping("/albums")
    public String createAlbum(
            @RequestParam String title,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) String labelId,
            @RequestParam Integer typeId,
            RedirectAttributes ra) {
        try {
            Integer id = adminService.createAlbum(title, parseDate(releaseDate), parseOptionalInt(labelId), typeId);
            return "redirect:/admin/albums/" + id;
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not create album.");
            return "redirect:/admin/albums/new";
        }
    }

    @GetMapping("/albums/{id}")
    public String editAlbum(@PathVariable Integer id, Model model) {
        return adminService.getAlbum(id)
                .map(album -> {
                    model.addAttribute("album", album);
                    model.addAttribute("labels", adminService.listLabels());
                    model.addAttribute("types", adminService.listReleaseTypes());
                    model.addAttribute("tracks", adminService.listTracks(id));
                    model.addAttribute("albumGenres", adminService.listAlbumGenres(id));
                    model.addAttribute("allGenres", adminService.listGenres());
                    return "admin/album-edit";
                })
                .orElse("redirect:/admin/albums");
    }

    @PostMapping("/albums/{id}")
    public String updateAlbum(
            @PathVariable Integer id,
            @RequestParam String title,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) String labelId,
            @RequestParam Integer typeId,
            RedirectAttributes ra) {
        try {
            adminService.updateAlbum(id, title, parseDate(releaseDate), parseOptionalInt(labelId), typeId);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not update album.");
        }
        return "redirect:/admin/albums/" + id;
    }

    @PostMapping("/albums/{id}/delete")
    public String deleteAlbum(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            adminService.deleteAlbum(id);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not delete album.");
        }
        return "redirect:/admin/albums";
    }

    @PostMapping("/albums/{id}/tracks")
    public String addTrack(
            @PathVariable Integer id,
            @RequestParam Integer trackNo,
            @RequestParam String title,
            @RequestParam(required = false) String duration,
            RedirectAttributes ra) {
        try {
            adminService.addTrack(id, trackNo, title, parseTime(duration));
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not add track (check track number is unique).");
        }
        return "redirect:/admin/albums/" + id;
    }

    @PostMapping("/albums/{id}/tracks/{trackNo}/delete")
    public String deleteTrack(@PathVariable Integer id, @PathVariable Integer trackNo, RedirectAttributes ra) {
        try {
            adminService.deleteTrack(id, trackNo);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not delete track.");
        }
        return "redirect:/admin/albums/" + id;
    }

    @PostMapping("/albums/{id}/genres")
    public String addAlbumGenre(@PathVariable Integer id, @RequestParam Integer genreId, RedirectAttributes ra) {
        try {
            adminService.addAlbumGenre(id, genreId);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not add genre.");
        }
        return "redirect:/admin/albums/" + id;
    }

    @PostMapping("/albums/{id}/genres/{genreId}/delete")
    public String removeAlbumGenre(@PathVariable Integer id, @PathVariable Integer genreId, RedirectAttributes ra) {
        try {
            adminService.removeAlbumGenre(id, genreId);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not remove genre.");
        }
        return "redirect:/admin/albums/" + id;
    }

    // --- Reviews ---

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", adminService.listReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes ra) {
        Optional<Integer> adminId = currentUserId(authentication);
        if (adminId.isEmpty()) {
            return "redirect:/login";
        }
        try {
            adminService.moderateAndDeleteReview(id, adminId.get());
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not remove review.");
        }
        return "redirect:/admin/reviews";
    }

    private Optional<Integer> currentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return userService.findUserIdByUsername(authentication.getName());
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private static Integer parseOptionalInt(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    private static LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        // Accept HH:MM or HH:MM:SS
        if (value.length() == 5) {
            return LocalTime.parse(value + ":00");
        }
        return LocalTime.parse(value);
    }
}
