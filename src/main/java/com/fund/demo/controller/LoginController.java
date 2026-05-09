package com.example.demo.controller;

import com.example.demo.entity.Admin;
import com.example.demo.entity.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String email,
                             @RequestParam Double monthlyBudget,
                             RedirectAttributes ra) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setMonthlyBudget(monthlyBudget);
        User result = userService.register(user);
        if (result == null) {
            ra.addFlashAttribute("error", "用户名已存在");
            return "redirect:/register";
        }
        ra.addFlashAttribute("success", "注册成功，请登录");
        return "redirect:/";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String role,
                          @RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes ra) {
        if ("user".equals(role)) {
            User user = userService.login(username, password);
            if (user != null) {
                session.setAttribute("user", user);
                session.setAttribute("role", "user");
                return "redirect:/user/home";
            }
        } else if ("admin".equals(role)) {
            Admin admin = adminService.login(username, password);
            if (admin != null) {
                session.setAttribute("admin", admin);
                session.setAttribute("role", "admin");
                return "redirect:/admin/home";
            }
        }
        ra.addFlashAttribute("error", "账号或密码错误");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}