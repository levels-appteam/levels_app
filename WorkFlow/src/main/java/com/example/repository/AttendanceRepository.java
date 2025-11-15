package com.example.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.domain.entity.AttendanceEntity;
import com.example.domain.entity.UserEntity;

/**
 * アテンダンスリポシトリークラス
 * 勤怠打刻状況を管理するリポシトリークラス
 */
@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {
	// 特定のユーザーに紐づく勤怠履歴を取得（新しい順）
	List<AttendanceEntity> findByUserEntityIdOrderByAtDesc(UserEntity userEntity);
	
	AttendanceEntity findFirstByUserEntityIdOrderByAtDesc(Integer userId);
	
	/**
	 * 打刻情報チェック
	 * @param userEntity
	 * @param workDate
	 * @return
	 */
	Optional<AttendanceEntity> findByUserEntityAndWorkDate(UserEntity userEntity, LocalDate workDate);
	
}