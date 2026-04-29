package com.elegantjbi.controller.graph;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.elegantjbi.dao.CurrentTenantIdentifierResolverImpl;
import com.elegantjbi.entity.admin.UserInfo;
import com.elegantjbi.util.logger.ApplicationLog;

public class GraphObjectThread extends Thread {
	
	GraphController graphController;
	String type;
	int width;
	int height;
	boolean isReadFromCache;
	String objectId;
	String dashboardId;
	String sectionId;
	UserInfo user;
	HttpSession session;
	HttpServletRequest request;
	boolean init = false;
	boolean isDefaultHomePage = false, isFromRestore = false, isSaveAnalysis=false, isFromDashboard=false;
	boolean isFromLinkDashboardObject = false;
	String tokenId;
	String filterKey;
	Authentication auth = null;
	String tenantId;
	
	
	
	public GraphObjectThread(GraphController graphController, int width, int height, String objectId, UserInfo user, HttpServletRequest request, boolean init, boolean isDefaultHomePage, boolean isFromRestore, boolean isSaveAnalysis, boolean isFromDashboard, String tokenId,boolean isFromLinkDashboardObject,String filterKey) {
		this.tenantId = CurrentTenantIdentifierResolverImpl.getCurrentTenantIdentifier();
		this.graphController=graphController;
		this.width=width;
		this.height=height;
		this.objectId=objectId;
		this.user=user;
		this.request = request;
		this.session=this.request.getSession();
		
		this.isDefaultHomePage = isDefaultHomePage;
		this.isFromRestore = isFromRestore;
		this.isSaveAnalysis = isSaveAnalysis;
		this.isFromDashboard = isFromDashboard;
		this.isFromLinkDashboardObject = isFromLinkDashboardObject;
		
		
		this.init = init;
		
		this.tokenId=tokenId;
		this.filterKey = filterKey;
		auth = SecurityContextHolder.getContext().getAuthentication();
	}
	
	
	
