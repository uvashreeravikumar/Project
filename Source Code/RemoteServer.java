package com.gts.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.gts.model.FileRq;
import com.gts.model.OwnerFileUpdatePojo;
import com.gts.service.OwnerDaoImp;
import com.gts.service.OwnerFileDao;
import com.gts.service.UserDao;

@Controller
public class RemoteServer {
	@Autowired
	private OwnerDaoImp ownerDaoImp;
	@Autowired
	private UserDao userdao;
	@Autowired
	private OwnerFileDao ownerFileDaoImp;

	@RequestMapping(value = "/RemoteLogin", method = RequestMethod.POST)
	public ModelAndView RemoteServerLogin(Model model, ModelAndView mv, @RequestParam("email") String email,
			@RequestParam("pass") String pass) throws IOException {

		if ("remoteserver@gmail.com".equalsIgnoreCase(email) && "admin".equalsIgnoreCase(pass)) {
			model.addAttribute("ownerlist", ownerDaoImp.getOwnerdate());
			model.addAttribute("userlist", userdao.getUserdate());
			System.out.println("Login ok");
			mv.setViewName("services");
		} else {
			System.out.println("loginFaild");
			mv.setViewName("index");
		}
		return mv;

	}

	@RequestMapping(value = "/OwnerAZ", method = RequestMethod.POST)
	public ModelAndView ac(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("oid") String oid, Model model) throws IOException {
		String status = "accepted";
		System.out.println("Owner_ID---->" + oid + "\nSTATUS---->" + status);
		int result = ownerDaoImp.onUpdate(status, oid);
		boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		if (ajax) {
			// Handle ajax (JSON or XML) response.
		} else {
			model.addAttribute("ownerlist", ownerDaoImp.getOwnerdate());
			request.setAttribute("msg", "ok");
			mv.setViewName("services");
			// Handle regular (JSP) response.
		}
		return mv;

	}

	@RequestMapping(value = "/AllInfo")
	public ModelAndView UserInfoAreOwner(HttpServletRequest request, HttpServletResponse response, Model model,
			ModelAndView mv) throws IOException {
		model.addAttribute("ownerlist", ownerDaoImp.getOwnerdate());
		model.addAttribute("userlist", userdao.getUserdate());
		mv.setViewName("Userinfo");
		return mv;

	}

	@RequestMapping(value = "/ShowInfo")
	public ModelAndView AllInfoAreOwner(HttpServletRequest request, HttpServletResponse response, Model model,
			ModelAndView mv) throws IOException {
		model.addAttribute("ownerlist", ownerDaoImp.getOwnerdate());
		model.addAttribute("userlist", userdao.getUserdate());
		mv.setViewName("services");
		return mv;

	}

	@RequestMapping(value = "/userAzh", method = RequestMethod.POST)
	public ModelAndView USERaZH(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("uid") String uid, Model model) throws IOException {
		String status = "accepted";
		System.out.println("Owner_ID---->" + uid + "\nSTATUS---->" + status);
		// int result = ownerDaoImp.onUpdate(status, uid);
		int result = userdao.onUpdate(status, uid);
		boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		if (ajax) {
			// Handle ajax (JSON or XML) response.
		} else {
			model.addAttribute("ownerlist", ownerDaoImp.getOwnerdate());
			model.addAttribute("userlist", userdao.getUserdate());
			request.setAttribute("msg", "Suecss");
			mv.setViewName("Userinfo");
			// Handle regular (JSP) response.
		}
		return mv;

	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView UserDalete(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam("customerId") String customerId) throws IOException {
		int result = userdao.onDElete(customerId);
		model.addAttribute("ownerlist", ownerDaoImp.getOwnerdate());
		model.addAttribute("userlist", userdao.getUserdate());
		mv.setViewName("services");
		return mv;

	}

	@RequestMapping(value = "/ShowUpdateFile")
	public ModelAndView showUpdateFile(HttpServletRequest request, HttpServletResponse response, ModelAndView m,
			Model model) throws IOException {
		List<OwnerFileUpdatePojo> list = ownerFileDaoImp.getFileDetails();
		model.addAttribute("userfileinfo", ownerFileDaoImp.getFileDetails());
		m.setViewName("ownerfledetails");
		return m;
	}

	@RequestMapping(value = "/OwnerAZFiles", method = RequestMethod.POST)
	public ModelAndView fileAZ(HttpServletRequest request, HttpServletResponse response, ModelAndView m, Model model,
			@RequestParam("oid") String oid) throws IOException {
		String status = "accepted";
		System.out.println(oid);

		System.out.println(status);
		int result = ownerFileDaoImp.onFileUpdate(oid, status);
		boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		if (ajax) {
		} else {
			List<OwnerFileUpdatePojo> list = ownerFileDaoImp.getFileDetails();
			model.addAttribute("userfileinfo", ownerFileDaoImp.getFileDetails());
			request.setAttribute("msg", "Suecss");
			m.setViewName("ownerfledetails");
		}
		return m;

	}

	@RequestMapping(value = "/OwnerPassRq", method = RequestMethod.POST)
	public ModelAndView filePassing(HttpServletRequest request, HttpServletResponse response, ModelAndView m,
			Model model, @RequestParam("fileName") String fileName, @RequestParam("ownerId") String ownerId,
			@RequestParam("userId") String userId) throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		String dt = formatter.format(date);
		FileRq fq = new FileRq();
		fq.setDt(dt);
		fq.setFileName(fileName);
		fq.setOwnerId(ownerId);
		fq.setSessionKey("key");
		fq.setUserId(userId);
		fq.setStatus("waiting");
		boolean result = ownerFileDaoImp.saveRQ(fq);
		model.addAttribute("ownerlist", ownerFileDaoImp.getRqFiles());
		m.addObject("msg", "File Request The Cloud Owner");
		m.setViewName("userportal");

		return m;

	}
}
