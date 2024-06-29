package com.vadarod.repository;

import com.vadarod.entities.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findByDateAndCurrencyCode(LocalDate date, String currencyCode);
}
