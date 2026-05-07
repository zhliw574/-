package com.fund.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

interface TransactionMapper {
    int insert(Object o);
    Object selectByUserId(Integer id);
    Object selectAll();
    int update(Object o);
    int delete(Integer id);
}

@Service
class UserService {
    @Autowired
    private TransactionMapper mapper;
    public boolean add(Object o) { return false; }
    public Object get(Integer id) { return null; }
    public boolean update(Object o) { return false; }
    public boolean delete(Integer id) { return false; }
}

@Service
class AdminService {
    @Autowired
    private TransactionMapper mapper;
    public Object all() { return null; }
}

@RestController
@RequestMapping("/user")
class UserController {
    @Autowired
    private UserService service;
    @PostMapping("/t") public String add(@RequestBody Object o) { return service.add(o) ? "ok" : "fail"; }
    @GetMapping("/t") public Object get(@RequestParam Integer id) { return service.get(id); }
    @PutMapping("/t") public String update(@RequestBody Object o) { return service.update(o) ? "ok" : "fail"; }
    @DeleteMapping("/t") public String delete(@RequestParam Integer id) { return service.delete(id) ? "ok" : "fail"; }
}

@RestController
@RequestMapping("/admin")
class AdminController {
    @Autowired
    private AdminService service;
    @GetMapping("/t") public Object all() { return service.all(); }
}