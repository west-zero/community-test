package com.community.fo.service;

import com.community.core.dto.MenuResponse;
import com.community.core.entity.Menu;
import com.community.core.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getActiveMenus() {
        return menuRepository.findByActiveOrderByDisplayOrderAsc(true).stream()
                .map(this::toMenuResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Menu getMenuById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
    }

    private MenuResponse toMenuResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .displayOrder(menu.getDisplayOrder())
                .active(menu.getActive())
                .createdAt(menu.getCreatedAt())
                .build();
    }
}
