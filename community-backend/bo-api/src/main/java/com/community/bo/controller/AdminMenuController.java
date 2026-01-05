package com.community.bo.controller;

import com.community.bo.service.AdminMenuService;
import com.community.core.dto.ApiResponse;
import com.community.core.dto.MenuRequest;
import com.community.core.dto.MenuResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllMenus() {
        List<MenuResponse> menus = adminMenuService.getAllMenus();
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenu(@PathVariable Long id) {
        try {
            MenuResponse menu = adminMenuService.getMenuById(id);
            return ResponseEntity.ok(ApiResponse.success(menu));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(@Valid @RequestBody MenuRequest request) {
        MenuResponse menu = adminMenuService.createMenu(request);
        return ResponseEntity.ok(ApiResponse.success("Menu created", menu));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody MenuRequest request) {
        try {
            MenuResponse menu = adminMenuService.updateMenu(id, request);
            return ResponseEntity.ok(ApiResponse.success("Menu updated", menu));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long id) {
        try {
            adminMenuService.deleteMenu(id);
            return ResponseEntity.ok(ApiResponse.success("Menu deleted", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
