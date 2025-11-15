package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.entity.RequestEntity;
import com.example.domain.entity.UserEntity;
import com.example.domain.enums.RequestKind;
import com.example.form.RequestForm;
import com.example.service.RequestService;
import com.example.service.UserService;

@Controller
@RequestMapping("/requests")
public class RequestController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RequestService requestService;
	
	
	/**
	 * 申請種類選択
	 * @param kind
	 * @param userDetails
	 * @return
	 */
	
	@GetMapping(value = "/kind", params = "!kind")
	public String kindSelection() {
		return "requests/kind";
	}
	
	@GetMapping(value = "/kind", params = "kind")
	public String kind(
			@RequestParam("kind") String kind, 
			@AuthenticationPrincipal UserDetails userDetails, 
			Model model
			) {
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		
		//String型からENUM型に変換して渡す
		RequestKind requestKind = RequestKind.valueOf(kind);
		
		model.addAttribute("requestKind", requestKind);
		model.addAttribute("loginUser", loginUser);
		
		return "requests/kind";
	}
	
	/**
	 * 有給申請画面表示
	 * @param userDetails
	 * @param model
	 * @return
	 */
	@GetMapping("/paidleave")
	public String showPaidLeave(
			@AuthenticationPrincipal UserDetails userDetails, 
			Model model) {
		
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		
		model.addAttribute("requestForm", new RequestForm());
		
		//申請状況をリスト表示
		List<RequestEntity> requestCorrectionList = requestService.getUserPaidLeaveRequests(loginUser);
		
		//申請状況を分けて表示
		List<RequestEntity> pendingList = new java.util.ArrayList<>();
		List<RequestEntity> approvedList = new java.util.ArrayList<>();
		List<RequestEntity> rejectedList = new java.util.ArrayList<>();
		List<RequestEntity> remandList = new java.util.ArrayList<>();
		
		for (RequestEntity request : requestCorrectionList) {
			switch (request.getStatus()) {
			case PENDING:
				pendingList.add(request);
				break;
			case APPROVED:
				approvedList.add(request);
				break;
			case REJECTED:
				rejectedList.add(request);
				break;
			case REMAND:
				remandList.add(request);
				break;
			default:
				break;
			}
		}
		
		//モデルに渡す
		model.addAttribute("requestList", requestCorrectionList); 
		model.addAttribute("pendingList", pendingList);
		model.addAttribute("approvedList", approvedList);
		model.addAttribute("rejectedList", rejectedList);
		model.addAttribute("remandList", remandList);
		
		return "requests/paidleave";
	}
	/**
	 * 有給申請登録
	 * @param requestForm
	 * @param userDetails
	 * @return
	 */
	@PostMapping("/paidleave")
	public String paidLeave(
			@ModelAttribute RequestForm requestForm,
			@AuthenticationPrincipal UserDetails userDetails) {
		
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		
		requestService.savePaidLeaveRequest(loginUser, requestForm);
		
		return "redirect:/requests/paidleave";
	}
	/**
	 * 勤務修正申請画面表示
	 * @param userDetails
	 * @param model
	 * @return
	 */
	@GetMapping("/correction")
	public String showCorrectionForm(
			@AuthenticationPrincipal UserDetails userDetails,
			Model model) {
		
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		
		model.addAttribute("requestForm", new RequestForm());
		
		//申請状況をリスト表示
		List<RequestEntity> requestList = requestService.getUserCorrectionRequests(loginUser);
		
		//申請状況を分けて表示
		List<RequestEntity> pendingList = new java.util.ArrayList<>();
		List<RequestEntity> approvedList = new java.util.ArrayList<>();
		List<RequestEntity> rejectedList = new java.util.ArrayList<>();
		List<RequestEntity> remandList = new java.util.ArrayList<>();
		
		for (RequestEntity request : requestList) {
			switch (request.getStatus()) {
			case PENDING:
				pendingList.add(request);
				break;
			case APPROVED:
				approvedList.add(request);
				break;
			case REJECTED:
				rejectedList.add(request);
				break;
			case REMAND:
				remandList.add(request);
				break;
			default:
				break;
			}
		}
		
		//モデルに渡す
		model.addAttribute("requestList", requestList); 
		model.addAttribute("pendingList", pendingList);
		model.addAttribute("approvedList", approvedList);
		model.addAttribute("rejectedList", rejectedList);
		model.addAttribute("remandList", remandList);
		
		return "requests/correction";
	}
	
	/**
	 * 勤務修正登録
	 * @param requestForm
	 * @param userDetails
	 * @return
	 */
	@PostMapping("/correction")
	public String correction(
			@ModelAttribute RequestForm requestForm,
			@AuthenticationPrincipal UserDetails userDetails) {
		
		String email = userDetails.getUsername();
		UserEntity loginUser = userService.getLoginUser(email);
		
		requestService.saveCorrectionRequest(loginUser, requestForm);
		
		return "redirect:/requests/correction";
	}
}