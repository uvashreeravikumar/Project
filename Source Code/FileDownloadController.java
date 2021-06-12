package com.gts.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/download.do")
@PropertySource("classpath:path.properties")
public class FileDownloadController {
	@Autowired
	private Environment environment;

	@RequestMapping(method = RequestMethod.GET)
	public void doDownload(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("filename") String filename) throws IOException {
		try {

			String fileName = filename+".txt";
			String filePathToBeServed = environment.getRequiredProperty("locationpath");
			File fileToDownload = new File(filePathToBeServed + fileName);

			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/force-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
			inputStream.close();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

	}

}
