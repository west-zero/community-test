package com.community.fo.controller;

import com.community.core.dto.ApiResponse;
import com.community.core.dto.MenuResponse;
import com.community.fo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenus() {
        List<MenuResponse> menus = menuService.getActiveMenus();
        return ResponseEntity.ok(ApiResponse.success(menus));
    }
}
