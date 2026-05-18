package com.example.demo.controller;

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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        List<Transaction> list = transactionService.getUserCurrentMonthTransactions(user.getUserId());
        Map<String, Double> typeMap = list.stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));

        model.addAttribute("typeMap", typeMap);
        model.addAttribute("user", user);
        return "user-home";
    }

    @GetMapping("/info")
    public String info(Model model, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/edit")
    public String edit(Model model, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/update")
    public String update(@RequestParam String username,
                         @RequestParam String password,
                         HttpSession session,
                         RedirectAttributes ra,
                         HttpServletRequest request) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        User exist = userService.findById(user.getUserId());
        exist.setUsername(username);
        exist.setPassword(password);
        userService.updateUser(exist);

        session.setAttribute("user", exist);
        ra.addFlashAttribute("success", "修改成功");

        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/type/{type}")
    public String type(@PathVariable String type, Model model, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        List<Transaction> transactions = transactionService.getUserTransactionsByType(user.getUserId(), type);
        model.addAttribute("transactions", transactions);
        model.addAttribute("type", type);
        model.addAttribute("user", user);
        return "user-type";
    }

    @GetMapping("/chart")
    public String chart(Model model, HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        List<Transaction> list = transactionService.getUserCurrentMonthTransactions(user.getUserId());
        Map<String, Double> map = list.stream().collect(Collectors.groupingBy(Transaction::getType, Collectors.summingDouble(Transaction::getAmount)));

        model.addAttribute("map", map);
        model.addAttribute("user", user);
        return "user-chart";
    }

    @PostMapping("/transaction/add")
    public String add(
            @RequestParam String type,
            @RequestParam Double amount,
            @RequestParam String date,
            HttpSession session,
            RedirectAttributes ra,
            HttpServletRequest request
    ) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        Transaction t = new Transaction();
        t.setUser(user);
        t.setType(type);
        t.setAmount(amount);
        t.setDate(java.time.LocalDate.parse(date));

        transactionService.save(t);
        ra.addFlashAttribute("success", "添加成功！");

        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/transaction/update")
    public String updateTransaction(
            @RequestParam Long transactionId,
            @RequestParam Double amount,
            @RequestParam String date,
            HttpSession session,
            RedirectAttributes ra,
            HttpServletRequest request
    ) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";

        Transaction t = transactionService.findById(transactionId);
        if (t == null || !t.getUser().getUserId().equals(user.getUserId())) {
            ra.addFlashAttribute("error", "无权限或记录不存在");
            return "redirect:" + request.getHeader("Referer");
        }

        t.setAmount(amount);
        t.setDate(java.time.LocalDate.parse(date));
        transactionService.save(t);

        ra.addFlashAttribute("success", "修改成功！");
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/transaction/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra, HttpServletRequest request) {
        Transaction t = transactionService.findById(id);
        if (t == null) {
            ra.addFlashAttribute("error", "记录不存在");
            return "redirect:" + request.getHeader("Referer");
        }
        transactionService.delete(id);
        ra.addFlashAttribute("success", "删除成功！");
        return "redirect:" + request.getHeader("Referer");
    }
}
