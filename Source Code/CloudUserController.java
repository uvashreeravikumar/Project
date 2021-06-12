package com.gts.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.Demo.EncryptorAesGcmPassword;
import com.gts.model.OwnerFileUpdatePojo;
import com.gts.model.UserPojo;
import com.gts.service.OwnerFileDao;
import com.gts.service.UserDao;

@Controller
@RequestMapping
@PropertySource("classpath:path.properties")
public class CloudUserController {
	@Autowired
	private Environment environment;
	@Autowired
	private UserDao userDao;
	@Autowired
	private OwnerFileDao ownerfile;

	@GetMapping("/userregister")
	public ModelAndView test(HttpServletResponse response) throws IOException {
		return new ModelAndView("userregister");
	}

	@PostMapping("/userreg")
	public ModelAndView OwnerRegister(ModelAndView mv, @RequestParam("name") String name,
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("phone") String phone, HttpSession session) throws IOException {
		session.setAttribute("sname", name);
		session.setAttribute("semail", email);
		session.setAttribute("sphone", phone);
		session.setAttribute("spass", password);
		// mv.setViewName("userAddBio");
		// Map<String, String> map = new HashMap<String, String>();
		HashSet<String> hset = new HashSet<String>();
		List<UserPojo> list = userDao.getUserdate();
		for (UserPojo userPojo : list) {
			System.out.println(userPojo.getEmail());
			hset.add(userPojo.getEmail());
		}
		boolean emailddd = hset.contains(email);
		if (emailddd == true) {
			// System.out.println("llll");
			mv.addObject("msg", "This Email Allready Register");
			mv.setViewName("userregister");
		} else {
			System.out.println("lddddlll");
			mv.setViewName("userAddBio");
		}
		return mv;

		// return mv;
	}

	@GetMapping("/UserReg")
	public ModelAndView RegisterProcess(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("txttemplate") String fig, HttpSession session) throws IOException {
		Random rand = new Random();
		int rand_int1 = rand.nextInt(1000);
		String status = "pending";
		String userid = "user" + Integer.toString(rand_int1);
		String pid = (String) session.getAttribute("pid");
		String name = (String) session.getAttribute("sname");
		String email = (String) session.getAttribute("semail");
		String phone = (String) session.getAttribute("sphone");
		String password = (String) session.getAttribute("spass");
		UserPojo user = new UserPojo();
		user.setName(name);
		user.setEmail(email);
		user.setPhone(phone);
		user.setPassword(password);
		user.setUserid(userid);
		user.setFingerPrint(fig);
		user.setStatus(status);
		user.setUserid(userid);

		boolean count = userDao.saveUser(user);
		mv.addObject("msg", "Registration Successfull");
		mv.setViewName("userregister");

		return mv;

	}

	@RequestMapping(value = "/userLogin", method = RequestMethod.POST)
	public ModelAndView loginProcess(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("login") UserPojo login, HttpSession session) throws IOException {
		ModelAndView mav = null;
		UserPojo user = userDao.validateUser(login);

		if (null != user) {
			mav = new ModelAndView("userloginBio");
			mav.addObject("email", user.getUserid());
			session.setAttribute("finger", user.getFingerPrint());
			session.setAttribute("Useremail", user.getEmail());
			session.setAttribute("cuserId", user.getUserid());

		} else {
			mav = new ModelAndView("userregister");
			mav.addObject("lmsg", "Username or Password is wrong!!");
		}

		return mav;
	}

