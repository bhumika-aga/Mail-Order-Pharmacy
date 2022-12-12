package com.mailorderpharma.webportal.service;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.mailorderpharma.webportal.entity.AdHocModel;
import com.mailorderpharma.webportal.entity.DrugDetails;
import com.mailorderpharma.webportal.entity.PrescriptionDetails;
import com.mailorderpharma.webportal.entity.SearchById;
import com.mailorderpharma.webportal.entity.UserData;

public interface PortalService {

	Boolean isSessionValid(HttpSession session);

	String postLogin(UserData user, HttpSession session, ModelMap warning);

	String getWelcome(String attribute);

	String subscribe(PrescriptionDetails prescriptionDetails, HttpSession session);

	String unsubscribe(HttpSession session, Long sId);

	String getSupportedDrugs(HttpSession session, ModelMap modelMap);

	String postSubscriptions(HttpSession session, Model model);

	public DrugDetails searchById(HttpSession session, SearchById searchModel);

	public DrugDetails searchByName(HttpSession session, SearchById searchModel);

	ModelAndView requestAdhocRefill(HttpSession session, AdHocModel adHocModel, ModelAndView view);

	public ModelAndView getAllRefill(HttpSession session, ModelAndView view);

	String getRefillDueAsofDate(HttpSession session, String date, Model model);
}
