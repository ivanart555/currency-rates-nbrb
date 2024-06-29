package com.vadarod.repository;

import com.vadarod.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCurId(Long curId);
}
