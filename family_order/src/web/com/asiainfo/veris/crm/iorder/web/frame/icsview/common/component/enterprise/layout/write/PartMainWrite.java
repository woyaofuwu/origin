package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.write;

import java.util.List;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.ILocation;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IPage;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutUtil;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.Point;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public class PartMainWrite 
{
	public static void renderLayouts(Point point, String partId, IPage page ,ILocation location, IComponent mainComponet, INamespace namespace, IComponent container) throws Exception
    {
		//查询元素信息
		IDataset eleElementInfos = LayoutUtil.getElmentInfos(partId);
		if(DataUtils.isNotEmpty(eleElementInfos))
		{
			for(int i = 0 ; i < eleElementInfos.size() ; i ++)
			{
				IData eleElementInfo = eleElementInfos.getData(i);
				String templetId = eleElementInfo.getString("TEMPLET_ID", "");
    			String templetStr = LayoutUtil.getPageAreaInfos(templetId, LayoutConstants.TEMPLET_ELEMENT);//获取模版
    			
    			String elementId = eleElementInfo.getString("ELEMENT_ID", "");
				String extraId = eleElementInfo.getString("EXTRA_ID", "");
				String pre = eleElementInfo.getString("PARAM_PRE","");

				IData elementInfo = LayoutUtil.getElementInfos(elementId);
				IData tempData = TempletComWrite.dealAttr(elementInfo,"ELEMENT_",true);
				if(StringUtils.isNotEmpty(extraId))
				{
					IDataset extraInfos = LayoutUtil.getAllExtraInfos(extraId, LayoutConstants.EXTRA_ELEMENT);
					tempData = TempletComWrite.spellElementAttr(extraInfos, tempData);
				}
				String tempJwcid = tempData.getString(LayoutConstants.JWCID, "");
				tempData = TempletComWrite.spellExtraElementAttr(tempData, pre, point);
    			if(StringUtils.isNotEmpty(templetStr))
    	    	{
	    			templetStr = TempletComWrite.dealStr(templetStr);
	    			templetStr = TempletComWrite.replaceStrData(templetStr, tempData);
	        		templetStr= TempletComWrite.spellXmlStr(templetStr);
	        		templetStr= TempletComWrite.dealEmpty(templetStr);
	        		if(StringUtils.isEmpty(templetStr))
	        		{
	        			CSViewException.apperr(CrmCommException.CRM_COMM_103, "模版格式不正确!");
	        		}
	        		Document doc = DocumentHelper.parseText(templetStr);
	        		Element rootElt = doc.getRootElement();
	        		
	        		//获取根目录下所有子元素
	        		List<Element> subElts = rootElt.elements();
	        		if(null != subElts && subElts.size() > 0)
	        		{
	        			for(int j = 0 ; j < subElts.size() ; j ++)
	        			{
	        				TempletComWrite.spellTempletPart(point, tempData, tempJwcid, mainComponet, subElts.get(j), location, namespace,  container, page);
	        			}
	        		}
    	    	}
    			else
    			{
    				ElementMainWrite.renderLayouts(point, tempData, tempJwcid, page, location, mainComponet, namespace, container);
    			}
			}
		}
    }
}
