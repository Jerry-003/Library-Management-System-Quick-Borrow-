package com.mgcfgs.LibraryManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgcfgs.LibraryManagement.model.RegisterUser;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        RegisterUser user = (RegisterUser) session.getAttribute("loggedInUser");

        // If admin is logged in, redirect to admin dashboard
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            redirectAttributes.addFlashAttribute("message", "Admins must use the admin portal");
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("loggedInUser", user);
        return "home/index";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        RegisterUser user = (RegisterUser) session.getAttribute("loggedInUser");
        // If admin is logged in, redirect to admin dashboard
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            redirectAttributes.addFlashAttribute("message", "Admins must use the admin portal");
            return "redirect:/admin/dashboard";
        }
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/profile"; // create profile.html page in templates/user
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
