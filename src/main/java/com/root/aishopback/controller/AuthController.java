package com.root.aishopback.controller;

import com.root.aishopback.service.MonitorClientManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private MonitorClientManager monitorClientManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // Allow any username as long as password is 123456 for testing multiple clients
        if ("123456".equals(password)) {
            // Start the background monitor client thread for this mapped user
            monitorClientManager.startClientForUser(username);

            Map<String, Object> data = new HashMap<>();
            data.put("token", UUID.randomUUID().toString());
            
            Map<String, Object> user = new HashMap<>();
            user.put("id", 1);
            user.put("username", username);
            user.put("nickname", "测试用户");
            user.put("avatar", "https://picsum.photos/seed/avatar/100/100");
            user.put("email", "test@example.com");
            data.put("user", user);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", data);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        if (username != null) {
            // Stop the background monitor client thread for this user
            monitorClientManager.stopClientForUser(username);
        }
        
        return ResponseEntity.ok(Map.of("code", 200, "message", "退出成功"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
         return ResponseEntity.ok(Map.of(
             "code", 200, 
             "data", Map.of(
                 "id", 1, 
                 "username", "test", 
                 "nickname", "测试用户", 
                 "avatar", "https://picsum.photos/seed/avatar/100/100"
             )
         ));
    }
}
