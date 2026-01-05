package com.community.bo.service;

import com.community.core.dto.MenuRequest;
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
public class AdminMenuService {

    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenus() {
        return menuRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::toMenuResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuResponse getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        return toMenuResponse(menu);
    }

    @Transactional
    public MenuResponse createMenu(MenuRequest request) {
        Menu menu = Menu.builder()
                .name(request.getName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        menu = menuRepository.save(menu);
        return toMenuResponse(menu);
    }

    @Transactional
    public MenuResponse updateMenu(Long id, MenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        
        menu.setName(request.getName());
        menu.setDescription(request.getDescription());
        menu.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) {
            menu.setActive(request.getActive());
        }
        
        menu = menuRepository.save(menu);
        return toMenuResponse(menu);
    }

    @Transactional
    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu not found");
        }
        menuRepository.deleteById(id);
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
