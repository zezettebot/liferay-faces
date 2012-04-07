/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.bridge;

import javax.faces.event.PhaseListener;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeDefaultViewNotSpecifiedException;
import javax.portlet.faces.BridgeException;

import com.liferay.faces.bridge.event.IPCPhaseListener;
import com.liferay.faces.bridge.logging.Logger;
import com.liferay.faces.bridge.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgePhaseActionImpl extends BridgePhaseBaseImpl {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgePhaseActionImpl.class);

	// Private Data Members
	private ActionRequest actionRequest;
	private ActionResponse actionResponse;

	public BridgePhaseActionImpl(ActionRequest actionRequest, ActionResponse actionResponse,
		PortletConfig portletConfig) {
		super(portletConfig);
		this.actionRequest = actionRequest;
		this.actionResponse = actionResponse;
	}

	public void execute() throws BridgeDefaultViewNotSpecifiedException, BridgeException {

		logger.debug(Logger.SEPARATOR);
		logger.debug("execute(ActionRequest, ActionResponse) portletName=[{0}]", portletName);

		try {

			init(actionRequest, actionResponse, Bridge.PortletPhase.ACTION_PHASE);

			// PROPOSED-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-202
			bridgeRequestScope.setPortletMode(actionRequest.getPortletMode());

			// If the "javax.portlet.faces.PortletMode" request parameter has a value, then the developer probably
			// specified a URL like <h:outputLink value="portlet:render"> using f:param to set the request parameter
			// for switching modes. This is one of the tests in the TCK.
			String portletModeParam = actionRequest.getParameter(Bridge.PORTLET_MODE_PARAMETER);

			if (portletModeParam != null) {

				try {
					actionResponse.setPortletMode(new PortletMode(portletModeParam));
				}
				catch (PortletModeException e) {
					logger.error("Invalid parameter value {0}=[{1}]}", Bridge.PORTLET_MODE_PARAMETER, portletModeParam);
				}
			}

			// Execute all the phases of the JSF lifecycle except for RENDER_RESPONSE since that can only be
			// executed during the RENDER_PHASE of the portlet lifecycle. Section 5.2.4 of the JSR 329 Spec requires
			// that a phase listener be registered in order to handle Portlet 2.0 Public Render Parameters after the
			// RESTORE_VIEW phase of the JSF lifecycle executes. The IPCPhaseListener satisfies this requirement.
			PhaseListener ipcPhaseListener = new IPCPhaseListener(bridgeConfig, portletContext, portletName,
					actionRequest, actionResponse);
			facesLifecycle.addPhaseListener(ipcPhaseListener);
			facesLifecycle.execute(facesContext);
			facesLifecycle.removePhaseListener(ipcPhaseListener);

			// Save the faces view root and any messages in the faces context so that they can be restored during
			// the RENDER_PHASE of the portlet lifecycle.
			bridgeRequestScope.preserveScopedData(facesContext);

			// PROPOSED-FOR-JSR344-API
			// http://java.net/jira/browse/JAVASERVERFACES_SPEC_PUBLIC-1070
			// PROPOSED-FOR-BRIDGE3-API: https://issues.apache.org/jira/browse/PORTLETBRIDGE-201
			bridgeRequestScope.setFlash(facesContext.getExternalContext().getFlash());

			// Spec 6.6 (Namespacing)
			indicateNamespacingToConsumers(facesContext.getViewRoot(), actionResponse);
		}
		catch (Exception e) {
			throw new BridgeException(e);
		}
		finally {

			if (facesContext != null) {
				facesContext.release();
			}

			cleanup(actionRequest);
		}
	}
}
