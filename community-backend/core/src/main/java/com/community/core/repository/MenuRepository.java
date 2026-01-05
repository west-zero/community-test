package com.community.core.repository;

import com.community.core.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByActiveOrderByDisplayOrderAsc(Boolean active);
    List<Menu> findAllByOrderByDisplayOrderAsc();
}
