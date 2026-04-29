/*
 * @(#)GraphInterceptor.java Version 4.0 <Mar 12, 2014>
 *
 *
 * Copyright 2015 Elegant MicroWeb Technologies Pvt. Ltd. (India). All Rights Reserved. Use is subject to license terms.
 */

package com.elegantjbi.controller.graph;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.elegantjbi.AppContext;

public class GraphInterceptor extends HandlerInterceptorAdapter{

	@Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        GraphController graphController = (GraphController) AppContext.getApplicationContext().getBean("graphController");
        String graphId = request.getParameter("objectId");
        
        if (graphId != null && !graphId.trim().equals(""))
        	graphController.setDetailAndService(graphId);
        //if returned false, we need to make sure 'response' is sent
        return true;
    }
}
