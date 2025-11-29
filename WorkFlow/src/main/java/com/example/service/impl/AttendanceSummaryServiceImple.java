package com.example.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.dto.AttendanceSummaryDto;
import com.example.domain.entity.AttendanceSummaryEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;
import com.example.mapper.AttendanceSummaryMapper;
import com.example.repository.AttendanceSummaryRepository;
import com.example.service.AttendanceSummaryService;

/**
 * アテンダンスサマリーサービスクラスの実装クラス 勤務時間をDBに記録する
 */
@Service
public class AttendanceSummaryServiceImple implements AttendanceSummaryService {

	@Autowired
	private AttendanceSummaryRepository summaryRepository;

	/**
	 * 打刻日時をINT型(分に変換する)にするメソッド
	 * 
	 * @return
	 */
	private int getTimeIntMinutes() {
		java.time.LocalTime time = java.time.LocalTime.now();
		return time.getHour() * 60 + time.getMinute();
	}

	/**
	 * 承認時の更新用（打刻日時をINT型(分に変換する)にするメソッド）
	 * @param time
	 * @return
	 */
	private int convertToMinutes(String time) {
		String[] parts = time.split(":");
		int hour = Integer.parseInt(parts[0]);
		int min = Integer.parseInt(parts[1]);
		return hour * 60 + min;
	}

	/**
	 * AttendanceSummaryDtoを返すメソッド
	 */
	@Override
	public List<AttendanceSummaryDto> getSummaryDtoByUser(UserEntity userEntity, LocalDate startMonth,
			LocalDate endMonth) {
		List<AttendanceSummaryEntity> entities = summaryRepository
				.findByUserAndWorkDateBetweenOrderByWorkDateAsc(userEntity, startMonth, endMonth);
		return entities.stream().map(AttendanceSummaryMapper::toSummaryDto).toList();
	}

	/**
	 * 打刻時間の情報を更新する（出勤→退勤等）
	 */
	@Override
	public void updateSummary(UserEntity userEntity, AttendanceType attendanceType) {
		Optional<AttendanceSummaryEntity> option = summaryRepository.findByUserAndWorkDate(userEntity, LocalDate.now());
		AttendanceSummaryEntity summaryEntity = option.orElse(null);

		// データがあるか確認
		if (summaryEntity == null) {
			summaryEntity = new AttendanceSummaryEntity();
			summaryEntity.setUser(userEntity);
			summaryEntity.setWorkDate(LocalDate.now());
		}

		// 打刻情報をINT型（分で保存）に
		switch (attendanceType) {
		case IN:
			summaryEntity.setWorkStart(getTimeIntMinutes());
			break;
		case OUT:
			summaryEntity.setWorkEnd(getTimeIntMinutes());
			break;
		case BREAK_IN:
			summaryEntity.setBreakStart(getTimeIntMinutes());
			break;
		case BREAK_OUT:
			summaryEntity.setBreakEnd(getTimeIntMinutes());
			break;
		default:
			break;
		}
		summaryRepository.save(summaryEntity);
	}

	/**
	 * ユーザーに紐づく勤怠サマリーデータを取得する
	 */
	@Override
	public List<AttendanceSummaryEntity> getSummaryByUser(UserEntity userEntity, LocalDate startMonth,
			LocalDate endMonth) {
		return summaryRepository.findByUserAndWorkDateBetweenOrderByWorkDateAsc(userEntity, startMonth, endMonth);
	}

	/**
	 * 打刻時間を取得（承認修正用）
	 */
	@Override
	public AttendanceSummaryDto getWorkTimeDto(String workStart, String workEnd) {
		AttendanceSummaryDto dto = new AttendanceSummaryDto();
		dto.setWorkStart(workStart);
		dto.setWorkEnd(workEnd);
		return dto;
	}

	/**
	 *承認時の打刻情報更新
	 */
	@Override
	public void updateWorkTime(UserEntity user, LocalDate date, String workStart, String workEnd) {
		Optional<AttendanceSummaryEntity> optional = summaryRepository.findByUserAndWorkDate(user, date);
		AttendanceSummaryEntity entity = optional.orElse(new AttendanceSummaryEntity());

		entity.setUser(user);
		entity.setWorkDate(date);

		if (Objects.nonNull(workStart) && !workStart.isEmpty()) {
			entity.setWorkStart(convertToMinutes(workStart));
		}

		if (Objects.nonNull(workEnd) && !workEnd.isEmpty()) {
			entity.setWorkEnd(convertToMinutes(workEnd));
		}

		summaryRepository.save(entity);
	}
}