	@RequestMapping(value = "/userLoginSucess", method = RequestMethod.POST)
	public ModelAndView loginsucess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			ModelAndView mv, Model model) throws Exception {
		String name = (String) session.getAttribute("Useremail");
		Map<String, String> map = new HashMap<String, String>();
		List<UserPojo> lis = userDao.getUserdate();
		for (UserPojo userPojo : lis) {
			map.put(userPojo.getEmail(), userPojo.getStatus());
		}

		String status = map.get(name);
		if (status.equalsIgnoreCase("pending")) {
			System.out.println("Contain is matched");
			request.setAttribute("msg", "Your processing Pending pleace Wait....!");
			// model.addObject("msg", "Your processing Pending pleace Wait");
			mv = new ModelAndView("userregister");
		} else {
			System.out.println("Contain is not matched");
			mv = new ModelAndView("userportal");
			model.addAttribute("ownerlist", ownerfile.getFileDetails());
		}
		return mv;
	}

	@RequestMapping(value = "ShowAllCloudFiles", method = RequestMethod.GET)
	public ModelAndView showCloudFile(HttpServletRequest request, HttpServletResponse response, ModelAndView mv,
			Model model) throws Exception {
		model.addAttribute("ownerlist", ownerfile.getFileDetails());
		mv.setViewName("ShowupdatefileInfo");
		return mv;

	}

	@RequestMapping(value = "OwnerPassFileRequest", method = RequestMethod.POST)
	public ModelAndView OwnerPassFileRequest(HttpServletRequest request, HttpServletResponse response, ModelAndView mv,
			Model model, @RequestParam("FileName") String FileName, @RequestParam("foi") String foi)
			throws IOException {
		System.out.println("FileName :" + FileName + "fileownerId :" + foi);
		model.addAttribute("ownerlist", ownerfile.getFileDetails());
		model.addAttribute("msg", "Request Pass File Owner");

		mv.setViewName("userportal");

		return mv;

	}

	@RequestMapping(value = "/rqFileDetails", method = RequestMethod.GET)
	public ModelAndView UserrqFileDetailst(HttpServletRequest request, HttpServletResponse response, ModelAndView mv,
			Model model, HttpSession session) throws IOException {
		String userid = (String) session.getAttribute("cuserId");
		System.out.println(userid);
		model.addAttribute("userfileinfo", ownerfile.getRqFiles());
		request.setAttribute("userfileinfo", ownerfile.getRqFiles());
		mv.setViewName("rqFileDetails");

		return mv;

	}

	@RequestMapping(value = "/DownlodeingFiles", method = RequestMethod.POST)
	public ModelAndView fileDownloding(HttpServletRequest request, HttpServletResponse response, ModelAndView mv,
			Model model, @RequestParam("ownerId") String ownerID, @RequestParam("key") String key) throws IOException {
		System.out.println(ownerID + "------------" + key);
		List<OwnerFileUpdatePojo> li = ownerfile.getFileDetails("", key);
		model.addAttribute("msg", ownerfile.getFileDetails("", key));
		// model.addAttribute("userfileinfo", ownerfile.getFileDetails("", key));
		mv.setViewName("fileDownLoading");

		return mv;

	}

	@RequestMapping(value = "/dow", method = RequestMethod.POST)
	public ModelAndView dow(HttpServletRequest request, HttpServletResponse response, ModelAndView mv, Model model,
			@RequestParam("ecfile") String ecfile, @RequestParam("key") String key,
			@RequestParam("filename") String name) throws Exception {
		System.out.println(ecfile + "" + key);
		EncryptorAesGcmPassword ss = new EncryptorAesGcmPassword();
		BufferedReader br = new BufferedReader(
				new FileReader(environment.getRequiredProperty("locationreport") + name));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while (line != null) {
			sb.append(line).append("\n");
			line = br.readLine();
		}
		String fileAsString = sb.toString();
		String aa = ss.setDcy(ecfile, key);
		// byte[] ENCname = Base64.getEncoder().encode(fileAsString.getBytes());
		// byte[] decodedString = Base64.getDecoder().decode(new
		// String(aa).getBytes("UTF-8"));
		System.out.println(aa);
		// System.out.println(new String(decodedString));
		model.addAttribute("msg", ownerfile.getFileDetails("", key));
		model.addAttribute("dec", aa);
		mv.setViewName("fileDownLoading");

		return mv;

	}

}
