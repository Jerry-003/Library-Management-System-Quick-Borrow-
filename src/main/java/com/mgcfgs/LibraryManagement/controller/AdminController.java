package com.mgcfgs.LibraryManagement.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mgcfgs.LibraryManagement.model.Book;
import com.mgcfgs.LibraryManagement.model.BookLoan;
import com.mgcfgs.LibraryManagement.model.BookLoan.LoanStatus;
import com.mgcfgs.LibraryManagement.model.RegisterUser;
import com.mgcfgs.LibraryManagement.model.ReturnHistory;
import com.mgcfgs.LibraryManagement.services.BookLoanService;
import com.mgcfgs.LibraryManagement.services.BooksServices;
import com.mgcfgs.LibraryManagement.services.DashboardServices;
import com.mgcfgs.LibraryManagement.services.ReturnHistoryServices;
import com.mgcfgs.LibraryManagement.services.UserServices;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServices userService;

    @Autowired
    private BooksServices booksServices;

    @Autowired
    private BookLoanService bookLoanService;

    @Autowired
    private ReturnHistoryServices returnHistoryService;
    // Predefined categories
    private final List<String> categories = Arrays.asList(
            "Fiction", "Non-Fiction", "Science", "Literature",
            "History", "Biography", "Technology", "Art");
    
    

    @Autowired
    private DashboardServices dashboardService;
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        long totalBooks = dashboardService.getTotalBooks();
        long totalMembers = dashboardService.getTotalMembers();
        long booksIssued = dashboardService.getBooksIssued();
        long overdueReturns = dashboardService.getOverdueReturns();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalMembers", totalMembers);  // <-- totalMembers
        model.addAttribute("booksIssued", booksIssued);
        model.addAttribute("overdueReturns", overdueReturns);

        return "admin/dashboard";
    }


    

    @GetMapping("/books")
    public String Books(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access the dashboard");
            return "redirect:/login";
        }

        // Check if user has admin role
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access - Admin privileges required");
            return "redirect:/";
        }
        // This method retrieves all books from the database and adds them to the model
        List<Book> books = booksServices.getAllBooks();
        model.addAttribute("books", books);
        return "admin/books";
    }

    @GetMapping("/members")
    public String Members(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access the dashboard");
            return "redirect:/login";
        }

        // Check if user has admin role
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access - Admin privileges required");
            return "redirect:/";
        }
        // This method retrieves all users from the database and adds them to the model
        List<RegisterUser> users = userService.getAllUsers();
        model.addAttribute("members", users);
        return "admin/members";
    }

    @GetMapping("/issue")
    public String IssuedBooks(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access the dashboard");
            return "redirect:/login";
        }

        // Check if user has admin role
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access - Admin privileges required");
            return "redirect:/";
        }
        // This method retrieves all issued books from the database and adds them to the
        // model
        List<Book> books = booksServices.getAllBooks();
        List<RegisterUser> users = userService.getAllUsers();
        List<BookLoan> loans = bookLoanService.getAllLoans();
        model.addAttribute("Books", books);
        model.addAttribute("members", users);
        model.addAttribute("loans", loans);
        return "admin/issue-books";
    }

    @PostMapping("/issue")
    public String issueBook(
            @RequestParam Long memberId,
            @RequestParam Long bookId,
            RedirectAttributes redirectAttributes) {

        RegisterUser user = userService.getUserById(memberId);
        Book book = booksServices.getBookById(bookId);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/issue";
        }

        if (book == null) {
            redirectAttributes.addFlashAttribute("error", "Book not found.");
            return "redirect:/admin/issue";
        }

        if (!book.isAvailable()) {
            redirectAttributes.addFlashAttribute("error", "Book is not available.");
            return "redirect:/admin/issue";
        }

        BookLoan loan = new BookLoan(book, user, LocalDate.now().plusDays(14));
        // loan.setBook(book);
        // loan.setMember(user);
        loan.setIssueDate(LocalDate.now());
        // loan.setDueDate(LocalDate.now().plusDays(14)); // or any due date policy
        // loan.setStatus(BookLoan.LoanStatus.ACTIVE);

        bookLoanService.saveLoan(loan);

        // Update book's available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookLoanService.saveBook(book);

        redirectAttributes.addFlashAttribute("success", "Book issued successfully to " + user.getName());
        return "redirect:/admin/issue";
    }

    @GetMapping("/books/add-book")
    public String AddBook(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access the dashboard");
            return "redirect:/login";
        }

        // Check if user has admin role
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access - Admin privileges required");
            return "redirect:/";
        }
        // This method adds a new book to the model
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categories);
        return "admin/addbook";
    }

    @PostMapping("/books/add-book")
    public String AddBook(@ModelAttribute("book") Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        // This method saves the new book to the database
        booksServices.saveBook(book);
        return "redirect:/admin/books";
    }

    @GetMapping("/books/edit-book")
    public String EditBook(@RequestParam("bookId") Long bookId, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
 
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access the dashboard");
            return "redirect:/login";
        }

        // Check if user has admin role
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access - Admin privileges required");
            return "redirect:/";
        }
        // This method retrieves a book by its ID and adds it to the model for editing
        Book book = booksServices.getBookById(bookId);
        if (book != null) {
            model.addAttribute("book", book);
            model.addAttribute("categories", categories);
            return "admin/edit-book";
        } else {
            return "redirect:/admin/books";
        }
    }
    

    @GetMapping("/return")
    public String ReturnBook(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access the dashboard");
            return "redirect:/login";
        }

        // Check if user has admin role
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access - Admin privileges required");
            return "redirect:/";
        }
        // This method retrieves all issued books from the database and adds them to the
        List<BookLoan> loans = bookLoanService.getAllLoans();
        List<ReturnHistory> returnHistories = returnHistoryService.getAllReturnHistories();
        model.addAttribute("loans", loans);
        model.addAttribute("returnHistories", returnHistories);
        model.addAttribute("todaysReturns", returnHistoryService.getTodaysReturnsCount());
        model.addAttribute("overdueReturns", returnHistoryService.getOverdueReturnsCount());
        model.addAttribute("totalFines", returnHistoryService.getTotalFinesCollected());

        return "admin/return-book";
    }

    @PostMapping("/return")
    public String ReturnBook(
            @RequestParam Long loanId,
            @RequestParam(required = false) Double fineAmount,
            RedirectAttributes redirectAttributes) {

        BookLoan loan = bookLoanService.getLoanById(loanId);

        if (loan == null) {
            redirectAttributes.addFlashAttribute("error", "Loan not found.");
            return "redirect:/admin/return";
        }

        // Update book's available copies
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        booksServices.saveBook(book);

        // Save return info to ReturnHistory
        ReturnHistory history = new ReturnHistory();
        history.setBookTitle(book.getTitle());
        history.setMember(loan.getMember().getName());
        history.setBookAuthor(loan.getBook().getAuthor());
        history.setIssueDate(loan.getIssueDate());
        history.setDueDate(loan.getDueDate());
        history.setMemberId(loan.getMember().getId());
        loan.getStatus();
        history.setStatus(LoanStatus.RETURNED); // Set status to null or appropriate value
        history.setReturnDate(LocalDate.now());
        history.setFineAmount(fineAmount != null ? fineAmount : 0.0);
        returnHistoryService.saveReturnHistory(history);

        // Remove the loan
        bookLoanService.deleteLoan(loanId);

        redirectAttributes.addFlashAttribute("success", "Book returned successfully.");
        return "redirect:/admin/return";
    }

    @GetMapping("/delete-book")
    public String DeleteBook(@RequestParam Long bookId, RedirectAttributes redirectAttributes, HttpSession session) {
        RegisterUser loggedInUser = (RegisterUser) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to access the dashboard");
            return "redirect:/login";
        }

        // Check if user has admin role
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access - Admin privileges required");
            return "redirect:/";
        }
        // This method deletes a book from the database
        Book book = booksServices.getBookById(bookId);
        if (book != null) {
            booksServices.deleteBook(bookId);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Book not found.");
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/delete-member")
    public String deleteMember(
            @RequestParam Long memberId,
            RedirectAttributes redirectAttributes) {

        try {
            RegisterUser user = userService.getUserById(memberId);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Member not found.");
                return "redirect:/admin/members";
            }

            if (bookLoanService.hasActiveLoans(memberId)) {
                redirectAttributes.addFlashAttribute("error",
                        "Cannot delete member with active book loans.");
                return "redirect:/admin/members";
            }

            userService.deleteUser(memberId);
            redirectAttributes.addFlashAttribute("success",
                    "Member deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error deleting member: " + e.getMessage());
        }

        return "redirect:/admin/members";
    }

}
