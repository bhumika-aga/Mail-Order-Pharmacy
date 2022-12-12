package com.mailorderpharma.webportal.controller;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mailorderpharma.webportal.entity.AdHocModel;
import com.mailorderpharma.webportal.entity.DateModel;
import com.mailorderpharma.webportal.entity.PrescriptionDetails;
import com.mailorderpharma.webportal.entity.SearchById;
import com.mailorderpharma.webportal.entity.UserData;
import com.mailorderpharma.webportal.exceptions.DrugQuantityNotAvailable;
import com.mailorderpharma.webportal.exceptions.InvalidTokenException;
import com.mailorderpharma.webportal.restclients.DrugClient;
import com.mailorderpharma.webportal.service.PortalService;
import com.sun.el.parser.ParseException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebPortalController {

	private static final Logger log = LoggerFactory.getLogger(WebPortalController.class);

	@Autowired
	private PortalService portalService;

	@Autowired
	DrugClient drugClient;

	/* Login end-points------------------------------------ */

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public ModelAndView getLogin() {
		return new ModelAndView("login");
	}

	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public ModelAndView postLogin() throws FeignException {
		/*
		 * log.info("inn login post" + user.toString());
		 * 
		 * return portalService.postLogin(user, session, warning);
		 */
		return new ModelAndView("login");
	}

	@RequestMapping(path = "/loginpage", method = RequestMethod.GET)
	public ModelAndView postLoginPage(/* @ModelAttribute UserData user, HttpSession session, ModelMap warning */)
			throws FeignException {
		/*
		 * log.info("inn login post" + user.toString());
		 * 
		 * return portalService.postLogin(user, session, warning);
		 */
		return new ModelAndView("anotherlogin");
	}

	@RequestMapping(path = "/anotherlogin", method = RequestMethod.POST)
	public String postLoginPagenew(@ModelAttribute UserData user, HttpSession session, ModelMap warning)
			throws FeignException {

		log.info("inn login post" + user.toString());

		return portalService.postLogin(user, session, warning);

		/* return new ModelAndView("anotherlogin"); */
	}

	@GetMapping("/home")
	public String getWelcome(HttpSession session) throws FeignException {
		return portalService.getWelcome((String) session.getAttribute("token"));
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) throws FeignException {
		session.invalidate();
		return "redirect:/";
	}
	/* DrugService end-points--------------------- */

	@GetMapping("/supportedDrugs")
	public String getSupportedDrugs(HttpSession session, ModelMap warning) {
		if (portalService.isSessionValid(session))
			return postSupportedDrugs(session, warning);
		return "redirect:/";
	}

	@PostMapping(path = "/supportedDrugs")
	public String postSupportedDrugs(HttpSession session, ModelMap warning) throws FeignException {
		log.info("Controller supportedDrugs");
		return portalService.getSupportedDrugs(session, warning);
	}

	/* Subscription end-points--------------------- */

	@GetMapping("/prescriptionform")
	public String getPrescriptionForm(HttpSession session) {
		if (portalService.isSessionValid(session))
			return "prescription";
		return "redirect:/";
	}

	@PostMapping("/subscribe")
	@ResponseBody
	public ModelAndView subscribe(@ModelAttribute PrescriptionDetails prescriptionDetails, HttpSession session)
			throws FeignException {

		log.info("inn subscribe post controller " + prescriptionDetails.toString());
		ModelAndView view = new ModelAndView("prescription");
		view.addObject("msg", portalService.subscribe(prescriptionDetails, session));
		return view;
	}

	@GetMapping("/subscriptions")
	public String getAllSubscriptions(HttpSession session, Model model) throws FeignException {
		log.info("controller getAllSubscriptions");
		return portalService.postSubscriptions(session, model);
	}

	@PostMapping("/unsubscribe/{sId}")
	public String getSubsAfterUnsubscribe(@PathVariable("sId") Long sId, HttpSession session) {
		return portalService.unsubscribe(session, sId);
	}

	@GetMapping("/search")
	public String search() {
		return "search";
	}

	@PostMapping("/searchDrugById")
	public ModelAndView searchDrugById(HttpSession session, @ModelAttribute SearchById searchModel) {
		ModelAndView view = new ModelAndView("search");
		view.addObject("id", searchModel.getId());
		view.addObject("drugdetails", portalService.searchById(session, searchModel));
		return view;
	}

	@PostMapping("/searchDrugByName")
	public ModelAndView searchDrugByName(HttpSession session, @ModelAttribute SearchById searchModel) {
		ModelAndView view = new ModelAndView("search");
		view.addObject("name", searchModel.getName());
		view.addObject("drugdetails", portalService.searchByName(session, searchModel));
		return view;
	}

	/**
	 * Refill End Points
	 */

	@GetMapping("/adhocRefill/{sId}")
	public String adhocRefill(@PathVariable("sId") Long sId, HttpSession session) {
		log.info("in controller get adhocrefill");
		session.setAttribute("sub_Id", sId);
		return "adhocRefill";
	}

	@PostMapping("/postAdhocRefill")
	public ModelAndView postAdHocDetails(@ModelAttribute AdHocModel adHocModel, HttpSession session)
			throws NumberFormatException, FeignException, ParseException, InvalidTokenException,
			DrugQuantityNotAvailable {
		// System.out.println(adHocModel);
		log.info("in postAdHocDetails controller " + adHocModel.getLocation() + adHocModel.isPaymentStatus()
				+ adHocModel.getQuantity());
		ModelAndView view = new ModelAndView("refillstatus");
		return portalService.requestAdhocRefill(session, adHocModel, view);
	}

	@GetMapping("/getAllRefill")
	public ModelAndView getAllRefill(HttpSession session)

	{
		System.out.println("??????????????????????######################################");
		ModelAndView view = new ModelAndView("allrefillstatus");
		return portalService.getAllRefill(session, view);

	}

	@PostMapping("/refillDueAsOfDate")
	public String getRefillDueAsofDate(@ModelAttribute DateModel date, HttpSession session, Model model)
			throws NumberFormatException, InvalidTokenException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

		return portalService.getRefillDueAsofDate(session, formatter.format(date.getDate()), model);
	}

	@GetMapping("/refillDateEntry")
	public ModelAndView refillDateEntry() {
		return new ModelAndView("refillDateEntry");
	}

}
