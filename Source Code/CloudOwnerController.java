package com.gts.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.Demo.EncryptorAesGcmPassword;
import com.gts.model.Owner;
import com.gts.model.OwnerFileUpdatePojo;
import com.gts.service.OwnerDaoImp;
import com.gts.service.OwnerFileDao;

@Controller
@PropertySource("classpath:path.properties")
public class CloudOwnerController {
	@Autowired
	private Environment environment;

	@Autowired
	private OwnerDaoImp ownerDaoImp;
	@Autowired
	private OwnerFileDao ownerFileDaoImp;

	@RequestMapping(value = "/index")
	public ModelAndView test(HttpServletResponse response) throws IOException {
		return new ModelAndView("index");
	}

//-----------------------------------------------------Register--------------------------------------------------------
	@RequestMapping(value = "register")
	public ModelAndView testa(HttpServletResponse response) throws IOException {
		return new ModelAndView("ownerReg");
	}

	@RequestMapping(value = "save_owner", method = RequestMethod.POST)
	public ModelAndView OwnerRegister(ModelAndView mv, @RequestParam("name") String name,
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("address") String address, @RequestParam("mb") String mobile, HttpSession session) {
		session.setAttribute("sname", name);
		session.setAttribute("semail", email);
		session.setAttribute("mobile", mobile);
		session.setAttribute("spass", password);
		session.setAttribute("saddress", address);

//		String path = session.getServletContext().getRealPath("/");
//		String filaName = file.getOriginalFilename();
//		System.out.println(path + " ::------- image file update----:::" + filaName);
//		session.setAttribute("simage", path);
//		try {
//			byte barr[] = file.getBytes();
//			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path + "/" + email + ".jpg"));
//			Object o = bout;
//			bout.write(barr);
//			bout.flush();
//			bout.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		HashSet<String> ha = new HashSet<String>();
		List<Owner> lis = ownerDaoImp.getOwnerdate();
		for (Owner owner : lis) {
			ha.add(owner.getEmail());
		}
		boolean result = ha.contains(email);
		if (result == true) {
			mv.setViewName("ownerReg");
		} else {

			mv.setViewName("AddBio");
		}

		return mv;

	}

	@RequestMapping(value = "OwnerLogin")
	public ModelAndView login(HttpServletResponse response) throws IOException {
		return new ModelAndView("OwnerLogin");
	}

	@RequestMapping(value = "/loginProcess", method = RequestMethod.POST)
	public ModelAndView loginProcess(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("login") Owner login, HttpSession session) throws IOException {
		ModelAndView mav = null;

		Owner user = ownerDaoImp.validateUser(login);

		if (null != user) {
			mav = new ModelAndView("LoginAddBio");
			mav.addObject("email", user.getOwnerId());
			session.setAttribute("finger", user.getFingerPrint());
			session.setAttribute("sesOwnerName", user.getName());
			session.setAttribute("sesOwnerId", user.getOwnerId());
			session.setAttribute("OwneremailId", user.getEmail());

		} else {
			mav = new ModelAndView("OwnerLogin");
			mav.addObject("msg", "Username or Password is wrong!!");
		}

		return mav;
	}

	@RequestMapping(value = "/OwnerReg", method = RequestMethod.GET)
	public ModelAndView RegisterProcess(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("txttemplate") String fig, HttpSession session) throws IOException {
		Random rand = new Random();
		int rand_int1 = rand.nextInt(1000);
		String status = "pending";
		String ownerId = "owid" + Integer.toString(rand_int1);
		String pid = (String) session.getAttribute("pid");
		String name = (String) session.getAttribute("sname");
		String email = (String) session.getAttribute("semail");
		String phone = (String) session.getAttribute("mobile");
		String password = (String) session.getAttribute("spass");
		String address = (String) session.getAttribute("saddress");
		Owner owner = new Owner();
		owner.setName(name);
		owner.setEmail(email);
		owner.setPhone(phone);
		owner.setPass(password);
		owner.setAddres(address);
		owner.setOwnerId(ownerId);
		owner.setStatus(status);
		owner.setFingerPrint(fig);
		List<Owner> lis = ownerDaoImp.getOwnerdate();
		String msg = "";

		boolean count = ownerDaoImp.saveOwner(owner);
		msg = "Registration Successfull";

		mv.addObject("msg", msg);
		mv.setViewName("OwnerLogin");

		return mv;

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView Logout(ModelAndView mv, HttpServletRequest request, HttpServletResponse servletResponse,
			HttpSession session) throws IOException {
		session = request.getSession(false);
		session.invalidate();
		mv.setViewName("index");
		return mv;

	}

	@RequestMapping(value = "/LoginSucess", method = RequestMethod.POST)
	public ModelAndView loginsucess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			ModelAndView mv) throws IOException {
		String oemail = (String) session.getAttribute("OwneremailId");
		System.out.println("================>>>>>>" + oemail);
		List<Owner> lis = ownerDaoImp.getOwnerdate();
		Map<String, String> map = new HashMap<String, String>();

		for (Owner owner : lis) {
			map.put(owner.getEmail(), owner.getStatus());
		}
		String status = map.get(oemail);
		if (status.equalsIgnoreCase("pending")) {
			System.out.println("Contain Value is Match");
			mv.addObject("msg", "Process Is Pending Pleace Wait");
			mv = new ModelAndView("OwnerLogin");
		} else {
			System.out.println("Contain Value is not  Match");
			mv = new ModelAndView("ownerupdatefile");
		}
		return mv;

	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ModelAndView uploadFileHandler(@RequestParam("fname") String name, @RequestParam("tb") String privatekey,
			@RequestParam("file") MultipartFile file, ModelAndView mv, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String msg = null;

		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				// String rootPath = System.getProperty("catalina.home");
				// File dir = new File(rootPath + File.separator + "tmpFiles");
				File dir = new File(environment.getRequiredProperty("locationpath"));
				if (!dir.exists())
					dir.mkdirs();
				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + name + ".txt");
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				System.out.println(name);

				File file1 = new File(environment.getRequiredProperty("locationpath") + name + ".txt"); // creates a new
																										// file
				// instance
				FileReader fr = new FileReader(file1); // reads the file
				BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
				StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line); // appends line to string buffer
					sb.append("\n"); // line feed
				}
				fr.close(); // closes the stream and release the resources
				System.out.println("Contents of File: ");
				System.out.println(sb.toString()); // returns a string that textually represents the object

				String fileAsString = sb.toString();

				EncryptorAesGcmPassword ss = new EncryptorAesGcmPassword();
				String aa = ss.setEcy(fileAsString, privatekey);
				// byte[] ENCname = Base64.getEncoder().encode(fileAsString.getBytes());
				// byte[] decodedString = Base64.getDecoder().decode(new
				// String(name).getBytes("UTF-8"));
				System.out.println(aa);
				// System.out.println(new String(decodedString));
