package com.example.service;

import java.time.LocalDate;
import java.util.List;

import com.example.domain.entity.UserEntity;

/**
 * ユーザー情報に関するビジネスロジックを定義するサービスインターフェース
 */
public interface UserService {
	/**
	 * ユーザー取得
	 * 
	 * @param userEntity
	 */
	public List<UserEntity> getUserEntities(UserEntity userEntity);

	/**
	 * ユーザー１件取得
	 * 
	 * @param email
	 */
	public UserEntity getUserOne(String email);

	/**
	 * IDでユーザーを1件取得
	 * 
	 * @param id
	 * @return ユーザー情報
	 */
	public UserEntity getUserById(Integer id);

	/**
	 * ユーザー更新
	 * 
	 * @param email
	 * @param password
	 * @param name
	 */
	public void updateUserOne(String email, String password, String name, Integer departmentId, LocalDate joiningDate);
	
	/**
	 * ユーザー削除
	 * 
	 * @param email
	 */
	public void deleteByEmail(String email);
	

	/**
	 * ログインユーザー取得
	 * 
	 * @param email
	 */
	public UserEntity getLoginUser(String email);
	
	/**
	 * パスワード更新用
	 * @param email
	 * @param encodedPassword
	 */
	void updatePassword(String email, String encodedPassword);

}
