package com.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.domain.entity.AttendanceEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.AttendanceType;
import com.example.repository.AttendanceRepository;
import com.example.repository.UserRepository;
import com.example.service.AttendanceSummaryService;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

	@InjectMocks
	private AttendanceServiceImpl attendanceService;

	@Mock
	private AttendanceRepository attendanceRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AttendanceSummaryService attendanceSummaryService;

	private UserEntity user;
	private Integer userId = 1;
	private LocalDate today;

	@BeforeEach
	void setUp() {
		user = new UserEntity();
		user.setId(userId);
		today = LocalDate.now();
	}

	@Test
	@DisplayName("正常：当日未登録 → 新規打刻が保存される")
	void testRecordPunch_newEntry() {
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(attendanceRepository.findByUserEntityAndWorkDate(user, today)).thenReturn(Optional.empty());

		attendanceService.recordPunch(userId, AttendanceType.IN);

		verify(attendanceRepository).save(any(AttendanceEntity.class));
		verify(attendanceSummaryService).updateSummary(user, AttendanceType.IN);
	}

	@Test
	@DisplayName("正常：当日既登録 → typeとatが更新される")
	void testRecordPunch_existingEntry() {
		AttendanceEntity existing = new AttendanceEntity();
		existing.setUserEntity(user);
		existing.setWorkDate(today);
		existing.setType(AttendanceType.IN);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(attendanceRepository.findByUserEntityAndWorkDate(user, today)).thenReturn(Optional.of(existing));

		attendanceService.recordPunch(userId, AttendanceType.OUT);

		assertThat(existing.getType()).isEqualTo(AttendanceType.OUT);
		verify(attendanceRepository).save(existing);
		verify(attendanceSummaryService).updateSummary(user, AttendanceType.OUT);
	}

	@Test
	@DisplayName("異常：userIdが存在しない場合 → nullでNPEなどにならないこと")
	void testRecordPunch_userNotFound() {
		// given
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		try {
			// when
			attendanceService.recordPunch(userId, AttendanceType.IN);
			// then
			// ここまで来れば「例外が発生しなかった」と判断
		} catch (Exception e) {
			fail("例外が発生しました: " + e.getMessage());
		}
	}

	@Test
	@DisplayName("正常：勤怠履歴を日付降順で取得")
	void testGetAttendanceHistory() {
		List<AttendanceEntity> dummyList = new ArrayList<AttendanceEntity>();
		dummyList.add(new AttendanceEntity());
		dummyList.add(new AttendanceEntity());

		when(attendanceRepository.findByUserEntityIdOrderByAtDesc(user)).thenReturn(dummyList);

		List<AttendanceEntity> result = attendanceService.getAttendanceHistory(user);
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("正常：打刻履歴なし → OUTを返す")
	void testGetLoginUserType_none() {
		when(attendanceRepository.findFirstByUserEntityIdOrderByAtDesc(userId)).thenReturn(null);

		AttendanceType type = attendanceService.getLoginUserType(userId);
		assertThat(type).isEqualTo(AttendanceType.OUT);
	}

	@Test
	@DisplayName("正常：打刻履歴あり → 直近のタイプを返す")
	void testGetLoginUserType_present() {
		AttendanceEntity entity = new AttendanceEntity();
		entity.setType(AttendanceType.IN);

		when(attendanceRepository.findFirstByUserEntityIdOrderByAtDesc(userId)).thenReturn(entity);

		AttendanceType type = attendanceService.getLoginUserType(userId);
		assertThat(type).isEqualTo(AttendanceType.IN);
	}

	@Test
	@DisplayName("正常：当日打刻あり → trueを返す")
	void testHasPunchedToday_present() {
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(attendanceRepository.findByUserEntityAndWorkDate(user, today))
				.thenReturn(Optional.of(new AttendanceEntity()));

		boolean result = attendanceService.hasPunchedToday(userId);
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("正常：当日打刻なし → falseを返す")
	void testHasPunchedToday_notPresent() {
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(attendanceRepository.findByUserEntityAndWorkDate(user, today)).thenReturn(Optional.empty());

		boolean result = attendanceService.hasPunchedToday(userId);
		assertThat(result).isFalse();
	}
}