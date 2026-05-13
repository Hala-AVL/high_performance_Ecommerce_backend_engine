package com.EcommerceApp.H2NS.repository;

import com.EcommerceApp.H2NS.model.DailySalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailySalesReportRepository extends JpaRepository<DailySalesReport, Long> {
   
    Optional<DailySalesReport> findByReportDate(LocalDate reportDate);
}