	@Override
	public void run() {
		CurrentTenantIdentifierResolverImpl.setTenantIds(tenantId);
		SecurityContextHolder.getContext().setAuthentication(auth);
			if (init){
				initializeObjects();
			} else {
				refreshObjects();
			}

		
		
	
	}
	public GraphController getGraphController() {
		return graphController;
	}
	public void setGraphController(GraphController graphController) {
		this.graphController = graphController;
	}
	
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	private void initializeObjects(){
		

		ModelAndView mAndv = null;
		ModelMap mm = new ModelMap();
		Date d = new Date();
		
			mAndv =graphController.prepareGraph(objectId, width, height, "", isDefaultHomePage, isFromRestore, isSaveAnalysis, isFromDashboard, mm, user, session,isFromLinkDashboardObject,filterKey,request);	
			
			mAndv.getModelMap().addAllAttributes(mm);
			
			graphController.ObjectInitializationMap.put(tokenId, mAndv);
			
//			WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, type+"-"+sectionId);				
			
		
		ApplicationLog.debug("DashboardObjectThread.initializeObjects() The "+type + " :: " + objectId+ " For Section "+sectionId+" Prepared in " + (new Date().getTime()-d.getTime()) + " ms");
		
	}
	
	
	private void refreshObjects(){

		ApplicationLog.debug("DashboardObjectThread.refreshObjects() The "+type + " :: " + objectId+ " For Section "+sectionId+" Prepared ");
/*		ModelAndView mAndv = null;
		ModelMap mm = new ModelMap();
		Date d = new Date();
		if (type.equalsIgnoreCase(AppConstants.CROSSTAB_TITLE)){
			mAndv = dashboardController.refreshAnalysis(objectId, null, sectionId, user, mm);
		} else if (type.equalsIgnoreCase(AppConstants.TABULAR_TITLE)){
			mAndv = dashboardController.refreshReport(objectId, null, sectionId, user, mm, true);
		} else if (type.equalsIgnoreCase(AppConstants.GRAPH_TITLE)){
			mAndv = dashboardController.refreshGraph(objectId, null, sectionId, user, mm);
		} else if (type.equalsIgnoreCase(AppConstants.MAP_TITLE)){
			mAndv = dashboardController.refreshMap(objectId, null, sectionId, user, mm);
		} else if (type.equalsIgnoreCase(AppConstants.KPI_TITLE)){
			mAndv= dashboardController.refreshKPI(objectId, null, sectionId, user, mm);
		} else if (type.equalsIgnoreCase(AppConstants.KPI_GROUP_TITLE)){ 
			mAndv= dashboardController.refreshKPIGroup(objectId, null, sectionId, user, mm);
		} else if (type.equalsIgnoreCase(DashboardConstants.TIMESERIES_FILTER_NAME)){
			mAndv = dashboardController.getTimeSeriesData("", type+"-"+sectionId, sectionId, user, mm);
		} else if (type.equalsIgnoreCase(DashboardConstants.MULTI_COLUMN_FILTER_NAME)){
			mAndv = dashboardController.getMultiColumnData("", type+"-"+sectionId, sectionId, mm, user);
		} else if (type.equalsIgnoreCase(DashboardConstants.SINGLE_COLUMN_FILTER_NAME)){
			mAndv = dashboardController.getSingleColumnData("", type+"-"+sectionId, sectionId, 0, 50, "", mm, user);
		} else if (type.equalsIgnoreCase(DashboardConstants.RANK)){
			mAndv = dashboardController.rankProperties(sectionId, type+"-"+sectionId, mm);
		} else if (type.equalsIgnoreCase(DashboardConstants.CURRENT_SELECTION_SECTION)){
			mAndv = dashboardController.getCurrentSelectionData(mm, sectionId, type+"-"+sectionId, user);
		}
		
		
		mAndv.getModelMap().addAllAttributes(mm);
		String token = objectId+sectionId+System.currentTimeMillis();
		dashboardController.ObjectInitializationMap2.put(token, mAndv);
		
		WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, type+"-"+sectionId);				
		
		System.out.println("DashboardObjectThread.refreshObjects() The "+type + " :: " + objectId+ " For Section "+sectionId+" Prepared in " + (new Date().getTime()-d.getTime()) + " ms");
		//setContent(mAndv, mm, type, sectionId);
		/*try {
			View resolvedView = internalResourceViewResolver.resolveViewName(mAndv.getViewName(), localeResolver.resolveLocale(req));
			MockHttpServletResponse mockResp = new MockHttpServletResponse();
			resolvedView.render(mm, req, mockResp);
		//	System.out.println("rendered html : " + mockResp.getContentAsString());
			WebSocketUtil.sendMessage(req.getSession().getId(), dashboardInfo.getDashboardId(), "SET_CONTENT", mockResp.getContentAsString(), type+"-"+objId);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
	
/*	
	private void prepareAnnalysisObject(String objId, String type, UserInfo userInfo, HttpSession session, boolean isReadFromCache, String sectionId, String dashboardId ) {
		
				Date d = new Date();
				ModelMap mm = new ModelMap();
				ModelAndView mAndv = dashboardController.showAnalysis(objId, "", sectionId, mm,isReadFromCache , userInfo, session);
				mAndv.getModelMap().addAllAttributes(mm);
				String token = objId+System.currentTimeMillis();
				dashboardController.ObjectInitializationMap2.put(token, mAndv);
		//		setContent(mAndv,mm,type,sectionId);
				WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, type+"-"+sectionId);				
				System.out.println("DashboardController.prepareAnnalysisObject Object Sent :: " + objId + " :: " + sectionId + " ::  TIME :: " + (new Date().getTime() - d.getTime()));
	}
	private void prepareGraphObject(String objId, String type, UserInfo userInfo, HttpSession session, boolean isReadFromCache, String sectionId, int sectionWidth, int sectionHeight, String dashboardId ){
		
				Date d = new Date();

				ModelMap mm = new ModelMap();
				ModelAndView mAndv = (ModelAndView) dashboardController.showGraph(objId, "", sectionId, sectionWidth, sectionHeight, isReadFromCache, mm, userInfo, session);
				mAndv.getModelMap().addAllAttributes(mm);
				String token = objId+System.currentTimeMillis();
				dashboardController.ObjectInitializationMap2.put(token, mAndv);
				
//				setContent(mAndv, mm, type, sectionId);

				WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, type+"-"+sectionId);
				
				System.out.println("DashboardController.prepareGraphObject Object Sent :: " + objId + " :: " + sectionId + " ::  TIME :: " + (new Date().getTime() - d.getTime()));
	}
	private void prepareKPIObject(String objId, String type, UserInfo userInfo, HttpSession session, boolean isReadFromCache, String sectionId, int sectionWidth, int sectionHeight, String dashboardId ){
		
				ModelMap mm = new ModelMap();			
				Date d = new Date();

				ModelAndView mAndv = dashboardController.showKPI(objId, "", sectionWidth, sectionHeight, sectionId, isReadFromCache, mm, userInfo, session);
				mAndv.getModelMap().addAllAttributes(mm);
				String token = objId+System.currentTimeMillis();
				dashboardController.ObjectInitializationMap2.put(token, mAndv);
				WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, type+"-"+sectionId);
				System.out.println("DashboardController.prepareKPIObject Object Sent :: " + objId + " :: " + sectionId + " ::  TIME :: " + (new Date().getTime() - d.getTime()));
		
	}
	
	private void prepareKPIGroupObject(String objId, String type, UserInfo userInfo, HttpSession session, boolean isReadFromCache, String sectionId, int sectionWidth, int sectionHeight, String dashboardId ){
		
				Date d = new Date();
				ModelMap mm = new ModelMap();
				ModelAndView mAndv = dashboardController.showKPIGroup(objId, "", sectionWidth, sectionHeight, sectionId, isReadFromCache, mm, userInfo, session);
				mAndv.getModelMap().addAllAttributes(mm);
				String token = objId+System.currentTimeMillis();
				dashboardController.ObjectInitializationMap2.put(token, mAndv);
				WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, type+"-"+sectionId);
				System.out.println("DashboardController.prepareKPIGroupObject Object Sent :: " + objId + " :: " + sectionId + " ::  TIME :: " + (new Date().getTime() - d.getTime()));
		
	}
	private void prepareMultiColumnFilter(String objId, String type, UserInfo userInfo, HttpSession session, boolean isReadFromCache, String sectionId, int sectionWidth, int sectionHeight, String dashboardId ){
		
				ModelMap mm = new ModelMap();
				Date d = new Date();

//				divId=${type}-${id}&sectionId=${id}
				ModelAndView mAndv = dashboardController.getMultiColumnData(null, type+"-"+sectionId, sectionId, mm, userInfo);
				mAndv.getModelMap().addAllAttributes(mm);
				String token = objId+System.currentTimeMillis();
				dashboardController.ObjectInitializationMap2.put(token, mAndv);
				WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, type+"-"+sectionId);
				System.out.println("DashboardController.prepareMultiColumnFilter Object Sent :: " + objId + " :: " + sectionId + " ::  TIME :: " + (new Date().getTime() - d.getTime()));
		
	}
	
	private void prepareTimeSeriesFilter(String objId, String type, UserInfo userInfo, HttpSession session, boolean isReadFromCache, String sectionId, int sectionWidth, int sectionHeight, String dashboardId ){
	
				Date d = new Date();

				ModelMap mm = new ModelMap();
//				divId=${type}-${id}&sectionId=${id}
				ModelAndView mAndv = dashboardController.getTimeSeriesData("", type+"-"+sectionId, sectionId, userInfo,mm);
				mAndv.getModelMap().addAllAttributes(mm);
				String token = objId+System.currentTimeMillis();
				dashboardController.ObjectInitializationMap2.put(token, mAndv);
				WebSocketUtil.sendMessage(session.getId(), dashboardId, "REFRESH", token, objId+"-"+sectionId);
	}
	
	
	private void setContent(ModelAndView mAndv, ModelMap mm, String type, String sectionId){
		
		try {
			View resolvedView = viewResolver.resolveViewName(mAndv.getViewName(), localeResolver.resolveLocale(request));
			MockHttpServletResponse mockResp = new MockHttpServletResponse();
//			MockHttpServletRequest mockReq = new MockHttpServletRequest(request.getServletContext(), request.getMethod(), request.getRequestURL().toString());
			resolvedView.render(mm, request, mockResp);
//			System.out.println("DashboardObjectThread.setContent()" +type+"-"+sectionId + " ::: " +  mockResp.getContentAsString());
			WebSocketUtil.sendMessage(session.getId(), dashboardId, "SET_CONTENT", mockResp.getContentAsString(), type+"-"+sectionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
}
