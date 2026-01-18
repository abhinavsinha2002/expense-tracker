package com.abhinav.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.UserRepository;
import com.abhinav.expense_tracker.security.JwtUtil;
import com.abhinav.expense_tracker.service.CsvService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/csv")
public class CsvController {
    @Autowired private CsvService csvService;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;

    @GetMapping("/export")
    public ResponseEntity<String> export(HttpServletRequest req){
        String username = jwtUtil.extractUsername(req.getHeader("Authorization").substring(7));
        User u = userRepository.findByUsername(username).orElseThrow();
        String csv = csvService.exportCsvForUser(u.getId());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=expenses.csv").body(csv);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file,HttpServletRequest req) throws Exception{
        String username = jwtUtil.extractUsername(req.getHeader("Authorization").substring(7));
        User u = userRepository.findByUsername(username).orElseThrow();
        csvService.importCsv(file, u.getId());
        return ResponseEntity.ok("Imported");
    }
        


}
