/*
 * @(#)GraphController.java Version 4.0 <Jan 12, 2014>
 *
 * Copyright 2015 Elegant MicroWeb Technologies Pvt. Ltd. (India). All Rights Reserved. Use is subject to license terms.
 */
package com.elegantjbi.controller.graph;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Row;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.elegantjbi.AppContext;
import com.elegantjbi.AppMainCommandList;
import com.elegantjbi.amcharts.CandleStickGraph;
import com.elegantjbi.amcharts.vo.Graphs;
import com.elegantjbi.controller.ObjectController;
import com.elegantjbi.core.mdx.MDXException;
import com.elegantjbi.core.olap.CubeColumnExpExecutor;
import com.elegantjbi.core.olap.CubeColumnInfo;
import com.elegantjbi.core.olap.CubeConditionInfo;
import com.elegantjbi.core.olap.CubeDataExpExecutor;
import com.elegantjbi.core.olap.CubeException;
import com.elegantjbi.core.olap.CubeLabelExpExecutor;
import com.elegantjbi.core.olap.CubeLabelInfo;
import com.elegantjbi.core.olap.CubeRankDataLabel;
import com.elegantjbi.core.olap.CubeVector;
import com.elegantjbi.core.olap.CubeViewInfo;
import com.elegantjbi.core.olap.ICubeConstants;
import com.elegantjbi.core.olap.ICubeResultSet;
import com.elegantjbi.core.olap.ICubeResultSetMetaData;
import com.elegantjbi.core.olap.ICubeResultSetSupport;
import com.elegantjbi.core.realtime.RealTimeCubeException;
import com.elegantjbi.dao.CurrentTenantIdentifierResolverImpl;
import com.elegantjbi.entity.FolderInfo;
import com.elegantjbi.entity.IDataObject;
import com.elegantjbi.entity.IEntity;
import com.elegantjbi.entity.WhatIfConfigurationInfo;
import com.elegantjbi.entity.admin.GeneralConfigurationInfo;
import com.elegantjbi.entity.admin.GroupInfo;
import com.elegantjbi.entity.admin.PDFPageSetupInfo;
import com.elegantjbi.entity.admin.RProfileInfo;
import com.elegantjbi.entity.admin.UserInfo;
import com.elegantjbi.entity.analysis.AnalysisInfo;
import com.elegantjbi.entity.customreport.CustomReportInfo;
import com.elegantjbi.entity.dashboard.DashboardInfo;
import com.elegantjbi.entity.graph.GraphInfo;
import com.elegantjbi.entity.smarten.SmartenInfo;
import com.elegantjbi.entity.tooltemplate.ActiveFilterInfo;
import com.elegantjbi.entity.tooltemplate.ActiveGlobalVariableInfo;
import com.elegantjbi.entity.tooltemplate.ActiveUDDCInfo;
import com.elegantjbi.entity.tooltemplate.ActiveUDHCInfo;
import com.elegantjbi.entity.tooltemplate.UddcTemplateInfo;
import com.elegantjbi.exception.CubeAccessException;
import com.elegantjbi.exception.CubeNotFoundException;
import com.elegantjbi.exception.DatabaseOperationException;
import com.elegantjbi.exception.ObjectAccessException;
import com.elegantjbi.exception.ObjectNotFoundException;
import com.elegantjbi.exception.ServiceException;
import com.elegantjbi.security.APICustomAuthenticationProvider;
import com.elegantjbi.security.utils.CustomWebAuthenticationDetails;
import com.elegantjbi.service.ObjectService;
import com.elegantjbi.service.admin.IApplicationConfigurationService;
import com.elegantjbi.service.admin.UserManagementServiceUtil;
import com.elegantjbi.service.analysis.AnalysisService;
import com.elegantjbi.service.analysis.parts.ALSCommandNameList;
import com.elegantjbi.service.analysis.parts.ALSException;
import com.elegantjbi.service.analysis.parts.LMRecentInfo;
import com.elegantjbi.service.graph.GraphConstants;
import com.elegantjbi.service.graph.GraphService;
import com.elegantjbi.service.graph.HierarchyTree;
import com.elegantjbi.service.kpi.KPIConstants;
import com.elegantjbi.service.kpi.KPIException;
import com.elegantjbi.service.repository.RepositoryService;
import com.elegantjbi.service.util.AccessRightServiceUtil;
import com.elegantjbi.service.util.RecentlyUsedServiceUtil;
import com.elegantjbi.spark.sql.BIDataset;
import com.elegantjbi.util.AppConstants;
import com.elegantjbi.util.AppContextUtil;
import com.elegantjbi.util.CalendarUtil;
import com.elegantjbi.util.CubeUtil;
import com.elegantjbi.util.DefaultConfUtil;
import com.elegantjbi.util.ExportServiceUtil;
import com.elegantjbi.util.GeneralFiltersUtil;
import com.elegantjbi.util.GeneralUtil;
import com.elegantjbi.util.HashtableEx;
import com.elegantjbi.util.LoggedInUser;
import com.elegantjbi.util.Pagination;
import com.elegantjbi.util.RScriptException;
import com.elegantjbi.util.ResourceManager;
import com.elegantjbi.util.StringUtil;
import com.elegantjbi.util.TemplateUtil;
import com.elegantjbi.util.logger.ApplicationLog;
import com.elegantjbi.vo.ApiTokenVo;
import com.elegantjbi.vo.OutlinerBean;
import com.elegantjbi.vo.PageFilterNew;
import com.elegantjbi.vo.Pair;
import com.elegantjbi.vo.ParamItem;
import com.elegantjbi.vo.RScriptInputOutputVO;
import com.elegantjbi.vo.SelectItem;
import com.elegantjbi.vo.analysis.Analysis;
import com.elegantjbi.vo.graph.Group;
import com.elegantjbi.vo.properties.ActiveTemplateProperties;
import com.elegantjbi.vo.properties.graph.GraphLineSettingProperties;
import com.elegantjbi.vo.properties.graph.GraphProperties;
import com.elegantjbi.vo.properties.graph.ReferenceLine;
import com.elegantjbi.vo.properties.kpi.TrendDataValueProperties;
import com.elegantjbi.vo.properties.kpi.TrendLineProperties;
import com.elegantjbi.vo.properties.kpi.YaxisTrendProperties;
import com.elegantjbi.vo.repository.Repository;
/**
 * The controller for the graph. 
 * @author Nikhil
 *
 */
@Controller
@Scope("session")
@RequestMapping("/graph")
public class GraphController extends ObjectController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String objectTypeName = ResourceManager.getString("LBL_GRAPH_LINK");

	private GraphInfo graphInfo;
	
	private GraphService graphService;
	
	private String keyFilter = null;

	/*@Autowired
	private UploadImageServiceUtil uploadImageServiceUtil;*/

	@Autowired
	private RecentlyUsedServiceUtil recentlyUsedServiceUtil;
	@Autowired
	private AccessRightServiceUtil accessRightServiceUtil;
	
	@Autowired
	private UserManagementServiceUtil userManagementServiceUtil;
	
	@Autowired
	private RepositoryService repositoryService;

	Map<String,ModelAndView> ObjectInitializationMap = new HashMap<String,ModelAndView>();
	Map<String,Thread> ObjectInitializationThreadMap = new HashMap<String,Thread>();
	
	
	
	@RequestMapping(value="/new")
	public ModelAndView createNewGraph(ModelMap map, @LoggedInUser UserInfo userInfo){
		graphInfo = new GraphInfo();
		
		String graphId = String.valueOf(System.currentTimeMillis());
		graphService =(GraphService) AppContext.getApplicationContext().getBean("graphService");
		getServiceMap().put(graphId, graphService);
		
		
		getDetailInfoMap().put(graphId, graphInfo);
		graphInfo.setNewGraphId(graphId);
		
		graphService.setLoggedInUserId(userInfo.getUserId());
		graphService.setIsFromAnalysis(false);
		map.addAttribute("Mode", AppConstants.NEW_MODE);
		try {
			int permission =  accessRightServiceUtil.getGraphPermission(userInfo, null);
			int operation = AppConstants.WRITE_RIGHTS_DB;
			if(!((operation & permission) == operation)){
				map.put("errorMessage", ResourceManager.getString("ERROR_NO_ACCESS_PERMISSION"));
				return new ModelAndView("permissionErrorPage");
			}
		} catch (DatabaseOperationException e1) {
			map.put("errorMessage", e1.getLocalizedMessage());
			return new ModelAndView("permissionErrorPage");
		} catch (ServiceException e1) {
			map.put("errorMessage", e1.getLocalizedMessage());
			return new ModelAndView("permissionErrorPage");
		}
		
		graphInfo.setGraphName(ResourceManager.getString("GRAPH_NEW"));
		graphInfo.setCreatedBy(userInfo);
		try {
			graphInfo.setPdfPageSetup(pageSetupServiceUtil.getPDFPageSetupInfo());
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_SET_PDFPAGESETUP",
					new Object[] { userInfo.getUsername() }), e);
		}
		graphInfo.setGraphMode(AppConstants.NEW_MODE);
		if (graphInfo.getGraphType() ==  GraphConstants.NUMERIC_DIAL_GAUGE) {
			map.put("isGaugeGraph", true);
		} else {
			map.put("isGaugeGraph", false);	
		}
		map.addAttribute("Mode", AppConstants.NEW_MODE);
		map.addAttribute("objectType", AppConstants.GRAPH);
		map.put("graphProperties", graphInfo.getGraphProperties());
		String strDateFormat = CalendarUtil.getDataDisplayFormat(userInfo, Types.TIMESTAMP);
		map.addAttribute("strDateFormat",strDateFormat);
		map.addAttribute("graphInfo",graphInfo);
		map.addAttribute("showToolbarForNew", false);	
		auditUserActionLog(ResourceManager.getString("LBL_CREATE_GRAPH"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph");
	}
	
	@RequestMapping(value = "/{strObjectId:.+}")
	public ModelAndView showGraph(
			@PathVariable String strObjectId,
			/*@RequestParam(value = "objectId", required = false) String strObjectId,*/
			@RequestParam(value = "screenWidth", required = false, defaultValue = "0") int windowScreenWidth,
			@RequestParam(value = "screenHeight", required = false, defaultValue = "0") int windowScreenHeight,
			@RequestParam(value = "objFolderId", required = false) String strObjFolderId,
			@RequestParam(value = "isDefaultHomePage", required = false,defaultValue="false") boolean isDefaultHomePage,
			@RequestParam(value = "isFromRestore", required = false,defaultValue="false") boolean isFromRestore,
			@RequestParam(value = "isFromMobile", required = false,defaultValue="false") boolean isFromMobile,
			@RequestParam(value = "isSaveAnalysis", required = false,defaultValue="false") boolean isSaveAnalysis,
			@RequestParam(value="isFromDashboard" ,required=false, defaultValue="false") boolean isFromDashboard,ModelMap map,
			@RequestParam(value ="isFromLinkDashboardObject", required = false,defaultValue="false") boolean isFromLinkDashboardObject,
			@LoggedInUser UserInfo userInfo, HttpSession session, HttpServletRequest request,
			@RequestParam(value="tokenId", required=false ) String tokenId,
			@RequestParam(value="filterKey", required=false ) String filterKey,
			@RequestParam(value="exportToken", required=false ) String exportToken) {
		
		map.addAttribute("showToolbarForNew", true);
		boolean mobReq = request != null && request.getHeader("User-Agent") != null && request.getHeader("User-Agent").contains("Mobile");
		map.put("isMobile", mobReq);
	/*	//amcharts plotting start
		List<Map<String, Object>> dpList =  new ArrayList<Map<String,Object>>();
		String[] jsonArr =  graphService.generateGraph(graphInfo);
		map.put("jsonData",jsonArr[0]);		
		map.put("chartSize", jsonArr[1]);
		//amcharts plotting end
*/
		
		if (tokenId != null && !tokenId.isEmpty() && ObjectInitializationThreadMap.get(tokenId) != null){
			return loadGraphObject(tokenId);
		}
		long start = System.currentTimeMillis();
		if (strObjectId != null && !strObjectId.trim().equals("")){
			DashboardInfo dashboardInfo = null;
			isFromDefaultHomePage = isDefaultHomePage;
			if(isFromDashboard){
				if(graphInfo != null){
					dashboardInfo =  graphInfo.getDashboardInfo();
				}
			}
			try {
				GraphInfo gInfo = null;
				if(isExportCall(exportToken)) {
					graphService = (GraphService) getExportRelatedService(exportToken, strObjectId, graphService);
					gInfo = (GraphInfo) getExportRelatedEntityInfo(exportToken, strObjectId, gInfo);
					isSaveAnalysis = true;	
				}
				if (graphService == null){
					graphService = (GraphService) AppContext.getApplicationContext().getBean("graphService");
					getServiceMap().put(strObjectId, graphService);
				}
				if(!isFromLinkDashboardObject){
					graphService.setLinkedObjectFilterValMap(new HashMap<>());
				}
				if(!isSaveAnalysis) {
					gInfo = graphService.getGraphById(strObjectId);
				}else {
					gInfo=getGraphObjectFromMap(strObjectId);
					graphInfo=gInfo;
				}
				gInfo.setGraphMode(AppConstants.OPEN_MODE);
				if (isFromDashboard){
					gInfo.setDashboardInfo(dashboardInfo);
					gInfo.setFromDashBoardLink(true);
				}
				/*if(isFromLinkDashboardObject && graphService.getLinkedObjectActiveGlobalVariableList() != null && !graphService.getLinkedObjectActiveGlobalVariableList().isEmpty() && gInfo.getActiveTemplateProperties() != null) {
					gInfo.getActiveTemplateProperties().setActiveGlobalVariableInfo(graphService.getLinkedObjectActiveGlobalVariableList());
				}*/
				detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),gInfo.getGraphName(),gInfo.getGraphId(),"Open Graph","Initialize Graph",Thread.currentThread(),userInfo,null);
				map.put("graphInfo", gInfo);
				map.put("isSetAsHome",userInfo.getHomePage());
				/*map.put("isDataValueOn", graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible());*/
				if(getDetailInfoMap() != null)
					getDetailInfoMap().put(strObjectId,gInfo);
				boolean isROutLinerdispaly = false;
				if(!isFromDashboard){
					if (gInfo.getCubeInfo() != null && gInfo.getCubeInfo().getDataObjectType() == ICubeConstants.REALTIME_R_CUBE) {
						isROutLinerdispaly = true;
					}
				}
				
				map.put("isROutLinerdispaly", isROutLinerdispaly);
				setStrParentHierchy(graphService.getFolderDisplayPathFromFolderInfo(gInfo.getFolderInfo(), userInfo.getUserId()));
			} catch (DatabaseOperationException e) {
				ApplicationLog.error(e);
			}
			
		}
		request.getSession().setAttribute("isFromMobile", isFromMobile);
		String token = strObjectId+System.currentTimeMillis();
		GraphObjectThread got = new GraphObjectThread(this, windowScreenWidth, windowScreenHeight, strObjectId, userInfo, request, true, isDefaultHomePage, isFromRestore, isSaveAnalysis, isFromDashboard,token,isFromLinkDashboardObject,filterKey);
//		ModelAndView mAndV = prepareGraph(strObjectId, windowScreenWidth, windowScreenHeight, strObjFolderId, isDefaultHomePage, isFromRestore, isSaveAnalysis, isFromDashboard, map, userInfo, session);
		auditUserActionLog(ResourceManager.getString("LBL_OPEN_OBJECT"), AppConstants.USER_ACCESS,userInfo);
		request.getSession().setAttribute("teamUP_"+strObjectId, getStrParentHierchy());
//		return mAndV;
		ApplicationLog.debug(" Show graph Time === >> "+(System.currentTimeMillis()-start));
		ObjectInitializationThreadMap.put(token, got);
		map.put("objectTokenId", token);		
		got.start();
		
		return new ModelAndView("graphLoading");
	}
	
	
	@RequestMapping(value = "/loadObject")
	public ModelAndView loadGraphObject(@RequestParam String tokenId){
		Thread thread = ObjectInitializationThreadMap.get(tokenId);
		try {
			thread.join();
		} catch (InterruptedException e) {
			ApplicationLog.error(e);
			Thread.currentThread().interrupt();//Added for SonarQube [24/04/2020]
		}
		return ObjectInitializationMap.get(tokenId);
	}
	
	public ModelAndView prepareGraph(String strObjectId, int windowScreenWidth, int windowScreenHeight, String strObjFolderId, boolean isDefaultHomePage, boolean isFromRestore, boolean isSaveAnalysis, boolean isFromDashboard, ModelMap map, UserInfo userInfo, HttpSession session,boolean isFromLinkDashboardObject,String filterKey, HttpServletRequest request){
		DashboardInfo dashboardInfo = null;
		GeneralConfigurationInfo generalConfigurationInfo =  generalConfigurationServiceUtil.getGeneralConfigurationInfo();
		GroupInfo groupInfo	= null;
		if(userInfo.getGroupInfo() != null){
			groupInfo=userManagementServiceUtil.getGroupInfoById(userInfo.getGroupInfo().getGroupId());
		}
		boolean isTrue = false;
		if(graphInfo != null && isFromDashboard) {
			dashboardInfo=graphInfo.getDashboardInfo();
		}
		if (filterKey != null) {
			keyFilter = filterKey;
		}
		GraphService graphServiceObj;
		GraphInfo graphObjInfo;
		if(!isSaveAnalysis) {
			graphServiceObj = (GraphService) AppContext.getApplicationContext().getBean("graphService");
			if (getServiceMap().get(strObjectId) != null) {
				graphServiceObj = (GraphService) getServiceMap().get(strObjectId);
				if (graphServiceObj.getLinkedObjectFilterValMap() != null
						&& graphServiceObj.getLinkedObjectFilterValMap().size() > 0) {
					graphServiceObj.setLinkedObjectFilterValMap(graphServiceObj.getLinkedObjectFilterValMap());
				}
			}
			if (getDetailInfoMap().get(strObjectId) != null) {
				graphObjInfo = (GraphInfo) getDetailInfoMap().get(strObjectId);
			} else {
				getServiceMap().put(strObjectId, graphServiceObj);
				graphObjInfo = new GraphInfo();
				getDetailInfoMap().put(strObjectId, graphObjInfo);
			}
		} else {
			graphServiceObj = (GraphService) getServiceMap().get(strObjectId);
			graphObjInfo = (GraphInfo) getDetailInfoMap().get(strObjectId);
			setSaveasProcess(false);
		}
		getServiceMap().put(strObjectId, graphServiceObj);
		this.graphService = graphServiceObj;
		this.graphInfo = graphObjInfo;
		isFromDefaultHomePage = isDefaultHomePage;
		if(graphObjInfo != null && isFromDashboard && dashboardInfo == null){
			dashboardInfo =  graphObjInfo.getDashboardInfo();
		}
		//for bug 12668
		if(graphObjInfo != null && graphServiceObj.isDrillUpPossible(graphObjInfo))
		{
			isSaveAnalysis = false;
			graphServiceObj.checkAndResetDrillUp(graphObjInfo, true);
		}
		//for bug 12668
		if(windowScreenWidth <= 0) {
			windowScreenWidth = userInfo.getDeviceWidth();
		}
		if(windowScreenHeight <= 0) {
			windowScreenHeight = userInfo.getDeviceHeight();
		}
	//	graphService.setLoggedInUserId(userInfo.getUserId());
		graphServiceObj.setLoggedInUserId(userInfo.getUserId());
		graphServiceObj.setIsFromAnalysis(false);
		Map<String, String> requiredItemsParams = null;
		if (strObjectId != null && strObjectId.trim().length() > 0) {
			graphServiceObj.setObjectMode(AppConstants.OPEN_MODE);
		} else {
			strObjectId = "";
		}
		
		if (graphServiceObj.getObjectMode() == AppConstants.OPEN_MODE) {
			Hashtable<String, Object> params = new Hashtable<String, Object>();
			if (generalConfigurationInfo.isShowToolbar() || userInfo.isShowToolbar() || (groupInfo != null && groupInfo.isShowToolbar())) {
				isTrue = true;
			}
			params.put("objectId", strObjectId);
			params.put("windowScreenWidth", windowScreenWidth);
			params.put("windowScreenHeight", windowScreenHeight);
			map.put("isDefaultHomePage", isDefaultHomePage);
			map.put("showToolbars", isTrue);
			map.put("isSetAsHome",userInfo.getHomePage());
			try {
				if(!isSaveAnalysis) {
					long start = System.currentTimeMillis();
					graphObjInfo = graphServiceObj.initializeGraph(graphServiceObj.getObjectMode(), params, userInfo, null,true,graphObjInfo);
					ApplicationLog.debug("initializeGraph  == >> "+(System.currentTimeMillis()-start));
					getDetailInfoMap().put(strObjectId, graphObjInfo);
					if(graphObjInfo.getDataColumns().size() <= 0) {
						map.put("isGraphVisible", false);
						map.put("errorMessage", "");
					}
				}
				
				
				 graphObjInfo = getGraphObjectFromMap(strObjectId);
				 graphServiceObj = getGraphServiceFromMap(strObjectId);
				
				if (graphObjInfo.getGraphProperties().getTitleProperties().isTitleVisible()) {
					HashtableEx ddvmList = graphServiceObj.getActiveDDVMs(graphObjInfo, userInfo.getUserId(), false);

					try {
						graphServiceObj.setObjectPageTitle(graphObjInfo.getGraphId(),graphObjInfo
								.getActiveFilterInfo(userInfo.getUserId()),
								graphServiceObj.getPageFilterNew(graphObjInfo),
								graphServiceObj.getActiveVariableMap(), graphServiceObj.getResultSetMetaData(),
								(IDataObject)graphObjInfo.getCubeInfo(), graphObjInfo.getGraphProperties().getTitleProperties(),
								userInfo, ddvmList);
					} catch (CubeException e) {
						ApplicationLog.error(e);
					}
				}
				String isFromDashBoardLink = ""+isFromLinkDashboardObject;//(String) session.getAttribute("isFromDashBoardLink");
				session.removeAttribute("isFromDashBoardLink");
				long start = System.currentTimeMillis();
				boolean isFromUseCase = false;
				List<String> list = DefaultConfUtil.getExploreObjectList();
				if(list != null && !list.isEmpty()) {
					if(list.contains(strObjectId)){
						isFromUseCase = true;
					}
				}
				map.put("isFromUseCase", isFromUseCase);
				if(!isFromUseCase && (!isDefaultHomePage && null == dashboardInfo && !("true").equals(isFromDashBoardLink) && !userInfo.isAnonymousUser())){
					recentlyUsedServiceUtil.saveRecentlyUsed(strObjectId, graphObjInfo.getGraphName(), userInfo, AppConstants.GRAPH,0);
				}
				ApplicationLog.debug(" saveRecentlyUsed  ==  >>  "+(System.currentTimeMillis()-start));
				setStrParentHierchy(graphServiceObj.getFolderDisplayPathFromFolderInfo(graphObjInfo.getFolderInfo(), userInfo.getUserId()));
				/*HashMap conditionMap = (HashMap) session.getAttribute("filtersessionCondMap");*/
				HashMap conditionMap = (HashMap) graphServiceObj.getLinkedObjectFilterValMap();
				boolean isRetrievalOnload =	graphServiceObj.retrievalonloadcheck(graphObjInfo,userInfo);
				if(isRetrievalOnload && !isFromRestore && !isDefaultHomePage && !isSaveAnalysis && (isFromDashBoardLink == null || !("true").equals(isFromDashBoardLink))) {
				WhatIfConfigurationInfo whatIfConfigurationInfo = graphObjInfo.getWhatIfConfigurationInfo();
				if(whatIfConfigurationInfo != null) {
					if(whatIfConfigurationInfo.getShowRetrievalOnLoad() != null && whatIfConfigurationInfo.getShowRetrievalOnLoad().equalsIgnoreCase("true")) {
						map.put("isforRetrievalOnLoad", "true");
						graphObjInfo.setGraphMode(graphServiceObj.getObjectMode());
						graphObjInfo.setReadFromCache(false);
						if (graphObjInfo.getGraphType() ==  GraphConstants.NUMERIC_DIAL_GAUGE) {
							map.put("isGaugeGraph", true);
						} else {
							map.put("isGaugeGraph", false);	
						}
						map.addAttribute("Mode", graphServiceObj.getObjectMode());
						map.addAttribute("objectType", AppConstants.GRAPH);
						map.put("graphProperties", graphObjInfo.getGraphProperties());
						String strDateFormat = CalendarUtil.getDataDisplayFormat(userInfo, Types.TIMESTAMP);
						map.addAttribute("strDateFormat",strDateFormat);
						if(graphObjInfo != null){
							graphObjInfo.setDashboardInfo(dashboardInfo);
						}
						showAppliedFilter(map, userInfo, strObjectId);
						map.addAttribute("graphInfo",getGraphObjectFromMap(strObjectId));
						this.graphService = graphServiceObj;
						this.graphInfo = graphObjInfo;
						return new ModelAndView("graph");
					}
				}
				}
				detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Open Graph","Initialize Graph",Thread.currentThread(),userInfo,null);
				String strObjectKey =  strObjectId+AppConstants.API_KEY_SEPERATOR+keyFilter;
				//CustomWebAuthenticationDetails customAuthenticationDetails = APICustomAuthenticationProvider.getObjectIdCustomWebDetailMap().get(strObjectKey);
				CustomWebAuthenticationDetails customAuthenticationDetails = null;
				String tenant = CurrentTenantIdentifierResolverImpl.getCurrentTenantIdentifier();
				
				List<ApiTokenVo> tokensByTenant = APICustomAuthenticationProvider.getTokensByTenantMap().get(tenant);
				if (tokensByTenant != null && !tokensByTenant.isEmpty()) {
					ApiTokenVo token = tokensByTenant.stream().filter(t-> t != null && t.getObjectKey().equals(strObjectKey)).findFirst().orElse(null);
		            if(token != null){
		            	customAuthenticationDetails = token.getCustomWebDetail();   	
		            }
				}
		        if(customAuthenticationDetails != null){
		            Map<String, List<String>> filterMap = customAuthenticationDetails.getFilterMap();
		            if(filterMap != null && filterMap.size() > 0) {
		            	graphObjInfo.setApiFilterMap(filterMap);
		            	graphObjInfo.setReadFromCache(false);
		            	request.setAttribute("objectId", strObjectId);
		            	Map<String,String> iteams = new HashMap<>();
						List<ActiveGlobalVariableInfo> acGv= graphObjInfo.getActiveGlobalVariableInfo(userInfo.getUsername());
						for(ActiveGlobalVariableInfo acGlob : acGv) {
							iteams.put(acGlob.getGlobalVariableInfo().getGlobalVariableName().replace("$", ""), acGlob.getGlobalVariableInfo().getTypeString());
						}
	                	request.setAttribute("objectId", strObjectId);
		            	String gv = GeneralFiltersUtil.getGvConditionString(filterMap,graphObjInfo.getCubeInfo().getId(),iteams);
						if(gv != null && !gv.equals("")) {
							setGlobalVariable(gv, userInfo, false,request);
						}
		            }
		        }
	            
				if(isFromDashBoardLink != null && ("true").equals(isFromDashBoardLink)) {
					/*conditionMap = (HashMap) session.getAttribute("filtersessionCondMap");*/
					if(graphServiceObj.getLinkedObjectActiveGlobalVariableList() != null && !graphServiceObj.getLinkedObjectActiveGlobalVariableList().isEmpty() && graphObjInfo.getActiveTemplateProperties() != null) {
						graphObjInfo.getActiveTemplateProperties().setActiveGlobalVariableInfo(graphServiceObj.getLinkedObjectActiveGlobalVariableList());
						graphServiceObj.setActiveGlobalVariableMapForLinkedObject(graphServiceObj.getLinkedObjectActiveGlobalVariableList());
					}
					HashMap objectConditionMap;
					conditionMap = (HashMap) graphServiceObj.getLinkedObjectFilterValMap();
					Map<String,String> dashboardFilterInfoMap = (HashMap) session.getAttribute("dashboardFilterInfoMap");
					session.removeAttribute("dashboardFilterInfoMap");
					if(conditionMap != null && !conditionMap.isEmpty()) {
						objectConditionMap=((GraphService)getServiceMap().get(strObjectId)).getFilterConditions();
						if(objectConditionMap!=null && !objectConditionMap.isEmpty()) {
							conditionMap.forEach((k, v) -> objectConditionMap.put(k,v));
							conditionMap = objectConditionMap;
						}
						CubeVector testCube = (CubeVector) conditionMap.get("PrelodingParameterList");
						conditionMap.remove("PrelodingParameterList");
						if(testCube != null && testCube.size() > 0) {
							graphServiceObj.setFilterConditionsFromDashboard(conditionMap, graphObjInfo);
							graphServiceObj.setDBPreloadingParameterList(testCube);
						} else {
							graphServiceObj.setFilterConditionsFromDashboard(conditionMap, graphObjInfo);
						}
						graphObjInfo.setReadFromCache(false);
						/*session.removeAttribute("filtersessionCondMap");*/
						
					}
					graphObjInfo.setDashboardFilterInfoMap(dashboardFilterInfoMap);
					dashboardInfo = (DashboardInfo) session.getAttribute("dashboardObj");
					session.removeAttribute("dashboardObj");
					graphObjInfo.setDashboardInfo(dashboardInfo);
					graphObjInfo.setFromDashBoardLink(true);
					}
				start = System.currentTimeMillis();				
				boolean isOnLoadInfo = false;
				if(!graphObjInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo) && !isDefaultHomePage && !("true").equals(isFromDashBoardLink) && graphObjInfo.getDashboardInfo() == null){
				/*Map<String,String> strTmp = graphServiceObj.readActiveTemplateProperties(graphObjInfo.getGraphId());
				
				if(strTmp != null && !strTmp.isEmpty()) {
					isOnLoadInfo = true;
					graphObjInfo.setOnLoadObjectInfo(strTmp);
				} else {
					graphObjInfo.setOnLoadObjectInfo(null);
				}*/
					if(graphObjInfo.getOnLoadObjectInfo() != null && !graphObjInfo.getOnLoadObjectInfo().isEmpty()) {
						if(!graphObjInfo.getOnLoadObjectInfo().get(0).isEmpty() || !graphObjInfo.getOnLoadObjectInfo().get(1).isEmpty()) {
							isOnLoadInfo = true;
						}
					}	
				}
				//ApplicationLog.debug("readActiveTemplateProperties == >> "+(System.currentTimeMillis()-start));
				map.put("isOnLoadInfo", isOnLoadInfo);
				
			} catch (DatabaseOperationException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(), graphServiceObj.getObjectMode() }), e);
			} catch (MDXException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				return new ModelAndView("permissionErrorPage");
			} catch (RealTimeCubeException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				return new ModelAndView("permissionErrorPage");
			} catch (CubeException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(), graphServiceObj.getObjectMode() }), e);
			} catch (IOException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(), graphServiceObj.getObjectMode() }), e);
			} catch (NotBoundException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(),graphServiceObj.getObjectMode() }), e);
			} catch (ObjectAccessException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				return new ModelAndView("permissionErrorPage");
			} catch (ObjectNotFoundException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				return new ModelAndView("permissionErrorPage");
			} catch (CubeNotFoundException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				return new ModelAndView("permissionErrorPage");
			} catch (CubeAccessException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				return new ModelAndView("permissionErrorPage");
			} catch (RScriptException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				return new ModelAndView("permissionErrorPage");
			}catch (ALSException e) {
				map.addAttribute("Mode", graphServiceObj.getObjectMode());
				map.addAttribute("objectType", AppConstants.GRAPH);
				map.put("errorMessage", e.getLocalizedMessage());
				map.put("isGraphVisible", false);
				graphObjInfo.setErrorMessage(e.getLocalizedMessage());
				return new ModelAndView("permissionErrorPage");
			}finally {
				detailedMonitorEndpoint.removeActiveRequest(Thread.currentThread().getId());
			}  
			map.put("issaveGraph", isSaveAnalysis);
			if(isSaveAnalysis)//Bug #12389
			{
				graphObjInfo.getGraphData().setFromAnalysis(false);
			}
			map.put("strObjectId", strObjectId);
			map.put("isFromMobile", session.getAttribute("isFromMobile"));
			map.put("isDataValueOn", graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible());
			refreshObjectData(request, null, userInfo, map);
			
			long currentTimestamp = System.currentTimeMillis();
			requiredItemsParams = new HashMap<String, String>();
			requiredItemsParams.put("sCmd", AppMainCommandList.NEW_GRAPH.getM_strCommandName());
			requiredItemsParams.put("firstTime" , "true");
			requiredItemsParams.put("currentTimeStamp", currentTimestamp + "");

			generateRequiredItemsForGraph(map, requiredItemsParams);
			map.put("currentTimeStamp", currentTimestamp);
		} /*else {
			graphObjInfo.setGraphName(ResourceManager.getString("GRAPH_NEW"));
			graphObjInfo.setCreatedBy(userInfo);
			try {
				graphObjInfo.setPdfPageSetup(pageSetupServiceUtil.getPDFPageSetupInfo());
			} catch (DatabaseOperationException e) {
				ApplicationLog.error(ResourceManager.getString(
						"LOG_ERROR_MSG_FAILED_TO_SET_PDFPAGESETUP",
						new Object[] { userInfo.getUsername() }), e);
			}
		}*/

		graphObjInfo.setGraphMode(graphServiceObj.getObjectMode());
		if (graphObjInfo.getGraphType() ==  GraphConstants.NUMERIC_DIAL_GAUGE) {
			map.put("isGaugeGraph", true);
		} else {
			map.put("isGaugeGraph", false);	
		}
		map.addAttribute("Mode", graphServiceObj.getObjectMode());
		map.addAttribute("objectType", AppConstants.GRAPH);
		map.put("graphProperties", graphObjInfo.getGraphProperties());
		String strDateFormat = CalendarUtil.getDataDisplayFormat(userInfo, Types.TIMESTAMP);
		map.addAttribute("strDateFormat",strDateFormat);
		//For Back to dashboard breadcrumb
		if(graphObjInfo != null){
			graphObjInfo.setDashboardInfo(dashboardInfo);
		}
		if(isFromLinkDashboardObject) {
			graphObjInfo.setFromDashBoardLink(true);
			}
		showAppliedFilter(map, userInfo, strObjectId);// Check whether filter is applied or not.
		map.addAttribute("graphInfo",getGraphObjectFromMap(strObjectId));
		this.graphService = graphServiceObj;
		this.graphInfo = graphObjInfo;
		return new ModelAndView("graph");
	}
	
	/**
	 * This method is use to Show outliner Dialog
	 * @param map
	 * @return model Map
	 */
	@RequestMapping (value = "/outliner")
	public ModelAndView showOutliner(
			@RequestParam(value = "selectedCubeId", required = false) String strCubeId,
			@RequestParam(value = "selectedGraphType", required = false) Integer graphType,
			ModelMap map, @LoggedInUser UserInfo userInfo) {

		try {
			IDataObject cubeInfo = graphInfo.getCubeInfo();
			if(cubeInfo.getDataObjectType() == ICubeConstants.REALTIME_R_CUBE) {
				map.put("isRRealTime",true);
			}
			Vector<String> vector = new Vector<String>();
			if (strCubeId == null) {
				//cubeId = graphInfo.getCubeInfo().getCubeId();
				//strCubeId = graphInfo.getCubeInfo().getCubeId();
				List<ActiveGlobalVariableInfo> activeGolbalVariableList = graphInfo.getActiveTemplateProperties().getActiveGlobalVariableInfo(userInfo.getUserId());
				if(activeGolbalVariableList != null && activeGolbalVariableList.size()>0){
					for (ActiveGlobalVariableInfo activeGlobalVariableInfo : activeGolbalVariableList) {
						vector.add(activeGlobalVariableInfo.getGlobalVariableInfo().getGlobalVariableName());
					}
				}	
			}
			HashMap<String, Vector<String>> dimensionMap = metadataServiceUtil
					.getColumnsAndMeasuresMap(cubeInfo, userInfo,
							vector,true,true,false,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
			List<String> dimensionList = new ArrayList<String>();
			dimensionList.addAll(dimensionMap.get("1"));
			dimensionList.addAll(dimensionMap.get("2"));
			//dimensionList.addAll(dimensionMap.get("3")); Resolved bug 14341
			//modified by harsh on 4 dec
			//dimensionList.addAll(dimensionMap.get("4"));
			dimensionList.addAll(dimensionMap.get("5"));
			dimensionList.addAll(dimensionMap.get("6"));

			List<String> measureList = metadataServiceUtil.getMeasureList(cubeInfo, userInfo,false,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
			
			if (strCubeId != null) {
				graphInfo.getRowColumns().clear();;
				graphInfo.getColColumns().clear();;
				graphInfo.getDataColumns().clear();;
			}
			
			OutlinerBean outlinerBean = new OutlinerBean();
			outlinerBean.setPtreeEnable(true);
			try {
				outlinerBean = graphService.getOutlinerData(outlinerBean, graphInfo, userInfo, dimensionList, cubeInfo, false);
			} catch (CubeException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_OPEN_OUTLINER"), e);
			} catch( DatabaseOperationException e){
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_OPEN_OUTLINER"), e);
			} catch (Exception e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_OPEN_OUTLINER"), e);
			}
			map.put("dimensionList", dimensionList);			
			map.put("measureList", measureList);
			map.put("outlinerBean", outlinerBean);

		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_OPEN_OUTLINER"), e);
		} catch (DatabaseOperationException ex) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_OPEN_OUTLINER"), ex);
		}

		if (graphType != null) {
			graphInfo.setGraphType(graphType);
		}
		map.put("objectType", AppConstants.GRAPH);
		map.put("selectedGraphType", graphInfo.getGraphType());
		Vector<String> dateDementionVector = null;
		try {
			dateDementionVector = cubeMetadataServiceUtil.getCubeDateColumn(graphInfo.getCubeInfo(), userInfo, false);
		} catch (Exception e1) {
			ApplicationLog.error(e1);
		}
		Map<String,Integer> dateDemention = new HashMap();
		for (String object : dateDementionVector) {
			dateDemention.put(object,CubeUtil.getColumnType(object, graphInfo.getCubeInfo()));
		}
		map.put("dateDemention", dateDemention);
		String value = StringUtils.join(graphInfo.getDateFrequencyMap().entrySet().stream().map(entry -> entry.getKey() + "||" + entry.getValue()).collect(Collectors.toList()), ',');
		map.put("dateDementionList", value);
		map.put("dateFrequencyMap",graphInfo.getDateFrequencyMap());

		if(strCubeId != null) {
			map.put("Mode", AppConstants.NEW_MODE);		
			map.put("selectedCubeId", strCubeId);
			graphService.setObjectMode(AppConstants.NEW_MODE);
			return new ModelAndView("graph/outlinerNew");	
		} else {
			map.put("Mode", AppConstants.OPEN_MODE);	
			map.put("selectedCubeId", strCubeId);
			graphService.setObjectMode(AppConstants.OPEN_MODE);
			return new ModelAndView("graph/outlinerNew");
		}
	}
	
	/**
	 * Show New Graph Type wizard
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/graphTypeSelecttionWizard")
	@ResponseBody
	public ModelAndView showGraphTypeWizard(
			@RequestParam(value = "selectedCubeId", required = false) String strCubeId,
			@RequestParam(value = "screenWidth", required = false, defaultValue = "0") int windowScreenWidth,
			@RequestParam(value = "screenHeight", required = false, defaultValue = "0") int windowScreenHeight,
			@LoggedInUser UserInfo loggedInUser, HttpSession session,
			ModelMap map) {

		if(strCubeId != null) {
			session.removeAttribute("cubeList");
			Hashtable<String, Object> params = new Hashtable<String, Object>();

			params.put("objectPath", "");
			params.put("cubeId", strCubeId);
			params.put("windowScreenWidth", windowScreenWidth);
			params.put("windowScreenHeight", windowScreenHeight);
			try {
				graphInfo = graphService.initializeGraph(AppConstants.NEW_MODE, params, loggedInUser, false);
				getDetailInfoMap().put(graphInfo.getGraphId(), graphInfo);
				//modified by harsh on 16 dec
				GeneralConfigurationInfo generalConfiguration =	generalConfigurationServiceUtil.getGeneralConfigurationInfo();
				
				if(generalConfiguration.getEditByCreator()!= null && generalConfiguration.getEditByCreator().intValue() == 0)
				{
					graphInfo.getGraphProperties().setEditByCreator(true);
				}
				else
				{
					graphInfo.getGraphProperties().setEditByCreator(false);
				}
			
		
				graphService.setFirstTime(true);
				graphService.setGraphTypeWizard(true);
			} catch (DatabaseOperationException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (CubeException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (IOException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (NotBoundException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (ObjectAccessException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (ObjectNotFoundException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (CubeNotFoundException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (CubeAccessException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			} catch (RScriptException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			}catch (ALSException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { loggedInUser.getUsername(), graphService.getObjectMode() }), e);
			}  

		} else{
			strCubeId = getGraphInfo().getCubeInfo().getDataObjectId();
		}
		map.put("selectedCubeId", strCubeId);
		return new ModelAndView("graph/graphTypeWizard");
	}
	
	/**
	 * Show save as dialog
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/showsaveasgraph")
	@ResponseBody
	public ModelAndView showSaveAsDialog(@LoggedInUser UserInfo userInfo, ModelMap map){
		
		boolean flag = userInfo.isSuperAdmin();

		Map<String, Object> treeNodeMap = repositoryServiceUtil.getTreeObjectMap(userInfo, AppConstants.PROJECTS_DIR, flag, true);

		FolderInfo currentFolderInfo = new FolderInfo();
		currentFolderInfo.setParentFolderId("folders");
		currentFolderInfo.setParentFolderHierarchy("folders");

		map.put("currentFolderInfo", currentFolderInfo);
		map.put("treeNodeMap", treeNodeMap);
		map.put("graphInfo", graphInfo);
		
		if (graphInfo.getGraphMode() == AppConstants.NEW_MODE)
			return new ModelAndView("graphSaveDialog");
		else
			return new ModelAndView("graphsaveas");
	}
	
	/**
	 * Show export dialog
	 */
	@RequestMapping(value="/showexportgraph")
	@ResponseBody
	public ModelAndView showExportDialog(ModelMap map,@LoggedInUser UserInfo userInfo,@RequestParam(value = "objectid", required = false) String strObjectId){
		ApplicationLog.debug("in showexportgraph");
		map.put("objectid",strObjectId);
		return new ModelAndView("exportgraph");
	}
	
	/**
	 * show graph Drill down browsing Dialog 
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/showdrilldownbrowsing")
	@ResponseBody
	public ModelAndView showDrillDownBrowsingDialog(@RequestParam("cTime") long lTime, @LoggedInUser UserInfo userInfo, ModelMap map){

		HierarchyTree graphHierachyTree = null;
		boolean bColLabelFirst = false;
		boolean bCombineGraph = false;
		HashtableEx ddvmValuesMap = null;
		try {
			ddvmValuesMap = graphService.getActiveDDVMs(graphInfo, userInfo.getUserId(), false);
			graphHierachyTree = graphService.getGraphHierarchyTree(graphInfo,ddvmValuesMap);
			Vector  theRowColLabels = graphInfo.getRowColumns();
			Vector  theRowColLabels2 = graphInfo.getLineGraphRowLabelsForCombinedGraph();
			Vector  theColColLabels =graphInfo.getColColumns();
			if( graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH )
			{
				bCombineGraph = true;
				if( theRowColLabels.size() <= 1 && theRowColLabels2.size() <= 1 )
					bColLabelFirst = true;
			}
			else
			{
				/*if( theRowColLabels.size() <= 1 || graphInfo.getGraphPanel().getMultipleYAxisLabelsEnable())
					bColLabelFirst = true;*/
				if(theRowColLabels.size() <=1 && theColColLabels.size() > 0){
					bColLabelFirst = true;
				}
			}
			
			graphService.setPackDDVMInfo(graphInfo, ddvmValuesMap);
		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_OPEN_DRILLDOWNBROWSING_DIALOG",
					new Object[] { userInfo.getUsername(), getObjectDisplayName() }), e);
		}
		map.put("graphHierachyTree", graphHierachyTree);
		map.put("isColFirst", bColLabelFirst);
		map.put("time", lTime);
		map.put("rowPath", graphInfo.getDrilldownRowPath());
		map.put("colPath", graphInfo.getDrilldownColumnPath());
		map.put("bCombineGraph", bCombineGraph);
		map.put("ddvmValuesMap", ddvmValuesMap);
		return new ModelAndView("drilldownvrowsing");
	}
	
	@RequestMapping(value="/applydrilldownbrowsing")
	@ResponseBody
	public Object performDrillDownBrowsing(@RequestParam("rowPath") String strRowPath, @RequestParam("colPath") String strColPath,
			HttpServletResponse response, ModelMap map, @LoggedInUser UserInfo userInfo) {

		try {
			detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Drill down browsing","set Drill down browsing",Thread.currentThread(),userInfo,null);
			graphInfo.setFromDrillDownBrowsing(true);
			graphService.setGraphDrillDownBrowsing(graphInfo, strRowPath, strColPath, userInfo);
		} catch(Exception ex) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_APPLY_DRILLDOWNBROWSING",
					new Object[] { userInfo.getUsername(), getObjectDisplayName() }), ex);
			
			return ResourceManager.getString("ERROR_MSG_FAILED_APPLY_DRILLDOWNBROWSING", new Object[] {ex.getMessage()});
		}
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_DRILLDOWN_BROWSING"), AppConstants.DETAIL,userInfo);
		response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),null,"Create Object view",Thread.currentThread(),userInfo,new Date());
		return refreshObjectData(null,response, userInfo, map);
	}
	/**
	 * show Data operation Dialog 
	 * @param map
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/showdataoperation")
	@ResponseBody
	public ModelAndView showDataOperatinoDialog(ModelMap map, @LoggedInUser UserInfo userInfo){
		
		Vector dateDimensions = null;
		Vector dimension = null;
		try {
			dateDimensions = metadataServiceUtil.getDateTimension(graphInfo.getCubeInfo(),
					userInfo,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
			dimension = metadataServiceUtil.getDimensionColumns(graphInfo.getCubeInfo(), userInfo,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
		if(dateDimensions != null && dateDimensions.size() > 0) {
		 dataMap = StringUtil.getDataOperationMap(true, isMDXCube()); 
		} else {
		 dataMap = StringUtil.getDataOperationMap(false, isMDXCube());	
		}
		Map<String,String> messureMap = new HashMap<String, String>();
		Map<String,String> postaggregationMap = new HashMap<String, String>();
		Map<String,Integer> yAxisComputationType = new HashMap<String, Integer>();
		Map<String,String> timeDimensionMap = new HashMap<String, String>();
		Map<String,String> agregationMap = new HashMap<String, String>();
		yAxisComputationType = graphInfo.getyAxisComputationType();
		Map<String,String> distinctCountMap = new HashMap<String, String>();
		Vector<String> dataLabels = (Vector<String>) graphInfo.getDataColumns().clone();
		Map cmbDataOps = graphInfo.getCombinedDataOperationMap();
		
		if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
			if(graphInfo.getLineGraphDataLabelsForCombinedGraph() != null && graphInfo.getLineGraphDataLabelsForCombinedGraph().size() > 0){
				dataLabels.addAll(graphInfo.getLineGraphDataLabelsForCombinedGraph());
			}	
		}
		
		List<ActiveUDDCInfo> actUddcInfo = graphInfo.getActiveUDDCInfo(userInfo.getUserId());
		if(actUddcInfo != null && actUddcInfo.size() > 0) {
			for (ActiveUDDCInfo activeUDDCInfo : actUddcInfo) {
				dataLabels.add(activeUDDCInfo.getUddcTemplateInfo().getColumnName());
			}
		}
		if (dataLabels != null && dataLabels.size() > 0) {
			for (String dataLabel : dataLabels) {
				String intTotal = "0";
				String postTotal = "0";

				if(yAxisComputationType != null && yAxisComputationType.size() > 0){
					if(yAxisComputationType.containsKey(dataLabel)) {    
						intTotal = ""+yAxisComputationType.get(dataLabel);
					}
				}
				postTotal = intTotal;
				LMRecentInfo lmRecentInfo = null;
				if(graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo() != null) {
					lmRecentInfo = (LMRecentInfo) graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo().get(dataLabel);
				}
				if (lmRecentInfo == null) {
					lmRecentInfo = new LMRecentInfo();
				} else {
					if(lmRecentInfo.getOperationType() == ICubeConstants.LEASTRECENT) {
						intTotal = Integer.toString(ICubeConstants.LEAST_RECENT);
					} else {
						intTotal = Integer.toString(ICubeConstants.MOST_RECENT);
					}
					postTotal = StringUtil.getDataOperationCMDMap().get(Integer.parseInt(postTotal));
				}
				
				String distinctCountColumn = null;
				if(graphInfo.getObjectCubeDefinition().getDistinctCountMap() != null) {
					distinctCountColumn = (String) graphInfo.getObjectCubeDefinition().getDistinctCountMap().get(dataLabel);
				}
				if(distinctCountColumn == null) {
					distinctCountColumn = "";
				} else {
					intTotal = Integer.toString(ICubeConstants.DISTINCT);
				}
				
				//BUG:13762
				if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
				{
					if(cmbDataOps != null && cmbDataOps.size() > 0 && cmbDataOps.get(dataLabel) != null)
						intTotal =cmbDataOps.get(dataLabel).toString();
				}
				//BUG:13762
				
				messureMap.put(dataLabel, intTotal);
				postaggregationMap.put(dataLabel, postTotal);
				String timeDimension = lmRecentInfo.getTimeDimension();
				int agregation = lmRecentInfo.getAggregationType();
				timeDimensionMap.put(dataLabel, timeDimension);
				agregationMap.put(dataLabel, ""+agregation);
				distinctCountMap.put(dataLabel, distinctCountColumn);
			}
		}
		Map<String, Map<String,Object>> measureDataMap = graphService.getSelectedDataOperation(graphInfo, messureMap, 
				(dateDimensions != null && dateDimensions.size() > 0),false);
		Map<String, Map<String,Object>> measureAggDataMap = graphService.getSelectedDataOperation(graphInfo, messureMap, 
				(dateDimensions != null && dateDimensions.size() > 0),true);
		map.put("datedimension", dateDimensions);
		map.put("timeDimensionMap", timeDimensionMap);
		map.put("agregationMap", agregationMap);
		map.put("postaggregationMap", postaggregationMap);
		map.put("dataMap", dataMap);
		map.put("messureMap", messureMap);
		map.put("dimension", dimension);
		map.put("distinctCountMap", distinctCountMap);
		map.put("distinctOprMap", graphInfo.getObjectCubeDefinition().getDistinctCountOperationMap());
		map.put("selectedoperation", measureDataMap);
		map.put("selectedpostoperation", measureAggDataMap);
		return new ModelAndView("dataoperationgraph");
	}

	@RequestMapping(value = "/lmrecentdataoperation")
	@ResponseBody
	public ModelAndView lmrecentdataoperation(
			ModelMap modelMap,
			@RequestParam(value = "columnName", required = false) String strColumnName,
			@RequestParam(value = "operationtype", required = false) String strOperationtype, @LoggedInUser UserInfo userInfo) {
		
		String strPostAgg = ALSCommandNameList.CMD_NAME_QT_TOTAL;
		String strOpertionType = "";
		if(strOperationtype.equals(ResourceManager.getString("LBL_LEAST_RECENT"))) {
			strOpertionType = ""+ICubeConstants.LEASTRECENT;
		} else {
			strOpertionType = ""+ICubeConstants.MOSTRECENT;
		}
		
		Vector dateDimensions = null;
		try {
			dateDimensions = metadataServiceUtil.getDateTimension(graphInfo.getCubeInfo(),
					userInfo,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		
		LMRecentInfo lmRecentInfo = (LMRecentInfo) graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo().get(strColumnName);
		if (lmRecentInfo == null) {
			lmRecentInfo = new LMRecentInfo();
		}
		Map<String,Integer> yAxisComputationType = new HashMap<String, Integer>();
		yAxisComputationType = graphInfo.getyAxisComputationType();
		
		/*Vector<String> dataLabels = graphInfo.getDataColumns();*/
		
			int intTotal = 0;
			if(yAxisComputationType != null && yAxisComputationType.size() > 0){
				if(yAxisComputationType.containsKey(strColumnName)) {    
					intTotal = yAxisComputationType.get(strColumnName);
				}
			}
			strPostAgg = StringUtil.getDataOperationCMDMap().get(intTotal);
		String timeDimension = lmRecentInfo.getTimeDimension();
		int agregation = lmRecentInfo.getAggregationType();
		
		
		modelMap.put("datedimension", dateDimensions);
		modelMap.put("timeDimension", timeDimension);
		modelMap.put("agregation", agregation);
		modelMap.put("objectType", 0);
		modelMap.put("selColumn", strColumnName);
		modelMap.put("bmostleast", true);
		modelMap.put("rlmrecent", strOpertionType);
		modelMap.put("postAgg", strPostAgg);
		
		if(strOpertionType.equals(""+ICubeConstants.LEASTRECENT))
			return new ModelAndView("analysisLeastRecentDataOperation");
		else 
			return new ModelAndView("analysisMostRecentDataOperation");
	}
	
	@RequestMapping(value = "/applyrecentdataoperation")
	@ResponseBody
	public Object applyLMRecentDataOperation(
			ModelMap modelMap,
			@RequestParam(value = "objectType", required = false) String strObjectType,
			@RequestParam(value = "selColumn", required = false) String columnName,
			@RequestParam(value = "bmostleast", required = false) String bmostleast,
			@RequestParam(value = "rlmrecent", required = false) String operaitontype,
			@RequestParam(value = "agrrgation", required = false) String agrrgation,
			@RequestParam(value = "timedimension", required = false) String timedimension, 
			@RequestParam(value = "cellreferance", required = false) String strCellRef,
			@RequestParam(value = "postaggregation", required = false) String strPostOperation,
			HttpServletResponse response, @LoggedInUser UserInfo userInfo) {

		try {
			if (operaitontype.equals("-1")) {
				HashMap tConditions = (HashMap) graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo();
				tConditions.remove(columnName);
				graphInfo.getObjectCubeDefinition().setLmRecentSettingsInfo(tConditions);
			} else {

				LMRecentInfo lmRecentInfo = new LMRecentInfo();
				lmRecentInfo.setAggregationType(Integer.parseInt(agrrgation));
				lmRecentInfo.setColumnName(columnName);
				lmRecentInfo.setOperationType(Integer.parseInt(operaitontype));
				lmRecentInfo.setTimeDimension(timedimension);
				graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo().put(columnName, lmRecentInfo);
				Map<String,Integer> dataMap = new LinkedHashMap<String,Integer>();
				dataMap = StringUtil.getDataOperationValueMap();
				int dataType = dataMap.get(strPostOperation);
				graphInfo.getyAxisComputationType().put(columnName, dataType);
				graphService.checkAndResetDrillUp(graphInfo, true);
			}

		} catch (Exception e) {
           ApplicationLog.error(e);
		}
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_DATA_OPERATION"), AppConstants.DETAIL,userInfo);
		response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		return refreshObjectData(null,response, userInfo, modelMap);
	}
	
	/**
	 * apply data operation .
	 * @param value 
	 * @return String
	 */
	@RequestMapping(value="/applyDataOperation")
	@ResponseBody
	public Object applyDataOperation(@RequestParam(value="value", required=false, defaultValue="") String strValue,
			HttpServletResponse response, ModelMap map, @LoggedInUser UserInfo userInfo,HttpServletRequest request) {

		Map<String,Integer> yAxisComputationType = new HashMap<String, Integer>();
		List barDataOps = new ArrayList();
		List lineDataOps = new ArrayList();
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Data operation","set Data operation",Thread.currentThread(),userInfo,null);
		try {
			Vector<String> dataLabels = (Vector<String>) graphInfo.getDataColumns().clone();
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
			if(graphInfo.getLineGraphDataLabelsForCombinedGraph() != null && graphInfo.getLineGraphDataLabelsForCombinedGraph().size() > 0){
				dataLabels.addAll(graphInfo.getLineGraphDataLabelsForCombinedGraph());
			}
			}
			List<ActiveUDDCInfo> actUddcInfo = graphInfo.getActiveUDDCInfo(userInfo.getUserId());
			if(actUddcInfo != null && actUddcInfo.size() > 0) {
				for (ActiveUDDCInfo activeUDDCInfo : actUddcInfo) {
					dataLabels.add(activeUDDCInfo.getUddcTemplateInfo().getColumnName());
				}
			}
			graphService.clearDistictLMRecentMap(graphInfo, null);
			Map combinedDataOperationMap = graphInfo.getCombinedDataOperationMap(); 
			for (String columnName : dataLabels) {
				    int iType = 0;
					String computationType = StringUtil.null2String(request.getParameter(columnName));
					
					//BUG:13762
					if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
					{
						if(graphInfo.getDataColLabels3() != null && graphInfo.getDataColLabels3().contains(columnName))
							barDataOps.add(Integer.parseInt(computationType));
						if(graphInfo.getTheDataColLabels4() != null && graphInfo.getTheDataColLabels4().contains(columnName))
							lineDataOps.add(Integer.parseInt(computationType));
						
						combinedDataOperationMap.put(columnName, computationType);
						
						//Added code to apply data operation for Combine chart when 2D are present [22/10/2020] Client: Shopify
						boolean callCombinedDS = true;
						if(graphInfo.getChangedDataColLabels3().contains(columnName)//Bar
							&& (graphInfo.getGraphData().getCmbBarrowLabel() != null && null != graphInfo.getGraphData().getCmbBarcolLabel()
							&& !graphInfo.getGraphData().getCmbBarrowLabel().equalsIgnoreCase("Legend")
							&& !graphInfo.getGraphData().getCmbBarrowLabel().equals(graphInfo.getGraphData().getCmbBarcolLabel())))
						{
							callCombinedDS = false;
						}
						if(graphInfo.getChangedTheDataColLabels4().contains(columnName)//Line
							&& (graphInfo.getGraphData().getCmbLinerowLabel() != null && !graphInfo.getGraphData().getCmbLinerowLabel().equalsIgnoreCase("Legend")))
						{
							callCombinedDS = false;
						}//Code end  for Shopify client
						
						if(!computationType.equals("6") && !computationType.equals("7")//min max
								&& !computationType.equals("1") && !computationType.equals("2")//average
								&& !computationType.equals("3") && !computationType.equals("4")
								&& !computationType.equals("101") && !computationType.equals("102")
								&& !computationType.equals("103") && !computationType.equals("29")
								&& !computationType.equals("30") && callCombinedDS)
							computationType = "0";
					}
					//BUG:13762
					
					if(Integer.parseInt(computationType)==ICubeConstants.LEAST_RECENT || Integer.parseInt(computationType) == ICubeConstants.MOST_RECENT) {
						int operaitontype = 0;
						LMRecentInfo lmRecentInfo = new LMRecentInfo();
						lmRecentInfo.setAggregationType(Integer.parseInt(StringUtil.null2String(request.getParameter(columnName+"_agrrgation"))));
						lmRecentInfo.setColumnName(columnName);
						if(Integer.parseInt(computationType)==ICubeConstants.LEAST_RECENT) {
							operaitontype = ICubeConstants.LEASTRECENT;
						} else {
							operaitontype = ICubeConstants.MOSTRECENT;
						}
						lmRecentInfo.setOperationType(operaitontype);
						lmRecentInfo.setTimeDimension(StringUtil.null2String(request.getParameter(columnName+"_timedimension")));
						if(graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo() == null){
							graphInfo.getObjectCubeDefinition().setLmRecentSettingsInfo(new HashMap());
						}
						graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo().put(columnName, lmRecentInfo);
						Map<String,Integer> dataMap = new LinkedHashMap<String,Integer>();
						dataMap = StringUtil.getDataOperationValueMap();
						iType = dataMap.get(StringUtil.null2String(request.getParameter(columnName+"_postaggregation")));
						if(graphInfo.getObjectCubeDefinition().getDistinctCountMap() != null) {
						     graphInfo.getObjectCubeDefinition().getDistinctCountMap().remove(columnName);
						}
					} else if(Integer.parseInt(computationType)==ICubeConstants.DISTINCT){
						if(graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo() != null) {
						     graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo().remove(columnName);
						}
						String dimensionName = StringUtil.null2String(request.getParameter(columnName+"_distinctCountdimension"));
						int operation = Integer.parseInt(StringUtil.null2String(request.getParameter(columnName+"_agrrgation_val")));
						if(graphInfo.getObjectCubeDefinition().getDistinctCountMap() == null) {
							graphInfo.getObjectCubeDefinition().setDistinctCountMap(new HashMap());
						}
						if(graphInfo.getObjectCubeDefinition().getDistinctCountOperationMap() == null) {
							graphInfo.getObjectCubeDefinition().setDistinctCountOperationMap(new HashMap());
						}
						graphInfo.getObjectCubeDefinition().getDistinctCountMap().put(columnName, dimensionName);
						graphInfo.getObjectCubeDefinition().getDistinctCountOperationMap().put(columnName, operation);
					} else {
						try {
							iType = Integer.parseInt(computationType);
							if(graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo() != null) {
						     graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo().remove(columnName);
						     graphService.setLMRecentInfo(graphInfo.getObjectCubeDefinition().getLmRecentSettingsInfo());
							}
							if(graphInfo.getObjectCubeDefinition().getDistinctCountMap() != null) {
							     graphInfo.getObjectCubeDefinition().getDistinctCountMap().remove(columnName);
							     graphService.setDistinctCountMap(graphInfo.getObjectCubeDefinition().getDistinctCountMap());
							}
						} catch (Exception ex) {}
					}
					
					yAxisComputationType.put(columnName, iType);
					
				}
			graphInfo.setBarDataOperationList(barDataOps);
			graphInfo.setLineDataOperationList(lineDataOps);
			graphInfo.setyAxisComputationType(yAxisComputationType);
			graphService.checkAndResetDrillUp(graphInfo, true);
		} catch (Exception ex) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_APPLY_DATA_OPERATION", 
					new Object[] { userInfo.getUsername(), getObjectDisplayName() }), ex);	
			return ResourceManager.getString("ERROR_MSG_FAILED_APPLY_DATA_OPERATION", new Object[] {ex.getMessage()});
		}
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_DATA_OPERATION"), AppConstants.DETAIL,userInfo);
		response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		return refreshObjectData(null,response, userInfo, map);
	}

	/**
	 * Show Group dialog.
	 * 
	 * @param map
	 *            model map object
	 * @return model and view response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/showgroupdailog")
	@ResponseBody
	public ModelAndView showGroupDialog(ModelMap map, @LoggedInUser UserInfo userInfo) {
		
		CubeVector cols = getColLabelNameVector(null);
		CubeVector rows = getRowLabelNameVector(null);
		List<Group> groupList = graphInfo.getGroupList();
		List<String> dimension = new ArrayList<String>();
		dimension.addAll(cols);
		dimension.addAll(rows);
		map.addAttribute("dimensionList", dimension);
		map.addAttribute("groupList", groupList);
		map.addAttribute("groupVO", new Group());
		return new ModelAndView("groupgraph");
	}
	
	
	
	/**
	 * Perform Save Group Operation.
	 * 
	 * @return 'Success' if operation is successful otherwise error message.
	 */
	@RequestMapping (value = "/saveGroup")
	@ResponseBody
	public Object saveGroup(@LoggedInUser UserInfo userInfo,  ModelMap map, HttpServletResponse response) {
		
		Object status = "";
		try {
			graphService.setGroupDetail(graphInfo);
			auditUserActionLog(ResourceManager.getString("LBL_SAVE_AND_APPLY_GRAPH_GROUP"), AppConstants.DETAIL,userInfo);
			status = refreshObjectData(null,response, userInfo, map);
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_SAVE_GROUP", new Object[]{userInfo.getUsername(), getObjectDisplayName() }), e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_SAVE");
		}
		return status;
	}
	
	/**
	 * Perform Add Group Operation.
	 * 
	 * @param map
	 *            model map object
	 * @param groupVO
	 *            group value object
	 * @return model and view response
	 */
	@RequestMapping(value="/addGroup")
	@ResponseBody
	public ModelAndView addNewGroup(ModelMap map, @ModelAttribute Group groupVO,@LoggedInUser UserInfo userInfo) {
		
		List<Group> groupList = graphInfo.getGroupList();
		int id = 0;
		if(groupVO.getGroupId() <= groupList.size())
			id = groupList.size()+100;
		else
			id = groupVO.getGroupId();
		
		String value = StringUtil.unescapeHtmlUtil(groupVO.getValues());
		String label = StringUtil.unescapeHtmlUtil(groupVO.getGroupLabel());
		String name = StringUtil.unescapeHtmlUtil(groupVO.getGroupName());
		groupVO.setGroupId(id);
		groupVO.setValues(value);
		groupVO.setGroupLabel(label);
		groupVO.setGroupName(name);
		groupList.add(groupVO);
		auditUserActionLog(ResourceManager.getString("LBL_ADD_GRAPH_GROUP"), AppConstants.DETAIL,userInfo);
		map.addAttribute("groupList", groupList);
		return new ModelAndView("graph/addGroupResponse");
	}
	
	
	
	/**
	 * This method is use to get Cube Dimension List for group Access By User
	 * 
	 * @param strSearchStr
	 *            search string
	 * @param strDimensionName
	 *            dimnesion name
	 * @param hideUdhc
	 *            flag for hide udhc or not
	 * @param showGV
	 *            flag for show global variable or not
	 * @param showAllData
	 *            flag for show all data
	 * @return list of selected item objecs
	 */
		
	/**
	 * Perform Edit Group Label Operation.
	 * 
	 * @param newValue
	 *            new group label
	 * @param index
	 *            editable group index
	 * @return 'Success' if operation is successful otherwise error message.
	 */
	@RequestMapping (value = "/editGroup")
	@ResponseBody
	public String editGroupLabel(@RequestParam("value") String newValue, @RequestParam("name") String index, @LoggedInUser UserInfo userInfo) {
		
		int ind = -1;
		String status = "";
		try {
			if (newValue != null && index != null && !newValue.trim().isEmpty()) {
				List<Group> groupList = graphInfo.getGroupList();
				try { ind = Integer.parseInt(index); }catch(Exception e){}
				Group objGroup = groupList.get(ind);
				if (objGroup != null) {
					objGroup.setGroupLabel(newValue);
				}
			} else {
				return "";
			}
			status = AppConstants.SUCCESS_STATUS;
		}catch(Exception ex) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_UPDATE_GROUP", new Object[]{userInfo.getUsername(), getObjectDisplayName() }), ex);
			status = ResourceManager.getString("ERROR_FAILED_TO_UPDATE_OBJECT");
		}
		auditUserActionLog(ResourceManager.getString("LBL_EDIT_GRAPH_GROUP_LABEL"), AppConstants.DETAIL,userInfo);
		return status;
	}
	
	
	
	/**
	 * Perform Remove Group Operation.
	 * 
	 * @param groupIndex
	 *            deleted group index
	 * @return 'Success' if operation is successful otherwise error message.
	 */
	@RequestMapping (value = "/deleteGroup")
	@ResponseBody
	public Object removeGroup(@RequestParam("groupId") Integer groupIndex, @LoggedInUser UserInfo userInfo) {
		
		String strGroupName = "";
		String status = "";
		int grpIndex = 0;
		try {
		if (groupIndex != null ) {
			List<Group> groupList = graphInfo.getGroupList();
			
			//Added code to solve bug #11194 start
			for(int j=0;j<groupList.size();j++)
			{
				if(groupList.get(j).getGroupId() == groupIndex.intValue())
				{
					grpIndex = j;
				}
			}
			//Added code to solve bug #11194 end
			
			Group deleteGrp = groupList.get(grpIndex);
			if (deleteGrp != null) {
			strGroupName = deleteGrp.getGroupName(); 
			String groupValues = deleteGrp.getValues();
			String selectedValeus[] = StringUtil.tokenize(groupValues, ",");
			removeFromPackColumnDDVM(deleteGrp.getColumnName(), selectedValeus[0], graphInfo,userInfo);
			groupList.remove(grpIndex);
			}
		}
		status = AppConstants.SUCCESS_STATUS;
		} catch(CubeException cx) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_DELETE_GROUP", new Object[]{userInfo.getUsername(), getObjectDisplayName() }), cx);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_DELETE")+ " : "+strGroupName;
		} catch (Exception e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_DELETE_GROUP", new Object[]{userInfo.getUsername(), getObjectDisplayName() }), e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_DELETE")+ " : "+strGroupName;
		}
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_GRAPH_GROUP"), AppConstants.DETAIL,userInfo);
		
		return status;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void removeFromPackColumnDDVM(String colName, String actualTextValues, GraphInfo graphInfo,UserInfo userInfo) throws CubeException {

		if ( graphInfo.getPackDDVMValues() != null) {
			Vector packValuesVect = (Vector) graphInfo.getPackDDVMValues().get(colName);
			if (packValuesVect != null && actualTextValues != null && !"".equals(actualTextValues)) {

				String[] actualText = actualTextValues.split(",");
				Vector packIndexesToDelete = new Vector();
				Vector tempPackValuesVect = new Vector();
				for (int i = 0; i < actualText.length; i++) {
					for (int j = 0; j < packValuesVect.size(); j++) {
						String[] values = (String[]) packValuesVect.get(j);
						if (actualText[i].equals(values[0])) {
							packIndexesToDelete.add(new Integer(j));
							break;
						}
					}
				}
				for (int i = 0; i < packValuesVect.size(); i++) {
					if (!packIndexesToDelete.contains(new Integer(i))) {
						tempPackValuesVect.add(packValuesVect.get(i));
					}
				}
				
				if (tempPackValuesVect.size() == 0) {
					graphInfo.getPackDDVMValues().remove(colName);
				} else {
					graphInfo.getPackDDVMValues().put(colName, tempPackValuesVect);
				}
			}
		}
	}

	/**
	 * show Group dialog.
	 * @param map ModelMap
	 * @param userInfo UserInfo
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/showgraphdata")
	@ResponseBody
	public ModelAndView showGraphDataDialog(ModelMap map, @LoggedInUser UserInfo userInfo) { 

		Pagination pagination = new Pagination();
		int iPageNo=1;
		int iSortColumn = 0;
		List graphLDataList = null;
		List headerList = null;
		try {
			detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"show data","Read data",Thread.currentThread(),userInfo,null);
			boolean isNumber = false;
			if(graphInfo.getGraphType()==GraphConstants.COMBINED_GRAPH) {
				graphLDataList = (List) graphService.getFormatedDataListCombine(graphInfo, userInfo).clone();
				headerList = (List) graphLDataList.get(0);
			}else{
				graphLDataList = (List) graphService.getFormatedDataList(graphInfo, userInfo).clone();
				headerList = (List) graphLDataList.get(0);
			}
			if(graphInfo.getGraphType() == GraphConstants.CANDLE_STICK_GRAPH
					 || graphInfo.getGraphType() == GraphConstants.HIGH_LOW_OPEN_CLOSE_GRAPH)
			{
				String[] splitString = ((String) headerList.get(0)).split(",");
				headerList.set(0, splitString[0]);
			}
			
			int ilength = headerList.size(); 
			iSortColumn = ilength - 1;
			if(headerList != null && headerList.size() >= iSortColumn) {
			  String columnName = (String) headerList.get(iSortColumn);
			  int type = CubeUtil.getColumnType(columnName, graphInfo.getCubeInfo());
			  int sortColumnType = GeneralFiltersUtil.getColType(type);
			  if(sortColumnType == GeneralFiltersUtil.NUMERIC_DIMENSION_COL) {
				  isNumber = true;
			  }
			}
			graphLDataList.remove(0);
			int iTotalRecord = graphLDataList.size();
			pagination = GeneralUtil.getPaginationInfo(iPageNo, iTotalRecord,15);

			graphLDataList = sortGraphData(graphLDataList,iSortColumn,0,isNumber);
			graphLDataList =  graphLDataList.subList(pagination.getStartIndex(), pagination.getEndIndex());
		} catch(Exception ex) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_OPEN_GRAPH_DATA", new Object[]{userInfo.getUsername(), getObjectDisplayName() }), ex);
		}
		map.addAttribute("headerList", headerList);
		map.addAttribute("sortOption", iSortColumn +":0");
		map.addAttribute("sortType", iSortColumn);
		map.addAttribute("totalPage",  pagination.getTotalPage());
		map.addAttribute("pageNo", 1);
		map.addAttribute("isSmarten",false);
		map.put("graphLDataList", graphLDataList);
		auditUserActionLog(ResourceManager.getString("LBL_SHOW_GRAPH_DATA_DIALOG"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("/graph/graphData");
	}
	
	/**
	 * @param graphData graphDataList
	 * @param sortIndex int
	 * @param sortOrder int 
	 * @return List of graphdata
	 */
	public List<List> sortGraphData(List<List> graphData,int sortIndex,int sortOrder,boolean isNumber){
		List<List> tmpGraphData = graphData;
		for (int i = 0; i < tmpGraphData.size(); i++) {
			for (int j = i+1; j < tmpGraphData.size(); j++) {
				List l1 = tmpGraphData.get(i);
				List l2 = tmpGraphData.get(j);
				Object val1 = l1.get(sortIndex);
				Object val2 = l2.get(sortIndex);
				int result = 0;
				boolean flag = false;
				if(isNumber) {
					try {
					if(Integer.parseInt(val1.toString()) > Integer.parseInt(val2.toString())){
						result = 1;
					} else {
						result = -1;
					}
					} catch (Exception e) {
						ApplicationLog.error(e);
						flag = true;
					} finally{
						if (flag) {
							result = compareData(val1,val2);
						}
					}
				} else {
					result = compareData(val1,val2);
				}
				if(sortOrder == 0){
					if(result == 0){
						
					} else if(result > 0) {
						graphData.set(i, l2);
						graphData.set(j, l1);
					} else {
						graphData.set(i, l1);
						graphData.set(j, l2);
					}
				} else {
					if(result == 0){
						
					} else if(result < 0) {
						graphData.set(i, l2);
						graphData.set(j, l1);
					} else {
						graphData.set(i, l1);
						graphData.set(j, l2);
					}
				}
			}	
		}
		return graphData;
		
	}
	
	/**
	 * @param val1 object value
	 * @param val2 object value
	 * @return int
	 */
	private int compareData(Object val1,Object val2){
		if (Byte.class.isInstance(val1)) {
			return ((Byte) val1).compareTo((Byte) val2);
		} else if (Character.class.isInstance(val1)) {
			return ((Character) val1).compareTo((Character) val2);
		} else if (Short.class.isInstance(val1)) {
			return ((Short) val1).compareTo((Short) val2);
		} else if (Integer.class.isInstance(val1)) {
			return ((Integer) val1).compareTo((Integer) val2);
		} else if (Long.class.isInstance(val1)) {
			return ((Long) val1).compareTo((Long) val2);
		} else if (Float.class.isInstance(val1)) {
			return ((Float) val1).compareTo((Float) val2);
		} else if (Double.class.isInstance(val1)) {
			return ((Double) val1).compareTo((Double) val2);
		} else if (Boolean.class.isInstance(val1)) {
			return ((Boolean) val1).compareTo((Boolean) val2) * -1;				//Change for Getting True values first in Ascending..
		} else if (Date.class.isInstance(val1)) {
			return ((Date) val1).compareTo((Date) val2);
		}
		
		return val1.toString().toLowerCase().compareTo(val2.toString().toLowerCase());
	}
	
	/**
	 * Get the graphdata List .
	 * 
	 * @param modelMap
	 *            Model Map Object
	 * @param session
	 *            Session Object
	 * @param iPageNo
	 *            page number
	 * @param iSortColumn
	 *            sort type
	 * @param strSortOrder
	 *            sort order
	 * @return refreshed UDDC List response.
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping (value = "/reloadGraphDataList")
	@ResponseBody
	public ModelAndView reloadGraphDataList(ModelMap modelMap, HttpSession session,
			@RequestParam(value="pageNo", required=false, defaultValue="1") int iPageNo,
			@RequestParam(value="sortType", required=false, defaultValue="0") int iSortColumn,
			@RequestParam (value = "sortOrder", required = false, defaultValue = "0") String strSortOrder, @LoggedInUser UserInfo userInfo) {
		
		Pagination pagination = new Pagination();
		int sortOrderNumber = 0;
		if (strSortOrder.equals("0")) {
			sortOrderNumber = 0;
			
		} else {
			sortOrderNumber = 1;
			strSortOrder = IApplicationConfigurationService.SORT_DESENDING;
		}
		
		boolean isNumber = false;
		List graphLDataList = (List) graphInfo.getFormatedDataList().clone();
		List headerList =  (List) graphLDataList.get(0);
		if(headerList != null && headerList.size() >= iSortColumn) {
		  String columnName = (String) headerList.get(iSortColumn);
		  int type = CubeUtil.getColumnType(columnName, graphInfo.getCubeInfo());
		  int sortColumnType = GeneralFiltersUtil.getColType(type);
		  if(sortColumnType == GeneralFiltersUtil.NUMERIC_DIMENSION_COL) {
			  isNumber = true;
		  }
		}
		graphLDataList.remove(0);
		int iTotalRecord = graphLDataList.size();
		pagination = GeneralUtil.getPaginationInfo(iPageNo, iTotalRecord,15);
		try {
			graphLDataList = sortGraphData(graphLDataList,iSortColumn,sortOrderNumber,isNumber);
		} catch(Exception ex) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_REFRESH_GRAPH_DATA_LIST", new Object[]{userInfo.getUsername(), getObjectDisplayName() }), ex);
		}
		graphLDataList = graphLDataList.subList(pagination.getStartIndex(), pagination.getEndIndex());
		
		modelMap.addAttribute("headerList", headerList);
		modelMap.addAttribute("graphLDataList", graphLDataList);
		modelMap.addAttribute("totalPage",  pagination.getTotalPage());
		modelMap.addAttribute("sortOption", iSortColumn +":"+sortOrderNumber);
		modelMap.addAttribute("sortType", iSortColumn);
		modelMap.addAttribute("pageNo", iPageNo);
		modelMap.addAttribute("isSmarten",false);
		auditUserActionLog(ResourceManager.getString("LBL_RELOAD_GRAPH_DATA"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("/graph/graphData");
	}

	/**
	 * @param bExportCsv true or false
	 * @param response HttpServletResponse
	 * @param bExportXml true or false
	 * @param userInfo UserInfo
	 */
	@RequestMapping(value = "/exportGraphData")
	@ResponseBody
	public void exportGraphData(
			@RequestParam(value = "exportToCsv", required = false) boolean bExportCsv,
			HttpServletResponse response,
			@RequestParam(value = "exportToXml", required = false) boolean bExportXml,
			@LoggedInUser UserInfo userInfo) {

		String sFileName = "";
		try {
			String graphName = graphInfo.getGraphName();
			if(graphName == null) {
				graphName = "New Graph"; 
			}

			if (bExportCsv) {
				sFileName = graphName +".csv"; 
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition",
						"attachment; filename=" + sFileName);
				response.setCharacterEncoding("UTF-8");
				OutputStream sout = new BufferedOutputStream(response.getOutputStream());
				if (sout != null) {
					graphService.exportGraphDataCSV(sout,graphInfo,userInfo);
				}
				sout.close();
				response.flushBuffer();
			} else if (bExportXml) {
				sFileName = graphName +".xml";
				response.setContentType("text/xml");
				response.setHeader("Content-Disposition",
						"attachment; filename=" + sFileName);
				response.setCharacterEncoding("UTF-8");
				OutputStream sout = new BufferedOutputStream(response.getOutputStream());
				if (sout != null) {
					graphService.exportGraphDataXML(sout,graphInfo,userInfo,-1);
				}
				sout.close();
				response.flushBuffer();
			}
		}
		catch (IOException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_DISPLAY_GRAPH_DATA",
					new Object[] {graphInfo.getGraphName(),userInfo.getUsername() }), e);
		}
		auditUserActionLog(ResourceManager.getString("LBL_EXPORT_GRAPH_DATA_CSV_XML"), AppConstants.DETAIL,userInfo);
	}
	
	/**
	 * show Group dialog.
	 * @param map ModelMap
	 * @param UserInfo userinfo
	 * @return ModelAndView
	 */
	@RequestMapping(value="/showobjectinfo")
	@ResponseBody
	public ModelAndView showObjectInfoDialog(ModelMap modelMap,
			@LoggedInUser UserInfo userinfo){
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Object information","open object information",Thread.currentThread(),userinfo,null);
		modelMap = (ModelMap) graphService.showObjectInformation(graphInfo, userinfo,modelMap,getCubeInfo(graphInfo.getGraphId()));
		auditUserActionLog(ResourceManager.getString("LBL_SHOW_GRAPH_OBJECT_INFORMATION_DIALOG"), AppConstants.DETAIL,userinfo);
		return new ModelAndView("objectinfograph");
	}
	
	@ResponseBody
	@RequestMapping(value = "/addNote")
	public ModelAndView showAddNote(ModelMap map,@LoggedInUser UserInfo userinfo) {
		return new ModelAndView("addGraphnote");
	}

	/**
	 * Returns the Cube User Column Expression Executor.
	 * 
	 * @return CubeColumnExpExecutor Object
	 * @throws CubeException
	 * @throws ALSException
	 */
	@Override
	public CubeColumnExpExecutor getColumnExpressionExecutor() throws CubeException, ALSException {
		return graphService.getColumnExpressionExecutor();
	}

	/**
	 * Returns the Cube User Label Expression Executor.
	 * 
	 * @return CubeLabelExpExecutor Object
	 * @throws CubeException
	 * @throws ALSException
	 */
	@Override
	public CubeLabelExpExecutor getLabelExpressionExecutor() throws CubeException, ALSException {
		return graphService.getLabelExpressionExecutor();
	}

	/**
	 * Get Cube Name
	 * 
	 * @return cube name.
	 */
	@Override
	public IDataObject getCubeInfo(String objectId) {
		if(objectId!=null && !objectId.isEmpty()) {
			return getGraphObjectFromMap(objectId).getCubeInfo();
		}
		return this.graphInfo.getCubeInfo();
	}

	/**
	 * Object Id like AnalysisId, GraphId...
	 * 
	 * @return String object id
	 */
	@Override
	public String getObjectId() {
		return graphInfo.getGraphId();
	}

	/**
	 * Set Active Templates info cube Through Appropriate Service.
	 * 
	 * @param activeTemplateIds
	 *            active templates array
	 * @param iType
	 *            template type like uddc-5, udhc-6
	 * @throws ALSException
	 * @throws CubeException 
	 * @throws RScriptException 
	 */
	@Override
	public void setActiveTemplates(Object[] activeTemplateIds, int iType,UserInfo userInfo, boolean isRefresh) throws MDXException, RealTimeCubeException, CubeException, RScriptException {
		if (iType == TemplateUtil.ANALYSIS_OUTLINER) {
			graphService.setActiveTemplates(iType, activeTemplateIds, graphInfo, userInfo.getUserId(), false);
			Object object1 = activeTemplateIds[0];
			if(object1 != null && object1 instanceof CubeVector) {
				graphInfo.setOutlinerFilter((CubeVector) object1);
				graphService.checkAndResetDrillUp(graphInfo, false);
			}else {
				graphInfo.setOutlinerFilter(new CubeVector());
				graphService.checkAndResetDrillUp(graphInfo, false);
			}
		}else if (iType == TemplateUtil.ANA_USER_UDDC) {
			List<ActiveUDDCInfo> activeUDDCList = graphInfo.getActiveUDDCInfo(userInfo.getUserId());
			activeTemplateIds = new Object[activeUDDCList.size()];
			for (int cnt = 0; cnt < activeUDDCList.size(); cnt++) {
				ActiveUDDCInfo activeuddcInfo = activeUDDCList.get(cnt);
				activeTemplateIds[cnt] = activeuddcInfo.getUddcTemplateInfo();
			}
			graphService.setActiveTemplates(iType, activeTemplateIds, graphInfo, userInfo.getUserId(), false);
		}else if (iType == TemplateUtil.ANALYSIS_MAINFILTER || iType == TemplateUtil.ANALYSIS_FILTER) {
			graphService.setActiveTemplates(iType, activeTemplateIds, graphInfo, userInfo.getUserId(), false);
		}
		
		auditUserActionLog(ResourceManager.getString("LBL_SET_GRAPH_ACTIVE_TEMPLATE"), AppConstants.DETAIL,userInfo);
	}

	//Not Require in graph
	/**
	 * Set UDDC Item Properties.
	 * 
	 * @param uddcTemplateInfo
	 *            uddc template info
	 * @throws ALSException
	 * @throws CubeException
	 */
	@Override
	public void setUddcItemProperties(UddcTemplateInfo uddcTemplateInfo) throws ALSException, CubeException {}

	//Not Require in graph
	/**
	 * Get Selected Column Name from the Right Click
	 * 
	 * @param cellRefId
	 *            cell refrence id
	 * @return string column name
	 */
	@Override
	public String getColumnNameFmCellReference(String cellRefId) { return ""; }

	/**
	 * Get Col Lable Name List
	 * 
	 * @return list of column label(s)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CubeVector getColLabelNameVector(String objectId) {
		
		CubeVector colVevtor = new CubeVector();
		Vector outLinerData = graphService.getOutLinerDataWithoutSysGeneratedFields(false, graphInfo);
		Vector<String> colDataLabels = (Vector<String>) outLinerData.get(1);
		if (colDataLabels != null) {
			colVevtor.addAll(colDataLabels);
		}
		
		return colVevtor;
	}

	/**
	 * Get Row Lable Name List
	 * 
	 * @return list of row label(s)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CubeVector getRowLabelNameVector(String objectId) {
		
		CubeVector rowVector = new CubeVector();
		Vector outLinerData = graphService.getOutLinerDataWithoutSysGeneratedFields(false, graphInfo);
		Vector outLinerDataTemp=new Vector<>(outLinerData);;
		Vector<String> rowDataLabels = new Vector<String>((Vector<String>) outLinerDataTemp.get(0));
		if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
		if(outLinerDataTemp.size() > 4) {
			if(rowDataLabels.isEmpty())
				rowDataLabels = (Vector<String>) outLinerDataTemp.get(3);
			else
			rowDataLabels.addAll((Vector<String>) outLinerDataTemp.get(3));
		}
		}
		if (rowDataLabels != null) {
			rowVector.addAll(rowDataLabels);
		}
		return rowVector;
	}

	/**
	 * Get Data Name List
	 * 
	 * @return list of data name(s)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CubeVector getDataNameVector(UserInfo userInfo) {
		
		CubeVector dataVector = new CubeVector();
		dataVector.addAll(graphInfo.getDataColumns());
		if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
			dataVector.addAll(graphInfo.getLineGraphDataLabelsForCombinedGraph());
		}
		List<ActiveUDDCInfo> activeUDDCList = graphInfo.getActiveUDDCInfo(userInfo.getUserId());
		for (int cnt = 0; cnt < activeUDDCList.size(); cnt++) {
			ActiveUDDCInfo activeuddcInfo = activeUDDCList.get(cnt);
			dataVector.add(activeuddcInfo.getUddcTemplateInfo().getColumnName());
		}
		return dataVector;
	}
	
	/**
	 * Get All Active Template Properties
	 * 
	 * @return ActiveTemplateProperties object
	 */
	@Override
	public ActiveTemplateProperties getActiveTemplateProperties(String objectId) {
		//below code commented for 14128
		//Added for bug no 11356
		/*if(graphInfo.getGraphData().isGraphFromDashboard())
		{
			graphInfo.getActiveTemplateProperties().setRankList(new ArrayList<CubeRankDataLabel>());
			return graphInfo.getActiveTemplateProperties();
		}*/
		//bug no 11356 end
		if(objectId!=null && !objectId.isEmpty()) {
			return getGraphObjectFromMap(objectId).getActiveTemplateProperties();
		}
		return graphInfo.getActiveTemplateProperties();
	}

	/**
	 * Returns type of column name provided.
	 * 
	 * @param columnName
	 *            Name of column to get type of
	 * @return Type of column name
	 * @throws ALSException
	 * @throws CubeException
	 */
	@Override
	public int getColumnTypeByColumnName(String columnName, boolean isBackEnd)
			throws ALSException, CubeException {

		return graphService.getColumnTypeByColumnName(columnName, graphInfo, isBackEnd);
	}

	/**
	 * Object display name like Analysis Name, Graph Name...
	 * 
	 * @return String object name
	 */
	@Override
	public String getObjectDisplayName() {
		
		return graphInfo.getGraphName();
	}

	/**
	 * This method will returns information of selected Page dimensions for the
	 * object.
	 * 
	 * @return Information of Page dimensions for the object
	 * @throws CubeException
	 */
	public String[][] getPageFilters(UserInfo userInfo, String objectId) throws CubeException {
		
		return graphService.getPageFilters(graphInfo);
	}
	
	public String[][] getRetrivalParameter(UserInfo userInfo,HttpServletRequest request) throws CubeException {
		return graphService.getRetrivalParameter(graphInfo);
	}

	/**
	 * This method will returns Map of formatted values of cube conditions to be
	 * displayed.
	 * 
	 * @param columnName
	 *            Name of column
	 * @param cubeId
	 *            Cube identifier
	 * @return Map of filter conditions as formatted String array
	 * @throws CubeException
	 * @throws DatabaseOperationException 
	 */
	@Override
	public Map<Integer, String[]> generateStringFromConditionMap(
			String columnName, String cubeId,HttpServletRequest request) throws CubeException, DatabaseOperationException {
		return graphService.generateStringFromConditionMap(columnName, cubeId);
	}

	
	@Override
	public String getObjectTypeName() {
		return objectTypeName;
	}

	/**
	 * This method will returns selected values for page filter component for
	 * the column specified.
	 * 
	 * @param columnName
	 *            Column Name
	 * @param cubeId
	 *            Cube identifier
	 * @return Selected page filter value for the column name
	 * @throws CubeException
	 * @throws DatabaseOperationException 
	 */
	@Override
	public String getPageFilterSelectedLOV(String columnName, String cubeId,HttpServletRequest request)
			throws CubeException, DatabaseOperationException {
		return graphService.getPageFilterSelectedLOV(columnName, cubeId);
	}

	/** 
	 * This method is use in show timeSeries For get dataLabel List
	 * @return Vector<String> 
	 */	
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> getDataLabelListTimeSeries(boolean pageFilter, String pageFilterColumnName,UserInfo uInfo) {
		
		Vector<String> dataLabels =  new Vector<String>();
		if (pageFilter) {
			dataLabels.add(pageFilterColumnName);
		} else {
			dataLabels.addAll(graphInfo.getDataColumns());
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
				dataLabels.addAll(graphInfo.getLineGraphDataLabelsForCombinedGraph());
			}
			List<ActiveUDDCInfo> activeUDDCList = graphInfo.getActiveUDDCInfo(uInfo.getUserId());
			for (int cnt = 0; cnt < activeUDDCList.size(); cnt++) {
				ActiveUDDCInfo activeuddcInfo = activeUDDCList.get(cnt);
				dataLabels.add(activeuddcInfo.getUddcTemplateInfo().getColumnName());
			}
		}
		
		
		return dataLabels;
	}

	/**
	 * This method is use for get filterCondiotn For Timefilter
	 * 
	 * @param dataLabel
	 *            columnName
	 * @return ConditonVector
	 */
	@Override
	public CubeVector getFilterConditions(String dataLabel, String objectId) throws CubeException, ALSException{
		
		CubeVector condition = null;
		condition = graphService.getFilterConditions(dataLabel, graphInfo);
		return condition;
	}
	
	/**
	 * This method is use for get TimeSeriesCondiotn For TimeSeries
	 * 
	 * @param dataLabel
	 *            columnName
	 * @return ConditonVector
	 */
	@Override
	public CubeVector getConditionVect(String dataLabel) {
		
		CubeVector condition = null;
		Map filterMap = graphInfo.getTimeConditions();
		if(filterMap == null)
	    {
			filterMap = new HashMap();
	    }   
		condition = (CubeVector) filterMap.get(dataLabel);
		return condition;
	}

	/**
	 * This method is use to get TimeSeries Map
	 * 
	 * @param conditions
	 *            Condition Vector and cube id
	 * @param strCubeId
	 *            cube Id
	 * @return Hashmap
	 * @throws CubeException 
	 */
	@Override
	public HashMap getConditionMap(CubeVector conditions, String strCubeId, String objectId) {
		
		HashMap conditionMap = null;
		try {
			conditionMap = (HashMap) graphService.getConditionMap(conditions, strCubeId);
		} catch (CubeException e) {
			ApplicationLog.error(e);
		}
		if (conditionMap == null) {
			conditionMap = new HashMap();
		}
		return conditionMap;
	}

	/**
	 * @param obj
	 *            object
	 * @param objmode
	 *            mode
	 * @return object
	 */
	@Override
	public Object getSelectedValue(Object obj, Object objmode,HttpServletRequest request) {
		Object selected = null;
    	selected = graphService.getSelectedValue(obj, objmode);
    	return selected;
	}

	/**
	 * for apply timeFilter
	 * 
	 * @param requestParamMap
	 * @throws CubeException 
	 */
	@Override
	public void applyTimeFilter(Map<String, String> requestParamMap, UserInfo userInfo,HttpServletRequest request) throws CubeException {
			graphService.applyTimeFilter(requestParamMap, graphInfo);
			
		// Added below code for when user put time dimension in page filter and
		// apply from advanced pageFilter means (time series).
			//graphService.setDHTMLGraph(graphInfo, userInfo.getUserId());
			graphService.refreshPageFilterTooltip(graphInfo);
	}

	/**
	 * @param strValue
	 * @param strCubeName
	 * @return
	 */
	@Override
	public String getConditionValueType(String strValue, String strCubeName) {

		return graphService.getConditionValueType(strValue,strCubeName);
	}

	/**
	 * @param strCol
	 *            column
	 * @return integer
	 */
	@Override
	public int getColumnType(String strCol,UserInfo userInfo) {
		int colType = 0;
    	try {
    		colType = CubeUtil.getColumnType(strCol, graphService.getOriginalResultSetMetaData(),getGraphInfo().getCubeInfo().getDataObjectId());
		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_GET_COLUMN_TYPE",
					new Object[] {userInfo.getUsername(), getObjectDisplayName()}), e);
		}
    	return colType;
	}
	
	/**
	 * The cube's meta data is returned.
	 * 
	 * @return inquiry result
	 * @exception CubeException
	 */
	@Override
	public ICubeResultSetMetaData getOriginalResultSetMetaData() throws CubeException {
		
		return graphService.getOriginalResultSetMetaData();
	}

	/**
	 * for apply timeFilter
	 * 
	 * @param requestParamMap
	 */
	@Override
	public void applyPageFilter(Map<String, String> requestParams, String cubeId, HttpServletRequest request, HttpServletResponse response,UserInfo userInfo)
			throws DatabaseOperationException, ALSException, MDXException, RealTimeCubeException, CubeException {

		String preventPivotCall = requestParams.get("preventPivotCall");
		boolean preventCallPivotTableFlag = (preventPivotCall != null && preventPivotCall.equals("true"));

		if (preventCallPivotTableFlag) {
			boolean refreshReq = graphInfo.isRefreshReq();
			graphInfo.setRefreshReq(false);

			graphService.applyPageFilter(requestParams, cubeId, graphInfo, userInfo);

			graphInfo.setRefreshReq(refreshReq);

			requestParams.remove("preventPivotCall");
		} else {
			graphService.applyPageFilter(requestParams, cubeId, graphInfo, userInfo);
		}
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_PAGE_FILTER"), AppConstants.DETAIL,userInfo);
	}

	/**
	 * Get Row Column Display Name(s) Map
	 * 
	 * @return display name map
	 * @throws ALSException
	 * @throws CubeException
	 */
	@Override
	public Map<String, String> getRowColumnDisplayNameMap() throws CubeException {
		
		return graphService.getRowColumnDisplayNameMapForSortRank(graphInfo,graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH ? true : false);
	}

	/**
	 * Get Measure Display Name(s) Map
	 * 
	 * @return display name map
	 * @throws ALSException
	 * @throws CubeException
	 */
	@Override
	public Map<String, String> getMeasureDisplayNameMap(UserInfo userInfo) throws CubeException {

		return graphService.getMeasureDisplayNameMap(graphInfo, graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH ? true : false, userInfo.getUserId());
	}


	/**
	 * Object Folder Id 
	 * @return String object id
	 */
	@Override
	public String getFolderId(HttpServletRequest request) {
		
		return graphInfo.getFolderInfo().getFolderId();
	}
	
	@Override
	public List<SelectItem> getLOVList(String strDimensionName,
			String strSearchStr, boolean isCustomSort, boolean hideUdhc, 
			boolean showGV,boolean showAllData,String strCubeId,UserInfo userInfo,
			int lastIndexValue,boolean isFromPageFilter,boolean isBackEnd,HttpServletRequest request,String filterCondition) {
		
		try {
			List<SelectItem> values = new ArrayList<SelectItem>();
			values = graphService.getLovData(strDimensionName, strSearchStr, graphInfo.getCubeInfo().getDataObjectId(), userInfo, graphInfo,isCustomSort,hideUdhc,showGV,showAllData, lastIndexValue, isFromPageFilter,true,isBackEnd,isPageFilterOpen(),filterCondition);

			return values;
		} catch (Exception e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_GET_LOV_DATA",
					new Object[] {userInfo.getUsername(), getObjectDisplayName()}), e);
			return new ArrayList<SelectItem>();
		} 
	}

	/**
	 * Schedulername 
	 * 
	 * @return String Schedulername
	 */
	@Override
	public String getSchedulerName(HttpServletRequest request){
		return getObjectId();
	}
	
	@Override
	public String getSchedulerDisplayName(HttpServletRequest request){
		return getObjectDisplayName();
	}
	
	/**
	 * get forecasting column Map
	 * @return map
	 * @throws CubeException 
	 */
	@Override
	public Map<SelectItem, String> getColumnMapForForecasting() throws CubeException {
		
		return graphService.getColumnMapForForecasting(graphInfo);
	}
	
	@Override
	public void setRetrivalParameterList(UserInfo userInfo) throws CubeException {
		graphService.checkAndResetDrillUp(graphInfo, true);
	}

	@Override
	public ArrayList<ParamItem> getRetrivalParameters(HttpServletRequest request) {
		return getActiveTemplateProperties(null).getPreloadingParameters();
	}

	@Override
	public void setRetrivalParameters(
			ArrayList<ParamItem> retrivalParam,HttpServletRequest request) {
		getActiveTemplateProperties(null).setPreloadingParameters(retrivalParam);
		
	}
	
	//Not Needed To Set in graph
	/**
	 * Sets activated global variable list.
	 * @param activeVariablesTable
	 * @throws ALSException 
	 * @throws CubeException 
	 */
	@Override
	public void setActiveVariableMap (Hashtable activeVariablesTable,HttpServletRequest request) throws KPIException,ALSException, CubeException {
		graphService.setActiveVariableMap(activeVariablesTable);
	}

	@Override
	public String getColumnDisplayName(String strColumnName) throws  CubeException {
		return graphService.getAxisDisplayName(strColumnName, graphInfo);
	}

	public ModelAndView refreshObjectData(HttpServletRequest request,HttpServletResponse response, UserInfo userInfo, ModelMap map) {
		GraphService graphService=this.graphService;
		GraphInfo graphInfo=this.graphInfo;
		String strObjectId=null;
		if(map.get("strObjectId")!=null) {
			strObjectId=map.get("strObjectId").toString();
			if(strObjectId!=null && !strObjectId.isEmpty()) {
				if (getServiceMap().get(strObjectId) != null) {
					graphService = (GraphService) getServiceMap().get(strObjectId);
					graphInfo = (GraphInfo) getDetailInfoMap().get(strObjectId);
				}		
			}
		}else {
			map.put("strObjectId", strObjectId);
		}
		if (detailedMonitorEndpoint.getActiveRequest(Thread.currentThread().getId()) != null && (detailedMonitorEndpoint.getActiveRequest(Thread.currentThread().getId()).getProcess() !=null && !detailedMonitorEndpoint.getActiveRequest(Thread.currentThread().getId()).getProcess().isEmpty())) {
    		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),detailedMonitorEndpoint.getActiveRequest(Thread.currentThread().getId()).getProcess().equalsIgnoreCase("refreshObject")?"Refresh Graph":null,"Refresh Graph",Thread.currentThread(),userInfo,null);
		} else {
			detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Open Graph","Initialize Graph",Thread.currentThread(),userInfo,null);
		}
		
		clearNarrativeInsightsByObjId(strObjectId);
		if(userInfo.isFromAPI() && graphInfo.getDashboardInfo()!=null) {
			String dbId = graphInfo.getDashboardInfo().getDashboardId();
			if(graphInfo.getDashboardInfo().getParentDashboardInfo()!=null) {
				dbId = graphInfo.getDashboardInfo().getParentDashboardInfo().getDashboardId();
			}
			GeneralUtil.setApiParameter(dbId, userInfo, map);
		}
		graphInfo.setDisplayBarIndexList(new ArrayList());
		graphInfo.setDisplayLineIndexList(new ArrayList());
		Map<String, String> params = new HashMap<String, String>();
		params.put("isRefreshReq", "true");
		params.put("loggedInUserId", userInfo.getUserId());
		boolean isFromExport=false;
		if (request != null && request.getSession() != null) { 		    
		    Object sessionAttribute = request.getSession().getAttribute("FROM_SE");
		    if (sessionAttribute != null && sessionAttribute instanceof Boolean) {}
		}
		if(request!=null && request.getSession()!=null){
			isFromExport = request.getSession().getAttribute("FROM_SE")!= null && ((boolean)request.getSession().getAttribute("FROM_SE"));
			}
		map.addAttribute("isFromExport",isFromExport);
		String strDateFormat = CalendarUtil.getDataDisplayFormat(userInfo, Types.TIMESTAMP);
		map.addAttribute("strDateFormat",strDateFormat);
		/*if(map.get("isFromMobile") != null && map.get("isFromMobile").toString().equals("true")) {
			graphService.setMobileDefaultView(graphInfo);
		}*/
		long currentTimeStamp = System.currentTimeMillis();
		GraphProperties graphProperties = null;
		try {
			List<CubeRankDataLabel> cubeRankDataLabels = graphInfo.getRankList();
			if(graphService.getObjectMode() == AppConstants.NEW_MODE) {
				if (graphService.isFirstTime()) {
					graphService.setDefaultSorting(graphInfo,userInfo);
				
					
					if (graphInfo.getGraphType() == GraphConstants.PIE_GRAPH) {

						
						CubeRankDataLabel cubeRankDataLabel  = getCubeRankLabel(CubeRankDataLabel.ROW_TYPE,cubeRankDataLabels);
						if (cubeRankDataLabel == null) {
							graphService.setDefaultRank(CubeRankDataLabel.ROW_TYPE, graphInfo, userInfo);
						}
						
					//Open Comment for Apply default rank in graph
					
					/*CubeRankDataLabel cubeRankDataLabel  = getCubeRankLabel(CubeRankDataLabel.COL_TYPE,cubeRankDataLabels);
					if (cubeRankDataLabel == null) {
						graphService.setDefaultRank(CubeRankDataLabel.COL_TYPE, graphInfo, userInfo);
					}
					cubeRankDataLabel  = getCubeRankLabel(CubeRankDataLabel.ROW_TYPE,cubeRankDataLabels);
					if (cubeRankDataLabel == null) {
						graphService.setDefaultRank(CubeRankDataLabel.ROW_TYPE, graphInfo, userInfo);
					}*/
					// added by krishna to apply default rank for nested pie graph start
					
					// added by krishna to apply default rank for nested pie graph end
					}
//					if (graphInfo.getGraphPanel() instanceof GaugeExPanel) {
//						graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().setWidth(180);
//						graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().setHeight(180);
//					}
				}
			}
			CubeRankDataLabel cubeRankDataLabel  = getCubeRankLabel(CubeRankDataLabel.COL_TYPE,cubeRankDataLabels);
			cubeRankDataLabel  = getCubeRankLabel(CubeRankDataLabel.ROW_TYPE,cubeRankDataLabels);
			if(cubeRankDataLabel != null)
			{	
				if(graphInfo.getGraphProperties().getPieGraph().isClustered() && cubeRankDataLabel.getRowLimit() == com.elegantjbi.service.graph.GraphConstants.DEFAULT_RANK)
				{	
					cubeRankDataLabel.setRowLimit(com.elegantjbi.service.graph.GraphConstants.DEFAULT_NESTED_RANK);
					cubeRankDataLabel.setShowOthers(true);
				}
			}
			String preventPivotCall = (String) map.get("preventPivotCall");
			boolean preventCallPivotTableFlag = (preventPivotCall != null && preventPivotCall.equals("true"));
			map.addAttribute("isLegendVisible",graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isLegendPanelVisible());
			boolean isSavegraph = false;
			if(map.get("issaveGraph") != null) {
				isSavegraph = (boolean)map.get("issaveGraph");
			}
			if(!isSavegraph) {
				if (preventCallPivotTableFlag) {
					boolean refreshReq = graphInfo.isRefreshReq();
					graphInfo.setRefreshReq(false);
	
					graphService.initGraphData(graphInfo, params,userInfo);
	
					graphInfo.setRefreshReq(refreshReq);
	
					map.remove("preventPivotCall");
				} else {
					
					graphService.initGraphData(graphInfo, params,userInfo);
				}
			}
			if(graphService.isGraphTypeWizard())
				graphService.setGraphTypeWizard(false);
			/* Added By Nikhil Patel */
			/*if (isFirstTime) {
				graphService.setXYDefalutTitle(graphInfo);
			}
			else {
				//graphService.setXYDefalutTitle(graphInfo);
				graphInfo.getGraphProperties().getxAxisProperties().getxAxisTitleTrendProperties().setTitle(graphInfo.getGraphProperties().getxAxisProperties().getxAxisTitleTrendProperties().getTitle());
				graphInfo.getGraphProperties().getyAxisProperties().getyAxisTitleTrendProperties().setTitle(graphInfo.getGraphProperties().getyAxisProperties().getyAxisTitleTrendProperties().getTitle());
			}*/
			
			graphProperties = graphInfo.getGraphProperties();
			graphService.setFirstTime(false);
			map.put("isAdaptive", "true");
			if(request == null || request.getHeader("User-Agent") == null || !request.getHeader("User-Agent").contains("Mobile")) {
				boolean isAdaptive = graphProperties.getAdaptiveBehaviour();
				if(GeneralUtil.getNonAdaptiveFixedWidth(isAdaptive) != -1) {
					isAdaptive = false;
				} else {
					isAdaptive = true;
				}
				String strAdaptive = graphService.getAdaptiveBehaviour();
				if(!strAdaptive.equals(""+isAdaptive)) {
					graphService.setAdaptiveBehaviour(""+isAdaptive);				
				}
				map.put("isAdaptive", ""+isAdaptive);
			}
		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_REFRESH_GRAPH",
					new Object[] {userInfo.getUsername(), getObjectDisplayName()}), e);
		} catch (ALSException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_REFRESH_GRAPH",
					new Object[] {userInfo.getUsername(), getObjectDisplayName()}), e);
		} 
		/*try {
			if (graphInfo != null) {
				graphService.setGraphImages(graphService.pushGraphImage(graphInfo, ""));
    		}
		} catch (IOException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_GRAPH_GENERATE_IMAGE",
					new Object[] { userInfo.getUsername(), getObjectDisplayName() }), e);
		}*/
		params = new HashMap<String, String>();

		params.put("sCmd", AppMainCommandList.NEW_GRAPH.getM_strCommandName());
		params.put("firstTime" , "true");
		params.put("currentTimeStamp", currentTimeStamp + "");

		generateRequiredItemsForGraph(map, params);

		if (graphInfo.getDrilldownBreadcrumbMap() != null && graphInfo.getDrilldownBreadcrumbMap().size() > 0) {
			map.put("drilldownBreadCrumb", graphInfo.getDrilldownBreadcrumbMap());

			map.put("drillUpLinkToOneLevel", graphInfo.getDrillUpLinkToOneLevel());
		}
		/*if (graphInfo.getGraphPanel() instanceof GaugeExPanel) {
			map.put("isGaugeGraph", true);
		} else {
			map.put("isGaugeGraph", false);	
		}
		*/
		if (graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE) {
            map.put("isGaugeGraph", true);
        } else {
            map.put("isGaugeGraph", false);
        } 
		showAppliedFilter(map, userInfo, strObjectId);// Check whether filter is applied or not.
		map.put("currentTimeStamp", currentTimeStamp);
		map.put("graphInfo", graphInfo);
		map.put("graphType", graphInfo.getGraphType());
		map.put("graphProperties", graphProperties);
		String[] jsonArr = new String[2];
		boolean d3Graph = false;
		if(graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH)
		{
			d3Graph = true;
			String d3ScatterJson = graphService.generateD3DataProvider(graphInfo, userInfo);
			/*ApplicationLog.debug(d3ScatterJson);*/
			map.put("jsonData",d3ScatterJson);	
			map.put("chartSize", 1);
		}
		else
		{	
			//amcharts plotting start
			long start = System.currentTimeMillis();
			jsonArr =  graphService.generateGraph(graphInfo,"",false,userInfo);
			ApplicationLog.debug("generateGraph  Time == >> "+(System.currentTimeMillis()-start));
			
			if(jsonArr.length > 0 && jsonArr[0]==null)
				jsonArr[0]="1";
			map.put("jsonData",jsonArr[0]);		
			map.put("chartSize", jsonArr[1]);
		//-----------------added by krishna start
		}
		map.put("d3Graph",d3Graph);
		map.put("gaugeLegendInfo", graphInfo.getGaugeData());
		map.put("completeGraphData",graphInfo.getGraphData().isCompleteGraphData());
		//-----------------added by krishna start
		boolean isROutLinerdispaly = false;
		if(graphInfo.getCubeInfo().getDataObjectType() == ICubeConstants.REALTIME_R_CUBE) {
			if(graphService.getrScriptInputVOs() != null && graphService.getrScriptInputVOs().size() > 0) {
				isROutLinerdispaly = true;
			}
		}
		map.put("isROutLinerdispaly", isROutLinerdispaly);
		
		/*for bug 11534*/
		if(graphInfo.getGraphData().isFromAnalysis())
		{
			map.put("fromAnalysis",true);
			map.put("analysisGraphInfo", graphInfo);//Added for Bug #12480
		}
		else
		{	
			map.put("fromAnalysis",false);		
		}
		/*for bug 11534*/
		
		int noOfChartsInRow = 0;
		if(graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
		{
			noOfChartsInRow =  graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getNoOfGauge();
			map.put("titledist", graphInfo.getGraphProperties().getGaugeTitleProperties().getDistanceFromCenter());
		}
		else if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
		{
			if(graphInfo.getGraphProperties().getPieGraph().isClustered())
			{
				noOfChartsInRow = Integer.parseInt(jsonArr[1]);
			}
			else
			{	
				noOfChartsInRow =  graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getNumberofpie();
			}
			map.put("pietitle", graphInfo.getTitleData());
			map.put("nestedgraph", graphInfo.getGraphProperties().getPieGraph().isClustered());
			map.put("pieDrillMap", graphInfo.getGraphData().getPieDrillMap());//Added for NeGD feature request 15075 of Pie drill on Dashboard (24 July 2019)
		}
		
		if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH ||	graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
		{
			if(jsonArr[1] == "1")
			{
				map.put("noOfChartsInRow", jsonArr[1]);
			}
			else
			{	
				map.put("noOfChartsInRow", noOfChartsInRow);
			}	
		}
		else
		{
			map.put("nestedgraph", "false");
			map.put("noOfChartsInRow", jsonArr[1]);
		}
		boolean emptyRow = null !=graphInfo.getRowColumns() && (graphInfo.getRowColumns().isEmpty() || graphInfo.getRowColumns().get(0).equals("")) ;//
		boolean emptyCol = null != graphInfo.getColColumns() &&(graphInfo.getColColumns().isEmpty()|| graphInfo.getColColumns().get(0).equals(""));
		/*if(check && ckeck) {
			values.add("");
			columnMap.put("", values);
		}*/
		boolean isMultiMeasurePie = emptyRow && emptyCol;
		map.put("isMultiMeasurePie", isMultiMeasurePie);
		/*request.setAttribute("isMultiMeasurePieWithReq", isMultiMeasurePie);
		request.*/
		//-----------------added by krishna end
		
		if (graphProperties.getTitleProperties().isTitleVisible()) {
			HashtableEx ddvmList = graphService.getActiveDDVMs(graphInfo, userInfo.getUserId(), false);
			try {
				graphService.setObjectPageTitle(graphInfo.getGraphId(),graphInfo
						.getActiveFilterInfo(userInfo.getUserId()),
						graphService.getPageFilterNew(graphInfo),
						graphService.getActiveVariableMap(), graphService.getResultSetMetaData(),
						(IDataObject)graphInfo.getCubeInfo(), graphProperties.getTitleProperties(),
						userInfo, ddvmList);
			} catch (CubeException e) {
				ApplicationLog.error(e);
			}
				
				String titleForObject = CubeUtil.replaceGlobalVariableInTitle(
				graphInfo.getGraphProperties().getTitleProperties().getGeneratedTitle(),
				graphInfo.getActiveGlobalVariableInfo(userInfo.getUsername()),
				graphInfo.getActiveTemplateProperties().getActiveWhatifVariableInfo(),userInfo);
				
				graphInfo.getGraphProperties().getTitleProperties().setGeneratedTitle(titleForObject);
			
		}
		boolean isOnLoadInfo = false;
		if(graphInfo.getOnLoadObjectInfo() != null && !graphInfo.getOnLoadObjectInfo().isEmpty()) {
			if(!graphInfo.getOnLoadObjectInfo().get(0).isEmpty() || !graphInfo.getOnLoadObjectInfo().get(1).isEmpty()) {
				isOnLoadInfo = true;
			}
		}
		map.put("isOnLoadInfo", isOnLoadInfo);
		//below code is commented as it seems not necessary here,and it was giving error on in DB[discussed with cp to remove it][26/3/2019]
		/*Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
		if(propertyMap != null) {
			@SuppressWarnings("rawtypes")
			Iterator itr = propertyMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				map.put(key, propertyMap.get(key));
			}
		}*/
		
		//amcharts plotting end
		//auditUserActionLog("refresh Graph Object Data");
		auditUserActionLog(ResourceManager.getString("LBL_REFRESH_GRAPH_OBJECT_DATA"), AppConstants.DETAIL, userInfo);

		/*if(graphInfo.getGraphMode() == AppConstants.NEW_MODE) {
			map.addAttribute("showToolbarForNew", true);
			return new ModelAndView("graph");
		}else {
			map.addAttribute("showToolbarForNew", true);
			return new ModelAndView("graph/graph");
		}*/
		
		return new ModelAndView("graph/graph");
		
	}

	private void generateRequiredItemsForGraph(ModelMap map, Map<String, String> params) {
		
		try {
			GraphService graphService=this.graphService;
			GraphInfo graphInfo=this.graphInfo;
			String objectId=null;
			if(map.get("strObjectId")!=null) {
				objectId=map.get("strObjectId").toString();
			}
			if(objectId!=null && !objectId.isEmpty()) {
				graphInfo = (GraphInfo)getGraphObjectFromMap(objectId);
				graphService=(GraphService)getGraphServiceFromMap(objectId);
			}
			Map graphMap = graphService.generateRequiredItemsForGraph(graphInfo,params);
			map.put("legendDrilldownLinkMapForLineGrf", graphMap.get("legendDrilldownLinkMapForLineGrf"));
			map.put("lineGraphLegendItemsList", graphMap.get("lineGraphLegendItemsList"));

			map.put("imageMapData", graphMap.get("imageMapData"));
			map.put("graphcss", graphMap.get("graphcss"));
			map.put("legendItemsList", graphMap.get("legendItemsList"));
			map.put("legendDrilldownLinkMap",graphMap.get("legendDrilldownLinkMap"));
			map.put("isCombinedGraph", graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH ? true : false);
			map.put("isPie", graphInfo.getGraphType() == GraphConstants.PIE_GRAPH ? true : false);
			map.put("isScatter", graphInfo.getGraphType() == GraphConstants.SCATTER_LINE_GRAPH ? true : false);
			map.put("isBubble", graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH ? true : false);
			map.put("isCandle", graphInfo.getGraphType() == GraphConstants.CANDLE_STICK_GRAPH ? true : false);
			map.put("isHighLowOC", graphInfo.getGraphType() == GraphConstants.HIGH_LOW_OPEN_CLOSE_GRAPH ? true : false);
			map.put("isHeatmap", graphInfo.getGraphType() == GraphConstants.HEAT_MAP_GRAPH? true : false);
			map.put("totalObjects", graphMap.get("totalObjects"));
			if(graphInfo.getGraphType() == GraphConstants.DRILLED_RADAR_GRAPH || graphInfo.getGraphType() == GraphConstants.DRILLED_STACKED_RADAR_GRAPH)
				map.put("isRadar", true);
			map.put("isStackedBar", graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH ? true : false);
			if(graphInfo.getGraphData().getRowLabel() != null && !graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend") && graphInfo.getGraphData().getColLabel() != null && graphInfo.getDataColLabels3().size() == 1
					||(graphInfo.getGraphData().getRowLabel() != null && graphInfo.getGraphData().getRowLabel().equals("legend") && graphInfo.getDataColLabels3().size()>1))
				map.put("isClustered", true);
			/*if(graphInfo.getGraphData().getRowLabel() != null && !graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend") && graphInfo.getGraphData().getColLabel() != null && graphInfo.getDataColLabels3().size() == 1
					||(graphInfo.getGraphData().getRowLabel() != null && graphInfo.getGraphData().getRowLabel().equals("legend") && graphInfo.getDataColLabels3().size()>1))
				map.put("isClustered", true);*/
			//map.put("gaugeLegendInfo", graphMap.get("gaugeLegendInfo"));
			map.put("isYAxisVisible", graphMap.get("isYAxisVisible"));
			map.put("isXAxisVisible", graphMap.get("isXAxisVisible"));
			map.put("isGraphVisible", graphMap.get("isGraphVisible"));
			map.put("graphXAxisTitle", graphMap.get("graphXAxisTitle"));
			map.put("graphYAxisTitle", graphMap.get("graphYAxisTitle"));
			map.put("graphYAxis2Title", graphMap.get("graphYAxis2Title"));
			map.put("graphLegendTitle", graphMap.get("graphLegendTitle"));
			map.put("graphLegend2Title", graphMap.get("graphLegend2Title"));
			map.put("errorMessage", graphMap.get("errorMessage"));
			
		} catch (Exception ex) {
			ApplicationLog.error(ex);
		}
	}

	

	//Not Needed To Set in graph
	/**
	 * Sets the Rank Label
	 * 
	 * @throws CubeException
	 * @throws ALSException
	 */
	@Override
	public void setRank() throws CubeException, ALSException {}

	@Override
	public Map<SelectItem, Integer> prepareCubeAllItemsMap(boolean isAddMeasure,UserInfo userInfo) throws CubeException {
		return graphService.prepareDimensionItemsMap(getCubeInfo(null).getDataObjectId(), userInfo,graphInfo);
	}

	@Override
	public List<ActiveFilterInfo> getActiveFilterInfo(UserInfo userInfo) {
		String id = "";
		if(userInfo.isAdmin()) {
			id = AppConstants.ADMIN_USERNAME;
		} else {
			id = userInfo.getUserId();
		}
		return graphInfo.getActiveFilterInfo(id);
	}

	@Override
	public CubeDataExpExecutor getDataExpressionExecutor() throws CubeException {

		return graphService.getDataExpressionExecutor();
	}

	@Override
	public PDFPageSetupInfo getPdfPageSetUpInfo(HttpServletRequest request) {
		PDFPageSetupInfo info;
		if(graphInfo != null){
			info =  graphInfo.getPdfPageSetup();
			if(info == null){
				info = new PDFPageSetupInfo();
			}
		} else {
			info = new PDFPageSetupInfo();
		}
		return info;
	}

	public GraphInfo getGraphInfo() {
		return graphInfo;
	}

	/**
	 * This method is use to set outliner Data.
	 * @param request
	 * @return Message String
	 */
	@RequestMapping (value = "/setOutliner")
	@ResponseBody
	public Object setOutliner(HttpServletRequest request, ModelMap map, @LoggedInUser UserInfo userInfo, HttpServletResponse response) {
		
		try {
			HashMap<String, String> requestParamMap = new HashMap<String, String>();
			Enumeration<String> requestEnum = request.getParameterNames();
			String graphId=graphInfo.getGraphId();
			if(graphInfo.getGraphMode() == AppConstants.NEW_MODE) {
				graphId=graphInfo.getNewGraphId()+AppConstants.GRAPH_FILE_EXT;
			}
			detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphId,"Outliner",null,Thread.currentThread(),userInfo,new Date());
			while (requestEnum.hasMoreElements()) {
				String paramName = requestEnum.nextElement();
				String paramValue = StringUtil.null2String(request.getParameter(paramName));

				requestParamMap.put(paramName, paramValue);
			}
			graphInfo.setFromOutlinerSubmit(true);
			//lovlist alternative for color start
			graphInfo.setLovListForColor(new ArrayList());
			//cmb
			graphInfo.setLovListForColorBar(new ArrayList());
			graphInfo.setLovListForColorLine(new ArrayList());
			
			graphInfo.setLovListForColorUnique(new ArrayList());
			//cmb
			graphInfo.setLovListForColorBarUnique(new ArrayList());
			graphInfo.setLovListForColorLineUnique(new ArrayList());
			//lovlist alternative for color end	
			graphInfo.getGraphProperties().setLegendCustomValueList(new ArrayList());
			graphInfo.getGraphProperties().setCustomLegendSelectedValueList(new ArrayList());
			
			if (graphInfo.getGraphMode() == AppConstants.NEW_MODE && requestParamMap.get("outlinerMode").toString().equals("1")) {			
				String strRecords =  StringUtil.null2String(request.getParameter("limitedrecord"));
				if(strRecords != null && !strRecords.equals("")) {
					graphInfo.setRecordlimit(Boolean.valueOf(strRecords));
				}
			}
			
			String windowScreenWidth = request.getParameter("windowScreenWidth");
			String windowScreenHeight = request.getParameter("windowScreenHeight");
			try
			{
				if (!"".equals(windowScreenWidth) && !"".equals(windowScreenHeight))
				{
					graphInfo.setWindowScreenWidth(Integer.parseInt(windowScreenWidth));
					graphInfo.setWindowScreenHeight(Integer.parseInt(windowScreenHeight));
				}
			}
			catch(Exception e)
			{
				graphInfo.setWindowScreenWidth(0);
				graphInfo.setWindowScreenHeight(0);
			}
			
			String selectedValues = request.getParameter("selectedValues");
	        Map<String, String> selectedValuesMap = new HashMap<>();
	        if (selectedValues != null && !selectedValues.isEmpty()) {
	            for (String pair : selectedValues.split(" && ")) {
	                String[] keyValue = pair.split("\\|\\|");
	                if (!keyValue[1].equals("null") && keyValue.length == 2 && Integer.parseInt(keyValue[1].trim()) != 0) {
	                    selectedValuesMap.put(keyValue[0].trim(), KPIConstants.Frequency.getCorrespondingValue(Integer.parseInt(keyValue[1].trim())).toString());
	                }
	            }
	        }
	        graphInfo.setDateFrequencyMap(selectedValuesMap);
			graphService.setPageRowColDataItems(requestParamMap, graphInfo, userInfo);
			graphService.doGraphChanged(graphInfo.getGraphType(), false, graphInfo, userInfo);
			if (graphInfo.getGraphMode() == AppConstants.NEW_MODE) {
				graphService.setDefaultSorting(graphInfo,userInfo);
				graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().setDateFormat(userInfo.getDateFormat());
				setDefaultTimeFormatForProperties(graphInfo, userInfo);
			}
			if(requestParamMap.get("outlinerMode").toString().equals("1")) {
			if (graphService.getObjectMode() == AppConstants.NEW_MODE) {
				GeneralConfigurationInfo generalConfigurationInfo = generalConfigurationServiceUtil.getGeneralConfigurationInfo();
				String mapValue = generalConfigurationInfo.getDefaultGraphProperties();
				if(mapValue != null && !mapValue.equalsIgnoreCase("-1")) {
					graphService.copyTheme(mapValue,graphInfo);
				}
			}
			}
			
		} catch (MDXException | RealTimeCubeException e) {
			String objectMode = (graphService.getObjectMode() == AppConstants.NEW_MODE ? "New" : "Open");
			
			String outlinerMode = request.getParameter("outlinerMode");
			if(outlinerMode != null && outlinerMode.equals("1")){
				ApplicationLog.error(ResourceManager.getString(
						"LOG_ERROR_MSG_FAILED_TO_SET_OUTLINER", new Object[] {
								objectTypeName, objectMode, getObjectDisplayName(),
								userInfo.getUsername() }), e);
				return ResourceManager.getString("ERROR_MSG_FAILED_TO_SET_OUTLINER") + " " + e.getMessage();
			} else {
				
				ApplicationLog.error(ResourceManager.getString(
						"LOG_ERROR_MSG_FAILED_TO_SET_OUTLINER", new Object[] {
								objectTypeName, objectMode, getObjectDisplayName(),
								userInfo.getUsername() }), e);
				return ResourceManager.getString("ERROR_MSG_FAILED_TO_SET_OUTLINER_EDIT_MODE") + " " + e.getMessage();
			}
		} catch (ALSException e) {
			return e.getLocalizedMessage();
		}catch (Exception e) {
			String objectMode = (graphService.getObjectMode() == AppConstants.NEW_MODE ? "New" : "Open");
			
			String outlinerMode = request.getParameter("outlinerMode");
			if(outlinerMode != null && outlinerMode.equals("1")){
				ApplicationLog.error(ResourceManager.getString(
						"LOG_ERROR_MSG_FAILED_TO_SET_OUTLINER", new Object[] {
								objectTypeName, objectMode, getObjectDisplayName(),
								userInfo.getUsername() }), e);
				return ResourceManager.getString("ERROR_MSG_FAILED_TO_SET_OUTLINER");
			} else {
				
				ApplicationLog.error(ResourceManager.getString(
						"LOG_ERROR_MSG_FAILED_TO_SET_OUTLINER", new Object[] {
								objectTypeName, objectMode, getObjectDisplayName(),
								userInfo.getUsername() }), e);
				return ResourceManager.getString("ERROR_MSG_FAILED_TO_SET_OUTLINER_EDIT_MODE");
			}
		}

			GraphProperties graphProperties = graphInfo.getGraphProperties();

			if (graphProperties.getTitleProperties().isTitleVisible()) {
				HashtableEx ddvmList = graphService.getActiveDDVMs(graphInfo, userInfo.getUserId(), false);

				try {
					graphService.setObjectPageTitle(graphInfo.getGraphId(),graphInfo
							.getActiveFilterInfo(userInfo.getUserId()),
							graphService.getPageFilterNew(graphInfo),
							graphService.getActiveVariableMap(), graphService.getResultSetMetaData(),
							(IDataObject)graphInfo.getCubeInfo(), graphProperties.getTitleProperties(),
							userInfo, ddvmList);
				} catch (CubeException e) {
					ApplicationLog.error(ResourceManager.getString(
							"LOG_ERROR_MSG_FAILED_SET_VARIABLE_IN_TITLE",
							new Object[] {
									objectTypeName,
									graphInfo.getGraphName(),
											userInfo.getUsername() }), e);

					return ResourceManager.getString("ERROR_MSG_FAILED_TO_SET_VARIABLE_IN_GRAPH_TITLE",new Object[] {e.getMessage()});
				}
			}
	
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_OUTLINER"), AppConstants.DETAIL,userInfo);
		response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		try {
			recentlyUsedServiceUtil.saveRecentlyUsed(graphInfo.getCubeInfo().getId(),graphInfo.getCubeInfo().getDataObjecName(), userInfo, AppConstants.DATASETS,1);
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		}
		changeAppliedFiltersFlags(request, userInfo, response, map);
		map.put("requireRefresh", true);
		return refreshObjectData(null,response, userInfo, map);	
	}

	@ResponseBody
	@RequestMapping (value = "/generateGraphImage", produces = {"image/jpg", "image/png", "image/svg+xml"}, headers = "Accept=*/*")
	public byte[] generateGraphImage(@RequestParam(value="imageId") String imageId, @LoggedInUser UserInfo userInfo) {
		byte[] imageData = null;
    	/*try {
    		if (graphService.getGraphImages() == null || graphService.getGraphImages().isEmpty()) {
    			graphService.setGraphImages(graphService.pushGraphImage(graphInfo, ""));
    		} 
    		imageData = graphService.getGraphImages().get(imageId);
		} catch (IOException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_GRAPH_GENERATE_IMAGE",
					new Object[] { userInfo.getUsername(), getObjectDisplayName() }), e);
		}*/
		auditUserActionLog(ResourceManager.getString("LBL_GENERATE_GRAPH_IMAGES"), AppConstants.DETAIL,userInfo);
    	return imageData;
	}
	
	/**
	 * Close and Apply UDDC in Graph
	 * 
	 * @return 'Success' if oparation is successfull otherwise error message.
	 */
	@RequestMapping (value = "/closeUDDC")
	@ResponseBody
	public Object closeUDDC(HttpSession session, @LoggedInUser UserInfo userInfo, HttpServletResponse response, ModelMap map) {
		
		Object status = "";
		try {
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"UDDC","Set UDDC",Thread.currentThread(),userInfo,null);
		List<ActiveUDDCInfo> activeUDDCList = graphInfo.getActiveUDDCInfo(userInfo.getUserId());
		List<UddcTemplateInfo> activeUddcInfoList = new ArrayList<>();
		for (int i = 0; i < activeUDDCList.size(); i ++) {
			activeUddcInfoList.add((UddcTemplateInfo) activeUDDCList.get(i).getUddcTemplateInfo());
		}
		List<String> activeGlobalVarNames = new ArrayList<>();
		List<ActiveGlobalVariableInfo> activeGolbalVariableList = graphInfo.getActiveTemplateProperties().getActiveGlobalVariableInfo(userInfo.getUserId());
		if(activeGolbalVariableList != null && !activeGolbalVariableList.isEmpty()){
			for (ActiveGlobalVariableInfo activeGlobalVariableInfo : activeGolbalVariableList) {
				activeGlobalVarNames.add(activeGlobalVariableInfo.getGlobalVariableInfo().getGlobalVariableName());
			}
		}
		Vector vecData = cubeMetadataServiceUtil.getDimensionColumnVector(graphInfo.getCubeInfo(), userInfo,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
		Vector vKeyLabels = (Vector) vecData.elementAt(1);
		Vector vDataLabels = (Vector) vecData.elementAt(2);
		
		Vector vTimeDimeDataLavel = (Vector) vecData.elementAt(4);
		Vector<String> uddcMeasureList = new Vector<>(vDataLabels);
		 List<UddcTemplateInfo> uddcTemplateInfoList = uddcServiceUtil.getUddcTemplateListByUserId(graphInfo.getCubeInfo().getDataObjectId(), userInfo.getUserId());
	
		for (UddcTemplateInfo uinfo : uddcTemplateInfoList) {
			uddcMeasureList.add(uinfo.getTemplateName());
		}
		uddcMeasureList.addAll(vKeyLabels);
		uddcMeasureList.addAll(vTimeDimeDataLavel);
		Vector<String> validActiveUddcList = cubeMetadataServiceUtil.getValidUDDCNameList(activeUddcInfoList, userInfo.getUserId(), uddcMeasureList, activeGlobalVarNames, uddcTemplateInfoList);
		List<ActiveUDDCInfo> validActiveUddcVect = new ArrayList<>();
		for (int i = 0; i < activeUDDCList.size(); i++) {
			UddcTemplateInfo uddcTemplateInfo =   activeUDDCList.get(i).getUddcTemplateInfo();
			if (validActiveUddcList.contains(uddcTemplateInfo.getTemplateName())) {
				validActiveUddcVect.add(activeUDDCList.get(i));
			}
		}
		activeUDDCList = validActiveUddcVect;
		
		Object[] activeTemplateIds = new Object[activeUDDCList.size()];
		for (int cnt = 0; cnt < activeUDDCList.size(); cnt++) {
			ActiveUDDCInfo activeuddcInfo = activeUDDCList.get(cnt);
			activeTemplateIds[cnt] = activeuddcInfo.getUddcTemplateInfo();
		}
		
			graphService.setActiveTemplates(TemplateUtil.ANA_USER_UDDC,
					activeTemplateIds, graphInfo, userInfo.getUserId(), false);
			graphService.reArrengeDataItems(activeUDDCList,graphInfo,userInfo);
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			//graphInfo.setFromCloseUDDC(true);
			status = refreshObjectData(null,response, userInfo, map);
		} catch (CubeException | RScriptException e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_APPLY_UDDC", new Object[] {
							userInfo.getUsername(), getObjectDisplayName() }),
					e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_APPLY");
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_APPLY_UDDC", new Object[] {
							userInfo.getUsername(), getObjectDisplayName() }),
					e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_APPLY");
		} 
		session.removeAttribute("uddcTemplateTable");
		session.removeAttribute("activeUddcTemplate");
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_UDDC"), AppConstants.DETAIL,userInfo);
		return status;
	}
	
	/**
	 * Close and Apply UDHC in Graph
	 * 
	 * @return 'Success' if operation is successfully otherwise error message.
	 */
	@RequestMapping (value = "/closeUDHC")
	@ResponseBody
	public Object closeUDHC(HttpSession session, @LoggedInUser UserInfo userInfo, HttpServletResponse response, ModelMap map) {
		
		Object status = "";
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"UDHC","Set UDHC",Thread.currentThread(),userInfo,null);
		List<ActiveUDHCInfo> activeUDHCList = graphInfo.getActiveUDHCInfo(userInfo.getUserId());
		Object[] activeTemplateIds = new Object[activeUDHCList.size()];
		for (int cnt = 0; cnt < activeUDHCList.size(); cnt++) {
			ActiveUDHCInfo udhcInfo = activeUDHCList.get(cnt);
			activeTemplateIds[cnt] = TemplateUtil.getDataLabelVector(udhcInfo, null);
		}
		try {
			graphService.setActiveTemplates(TemplateUtil.ANA_USER_UDHC, activeTemplateIds, graphInfo, userInfo.getUserId(), false);
			
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			status = refreshObjectData(null,response, userInfo, map);
		} catch (CubeException | RScriptException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_APPLY_UDHC", new Object[]{userInfo.getUsername(), getObjectDisplayName()}), e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_APPLY");
		}
		session.removeAttribute("udhcTemplateTable");
		session.removeAttribute("activeUdhcTemplate");
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_UDHC"), AppConstants.DETAIL,userInfo);
		return status;
	}
	
	/**
	 * Close and Apply Data Display Value in Graph
	 * 
	 * @return 'Success' if oparation is successfull otherwise error message.
	 */
	@RequestMapping (value = "/closeDDVM")
	@ResponseBody
	public Object closeDDVM(HttpSession session, @LoggedInUser UserInfo userInfo, HttpServletResponse response, ModelMap map) {
		Object status = "";
		try {
			detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Data display value","Apply data display value",Thread.currentThread(),userInfo,null);
			graphService.setActiveTemplates(TemplateUtil.ANALYSIS_VALUEMAP, null, graphInfo, userInfo.getUserId(), false);
			
			if (graphInfo.getGraphProperties().getTitleProperties().isTitleVisible()) {
				HashtableEx ddvmList = graphService.getActiveDDVMs(graphInfo, userInfo.getUserId(), false);

				try {
					graphService.setObjectPageTitle(graphInfo.getGraphId(),graphInfo
							.getActiveFilterInfo(userInfo.getUserId()),
							graphService.getPageFilterNew(graphInfo),
							graphService.getActiveVariableMap(), graphService.getResultSetMetaData(),
							(IDataObject)graphInfo.getCubeInfo(), graphInfo.getGraphProperties().getTitleProperties(),
							userInfo, ddvmList);
				} catch (CubeException e) {
					ApplicationLog.error(e);
				}
			}
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			status = refreshObjectData(null,response, userInfo, map);
		} catch (CubeException | RScriptException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_APPLY_DDVM", new Object[] {getObjectDisplayName(),
					userInfo.getUsername() }), e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_APPLY");
		}
		session.removeAttribute("ddvmTemplateTable");
		session.removeAttribute("activeDDVMTemplate");
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_DDVM"), AppConstants.DETAIL,userInfo);
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),null,"Create Object view",Thread.currentThread(),userInfo,new Date());
		return status;
	}
	
	@RequestMapping (value = "/viewGraphProperties")	
	public ModelAndView showGraphProperties(ModelMap modelMap, @LoggedInUser UserInfo userInfo)	{	

		Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
		if(propertyMap != null) {
			@SuppressWarnings("rawtypes")
			Iterator itr = propertyMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				modelMap.put(key, propertyMap.get(key));
			}
		}
		modelMap.put("gpdCurrentTabName", com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GENERAL);
		modelMap.put("measureCurrentTabName", "M"+0);
		boolean d3Graph = false;
		if(graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH)
			d3Graph = true;
		modelMap.put("d3Graph", d3Graph);
		if(isFromSmarten())
			modelMap.put("isFromSmarten", true);
		String visibleGraphs = "";
		Map graphsVisibleMap = graphInfo.getGraphProperties().getGraphsVisibleMap();
		// 9 Apr changes [p.p] Bug #14983 [p.p]
	if(graphsVisibleMap != null)
	{

		for(int i=0;i<graphInfo.getDataColLabels3().size();i++)
		{
			if(graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i).toString()) != null && graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i).toString()).toString().equals("true"))
				visibleGraphs = visibleGraphs + graphInfo.getDataColLabels3().get(i) + ","; 
		}
		if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
		{
		
			for(int i=0;i<graphInfo.getTheDataColLabels4().size();i++)
			{
				if(graphsVisibleMap.get(graphInfo.getTheDataColLabels4().get(i).toString()) != null && graphsVisibleMap.get(graphInfo.getTheDataColLabels4().get(i).toString()).toString().equals("true"))
					visibleGraphs = visibleGraphs + graphInfo.getTheDataColLabels4().get(i) + ","; 
			}
		
		}
		/*commented for SDEVAPR20-395 
		if(graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
		{
			visibleGraphs = "";
			for(int i=0;i<graphInfo.getDataColumnInfoList().size();i++)//Added for Bug 15574
			{
				if(graphsVisibleMap.get(graphInfo.getDataColumnInfoList().get(i).toString()) != null && graphsVisibleMap.get(graphInfo.getDataColumnInfoList().get(i).toString()).toString().equals("true"))
					visibleGraphs = visibleGraphs + graphInfo.getDataColumnInfoList().get(i) + ","; 
			}
		
		}*/
	}
	// 9 Apr changes [p.p]
		if(visibleGraphs.endsWith(","))
			visibleGraphs = visibleGraphs.substring(0, visibleGraphs.length() - 1);
		
		List<String> partitionCols =new ArrayList<>();
		List<String> metaDataLst=new ArrayList<>();
		try {
			if(graphService.getObjectusedcolumnList()!=null && !graphService.getObjectusedcolumnList().isEmpty()) {
				metaDataLst = graphService.getObjectusedcolumnList();
			}else {
				metaDataLst = graphService.getSubCubeMetadataList();
			}
		} catch (CubeException e) {
			ApplicationLog.error(e);
		}
		setPartitionColumns(modelMap, graphInfo.getCubeInfo().getDataObjectColumnInfoList(), metaDataLst, graphInfo.getPartitionBy());
		modelMap.put("visibleGraphs", visibleGraphs);
		return new ModelAndView("graphProperties");
	}
	
	@RequestMapping(value = "/saveGraphProperties", method=RequestMethod.POST)
	@ResponseBody
	public Object saveGraphProperties(@ModelAttribute GraphProperties graphProperties,
			@RequestParam(required = false, value="gpdSelectedTabNames") String selectedTabNames,
			@RequestParam(required = false, value="gpdNextTabName") String nextTabName,
			@RequestParam(required = false, value="editByCreator") String editByCreator,
			@RequestParam(required = false, value="measureSelectedTabNames") String measureSelectedTabNames,
			@RequestParam(required = false, value="measureNextTabName") String measureNextTabName,
			@RequestParam(required = false, value="showHideGraphStr", defaultValue = "") String showHideGraphStr,
			@RequestParam(value = "partitionBy", required= false, defaultValue = "") String partitionBy,
			@RequestParam(value="valueLegendList",required=false,defaultValue="") String strValues, 
			ModelMap map, HttpServletResponse response, @LoggedInUser UserInfo userInfo) {
		    
		List barcolList=graphInfo.getGraphData().getCmbBarcolList();
		 List barrowList=graphInfo.getGraphData().getCmbBarrowList();
		 List linerowList=graphInfo.getGraphData().getCmbLinerowList();
		 List selectedValues = new ArrayList<>();
		 int barcolsize = barcolList.size();
		 int barrowsize = barrowList.size();
		 int linerowsize = linerowList.size();
		 int linecolsize = 0;
		 graphInfo.setPartitionBy(partitionBy);
		 detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Graph Properties","Apply properties",Thread.currentThread(),userInfo,null);
		 //Added below IF condition to handle Dial Gauge properties dialog error [23 Nov 2020]
		 if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH && null != graphInfo.getGraphData().getCmbLinecolLabel())
		 {
			 linecolsize = graphInfo.getGraphData().getCmbLinecolLabel().length();
		 }//Dial Gauge properties dialog error code endggg
		 
		if(isFromSmarten())
			map.put("isFromSmarten",true);
		try {
			List<String> vDimension = cubeMetadataServiceUtil.getDimensionColumns(graphInfo.getCubeInfo(), userInfo,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
			map.put("dimensionList",StringUtils.join(vDimension,','));
		} catch (CubeException e) {
			ApplicationLog.error(e);
			return false;
		}
		
		if (nextTabName.equals("submit")) 
		{
			/*	below checks are used to solve a problem :
			 * Problem : modelAttribute of Graph properties will apply default properties, if user doesn't click on the property tab.
			 */

			GraphProperties serverGraphProperties = graphInfo.getGraphProperties();
			//11939
				
			String[] strValueList = null;
			List<String> rowList = new ArrayList<>(graphInfo.getGraphData().getRowList());
			if( null == rowList || rowList.isEmpty()) {
				rowList = new ArrayList<>(graphInfo.getGraphData().getColList());
			}
			if((null == graphInfo.getGraphData().getRowList() || graphInfo.getGraphData().getRowList().isEmpty() )&& graphInfo.getGraphType() == GraphConstants.PIE_GRAPH ) {
				Collections.sort(rowList);
			}
			if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH && graphInfo.getGraphData().getCmbBarrowList() != null && !graphInfo.getGraphData().getCmbBarrowList().isEmpty()) {
				rowList = new ArrayList<>(graphInfo.getGraphData().getCmbBarrowList());
			}
			if(strValues.contains(AppConstants.CUBE_CONDITION_FILTER_VALUE_SEPERATOR)) {
				if(!strValues.equals("")) {
				strValues = strValues.substring(0, strValues.length()-1);}
				strValueList = StringUtil.tokenize(strValues, AppConstants.CUBE_CONDITION_FILTER_VALUE_SEPERATOR,false);
			}else{
				if(!strValues.equals("")) {
				strValues = strValues.substring(0, strValues.length()-1);}
				strValueList = (new String[] {strValues});
			}
			if(null != strValueList[0] && (strValueList[0].equals(",")||strValueList[0].equals(""))) {
				strValueList = null;	
			}
			if (strValueList != null && strValueList.length > 0) {
				for (int i = 0; i < strValueList.length; i++) {
					strValueList[i] = StringUtil.decodeURL(strValueList[i]);
				}
			}
			 /*if (strValueList.length > 0 && strValueList[strValueList.length - 1].endsWith(",")) {
				 strValueList[strValueList.length - 1] = strValueList[strValueList.length - 1].substring(0, strValueList[strValueList.length - 1].length() - 1);
		        }*/
			 LinkedHashSet<String> resultSet = new LinkedHashSet<>();

		        if(null!= strValueList && !strValueList[0].equals("")) {
			        for (String value : strValueList) {
			            resultSet.add(value);
			        }
		        }
		        
		        resultSet.addAll(rowList);

		        
		        List<String> resultList = new ArrayList<>(resultSet);
		        if(resultList.equals(rowList) ) {
		        	resultList = new ArrayList<>();
                }
				if (serverGraphProperties.getLegendCustomValueList() != null
						&& !serverGraphProperties.getLegendCustomValueList().isEmpty()
						&& !rowList.equals(serverGraphProperties.getLegendCustomValueList()) && strValueList==null) {
					graphProperties
							.setLegendCustomValueList(serverGraphProperties.getLegendCustomValueList());
					graphProperties.setCustomLegendSelectedValueList(serverGraphProperties.getCustomLegendSelectedValueList());
				} else {
					if (null != strValueList)
						graphProperties.setCustomLegendSelectedValueList(new ArrayList<>(Arrays.asList(strValueList)));
					else
						graphProperties.setCustomLegendSelectedValueList(new ArrayList<>());
					// selectedValues = Arrays.asList(strValueList);
					graphProperties.setLegendCustomValueList(resultList);
					if (null != graphProperties.getLegendProperties().getLegendValuesProperties())
						graphProperties.getLegendProperties().getLegendValuesProperties()
								.setLegendCustomValueList(resultList);
				}
		/* graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().setLegendCustomValueList(graphProperties.getLegendCustomValueList());
		        graphInfo.getGraphProperties().setLegendCustomValueList(graphProperties.getLegendCustomValueList());
*/              // graphInfo.setLovListForColor(resultList);
			if(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH)
			{	
				graphInfo.getGraphProperties().setColorType(graphProperties.getLineColorType());
				graphInfo.getGraphProperties().setCustomColors(graphProperties.getLineCustomColors());
				graphInfo.getGraphProperties().setColor(graphProperties.getLinecolor());
			}
			
			//changes made by harsh on 17 dec
			
			//9 Apr 2019[show/hide graphs] add changes [p.p]
			if(showHideGraphStr == null)
				showHideGraphStr = "";
			if(	(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH)
					&& (graphInfo.getDataColLabels3().size() > 1 //Added for Bug #14886
					&& graphInfo.getGraphData().getRowLabel()!=null /*&& graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")*/)
					|| ((graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) // Bug #15037
						&& (graphInfo.getDataColLabels3().size() > 1 || graphInfo.getTheDataColLabels4().size() > 1) && (barcolsize <= 2 || barrowsize <= 2 || linerowsize <= 2 || linecolsize <= 2))) // changes [p.p] for combine graph hidden value.

			{
				Map graphsVisibleMap = serverGraphProperties.getGraphsVisibleMap();
				List<String> graphsVisibleList = new ArrayList<>();
			
				if (!showHideGraphStr.isEmpty()) {
					graphsVisibleList = Arrays.asList(showHideGraphStr.split("\\s*,\\s*"));
				}
				
				for(int i=0;i<graphInfo.getDataColLabels3().size();i++)//Changed from getDataColumns() to getDataColLabels3() for Jira Bug SDEVAPR20-79
				{/*if(false) {*/
					if (!graphsVisibleList.isEmpty()) {
						if (graphsVisibleList.contains(graphInfo.getDataColLabels3().get(i)))
							graphsVisibleMap.put(graphInfo.getDataColLabels3().get(i), true);
						else
							graphsVisibleMap.put(graphInfo.getDataColLabels3().get(i), false);
					}
					else {
						graphsVisibleMap.put(graphInfo.getDataColLabels3().get(i), true);
					}
				}/*}*/
				for(int j=0;j<graphInfo.getTheDataColLabels4().size();j++)
				{
					if (!graphsVisibleList.isEmpty()) {
						if (graphsVisibleList.contains(graphInfo.getTheDataColLabels4().get(j)))
							graphsVisibleMap.put(graphInfo.getTheDataColLabels4().get(j), true);
						else
							graphsVisibleMap.put(graphInfo.getTheDataColLabels4().get(j), false);
					} else {
						graphsVisibleMap.put(graphInfo.getTheDataColLabels4().get(j), true);
					}
				}
				graphProperties.setGraphsVisibleMap(graphsVisibleMap);
			}
			else
				graphProperties.setGraphsVisibleMap(graphInfo.getGraphProperties().getGraphsVisibleMap());
			//9 Apr 2019[show/hide graphs]
			
			graphProperties.setEditByCreator(Boolean.parseBoolean(editByCreator));

			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GENERAL))
			{
				graphProperties.setGeneralProperties(serverGraphProperties.getGeneralProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_TITLE))
			{
				graphProperties.setTitleProperties(serverGraphProperties.getTitleProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_AREA))
			{
				graphProperties.setGraphAreaProperties(serverGraphProperties.getGraphAreaProperties());
				graphProperties.setZoomType(serverGraphProperties.getZoomType());//Added to solve Bug #12119
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_PIE_TITLE))
			{
				graphProperties.setPieTitle(serverGraphProperties.getPieTitle());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DOUGHNUT_TITLE))
			{
				graphProperties.setDoughnutTitleProperties(serverGraphProperties.getDoughnutTitleProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_X_AXIS)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_RADAR_AXIS))
			{
				graphProperties.setxAxisProperties(serverGraphProperties.getxAxisProperties());
			}
			/*if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_Y_AXIS)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_RADAR_SCALE))
			{*/
				//graphProperties.setyAxisProperties(serverGraphProperties.getyAxisProperties());
			
				Map<String, YaxisTrendProperties> yAxisMap = graphProperties.getyAxisPropertiesMap();
				Map<String, YaxisTrendProperties> yAxisServerMap = serverGraphProperties.getyAxisPropertiesMap();
				if (null != yAxisServerMap) {
					Object[] yAxisTrendServerProperties = yAxisServerMap.values().toArray();
					// This is to maintain default properties while we click on any other tabs except y-axis tab (Bug #10857)
					if (yAxisMap.size() != 0 && (selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_Y_AXIS))) {
						for (int i=0; i<yAxisServerMap.size(); i++) {
							if (i == 0) {
								
								 yAxisMap.remove("M"+i);
								 graphProperties.getyAxisProperties().setTabDisplayColumnName(((YaxisTrendProperties)yAxisTrendServerProperties[i]).getTabDisplayColumnName());
								 graphProperties.getyAxisProperties().setTabMesureName(((YaxisTrendProperties)yAxisTrendServerProperties[i]).getTabMesureName());
								// if(graphProperties.getyAxisPropertiesMap().get("M"+i).getLabelProperties().getAdjustedDigit()==999)
								 yAxisMap.put("M"+i, graphProperties.getyAxisProperties());
								 if(yAxisMap.get("M"+i).getLabelProperties().getAdjustedDigit()==999) {
							    		yAxisMap.get("M"+i).getLabelProperties().setAutoValue(true);	
							     }else {
							    		yAxisMap.get("M"+i).getLabelProperties().setAutoValue(false);
							    }
						    } else {
							if (null != yAxisMap.get("M" + i)) {
								if (yAxisMap.get("M" + i).getLabelProperties().getAdjustedDigit() == 999) {
									yAxisMap.get("M" + i).getLabelProperties().setAutoValue(true);
								} else {
									yAxisMap.get("M" + i).getLabelProperties().setAutoValue(false);
								}
								if (measureSelectedTabNames != null && !measureSelectedTabNames.contains("M" + i)) {
									yAxisMap.put("M" + i, (YaxisTrendProperties) yAxisTrendServerProperties[i]);
								} else {
									yAxisMap.get("M"+i).setTabDisplayColumnName(((YaxisTrendProperties)yAxisTrendServerProperties[i]).getTabDisplayColumnName());
									yAxisMap.get("M"+i).setTabMesureName(((YaxisTrendProperties)yAxisTrendServerProperties[i]).getTabMesureName());
								}
							}
						    }
						}
					} else {
						if (null != yAxisServerMap && yAxisServerMap.size() > 0) {
							yAxisServerMap.remove("M0");
							graphProperties.getyAxisProperties().setTabDisplayColumnName(((YaxisTrendProperties)yAxisTrendServerProperties[0]).getTabDisplayColumnName());
							graphProperties.getyAxisProperties().setTabMesureName(((YaxisTrendProperties)yAxisTrendServerProperties[0]).getTabMesureName());
							if (selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_RADAR_SCALE)) {
								yAxisServerMap.put("M0", graphProperties.getyAxisProperties());
							} else if (measureNextTabName.isEmpty()) {
								yAxisServerMap.put("M0", serverGraphProperties.getyAxisProperties());
							} else {
								yAxisServerMap.put("M0", graphProperties.getyAxisProperties());
								if(yAxisServerMap.get("M0").getLabelProperties().getAdjustedDigit()==999) {
									yAxisServerMap.get("M0").getLabelProperties().setAutoValue(true);
								}
								else {
									yAxisServerMap.get("M0").getLabelProperties().setAutoValue(false);
								}
							}
						}
						
						graphProperties.setyAxisPropertiesMap(yAxisServerMap);
					}
				}
			/*}*/
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_Y_LINE_BAR_AXIS))
			{
				graphProperties.setCombinedYaxisProperties(serverGraphProperties.getCombinedYaxisProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_STOCK_CONFIG)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_CANDAL_STICK))
			{
				graphProperties.setCandleStick(serverGraphProperties.getCandleStick());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GAUGE_TITLE_PROP))
			{
				graphProperties.setGaugeTitleProperties(serverGraphProperties.getGaugeTitleProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GAUGE_SCALE_PROP))
			{
				graphProperties.setGaugeScaleProperties(serverGraphProperties.getGaugeScaleProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GAUGE_THERMOMETER_PROP))
			{
				graphProperties.setThermometerGauge(serverGraphProperties.getThermometerGauge());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GAUGE_NEEDLE_PROP))
			{
				graphProperties.setGaugeNeedleProperties(serverGraphProperties.getGaugeNeedleProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GAUGE_DIAL_PROP))
			{
				graphProperties.setGaugeDialProperties(serverGraphProperties.getGaugeDialProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_GAUGE_LEGEND_PROP) 
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_LEGEND))
			{
				graphProperties.setLegendProperties(serverGraphProperties.getLegendProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_LEVEL_PROP))
			{
				graphProperties.setGaugeLevel(serverGraphProperties.getGaugeLevel());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DATAVALUE_PROP))
			{
				graphProperties.setGaugeDataValueScale(serverGraphProperties.getGaugeDataValueScale());
				graphProperties.setGaugeDataValueActual(serverGraphProperties.getGaugeDataValueActual());
				graphProperties.setGaugeDataValueTarget(serverGraphProperties.getGaugeDataValueTarget());
				graphProperties.setGaugeDataValueZone(serverGraphProperties.getGaugeDataValueZone());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_BUBBLE)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_COMB_CONFIG)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_VERTICAL_BAR))
			{
				graphProperties.setBarProperties(serverGraphProperties.getBarProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_COMB_CONFIG)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_LINE_PROP))
			{
				graphProperties.setGraphLineProperties(serverGraphProperties.getGraphLineProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_COMB_LEGEND))
			{
				graphProperties.getCombinedGraph().setBarLegendProperties(serverGraphProperties.getCombinedGraph().getBarLegendProperties());
				graphProperties.getCombinedGraph().setLineLegendProperties(serverGraphProperties.getCombinedGraph().getLineLegendProperties());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_RADAR))
			{
				graphProperties.setRadar(serverGraphProperties.getRadar());
				
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DOUGHNUT))
			{
				graphProperties.setDoughNutGraph(serverGraphProperties.getDoughNutGraph());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_PIE))
			{
				graphProperties.setPieGraph(serverGraphProperties.getPieGraph());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_AREA_PROP))
			{
				graphProperties.setGraphArea(serverGraphProperties.getGraphArea());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_HISTOGRAM))
			{
				graphProperties.setHistogram(serverGraphProperties.getHistogram());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_HEAT_MAP))
			{
				graphProperties.setHeatmap(serverGraphProperties.getHeatmap());
			}
			
			if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{
				Map<String, TrendDataValueProperties> dataValuePropertiesMap = graphProperties.getDataValuePropertiesMap();
				Map<String, TrendDataValueProperties> dataValueServerMap = serverGraphProperties.getDataValuePropertiesMap();
				if (selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DATA_VALUE))
				{
					if (null != dataValuePropertiesMap) {
						Object[] dataValuesServerProperties = dataValueServerMap.values().toArray();
						for (int i=0; i<dataValueServerMap.size(); i++) {
							if (i == 0) {
								dataValuePropertiesMap.remove("M"+i);
								graphProperties.getDataValueProperties().setTabDisplayColumnName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabDisplayColumnName());
								graphProperties.getDataValueProperties().setTabMesureName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabMesureName());
								dataValuePropertiesMap.put("M"+i, graphProperties.getDataValueProperties());
						    } else {
						    	if (measureSelectedTabNames != null && !measureSelectedTabNames.contains("M"+i)) {
						    		dataValuePropertiesMap.put("M"+i, (TrendDataValueProperties)dataValuesServerProperties[i]);
								} else {
									dataValuePropertiesMap.get("M"+i).setTabDisplayColumnName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabDisplayColumnName());
									dataValuePropertiesMap.get("M"+i).setTabMesureName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabMesureName());
								}
						    }
						}
						graphProperties.setDataValuePropertiesMap(dataValuePropertiesMap);
					}
					
				}
				else
				{
					Object[] dataValuesServerProperties = dataValueServerMap.values().toArray();
					for (int i=0; i<dataValueServerMap.size(); i++) {
						if (i == 0) {
							dataValuePropertiesMap.remove("M"+i);
							graphProperties.getDataValueProperties().setTabDisplayColumnName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabDisplayColumnName());
							graphProperties.getDataValueProperties().setTabMesureName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabMesureName());
							dataValuePropertiesMap.put("M"+i, dataValueServerMap.get("M"+i));
					    } else {
					    	if (measureSelectedTabNames != null && !measureSelectedTabNames.contains("M"+i)) {
					    		dataValuePropertiesMap.put("M"+i, (TrendDataValueProperties)dataValuesServerProperties[i]);
							} else {
								dataValuePropertiesMap.get("M"+i).setTabDisplayColumnName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabDisplayColumnName());
								dataValuePropertiesMap.get("M"+i).setTabMesureName(((TrendDataValueProperties)dataValuesServerProperties[i]).getTabMesureName());
							}
					    }
					}
					graphProperties.setDataValuePropertiesMap(dataValuePropertiesMap);
				}
				graphProperties.setDataValueProperties(serverGraphProperties.getDataValueProperties());
			}
			else
			{
				if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DATA_VALUE))
				{
					graphProperties.setDataValueProperties(serverGraphProperties.getDataValueProperties());
				}	
				else if(graphInfo.getGraphType()==GraphConstants.HEAT_MAP_GRAPH || graphInfo.getGraphType()==GraphConstants.CANDLE_STICK_GRAPH || graphInfo.getGraphType()==GraphConstants.HIGH_LOW_OPEN_CLOSE_GRAPH) {
					if(graphProperties.getDataValueProperties().getNumberFormat().getAdjustedDigit()==999) {
						graphProperties.getDataValueProperties().getNumberFormat().setAutovalue(true);
						
					}else {
						graphProperties.getDataValueProperties().getNumberFormat().setAutovalue(false);
					}
					
				}
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DATA_BAR_LINE_VALUE))
			{
				graphProperties.setCombinedDataValueProperties(serverGraphProperties.getCombinedDataValueProperties());
			}else if(selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DATA_BAR_LINE_VALUE)) {
				if(graphProperties.getCombinedDataValueProperties().getBarnumberFormat().getAdjustedDigit()==999) {
					graphProperties.getCombinedDataValueProperties().getBarnumberFormat().setAutovalue(true);
				}else {
					graphProperties.getCombinedDataValueProperties().getBarnumberFormat().setAutovalue(false);
				}
				if(graphProperties.getCombinedDataValueProperties().getLinenumberFormat().getAdjustedDigit()==999) {
					graphProperties.getCombinedDataValueProperties().getLinenumberFormat().setAutovalue(true);
				}else {
					graphProperties.getCombinedDataValueProperties().getLinenumberFormat().setAutovalue(false);
				}
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_COMBINED_REFERENCE_LINE))
			{
				graphProperties.setBarReferencelinePropertiesMap(serverGraphProperties.getBarReferencelinePropertiesMap());
				graphProperties.setLineReferencelinePropertiesMap(serverGraphProperties.getLineReferencelinePropertiesMap());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_COMBINED_TREND_LINE))
			{
				graphProperties.setBartrendlinePropertiesMap(serverGraphProperties.getBartrendlinePropertiesMap());
				graphProperties.setLinetrendlinePropertiesMap(serverGraphProperties.getLinetrendlinePropertiesMap());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_REFERENCE_LINE))
			{
				graphProperties.setReferencelinePropertiesMap(serverGraphProperties.getReferencelinePropertiesMap());
			}
			if (!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_TREND_LINE))
			{
				graphProperties.setTrendlinePropertiesMap(serverGraphProperties.getTrendlinePropertiesMap());
			}
			
			if(!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_BUBBLE)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_COMB_CONFIG)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_VERTICAL_BAR)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_DOUGHNUT)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_PIE)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_AREA_PROP)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_HISTOGRAM)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_RADAR)
					&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_HEAT_MAP))	
			{
				graphProperties.setCustomColors(serverGraphProperties.getCustomColors());
				graphProperties.setColorType(serverGraphProperties.getColorType());
				graphProperties.setSameColor(serverGraphProperties.isSameColor());
				graphProperties.setColor(serverGraphProperties.getColor());
				graphProperties.setTotalBarColor(serverGraphProperties.getTotalBarColor());
				graphProperties.setOtherBarColor(serverGraphProperties.getOtherBarColor());
				graphProperties.setTranceperancy(serverGraphProperties.getTranceperancy());
			}
			if(!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_HEAT_MAP))
			{
				graphProperties.setHeatmap(serverGraphProperties.getHeatmap());
			}
			if(!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_LINE_PROP)
				&& !selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_COMB_CONFIG))
			{
				graphProperties.setLineCustomColors(serverGraphProperties.getLineCustomColors());
				graphProperties.setPointCustomColors(serverGraphProperties.getPointCustomColors());
				
				graphProperties.setLineType(serverGraphProperties.getLineType());
				graphProperties.setPointType(serverGraphProperties.getPointType());
				
				graphProperties.setLineColorType(serverGraphProperties.getLineColorType());
				graphProperties.setPointColorType(serverGraphProperties.getPointColorType());
				
				graphProperties.setLinecolor(serverGraphProperties.getLinecolor());
				graphProperties.setPointcolor(serverGraphProperties.getPointcolor());
				
			}
			if(!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_BREADCRUM))
			{
				graphProperties.setBreadCrumProperties(serverGraphProperties.getBreadCrumProperties());
			}
			if(!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_COLUMN_LABELS)) {
				graphProperties.setColLabelsMap(serverGraphProperties.getColLabelsMap());
			}
			if(!selectedTabNames.contains(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_CHART_SELECTION)) {
				graphProperties.setSelectionFontProp(serverGraphProperties.getSelectionFontProp());
			}
			if(graphInfo.getGraphType().equals(GraphConstants.LINE_GRAPH) ||
					graphInfo.getGraphType().equals(GraphConstants.STACKED_LINE_GRAPH) ||
					graphInfo.getGraphType().equals(GraphConstants.PERCENTAGE_LINE_GRAPH) ||
					graphInfo.getGraphType().equals(GraphConstants.COMBINED_GRAPH))
			{
				if(graphProperties.getGraphLineProperties().getGraphlinepointPropertiesList() != null && graphProperties.getGraphLineProperties().getGraphlinepointPropertiesList().isEmpty())
					graphProperties.getGraphLineProperties().setAllLineCompatibility(true);	
			}
			
			graphProperties.setClickFromSave(true);
			graphProperties.getxAxisProperties().getLabelProperties().setDateFormat(StringUtil.unescapeHtmlUtil(graphProperties.getxAxisProperties().getLabelProperties().getDateFormat()));
			graphInfo.setGraphProperties(graphProperties);
			auditUserActionLog(ResourceManager.getString("LBL_SAVE_AND_APPLY_GRAPH_PROPERTIES"), AppConstants.DETAIL,userInfo);
			if (graphProperties.getTitleProperties().isTitleVisible()) {
				HashtableEx ddvmList = graphService.getActiveDDVMs(graphInfo, userInfo.getUserId(), false);
				try {
					graphService.setObjectPageTitle(graphInfo.getGraphId(),graphInfo
							.getActiveFilterInfo(userInfo.getUserId()),
							graphService.getPageFilterNew(graphInfo),
							graphService.getActiveVariableMap(), graphService.getResultSetMetaData(),
							(IDataObject)graphInfo.getCubeInfo(), graphInfo.getGraphProperties().getTitleProperties(),
							userInfo, ddvmList);
				} catch (CubeException e) {
					ApplicationLog.error(ResourceManager.getString(
							"LOG_ERROR_MSG_FAILED_SET_VARIABLE_IN_TITLE",
							new Object[] {
									objectTypeName,
									graphInfo.getGraphName(),
											userInfo.getUsername() }), e);

					return ResourceManager.getString("ERROR_MSG_FAILED_TO_SET_VARIABLE_IN_GRAPH_TITLE",new Object[] {e.getMessage()});
				}
			}

			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			return refreshObjectData(null,response, userInfo, map);
		}
		else
		{
			Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
			if(propertyMap != null) {
				@SuppressWarnings("rawtypes")
				Iterator itr = propertyMap.keySet().iterator();
				while (itr.hasNext()) {
					String key = (String) itr.next();
					map.put(key, propertyMap.get(key));
				}
			}
			map.put("currentTabName", nextTabName);
			if (nextTabName.equals(com.elegantjbi.service.graph.GraphConstants.GPD_GRAPH_Y_AXIS)) {
				if (null == measureNextTabName || measureNextTabName.isEmpty()) {
					GraphProperties gProp = (GraphProperties)map.get("graphProperties");
					gProp.setyAxisProperties(gProp.getyAxisPropertiesMap().get("M"+0));
					map.put("measureCurrentTabName", "M"+0);
				} 
			}
			boolean d3Graph = false;
			if(graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH)
				d3Graph = true;
			map.put("d3Graph", d3Graph);
			
			//9 Apr 2019[show/hide graphs] show selected measure[measure who's graph needs to be shown] changes [p.p]
			Map graphsVisibleMap = graphInfo.getGraphProperties().getGraphsVisibleMap();
			String visibleGraphs = "";
			String visibleLine = "";
			
			for(int i=0;i<graphInfo.getDataColLabels3().size();i++)//Changed from getDataColumns() to getDataColLabels3() for Jira Bug SDEVAPR20-79
			{
				if(graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i).toString()) != null && graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i).toString()).toString().equals("true"))
					visibleGraphs = visibleGraphs + graphInfo.getDataColLabels3().get(i) + ","; 
			}
			for(int j=0;j<graphInfo.getTheDataColLabels4().size();j++)
			{
				if(graphsVisibleMap.get(graphInfo.getTheDataColLabels4().get(j).toString()) != null && graphsVisibleMap.get(graphInfo.getTheDataColLabels4().get(j).toString()).toString().equals("true"))
					visibleLine = visibleLine + graphInfo.getTheDataColLabels4().get(j) + ","; 
			}
			graphInfo.getGraphProperties().getTitleProperties().setTitle(StringUtil.scriptEscape(graphInfo.getGraphProperties().getTitleProperties().getTitle()));
			if(visibleGraphs.endsWith(","))
				visibleGraphs = visibleGraphs.substring(0, visibleGraphs.length() - 1);
			if(visibleLine.endsWith(","))
				visibleLine = visibleLine.substring(0, visibleLine.length() - 1);
			
			
			map.put("showMeasureCheckBox", false);
			if(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH
					)
			{
				if(graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend") 
						&& graphInfo.getDataColLabels3().size() > 0)
				map.put("showMeasureCheckBox", true);
			}
			if(graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH)
			{
				map.put("showMeasureCheckBox", true);
			}
			
			/*graphInfo.getGraphData().getCmbBarrowLabel();
			graphInfo.getGraphData().getCmbBarcolLabel();
			graphInfo.getGraphData().getCmbLinerowLabel();
			graphInfo.getGraphData().getCmbLinecolLabel();
			*/
			 //Line data Label
			 
			 int barcolListsize=barcolList.size();
			 
			 int barrowListsize=barrowList.size();
			 int linerowListsize=linerowList.size();
			
			int BarSize = graphInfo.getDataColLabels3().size();
			int LineSize = graphInfo.getTheDataColLabels4().size();
			if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
			{
			if(barcolsize <= 2 || barrowsize <= 2 || linerowsize <= 2 || linecolsize <= 2)
			{
				if(graphInfo.getDataColLabels3().size() > 1 
						&& (graphInfo.getGraphData().getCmbBarrowLabel()!=null
						|| graphInfo.getGraphData().getCmbBarrowLabel().equalsIgnoreCase("Legend")))
				{
					map.put("showMeasureCheckBox", true);
				}
				if(graphInfo.getTheDataColLabels4().size() > 1 
						&& (graphInfo.getGraphData().getCmbLinerowLabel()!=null
						|| graphInfo.getGraphData().getCmbLinerowLabel().equalsIgnoreCase("Legend")))
				{
					map.put("showMeasureCheckBox", true);
				}
			}
			}
			map.put("BarSize", BarSize);
			map.put("LineSize", LineSize);
			map.put("visibleLine", visibleLine);
			map.put("visibleGraphs", visibleGraphs);
			map.put("legendLabel", graphInfo.getGraphData().getRowLabel());
			if(null !=graphInfo.getGraphProperties().getCustomLegendSelectedValueList()){
			map.put("selectedValues",graphInfo.getGraphProperties().getCustomLegendSelectedValueList());}
			if(graphInfo.getGraphData().getRowLabel() == null){
				map.put("legendLabel", graphInfo.getGraphData().getColLabel());					
			}
			//9 Apr 2019[show/hide graphs]
			
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			return new ModelAndView("graph/graphPropertiesTabContent");
		}
	}
	
	
	@RequestMapping(value = "/loadDataValuesMeasureProperties", method=RequestMethod.POST)
	@ResponseBody
	public Object loadDataValuesMeasureProperties(@ModelAttribute GraphProperties graphProperties,
			@RequestParam(required = false, value="measureSelectedTabNames") String selectedTabNames,
			@RequestParam(required = false, value="measureNextTabName") String nextTabName,
			ModelMap map, HttpServletResponse response, @LoggedInUser UserInfo userInfo) {
		//System.out.println("GraphController.loadYaxisMeasureProperties() ::: "+nextTabName);
		Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
		if(propertyMap != null) {
			@SuppressWarnings("rawtypes")
			Iterator itr = propertyMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				map.put(key, propertyMap.get(key));
			}
		}
		if (null == nextTabName) {
			map.put("measureCurrentTabName", "M"+0);
		} else {
			map.put("measureCurrentTabName", nextTabName);
		}
		response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		return new ModelAndView("graph/graphDataValuesPropertiesMultiMeasureProperties");
	}
	
	
	@RequestMapping(value = "/loadYaxisMeasureProperties", method=RequestMethod.POST)
	@ResponseBody
	public Object loadYaxisMeasureProperties(@ModelAttribute GraphProperties graphProperties,
			@RequestParam(required = false, value="measureSelectedTabNames") String selectedTabNames,
			@RequestParam(required = false, value="measureNextTabName") String nextTabName,
			ModelMap map, HttpServletResponse response, @LoggedInUser UserInfo userInfo) {
		//System.out.println("GraphController.loadYaxisMeasureProperties() ::: "+nextTabName);
		Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
		if(propertyMap != null) {
			@SuppressWarnings("rawtypes")
			Iterator itr = propertyMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				map.put(key, propertyMap.get(key));
			}
		}
		if (null == nextTabName) {
			map.put("measureCurrentTabName", "M"+0);
		} else {
			map.put("measureCurrentTabName", nextTabName);
		}
		response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		return new ModelAndView("graph/graphYaxisPropertiesMultiMeasureProperties");
	}
	/**
	 * Close and Apply Rank in Graph
	 * 
	 * @return 'Success' if oparation is successfull otherwise error message.
	 */
	@RequestMapping (value = "/closeRank")
	@ResponseBody
	public Object closeRankDialog(HttpServletRequest request, HttpServletResponse response, ModelMap map,
			@LoggedInUser UserInfo userInfo, HttpSession session) {
		
		List<CubeRankDataLabel> rankList = graphInfo.getRankList();
		graphInfo.getGraphData().setGraphFromDashboard(false);
		List<CubeRankDataLabel> activeRankList = new ArrayList<CubeRankDataLabel>();
		Object status = "";
		try {
			/*if(rankList.size() == 0) {
				graphService.setRowRankDataLabel(null);
				graphService.setAutoRefresh(true);
				graphService.setRankDataLabel(null);
			}*/
			for (int cnt = 0; cnt < rankList.size(); cnt++) {
				
				CubeRankDataLabel rankDaraLabel = rankList.get(cnt);
				graphService.setAutoRefresh(false);
				if(rankDaraLabel.isStatus()) {
						activeRankList.add(rankDaraLabel);
				}
					/*if(rankDaraLabel.getLabelType() == CubeRankDataLabel.ROW_TYPE)
						graphService.setRowRankDataLabel(rankDaraLabel);			
					else
						graphService.setRankDataLabel(rankDaraLabel);
				} else {
						graphService.setRowRankDataLabel(null);
						graphService.setRankDataLabel(null);
				}*/
			}
			graphService.setCubeRankDataLabelList(activeRankList);
			session.removeAttribute("activeRankList");
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			
			if(map != null && map.get("requireRefresh") == null) {
				request.setAttribute("forceRefresh", true);
				map.put("requireRefresh", true);
				map.put("forceRefresh", true);
				status = refreshObjectData(request, response, userInfo, map);
			}else {
				status = AppConstants.SUCCESS_STATUS;
			}
		} catch (CubeException | RScriptException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_APPLIED_SORT", new Object[] {getObjectDisplayName(),
							userInfo.getUsername() }), e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_APPLY_RANK", new Object[] {e.getMessage()});
		} 
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_RANK"), AppConstants.DETAIL,userInfo);
		return status;
	}

	/**
	 * Close and Apply SORT in Analysis
	 * 
	 * @return 'Success' if operation is successfull otherwise error message.
	 */
	@RequestMapping (value = "/closeSort")
	@ResponseBody
	public Object closeSortDialog(HttpServletRequest request, HttpServletResponse response, ModelMap map,
			@LoggedInUser UserInfo userInfo, HttpSession session) {
		List<CubeLabelInfo> sortInfo = graphInfo.getSortList();
		Object status = "";
		String dimensionName = "";
		try {
			for (int i = 0; i < sortInfo.size(); i++) {
				CubeLabelInfo cubeLabelInfo = sortInfo.get(i);
				dimensionName = cubeLabelInfo.getName();
				if (cubeLabelInfo.getSortType() == ICubeResultSetSupport.sortTypeByNone) {
					sortInfo.remove(i--);
				}
			}
			
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
			session.removeAttribute("activeSortList");
			
			if(map != null && map.get("requireRefresh") == null) {
				request.setAttribute("forceRefresh", true);
				map.put("requireRefresh", true);
				map.put("forceRefresh", true);
				status = refreshObjectData(request, response, userInfo, map);
			}else {
				status = AppConstants.SUCCESS_STATUS;
			}

		} catch (Exception e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_APPLIED_SORT", new Object[] {
							dimensionName, getObjectDisplayName(),
							getObjectTypeName(),
							userInfo.getUsername() }), e);

			status = ResourceManager.getString(
					"ERROR_MSG_FAILED_TO_APPLY_SORT",
					new Object[] { dimensionName,e.getMessage() });
		} 
		auditUserActionLog(ResourceManager.getString("LBL_APPLY_GRAPH_SORT"), AppConstants.DETAIL,userInfo);
		return status;
	}

	@Override
	public Map<SelectItem, Integer> prepareAllItemsMap(boolean includeMeasure,UserInfo userInfo) throws CubeException {
		
		return graphService.prepareAllItemsMap(graphInfo, includeMeasure,userInfo);
	}

	@Override
	public List<SelectItem> getAdvList(UserInfo uInfo,boolean isGlobalAdd, String objectId) throws ALSException, CubeException {
		List<SelectItem> advList = new ArrayList<SelectItem>();
		Map<String, String> rowLabelNames = graphService.getRowColumnDisplayNameMap(graphInfo);
		Map<String, String> dataLabelNames = graphService.getMeasureDisplayNameMap(graphInfo, uInfo.getUserId());
		
		for (String key : rowLabelNames.keySet()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(rowLabelNames.get(key));
			selectItem.setValue(key);
			advList.add(selectItem);
		}
		for (String key : dataLabelNames.keySet()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(dataLabelNames.get(key));
			selectItem.setValue(key);
			advList.add(selectItem);
		}
		if(isGlobalAdd) {
			List<ActiveGlobalVariableInfo> activeGolbalVariableList = graphInfo.getActiveTemplateProperties().getActiveGlobalVariableInfo(uInfo.getUserId());
			if(activeGolbalVariableList != null && activeGolbalVariableList.size()>0){
				for (ActiveGlobalVariableInfo activeGlobalVariableInfo : activeGolbalVariableList) {
					String globalVariableName = activeGlobalVariableInfo.getGlobalVariableInfo().getGlobalVariableName().substring(1,  activeGlobalVariableInfo.getGlobalVariableInfo().getGlobalVariableName().lastIndexOf("$"));
					SelectItem item = new SelectItem();
					item.setLabel(globalVariableName);
					item.setValue(activeGlobalVariableInfo.getGlobalVariableInfo().getGlobalVariableName());
					advList.add(item);
				}
			}
			}
		return advList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SelectItem> getDisplayLabel(boolean pageFilter, String pageFilterColumnName,UserInfo userInfo) throws CubeException {
		
		Vector<String> dataItemList = new Vector<String>();
		List<SelectItem> dataLabels =  new ArrayList<SelectItem>();
		dataItemList.addAll(graphInfo.getDataColumns());
		if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
			dataItemList.addAll(graphInfo.getLineGraphDataLabelsForCombinedGraph());
		}
		if (pageFilter) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(pageFilterColumnName);
			selectItem.setValue(pageFilterColumnName);
			dataLabels.add(selectItem);
		} else {	
			for (String dataLabel : dataItemList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(graphService.getAxisDisplayName(dataLabel, graphInfo));
				selectItem.setValue(dataLabel);
				dataLabels.add(selectItem);
			}
			List<ActiveUDDCInfo> activeUDDCList = graphInfo.getActiveUDDCInfo(userInfo.getUserId());
			for (int cnt = 0; cnt < activeUDDCList.size(); cnt++) {
				SelectItem selectItem = new SelectItem();
				ActiveUDDCInfo activeuddcInfo = activeUDDCList.get(cnt);
				selectItem.setLabel(activeuddcInfo.getUddcTemplateInfo().getColumnName());
				selectItem.setValue(activeuddcInfo.getUddcTemplateInfo().getColumnName());
				dataLabels.add(selectItem);
			}
		}
			return dataLabels;
	}
	
	@RequestMapping (value = "/addTrendLineProperties")
	@ResponseBody
	public ModelAndView addTrendLineProperties(ModelMap modelMap,@RequestParam("trendLineName") String strTrendLineName
			,@RequestParam("trendLineColumn") String strTrendLineColumn			
			,@RequestParam("trendLineType") String strTrendLineType
			,@RequestParam("trendLineStyle") String strTrendLineStyle
			,@RequestParam("trendLineThickness") String strtrendLineThickness
			,@RequestParam("trendLineColor") String strtrendLineColor,@LoggedInUser UserInfo userInfo) {
		
		TrendLineProperties trendlineProperties = new TrendLineProperties();
		if(strTrendLineName != null && !strTrendLineName.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineName(strTrendLineName);
		}
		if(strTrendLineColumn != null && !strTrendLineColumn.equalsIgnoreCase(""))
		{
			//trendlineProperties.setTrendLineColumn(strTrendLineColumn);
			//check with ddvm
			//DDVM
			HashtableEx ddvmMap = new HashtableEx();
			ddvmMap = (HashtableEx) getActiveDDVMs(userInfo.getUserId());
			Vector packDisplayList=null;
			if(ddvmMap != null && ddvmMap.size() > 0)
			{
				if(ddvmMap.containsKey(graphInfo.getGraphData().getRowLabel())) 
				{
					packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getRowLabel());
				}
				else if(ddvmMap.containsKey(graphInfo.getGraphData().getColLabel()))
				{
					packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getColLabel());
				}
			}
			String[] splitString = (strTrendLineColumn).split(",");
			if (packDisplayList != null && packDisplayList.size() > 0) 
			{

					for (Iterator iterator = packDisplayList.iterator(); iterator.hasNext();) 
					{
						String[] values = (String[]) iterator.next();															
						if (values[1].equalsIgnoreCase(splitString[0]))
						{
							strTrendLineColumn =  values[0]+","+values[0];
							break;
						}
					}
			}
			trendlineProperties.setTrendLineColumn(strTrendLineColumn);
			
		}
		if(strTrendLineType != null && !strTrendLineType.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineType(strTrendLineType);
		}
		if(strTrendLineStyle != null && !strTrendLineStyle.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineStyle(strTrendLineStyle);
		}
		if(strtrendLineThickness != null && !strtrendLineThickness.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineThickness(Integer.parseInt(strtrendLineThickness));
		}
		if(strtrendLineColor != null && !strtrendLineColor.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineColor(strtrendLineColor);
		}
		Map<Integer,TrendLineProperties> trendLinePropertiesMap  = null;
		if(graphInfo.getGraphProperties().getTrendlinePropertiesMap()!= null && !graphInfo.getGraphProperties().getTrendlinePropertiesMap().isEmpty())
		{
			trendLinePropertiesMap = graphInfo.getGraphProperties().getTrendlinePropertiesMap();
		}
		else
		{
			trendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
		}
		if(trendLinePropertiesMap!= null)
		{
			trendLinePropertiesMap.put(trendLinePropertiesMap.size()+1,trendlineProperties);
		}
		
		
		graphInfo.getGraphProperties().setTrendlinePropertiesMap(trendLinePropertiesMap);	
		modelMap.put("trendlinePropertiesMap",trendLinePropertiesMap);	
		modelMap.put("isGraphProperties",true);
		auditUserActionLog(ResourceManager.getString("LBL_ADD_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		modelMap.put("isSmarten",false);
		return new ModelAndView("kpi/trendlinePropobjects");
	}	
	
	@RequestMapping (value = "/removeTrendLinPropObj")
	@ResponseBody
	public ModelAndView removeTrendLinPropObj(ModelMap modelMap,@RequestParam("trendlineobjkey") String strTrendlineobjkey,@LoggedInUser UserInfo userInfo)	{
		Map<Integer,TrendLineProperties> trendLinePropertiesMap = null;	
		Map<Integer,TrendLineProperties>  tempTrendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
		if(strTrendlineobjkey != null && !strTrendlineobjkey.equalsIgnoreCase("")){
			if(graphInfo.getGraphProperties().getTrendlinePropertiesMap() != null && !graphInfo.getGraphProperties().getTrendlinePropertiesMap().isEmpty())
			{
				trendLinePropertiesMap = graphInfo.getGraphProperties().getTrendlinePropertiesMap();
				trendLinePropertiesMap.remove(Integer.parseInt(strTrendlineobjkey));				
				
				int i = 0;
			//trendLinePropertiesMap = graphInfo.getGraphProperties().getTrendlinePropertiesMap();
			
			Iterator iterator = graphInfo.getGraphProperties().getTrendlinePropertiesMap().keySet().iterator();
			 while (iterator.hasNext()) {
			     int key = (int) iterator.next();
				tempTrendLinePropertiesMap.put(i, graphInfo.getGraphProperties().getTrendlinePropertiesMap().get(key));
				i++;
			}
				
				graphInfo.getGraphProperties().setTrendlinePropertiesMap(tempTrendLinePropertiesMap);
			}
		}
		modelMap.put("trendlinePropertiesMap",tempTrendLinePropertiesMap);
		modelMap.put("isGraphProperties",true);
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		modelMap.put("isSmarten",false);
		return new ModelAndView("kpi/trendlinePropobjects");
	}
	
	@RequestMapping (value = "/editTrendTrendLinePropObj")
	@ResponseBody
	public ModelAndView editTrendTrendLinePropObj(ModelMap modelMap,@RequestParam("trendlineobjkey") String strTrendlineobjkey,
			@LoggedInUser UserInfo userInfo)	{
		Map<Integer,TrendLineProperties> trendLinePropertiesMap = null;	
		if(strTrendlineobjkey != null && !strTrendlineobjkey.equalsIgnoreCase("")){

			if(graphInfo.getGraphProperties().getTrendlinePropertiesMap()!= null && !graphInfo.getGraphProperties().getTrendlinePropertiesMap().isEmpty()){
				trendLinePropertiesMap = graphInfo.getGraphProperties().getTrendlinePropertiesMap();

				String trendlineobjName = "";
				String trendlineobjColumn = "";
				String trendlineobjType = "";
				String trendlineobjStyle = "";
				int trendlineobjThickness = 0;
				String trendlineobjColor = "";
				int trendlineobjKey = 0;

				if(trendLinePropertiesMap!=null && !trendLinePropertiesMap.isEmpty()){
					trendlineobjName = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineName();
					trendlineobjColumn = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineColumn();
					trendlineobjType = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineType();
					trendlineobjStyle = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineStyle();
					trendlineobjThickness = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineThickness();
					trendlineobjColor = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineColor();
					trendlineobjKey = Integer.parseInt(strTrendlineobjkey);
				}
				
				if(trendlineobjColumn != null)
				{
					String[] splitString = trendlineobjColumn.split(",");
					trendlineobjColumn = splitString[0];
				}
				
				Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
				if(propertyMap != null) {
					/*@SuppressWarnings("rawtypes")
					Iterator itr = propertyMap.keySet().iterator();
					while (itr.hasNext()) {*/
						String key = "trendColumnsList";
						modelMap.put(key, propertyMap.get(key));
					//}
				}

				modelMap.put("trendlineobjName",trendlineobjName);
				modelMap.put("trendlineobjColumn",trendlineobjColumn);
				modelMap.put("trendlineobjType",trendlineobjType);
				modelMap.put("trendlineobjStyle",trendlineobjStyle);
				modelMap.put("trendlineobjThickness",trendlineobjThickness);
				modelMap.put("trendlineobjColor",trendlineobjColor);
				modelMap.put("trendlineobjKey",trendlineobjKey);
			}
		}
		modelMap.put("isSmarten",false);
		auditUserActionLog(ResourceManager.getString("LBL_EDIT_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("kpi/editTrendTrendLine");
	}
	
	@RequestMapping (value = "/addTrendLinePropertiesForCombinedGraph")
	@ResponseBody
	public ModelAndView addTrendLinePropertiesForCombinedGraph(ModelMap modelMap,@RequestParam("trendLineName") String strTrendLineName
			,@RequestParam("trendLineColumn") String strTrendLineColumn			
			,@RequestParam("trendLineType") String strTrendLineType
			,@RequestParam("trendLineStyle") String strTrendLineStyle
			,@RequestParam("trendLineThickness") String strtrendLineThickness
			,@RequestParam("trendLineColor") String strtrendLineColor
			,@RequestParam("fromGraph") String strfromGraph,@LoggedInUser UserInfo userInfo) {
		String objectview = "";
		if(strfromGraph!= null && !strfromGraph.equalsIgnoreCase("")) {
			TrendLineProperties trendlineProperties = new TrendLineProperties();
			if(strTrendLineName != null && !strTrendLineName.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineName(strTrendLineName);
			}
			if(strTrendLineColumn != null && !strTrendLineColumn.equalsIgnoreCase(""))
			{
				//DDVM
				HashtableEx ddvmMap = new HashtableEx();
				ddvmMap = (HashtableEx) getActiveDDVMs(userInfo.getUserId());
				Vector packDisplayList=null;
				if(ddvmMap != null && ddvmMap.size() > 0)
				{
					
					if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbBarrowLabel()))
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbBarrowLabel());
					}
					else if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbLinerowLabel()))
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbLinerowLabel());
					}
					else if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbLinecolLabel())) 
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbLinecolLabel());
					}
					else if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbBarcolLabel())) 
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbBarcolLabel());
					}
				}
				String[] splitString = (strTrendLineColumn).split(",");
				if (packDisplayList != null && packDisplayList.size() > 0) 
				{

						for (Iterator iterator = packDisplayList.iterator(); iterator.hasNext();) 
						{
							String[] values = (String[]) iterator.next();															
							if (values[1].equalsIgnoreCase(splitString[0]))
							{
								strTrendLineColumn =  values[0]+","+values[0];
								break;
							}
						}
				}
				//trendlineProperties.setTrendLineColumn(strTrendLineColumn);
				trendlineProperties.setTrendLineColumn(strTrendLineColumn);
			}
			if(strTrendLineType != null && !strTrendLineType.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineType(strTrendLineType);
			}
			if(strTrendLineStyle != null && !strTrendLineStyle.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineStyle(strTrendLineStyle);
			}
			if(strtrendLineThickness != null && !strtrendLineThickness.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineThickness(Integer.parseInt(strtrendLineThickness));
			}
			if(strtrendLineColor != null && !strtrendLineColor.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineColor(strtrendLineColor);
			}
			Map<Integer,TrendLineProperties> trendLinePropertiesMap  = null;			
			if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
				if(graphInfo.getGraphProperties().getBartrendlinePropertiesMap()!= null && !graphInfo.getGraphProperties().getBartrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getBartrendlinePropertiesMap();
				}
				else
				{
					trendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
				}
				graphInfo.getGraphProperties().setBartrendlinePropertiesMap(trendLinePropertiesMap);	
				modelMap.put("bartrendlinePropertiesMap",trendLinePropertiesMap);
				objectview = "/barTrendLineObjects";
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
				if(graphInfo.getGraphProperties().getLinetrendlinePropertiesMap()!= null && !graphInfo.getGraphProperties().getLinetrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getLinetrendlinePropertiesMap();
				}
				else
				{
					trendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
				}
				graphInfo.getGraphProperties().setLinetrendlinePropertiesMap(trendLinePropertiesMap);	
				modelMap.put("linetrendlinePropertiesMap",trendLinePropertiesMap);
				objectview = "/lineTrendLineObjects";
			}				
			if(trendLinePropertiesMap!= null)
			{
				trendLinePropertiesMap.put(trendLinePropertiesMap.size()+1,trendlineProperties);
			}						
		}
		auditUserActionLog(ResourceManager.getString("LBL_ADD_COMBINE_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		modelMap.put("isGraphProperties",true);		
		return new ModelAndView("graph"+objectview);
	}	
	
	@RequestMapping (value = "/removeTrendLinPropObjForCombinedGraph")
	@ResponseBody
	public ModelAndView removeTrendLinPropObjForCombinedGraph(ModelMap modelMap,@RequestParam("trendlineobjkey") String strTrendlineobjkey,
			@RequestParam("fromGraph") String strfromGraph,@LoggedInUser UserInfo userInfo)	{
		Map<Integer,TrendLineProperties> trendLinePropertiesMap = null;	
		String objectview = "";
		if(strTrendlineobjkey != null && !strTrendlineobjkey.equalsIgnoreCase("") && strfromGraph != null && !strfromGraph.equalsIgnoreCase("")){
			if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
				if(graphInfo.getGraphProperties().getBartrendlinePropertiesMap() != null && !graphInfo.getGraphProperties().getBartrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getBartrendlinePropertiesMap();
					trendLinePropertiesMap.remove(Integer.parseInt(strTrendlineobjkey));				
					
					int i = 0;
					Map<Integer,TrendLineProperties>  tempTrendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
					
					Iterator iterator = graphInfo.getGraphProperties().getBartrendlinePropertiesMap().keySet().iterator();
					 while (iterator.hasNext()) {
					     int key = (int) iterator.next();
						tempTrendLinePropertiesMap.put(i, graphInfo.getGraphProperties().getBartrendlinePropertiesMap().get(key));
						i++;
					}
					 modelMap.put("bartrendlinePropertiesMap",tempTrendLinePropertiesMap);
						objectview = "/barTrendLineObjects";
					
					graphInfo.getGraphProperties().setBartrendlinePropertiesMap(tempTrendLinePropertiesMap);
				}
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
				if(graphInfo.getGraphProperties().getLinetrendlinePropertiesMap() != null && !graphInfo.getGraphProperties().getLinetrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getLinetrendlinePropertiesMap();
					trendLinePropertiesMap.remove(Integer.parseInt(strTrendlineobjkey));		
					
					int i = 0;
					Map<Integer,TrendLineProperties>  tempTrendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
					
					Iterator iterator = graphInfo.getGraphProperties().getLinetrendlinePropertiesMap().keySet().iterator();
					 while (iterator.hasNext()) {
					     int key = (int) iterator.next();
						tempTrendLinePropertiesMap.put(i, graphInfo.getGraphProperties().getLinetrendlinePropertiesMap().get(key));
						i++;
					}
					graphInfo.getGraphProperties().setLinetrendlinePropertiesMap(tempTrendLinePropertiesMap);
					modelMap.put("linetrendlinePropertiesMap",tempTrendLinePropertiesMap);
					objectview = "/lineTrendLineObjects";
				}
			}
		}
		modelMap.put("trendlinePropertiesMap",trendLinePropertiesMap);
		modelMap.put("isGraphProperties",true);
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_COMBINE_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph"+objectview);
		//return new ModelAndView("kpi/trendlinePropobjects");
	}
	
	@RequestMapping (value = "/editTrendLinePropObjForCombinedGraph")
	@ResponseBody
	public ModelAndView editTrendLinePropObjForCombinedGraph(ModelMap modelMap,@RequestParam("trendlineobjkey") String strTrendlineobjkey,
			@RequestParam("fromGraph") String strfromGraph,
			@LoggedInUser UserInfo userInfo)	{
		Map<Integer,TrendLineProperties> trendLinePropertiesMap = null;	
		String objectview = "";
		if(strTrendlineobjkey != null && !strTrendlineobjkey.equalsIgnoreCase("") && strfromGraph != null && !strfromGraph.equalsIgnoreCase("")){
			if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
				if(graphInfo.getGraphProperties().getBartrendlinePropertiesMap() != null && !graphInfo.getGraphProperties().getBartrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getBartrendlinePropertiesMap();
					
					String trendlineobjName = "";
					String trendlineobjColumn = "";
					String trendlineobjType = "";
					String trendlineobjStyle = "";
					int trendlineobjThickness = 0;
					String trendlineobjColor = "";
					int trendlineobjKey = 0;

					if(trendLinePropertiesMap!=null && !trendLinePropertiesMap.isEmpty()){
						trendlineobjName = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineName();
						trendlineobjColumn = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineColumn();
						trendlineobjType = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineType();
						trendlineobjStyle = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineStyle();
						trendlineobjThickness = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineThickness();
						trendlineobjColor = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineColor();
						trendlineobjKey = Integer.parseInt(strTrendlineobjkey);
					}
					
					if(trendlineobjColumn != null)
					{
						String[] splitString = trendlineobjColumn.split(",");
						trendlineobjColumn = splitString[0];
					}
					
					Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
					if(propertyMap != null) {
						/*@SuppressWarnings("rawtypes")
						Iterator itr = propertyMap.keySet().iterator();
						while (itr.hasNext()) {*/
							String key = "trendColumnsList";
							modelMap.put(key, propertyMap.get(key));
						//}
					}

					modelMap.put("trendlineobjName",trendlineobjName);
					modelMap.put("trendlineobjColumn",trendlineobjColumn);
					modelMap.put("trendlineobjType",trendlineobjType);
					modelMap.put("trendlineobjStyle",trendlineobjStyle);
					modelMap.put("trendlineobjThickness",trendlineobjThickness);
					modelMap.put("trendlineobjColor",trendlineobjColor);
					modelMap.put("trendlineobjKey",trendlineobjKey);
					
					objectview = "/editBarTrendLine";
				}
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
				if(graphInfo.getGraphProperties().getLinetrendlinePropertiesMap() != null && !graphInfo.getGraphProperties().getLinetrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getLinetrendlinePropertiesMap();

					String trendlineobjName = "";
					String trendlineobjColumn = "";
					String trendlineobjType = "";
					String trendlineobjStyle = "";
					int trendlineobjThickness = 0;
					String trendlineobjColor = "";
					int trendlineobjKey = 0;

					if(trendLinePropertiesMap!=null && !trendLinePropertiesMap.isEmpty()){
						trendlineobjName = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineName();
						trendlineobjColumn = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineColumn();
						trendlineobjType = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineType();
						trendlineobjStyle = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineStyle();
						trendlineobjThickness = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineThickness();
						trendlineobjColor = trendLinePropertiesMap.get(Integer.parseInt(strTrendlineobjkey)).getTrendLineColor();
						trendlineobjKey = Integer.parseInt(strTrendlineobjkey);
					}
					
					if(trendlineobjColumn != null)
					{
						String[] splitString = trendlineobjColumn.split(",");
						trendlineobjColumn = splitString[0];
					}
					
					Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
					if(propertyMap != null) {
						/*@SuppressWarnings("rawtypes")
						Iterator itr = propertyMap.keySet().iterator();
						while (itr.hasNext()) {*/
							String key = "trendColumnsLineList";
							modelMap.put(key, propertyMap.get(key));
						//}
					}

					modelMap.put("trendlineobjName",trendlineobjName);
					modelMap.put("trendlineobjColumn",trendlineobjColumn);
					modelMap.put("trendlineobjType",trendlineobjType);
					modelMap.put("trendlineobjStyle",trendlineobjStyle);
					modelMap.put("trendlineobjThickness",trendlineobjThickness);
					modelMap.put("trendlineobjColor",trendlineobjColor);
					modelMap.put("trendlineobjKey",trendlineobjKey);
					
					objectview = "/editLineTrendLine";
				}
			}
		}
		auditUserActionLog(ResourceManager.getString("LBL_EDIT_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph"+objectview);
	}
	
	@RequestMapping (value = "/updateTrendLinePropObjForCombined")
	@ResponseBody
	public ModelAndView updateTrendLinePropObjForCombined(ModelMap modelMap,@RequestParam("trendLineKey") String strTrendLineKey
			,@RequestParam("trendLineName") String strTrendLineName
			,@RequestParam("trendLineColumn") String strTrendLineColumn			
			,@RequestParam("trendLineType") String strTrendLineType
			,@RequestParam("trendLineStyle") String strTrendLineStyle
			,@RequestParam("trendLineThickness") String strtrendLineThickness
			,@RequestParam("trendLineColor") String strtrendLineColor
			,@RequestParam("fromGraph") String strfromGraph,@LoggedInUser UserInfo userInfo) {
		String objectview = "";
		if(strfromGraph!= null && !strfromGraph.equalsIgnoreCase("")) {
			TrendLineProperties trendlineProperties = new TrendLineProperties();
			if(strTrendLineName != null && !strTrendLineName.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineName(strTrendLineName);
			}
			if(strTrendLineColumn != null && !strTrendLineColumn.equalsIgnoreCase(""))
			{
				//DDVM
				HashtableEx ddvmMap = new HashtableEx();
				ddvmMap = (HashtableEx) getActiveDDVMs(userInfo.getUserId());
				Vector packDisplayList=null;
				if(ddvmMap != null && ddvmMap.size() > 0)
				{
					
					if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbBarrowLabel()))
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbBarrowLabel());
					}
					else if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbLinerowLabel()))
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbLinerowLabel());
					}
					else if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbLinecolLabel())) 
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbLinecolLabel());
					}
					else if(ddvmMap.containsKey(graphInfo.getGraphData().getCmbBarcolLabel())) 
					{
						packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getCmbBarcolLabel());
					}
				}
				String[] splitString = (strTrendLineColumn).split(",");
				if (packDisplayList != null && packDisplayList.size() > 0) 
				{

						for (Iterator iterator = packDisplayList.iterator(); iterator.hasNext();) 
						{
							String[] values = (String[]) iterator.next();															
							if (values[1].equalsIgnoreCase(splitString[0]))
							{
								strTrendLineColumn =  values[0]+","+values[0];
								break;
							}
						}
				}
				//trendlineProperties.setTrendLineColumn(strTrendLineColumn);
				trendlineProperties.setTrendLineColumn(strTrendLineColumn);
			}
			if(strTrendLineType != null && !strTrendLineType.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineType(strTrendLineType);
			}
			if(strTrendLineStyle != null && !strTrendLineStyle.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineStyle(strTrendLineStyle);
			}
			if(strtrendLineThickness != null && !strtrendLineThickness.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineThickness(Integer.parseInt(strtrendLineThickness));
			}
			if(strtrendLineColor != null && !strtrendLineColor.equalsIgnoreCase(""))
			{
				trendlineProperties.setTrendLineColor(strtrendLineColor);
			}
			Map<Integer,TrendLineProperties> trendLinePropertiesMap  = null;			
			if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
				if(graphInfo.getGraphProperties().getBartrendlinePropertiesMap()!= null && !graphInfo.getGraphProperties().getBartrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getBartrendlinePropertiesMap();
				}
				else
				{
					trendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
				}
				graphInfo.getGraphProperties().setBartrendlinePropertiesMap(trendLinePropertiesMap);	
				modelMap.put("bartrendlinePropertiesMap",trendLinePropertiesMap);
				objectview = "/barTrendLineObjects";
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
				if(graphInfo.getGraphProperties().getLinetrendlinePropertiesMap()!= null && !graphInfo.getGraphProperties().getLinetrendlinePropertiesMap().isEmpty())
				{
					trendLinePropertiesMap = graphInfo.getGraphProperties().getLinetrendlinePropertiesMap();
				}
				else
				{
					trendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
				}
				graphInfo.getGraphProperties().setLinetrendlinePropertiesMap(trendLinePropertiesMap);	
				modelMap.put("linetrendlinePropertiesMap",trendLinePropertiesMap);
				objectview = "/lineTrendLineObjects";
			}				
			if(trendLinePropertiesMap!= null)
			{
				if(strTrendLineKey != null && !strTrendLineKey.equalsIgnoreCase(""))
				{
					trendLinePropertiesMap.put(Integer.parseInt(strTrendLineKey),trendlineProperties);
				}
			}						
		}
		auditUserActionLog(ResourceManager.getString("LBL_ADD_COMBINE_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		modelMap.put("isGraphProperties",true);		
		return new ModelAndView("graph"+objectview);
	}
	
	@RequestMapping (value = "/updateTrendLineProperties")
	@ResponseBody
	public ModelAndView updateTrendLineProperties(ModelMap modelMap,@RequestParam("trendLineKey") String strTrendLineKey
			,@RequestParam("trendLineName") String strTrendLineName
			,@RequestParam("trendLineColumn") String strTrendLineColumn			
			,@RequestParam("trendLineType") String strTrendLineType
			,@RequestParam("trendLineStyle") String strTrendLineStyle
			,@RequestParam("trendLineThickness") String strtrendLineThickness
			,@RequestParam("trendLineColor") String strtrendLineColor,@LoggedInUser UserInfo userInfo) {
		
		TrendLineProperties trendlineProperties = new TrendLineProperties();
		if(strTrendLineName != null && !strTrendLineName.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineName(strTrendLineName);
		}
		if(strTrendLineColumn != null && !strTrendLineColumn.equalsIgnoreCase(""))
		{
			//trendlineProperties.setTrendLineColumn(strTrendLineColumn);
			//check with ddvm
			//DDVM
			HashtableEx ddvmMap = new HashtableEx();
			ddvmMap = (HashtableEx) getActiveDDVMs(userInfo.getUserId());
			Vector packDisplayList=null;
			if(ddvmMap != null && ddvmMap.size() > 0)
			{
				if(ddvmMap.containsKey(graphInfo.getGraphData().getRowLabel())) 
				{
					packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getRowLabel());
				}
				else if(ddvmMap.containsKey(graphInfo.getGraphData().getColLabel()))
				{
					packDisplayList = (Vector) ddvmMap.get(graphInfo.getGraphData().getColLabel());
				}
			}
			String[] splitString = (strTrendLineColumn).split(",");
			if (packDisplayList != null && !packDisplayList.isEmpty()) 
			{

					for (Iterator iterator = packDisplayList.iterator(); iterator.hasNext();) 
					{
						String[] values = (String[]) iterator.next();															
						if (values[1].equalsIgnoreCase(splitString[0]))
						{
							strTrendLineColumn =  values[0]+","+values[0];
							break;
						}
					}
			}
			trendlineProperties.setTrendLineColumn(strTrendLineColumn);
			
		}
		if(strTrendLineType != null && !strTrendLineType.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineType(strTrendLineType);
		}
		if(strTrendLineStyle != null && !strTrendLineStyle.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineStyle(strTrendLineStyle);
		}
		if(strtrendLineThickness != null && !strtrendLineThickness.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineThickness(Integer.parseInt(strtrendLineThickness));
		}
		if(strtrendLineColor != null && !strtrendLineColor.equalsIgnoreCase(""))
		{
			trendlineProperties.setTrendLineColor(strtrendLineColor);
		}
		Map<Integer,TrendLineProperties> trendLinePropertiesMap  = null;
		if(graphInfo.getGraphProperties().getTrendlinePropertiesMap()!= null && !graphInfo.getGraphProperties().getTrendlinePropertiesMap().isEmpty())
		{
			trendLinePropertiesMap = graphInfo.getGraphProperties().getTrendlinePropertiesMap();
		}
		else
		{
			trendLinePropertiesMap = new HashMap<Integer,TrendLineProperties>();
		}
		if(trendLinePropertiesMap!= null)
		{
			if(strTrendLineKey != null && !strTrendLineKey.equalsIgnoreCase(""))
			{
				trendLinePropertiesMap.put(Integer.parseInt(strTrendLineKey),trendlineProperties);
			}
		}
		
		graphInfo.getGraphProperties().setTrendlinePropertiesMap(trendLinePropertiesMap);	
		modelMap.put("trendlinePropertiesMap",trendLinePropertiesMap);	
		modelMap.put("isGraphProperties",true);
		auditUserActionLog(ResourceManager.getString("LBL_UPDATE_GRAPH_TREND_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		modelMap.put("isSmarten",false);
		return new ModelAndView("kpi/trendlinePropobjects");
	}

	@RequestMapping (value = "/addReferenceLineProperties")
	@ResponseBody
	public ModelAndView addReferenceLineProperties(ModelMap modelMap,@RequestParam("referencelinename") String strReferencelinename
			,@RequestParam("referencelinevalue") String strReferencelinevalue	
			,@RequestParam("referencelinestyle") String strReferencelinestyle
			,@RequestParam("referencelinewidth") String strReferencelinewidth
			,@RequestParam("referencelinecolor") String strReferencelinecolor
			,@RequestParam("fromGraph") String strfromGraph,@LoggedInUser UserInfo userInfo) {
		if(strfromGraph != null && !strfromGraph.equalsIgnoreCase("")) {
			ReferenceLine referencelineProperties = new ReferenceLine();
			String objectsview = "";
			if(strReferencelinename != null && !strReferencelinename.equalsIgnoreCase(""))
			{
				referencelineProperties.setLabel(strReferencelinename);
			}
			if(strReferencelinevalue != null && !strReferencelinevalue.equalsIgnoreCase(""))
			{
				referencelineProperties.setValue(strReferencelinevalue);
			}
			if(strReferencelinestyle != null && !strReferencelinestyle.equalsIgnoreCase(""))
			{
				referencelineProperties.setStyle(strReferencelinestyle);
			}
			if(strReferencelinewidth != null && !strReferencelinewidth.equalsIgnoreCase(""))
			{
				referencelineProperties.setWidth(Integer.parseInt(strReferencelinewidth));
			}
			if(strReferencelinecolor != null && !strReferencelinecolor.equalsIgnoreCase(""))
			{
				referencelineProperties.setColor(strReferencelinecolor);
			}		
			Map<Integer,ReferenceLine> referencelinePropertiesMap  = null;
			
				if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_GENERAL)) {
					if(graphInfo.getGraphProperties().getReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getReferencelinePropertiesMap().isEmpty()) {
						referencelinePropertiesMap = graphInfo.getGraphProperties().getReferencelinePropertiesMap();
					}
					else
					{
						referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
					}
				}
				else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
					if(graphInfo.getGraphProperties().getBarReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getBarReferencelinePropertiesMap().isEmpty()) {
						referencelinePropertiesMap = graphInfo.getGraphProperties().getBarReferencelinePropertiesMap();
					}
					else
					{
						referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
					}
				}
				else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
					if(graphInfo.getGraphProperties().getLineReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getLineReferencelinePropertiesMap().isEmpty()){
						referencelinePropertiesMap = graphInfo.getGraphProperties().getLineReferencelinePropertiesMap();
					}
					else
					{
						referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
					}
				}			
			if(referencelinePropertiesMap!= null)
			{
				referencelinePropertiesMap.put(referencelinePropertiesMap.size()+1,referencelineProperties);
			}
			if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_GENERAL)) {						
				modelMap.put("referencelinePropertiesMap",referencelinePropertiesMap);					
				graphInfo.getGraphProperties().setReferencelinePropertiesMap(referencelinePropertiesMap);
				objectsview = "/referencelineobjects";
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR))
			{
				modelMap.put("barreferencelinePropertiesMap",referencelinePropertiesMap);				
				graphInfo.getGraphProperties().setBarReferencelinePropertiesMap(referencelinePropertiesMap);	
				objectsview = "/barReferencelineobjects";
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE))
			{
				modelMap.put("linereferencelinePropertiesMap",referencelinePropertiesMap);				
				graphInfo.getGraphProperties().setLineReferencelinePropertiesMap(referencelinePropertiesMap);
				objectsview = "/lineReferencelineobjects";
			}	
			auditUserActionLog(ResourceManager.getString("LBL_ADD_GRAPH_REFERENCE_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
			return new ModelAndView("graph"+objectsview);
		}
		else
		{
			Map<Integer,ReferenceLine> referencelinePropertiesMap  = new HashMap<Integer, ReferenceLine>();
			modelMap.put("referencelinePropertiesMap",referencelinePropertiesMap);
			auditUserActionLog(ResourceManager.getString("LBL_ADD_GRAPH_REFERENCE_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
			return new ModelAndView("graph/referencelineobjects");
		}
	}
	
	@RequestMapping (value = "/removeReferenceLinePropObj")
	@ResponseBody
	public ModelAndView removeReferenceLinePropObj(ModelMap modelMap,@RequestParam("referencelineobjkey") String strReferencelineobjkey,@RequestParam("fromGraph") String strfromGraph,@LoggedInUser UserInfo userInfo)	{
		Map<Integer,ReferenceLine> referencelinePropertiesMap = null;	
		String objectsview = "";
		if(strReferencelineobjkey != null && !strReferencelineobjkey.equalsIgnoreCase("")){		
				if(strfromGraph != null && !strfromGraph.equalsIgnoreCase(""))
				{
					if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_GENERAL)) {
						if(graphInfo.getGraphProperties().getReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getReferencelinePropertiesMap().isEmpty()){
							referencelinePropertiesMap = graphInfo.getGraphProperties().getReferencelinePropertiesMap();
							referencelinePropertiesMap.remove(Integer.parseInt(strReferencelineobjkey));		
							
							int i = 0;
							Map<Integer,ReferenceLine>  tempRefrenceLinePropertiesMap = new HashMap<Integer,ReferenceLine>();
							Iterator iterator =graphInfo.getGraphProperties().getReferencelinePropertiesMap().keySet().iterator();
							 while (iterator.hasNext()) {
								 int key = (int) iterator.next();
							     tempRefrenceLinePropertiesMap.put(i, graphInfo.getGraphProperties().getReferencelinePropertiesMap().get(key));
								i++;
							}
							
							graphInfo.getGraphProperties().setReferencelinePropertiesMap(tempRefrenceLinePropertiesMap);
							modelMap.put("referencelinePropertiesMap",referencelinePropertiesMap);
							objectsview = "/referencelineobjects";
						}
					}
					else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
						if(graphInfo.getGraphProperties().getBarReferencelinePropertiesMap()!=null && !graphInfo.getGraphProperties().getBarReferencelinePropertiesMap().isEmpty()){
							referencelinePropertiesMap = graphInfo.getGraphProperties().getBarReferencelinePropertiesMap();
							referencelinePropertiesMap.remove(Integer.parseInt(strReferencelineobjkey));		
							
							int i = 0;
							Map<Integer,ReferenceLine>  tempRefrenceLinePropertiesMap = new HashMap<>();
							Iterator iterator = graphInfo.getGraphProperties().getBarReferencelinePropertiesMap().keySet().iterator();
							 while (iterator.hasNext()) {
								 int key = (int) iterator.next();
							     tempRefrenceLinePropertiesMap.put(i, graphInfo.getGraphProperties().getBarReferencelinePropertiesMap().get(key));
								i++;
							}
							
							graphInfo.getGraphProperties().setBarReferencelinePropertiesMap(tempRefrenceLinePropertiesMap);
							modelMap.put("barreferencelinePropertiesMap",tempRefrenceLinePropertiesMap);
							objectsview = "/barReferencelineobjects";
						}
					}
					else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
						if(graphInfo.getGraphProperties().getLineReferencelinePropertiesMap()!=null && !graphInfo.getGraphProperties().getLineReferencelinePropertiesMap().isEmpty())
						{
							referencelinePropertiesMap = graphInfo.getGraphProperties().getLineReferencelinePropertiesMap();
							referencelinePropertiesMap.remove(Integer.parseInt(strReferencelineobjkey));
							
							int i = 0;
							Map<Integer,ReferenceLine>  tempRefrenceLinePropertiesMap = new HashMap<Integer,ReferenceLine>();
							Iterator iterator = graphInfo.getGraphProperties().getLineReferencelinePropertiesMap().keySet().iterator();
							 while (iterator.hasNext()) {
								 int key = (int) iterator.next();
							     tempRefrenceLinePropertiesMap.put(i, graphInfo.getGraphProperties().getLineReferencelinePropertiesMap().get(key));
								i++;
							}
							 
							graphInfo.getGraphProperties().setLineReferencelinePropertiesMap(tempRefrenceLinePropertiesMap);
							modelMap.put("linereferencelinePropertiesMap",tempRefrenceLinePropertiesMap);
							objectsview = "/lineReferencelineobjects";
						}
					}										
				}							
		}	
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_GRAPH_REFERENCE_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph"+objectsview);
	}
	
	@RequestMapping (value = "/quickSettings")
	@ResponseBody
	public ModelAndView quickSettings(HttpServletRequest request, HttpServletResponse response,@LoggedInUser UserInfo userInfo,
			ModelMap map, @RequestParam(value = "dataValue", required = false) boolean dataValue
			,@RequestParam(value = "mouseOver", required = false) boolean mouseOver
			,@RequestParam(value = "drillOnLegend", required = false) boolean drillOnLegend
			,@RequestParam(value = "legend", required = false) boolean legend
			,@RequestParam(value = "cmbLegend", required = false) boolean cmbLegend
			,@RequestParam(value = "zoom", required = false) boolean zoom
			,@RequestParam(value = "barDataValue", required = false) boolean barDataValue
			,@RequestParam(value = "drillOnLegendCmb", required = false) boolean drillOnLegendCmb
			,@RequestParam(value = "mouseOverCmb", required = false) boolean mouseOverCmb
			,@RequestParam(value = "lineDataValue", required = false) boolean lineDataValue){
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Quick settings","Set quick settings",Thread.currentThread(),userInfo,null);
		//DataValue and MouseOver
		if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
		{
			for (int i = 0; i < graphInfo.getGraphProperties().getDataValuePropertiesMap().size(); i++) {
				graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().setDataValuePointVisible(dataValue);
				//graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().setPosition("Inside");
				graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValueMouseOver().setMouseOverTextEnable(mouseOver);
			}
		}
		else
		{
			if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
			{
				graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValuePoint().setDataValuePointVisible(barDataValue);
				graphInfo.getGraphProperties().getCombinedDataValueProperties().getLinedataValuePoint().setDataValuePointVisible(lineDataValue);
				graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValueMouseOver().setMouseOverTextEnable(mouseOverCmb);
				graphInfo.getGraphProperties().getCombinedDataValueProperties().getLinedataValueMouseOver().setMouseOverTextEnable(mouseOverCmb);;
				graphInfo.getGraphProperties().getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().setDrillDown(drillOnLegendCmb);
			}
		}
		
		
		
		//MouseOver
		if(graphInfo.getGraphType() != GraphConstants.COMBINED_GRAPH)
		{
			graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().setDataValuePointVisible(dataValue);
			graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().setMouseOverTextEnable(mouseOver);
			//For gauges(as discussed with chintan sir for quick settings)
			graphInfo.getGraphProperties().getGaugeDataValueZone().getDataValueConfiguration().setVisible(dataValue);
		}
		
		
		//Drill on legend
		graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().setDrillDown(drillOnLegend);
	
		//Legend
		if(graphInfo.getGraphType() != GraphConstants.COMBINED_GRAPH)
		{
			graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().setLegendPanelVisible(legend);
		}
		else
		{
			graphInfo.getGraphProperties().getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().setLegendPanelVisible(cmbLegend);
		}
		
		//Zoom
		graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartCursor().setEnable(zoom);
		
		request.setAttribute("forceRefresh", true);
		return refreshObjectData(request, response,userInfo, map);
	}
	
	@RequestMapping (value = "/showDatavalueMobile")
	@ResponseBody
	public ModelAndView showDatavalueMobile(HttpServletRequest request, HttpServletResponse response,@LoggedInUser UserInfo userInfo,
			ModelMap map, @RequestParam(value = "showAllDataValue", required = false) boolean showAllDataValue) {


		boolean dataValue = showAllDataValue;
		if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH )
		{
			boolean isClustered = graphInfo.getGraphProperties().getPieGraph().isClustered();
			for(int i = 0; i < graphInfo.getGraphProperties().getDataValuePropertiesMap().size(); i++) {
				if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().isDataValuePointVisible()) {
					graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().setDataValuePointVisible(false);
					dataValue=false;
				}else {
					graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().setDataValuePointVisible(true);
					dataValue=true;
				}
				/*if(isClustered) {
					graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().setPosition("Inside");
				}*/
			}
		}	
		else if(graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
		{
			graphInfo.getGraphProperties().getGaugeDataValueZone().getDataValueConfiguration().setVisible(dataValue);
		}
		else if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
		{
			graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValuePoint().setDataValuePointVisible(dataValue);
			graphInfo.getGraphProperties().getCombinedDataValueProperties().getLinedataValuePoint().setDataValuePointVisible(dataValue);
			
		}
		else if(graphInfo.getGraphType() != GraphConstants.COMBINED_GRAPH)
		{
			if(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible()) {
				graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().setDataValuePointVisible(false);
			}else {
				graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().setDataValuePointVisible(true);
			}
		}
		
		
		
/*		if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
		{
			for (int i = 0; i < graphInfo.getGraphProperties().getDataValuePropertiesMap().size(); i++) {
				dataValue = graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().isDataValuePointVisible();
				graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i).getDataValuePoint().setDataValuePointVisible(!dataValue);
			}
		}
		else if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
		{
			dataValue = graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValuePoint().isDataValuePointVisible();
			graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValuePoint().setDataValuePointVisible(!dataValue);
			graphInfo.getGraphProperties().getCombinedDataValueProperties().getLinedataValuePoint().setDataValuePointVisible(!dataValue);
		}
		
		graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().setDataValuePointVisible(!dataValue);

		request.setAttribute("forceRefresh", true);
		
*/		
		map.put("showAllDataValue", dataValue);
		return refreshObjectData(request, response,userInfo, map);
	}
	
	@RequestMapping (value = "/editReferenceLinePropObj")
	@ResponseBody
	public ModelAndView editReferenceLinePropObj(ModelMap modelMap,@RequestParam("referencelineobjkey") String strReferencelineobjkey,
			@RequestParam("fromGraph") String strfromGraph,@LoggedInUser UserInfo userInfo)	{
		Map<Integer,ReferenceLine> referencelinePropertiesMap = null;	
		String objectsview = "";
		if(strReferencelineobjkey != null && !strReferencelineobjkey.equalsIgnoreCase("")){	
				if(strfromGraph != null && !strfromGraph.equalsIgnoreCase(""))
				{
					if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_GENERAL)) {
						if(graphInfo.getGraphProperties().getReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getReferencelinePropertiesMap().isEmpty()){
							referencelinePropertiesMap = graphInfo.getGraphProperties().getReferencelinePropertiesMap();
							
							objectsview = "/editReferenceLine";
						}
					}
					else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
						if(graphInfo.getGraphProperties().getBarReferencelinePropertiesMap()!=null && !graphInfo.getGraphProperties().getBarReferencelinePropertiesMap().isEmpty()){
							referencelinePropertiesMap = graphInfo.getGraphProperties().getBarReferencelinePropertiesMap();
							
							objectsview = "/editbarReferenceLine";
						}
					}
					else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
						if(graphInfo.getGraphProperties().getLineReferencelinePropertiesMap()!=null && !graphInfo.getGraphProperties().getLineReferencelinePropertiesMap().isEmpty())
						{
							referencelinePropertiesMap = graphInfo.getGraphProperties().getLineReferencelinePropertiesMap();
							
							objectsview = "/editLineRefenrenceLine";
						}
					}
					String referencelineobjLabel = "";
					String referencelineobjValue = "";
					String referencelineobjStyle = "";
					int referencelineobjWidth = 0;
					String referencelineobjColor = "";
					int referencelineobjKey = 0;
					
					if(referencelinePropertiesMap!=null && !referencelinePropertiesMap.isEmpty()){
						referencelineobjLabel = referencelinePropertiesMap.get(Integer.parseInt(strReferencelineobjkey)).getLabel();
						referencelineobjValue = referencelinePropertiesMap.get(Integer.parseInt(strReferencelineobjkey)).getValue();
						referencelineobjStyle = referencelinePropertiesMap.get(Integer.parseInt(strReferencelineobjkey)).getStyle();
						referencelineobjWidth = referencelinePropertiesMap.get(Integer.parseInt(strReferencelineobjkey)).getWidth();
						referencelineobjColor = referencelinePropertiesMap.get(Integer.parseInt(strReferencelineobjkey)).getColor();
						referencelineobjKey = Integer.parseInt(strReferencelineobjkey);
					}
					
					modelMap.put("referencelineobjLabel",referencelineobjLabel);
					modelMap.put("referencelineobjValue",referencelineobjValue);
					modelMap.put("referencelineobjStyle",referencelineobjStyle);
					modelMap.put("referencelineobjWidth",referencelineobjWidth);
					modelMap.put("referencelineobjColor",referencelineobjColor);
					modelMap.put("referencelineobjKey",referencelineobjKey);
				}
		}
		auditUserActionLog(ResourceManager.getString("LBL_EDIT_GRAPH_REFERENCE_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph"+objectsview);
	}
	
	@RequestMapping (value = "/updateReferenceLineProperties")
	@ResponseBody
	public ModelAndView updateReferenceLineProperties(ModelMap modelMap,@RequestParam("referencelineobjkey") String strReferencelineobjkey
			,@RequestParam("referencelinename") String strReferencelinename
			,@RequestParam("referencelinevalue") String strReferencelinevalue	
			,@RequestParam("referencelinestyle") String strReferencelinestyle
			,@RequestParam("referencelinewidth") String strReferencelinewidth
			,@RequestParam("referencelinecolor") String strReferencelinecolor
			,@RequestParam("fromGraph") String strfromGraph,@LoggedInUser UserInfo userInfo) {
		if(strfromGraph != null && !strfromGraph.equalsIgnoreCase("")) {
			ReferenceLine referencelineProperties = new ReferenceLine();
			String objectsview = "";
			if(strReferencelinename != null && !strReferencelinename.equalsIgnoreCase(""))
			{
				referencelineProperties.setLabel(strReferencelinename);
			}
			if(strReferencelinevalue != null && !strReferencelinevalue.equalsIgnoreCase(""))
			{
				referencelineProperties.setValue(strReferencelinevalue);
			}
			if(strReferencelinestyle != null && !strReferencelinestyle.equalsIgnoreCase(""))
			{
				referencelineProperties.setStyle(strReferencelinestyle);
			}
			if(strReferencelinewidth != null && !strReferencelinewidth.equalsIgnoreCase(""))
			{
				referencelineProperties.setWidth(Integer.parseInt(strReferencelinewidth));
			}
			if(strReferencelinecolor != null && !strReferencelinecolor.equalsIgnoreCase(""))
			{
				referencelineProperties.setColor(strReferencelinecolor);
			}		
			Map<Integer,ReferenceLine> referencelinePropertiesMap  = null;
			
				if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_GENERAL)) {
					if(graphInfo.getGraphProperties().getReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getReferencelinePropertiesMap().isEmpty()) {
						referencelinePropertiesMap = graphInfo.getGraphProperties().getReferencelinePropertiesMap();
					}
					else
					{
						referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
					}
				}
				else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR)) {
					if(graphInfo.getGraphProperties().getBarReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getBarReferencelinePropertiesMap().isEmpty()) {
						referencelinePropertiesMap = graphInfo.getGraphProperties().getBarReferencelinePropertiesMap();
					}
					else
					{
						referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
					}
				}
				else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE)) {
					if(graphInfo.getGraphProperties().getLineReferencelinePropertiesMap()!= null && !graphInfo.getGraphProperties().getLineReferencelinePropertiesMap().isEmpty()){
						referencelinePropertiesMap = graphInfo.getGraphProperties().getLineReferencelinePropertiesMap();
					}
					else
					{
						referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
					}
				}			
			if(referencelinePropertiesMap!= null)
			{
				if(strReferencelineobjkey != null && !strReferencelineobjkey.equalsIgnoreCase(""))
				{
					referencelinePropertiesMap.put(Integer.parseInt(strReferencelineobjkey),referencelineProperties);
				}
			}
			if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_GENERAL)) {						
				modelMap.put("referencelinePropertiesMap",referencelinePropertiesMap);					
				graphInfo.getGraphProperties().setReferencelinePropertiesMap(referencelinePropertiesMap);
				objectsview = "/referencelineobjects";
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_BAR))
			{
				modelMap.put("barreferencelinePropertiesMap",referencelinePropertiesMap);				
				graphInfo.getGraphProperties().setBarReferencelinePropertiesMap(referencelinePropertiesMap);	
				objectsview = "/barReferencelineobjects";
			}
			else if(strfromGraph.equalsIgnoreCase(AppConstants.GRAPH_LINE))
			{
				modelMap.put("linereferencelinePropertiesMap",referencelinePropertiesMap);				
				graphInfo.getGraphProperties().setLineReferencelinePropertiesMap(referencelinePropertiesMap);
				objectsview = "/lineReferencelineobjects";
			}	
			auditUserActionLog(ResourceManager.getString("LBL_ADD_GRAPH_REFERENCE_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
			return new ModelAndView("graph"+objectsview);
		}
		else
		{
			Map<Integer,ReferenceLine> referencelinePropertiesMap  = new HashMap<Integer, ReferenceLine>();
			modelMap.put("referencelinePropertiesMap",referencelinePropertiesMap);
			auditUserActionLog(ResourceManager.getString("LBL_UPDATE_REFERENCE_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
			return new ModelAndView("graph/referencelineobjects");
		}
	}
	
	@ResponseBody
	@RequestMapping ("/drillDown")
	public Object performDrillDown(
			@RequestParam (value = "colName", required = false) String columnName,
			@RequestParam (value = "colValue", required = false) String columnValue,
			@RequestParam (value = "isHomePage", required = false) String homePageFlag,
			@RequestParam (value = "shortCutGraph", required = false) String shortCutGraph,
			@RequestParam (value = "isRow", required = false) String rowDrilldownFlag,
			@RequestParam (value = "isColumn", required = false) String columnDrilldownFlag, 
			@RequestParam (value = "type", required = false) String type, 
			@RequestParam (value = "drillType", required = false) String drillType,
			@RequestParam (value = "isApply", required = false) String isApply,
			@RequestParam (value = "isTreeDrillDown", required = false) String drillDownBrowsingFlag,
			@LoggedInUser UserInfo loggedInUser, HttpServletResponse response, ModelMap map) {

		Object responseText = "";

		Map<String, String> params = new HashMap<>();
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"drill down","set drill down",Thread.currentThread(),loggedInUser,null);
		params.put("colName", columnName);
		params.put("colValue", columnValue);
		params.put("isHomePage", homePageFlag);
		params.put("shortCutGraph", shortCutGraph);
		params.put("isRow", rowDrilldownFlag);
		params.put("isColumn", columnDrilldownFlag);
		params.put("type", type);
		params.put("drillType", drillType);
		params.put("isApply", isApply);
		params.put("isTreeDrillDown", drillDownBrowsingFlag);

		try {
			graphService.setGraphDrillDown(graphInfo, params, loggedInUser);

			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());

			boolean refreshReq = graphInfo.isRefreshReq();
			graphInfo.setRefreshReq(false);

			responseText = refreshObjectData(null,response, loggedInUser, map);

			graphInfo.setRefreshReq(refreshReq);
		} catch (RemoteException e) {
			responseText = ResourceManager.getString("ERROR_FAILED_TO_DRILLDOWN_DRILLUP", new Object[] {e.getMessage()});
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_DRILL_DOWN_OPERATION", new Object[] {
							loggedInUser.getUsername(), getObjectDisplayName() }), e);
		} catch (CubeException e) {
			responseText = ResourceManager.getString("ERROR_FAILED_TO_DRILLDOWN_DRILLUP", new Object[] {e.getMessage()});
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_DRILL_DOWN_OPERATION", new Object[] {
							loggedInUser.getUsername(), getObjectDisplayName() }), e);
		} catch (RScriptException e) {
			responseText = ResourceManager.getString("ERROR_FAILED_TO_DRILLDOWN_DRILLUP", new Object[] {e.getMessage()});
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_DRILL_DOWN_OPERATION", new Object[] {
							loggedInUser.getUsername(), getObjectDisplayName() }), e);
		}
		auditUserActionLog(ResourceManager.getString("LBL_PERFORM_GRAPH_DRILLDOWN"), AppConstants.DETAIL,loggedInUser);
		return responseText;
	}

	@ResponseBody
	@RequestMapping ("/drillUp")
	public Object performDrillUp(
			@RequestParam(value = "isDrillUpPosible", required = false) String isDrillUpPosible,
			@RequestParam(value = "isHomePage", required = false) String isHomePage,
			@RequestParam(value = "shortCutGraph", required = false) String shortCutGraph,
			@RequestParam(value = "rowName", required = false) String dimensionName,
			@RequestParam(value = "rowIndex", required = false) String rowIndex,
			@RequestParam(value = "colIndex", required = false) String colIndex,
			@RequestParam(value = "drillType", required = false) String drillType,
			@LoggedInUser UserInfo userInfo, HttpServletResponse response, ModelMap map) {

		Object responseText = "";
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"drill up","set drill up",Thread.currentThread(),userInfo,null);
		try {
			int iDrillType = 0;

			if (drillType != null && drillType.trim().length() > 0) {
				iDrillType = Integer.parseInt(drillType);
			}

			graphService.setGraphDrillUp(graphInfo, dimensionName,
					Integer.parseInt(rowIndex), Integer.parseInt(colIndex),
					iDrillType, userInfo);

			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());

			boolean refreshReq = graphInfo.isRefreshReq();
			graphInfo.setRefreshReq(false);

			responseText = refreshObjectData(null,response, userInfo, map);

			graphInfo.setRefreshReq(refreshReq);
		} catch (Exception e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_DRILL_UP_OPERATION", new Object[] {
							userInfo.getUsername(), getObjectDisplayName() }), e);

			responseText = ResourceManager.getString("ERROR_FAILED_TO_DRILLDOWN_DRILLUP", new Object[] {e.getMessage()});
		}

		// TODO: For Dashboard!
		//boolean firstTime = false;
		  /*if( isHomePage.equals("false") )
		  	firstTime = Controller.getForDashboardRank();
		  else
		    firstTime = Controller.getDefaultGraphRank();*/
		auditUserActionLog(ResourceManager.getString("LBL_PERFORM_GRAPH_DRILLUP"), AppConstants.DETAIL,userInfo);
	   	return responseText;
	}

	@Override
	public int getColumnTypeByColumnNameAndCubeId(String strCubeId, String columnName,HttpServletRequest request) throws ALSException, CubeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CubeVector getFilterConditionsForMultipleCube(String cubeId, String column,HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@ResponseBody
	@RequestMapping ("/changeGraphType")
	public Object changeGraphType(
			@RequestParam(value = "graphType", required = true) Integer graphType,
			@RequestParam(value = "fromAnalysis", required = true) boolean fromAnalysis,
			@LoggedInUser UserInfo userInfo, HttpServletResponse response, ModelMap map) {

		Object responseText = "";
		graphInfo.setGraphType(graphType);
		graphInfo.setGraphTypeChanged(true);
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Change graph type","set Change graph type",Thread.currentThread(),userInfo,null);
		//11939
		if(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH)
		{
			graphInfo.getGraphProperties().setLineColorType(graphInfo.getGraphProperties().getColorType());
			graphInfo.getGraphProperties().setLineCustomColors(graphInfo.getGraphProperties().getCustomColors());
			graphInfo.getGraphProperties().setLinecolor(graphInfo.getGraphProperties().getColor());
		}
		try {
			graphInfo = graphService.processGraphChangeType(graphInfo, userInfo);
			getDetailInfoMap().put(graphInfo.getGraphId(), graphInfo);
			//graphService.setGraphProperties(graphInfo, userInfo);

			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());

			boolean refreshReq = graphInfo.isRefreshReq();
			graphInfo.setRefreshReq(false);

			responseText = refreshObjectData(null,response, userInfo, map);

			graphInfo.setRefreshReq(refreshReq);
		} catch (Exception ex) {
			String graphMode = (graphInfo.getGraphMode() == AppConstants.NEW_MODE) ? "New" : "Open";

			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_FAILED_TO_CHANGE_GRAPH", new Object[] {
							graphInfo.getGraphName(), graphMode }), ex);

			responseText = ResourceManager.getString("ERROR_FAILED_TO_CHANGE_GRAPH_TYPE",new Object[] {ex.getMessage()});
		}
		
		if(graphInfo.getGraphType().equals(GraphConstants.DOUGHNUT_GRAPH))
		{	
			map.put("changetoDoughnut", true);
		}
		/*for bug 11534*/
		graphInfo.getGraphData().setFromAnalysis(fromAnalysis);
		/*for bug 11534*/
		map.put("fromAnalysis", fromAnalysis);
		auditUserActionLog(ResourceManager.getString("LBL_CHANGE_GRAPH_TYPE"), AppConstants.DETAIL,userInfo);
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Change graph type","set Change graph type",Thread.currentThread(),userInfo,null);
		return responseText;
	}

	@RequestMapping (value = "/addGraphLineProperties")
	@ResponseBody
	public ModelAndView addGraphLineProperties(ModelMap modelMap
			,@RequestParam("style") String strStyle	
			,@RequestParam("thickness") String strThickness
			,@LoggedInUser UserInfo userInfo) {		
		GraphLineSettingProperties graphLineSettingProperties = new GraphLineSettingProperties();
		/*if(strIndex != null && !strIndex.equalsIgnoreCase(""))
		{
			graphLineSettingProperties.setIndex(Integer.parseInt(strIndex));
		}*/
		/*if(strLineColor != null && !strLineColor.equalsIgnoreCase(""))
		{			
			graphLineSettingProperties.setColor(strLineColor);
		}*/
		if(strStyle != null && !strStyle.equalsIgnoreCase(""))
		{			
			graphLineSettingProperties.setStyle(strStyle);
		}
		if(strThickness != null && !strThickness.equalsIgnoreCase(""))
		{
			graphLineSettingProperties.setThickness(strThickness);
		}
		/*if(strPointColor != null && !strPointColor.equalsIgnoreCase(""))
		{			
			graphLineSettingProperties.setPointColor(strPointColor);
		}
		if(strPointstyle != null && !strPointstyle.equalsIgnoreCase(""))
		{
			graphLineSettingProperties.setPointStyle(strPointstyle);
		}
		if(strPointthickness != null && !strPointthickness.equalsIgnoreCase(""))
		{
			graphLineSettingProperties.setPointThickness(strPointthickness);
		}
		if(strBorderwidth != null && !strBorderwidth.equalsIgnoreCase(""))
		{
			graphLineSettingProperties.setBorderwidth(strBorderwidth);
		}
		if(strBorderstyle != null && !strBorderstyle.equalsIgnoreCase(""))
		{
			graphLineSettingProperties.setBorderstyle(strBorderstyle);
		}
		if(strbordercolor != null && !strbordercolor.equalsIgnoreCase(""))
		{
			graphLineSettingProperties.setBordercolor(strbordercolor);
		}
		*/		
		graphInfo.getGraphProperties().getGraphLineProperties().setAllLineCompatibility(true);
		List<GraphLineSettingProperties>  graphlineSettingPropertiesList = null;
		if(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList()!= null && !graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList().isEmpty())
		{
			graphlineSettingPropertiesList = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList();
		}
		else
		{
			graphlineSettingPropertiesList = new ArrayList<GraphLineSettingProperties>();
		}
		if(graphlineSettingPropertiesList!= null)
		{
			graphlineSettingPropertiesList.add(graphLineSettingProperties);
		}		
		graphInfo.getGraphProperties().getGraphLineProperties().setGraphlinePropertiesList(graphlineSettingPropertiesList);
		modelMap.put("graphLineProperties",graphInfo.getGraphProperties().getGraphLineProperties());
		auditUserActionLog(ResourceManager.getString("LBL_ADD_GRAPH_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph/graphlineobjects");
	}
	
	@RequestMapping (value = "/removeGraphlinePropObj")
	@ResponseBody
	public ModelAndView removeGraphlinePropObj(ModelMap modelMap,@RequestParam("graphlineobjkey") String strGraphlineobjkey,@LoggedInUser UserInfo userInfo){
		List<GraphLineSettingProperties>  graphlineSettingPropertiesList = null;	
		if(strGraphlineobjkey != null && !strGraphlineobjkey.equalsIgnoreCase("")){
			if(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList() != null && !graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList().isEmpty())
			{
				graphlineSettingPropertiesList = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList();
				graphlineSettingPropertiesList.remove(Integer.parseInt(strGraphlineobjkey));
				
				graphInfo.getGraphProperties().getGraphLineProperties().setGraphlinePropertiesList(graphlineSettingPropertiesList);
			}
		}
		modelMap.put("graphLineProperties",graphInfo.getGraphProperties().getGraphLineProperties());		
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_GRAPH_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph/graphlineobjects");
	}
	
	@RequestMapping (value = "/editGraphlinePropObj")
	@ResponseBody
	public ModelAndView editGraphlinePropObj(ModelMap modelMap,@RequestParam("graphlineobjkey") String strGraphlineobjkey,@RequestParam("graphlineobjstyle") String graphlineobjstyle,@RequestParam("graphlineobjthikness") String graphlineobjthikness,@LoggedInUser UserInfo userInfo){
		List<GraphLineSettingProperties>  graphlineSettingPropertiesList = null;	
		if(strGraphlineobjkey != null && !strGraphlineobjkey.equalsIgnoreCase("")){
			if(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList() != null && !graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList().isEmpty())
			{
				graphlineSettingPropertiesList = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList();
					
				graphlineSettingPropertiesList.get(Integer.parseInt(strGraphlineobjkey)).setStyle(graphlineobjstyle);
				graphlineSettingPropertiesList.get(Integer.parseInt(strGraphlineobjkey)).setThickness(graphlineobjthikness);
				graphInfo.getGraphProperties().getGraphLineProperties().setGraphlinePropertiesList(graphlineSettingPropertiesList);
			}
		}
		modelMap.put("graphLineProperties",graphInfo.getGraphProperties().getGraphLineProperties());		
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_GRAPH_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph/graphlineobjects");
	}

	@RequestMapping (value = "/editGraphlinePointPropObj")
	@ResponseBody
	public ModelAndView editGraphlinePointPropObj(ModelMap modelMap,@RequestParam("graphlinepointobjkey") String strGraphlineobjkey,@RequestParam("graphlinepointobjstyle") String graphlineobjstyle,@RequestParam("graphlineobjpointthikness") String graphlineobjthikness,@LoggedInUser UserInfo userInfo){
		List<GraphLineSettingProperties>  graphlineSettingPropertiesList = null;	
		if(strGraphlineobjkey != null && !strGraphlineobjkey.equalsIgnoreCase("")){
			if(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList() != null && !graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList().isEmpty())
			{
				graphlineSettingPropertiesList = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList();
					
				graphlineSettingPropertiesList.get(Integer.parseInt(strGraphlineobjkey)).setStyle(graphlineobjstyle);
				graphlineSettingPropertiesList.get(Integer.parseInt(strGraphlineobjkey)).setThickness(graphlineobjthikness);
				graphInfo.getGraphProperties().getGraphLineProperties().setGraphlinePropertiesList(graphlineSettingPropertiesList);
			}
		}
		modelMap.put("graphLineProperties",graphInfo.getGraphProperties().getGraphLineProperties());		
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_GRAPH_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph/graphlineobjects");
	}
	//NO Need in Graph
	@Override
	public void applyKPIGroupTimeFilter(Map<String, String> requestParamMap, UserInfo userInfo) {}

	@ResponseBody
	@RequestMapping (value = "/generateGraphPreviewImage")
	public String generateGraphPreviewImage(
			@RequestParam (value = "rowLabel", required = false) String rowLabel,
			@RequestParam (value = "colLabel", required = false) String colLabel,
			@RequestParam (value = "dataLabel", required = false) String dataLabel,
			@RequestParam (value = "graphHeight", required = false, defaultValue = "1") Integer graphHeight,
			@RequestParam (value = "graphWidth", required = false, defaultValue = "1") Integer graphWidth,
			@RequestParam (value = "barRowLabel", required = false) String barRowLabel,
			@RequestParam (value = "barDataLabel", required = false) String barDataLabel,
			@RequestParam(value = "objectId", required = false,defaultValue="") String strObjectId,			
			@RequestParam(value = "legendIndex", required = false,defaultValue="0") int legendIndex,
			@RequestParam(value = "legendQuantity", required = false,defaultValue="0") int legendQuantity,
			@RequestParam(value = "categoryIndex", required = false,defaultValue="0") int categoryIndex,
			@RequestParam(value = "categoryQuantity", required = false,defaultValue="0") int categoryQuantity,ModelMap map,
			@LoggedInUser UserInfo userInfo) {

		byte[] responseData = new byte[0];

		String graphRowLabel = rowLabel;
	    if(rowLabel != null && !rowLabel.equals("") && rowLabel.indexOf(',')!=-1)
	    	graphRowLabel = graphRowLabel.split(",")[0];
	    if(colLabel != null && !colLabel.equals("") && colLabel.indexOf(',')!=-1)
	    	colLabel = colLabel.split(",")[0];

	    String[] dataLabels = {};
	    if(dataLabel != null && !dataLabel.equals("") && dataLabel.indexOf(',')!=-1) {
	    	dataLabels = dataLabel.split(",");
	    } else {
	    	dataLabels = new String[] {dataLabel};
	    }

	    Map<String, Object> params = new HashMap<String, Object>();

	    params.put("rowLabel", graphRowLabel);
    	params.put("colLabel", colLabel);
    	params.put("dataLabels", dataLabels);
    	params.put("graphHeight", graphHeight);
    	params.put("graphWidth", graphWidth);

    	String barGraphRowLabel = barRowLabel;
    	String[] barDataLabels = {};
		if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {

			//String barGraphRowLabel = barRowLabel;
			if (barRowLabel != null && !barRowLabel.equals("") && barRowLabel.indexOf(',') != -1)
				barGraphRowLabel = barGraphRowLabel.split(",")[0];

			//String[] barDataLabels = {};
			if (barDataLabel != null && !barDataLabel.equals("") && barDataLabel.indexOf(',') != -1) {
				barDataLabels = barDataLabel.split(",");
			} else {
				barDataLabels = new String[] { barDataLabel };
			}

			// Checks whether row and data labels are same for bar and line graph in combined graph
			if (dataLabel.trim().equals(barDataLabel.trim()) && rowLabel.trim().equals(barRowLabel.trim())) {
				params.put("sameConfiguration", "true");
			}

			params.put("barRowLabel", barGraphRowLabel);
			params.put("barDataLabels", barDataLabels);
		}
		
		String json = null;
		try {
			List colList = new ArrayList();
			List rowList = new ArrayList();

			int dataLength = dataLabels.length;
		
			CubeVector<String> orderedColumnInfoList = new CubeVector<String>();
			
			if (graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH) {
				if (!colLabel.isEmpty()) {
					if (graphService.isDimensionInMeasureInUDDC(colLabel, graphInfo, userInfo)) {
						orderedColumnInfoList.add(colLabel);
					}
				}
			}
			
			for(int k = 0;k<dataLabels.length;k++)
			{
				orderedColumnInfoList.add(dataLabels[k]);
			}
			
			if(graphInfo.getGraphType() != GraphConstants.PIE_GRAPH &&
					graphInfo.getGraphType() != GraphConstants.NUMERIC_DIAL_GAUGE)
				graphService.addRemoveYAxisPropertiesFromMap(graphInfo, userInfo, orderedColumnInfoList);
			
			/*
			 * 1:21PM 10/20/2016 added by krishna for multiple measure of pie graph
			 */
			if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
				graphService.addRemoveDataValuesPropertiesFromMap(graphInfo, userInfo.getUserId(), orderedColumnInfoList);
			
			List dataList = new ArrayList();
			dataList = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, dataLabels, 3);

			rowList = graphService.getDummyRowlistForAmchartsPreview(graphRowLabel, colLabel, dataLabels);
			colList = graphService.getDummyCollistForAmchartsPreview(graphRowLabel, colLabel, dataLabels);
			
			Map keyValueMap = new HashMap();
			keyValueMap = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, dataLabels, 3, rowList, colList);

			graphInfo.getGraphData().setColLabel(colLabel);
			graphInfo.getGraphData().setRowLabel(graphRowLabel);
			graphInfo.getGraphData().setDataLabel(dataLabels[0]);

			graphInfo.getGraphData().setColList(colList);
			graphInfo.getGraphData().setRowList(rowList);
			graphInfo.getGraphData().setKeyValueMap(keyValueMap);
			graphInfo.getGraphData().setDataLabel2(dataLabels[0]);
			
			Vector<String> tempColumnNames = new Vector<String>(Arrays.asList(dataLabels));
			graphInfo.setDataColLabels3(tempColumnNames);
			
			List<Integer> colLabelsName = new ArrayList<Integer>();
			graphInfo.getGraphData().setColLabelsName(colLabelsName);
			
			List<Integer> colorInfoListTemp = new ArrayList<Integer>();
			int colorInfoList = rowList.size()+dataList.size();
			if(colorInfoList < colList.size())
				colorInfoList = colList.size();
			for(int m = 0;m < colorInfoList; m++)
			{
				colorInfoListTemp.add(m);
			}
			graphInfo.setColorInfoList(colorInfoListTemp);
			graphInfo.getGraphData().setTotalValueList(dataList);
			
			graphInfo.getGraphData().setDaterowList(new ArrayList());
			graphInfo.getGraphData().setDatecolList(new ArrayList());
			
			if(((rowLabel.equals("") && !colLabel.equals("")) || (colLabel.equals("") && !rowLabel.equals(""))) && dataLength >= 2)
				graphInfo.getGraphData().setRowLabel("Legend");

			if(graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
			{
				if(graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
				{
					if(graphRowLabel.equals("") || colLabel.equals(""))
					{
						graphInfo.getGraphData().setRowLabel("legend");
					}
					
					graphInfo.getGraphData().setStackedTotalValues(new HashMap());
				}
				if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
				{
					graphInfo.getGraphData().setPercentageValueList(dataList);
					Map stackedDataValuesTempMap = new HashMap();
					for(int n = 0;n < dataList.size(); n++)
					{
						stackedDataValuesTempMap.put(n, dataList.get(n));
					}
					graphInfo.getGraphData().setStackedDataTotalValues(stackedDataValuesTempMap);
					graphInfo.getGraphData().setRealPercentageValueList(dataList);
				}
				json = com.elegantjbi.amcharts.BarGraph.amJson(graphInfo,false);
			}

			if(graphInfo.getGraphType() == GraphConstants.DRILLED_RADAR_GRAPH
					||graphInfo.getGraphType() == GraphConstants.DRILLED_STACKED_RADAR_GRAPH)
			{
				json = com.elegantjbi.amcharts.RadarGraph.amJson(graphInfo,false);
			}

			if(graphInfo.getGraphType() == GraphConstants.HISTOGRAM_GRAPH)
			{
				List rangeList = new ArrayList();
				double[] data = new double[dataList.size()];

				for(int i = 0; i < dataList.size(); i++)
				{
					data[i] = Double.valueOf(dataList.get(i).toString());
				}

				Arrays.sort(data);
				double min = data[0];
				double max = data[data.length-1];
				double theWidthOfSection = (max - min)/4;
				double theNextValue = (min);
				int colSize = graphInfo.getGraphProperties().getHistogram().getNoOfBars();
				if(colSize < 3)
				{
					colSize = 5;
				}
				String histogramRange = null;
				for (int i = 0; i < colSize; i++) {
					histogramRange = theNextValue +" - "+( (theNextValue)  + theWidthOfSection - 1);

					if(i == colSize-1)
					{
						histogramRange = theNextValue +" - "+(max);
					}
					theNextValue += (theWidthOfSection);
					rangeList.add(histogramRange);
				}
				dataList = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, dataLabels, colSize);
				graphInfo.getGraphData().setRangeList(rangeList);
				graphInfo.getGraphData().setBinList(dataList);
				json = com.elegantjbi.amcharts.HistogramGraph.amJson(graphInfo,false,false);
			}
			if(graphInfo.getGraphType() == GraphConstants.CANDLE_STICK_GRAPH
					||graphInfo.getGraphType() == GraphConstants.HIGH_LOW_OPEN_CLOSE_GRAPH)
			{
				String candleDataLabel = dataLabel;
				String[] openDataLabels = {};
				openDataLabels = new String[] {candleDataLabel+"_1"};
				String[] closeDataLabels = {};
				closeDataLabels = new String[] {candleDataLabel+"_2"};
				String[] minDataLabels = {};
				minDataLabels = new String[] {candleDataLabel+"_3"};
				String[] maxDataLabels = {};
				maxDataLabels = new String[] {candleDataLabel+"_4"};
				
				Map keyValueMap_Open = new HashMap();
				keyValueMap_Open = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, openDataLabels, 3, rowList, colList);
				Map keyValueMap_Close = new HashMap();
				keyValueMap_Close = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, closeDataLabels, 3, rowList, colList);
				Map keyValueMap_Min = new HashMap();
				keyValueMap_Min = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, minDataLabels, 3, rowList, colList);
				Map keyValueMap_Max = new HashMap();
				keyValueMap_Max = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, maxDataLabels, 3, rowList, colList);

				keyValueMap.putAll(keyValueMap_Open);
				keyValueMap.putAll(keyValueMap_Close);
				keyValueMap.putAll(keyValueMap_Min);
				keyValueMap.putAll(keyValueMap_Max);
				graphInfo.getGraphData().setKeyValueMap(keyValueMap);
				json = CandleStickGraph.amJson(graphInfo);
			}

			if(graphInfo.getGraphType() == com.elegantjbi.service.graph.GraphConstants.HEAT_MAP_GRAPH)
			{
				List rangeList = new ArrayList();
				rangeList.add(0, "0-2");
				rangeList.add(1, "2-4");
				rangeList.add(2, "4-6");
				rangeList.add(3, "6-8");
				rangeList.add(4, "8-10");
				graphInfo.getGraphData().setRangeList(rangeList);
				graphInfo.getGraphData().setTempDataList(dataList);
				json = com.elegantjbi.amcharts.HeatmapGraph.amJson(graphInfo,false);
			}

			if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
			{
				Map keyValueMap2 = new HashMap();

				graphInfo.getGraphData().setCmbBarcolLabel(colLabel);
				graphInfo.getGraphData().setCmbBarrowLabel(rowLabel);
				graphInfo.getGraphData().setCmbBardataLabel(dataLabels[0]);

				graphInfo.getGraphData().setCmbLinecolLabel(colLabel);
				graphInfo.getGraphData().setCmbLinerowLabel(barGraphRowLabel);
				graphInfo.getGraphData().setCmbLinedataLabel(barDataLabels[0]);
				
				if(((rowLabel.equals("") && !colLabel.equals("")) || (colLabel.equals("") && !rowLabel.equals(""))) && dataLength >= 2)
					graphInfo.getGraphData().setCmbBarrowLabel("Legend");
				
				if(((barGraphRowLabel.equals("") && !colLabel.equals("")) || (colLabel.equals("") && !barGraphRowLabel.equals(""))) && barDataLabels.length >= 2)
				{
					graphInfo.getGraphData().setCmbLinerowLabel("Legend");
					
					Vector changedTheDataColLabels4Tmp = new Vector();
					changedTheDataColLabels4Tmp.add(barDataLabels[0]);
					graphInfo.setChangedTheDataColLabels4(changedTheDataColLabels4Tmp);
				}

				graphInfo.getGraphData().setCmbBarcolList(colList);
				graphInfo.getGraphData().setCmbBarrowList(rowList);
				graphInfo.getGraphData().setKeyValueMap(keyValueMap);

				graphInfo.getGraphData().setCmbLinecolList(colList);
				List rowList2 = graphService.getDummyRowlistForAmchartsPreview(barRowLabel, colLabel, barDataLabels);
				graphInfo.getGraphData().setCmbLinerowList(rowList2);
				List<Integer> lineColorInfoListTemp = new ArrayList<Integer>();
				int lineColorInfoList = rowList2.size();
				if(lineColorInfoList < colList.size())
					lineColorInfoList = colList.size(); 
				for(int n = 0;n < lineColorInfoList; n++)
				{
					lineColorInfoListTemp.add(n);
				}
				
				keyValueMap2 = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, barDataLabels, 3, rowList2, colList);
				graphInfo.getGraphData().setKeyValueMapLineCmb(keyValueMap2);
				
				graphInfo.getGraphData().setColLabelsName2(colLabelsName);
				graphInfo.setCmbBarColorInfoList(colorInfoListTemp);
				graphInfo.setCmbLineColorInfoList(lineColorInfoListTemp);

				json = com.elegantjbi.amcharts.AmCombinedGraph.amJson(graphInfo,false);
			}

			if(graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH)
			{
				Vector<String> tempDataVector = new Vector<String>(Arrays.asList(dataLabels));

				Map keyValueMap2 = new HashMap();
				keyValueMap2 = graphService.getDummyDataForAmchartsPreview(graphRowLabel, colLabel, dataLabels, 3, rowList, colList);

				graphInfo.getGraphData().setKeyValueMap(keyValueMap);
				graphInfo.setDataColLabels3(tempDataVector);
				graphInfo.getGraphData().setMeasureInColForBubbLe(false);
				graphInfo.getGraphData().setyAxisTitle(dataLabels[0]);
				graphInfo.getGraphData().setxAxisTitle(colLabel);
				
				if(dataLength > 1)
					graphInfo.getGraphData().setKeyValueMeasureTwoMap(keyValueMap2);//setDataListMeasureTwo

				json = com.elegantjbi.amcharts.BubbleGraph.amJson(graphInfo,false);
			}

			else if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{
				List dataList2 = new ArrayList();
				double[] data = new double[dataList.size()];

				for(int i = 0; i < dataList.size(); i++)
				{
					data[i] = Double.valueOf(dataList.get(i).toString());
					dataList2.add(data[i]);
				}

				graphInfo.getGraphData().setDataList(dataList2);
				json = com.elegantjbi.amcharts.PieGraph.amPieJson(graphInfo,null,false);
			}

			if(graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
			{
				List dataList2 = new ArrayList();
				double[] data = new double[dataList.size()];

				for(int i = 0; i < dataList.size(); i++)
				{
					data[i] = Double.valueOf(dataList.get(i).toString());
					dataList2.add(data[i]);
				}

				graphInfo.getGraphData().setDataList(dataList2);
				json = com.elegantjbi.amcharts.GaugeGraph.amJson(graphInfo);
			}

			/*Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
			if(propertyMap != null) {
				@SuppressWarnings("rawtypes")
				Iterator itr = propertyMap.keySet().iterator();
				while (itr.hasNext()) {
					String key = (String) itr.next();
					map.put(key, propertyMap.get(key));
				}
			}*/

			//}

			//try {
			//responseData = graphService.generateGraphPreviewImage(graphInfo.getGraphType(), params, userInfo.getUserId());
		} catch (Exception e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_GENERATE_GRAPH_PREVIEWIMAGE", new Object[] {
							userInfo.getUsername(), graphInfo.getGraphName() }), e);
		}
		auditUserActionLog(ResourceManager.getString("LBL_GENERATE_GRAPH_PREVIEW_IMAGES"), AppConstants.DETAIL,userInfo);
		return json;
	}

	/**
	 * This method will saves graph object.
	 * 
	 * @param GraphInfo
	 *            graphInfo
	 * @param strFolderId
	 *            FolderId
	 * @return String Result of operation as Message
	 */
	@ResponseBody
	@RequestMapping(value = "/saveGraph", method = RequestMethod.POST)
	public Object saveGraph(
			@ModelAttribute (value = "graphInfo") GraphInfo saveGraphInfo,
			@RequestParam(value = "destFolderId", required = false) String strFolderId,
			@RequestParam(value = "performSave", required = false) boolean performSave, 
			@LoggedInUser UserInfo userInfo, ModelMap modelMap, HttpServletResponse response) {

		String strUpdate = "";
//		auditUserActionLog(ResourceManager.getString("CMD_SAVEAS_GRAPH"), AppConstants.USER_ACCESS,userInfo);
		int result =  0 ;
		FolderInfo folderInfo = null;
		graphInfo.setVersionId(Double.parseDouble(AppContextUtil.getVersion()));
		try{
			if(strFolderId != null && !strFolderId.isEmpty()){
			 folderInfo = repositoryServiceUtil.getFolder(strFolderId);
			} else {
				folderInfo = graphInfo.getFolderInfo();
			}
			result = accessRightServiceUtil.getFolderPermission(userInfo, folderInfo, false);
			if(result >= 2){
				if(graphInfo != null && graphService.isDrillUpPossible(graphInfo))//12699
				{
					graphInfo.setLovListForColor(new ArrayList());
					graphInfo.setLovListForColorBar(new ArrayList());
					graphInfo.setLovListForColorLine(new ArrayList());
				}
				if (performSave) {
					// Save existing graph object
					try {
						boolean isDrillUpPossible = graphService.isDrillUpPossible(graphInfo);
						Object responseStatus = null;
						graphService.checkAndResetDrillUp(graphInfo, true);//for bug 12668
						if (graphService.saveGraph(graphInfo, userInfo, false) != null) {
							boolean refreshReq = graphInfo.isRefreshReq();
							if (isDrillUpPossible) {
								graphInfo.setRefreshReq(true);
							} else {
								graphInfo.setRefreshReq(false);
							}
							response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
							responseStatus = refreshObjectData(null,response, userInfo, modelMap);
							/*
							 * String graphId = graphInfo.getGraphId(); String cacheFilePath = ""; String
							 * cacheFilePathLine = ""; if(graphService.isobjectCacheOnSingleFile()) {
							 * cacheFilePath = graphId.substring(0, graphId.indexOf(".")); } else {
							 * cacheFilePath = graphId.substring(0, graphId.indexOf(".")+1)+
							 * userInfo.getUserId(); }
							 * if(graphInfo.getGraphType()==GraphConstants.COMBINED_GRAPH) {
							 * cacheFilePathLine = cacheFilePath+"_line"+
							 * AppConstants.GRAPH_CUBE_DATA_FILE_EXT; cacheFilePath = cacheFilePath +
							 * AppConstants.GRAPH_CUBE_DATA_FILE_EXT; }
							 * graphService.saveGraphCache(cacheFilePath,graphInfo.
							 * isSkipcubedatasetcolumndataaccesspermission(userInfo),cacheFilePathLine);
							 */
							graphInfo.setRefreshReq(refreshReq);
							
							auditUserActionLog(ResourceManager.getString("LBL_SAVE_OBJECT"), AppConstants.USER_ACCESS,userInfo);
							return responseStatus;
						} else {
							strUpdate = ResourceManager.getString("ERROR_FALITO_SAVE");
						}
					} catch (Exception e) {
						String graphMode = (graphInfo.getGraphMode() == AppConstants.NEW_MODE) ? "New"
								: "Open";
						String message = ResourceManager.getString(
								"LOG_ERROR_FAILED_TO_SAVE_GRAPH", new Object[] {
										graphInfo.getGraphName(), graphMode,
										userInfo.getUsername() })
								+ " " + e.getMessage();
		
						ApplicationLog.error(message, e);
		
						strUpdate = ResourceManager.getString("ERROR_FALITO_SAVE");
					}
				} else {
					if (saveGraphInfo != null) {
						if (saveGraphInfo.getGraphName() == null
								|| saveGraphInfo.getGraphName().trim().equalsIgnoreCase("")) {
							return ResourceManager
									.getString("ERROR_REPORTS_ADD_INVALID_NAME_MSG");
						}
		
						try {
							Map<String, String> graphDisplayNameMap = graphService
									.getGraphDisplayNameByFolderId(strFolderId);
		
							for (Map.Entry<String, String> entry : graphDisplayNameMap
									.entrySet()) {
								String graphId = entry.getKey();
								String displayName = entry.getValue();
								if (graphId.endsWith(AppConstants.GRAPH_FILE_EXT)) {
									if (displayName.equals(saveGraphInfo.getGraphName())) {
										return ResourceManager
												.getString("ERROR_OBJECT_ALREADY_EXIST_ENTER_OTHER_NAME");
									}
								}
							}
						} catch (Exception ex) {
							String graphMode = (graphInfo.getGraphMode() == AppConstants.NEW_MODE) ? "New"
									: "Open";
							String message = ResourceManager.getString(
									"LOG_ERROR_FAILED_TO_SAVE_GRAPH", new Object[] {
											graphInfo.getGraphName(), graphMode,
											userInfo.getUsername() })
									+ " " + ex.getMessage();
		
							ApplicationLog.error(message, ex);
		
							return ResourceManager.getString("ERROR_FALITO_SAVE");
						}
		
						boolean newGraph = (graphInfo.getGraphMode() == AppConstants.NEW_MODE);
						setSaveasProcess(true);
						if (saveGraphInfo != null && !newGraph) {
							// Save as functionality
							
							try {
								graphInfo.setGraphName(StringUtil.unescapeHtmlUtil(saveGraphInfo.getGraphName().trim()));
								graphInfo.getGraphProperties()
										.getTitleProperties()
										.setTitle(
												saveGraphInfo.getGraphProperties()
														.getTitleProperties()
														.getTitle());
		
								graphInfo.setFolderInfo(folderInfo);
								graphInfo.setCreatedBy(userInfo);
								graphInfo.setCreatedDate(new Date());
								graphInfo.setModifiedBy(userInfo);
								graphInfo.setModifiedDate(new Date());
		
								Vector outLinerData =  graphService.getOutLinerDataWithoutSysGeneratedFields(false, graphInfo);
								Vector rowLabeList =(Vector) outLinerData.get(0);
								Vector colLabeList = (Vector)outLinerData.get(1);
								Vector rowLabeList2 = (Vector)outLinerData.get(3);
								graphInfo.setRowColumns(rowLabeList);
								graphInfo.setColColumns(colLabeList);
								graphInfo.setLineGraphRowLabelsForCombinedGraph(rowLabeList2);
								if(graphInfo.getWhatIfConfigurationInfo() != null) {
									graphInfo.getWhatIfConfigurationInfo().setId(0);
								} else {
									graphInfo.setWhatIfConfigurationInfo(new WhatIfConfigurationInfo());
								} // graphInfo save as
								GraphInfo tmpGinfo = null;
								graphService.checkAndResetDrillUp(graphInfo, true);////for bug 12668
								if ((tmpGinfo=graphService.saveGraph(graphInfo, userInfo, true)) != null) {
									strUpdate = AppConstants.SUCCESS_STATUS + ','
											+ graphInfo.getGraphId()
											+ "," + AppConstants.GRAPH;
									
									/*String graphId = graphInfo.getGraphId();
									String cacheFilePath = graphId.substring(0, graphId.indexOf(".")+1) + userInfo.getUserId() + AppConstants.GRAPH_CUBE_DATA_FILE_EXT;
									graphService.saveGraphCache(cacheFilePath);
									*/
									getDetailInfoMap().put(tmpGinfo.getGraphId(), tmpGinfo);
									getServiceMap().put(tmpGinfo.getGraphId(), graphService);
									getDetailInfoMap().remove(graphInfo.getNewGraphId());
									getServiceMap().remove(graphInfo.getNewGraphId());
									
									auditUserActionLog(ResourceManager.getString("LBL_SAVE_NEW_OBJECT"), AppConstants.USER_ACCESS,userInfo);
								} else {
									strUpdate = ResourceManager
											.getString("ERROR_FALITO_SAVE");
								}
							} catch (DatabaseOperationException e) {
								strUpdate = ResourceManager
										.getString("ERROR_FALITO_SAVE");
								String graphMode = (graphInfo.getGraphMode() == AppConstants.NEW_MODE) ? "New"
										: "Open";
								String message = ResourceManager.getString(
										"LOG_ERROR_FAILED_TO_SAVE_GRAPH", new Object[] {
												graphInfo.getGraphName(), graphMode,
												userInfo.getUsername() })
										+ " " + e.getMessage();
		
								ApplicationLog.error(message, e);
							} catch (CubeException e) {
								strUpdate = ResourceManager
										.getString("ERROR_FALITO_SAVE");
								String graphMode = (graphInfo.getGraphMode() == AppConstants.NEW_MODE) ? "New"
										: "Open";
								String message = ResourceManager.getString(
										"LOG_ERROR_FAILED_TO_SAVE_GRAPH", new Object[] {
												graphInfo.getGraphName(), graphMode,
												userInfo.getUsername() })
										+ " " + e.getMessage();
		
								ApplicationLog.error(message, e);
							} catch (Exception ex) {
								String graphMode = (graphInfo.getGraphMode() == AppConstants.NEW_MODE) ? "New"
										: "Open";
								String message = ResourceManager.getString(
										"LOG_ERROR_FAILED_TO_SAVE_GRAPH", new Object[] {
												graphInfo.getGraphName(), graphMode,
												userInfo.getUsername() })
										+ " " + ex.getMessage();
		
								ApplicationLog.error(message, ex);
								strUpdate = ResourceManager
										.getString("ERROR_FALITO_SAVE");
							}
						} else {
							// Save new Graph functionality
							try {
								graphInfo.setGraphName(StringUtil.unescapeHtmlUtil(saveGraphInfo.getGraphName().trim()));
								graphInfo.getGraphProperties()
										.getTitleProperties()
										.setTitle(
												saveGraphInfo.getGraphProperties()
														.getTitleProperties()
														.getTitle());
		
								graphInfo.setFolderInfo(folderInfo);
								graphInfo.setCreatedBy(userInfo);
								graphInfo.setCreatedDate(new Date());
								graphInfo.setModifiedBy(userInfo);
								graphInfo.setModifiedDate(new Date());
		
								GraphInfo tmpGinfo = null;
								if ((tmpGinfo =graphService.saveGraph(graphInfo, userInfo, true)) != null) {
									strUpdate = AppConstants.SUCCESS_STATUS + ','
											+ graphInfo.getGraphId()
											+ "," + AppConstants.GRAPH;
									
									/*String graphId = graphInfo.getGraphId();
									String cacheFilePath = graphId.substring(0, graphId.indexOf(".")+1) + userInfo.getUserId() + AppConstants.GRAPH_CUBE_DATA_FILE_EXT;
									graphService.saveGraphCache(cacheFilePath);
									*/
									getDetailInfoMap().put(tmpGinfo.getGraphId(), graphInfo);
									getServiceMap().put(tmpGinfo.getGraphId(), graphService);
									getDetailInfoMap().remove(graphInfo.getNewGraphId());
									getServiceMap().remove(graphInfo.getNewGraphId());
									
									auditUserActionLog(ResourceManager.getString("LBL_SAVE_NEW_OBJECT"), AppConstants.USER_ACCESS,userInfo);
								} else {
									strUpdate = ResourceManager
											.getString("ERROR_FALITO_SAVE");
								}
							} catch (Exception e) {
								String graphMode = (graphInfo.getGraphMode() == AppConstants.NEW_MODE) ? "New"
										: "Open";
								String message = ResourceManager.getString(
										"LOG_ERROR_FAILED_TO_SAVE_GRAPH", new Object[] {
												graphInfo.getGraphName(), graphMode,
												userInfo.getUsername() })
										+ " " + e.getMessage();
		
								ApplicationLog.error(message, e);
		
								strUpdate = ResourceManager
										.getString("ERROR_FALITO_SAVE");
							}
						}
					}
				}
			} else {
				strUpdate = ResourceManager.getString("ERROR_NO_OBJECT_SAVE_PERMISSION");
			}
		} catch(DatabaseOperationException e){
			ApplicationLog.error(e);
		} catch (ServiceException e1) {
			ApplicationLog.error(e1);
		}
		return strUpdate;
	}

	@Override
	public boolean isFromDashBoard() {return false;}

	@Override
	public void setFilterColumnInformation(String[][] filterColumnInformation,HttpServletRequest request) {}

	@RequestMapping ("/refreshGraphFromAnalysis")
	public ModelAndView refreshGraphFromAnalysis(ModelMap map,HttpServletRequest request, @LoggedInUser UserInfo userInfo) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("isRefreshReq", "true");
		params.put("loggedInUserId",userInfo.getUserId());

		long currentTimeStamp = System.currentTimeMillis();
		GraphProperties graphProperties = null;

		if (request.getAttribute("graphServiceFromAnalysis") != null) {
			graphService = (GraphService) request.getAttribute("graphServiceFromAnalysis");
			graphInfo = (GraphInfo) request.getAttribute("graphInfoFromAnalysis");
		}

		try {
			graphService.initGraphData(graphInfo, params,userInfo);

			graphProperties = graphInfo.getGraphProperties();
		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_REFRESH_GRAPH_FROM_ANALYSIS", new Object[] {
							userInfo.getUsername() }), e);
		} catch (ALSException e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_TO_REFRESH_GRAPH_FROM_ANALYSIS", new Object[] {
							userInfo.getUsername() }), e);
		}

		
		params = new HashMap<String, String>();

		params.put("sCmd", AppMainCommandList.NEW_GRAPH.getM_strCommandName());
		params.put("firstTime" , "true");
		params.put("currentTimeStamp", currentTimeStamp + "");

		generateRequiredItemsForGraph(map, params);

		if (graphInfo.getDrilldownBreadcrumbMap() != null && graphInfo.getDrilldownBreadcrumbMap().size() > 0) {
			map.put("drilldownBreadCrumb", graphInfo.getDrilldownBreadcrumbMap());

			map.put("drillUpLinkToOneLevel", graphInfo.getDrillUpLinkToOneLevel());
		}
		auditUserActionLog(ResourceManager.getString("LBL_REFRESH_GRAPH_FROM_ANALYSIS"), AppConstants.DETAIL,userInfo);
		
		String[] jsonArr =  graphService.generateGraph(graphInfo,"",false,userInfo);
		if(jsonArr.length > 0 && jsonArr[0]==null)
			jsonArr[0]="1";
		map.put("jsonData",jsonArr[0]);		
		map.put("chartSize", jsonArr[1]);
		
		map.put("gaugeLegendInfo", graphInfo.getGaugeData());
		int noOfChartsInRow = 0;
		if(graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
		{
			noOfChartsInRow =  graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getNoOfGauge();
			map.put("titledist", graphInfo.getGraphProperties().getGaugeTitleProperties().getDistanceFromCenter());
		}
		else if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
		{
			if(graphInfo.getGraphProperties().getPieGraph().isClustered())
			{
				noOfChartsInRow = Integer.parseInt(jsonArr[1]);
			}
			else
			{	
				noOfChartsInRow =  graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getNumberofpie();
			}
			map.put("pietitle", graphInfo.getTitleData());
			map.put("nestedgraph", graphInfo.getGraphProperties().getPieGraph().isClustered());
			map.put("pieDrillMap", graphInfo.getGraphData().getPieDrillMap());//Added for NeGD feature request 15075 of Pie drill on Dashboard (24 July 2019)
		}
		if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH  ||	graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
		{
			if(jsonArr[1] == "1")
			{
				map.put("noOfChartsInRow", jsonArr[1]);
			}
			else
			{	
				map.put("noOfChartsInRow", noOfChartsInRow);
			}	
		}
		else
		{
			map.put("nestedgraph", "false");
			map.put("noOfChartsInRow", jsonArr[1]);
		}
		
		map.put("currentTimeStamp", currentTimeStamp);
		map.put("graphType", graphInfo.getGraphType());
		map.put("graphProperties", graphProperties);
		map.put("Mode", AppConstants.OPEN_MODE);
		
		map.put("analysisGraphInfo", graphInfo);
		map.put("fromAnalysis", true);
		
		boolean d3Graph = false;
		if(graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH)
			d3Graph = true;
		map.put("d3Graph",d3Graph);
		String strDateFormat = CalendarUtil.getDataDisplayFormat(userInfo, Types.TIMESTAMP);
		map.addAttribute("strDateFormat",strDateFormat);
		return new ModelAndView("graphFromAnalysis");
	}

	@Override
	public List<String> getObjectList(HttpServletRequest request) { return null; }

	@Override
	public Map<String, List<String>> getPageAssociateMap(HttpServletRequest request) { return null;}

	@Override
	public void setPageAssociateMap(Map<String, List<String>> pageAssociate,HttpServletRequest request) {}
	
	
	@RequestMapping(value = "/exportApi")
	@ResponseBody
	public void exportApi(@RequestParam("exportType") String exportType,
			@RequestParam("objectid") String strObjectId,
			@RequestParam (value="exportToken",defaultValue="", required=false)String exportToken,
			HttpServletResponse response, HttpServletRequest request,
			@LoggedInUser UserInfo userInfo) {
		
		graphService = (GraphService) AppContext.getApplicationContext().getBean("graphService");
		try {
			graphService = (GraphService)ExportServiceUtil.getExportObjectService().get(exportToken);
			 
			
			graphInfo = (GraphInfo)ExportServiceUtil.getExportEntityInfo().get(exportToken);
			
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		this.export(exportType, response, request, userInfo,exportToken);
		
	}
	/**
	 * Export Graph with different type JPG, PDF..
	 * 
	 * @param exportType
	 *            export type like 1-JPG, 2-PDF
	 * @param response
	 *            HttpServletResponse object
	 * @param request
	 *            HttpServletRequest object
	 * @param userInfo
	 *            current logged in info.
	 */
	@RequestMapping(value = "/export")
	@ResponseBody
	public void export(@RequestParam("exportType") String exportType,
			HttpServletResponse response, HttpServletRequest request,
			@LoggedInUser UserInfo userInfo,@RequestParam (value="exportToken",defaultValue="", required=false)String exportToken) {
		setExportInProcess(true);
		OutputStream sout = null;
		String useragent = null;
		String strFileName = "";
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Export","export object",Thread.currentThread(),userInfo,null);
		try {
			useragent = request.getHeader("User-Agent");
			sout = response.getOutputStream();
			strFileName = graphInfo.getGraphName();
			strFileName = formatExportFileName(strFileName);
			/*strFileName = StringUtil.unescapeHtmlUtil(strFileName);
			strFileName = StringUtil.replaceSpecialCharWith(strFileName,
					"[~`!@#$%^&*()--+=\\/,<>.:;\"\' " + (char) 184 + "]", "_");*/
			if (exportType.equals("1")) {
				strFileName = CalendarUtil.getFileSuffix(strFileName) + AppConstants.JPG_EXT;
			} else if (exportType.equals("2")) {
				strFileName = CalendarUtil.getFileSuffix(strFileName) + AppConstants.PDF_EXT;
			} else if (exportType.equals("3")) {
				strFileName = CalendarUtil.getFileSuffix(strFileName) + AppConstants.PNG_EXT;
			} else if (exportType.equals("4")) {
				strFileName = CalendarUtil.getFileSuffix(strFileName) + AppConstants.XLS_EXT;
			} else if (exportType.equals("5")) {
				strFileName = CalendarUtil.getFileSuffix(strFileName) + AppConstants.XLSX_EXT;
			}

			if (useragent.indexOf("Chrome") != -1
					|| useragent.indexOf("Safari") != -1
					|| useragent.indexOf("Opera") != -1) {
				response.setHeader("Content-Disposition",
						"attachment; filename=" + strFileName);
			} else {
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + strFileName);
			}
			if (exportType.equals("1")) {
				response.setContentType("image/jpg");
				graphService.exportToJpg(graphInfo.getGraphId(), userInfo, sout, AppConstants.JPG_EXT, false,exportToken);
			} else if (exportType.equals("2")) {
				response.setContentType("application/pdf");
				graphService.exportToPDF(sout, graphInfo, userInfo.getUserId(),false,exportToken);
			} else if (exportType.equals("3")) {
				response.setContentType("image/png");
				graphService.exportToPng(graphInfo.getGraphId(), userInfo, sout, AppConstants.PNG_EXT,false,exportToken);
			} else if (exportType.equals("4") || exportType.equals("5")) {
				sout = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/ms-excel");
				String strFileExt = AppConstants.XLS_EXT;
				//strExportType = "XLS";
				if (exportType.equals("5")) {
					strFileExt = AppConstants.XLSX_EXT;
					//strExportType = "XLSX";
				}
				graphService.exportXLS(null, sout, graphInfo, strFileExt, userInfo,exportToken);
			}
			auditUserActionLog(ResourceManager.getString("LBL_EXPORT_GRAPH_JPG_PNG_PDF"), AppConstants.DETAIL,userInfo);
		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_EXPORT",
					new Object[] { graphInfo.getGraphName(),
							userInfo.getUsername() }), e);
		} catch (Exception e) {
			ApplicationLog.error(ResourceManager.getString(
					"LOG_ERROR_MSG_FAILED_EXPORT",
					new Object[] { graphInfo.getGraphName(),
							userInfo.getUsername() }), e);
		} finally {
			try {
				if (sout != null) {
					sout.close();
					sout = null;
				}
			} catch (Exception ex) {
			}
			setExportInProcess(false);
		}
	}
	
	@Override
	public String setObjectFromDashboard(HttpServletRequest request) {
		graphService = (GraphService) request.getAttribute("graphService");
		graphInfo = (GraphInfo) request.getAttribute("graphInfo");
		graphService.setGraphImages(graphService.getGraphImages()); 
		graphService.setDashbordTdId("");
		graphInfo.setGraphFromDashBoard(true);//11769 bug	
		graphInfo.setGraphAreaFitToSection(false);
		graphInfo.setGraphFromDashBoard(true);//For bug no 11769
		graphInfo.setWindowScreenHeight((Integer) request.getAttribute("screenHeight"));
		graphInfo.setWindowScreenWidth( (Integer) request.getAttribute("screenWidth"));
		return AppConstants.SUCCESS_STATUS;
	}
	
	@Override
	public ModelAndView openObjectFromDahbaord(ModelMap map,@LoggedInUser UserInfo userInfo) {
		graphService.setObjectMode(AppConstants.OPEN_MODE);
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		try {
		params.put("objectId", graphInfo.getGraphId());		
		Map<String, String> requiredItemsParams = new HashMap<String, String>();
		
		long currentTimestamp = System.currentTimeMillis();
		
		requiredItemsParams.put("sCmd", AppMainCommandList.NEW_GRAPH.getM_strCommandName());
		requiredItemsParams.put("firstTime" , "true");
		requiredItemsParams.put("currentTimeStamp", currentTimestamp + "");
		//requiredItemsParams.put("windowScreenWidth", screenWidth+"");
		//requiredItemsParams.put("windowScreenHeight", screenHeight+"");
		//graphService.doGraphChanged(graphInfo.getGraphType(), true, graphInfo, userInfo.getUserId());
		if(userInfo.isFromAPI() && graphInfo.getDashboardInfo()!=null) {
			String dbId = graphInfo.getDashboardInfo().getDashboardId();
			if(graphInfo.getDashboardInfo().getParentDashboardInfo()!=null) {
				dbId = graphInfo.getDashboardInfo().getParentDashboardInfo().getDashboardId();
			}
			GeneralUtil.setApiParameter(dbId, userInfo, map);
		}
		generateRequiredItemsForGraph(map, requiredItemsParams);
		graphService.lockObject(graphInfo.getGraphId(), AppConstants.GRAPH_TITLE, userInfo.getUserId());
		map.put("currentTimeStamp", currentTimestamp);
		graphInfo.setGraphMode(graphService.getObjectMode());
		auditUserActionLog(ResourceManager.getString("LBL_OPEN_OBJECT_FROM_DASHBOARD"), AppConstants.USER_ACCESS,userInfo);
		if (graphInfo.getGraphType() ==  GraphConstants.NUMERIC_DIAL_GAUGE) {
			map.put("isGaugeGraph", true);
		} else {
			map.put("isGaugeGraph", false);	
		}
		map.addAttribute("Mode", graphService.getObjectMode());
		map.addAttribute("objectType", AppConstants.GRAPH);
		map.put("graphProperties", graphInfo.getGraphProperties());
		map.put("isCombinedGraph", graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH ? true : false);
		String strDateFormat = CalendarUtil.getDataDisplayFormat(userInfo, Types.TIMESTAMP);
		map.addAttribute("strDateFormat",strDateFormat);
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		showAppliedFilter(map,userInfo, null);// Check whether filter is applied or not.
		map.addAttribute("graphInfo",graphInfo);
		map.put("isSetAsHome",userInfo.getHomePage());
		map.put("isDataValueOn", graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible());
		getServiceMap().put(graphInfo.getGraphId(), graphService);
		getDetailInfoMap().put(graphInfo.getGraphId(), graphInfo);

		List<Map<String, Object>> dpList =  new ArrayList<Map<String,Object>>();
		
		String[] jsonArr = new String[2];
		boolean d3Graph = false;
		if(graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH)
		{
			d3Graph = true;
			String d3ScatterJson = graphService.generateD3DataProvider(graphInfo, userInfo);
			map.put("jsonData",d3ScatterJson);	
			map.put("chartSize", 1);
		}
		else
		{	
			//amcharts plotting start
			jsonArr =  graphService.generateGraph(graphInfo,"",false,userInfo);
			if(jsonArr.length > 0 && jsonArr[0]==null)
				jsonArr[0]="1";
			map.put("jsonData",jsonArr[0]);		
			map.put("chartSize", jsonArr[1]);
		//-----------------added by krishna start
		}
		map.put("d3Graph",d3Graph);
		/*String[] jsonArr =  graphService.generateGraph(graphInfo,"",false,userInfo);
		if(jsonArr.length > 0 && jsonArr[0]==null)
			jsonArr[0]="1";
		map.put("jsonData",jsonArr[0]);
		map.put("chartSize", jsonArr[1]);*/
		map.put("completeGraphData",graphInfo.getGraphData().isCompleteGraphData());
		map.put("gaugeLegendInfo", graphInfo.getGaugeData());
		map.put("isLegendVisible",true);
		int noOfChartsInRow = 0;
		if(graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
		{
			noOfChartsInRow =  graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getNoOfGauge();
			map.put("titledist", graphInfo.getGraphProperties().getGaugeTitleProperties().getDistanceFromCenter());
		}
		else if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
		{
			if(graphInfo.getGraphProperties().getPieGraph().isClustered() && jsonArr[1] != null)
			{
				noOfChartsInRow = Integer.parseInt(jsonArr[1]);
			}
			else
			{	
				noOfChartsInRow =  graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getNumberofpie();
			}
			map.put("pietitle", graphInfo.getTitleData());
			map.put("nestedgraph", graphInfo.getGraphProperties().getPieGraph().isClustered());
			map.put("pieDrillMap", graphInfo.getGraphData().getPieDrillMap());//Added for NeGD feature request 15075 of Pie drill on Dashboard (24 July 2019)
		}
		
		if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH  ||	graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
		{
			if(jsonArr[1] == "1")
			{
				map.put("noOfChartsInRow", jsonArr[1]);
			}
			else
			{	
				map.put("noOfChartsInRow", noOfChartsInRow);
			}	
		}
		else
		{
			map.put("nestedgraph", "false");
			map.put("noOfChartsInRow", jsonArr[1]);
		}
		if(null != graphInfo.getDrilldownBreadcrumbMap() && !graphInfo.getDrilldownBreadcrumbMap().isEmpty())//Added to maintain drill Breadcrumb while opening object from dashboard (29 July 2019)
		{
			map.put("drilldownBreadCrumb", graphInfo.getDrilldownBreadcrumbMap());
			map.put("drillUpLinkToOneLevel", graphInfo.getDrillUpLinkToOneLevel());
		}
		
		map.put("graphType", graphInfo.getGraphType());
		map.addAttribute("isFromOpen",true);
		//map.put("gaugeLegendInfo", legends);
		return new ModelAndView("graph");
	}
	
	@Override
	public String backToDashboard(){
		//graphService = (graphService) applicationContext.getBean("analysisService");
		graphInfo = null;
		return AppConstants.SUCCESS_STATUS;
	}
	
	/**
	 * Get objectList.
	 * 
	 * @param modelMap
	 *            ModelMap Object
	 * @param strFolderId
	 *            String
	 * @param strNodeType
	 *            String Object
	 * @return ModelAndView Object
	 */
	@RequestMapping(value = "/loadGraphName")
	@ResponseBody
	public ModelAndView loadGraphName(
			ModelMap modelMap,
			@RequestParam(value = "folderId", required = false) String strFolderId,
			@RequestParam(value = "nodetype", required = false) String strNodeType, @LoggedInUser UserInfo userInfo) {

		List<Repository> biObjectList = repositoryServiceUtil.getRepositoryObjectList(
				userInfo, strFolderId, strNodeType, AppConstants.GRAPH,
				userInfo.isAdmin(), AppConstants.DEFAULT_SORT,
				IApplicationConfigurationService.SORT_ASCENDING, "",false,false);

		modelMap.put("biobjects", biObjectList);
		modelMap.put("folderId", strFolderId);
		return new ModelAndView("/graph/generateGraphNameList");
	}

	/**
	 * Copy Graph Theme
	 * 
	 * @param modelMap
	 *            Model Map Object
	 * @param strGarphId
	 *            graph id
	 * @param userInfo
	 *            user info
	 * @return object of model and view
	 */
	@RequestMapping(value = "/graphCopytheme")
	@ResponseBody
	public ModelAndView copyGraphTheme( ModelMap modelMap, @RequestParam(value = "graphId", required = false) String strGarphId, @LoggedInUser UserInfo userInfo) {

		graphService.copyTheme(strGarphId, graphInfo);

		Map<String,Object> propertyMap = graphService.getGraphPropertiesMap(graphInfo, userInfo);
		if(propertyMap != null) {
			@SuppressWarnings("rawtypes")
			Iterator itr = propertyMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				modelMap.put(key, propertyMap.get(key));
			}
		}
		if(isFromSmarten())
			modelMap.put("isFromSmarten", true);
		
		
		String visibleGraphs = "";
		Map graphsVisibleMap = graphInfo.getGraphProperties().getGraphsVisibleMap();
		// 9 Apr changes [p.p] Bug #14983 [p.p]
		if(graphsVisibleMap != null)
		{
	
			for(int i=0;i<graphInfo.getDataColLabels3().size();i++)
			{
				if(graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i).toString()) != null && graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i).toString()).toString().equals("true"))
					visibleGraphs = visibleGraphs + graphInfo.getDataColLabels3().get(i) + ","; 
			}
			if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
			{
			
				for(int i=0;i<graphInfo.getTheDataColLabels4().size();i++)
				{
					if(graphsVisibleMap.get(graphInfo.getTheDataColLabels4().get(i).toString()) != null && graphsVisibleMap.get(graphInfo.getTheDataColLabels4().get(i).toString()).toString().equals("true"))
						visibleGraphs = visibleGraphs + graphInfo.getTheDataColLabels4().get(i) + ","; 
				}
			
			}
			
		}
	// 9 Apr changes [p.p]
		if(visibleGraphs.endsWith(","))
			visibleGraphs = visibleGraphs.substring(0, visibleGraphs.length() - 1);
		

		modelMap.put("visibleGraphs", visibleGraphs);
		
		auditUserActionLog(ResourceManager.getString("LBL_COPY_GRAPH_THEME"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graphProperties");
	}

	
	@ResponseBody
	@RequestMapping("/getXmlFormatDataForObjectId")
	public String writeXMLString(@RequestParam(value="objectId")String strObjectId,
			@RequestParam(value = AppConstants.API_PAGE_NUMBER,defaultValue="-1") String pageNumber,
			@RequestParam(value = "filterKey") String filterKey,ModelMap map,@LoggedInUser UserInfo userInfo,HttpServletRequest request) {
		
		graphService = (GraphService) AppContext.getApplicationContext().getBean("graphService");
		getServiceMap().put(strObjectId, graphService);

		graphInfo = new GraphInfo();
		
		getDetailInfoMap().put(strObjectId, graphInfo);
		
		graphService.setLoggedInUserId(userInfo.getUserId());
		graphService.setIsFromAnalysis(false);
		Map<String, String> requiredItemsParams = null;
		if (strObjectId != null && strObjectId.trim().length() > 0) {
			graphService.setObjectMode(AppConstants.OPEN_MODE);
		} else {
			strObjectId = "";
		}
		
		if (graphService.getObjectMode() == AppConstants.OPEN_MODE) {
			Hashtable<String, Object> params = new Hashtable<String, Object>();

			params.put("objectId", strObjectId);
			params.put("isFromAPI", userInfo.isFromAPI());

			try {
				graphInfo=graphService.getGraphById(strObjectId);
				//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				String strObjectKey =  strObjectId+AppConstants.API_KEY_SEPERATOR+filterKey;
//				CustomWebAuthenticationDetails customAuthenticationDetails = APICustomAuthenticationProvider.getObjectIdCustomWebDetailMap().get(strObjectKey);
				CustomWebAuthenticationDetails customAuthenticationDetails = null;
				String tenant = CurrentTenantIdentifierResolverImpl.getCurrentTenantIdentifier();
				
				List<ApiTokenVo> tokensByTenant = APICustomAuthenticationProvider.getTokensByTenantMap().get(tenant);
				if (tokensByTenant != null && !tokensByTenant.isEmpty()) {
					ApiTokenVo token = tokensByTenant.stream().filter(t-> t != null && t.getObjectKey().equals(strObjectKey)).findFirst().orElse(null);
		            if(token != null){
		            	customAuthenticationDetails = token.getCustomWebDetail();   	
		            }
				}
	            if(customAuthenticationDetails != null){
	            	//CustomWebAuthenticationDetails customAuthenticationDetails = (CustomWebAuthenticationDetails)auth.getDetails();
	                Map<String, List<String>> filterMap = customAuthenticationDetails.getFilterMap();
	                if(filterMap != null && filterMap.size() > 0) {
	                	//CubeVector filterCondition = CubeParameter.meagreMapForPreloadParameter(graphObjInfo.getActiveTemplateProperties().getPreloadingParameters(), filterMap);
	                	graphInfo.setApiFilterMap(filterMap);
	                	graphInfo.setReadFromCache(false);
	                	Map<String,String> iteams = new HashMap<>();
						List<ActiveGlobalVariableInfo> acGv= graphInfo.getActiveGlobalVariableInfo(userInfo.getUsername());
						for(ActiveGlobalVariableInfo acGlob : acGv) {
							iteams.put(acGlob.getGlobalVariableInfo().getGlobalVariableName().replace("$", ""), acGlob.getGlobalVariableInfo().getTypeString());
						}
	                	request.setAttribute("objectId", strObjectId);
		            	String gv = GeneralFiltersUtil.getGvConditionString(filterMap,graphInfo.getCubeInfo().getId(),iteams);
						if(gv != null && !gv.equals("")) {
							setGlobalVariable(gv, userInfo, false,request);
						}
	                }
	            }
				graphInfo = graphService.initializeGraph(graphService.getObjectMode(), params, userInfo,null, true,graphInfo);
				getDetailInfoMap().put(strObjectId, graphInfo);
				
				
				if (graphInfo.getGraphProperties().getTitleProperties().isTitleVisible()) {
					HashtableEx ddvmList = graphService.getActiveDDVMs(graphInfo, userInfo.getUserId(), false);

					try {
						graphService.setObjectPageTitle(graphInfo.getGraphId(),graphInfo
								.getActiveFilterInfo(userInfo.getUserId()),
								graphService.getPageFilterNew(graphInfo),
								graphService.getActiveVariableMap(), graphService.getResultSetMetaData(),
								graphInfo.getCubeInfo(), graphInfo.getGraphProperties().getTitleProperties(),
								userInfo, ddvmList);
					} catch (CubeException e) {
						ApplicationLog.error(e);
					}
				}
			
			} catch (DatabaseOperationException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(), graphService.getObjectMode() }), e);
			} catch (CubeException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(), graphService.getObjectMode() }), e);
			} catch (IOException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(), graphService.getObjectMode() }), e);
			} catch (NotBoundException e) {
				ApplicationLog.error(ResourceManager.getString("LOG_ERROR_MSG_FAILED_TO_INITIALIZEGRAPH",
						new Object[] { userInfo.getUsername(),graphService.getObjectMode() }), e);
			} catch (ObjectAccessException e) {
				return ResourceManager.getString("ERROR_NO_ACCESS_PERMISSION");
			} catch (ObjectNotFoundException e) {
				return ResourceManager.getString("ERROR_NO_ACCESS_PERMISSION");
			} catch (CubeNotFoundException e) {
				return ResourceManager.getString("ERROR_NO_ACCESS_PERMISSION");
			} catch (CubeAccessException e) {
				return ResourceManager.getString("ERROR_NO_ACCESS_PERMISSION");
			} catch (RScriptException e) {
				return ResourceManager.getString("ERROR_NO_ACCESS_PERMISSION")+e.getMessage();
			} catch (ALSException e) {
				return e.getMessage();
			} 

			
			long currentTimestamp = System.currentTimeMillis();
			requiredItemsParams = new HashMap<String, String>();
			requiredItemsParams.put("sCmd", AppMainCommandList.NEW_GRAPH.getM_strCommandName());
			requiredItemsParams.put("firstTime" , "true");
			requiredItemsParams.put("currentTimeStamp", currentTimestamp + "");

			generateRequiredItemsForGraph(map, requiredItemsParams);
			map.put("currentTimeStamp", currentTimestamp);
		}

		graphInfo.setGraphMode(graphService.getObjectMode());
		graphService.getFormatedDataList(graphInfo, userInfo).clone();
		if (graphInfo.getGraphType() ==  GraphConstants.NUMERIC_DIAL_GAUGE) {
			map.put("isGaugeGraph", true);
		} else {
			map.put("isGaugeGraph", false);	
		}
		String xmlString = "";
		try(OutputStream output = new OutputStream(){
	        private StringBuilder string = new StringBuilder();
	        @Override
	        public void write(int b) throws IOException {
	            this.string.append((char) b );
	        }

	        public String toString(){
	            return this.string.toString();
	        }
	    };)
	     {
			graphService.exportGraphDataXML(output, graphInfo,userInfo,Integer.parseInt(pageNumber));
			xmlString =  output.toString();
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
	    
		return xmlString;
	}
	
	@Override
	public List<String> getCommonDimensionList(UserInfo userInfo,HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String savePDFPageSetupInfo(PDFPageSetupInfo pdfPageSetupInfo,
			ModelMap modelMap, HttpServletRequest request) {
		graphInfo.setPdfPageSetup(pdfPageSetupInfo);
		UserInfo userInfo = (UserInfo) request.getAttribute("userInfo");
		detailedMonitorEndpoint.setProcessLog(Thread.currentThread().getId(),graphInfo.getGraphName(),graphInfo.getGraphId(),"Pdf Page setup","Save pdf page setup",Thread.currentThread(),userInfo,new Date());
		return AppConstants.SUCCESS_STATUS;
	}

	@Override
	public List<IDataObject> getCubeListFromDifferentObject(UserInfo userInfo,HttpServletRequest request)
			throws DatabaseOperationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addVirtualItems(HashMap<String, Vector<String>> columnMap) {
		graphService.addVirtualItems(graphInfo, columnMap);
	}

	@Override
	public List<String> getVirtualMeasureList() {
		return graphService.getVirtualMeasureList(graphInfo);
	}

	@Override
	public List<SelectItem> getItemDDVMApplyName(List<Object> objects,
			String strUserId, String strColumnName) throws ALSException,
			CubeException {
		return  graphService.getItemDDVMApplyName(graphInfo, objects, strUserId, strColumnName);
		
	}

	@Override
	public boolean isPageFilterToolTip(String objectId) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void auditUserActionLog(String action, String msgLevel, UserInfo user) {
		GeneralUtil.auditUserActionLog(graphInfo, user, getStrParentHierchy(), action, msgLevel);
	}

	@Override
	public void unlockObject(List<String> objList,HttpServletRequest request) {
		try {
			if(graphService != null){
				graphService.unLockObject(objList);
				if(!isSaveasProcess()) {
					graphService.clear();
				}
			}
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		}
	}
	
	@Override
	public boolean checkForUpdate(String objectId, String userId) {
		boolean bUpdate = false;
		try {
			if(graphService != null){
				bUpdate = graphService.checkForUpdate(objectId, userId);
			}
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		}
		return bUpdate;
	}
	
	@Override
	public void setUpdateByObjectAndUserId(String objectId, String userId,
			boolean update) {
		try {
			graphService.setUpdateByObjectAndUserId(objectId, userId,update);
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		}
		
	}
	
	@Override
	public boolean checkForObjectAccessByAnyUser(String objectId, String userId) {
		boolean bUpdate = false;
		try {
			bUpdate = graphService.checkForObjectAccessByAnyUser(objectId, userId);
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		}
		return bUpdate;
	}

	@Override
	public void setService(ObjectService objectService) {
		if(graphInfo != null &&  graphInfo.getDashboardInfo() == null){
			graphService = (GraphService) objectService;
		}
	}

	@Override
	public void setDetailInfo(IEntity entity) {
		if(graphInfo != null &&  graphInfo.getDashboardInfo() == null){
			graphInfo = (GraphInfo) entity;
		}
	}
	
	public GraphInfo getGraphObjectFromMap(String objId) {
		GraphInfo graphObjInfo = null;
		if(objId!=null && !objId.trim().isEmpty()) {
			graphObjInfo = (GraphInfo) getDetailInfoMap().get(objId);
		}
		if(objId != null && !objId.isEmpty() && null != graphInfo && graphInfo.getDashboardInfo() == null){
			graphObjInfo = (GraphInfo) getDetailInfoMap().get(objId);
		}
		
		if(graphObjInfo != null){
			return graphObjInfo;
		} else {
			graphObjInfo = graphInfo;
		}
		return graphObjInfo;
	}

	public GraphService getGraphServiceFromMap(String objId) {
		GraphService graphServiceObj = null;
		if(objId!=null && !objId.trim().isEmpty()) {
			graphServiceObj = (GraphService) getServiceMap().get(objId);
		}
		if(objId != null && !objId.isEmpty() && graphInfo!=null && graphInfo.getDashboardInfo() == null){
			graphServiceObj = (GraphService) getServiceMap().get(objId);
		}
		if(graphServiceObj != null){
			return graphServiceObj;
		} else {
			graphServiceObj = graphService;
		}
		return graphServiceObj;
	}
	
	@Override
	public String checkColumnHeader(String strColumnName) {
		return strColumnName;
		
	}

	@Override
	public boolean isMDXCube() {
		boolean isMDXCube = graphInfo.getCubeInfo().isMdxCube();
		return isMDXCube;
	}
	
	@Override
	public ModelAndView showAppliedFilter(ModelMap modelMap,@LoggedInUser UserInfo uInfo, String objectId) {
		GraphService graphService=this.graphService;
		GraphInfo graphInfo = this.graphInfo;
		if(objectId!=null && !objectId.isEmpty()) {
			graphInfo = (GraphInfo)getGraphObjectFromMap(objectId);
			graphService=(GraphService)getGraphServiceFromMap(objectId);
		}
		graphService.getAppliedFilterInfo(graphInfo,uInfo,modelMap,getCubeInfo(objectId));
		String logAction = ResourceManager.getString("LBL_APPLIED_FILTER_DIALOG");		
		auditUserActionLog(logAction, AppConstants.DETAIL, uInfo);
		// summary operations end
		return new ModelAndView("graphAppliedFilterInformation");
	}

	@ResponseBody
	@RequestMapping(value = "/applyGraphsPagination")
	public String applyGraphsPagination(
			@RequestParam(value = "objectId", required = false) String strObjectId,
			@RequestParam(value = "legendIndex", required = false) int legendIndex,
			@RequestParam(value = "legendQuantity", required = false) int legendQuantity,ModelMap map,
			@LoggedInUser UserInfo userInfo) {
		ObjectMapper objectMapper = new ObjectMapper();		
		List<Graphs> graphsList = new ArrayList<Graphs>();
		GraphInfo graphInfo=this.graphInfo; 
		GraphService graphService=this.graphService;
		
		if(strObjectId!=null && !strObjectId.isEmpty()) {
			graphInfo = (GraphInfo)getGraphObjectFromMap(strObjectId);
			graphService=(GraphService)getGraphServiceFromMap(strObjectId);
		}
		//11531
		if(graphInfo.getColorInfoList().isEmpty())
		{
			List<Integer> colorInfoListTemp = new ArrayList<Integer>();
			colorInfoListTemp.add(0);
			graphInfo.setColorInfoList(colorInfoListTemp);
		}
		if(graphInfo.getCmbBarColorInfoList().isEmpty())
		{
			List<Integer> cmbBarColorInfoList = new ArrayList<Integer>();
			cmbBarColorInfoList.add(0);
			graphInfo.setCmbBarColorInfoList(cmbBarColorInfoList);
		}
		if(graphInfo.getCmbLineColorInfoList().isEmpty())
		{
			List<Integer> cmbLineColorInfoList = new ArrayList<Integer>();
			cmbLineColorInfoList.add(0);
			graphInfo.setCmbLineColorInfoList(cmbLineColorInfoList);
		}
		//11531
		
		if(null != graphInfo.getGraphData().getKeyValueMap() && graphInfo.getGraphData().getKeyValueMap().size() > 0)
			graphsList=graphService.generateGraphs(graphInfo,legendIndex,legendQuantity);
		
		String json="";	
		
		if(graphInfo.getGraphType() == GraphConstants.BUBBLE_GRAPH &&  null != graphInfo && graphInfo.getGraphData().isMeasureInColForBubbLe())
		{
			List<Map<String, Object>>  dataRulesMap=graphsList.get(0).getDataRulesMap();
			if(dataRulesMap.size()==0)
			{
				json="1";
				return json;				
			}	
			try {
				json = objectMapper.writeValueAsString(dataRulesMap);				
			} catch (IOException e) {
				ApplicationLog.error(e);
			}
			
		}else{
		
		
		try {
			if(graphsList.size()==0)
			{
				json="1";
				return json;				
			}	
			json = objectMapper.writeValueAsString(graphsList);
			
		}catch (IOException e) {
			ApplicationLog.error(e);
		}		

		}
		//ApplicationLog.debug("graphs:="+json);
		return json;
				
	}

	@ResponseBody
	@RequestMapping(value = "/applyPagination")
	public String applyPagination(
			@RequestParam(value = "objectId", required = false,defaultValue="") String strObjectId,			
			@RequestParam(value = "legendIndex", required = false,defaultValue="0") int legendIndex,
			@RequestParam(value = "legendQuantity", required = false,defaultValue="0") int legendQuantity,
			@RequestParam(value = "categoryIndex", required = false,defaultValue="0") int categoryIndex,
			@RequestParam(value = "categoryQuantity", required = false,defaultValue="0") int categoryQuantity,ModelMap map,
			@LoggedInUser UserInfo userInfo) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> dpList =  new ArrayList<Map<String,Object>>();
		
		GraphInfo graphInfo=this.graphInfo; 
		GraphService graphService=this.graphService;
		
		if(strObjectId!=null && !strObjectId.isEmpty()) {
			graphInfo = (GraphInfo)getGraphObjectFromMap(strObjectId);
			graphService=(GraphService)getGraphServiceFromMap(strObjectId);
		}
		
		dpList=graphService.generateDataProviderGraph(graphInfo,legendIndex,legendQuantity,categoryIndex,categoryQuantity);
		
		String json="";	
		try {
			if(dpList.isEmpty())
			{
				json="1";
				return json;				
			}	
			json = objectMapper.writeValueAsString(dpList);			
		} catch (IOException e) {
			ApplicationLog.error(e);
		}
		//ApplicationLog.debug("data:="+json);
		return json;
				
	}

	@Override
	public boolean isRealTimeCube() {
		return  graphInfo.getCubeInfo().isRealTimeCube();
	}

	@Override
	public WhatIfConfigurationInfo getWhatIfConfigurationInfo(String objectId) {
		// TODO Auto-generated method stub
		if(objectId!=null && !objectId.isEmpty()) {
			return getGraphObjectFromMap(objectId).getWhatIfConfigurationInfo();
		}
		return graphInfo.getWhatIfConfigurationInfo();
	}

	@Override
	public void setWhatIfConfigurationInfo(WhatIfConfigurationInfo whatIfConfigurationInfo, String objectId) {
		// TODO Auto-generated method stub
		if(objectId!=null && !objectId.isEmpty()) {
			getGraphObjectFromMap(objectId).setWhatIfConfigurationInfo(whatIfConfigurationInfo);
		}
		graphInfo.setWhatIfConfigurationInfo(whatIfConfigurationInfo);
	}
	
	@Override
	public List<SelectItem> getDataDisplayValueMappingOnLOV(HashtableEx ddvmMap, List<Object> cubeDataValueList,
			String userId, int itemType, String strColumnName, String objectId) {
		// TODO Auto-generated method stub
		List<SelectItem> lovMappingValue = graphService.getDataDisplayValueMappingOnLOV(ddvmMap, cubeDataValueList, userId, itemType, strColumnName);
		return lovMappingValue;
	}


	@Override
	public int getGraphType() {
		return graphInfo.getGraphType();
	}

	@Override
	public HashtableEx getActiveDDVMs(String loggedInUserId) {
		return graphService.getActiveDDVMs(graphInfo, loggedInUserId, false);
	}

	@Override
	public String getJSON(ConcurrentHashMap inputMap,HttpServletRequest request) {
		// TODO Auto-generated method stub
		 String values = graphService.getJSONFromService(inputMap);
			return values;
	}


	@Override
	public HashtableEx getRetrivalParametersWithDDVM(String columnName, UserInfo userInfo) {	
		HashtableEx ddvmMap = new HashtableEx();
			ddvmMap = (HashtableEx) graphService.getActiveDDVMs(graphInfo, userInfo.getUserId(), false);
			return ddvmMap;
	}
	@RequestMapping (value = "/addGraphLinePointProperties")
	@ResponseBody
	public ModelAndView addGraphLinePointProperties(ModelMap modelMap
			,@RequestParam("style") String strStyle	
			,@RequestParam("thickness") String strThickness
			,@RequestParam("borderwidth") String strBorderwidth
			,@RequestParam("borderstyle") String strBorderstyle
			,@RequestParam("bordercolor") String strbordercolor
			,@LoggedInUser UserInfo userInfo) {		
		GraphLineSettingProperties graphLinePointSettingProperties = new GraphLineSettingProperties();
		if(strStyle != null && !strStyle.equalsIgnoreCase(""))
		{			
			graphLinePointSettingProperties.setStyle(strStyle);
		}
		if(strThickness != null && !strThickness.equalsIgnoreCase(""))
		{
			graphLinePointSettingProperties.setThickness(strThickness);
		}
		if(strBorderwidth != null && !strBorderwidth.equalsIgnoreCase(""))
		{
			graphLinePointSettingProperties.setBorderwidth(strBorderwidth);
		}
		if(strBorderstyle != null && !strBorderstyle.equalsIgnoreCase(""))
		{
			graphLinePointSettingProperties.setBorderstyle(strBorderstyle);
		}
		if(strbordercolor != null && !strbordercolor.equalsIgnoreCase(""))
		{
			graphLinePointSettingProperties.setBordercolor(strbordercolor);
		}
		
		List<GraphLineSettingProperties>  graphlinepointSettingPropertiesList = null;
		if(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList()!= null && !graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().isEmpty())
		{
			graphlinepointSettingPropertiesList = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList();
		}
		else
		{
			graphlinepointSettingPropertiesList = new ArrayList<GraphLineSettingProperties>();
		}
		if(graphlinepointSettingPropertiesList!= null)
		{
			graphlinepointSettingPropertiesList.add(graphLinePointSettingProperties);
		}		
		graphInfo.getGraphProperties().getGraphLineProperties().setGraphlinepointPropertiesList(graphlinepointSettingPropertiesList);
		modelMap.put("graphLineProperties",graphInfo.getGraphProperties().getGraphLineProperties());
		auditUserActionLog(ResourceManager.getString("LBL_ADD_GRAPH_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph/graphlinepointobjects");
	}
	
	@RequestMapping (value = "/removeGraphlinepointPropObj")
	@ResponseBody
	public ModelAndView removeGraphlinepointPropObj(ModelMap modelMap,@RequestParam("graphlineobjkey") String strGraphlineobjkey,@LoggedInUser UserInfo userInfo){
		List<GraphLineSettingProperties>  graphlinepointSettingPropertiesList = null;	
		if(strGraphlineobjkey != null && !strGraphlineobjkey.equalsIgnoreCase("")){
			if(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList() != null && !graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().isEmpty())
			{
				graphlinepointSettingPropertiesList = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList();
				graphlinepointSettingPropertiesList.remove(Integer.parseInt(strGraphlineobjkey));								
				graphInfo.getGraphProperties().getGraphLineProperties().setGraphlinepointPropertiesList(graphlinepointSettingPropertiesList);
			}
		}
		modelMap.put("graphLineProperties",graphInfo.getGraphProperties().getGraphLineProperties());		
		auditUserActionLog(ResourceManager.getString("LBL_DELETE_GRAPH_LINE_PROPERTIES"), AppConstants.DETAIL,userInfo);
		return new ModelAndView("graph/graphlinepointobjects");
	}
	
	@RequestMapping(value = "/addpagefilter")
	public ModelAndView addPageFilter(ModelMap map,@LoggedInUser UserInfo userInfo) {		
			Vector dimensionColumns = new Vector();
			String dimensionColumnStr = "";
			String selectedColumnStr = "";
			GraphInfo graphInfo = getGraphInfo();
			try {
				if(graphInfo.getMultiPageFilterInfo() != null && graphInfo.getMultiPageFilterInfo().length >0) {	
					String temparr[][] = new String[graphService.getPageFilters(graphInfo).length][];
					temparr = graphService.getPageFilters(graphInfo);
					for(int i=0;i<temparr.length;i++)
					{
						selectedColumnStr += temparr[i][0]+","; 
					}
				
				}
				IDataObject cubeInfo = null;
				if(graphInfo.getCubeInfo().getDataObjectType() == ICubeConstants.REALTIME_R_CUBE) {
					try {
						cubeInfo = cubeDataServiceUtil.getCubeByCubeId(graphInfo.getCubeInfo().getrInputCubeId());
					} catch (DatabaseOperationException e) {
						ApplicationLog.error(e);
					}
				}else {
					cubeInfo = graphInfo.getCubeInfo();
				}
				dimensionColumns = cubeMetadataServiceUtil.getDimensionColumns(cubeInfo,userInfo,graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo));
			} catch (CubeException e) {
				ApplicationLog.error(e);
			}			
			for(int i=0;i<dimensionColumns.size();i++) {
				dimensionColumnStr += dimensionColumns.get(i).toString()+",";			
			}		
			if(dimensionColumnStr != null && dimensionColumnStr.length()>0){	
				dimensionColumnStr = dimensionColumnStr.substring(0,dimensionColumnStr.length()-1);
			}
			if(selectedColumnStr != null && selectedColumnStr.length()>0){
				selectedColumnStr = selectedColumnStr.substring(0,selectedColumnStr.length()-1);
			}
			map.put("dimensionColumnStr",dimensionColumnStr);
			map.put("selectedColumnStr",selectedColumnStr);
			map.put("cubeInfo",graphInfo.getCubeInfo());
			return new ModelAndView("addpagefilter");
	}
	
	/**
	 * This method is use to Save Page Filters
	 * @return String
	 * @throws CubeException 
	 */
	@RequestMapping(value = "/savePageFilterColumns")
	@ResponseBody
	public Object savePageFilterColumns(@RequestParam(value = "cubeId", required = false) String strCubeId,
			@RequestParam(value = "filterval", required = false) String strFilterval,ModelMap modelMap,HttpServletResponse response,
			@LoggedInUser UserInfo userInfo) {
		GraphInfo graphInfo = getGraphInfo();
		String cubeDisplayName = "";
		try {
			HashMap conditionMap = graphService.getFilterConditions();
			if(strFilterval != null) {
				String[][] columnInfo = graphService.getPageFilters(graphInfo);
				String[][] finalColumnInfo = null;

				if (strFilterval.length() > 0) {
					String tempStr[] = strFilterval.split(",");
					finalColumnInfo = new String[tempStr.length][];
					if(columnInfo != null && columnInfo.length > 0) {
						for(int i=0;i<tempStr.length;i++) {
							boolean contains = false;
							for(int j=0; j<columnInfo.length; j++) {
								contains = tempStr[i].equals(columnInfo[j][0]);
								if(contains) {
									finalColumnInfo[i] = new String[8];
									finalColumnInfo[i][0] = columnInfo[j][0];
									finalColumnInfo[i][1] = columnInfo[j][1];
									finalColumnInfo[i][2] = columnInfo[j][2];
									finalColumnInfo[i][4] = columnInfo[j][4];
									finalColumnInfo[i][7] = columnInfo[j][7];
									break;
								}
							}
							if(!contains) {
								int iType = -1;
								try{
									IDataObject cubeInfo = null;
									if(graphInfo.getCubeInfo().getDataObjectType() == ICubeConstants.REALTIME_R_CUBE) {
										cubeInfo = cubeDataServiceUtil.getCubeByCubeId(graphInfo.getCubeInfo().getrInputCubeId());
										cubeDisplayName = cubeInfo.getDataObjecName();
									}else {
										cubeInfo = graphInfo.getCubeInfo();
										cubeDisplayName = cubeInfo.getDataObjecName();
									}
									iType = GeneralFiltersUtil.getCubeColumnType(cubeInfo,tempStr[i]);
								} catch (CubeException e) {
									ApplicationLog.error(e);
								}	
								finalColumnInfo[i] = new String[8];	
								finalColumnInfo[i][0]= tempStr[i];
								finalColumnInfo[i][1] = String.valueOf(iType);
								finalColumnInfo[i][2] = ResourceManager.getString("NONE");
								finalColumnInfo[i][7] = graphInfo.getCubeInfo().getDataObjecName();
							}
						}
					} else {
						for(int i=0;i<tempStr.length;i++) { 
							int iType = -1;
							try{
								IDataObject cubeInfo = null;
								if(graphInfo.getCubeInfo().getDataObjectType() == ICubeConstants.REALTIME_R_CUBE) {
									cubeInfo = cubeDataServiceUtil.getCubeByCubeId(graphInfo.getCubeInfo().getrInputCubeId());
									cubeDisplayName = cubeInfo.getDataObjecName();
								}else {
									cubeInfo = graphInfo.getCubeInfo();
									cubeDisplayName = cubeInfo.getDataObjecName();
								}
								iType = GeneralFiltersUtil.getCubeColumnType(cubeInfo,tempStr[i]);
							} catch (CubeException e) {
								ApplicationLog.error(e);
							}	
							finalColumnInfo[i] = new String[8];	
							finalColumnInfo[i][0]= tempStr[i];
							finalColumnInfo[i][1] = String.valueOf(iType);
							finalColumnInfo[i][2] = ResourceManager.getString("NONE");
							finalColumnInfo[i][7] = graphInfo.getCubeInfo().getDataObjecName();
						}
					}
					
					for(Object key : conditionMap.keySet()) {
						boolean contains = false;
						for(int i=0; i<tempStr.length; i++) {
							contains = tempStr[i].equals(key.toString());
							if(contains)
								break;
						}
						/*if(!contains)
							graphService.setFilterConditions(key.toString(), new CubeVector<>(), graphInfo);*/
					}
					
				} else {
					columnInfo = new String[][] {};
					/*graphService.setFilterConditions(new HashMap<>(), true, graphInfo);*/
				}
				graphInfo.setMultiPageFilterInfo(finalColumnInfo);
				List<String> strColumnList = new ArrayList<String>();
				if(finalColumnInfo != null && finalColumnInfo.length > 0) {
					for(int i = 0; i< finalColumnInfo.length; i++) {
						strColumnList.add(finalColumnInfo[i][0]);
					}
				}
				/*graphService.setFilterInfoList(strColumnList.toArray(),graphInfo,new CubeVector<>());*/
			}
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		String logAction = ResourceManager.getString("LBL_SAVE_PAGE_FILTER_COLUMNS");		
		auditUserActionLog(logAction, AppConstants.DETAIL,userInfo);
		response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		return refreshObjectData(null,response, userInfo, modelMap);
	}
	
	@RequestMapping (value = "/showObjectloadInfoPage")
	public ModelAndView showObjectloadInfoPage(ModelMap model,@LoggedInUser UserInfo userInfo) {
    	
		try {
			if(getGraphInfo().getOnLoadObjectInfo() != null && !getGraphInfo().getOnLoadObjectInfo().isEmpty() 
					&& getGraphInfo().getOnLoadObjectInfo().get(0) != null && !getGraphInfo().getOnLoadObjectInfo().get(0).isEmpty()) {
				model.put("strTmp", getGraphInfo().getOnLoadObjectInfo().get(0));
			}
			if(getGraphInfo().getOnLoadObjectInfo() != null && !getGraphInfo().getOnLoadObjectInfo().isEmpty() 
					&& getGraphInfo().getOnLoadObjectInfo().get(1) != null && !getGraphInfo().getOnLoadObjectInfo().get(1).isEmpty()) {
				model.put("strTemplate", getGraphInfo().getOnLoadObjectInfo().get(1));
			}
			getGraphInfo().setOnLoadObjectInfo(null);
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		return  new ModelAndView("showObjectloadInfoPage");
	}

	@Override
	public Map<String, RProfileInfo> getDashboardCubeIdAndRProfileMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IDataObject> getDashboardCubeIdAndInputCubeInfoMap(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RScriptInputOutputVO> getrScriptInputVOs() {
		return graphService.getrScriptInputVOs();
	}

	@Override
	public void setrScriptInputVOs(List<RScriptInputOutputVO> rScriptInputVOs) {
		graphService.setrScriptInputVOs(rScriptInputVOs);
	}
	
	@ModelAttribute
	public void getCommonModelMap(ModelMap model) {
		if(graphService != null)
			model.addAttribute("isAdaptive",graphService.getAdaptiveBehaviour());		
	}

	@Override
	public boolean isFromSmarten() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Object> getCubeWiseDimensionMap(List<IDataObject> cubeList, boolean showAll, UserInfo userInfo,HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getCommonColumnListFromCubes(Map cubeDimensionMap, UserInfo userInfo,HttpServletRequest request,List<IDataObject> cubeList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSingleColumnSort() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> prepareMeasureItemsList(UserInfo userInfo) throws ALSException, CubeException {
		return graphInfo.getDataColumns();
	}

	@Override
	public Map<String, ArrayList<String>> prepareLatitudeLongitudeMap(UserInfo userInfo)
			throws ALSException, CubeException {
		return null;
	}

	@Override
	public void setViewStruct(CubeVector cubeVector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CubeViewInfo getViewStruct() {		
		try {
			return graphService.getViewStruct();
		} catch (CubeException e) {
			ApplicationLog.error(e);
		}
		return null;
	}

	@Override
	public ArrayList<String> getOrderdAllDimensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CubeVector getOrderedDataColumnInfoList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@PreDestroy 
	private void preDestoy() {
		ApplicationLog.debug("GraphContoller == >> Destory Call");
		try {
		if(getServiceMap() != null && getServiceMap().size() > 0) {
			getServiceMap().forEach((k,v)->{
				if(v instanceof GraphService) {
					((GraphService)v).clear();
				}
			});
		}
		} catch(Exception e) {
			ApplicationLog.error(e);
		}
	}

	@Override
	public boolean isMeasureSortApply() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSkipcubedatasetcolumndataaccesspermission(HttpServletRequest request,UserInfo userInfo) {
		String dashboardId =""+request.getAttribute("objectId");
		boolean isskipcolumnpermission = false;
		if (graphInfo != null) {
			isskipcolumnpermission = graphInfo.isSkipcubedatasetcolumndataaccesspermission(userInfo);
		}
		return isskipcolumnpermission;
	}

	public Map<String, String> geoMapSpotlighterMap(UserInfo userInfo) throws ALSException, CubeException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@RequestMapping("/graphPageFilterMobile")
	public ModelAndView graphPageFilterMobile(ModelMap map,@LoggedInUser UserInfo userInfo,@RequestParam(value="dimensionName", required = false)String dimensionName){
		String[][] pageFilterInformation = null;
		String[][] pageFilterInformation2 = new String[0][8];
		String selectedValues = "";
		String cubeId = "";
		int currentIndex = 0;
		dimensionName = dimensionName.trim();
		try {
			if(getPageFilters(userInfo, null) != null) {
    			pageFilterInformation = getPageFilters(userInfo, null);
    			CubeVector vect = new CubeVector();
    			for(int i=0; i < pageFilterInformation.length; i++) {
    				if(pageFilterInformation[i][4] != null && pageFilterInformation[i][0].equals(dimensionName)) {
    					cubeId = pageFilterInformation[i][4];
    					currentIndex = i;
    				}
    			}
    			try {
					vect = getFilterConditions(pageFilterInformation[currentIndex][0], null);
				} catch (ALSException e) {
					ApplicationLog.error(e);
				}
    			if(vect != null && !vect.isEmpty()) {
    				for(int i=0; i < vect.size(); i++) {
    					CubeConditionInfo cinfo = (CubeConditionInfo) vect.get(i);
    					if(i == vect.size() - 1) {
    						selectedValues = selectedValues + cinfo.getConditionalValue();
    					}else {
    						selectedValues = selectedValues + cinfo.getConditionalValue()+ ",";
    					}
    				}
    			}
			}
			for(int i=0; i<pageFilterInformation.length;i++) {
    			//pageFilterInformation[i][2]  = StringUtil.unescapeHtmlUtil(pageFilterInformation[i][2]);
    			if(pageFilterInformation[i][2].contains("!NULL") && pageFilterInformation[i][2].contains("!")){
					pageFilterInformation[i][2] = pageFilterInformation[i][0]+"="+"(NOT NULL)" +"@#@#@#" + pageFilterInformation[i][0]+"="+"(NOT NULL)";
				}
    			if(pageFilterInformation[i][0].equals(dimensionName) && !pageFilterInformation[i][2].equals("None")) {
    				map.put("pageFilterField",pageFilterInformation[i]);
    			}
			}
		} catch (CubeException e) {
			ApplicationLog.error(e);
		}
		
		map.put("selectedValues",selectedValues);
		map.put("dimensionName",dimensionName);
		map.put("cubeId",cubeId);
		
		return new ModelAndView("graph/graphPageFilter");
	}
	
	@RequestMapping("getGraphPageFilterValue")
	@ResponseBody
	public HashSet<String> getFilterValue(@LoggedInUser UserInfo userInfo) {
		HashSet<String> filterStatusMap = new HashSet<>();
		/*Added For pageFilterStatus*/
		String[][] pageFilterInformation = null;
		String pageFilterColumn = "";
		String cubeId = "",selectedValues="";
		int currentIndex=0;
		HashSet<String> selectedColumn = new HashSet<String>();
		try {
			if(getPageFilters(userInfo, null) != null) {
    			pageFilterInformation = getPageFilters(userInfo, null);
    			CubeVector vect = new CubeVector();
    			for(int j=0; j < pageFilterInformation.length; j++) {
	    			selectedColumn.add(pageFilterInformation[currentIndex][0]);
	    			currentIndex++;
    			}
			}
			//modelMap.put("selectedFilterColumn", selectedColumn);
			
		} catch (CubeException e) {
			ApplicationLog.error(e);
		}
		return selectedColumn;
	}
	
	@RequestMapping("getGraphPageFilterStatus")
	@ResponseBody
	public Map<String, Boolean> getFilterStatus(@LoggedInUser UserInfo userInfo) {
		HashSet<String> filterStatusMap = new HashSet<>();
		/*Added For pageFilterStatus*/
		String[][] pageFilterInformation = null;		
		int currentIndex=0;
		Map<String, Boolean> selectedColumn = new HashMap<>();
		try {
			if(getPageFilters(userInfo, null) != null) {
    			pageFilterInformation = getPageFilters(userInfo, null);
    			CubeVector vect = new CubeVector();
    			for(int j=0; j < pageFilterInformation.length; j++) {
    				/*if(pageFilterInformation[i][4] != null && pageFilterInformation[i][0].toString().equals(dimensionName)) {
    					cubeId = pageFilterInformation[i][4].toString();*/
    					currentIndex = j;
    				//}
    		
	    			//try {
						vect = getFilterConditions(pageFilterInformation[currentIndex][0], null);
					/*} catch (ALSException e) {
						ApplicationLog.error(e);
					}*/
	    			if(vect != null && !vect.isEmpty()) {
	    				for(int i=0; i < vect.size(); i++) {
	    					CubeConditionInfo cinfo = (CubeConditionInfo) vect.get(i);
	    					/*if(i == vect.size() - 1) {
	    						selectedValues = selectedValues + cinfo.getConditionalValue();
	    					}else {
	    						selectedValues = selectedValues + cinfo.getConditionalValue()+ ",";
	    					}*/
	    					selectedColumn.put(cinfo.getColumnName(),true);
	    				}
	    			}else {
	    					selectedColumn.put(pageFilterInformation[currentIndex][0],false);
	    			}
    			}
			}
			//modelMap.put("selectedFilterColumn", selectedColumn);
			
		} catch (CubeException | ALSException e) {
			ApplicationLog.error(e);
		}
		return selectedColumn;
	}

	@Override
	public void setCubeForLineage(){
		graphService.setCubeforLineage(graphInfo);
	}

	@Override
	public String getColumnValue(String strColumnName, String strCellref, boolean isUDDC) {
		Object obj="";
		try {
			/*Random r = new Random( System.currentTimeMillis() );
			obj = 10000 + r.nextInt(20000);*/
			obj = graphService.getColumnValue(strColumnName,graphInfo,isUDDC);
		} catch (Exception e) {
			ApplicationLog.error(e);
		}
		return obj+"";
	}
	
	@Override
	public String getDataOperationName(String columnName , String strCellref, UserInfo userInfo) {
		String strOperationName = "";
		strOperationName = "Sum";
		strOperationName = graphService.getDataOperationName(strCellref,columnName, graphInfo,userInfo);
		return strOperationName;
	}
	
	@Override
	public Map<SelectItem, Integer> prepareAllOutlinerItemsMap(UserInfo userInfo) throws ALSException, CubeException {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public ArrayList<PageFilterNew> getPageFilterNew(UserInfo userInfo, String objectId) throws CubeException {
		if(objectId!=null) {
			GraphInfo graphInfo=getGraphObjectFromMap(objectId);
			return getGraphServiceFromMap(objectId).getPageFilterNew(graphInfo);
		}
		return graphService.getPageFilterNew(graphInfo);
	}
	
	@Override
	public void setPageFilterInfoNew(ArrayList<PageFilterNew> pageFiter, HttpServletRequest request) {
		graphInfo.setPageFilterColumnInfo(pageFiter);
	}



	@Override
	public BIDataset<Row> getSparkBIResultset() {		
		return graphService.getSparkResultsetNarrative(graphInfo);
	}
	public void setDefaultTimeFormatForProperties(GraphInfo graphInfo,UserInfo userInfo) {
		try {
			if(graphInfo != null) {
				List dimensionsList = new ArrayList(graphInfo.getColColumns());
				dimensionsList.addAll(graphInfo.getRowColumns());
				for(Object obj : dimensionsList){
					if(obj != null) {
						String dimName = obj.toString();
						int colType = getColumnType(dimName, userInfo);
						if(colType == Types.TIMESTAMP) {
							String userTimeFormat = userInfo.getTimeFormat();
							String timeFormat = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getTimeFormat();
							if(timeFormat != null && timeFormat.isEmpty()) {
								graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().setTimeFormat(userTimeFormat);
							}
							break;
						}
					}
				}
			}
		}catch(Exception e) {
			ApplicationLog.error(e);
		}

	}

	@Override
	public GraphInfo getGraphInformation(UserInfo userInfo) {
		// TODO Auto-generated method stub
		return graphInfo;
	}
	
public List<String> getDimensionList(UserInfo userInfo){
		
		CubeVector cols = getColLabelNameVector(null);
		CubeVector rows = getRowLabelNameVector(null);
		
		List<String> dimension = new ArrayList<String>();
		
		dimension.addAll(cols);
		dimension.addAll(rows);
		
		return dimension;
	}

	@Override
	public Object setDataGroupDetails(HttpServletRequest request, HttpServletResponse response, UserInfo userInfo, ModelMap map) {
		
		Object status = null;
		
		try {
			graphService.setGroupDetail(graphInfo);
			status = AppConstants.SUCCESS_STATUS;
			response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
		} catch (CubeException e) {
			ApplicationLog.error(ResourceManager.getString("LOG_ERROR_FAILED_TO_SAVE_GROUP", new Object[]{userInfo.getUsername(), getObjectDisplayName() }), e);
			status = ResourceManager.getString("ERROR_MSG_FAILED_TO_SAVE");
		}
		return status;
	}

	@Override
	public List<Group> getDataGroupList(UserInfo userInfo) {
		List<Group> list = null;
		
		if(graphInfo != null) {
			list = graphInfo.getGroupList();
		}
		return list;
	}

	@Override
	public void removeFromPackColumnDDVMDataGroup(String colName, String actualTextValues, UserInfo userInfo)
			throws CubeException {
		removeFromPackColumnDDVM(colName, actualTextValues, graphInfo, userInfo);
	}

	@Override
	public void setActiveSpotLighter(UserInfo userInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getAppliedFilters(ModelMap modelMap, UserInfo userInfo, HttpServletRequest request, HttpSession session) {
		graphService.showObjectInformation(graphInfo, userInfo,modelMap, getCubeInfo(graphInfo.getGraphId()));
		
	}

	@Override
	public void getSamplingApplied(ModelMap modelMap, UserInfo userInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getViewConditionApplied(ModelMap modelMap, UserInfo userInfo) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Hashtable getActiveVariableMap(HttpServletRequest request) {
		try {
			return graphService.getActiveVariableMap();
		}catch(Exception e) {
			ApplicationLog.error(e);
		}
		return null;
	}

	@Override
	public Map<String, String> getDashboardFilters() {		
		return null;
	}
	
	/**
	 * Show share dialog
	 */
	@RequestMapping(value="/showShareGraph")
	@ResponseBody
	public ModelAndView showShareGraph(ModelMap map,@LoggedInUser UserInfo userInfo,@RequestParam(value = "objectid", required = false) String strObjectId){
		
		map.put("objectid",strObjectId);
		map.put("isFromShare",true);
		return new ModelAndView("sharegraph");
	}

	@Override
	public List<Pair> getRangeBucketList(String strDimensionName, String strSearchStr, UserInfo userInfo,
			int lastIndexValue, HttpServletRequest request, String objectId, String strCubeId, boolean isBackEnd) {
		boolean isUDDC = false;
		if(isBackEnd) {
		Vector<CubeColumnInfo> cubeColumnInfoList = graphInfo.getDataColumnInfoList();
		for (int iCnt = 0; iCnt < cubeColumnInfoList.size(); iCnt++) {
			CubeColumnInfo cubeColumnInfo = cubeColumnInfoList.elementAt(iCnt);
			String columnName = cubeColumnInfo.getName();
			String srcColumnName = cubeColumnInfo.getSourceName();
			if(!columnName.equals(srcColumnName)) {
				if (columnName.equals(strDimensionName)) {
					strDimensionName = srcColumnName;
					break;
				}
			}
		}
		} else {
			Vector<CubeColumnInfo> cubeColumnInfoList = graphInfo.getDataColumnInfoList();
			for (int iCnt = 0; iCnt < cubeColumnInfoList.size(); iCnt++) {
				CubeColumnInfo cubeColumnInfo = cubeColumnInfoList.elementAt(iCnt);
				String columnName = cubeColumnInfo.getName();
				String srcColumnName = cubeColumnInfo.getSourceName();
				if(!columnName.equals(strDimensionName)) {
					if (strDimensionName.equals(srcColumnName)) {
						isUDDC = true;
						break;
					}
				} else if(columnName.equals(strDimensionName)) {
					isUDDC = true;
					break;
				}
			}
			if(!isUDDC && graphInfo.getCubeInfo() != null) {
				List<ActiveUDDCInfo> activeUDDCList = getActiveTemplateProperties(null).getActiveUDDCInfo();
				
				for(ActiveUDDCInfo activeUDDC : activeUDDCList) {
					String activeUddcName = activeUDDC.getUddcTemplateInfo().getColumnName();
					
					if(activeUddcName.equals(strDimensionName)) {
						isUDDC = true;
						break;
					}
				}
			}
			if(!isUDDC && graphInfo.getDateFrequencyMap() != null && graphInfo.getDateFrequencyMap().get(strDimensionName) != null &&
					!graphInfo.getDateFrequencyMap().get(strDimensionName).isEmpty()) {
				isUDDC = true;
			}
		}
		try {
		BIDataset<Row> dataset = graphService.getMainResultset();
		ICubeResultSet resultset = graphService.getDataResultSet();
		if (graphInfo.getGraphType() ==  GraphConstants.COMBINED_GRAPH) {
			if(graphInfo.getDataColLabels3().contains(strDimensionName) || graphInfo.getRowColumns().contains(strDimensionName) || graphInfo.getColColumns().contains(strDimensionName)) {
				dataset = graphService.getMainResultsetCombineBar();
				resultset = graphService.getDataResultSetCombineBar();
			} else if(graphInfo.getTheDataColLabels4().contains(strDimensionName) || graphInfo.getLineGraphRowLabelsForCombinedGraph().contains(strDimensionName)) {
				dataset = graphService.getMainResultsetCombineLine();
				resultset = graphService.getDataResultSetCombineLine();
			} 
		}
		
			List<Pair> values ;
			values = graphService.getRangeBucket(strDimensionName, graphInfo.getCubeInfo(), lastIndexValue,  strSearchStr, 
					  objectId, userInfo, isUDDC, dataset, resultset);
			return values;
		} catch (Exception e) {
			ApplicationLog.error(e);
			return new ArrayList<>();
		}
	}
	@RequestMapping(value = "/getObjectReferenceData")
	@ResponseBody
	public ModelAndView getObjectReferenceData(ModelMap modelMap, @LoggedInUser UserInfo userInfo) {

		// for associated dashboard
		modelMap.put("associatedDashboardList",
				repositoryService.getDashboardListAssociatedWithObjectMap(graphInfo.getGraphId(), userInfo));
		// for bi object datasource
		modelMap.put("associatedBiObjecttList",
				repositoryService.getBIObjectDataSourcetListAssociatedWithObjectMap(graphInfo.getGraphId()));
		// for Linked object in dashboard
		modelMap.put("linkedDashboardObjMap",
				repositoryService.getObjectsLinkedInDashboard(graphInfo.getGraphId(), userInfo));
		// for Linked object in kpi
		modelMap.put("linkedKPIObjMap", repositoryService.getObjectsLinkedInKPI(graphInfo.getGraphId(), userInfo));
		// for Linked object in  kpi group
		modelMap.put("linkedKPIGroupObjMap", repositoryService.getObjectsLinkedInKPIGroup(graphInfo.getGraphId(), userInfo));
		return new ModelAndView("graph/objectReferenceData");
	}
	
	@RequestMapping (value = "/customLegendSortDimensionValue")
	@ResponseBody
	public ModelAndView getCustomLegendSortDimensionValue(@RequestParam("selectedDimension") String strDimensionName,
			ModelMap map, @LoggedInUser UserInfo userInfo,
			@RequestParam(value="objectId", required=false) String objectId,HttpServletRequest request){
		try {
			List selectedValue =new ArrayList<>(); 
			if(graphInfo.getGraphProperties().getCustomLegendSelectedValueList() != null && !graphInfo.getGraphProperties().getCustomLegendSelectedValueList().isEmpty())
			 selectedValue = new ArrayList<>(graphInfo.getGraphProperties().getCustomLegendSelectedValueList());
			if(graphInfo.getDrilldownBreadcrumbMap() != null && graphInfo.getDrilldownBreadcrumbMap().containsKey("Row")) {
				selectedValue = new ArrayList<>();
			}
			List values = new ArrayList<>(graphInfo.getGraphData().getRowList());
			if (!values.isEmpty() && !(values.get(0) instanceof String)) {
	            values = (List) values.stream()
	                           .map(Object::toString)
	                           .collect(Collectors.toList());
	        }
			if(null ==graphInfo.getGraphData().getRowList() || graphInfo.getGraphData().getRowList().isEmpty()) {
				 values = new ArrayList<>(graphInfo.getGraphData().getColList());
			}
			if (graphInfo.getGraphProperties().getCustomLegendSelectedValueList() != null
					&& !graphInfo.getGraphProperties().getCustomLegendSelectedValueList().isEmpty()) {
				values.removeAll(selectedValue);
			}
			if(graphInfo.getGraphType()== GraphConstants.COMBINED_GRAPH && graphInfo.getGraphData().getCmbBarrowList() != null && !graphInfo.getGraphData().getCmbBarrowList().isEmpty() ) {
				values = new ArrayList<>(graphInfo.getGraphData().getCmbBarrowList());
			}
			/*if(graphInfo.getGraphType()== GraphConstants.COMBINED_GRAPH && graphInfo.getGraphData().getCmbBarcolList() != null && !graphInfo.getGraphData().getCmbBarcolList().isEmpty()) {
				values = new ArrayList<>(graphInfo.getGraphData().getCmbBarcolList());
			}*/
		
			map.put("availableLegendValues", values);
			map.put("selectedLegendValues", selectedValue);
		
		}catch (Exception e) {
			ApplicationLog.error(e);
		} 
		return new ModelAndView("analysis/customLegendSortDimensionValue");
	}

	@Override
	public Map<String, String> getDateFrequencyMap() {
		if(graphInfo != null) {
			return graphInfo.getDateFrequencyMap();
		}
			return null;
	}

	@Override
	public String convertRelativePeriodToAbsoluteString(UserInfo userInfo, String stringToConvert, int dateType,
			int yearType, int criteriaType, HttpServletRequest request, String columnName, String cubeId, boolean isDisplayRange) {
		List<IDataObject> cubeList = null;
		String result = "";
		try {
			cubeList = getCubeListFromDifferentObject(userInfo,request);
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		}
		
		result = graphService.convertRelativePeriodToAbsoluteString(getCubeInfo(null), getObjectId(), cubeList, userInfo, stringToConvert, dateType, yearType, criteriaType, request, columnName, cubeId, isDisplayRange);
		
		return result;
	}

	@Override
	public void saveFiltersOnObject(UserInfo userInfo) {
		try {
			if(graphInfo.getGraphMode()!= AppConstants.NEW_MODE) {
				graphService.saveFiltersOnObject(graphInfo, userInfo, getStrParentHierchy());
			}
		} catch (DatabaseOperationException e) {
			ApplicationLog.error(e);
		}
		
		
	}

	@Override
	public UserInfo getObjectCreator() {
		return graphInfo.getCreatedBy();
	}
	
	@RequestMapping(value = "/cancelTask")
	@ResponseBody
	public String cancelTask(@LoggedInUser UserInfo userInfo) {
		try {
			graphService.cancelTask(getObjectId());
		} catch(Exception e) {
			ApplicationLog.error(e);
		}
		return AppConstants.SUCCESS_STATUS;
    }
	@Override
	public boolean isObjectOpenedFromDashBoard() {
		if(graphInfo.getDashboardInfo()!=null) {
			String dbId = graphInfo.getDashboardInfo().getDashboardId();
			if(dbId != null && !dbId.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<SelectItem, Integer> prepareCubeAllItemsMapWithType(boolean isAddMeasure, UserInfo uInfo,
			HttpServletRequest request, String strCubeId)
			throws ALSException, CubeException, DatabaseOperationException {
		return prepareCubeAllItemsMap(isAddMeasure, uInfo);
	}
	
}