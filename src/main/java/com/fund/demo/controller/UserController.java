package com.example.demo.controller;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;

    private User checkLogin(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = checkLogin(session);
        if (user == null) return "redirect:/";
        Map<String, Double> typeSum = transactionService.getUserCurrentMonthTransactions(user.getUserId())
                .stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));
        String[] types = {"餐饮", "水电", "交通", "生活用品", "工资", "其他"};
        for (String t : types) model.addAttribute(t + "Sum", typeSum.getOrDefault(t, 0.0));
        model.addAttribute("user", user);
        return "user-home";
    }

    @GetMapping("/info")
    public String info(HttpSession session, Model model) {
        User user = checkLogin(session);
        if (user == null) return "redirect:/";
        model.addAttribute("user", userService.findById(user.getUserId()));
        return "user-info";
    }

    @GetMapping("/edit")
    public String editPage(HttpSession session, Model model) {
        User user = checkLogin(session);
        if (user == null) return "redirect:/";
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/update")
    public String update(@RequestParam String username, @RequestParam String password,
                         HttpSession session, RedirectAttributes ra) {
        User user = checkLogin(session);
        if (user == null) return "redirect:/";
        User existing = userService.findById(user.getUserId());
        existing.setUsername(username);
        existing.setPassword(password);
        userService.updateUser(existing);
        session.setAttribute("user", existing);
        ra.addFlashAttribute("success", "修改成功");
        return "redirect:/user/home";
    }

    @GetMapping("/type/{type}")
    public String typeTransactions(@PathVariable String type, HttpSession session, Model model) {
        User user = checkLogin(session);
        if (user == null) return "redirect:/";
        model.addAttribute("transactions", transactionService.getUserTransactionsByType(user.getUserId(), type));
        model.addAttribute("typeName", type);
        model.addAttribute("user", user);
        return "type-detail";
    }

    @GetMapping("/chart")
    public String chart(HttpSession session, Model model) {
        User user = checkLogin(session);
        if (user == null) return "redirect:/";
        Map<String, Double> typeSum = transactionService.getUserCurrentMonthTransactions(user.getUserId())
                .stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));
        model.addAttribute("typeSum", typeSum);
        model.addAttribute("user", user);
        return "chart";
    }

    private Transaction checkOwnership(Long txId, User user) {
        Transaction tx = transactionService.findById(txId);
        return (tx != null && tx.getUser().getUserId().equals(user.getUserId())) ? tx : null;
    }

    @PostMapping("/transaction/update")
    public String updateTx(@RequestParam Long transactionId, @RequestParam Double amount,
                           @RequestParam String date, HttpSession session, RedirectAttributes ra) {
        User user = checkLogin(session);
        if (user == null) return "redirect:/";
        Transaction tx = checkOwnership(transactionId, user);
        if (tx != null) {
            tx.setAmount(amount);
            tx.setDate(LocalDate.parse(date));
            transactionService.save(tx);
            ra.addFlashAttribute("success", "修改成功");
            return "redirect:/user/type/" + tx.getType();
        }
        ra.addFlashAttribute("error", "无权操作");
        return "redirect:/user/home";
    }
}