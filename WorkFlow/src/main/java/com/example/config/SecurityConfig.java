package com.example.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * セキュリティコンフィグクラス Spring Securityの設定用クラス
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	/**
	 * パスワードを暗号化する
	 */
	@Bean
	protected PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authz -> authz.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
				.permitAll().requestMatchers("/h2-console/**").permitAll()
				// ログイン不要のページ
				.requestMatchers("/login", "/user/signup", "/password/change").permitAll().anyRequest().authenticated()

		).formLogin(login -> login.loginProcessingUrl("/login").loginPage("/login").failureUrl("/login?error")
				.usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/dashboard", true)
				.permitAll()).csrf(csrf -> csrf.disable());

		return http.build();
	}
}