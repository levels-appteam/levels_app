package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.domain.entity.CalendarEntity;
import java.time.LocalDate;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEntity, Integer> {

	/**
	 * カレンダー取得
	 * @param end
	 * @param start
	 * @return
	 */
	List<CalendarEntity> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate end, LocalDate start);
}