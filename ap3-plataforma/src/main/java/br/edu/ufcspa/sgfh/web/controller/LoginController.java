package br.edu.ufcspa.sgfh.web.controller;

import br.edu.ufcspa.sgfh.web.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        return currentUser == null ? "login" : "redirect:/";
    }
}