//---------------------ECFILE write----------
				FileWriter fw = new FileWriter(environment.getRequiredProperty("locationreport") + name);
				fw.write(aa);
				fw.close();
				request.setAttribute("file", aa);
				request.setAttribute("filename", name);
				request.setAttribute("privatekey", privatekey);
				// request.setAttribute("msg", msg);
				msg = "File upload Sucess";
				mv.setViewName("ownerupdatefile");
				return mv;
			} catch (Exception e) {
				msg = "You failed to upload " + name + " => " + e.getMessage();
				request.setAttribute("msg", msg);
				mv.setViewName("ownerupdatefile");
				request.setAttribute("filename", name);
				return mv;
			}
		} else {
			msg = "You failed to upload " + name + " because the file was empty";
			request.setAttribute("msg", msg);
			request.setAttribute("filename", name);
			mv.setViewName("ownerupdatefile");
			return mv;
		}
	}

	@RequestMapping(value = "/deleteowner", method = RequestMethod.GET)
	public ModelAndView UserDalete(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam("customerId") String customerId) throws IOException {
		int result = ownerDaoImp.onDelete(customerId);
		model.addAttribute("ownerlist", ownerDaoImp.getOwnerdate());
		mv.setViewName("ShowInfo");
		return mv;
	}

	@RequestMapping(value = "/ownerfileupdate", method = RequestMethod.POST)
	public ModelAndView OwnerFileUpdate(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam("fileOwnerId") String ownerid,
			@RequestParam("fileOwnerName") String fileOwnerName, @RequestParam("filename") String filename,
			@RequestParam("eyfile") String eyfile, @RequestParam("pry") String pry) throws IOException {

		System.out.println(ownerid + "" + fileOwnerName + "" + filename + "------'''''" + pry);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now).toString();
		OwnerFileUpdatePojo ofdi = new OwnerFileUpdatePojo();
		ofdi.setDate_time(date);
		ofdi.setFileName(filename);
		ofdi.setFileOwnerName(fileOwnerName);
		ofdi.setPrivateKey(pry);
		ofdi.setFileOwnerId(ownerid);
		ofdi.setStatus("Pending");
		ofdi.setSessionKey(eyfile);
		ofdi.setSt("upload");
		ofdi.setAz("no");

		boolean result = ownerFileDaoImp.saveOwner(ofdi);
		request.setAttribute("msg", "Uplode File In Cloud");
		mv.setViewName("ownerupdatefile");
		return mv;

	}

	@RequestMapping(value = "/OwnerShowUpdateFile", method = RequestMethod.GET)
	public ModelAndView OwnerShowUpdateFile(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			Model model, HttpSession session) throws IOException {
		String ownerId = (String) session.getAttribute("sesOwnerId");
		System.out.println("-------------->>>>>>" + ownerId);
		// List<OwnerFileUpdatePojo> list = ownerFileDaoImp.getFileDetails();
		model.addAttribute("userfileinfo", ownerFileDaoImp.getFileDetails());
		model.addAttribute("userfilerequest", ownerFileDaoImp.getRqFiles());
		request.setAttribute("msg", "Suecss");
		mv.setViewName("ownerviewRq");
		return mv;
	}

	@PostMapping("/KeySharing")
	public ModelAndView KeySharing(ModelAndView mv, HttpServletRequest request, HttpServletResponse response,
			Model model, HttpSession session, @RequestParam("Id") String id, @RequestParam("key") String key)
			throws IOException {
		System.out.println(id + "=-------------------->>>" + key);
		ownerDaoImp.onkeyShareing(key, id);
		model.addAttribute("userfileinfo", ownerFileDaoImp.getFileDetails());
		model.addAttribute("userfilerequest", ownerFileDaoImp.getRqFiles());
		request.setAttribute("msg", "Suecss");
		mv.setViewName("ownerviewRq");
		return mv;
	}

}
