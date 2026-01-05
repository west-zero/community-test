package com.community.fo.controller;

import com.community.core.dto.*;
import com.community.fo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            MemberResponse member = authService.signUp(request);
            return ResponseEntity.ok(ApiResponse.success("Registration successful", member));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberResponse>> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        try {
            MemberResponse member = authService.login(request);
            session.setAttribute("memberId", member.getId());
            session.setAttribute("member", member);
            return ResponseEntity.ok(ApiResponse.success("Login successful", member));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getCurrentUser(HttpSession session) {
        MemberResponse member = (MemberResponse) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.ok(ApiResponse.error("Not logged in"));
        }
        return ResponseEntity.ok(ApiResponse.success(member));
    }
}
