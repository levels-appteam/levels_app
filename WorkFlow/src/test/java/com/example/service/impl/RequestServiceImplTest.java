package com.example.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
	
	@InjectMocks
	private RequestServiceImpl requestServiceImpl;

	@Mock
	private UserServiceImpl userServiceImpl;
	
	@BeforeEach
	void setup () {
		
	}
	
	@Test
	@DisplayName("正常：requestRepositoryの値が返されること")
	void getUserRequests() {
		
	}

}
