package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.write;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.ILocation;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IPage;
import org.apache.tapestry.parse.TextToken;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.CommonTools;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutUtil;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.ComponentFactory;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.Point;

public class ElementMainWrite 
{
	public static void renderLayouts(Point point, IData tempData, String tempJwcid, IPage page ,ILocation location, IComponent mainComponet, INamespace namespace, IComponent container) throws Exception
	{
		String relPartIdStr = tempData.getString("REL_PAGE_PART_ID","");
		IComponent childComponent = ComponentFactory.builderComponent(page, location, container, namespace, tempJwcid, tempData);
		
		String innerHtml =TempletComWrite.getInnerHtml(tempData);
		
		if(StringUtils.isNotEmpty(innerHtml))
		{
			StringBuilder sbd = new StringBuilder();
			sbd.append(innerHtml);
			TextToken tt = CommonTools.dealTextToken(sbd, location);
			childComponent.addBody(tt);
		}
		
		mainComponet.addBody(childComponent);
		
		if(StringUtils.isNotEmpty(relPartIdStr))
		{
			String[] relPartIds = relPartIdStr.split(",");
			for(int i = 0 ; i < relPartIds.length ; i ++)
			{
				IDataset eleElementInfos = LayoutUtil.getElmentInfos(relPartIds[i]);
				if(DataUtils.isNotEmpty(eleElementInfos))
				{
					for(int j = 0 ; j < eleElementInfos.size() ; j ++)
					{
						IData subEleElementInfo = eleElementInfos.getData(j);
						String elementId = subEleElementInfo.getString("ELEMENT_ID", "");
	    				String extraId = subEleElementInfo.getString("EXTRA_ID", "");
	    				String pre = subEleElementInfo.getString("PARAM_PRE","");

	    				IData elementInfo = LayoutUtil.getElementInfos(elementId);
	    				IData result = TempletComWrite.dealAttr(elementInfo,"ELEMENT_",true);
	    				if(StringUtils.isNotEmpty(extraId))
	    				{
		    				IDataset extraInfos = LayoutUtil.getAllExtraInfos(extraId, LayoutConstants.EXTRA_ELEMENT);
		    				result = TempletComWrite.spellElementAttr(extraInfos, result);
	    				}
	    				String subTempJwcid = result.getString(LayoutConstants.JWCID, "");
	    				result = TempletComWrite.spellExtraElementAttr(result, pre, point);
						ElementMainWrite.renderLayouts(point, subEleElementInfo,subTempJwcid , page, location, childComponent, namespace, container);
					}
				}
			}
		}
	}
}
