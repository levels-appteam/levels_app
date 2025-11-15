package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.entity.PaidleavesEntity;
import com.example.domain.entity.UserEntity;

public interface PaidleavesRepository extends JpaRepository<PaidleavesEntity, Integer>{
	
	
	 /**
	  * 指定ユーザーに対して、指定期間内に付与済みの有給が存在するか確認
	 * @param user
	 * @param start
	 * @param end
	 * @return
	 */
	boolean existsByUserEntityAndGrantDateBetween(UserEntity user, LocalDate start, LocalDate end);
	
	/**
	 * 最大付与日数制限
	 * @param user
	 * @param date
	 * @return
	 */
	List<PaidleavesEntity> findByUserEntityAndRevocationDateAfter(UserEntity user, LocalDate date);
	
	/**
	 * 有給付与情報取得
	 * @return
	 */
	List<PaidleavesEntity> findByUserEntityOrderByGrantDateAsc(UserEntity userEntity);

	/**
	 * ユーザー1件取得
	 * @param user
	 * @return
	 */
	List<PaidleavesEntity> findByUserEntity(UserEntity user);
	
	/**
	 * 有給管理用
	 * @param user
	 * @param today
	 * @return
	 */
	List<PaidleavesEntity> findByUserEntityAndRevocationDateGreaterThanEqualOrderByGrantDateAsc(UserEntity user, LocalDate today);
	
	/**
	 * 有給管理表示用
	 * @return
	 */
	List<PaidleavesEntity> findAllByOrderByUserEntity_NameAscGrantDateAsc();
	
	}

