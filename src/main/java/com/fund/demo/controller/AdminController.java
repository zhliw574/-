package com.example.demo.controller;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    private Admin getAdmin(HttpSession session) {
        return (Admin) session.getAttribute("admin");
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Admin admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        List<Transaction> list = transactionService.getAllTransactions();
        Map<String, Double> typeMap = list.stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));

        model.addAttribute("typeMap", typeMap);
        model.addAttribute("admin", admin);
        model.addAttribute("transactions", list);

        return "admin-home";
    }

    @GetMapping("/type/{type}")
    public String type(@PathVariable String type,
                       @RequestParam(required = false) Long userId,
                       Model model, HttpSession session) {
        Admin admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        List<Transaction> transactions;

        if ("全部".equals(type)) {
            transactions = transactionService.getAllTransactions();
        }
        else if (userId != null) {
            transactions = transactionService.getUserTransactionsByType(userId, type);
        } else {
            transactions = transactionService.getAllTransactionsByType(type);
        }

        model.addAttribute("transactions", transactions);
        model.addAttribute("type", type);
        model.addAttribute("admin", admin);
        model.addAttribute("userId", userId);

        return "admin-type";
    }

    @GetMapping("/chart")
    public String chart(Model model, HttpSession session) {
        Admin admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        List<Transaction> list = transactionService.getAllTransactions();
        Map<String, Double> map = list.stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));

        model.addAttribute("admin", admin);
        model.addAttribute("map", map);
        return "admin-chart";
    }

    @GetMapping("/transaction/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra, HttpServletRequest request) {
        Transaction t = transactionService.findById(id);
        if (t == null) {
            ra.addFlashAttribute("error", "记录不存在");
            return "redirect:" + request.getHeader("Referer");
        }
        transactionService.delete(id);
        ra.addFlashAttribute("success", "删除成功");
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/transaction/update")
    public String update(@RequestParam Long transactionId,
                         @RequestParam Double amount,
                         @RequestParam String date,
                         RedirectAttributes ra, HttpServletRequest request) {
        Transaction t = transactionService.findById(transactionId);
        if (t == null) {
            ra.addFlashAttribute("error", "记录不存在");
            return "redirect:" + request.getHeader("Referer");
        }
        t.setAmount(amount);
        t.setDate(LocalDate.parse(date));
        transactionService.save(t);
        ra.addFlashAttribute("success", "修改成功");
        return "redirect:" + request.getHeader("Referer");
    }
}
