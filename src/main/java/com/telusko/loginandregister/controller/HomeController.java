package com.telusko.loginandregister.controller;

import com.telusko.loginandregister.model.User;
import com.telusko.loginandregister.repository.UserRepository;
import com.telusko.loginandregister.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = userRepository.findByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/signin")
    public String loginPage() {
        return "login";
    }

//    @GetMapping("/user/profile")
//    public String profilePage(Principal principal, Model model) {
//        String email = principal.getName();
//        User user = userRepository.findByEmail(email);
//        model.addAttribute("user", user);
//        return "profile";
//    }

    @GetMapping("/user/home")
    public String homePage() {
        return "home";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, HttpSession httpSession, HttpServletRequest request){
        // System.out.println(user);

        String url = request.getRequestURL().toString();

        // System.out.println(url); // http://localhost:8080/saveUser
        // http://localhost:8080/verify?code=2348shjidfis76f98sidhfq9870

        url = url.replace(request.getServletPath(), "");
        System.out.println(url); // http://localhost:8080

        User user1 = userService.saveUser(user, url);

        if (user1 != null) {
            System.out.println("Save Success");
            httpSession.setAttribute("msg", "Registration Successful");
        }
        else {
            System.out.println("Internal Problem");
            httpSession.setAttribute("msg", "Registration Not Successful");
        }

        return "redirect:/register";
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model){
        boolean result = userService.verifyAccount(code);

        if (result){
            model.addAttribute("msg", "Successfully your account is verified");
        }
        else {
            model.addAttribute("msg", "may be your vefication code is incorrect or already veified ");
        }

        return "message";
    }

}
