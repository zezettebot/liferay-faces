/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.alloy.component.column;

import java.io.IOException;

import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.liferay.faces.util.lang.StringPool;
import com.liferay.faces.util.render.RendererUtil;


/**
 * @author  Kyle Stiemann
 */
@FacesRenderer(componentFamily = Column.COMPONENT_FAMILY, rendererType = Column.RENDERER_TYPE)
@ResourceDependency(library = "liferay-faces-reslib", name = "build/aui-css/css/bootstrap.min.css")
public class ColumnRenderer extends ColumnRendererBase {

	protected static Integer getColumnUnitSize(Integer width) {
		return (int) Math.round(Column.COLUMNS * ((double) width / 100));
	}

	@Override
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();

		UIComponent parent = uiComponent.getParent();

		if (parent instanceof HtmlDataTable) {
			// TODO
		}
		else if (parent instanceof HtmlPanelGrid) {
			// TODO
		}
		else {

			responseWriter.startElement(StringPool.DIV, uiComponent);

			String clientId = uiComponent.getClientId(facesContext);
			responseWriter.writeAttribute(StringPool.ID, clientId, null);

			Column column = (Column) uiComponent;
			StringBuilder classNames = new StringBuilder();

			Integer span = column.getSpan();

			if (span != null) {

				if ((span < 1) || (span > Column.COLUMNS)) {
					throw new IOException("span number must be between 1 and " + Column.COLUMNS);
				}
			}

			Integer width = column.getWidth();

			if (width != null) {

				if ((width < 1) || (width > 100)) {
					throw new IOException("width must be between 1 and 100");
				}

				span = getColumnUnitSize(width);
			}

			classNames.append(StringPool.SPAN);
			classNames.append(span);

			Integer offset = column.getOffset();

			if (offset != null) {

				if ((offset < 1) || (offset > Column.COLUMNS)) {
					throw new IOException("offset must be between 1 and " + Column.COLUMNS);
				}
			}

			Integer offsetWidth = column.getOffsetWidth();

			if (offsetWidth != null) {

				if ((offsetWidth < 1) || (offsetWidth > 100)) {
					throw new IOException("offsetWidth must be between 1 and 100");
				}

				offset = getColumnUnitSize(offsetWidth);
			}

			if (offset != null) {
				classNames.append(StringPool.SPACE);
				classNames.append(OFFSET);
				classNames.append(offset);
			}

			RendererUtil.encodeStyleable(responseWriter, column, classNames.toString());
		}
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		ResponseWriter responseWriter = facesContext.getResponseWriter();

		UIComponent parent = uiComponent.getParent();

		if (parent instanceof HtmlDataTable) {
			// TODO
		}
		else if (parent instanceof HtmlPanelGrid) {
			// TODO
		}
		else {
			responseWriter.endElement(StringPool.DIV);
		}
	}
}
