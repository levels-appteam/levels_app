package com.example.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.entity.AttendanceEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;
import com.example.repository.AttendanceRepository;
import com.example.repository.UserRepository;
import com.example.service.AttendanceService;
import com.example.service.AttendanceSummaryService;

/**
 * アテンダンスサービスの実装クラス 打刻の情報をDBに記録する
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AttendanceSummaryService attendanceSummaryService;

	/**
	 * ログイン中のユーザーの打刻情報を取得
	 */
	@Transactional
	@Override
	public void recordPunch(Integer userId, AttendanceType type) {
		Optional<UserEntity> option = userRepository.findById(userId);
		UserEntity userEntity = option.orElse(null);

		// 打刻した日の日付を取得
		LocalDate todayDate = LocalDate.now();

		// ログインユーザーの打刻の状態を確認
		Optional<AttendanceEntity> optional = attendanceRepository.findByUserEntityAndWorkDate(userEntity, todayDate);

		if (optional.isEmpty()) {
			// 打刻情報（ユーザー、打刻種類、打刻時間）
			AttendanceEntity attendanceEntity = new AttendanceEntity();
			attendanceEntity.setUserEntity(userEntity);
			attendanceEntity.setType(type);
			attendanceEntity.setAt(LocalDateTime.now());
			attendanceEntity.setWorkDate(todayDate);
			// リポシトリに保存
			attendanceRepository.save(attendanceEntity);
		} else {
			AttendanceEntity apdAttendanceEntity = optional.get();
			apdAttendanceEntity.setType(type);
			apdAttendanceEntity.setAt(LocalDateTime.now());
			apdAttendanceEntity.setWorkDate(todayDate);
			// リポシトリに保存
			attendanceRepository.save(apdAttendanceEntity);
		}
		attendanceSummaryService.updateSummary(userEntity, type);
	}

	/**
	 * 勤怠履歴を取得 日付（降順）
	 */
	@Override
	public List<AttendanceEntity> getAttendanceHistory(UserEntity userEntity) {
		return attendanceRepository.findByUserEntityIdOrderByAtDesc(userEntity);
	}

	/**
	 * ログインユーザーの取得 打刻初期値設定
	 */
	@Override
	public AttendanceType getLoginUserType(Integer userId) {
		AttendanceEntity attendanceEntity = attendanceRepository.findFirstByUserEntityIdOrderByAtDesc(userId);

		if (attendanceEntity == null || attendanceEntity.getType() == null) {
			return AttendanceType.OUT;
		} else {
			AttendanceType type = attendanceEntity.getType();
			return type;
		}
	}
}