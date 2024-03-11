package com.activeviam.apps.controllers;

import com.activeviam.apps.parquet.ParquetLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private static final Logger LOGGER = Logger.getLogger(HelloController.class.getSimpleName());

	private final ParquetLoaderService parquetService;

    @GetMapping("/startCopyFiles")
    public String runStartCopy() {
		parquetService.startCopyFiles();
		return "Started Copying Files....";
    }

	@GetMapping("/stopCopyFiles")
	public String runStopCopy() {
		parquetService.stopCopyFiles();
		return "Stopped Copying Files....";
	}

	@GetMapping("/startLoading")
	public String runStartLoading() {
		parquetService.startLoading();
		return "Started loading....";
	}

	@GetMapping("/stopLoading")
	public String runStopLoading() {
		parquetService.stopLoading();
		return "Stopped loading.";
	}

    @GetMapping("/hello")
    public String index() { return "Hello from Pivot Spring Boot!"; }

}