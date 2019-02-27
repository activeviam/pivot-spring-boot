package com.sbr.pivotspringboot.activepivot;

import com.qfs.security.cfg.impl.ACorsFilterConfig;
import com.qfs.security.impl.SpringCorsFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;

public class PivotCorsFilterSecurityConfig extends ACorsFilterConfig {

	@Override
	public Collection<String> getAllowedOrigins() {
		return Arrays.asList();
	}

	@Bean
	@Override
	public SpringCorsFilter corsFilter() throws ServletException {
		final SpringCorsFilter corsFilter = new SpringCorsFilter() {
			private static final String ALREADY_FILTERED_ATTRIBUTE = "SpringCorsFilter.FILTERED";

			@Override
			public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {

				if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
					throw new ServletException("OncePerRequestFilter just supports HTTP requests");
				}
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				HttpServletResponse httpResponse = (HttpServletResponse) response;

				boolean hasAlreadyFilteredAttribute = request.getAttribute(ALREADY_FILTERED_ATTRIBUTE) != null;

				if (hasAlreadyFilteredAttribute) {
					filterChain.doFilter(request, response);
				} else {
					// Do invoke this filter...
					request.setAttribute(ALREADY_FILTERED_ATTRIBUTE, Boolean.TRUE);
					try {
						super.doFilter(httpRequest, httpResponse, filterChain);
					} finally {
						// Remove the "already filtered" request attribute for this request.
						request.removeAttribute(ALREADY_FILTERED_ATTRIBUTE);
					}
				}
			}
		};
		corsFilter.init(getCorsFilterConfig());
		return corsFilter;
	}
}
