package com.gts.controller;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gts.mail.Mail;

@Controller
@RequestMapping
public class MailComtroller {

	@GetMapping("/mailsend")
	public ModelAndView sendMail(Model mo, ModelAndView mv, HttpServletRequest request, HttpServletResponse response) {
		String str = commonMet(request);
		String spl[] = str.split("\\=");

		String to = "msmart870@gmail.com";
		String from = spl[0].trim();
		String subject = "AUTHENTICATION REQUEST";
		String messagge = "Hi" + "lll" + "...\nYour Application Registration Successful" + "\n Your Id :" + "112"
				+ "\n Your image key :";
		String host = "smtp.gmail.com";
		System.out.println("WELCOME");
		Mail msend = new Mail(host, from, spl[1].trim(), from, to, subject, messagge);

		request.setAttribute("msg", "User Mail Send Successfully");
		return mv;
	}

	public String commonMet(HttpServletRequest request) {
		String accmail = "", mailpassword = "";
		try {
			Properties prop = new Properties();
			String s1 = request.getRealPath("/");
			FileInputStream fis = new FileInputStream(
					(new StringBuilder()).append(s1).append("Mail.properties").toString());
			prop.load(fis);
			accmail = prop.getProperty("mailid").trim();
			mailpassword = prop.getProperty("mailpassword").trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accmail + "=" + mailpassword;

	}

}
