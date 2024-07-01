package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.ClothCategory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothCatergoryRepo extends JpaRepository<ClothCategory , Long> {
}
