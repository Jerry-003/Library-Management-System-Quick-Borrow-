package com.mgcfgs.LibraryManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgcfgs.LibraryManagement.model.Book;
import com.mgcfgs.LibraryManagement.model.BookLoan;
import com.mgcfgs.LibraryManagement.model.LoginUser;
import com.mgcfgs.LibraryManagement.model.RegisterUser;
import com.mgcfgs.LibraryManagement.model.ReturnHistory;
import com.mgcfgs.LibraryManagement.services.BookLoanService;
import com.mgcfgs.LibraryManagement.services.BooksServices;
import com.mgcfgs.LibraryManagement.services.ReturnHistoryServices;
import com.mgcfgs.LibraryManagement.services.UserServices;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private BooksServices booksServices;

    @Autowired
    private ReturnHistoryServices returnService;

    @Autowired
    private BookLoanService loanService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterUser());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") RegisterUser user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. Return if there are validation errors (from @Valid annotations)
        if (result.hasErrors()) {
            return "user/register";
        }

        // 2. Check if passwords match
        // if (!user.getPassword().equals(user.getConfirm_password())) {
        // model.addAttribute("passwordError", "Passwords do not match");
        // return "user/register";
        // }

        // 3. Check if user already exists
        RegisterUser existingUser = userServices.findByEmail(user.getEmail());
        if (existingUser != null) {
            model.addAttribute("emailError", "User already exists with this email");
            return "user/register";
        }

        // 4. Save user
        userServices.saveUser(user);
        redirectAttributes.addFlashAttribute("message", "User registered successfully!");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new LoginUser());
        return "user/login"; // return the login form view
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("user") LoginUser user,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "user/login"; // field validation errors
        }

        RegisterUser dbUser = userServices.findByEmail(user.getEmail());

        if (dbUser == null) {
            model.addAttribute("emailError", "User not found with this email");
            return "user/login";
        }

        if (!dbUser.getPassword().equals(user.getPassword())) {
            model.addAttribute("passwordError", "Incorrect password");
            return "user/login";
        }

        // Save full user object in session after successful login
        session.setAttribute("loggedInUser", dbUser);

        if (dbUser.getRole().equals("ADMIN")) {
            return "redirect:/admin/dashboard"; // redirect to admin dashboard
        } else {
            redirectAttributes.addFlashAttribute("message", "Login successful!");
            return "redirect:/"; // redirect to home page

        }
    }

    @GetMapping("/delete-profile")
    public String deleteProfile(HttpSession session) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            userServices.deleteUserById(loggedInUser.getId());
            session.invalidate(); // clear session after deletion
            return "redirect:/register?deleted"; // or home page
        }
        return "redirect:/login";
    }

    @GetMapping("/view-books")
    public String viewBooks(HttpSession session, Model model) {
        // This method retrieves all books from the database and adds them to the model
        List<Book> books = booksServices.getAllBooks();
        RegisterUser user = (RegisterUser) session.getAttribute("loggedInUser");
        // if (user == null) {
        // return "redirect:/login";
        // }
        model.addAttribute("loggedInUser", user);
        model.addAttribute("books", books);
        return "user/viewBooks"; // create view-books.html page in templates/user
    }

    @GetMapping("/update-profile")
    public String updateProfile(HttpSession session, Model model) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("user", loggedInUser);
            return "user/updateProfile"; // create update-profile.html page in templates/user
        }
        return "redirect:/login";
    }

    @GetMapping("/return-book")
    public String returnBook(HttpSession session, Model model) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        // Get the user's return history from service
        List<ReturnHistory> returnHistory = returnService.getReturnHistoryByMember(loggedInUser.getId());

        model.addAttribute("user", loggedInUser);
        model.addAttribute("returnHistory", returnHistory);
        return "user/return-book";
    }

    @GetMapping("/borrowed-book")
    public String borrowedBook(HttpSession session, Model model) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<BookLoan> activeLoans = loanService.getActiveLoansByMemberId(loggedInUser.getId());
        model.addAttribute("user", loggedInUser);
        model.addAttribute("loans", activeLoans);
        return "user/borrowed-books";
    }

}
