package com.example.demo.controller;

import com.example.demo.entity.Admin;
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
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;

    private Admin checkLogin(HttpSession session) {
        return (Admin) session.getAttribute("admin");
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Admin admin = checkLogin(session);
        if (admin == null) return "redirect:/";
        Map<String, Double> typeSum = transactionService.getAllCurrentMonthTransactions()
                .stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));
        String[] types = {"餐饮", "水电", "交通", "生活用品", "工资", "其他"};
        for (String t : types) model.addAttribute(t + "Sum", typeSum.getOrDefault(t, 0.0));
        model.addAttribute("admin", admin);
        return "admin-home";
    }

    @GetMapping("/type/{type}")
    public String typeTransactions(@PathVariable String type, @RequestParam(required = false) Long userId,
                                   HttpSession session, Model model) {
        Admin admin = checkLogin(session);
        if (admin == null) return "redirect:/";
        List<Transaction> list;
        if (userId != null) {
            list = transactionService.getUserTransactionsByType(userId, type);
        } else {
            list = transactionService.getAllTransactionsByType(type);
        }
        model.addAttribute("transactions", list);
        model.addAttribute("typeName", type);
        model.addAttribute("admin", admin);
        model.addAttribute("userId", userId);
        return "admin-type-detail";
    }

    @GetMapping("/chart")
    public String chart(HttpSession session, Model model) {
        Admin admin = checkLogin(session);
        if (admin == null) return "redirect:/";
        Map<String, Double> typeSum = transactionService.getAllCurrentMonthTransactions()
                .stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));
        model.addAttribute("typeSum", typeSum);
        model.addAttribute("admin", admin);
        return "admin-chart";
    }

    @PostMapping("/transaction/add")
    public String add(@RequestParam String type, @RequestParam Double amount,
                      @RequestParam String date, @RequestParam Long userId,
                      HttpSession session, RedirectAttributes ra) {
        Admin admin = checkLogin(session);
        if (admin == null) return "redirect:/";
        User user = userService.findById(userId);
        if (user == null) {
            ra.addFlashAttribute("error", "用户不存在");
            return "redirect:/admin/home";
        }
        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setDate(LocalDate.parse(date));
        tx.setType(type);
        tx.setUser(user);
        transactionService.save(tx);
        ra.addFlashAttribute("success", "添加成功");
        return "redirect:/admin/type/" + type;
    }

    @GetMapping("/transaction/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Admin admin = checkLogin(session);
        if (admin == null) return "redirect:/";
        Transaction tx = transactionService.findById(id);
        if (tx != null) {
            String type = tx.getType();
            transactionService.delete(id);
            ra.addFlashAttribute("success", "删除成功");
            return "redirect:/admin/type/" + type;
        }
        ra.addFlashAttribute("error", "流水不存在");
        return "redirect:/admin/home";
    }

    @PostMapping("/transaction/update")
    public String update(@RequestParam Long transactionId, @RequestParam Double amount,
                         @RequestParam String date, HttpSession session, RedirectAttributes ra) {
        Admin admin = checkLogin(session);
        if (admin == null) return "redirect:/";
        Transaction tx = transactionService.findById(transactionId);
        if (tx != null) {
            tx.setAmount(amount);
            tx.setDate(LocalDate.parse(date));
            transactionService.save(tx);
            ra.addFlashAttribute("success", "修改成功");
            return "redirect:/admin/type/" + tx.getType();
        }
        ra.addFlashAttribute("error", "流水不存在");
        return "redirect:/admin/home";
    }
}
