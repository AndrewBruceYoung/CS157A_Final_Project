package com.musicvault.controller;

import com.musicvault.dto.RegisterForm;
import com.musicvault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @Valid @ModelAttribute("registerForm") RegisterForm form,
            BindingResult bindingResult) {
        if (userService.usernameExists(form.getUsername())) {
            bindingResult.rejectValue("username", "username.taken", "Username is already taken.");
        }
        if (userService.emailExists(form.getEmail())) {
            bindingResult.rejectValue("email", "email.taken", "Email is already registered.");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        userService.register(form);
        return "redirect:/login?registered";
    }
}
