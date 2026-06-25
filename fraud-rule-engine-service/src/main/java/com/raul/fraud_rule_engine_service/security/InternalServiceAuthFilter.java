package com.raul.fraud_rule_engine_service.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class InternalServiceAuthFilter extends OncePerRequestFilter {

	private final String expectedToken;

	public InternalServiceAuthFilter(@Value("${internal.security.service-token}") String expectedToken) {
		this.expectedToken = expectedToken;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !request.getRequestURI().startsWith("/internal/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.equals("Bearer " + expectedToken)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write("Unauthorized internal request");
			return;
		}
		filterChain.doFilter(request, response);
	}
}
