package com.example.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.entity.PaidleavesEntity;
import com.example.domain.entity.UserEntity;
import com.example.repository.PaidleavesRepository;
import com.example.repository.UserRepository;
import com.example.service.PaidleavesService;

@Service
public class PaidleavesServiceImpl implements PaidleavesService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PaidleavesRepository paidleavesRepository;

	/**
	 * ユーザー一覧取得
	 */
	public List<UserEntity> getUsersList() {
		return userRepository.findAll();
	}

	/**
	 * 有給付与一覧取得
	 */
	@Override
	public List<PaidleavesEntity> getAllPaidleaves(UserEntity userEntity) {
		return paidleavesRepository.findByUserEntityOrderByGrantDateAsc(userEntity);
	}

	/**
	 * ユーザーをidで取得
	 */
	public UserEntity findUserById(Integer id) {
		return userRepository.findById(id).orElse(null);
	}

	/**
	 *有給管理表示用
	 *ユーザー有給情報
	 */
	@Override
	public List<PaidleavesEntity> getAllPaidleaves() {
		return paidleavesRepository.findAllByOrderByUserEntity_NameAscGrantDateAsc();
	}

	/**
	 * ユーザーごとの有給取得
	 */
	public List<PaidleavesEntity> getPaidleavesByUser(UserEntity user) {
		return paidleavesRepository.findByUserEntity(user);
	}

	/**
	 * 勤怠年数計算
	 */
	public long calculateMonthsWorked(LocalDate joiningDate) {
		LocalDate today = LocalDate.now();
		return ChronoUnit.MONTHS.between(joiningDate, today);
	}

	/**
	 * 有給申請分の計算
	 */
	@Transactional
	@Override
	public void deductPaidLeaveDays(UserEntity user, float usedDays) {
		LocalDate today = LocalDate.now();

		// 有効期限内の有給を古い順に取得
		List<PaidleavesEntity> activeLeaves = paidleavesRepository
				.findByUserEntityAndRevocationDateGreaterThanEqualOrderByGrantDateAsc(user, today);

		float remainingToDeduct = usedDays;

		for (PaidleavesEntity leave : activeLeaves) {
			float available = leave.getDays() - leave.getUsedDays();
			if (available > 0) {
				float deduct = Math.min(available, remainingToDeduct);
				leave.setUsedDays(leave.getUsedDays() + deduct);
				remainingToDeduct -= deduct;
			}
			if (remainingToDeduct <= 0)
				break;
		}

		paidleavesRepository.saveAll(activeLeaves);
	}

	/**
	 * 有給付与月数計算
	 * 
	 * @param months
	 * @return
	 */
	public float calculateGrantDays(long months) {
		if (months < 6)
			return 0f;
		if (months < 18)
			return 10f;
		if (months < 30)
			return 11f;
		if (months < 42)
			return 12f;
		if (months < 54)
			return 14f;
		if (months < 66)
			return 16f;
		if (months < 78)
			return 18f;
		return 20f;
	}

	/**
	 * 指定ユーザーに対して有給を付与する条件を満たしていれば、付与処理を行う
	 * 
	 * @param user
	 */
	@Transactional
	public void grantPaidLeaveIfEligible(UserEntity user) {
		LocalDate today = LocalDate.now();
		LocalDate joiningDate = user.getJoiningDate();

		// 入社日入力がなかったらスキップする
		if (joiningDate == null) {
			return;
		}

		// 勤続年数（月単位）を計算
		long months = ChronoUnit.MONTHS.between(joiningDate, today);
		float grantDays = calculateGrantDays(months);

		// 同年度で付与されているかチェック
		LocalDate startOfYear = today.withDayOfYear(1);
		LocalDate endOfYear = today.withDayOfYear(today.lengthOfYear());
		boolean alreadyGranted = paidleavesRepository.existsByUserEntityAndGrantDateBetween(user, startOfYear,
				endOfYear);

		if (alreadyGranted) {
			System.out.println("今年はすでに付与済みです: " + user.getEmail());
			return;
		}

		// 失効していない有給の合計を計算
		List<PaidleavesEntity> activeLeaves = paidleavesRepository.findByUserEntityAndRevocationDateAfter(user, today);
		float totalDays = 0f;
		for (PaidleavesEntity pl : activeLeaves) {
			// 残日数を計算
			float remainingDays = pl.getDays() - pl.getUsedDays();

			totalDays += remainingDays;
		}

		if (totalDays + grantDays > 40f) {
			// 最大40日まで付与
			grantDays = 40f - totalDays;

			// 0以下になったら付与せず
			if (grantDays <= 0f) {
				System.out.println("保有日数が上限のため、付与できません: " + user.getEmail());
				return;
			}
		}

		// 新しい有給を登録
		PaidleavesEntity paidLeave = new PaidleavesEntity();
		paidLeave.setUserEntity(user);
		paidLeave.setGrantDate(today);
		// 2年で失効
		paidLeave.setRevocationDate(today.plusYears(2));
		paidLeave.setDays(grantDays);
		paidLeave.setUsedDays(0f);

		paidleavesRepository.save(paidLeave);
	}

	/**
	 * 有給残日数計算 ユーザー詳細表示用
	 */
	@Override
	public float getRemainingDays(UserEntity user) {
		LocalDate today = LocalDate.now();
		List<PaidleavesEntity> active = paidleavesRepository.findByUserEntityAndRevocationDateAfter(user, today);

		float total = 0f;
		for (PaidleavesEntity p : active) {
			float remaining = (p.getDays() != null ? p.getDays() : 0f)
					- (p.getUsedDays() != null ? p.getUsedDays() : 0f);
			if (remaining > 0f) {
				total += remaining;
			}
		}
		return total;
	}

}