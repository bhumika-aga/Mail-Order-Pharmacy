package com.mailorderpharma.webportal.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.mailorderpharma.webportal.controller.WebPortalController;
import com.mailorderpharma.webportal.entity.AdHocModel;
import com.mailorderpharma.webportal.entity.AuthResponse;
import com.mailorderpharma.webportal.entity.DrugDetails;
import com.mailorderpharma.webportal.entity.PrescriptionDetails;
import com.mailorderpharma.webportal.entity.RefillOrder;
import com.mailorderpharma.webportal.entity.RefillOrderName;
import com.mailorderpharma.webportal.entity.SearchById;
import com.mailorderpharma.webportal.entity.SubscriptionDetails;
import com.mailorderpharma.webportal.entity.UserData;
import com.mailorderpharma.webportal.restclients.AuthClient;
import com.mailorderpharma.webportal.restclients.DrugClient;
import com.mailorderpharma.webportal.restclients.RefillClient;
import com.mailorderpharma.webportal.restclients.SubscriptionClient;

import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PortalServiceImpl implements PortalService {

	private static final Logger log = LoggerFactory.getLogger(WebPortalController.class);

	@Autowired
	AuthClient authClient;
	@Autowired
	SubscriptionClient subscriptionClient;
	@Autowired
	DrugClient drugClient;

	@Autowired
	RefillClient refillClient;

	@SuppressWarnings("unused")
	private AuthResponse authResponse;

	@Override
	public Boolean isSessionValid(HttpSession session) {
		try {
			authResponse = authClient.getValidity((String) session.getAttribute("token"));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public String postLogin(UserData user, HttpSession session, ModelMap warning) {
		UserData res = null;
		try {
			res = authClient.login(user);
			log.info("inn login post login success");
		} catch (Exception e) {
			String errmsg = "";
			if (e.getClass().toString().contains("feign.RetryableException"))
				errmsg = "Site is Temporarily down. Try again later.";
			else
				errmsg = "Unable to login. please check your credentials.";
			warning.addAttribute("errormsg", errmsg);
			return "login";
		}
		session.setAttribute("token", "Bearer " + res.getAuthToken());
		session.setAttribute("memberId", res.getUserid());
		return getWelcome((String) session.getAttribute("token"));
	}

	@Override
	public String getWelcome(String token) {
		try {
			authResponse = authClient.getValidity(token);
		} catch (Exception e) {
			return "redirect:/";
		}
		return "welcome";

	}

	@Override
	public String getSupportedDrugs(HttpSession session, ModelMap modelMap) {
		try {
			String token = (String) session.getAttribute("token");
			authResponse = authClient.getValidity(token);
		} catch (ExpiredJwtException e) {
			modelMap.addAttribute("Warning", "Please login again");
			return "redirect:/";
		}
		try {
			log.info("2");
			List<DrugDetails> drugList = drugClient.getAllDrugs();
			modelMap.addAttribute("drugList", drugList);
			log.info("4");
		}

		catch (NullPointerException e) {
			modelMap.addAttribute("Warning", "Null Pointer");
			return "redirect:/";
		}
		return "availabledrugs";

	}

	@Override
	public String subscribe(PrescriptionDetails prescriptionDetails, HttpSession session) throws FeignException {
		try {
			authResponse = authClient.getValidity((String) session.getAttribute("token"));
		} catch (Exception e) {
			log.info(e.getClass().toString());
			return "session expired...Please login again";
		}
		prescriptionDetails.setMemberId((String) session.getAttribute("memberId"));
		String msg = "";
		try {
			msg = subscriptionClient.subscribe((String) session.getAttribute("token"), prescriptionDetails);
		} catch (Exception e) {
			System.out.println("@@@@@@@" + e.getMessage());
			log.info("in catch portalservice subscribe method " + e.getMessage() + " " + e.getClass().toString());
			if (e.getClass().toString().contains("UndeclaredThrowableException")) {
				msg = "Stock Not Available.";
			} else if (e.getClass().toString().contains("feign.RetryableException")
					|| e.getClass().toString().contains("UndeclaredThrowableException"))
				msg = "Service is Temporarily down. Try again later.";
			else if (e.getMessage().toString().contains("Content is not available"))
				msg = "Currently we do not support this Medicine.";
		}
		return msg;
	}

	public List<SubscriptionDetails> getSubscriptions(HttpSession session, Model model) {
		List<SubscriptionDetails> subscriptionList = null;
		try {
			subscriptionList = subscriptionClient.getAllSubscription((String) session.getAttribute("token"),
					(String) session.getAttribute("memberId"));

			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println(subscriptionList.toString());

			model.addAttribute("subscriptionList", subscriptionList);
		} catch (Exception e) {
			log.info("hey" + e.getClass().toString());
			if (e.getClass().toString().contains("feign.RetryableException")) {
				model.addAttribute("msg", "Service is Temporarily down. Try again later.");
			} else
				model.addAttribute("msg", "No Subscriptions? Check out our vast range of medicines.");

		}
		return subscriptionList;

	}

	@Override
	public String postSubscriptions(HttpSession session, Model model) {
		try {
			authResponse = authClient.getValidity((String) session.getAttribute("token"));
		} catch (Exception e) {
			log.info("hey" + e.getClass().toString());
			return "redirect:/";
		}
		log.info("token validation success");
		getSubscriptions(session, model);
		return "subscriptions";
	}

	@Override
	public String unsubscribe(HttpSession session, Long sId) {
		try {
			authResponse = authClient.getValidity((String) session.getAttribute("token"));
		} catch (Exception e) {
			log.info("hey" + e.getClass().toString());
			return "redirect:/";
		}
		String result = subscriptionClient.unsubscribe((String) session.getAttribute("token"),
				(String) session.getAttribute("memberId"), sId);

		System.out.println("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@" + result);

		session.setAttribute("rs", result);
		return "redirect:/subscriptions";
	}

	public DrugDetails searchById(HttpSession session, SearchById searchModel) {
		String token = (String) session.getAttribute("token");
		String id = searchModel.getId();
		DrugDetails drugdetails = new DrugDetails();
		String msg = "";
		try {
			drugdetails = drugClient.getDrugById(token, id);
			drugdetails.setMsg("Drug Details");
			return drugdetails;
		} catch (Exception e) {
			log.info("in catch portalservice searchById method " + e.getMessage() + " " + e.getClass().toString());
			if (e.getClass().toString().contains("feign.RetryableException")
					|| e.getClass().toString().contains("UndeclaredThrowableException"))
				msg = "Service is Temporarily down. Try again later.";
			else if (e.getMessage().toString().contains("Content is not available"))
				msg = "Please search drug by Id.";
		}

		drugdetails.setMsg(msg);
		return drugdetails;
	}

	public DrugDetails searchByName(HttpSession session, SearchById searchModel) {
		String token = (String) session.getAttribute("token");
		String name = searchModel.getName();
		DrugDetails drugdetails = new DrugDetails();
		String msg = "";
		try {
			drugdetails = drugClient.getDrugByName(token, name);
			drugdetails.setMsg("Drug Details :");
			return drugdetails;
		} catch (Exception e) {
			log.info("in catch portalservice searchByName method " + e.getMessage() + " " + e.getClass().toString());
			if (e.getClass().toString().contains("feign.RetryableException")
					|| e.getClass().toString().contains("UndeclaredThrowableException"))
				msg = "Service is Temporarily down. Try again later.";
			else if (e.getMessage().toString().contains("Content is not available"))
				msg = "Please search drug by Name.";
		}

		drugdetails.setMsg(msg);
		return drugdetails;
	}

	@Override
	public ModelAndView requestAdhocRefill(HttpSession session, AdHocModel adHocModel, ModelAndView view) {

		try {
			authResponse = authClient.getValidity((String) session.getAttribute("token"));
		} catch (Exception e) {
			log.info(e.getClass().toString());
//			return "redirect:/";
		}
		RefillOrder refillOrder = null;
		try {
			refillOrder = refillClient.requestAdhocRefill((String) session.getAttribute("token"),
					((long) session.getAttribute("sub_Id")), adHocModel.isPaymentStatus(), adHocModel.getQuantity(),
					adHocModel.getLocation(), (String) session.getAttribute("memberId")).getBody();
			log.info("service method requestadhoc after feign" + refillOrder.getPayStatus());
		} catch (Exception e) {
			log.info(e.getClass().toString() + " clas---  msg " + e.getMessage());
			view.addObject("ackmsg", "cannot request refill.Try again later.");
		}
		if (refillOrder == null)
			view.addObject("ackmsg", "Sorry request is not valid.");
		else
			view.addObject("ackmsg", "Order placed successfully");

		view.addObject("msg", refillOrder);
		return view;
	}

	@Override
	public ModelAndView getAllRefill(HttpSession session, ModelAndView view)

	{
		String token = (String) session.getAttribute("token");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$");
		try {
			authResponse = authClient.getValidity(token);
		} catch (Exception e) {
			log.info(e.getClass().toString());

		}
		String ackmsg = "";
		List<RefillOrder> refillOrderList = null;
		try {
			refillOrderList = refillClient.viewRefillStatusByMemberId(token, (String) session.getAttribute("memberId"))
					.getBody();
		} catch (Exception e) {
			if (e.getClass().toString().contains("feign.RetryableException")
					|| e.getClass().toString().contains("UndeclaredThrowableException"))
				ackmsg = "Service is Temporarily down. Try again later.";
			else if (e.getMessage().toString().contains("Content is not available"))
				ackmsg = "No Refills For Now.";
			// TODO Auto-generated catch block
			view.addObject("ackmsg", ackmsg);
		}
		view.addObject("list", refillOrderList);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + refillOrderList);

		return view;
	}

	@Override
	public String getRefillDueAsofDate(HttpSession session, String date, Model model) {

		List<RefillOrder> refillOrderSubscriptions = null;
		String token = (String) session.getAttribute("token");
		try {
			authResponse = authClient.getValidity(token);
		} catch (Exception e) {
			log.info("hey" + e.getClass().toString());
			return "redirect:/";
		}

		try {
			refillOrderSubscriptions = refillClient
					.getRefillDuesAsOfDate(token, (String) session.getAttribute("memberId"), date).getBody();
			log.info("refillOrderSubscriptions " + refillOrderSubscriptions.toString());
		} catch (Exception e) {
			log.info("hey" + e.getClass().toString());

			if (e.getClass().toString().contains("feign.RetryableException")) {
				model.addAttribute("msg", "Service is Temporarily down. Try again later.");
				return "refillDueAsofDate";
			} else if (e.getClass().toString().contains("UndeclaredThrowableException")) {
				model.addAttribute("msg", "No Refill Dues For now!!");
				return "refillDueAsofDate";
			}

			else {
				model.addAttribute("msg", "Something went wrong. Try again later.");
				return "refillDueAsofDate";
			}
		}

		if (refillOrderSubscriptions.isEmpty()) {
			model.addAttribute("msg", "No dues for now!!");
			return "refillDueAsofDate";
		}
		List<RefillOrderName> allDetails = new ArrayList<>();
		for (RefillOrder refillOrder : refillOrderSubscriptions) {
			String drugName = subscriptionClient.getDrugBySubscription(token, refillOrder.getSubId());
			System.out.println("@@@@@@@@######$$$$$$$$$$" + drugName);
			allDetails.add(new RefillOrderName(refillOrder.getId(), drugName, refillOrder.getRefilledDate(),
					refillOrder.getPayStatus(), refillOrder.getSubId(), refillOrder.getQuantity(),
					refillOrder.getMemberId()));

		}

		model.addAttribute("refillResponses", allDetails);

		return "refillDueAsofDate";
	}
}
