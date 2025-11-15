package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.RequestKind;
import com.example.domain.enums.RequestStatus;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Integer> {

	/**
	 * ログインユーザーの申請
	 * 
	 * @param userEntity
	 * @return
	 */
	List<RequestEntity> findByUserEntity(UserEntity userEntity);

	/**
	 * 全ユーザー申請状況
	 * 
	 * @param status
	 * @return
	 */
	List<RequestEntity> findByStatus(RequestStatus status);

	/**
	 * 申請の種類
	 * 
	 * @param userEntity
	 * @param kind
	 * @return
	 */
	List<RequestEntity> findByUserEntityAndKind(UserEntity userEntity, RequestKind requestKind);

	/**
	 * 承認画面用（新しい順で表示）
	 * 
	 * @return
	 */
	@Query("""
			  select r
			  from RequestEntity r
			  join fetch r.userEntity u
			  order by r.submittedAt desc
			""")
	List<RequestEntity> findAllWithUserOrderBySubmittedAtDesc();

	List<RequestEntity> findByUserEntityAndStatusAndKindAndTargetDateBetween(UserEntity userEntity,
			RequestStatus status, RequestKind kind, LocalDate startDate, LocalDate endDate);
}