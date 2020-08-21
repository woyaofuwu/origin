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

public class AreaMainWrite 
{
	public static void renderLayouts(Point point, String areaId, IPage page ,ILocation location, IComponent mainComponet, INamespace namespace, IComponent container) throws Exception
    {
		//查询区域id
    	IDataset areaInfos = LayoutUtil.getAreaInfos(areaId);
    	if(DataUtils.isNotEmpty(areaInfos))
    	{
    		for(int i = 0 ; i < areaInfos.size() ; i ++)
    		{
    			IData areaInfo = areaInfos.getData(i);
    			String partId = areaInfo.getString("PART_ID", "");
    			String partType = areaInfo.getString("PART_TYPE", "");
    			IData partInfo = LayoutUtil.getPartInfo(partId);
    			String templetStr = LayoutUtil.getPageAreaInfos(areaInfo.getString("TEMPLET_ID", ""), LayoutConstants.TEMPLET_PART);//获取模版
    	    	if(StringUtils.isNotEmpty(templetStr))
    	    	{
	    			templetStr = TempletComWrite.dealStr(templetStr);
	    			partInfo = TempletComWrite.dealAttr(partInfo,"PART_",false);
	    			templetStr = TempletComWrite.replaceStrData(templetStr, partInfo);
	    			String extraId = areaInfo.getString("EXTRA_ID", "");
	    			if(StringUtils.isNotEmpty(extraId))
	    			{
	    				templetStr = TempletComWrite.replaceStr(templetStr, extraId, LayoutConstants.EXTRA_PART);
	    			}
	        		
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
	        				TempletComWrite.spellTempletArea(point, partId, partType, mainComponet, subElts.get(j), location, namespace,  container, page);
	        			}
	        		}
    	    	}
    	    	else
    	    	{
    	    		if(LayoutConstants.PART_TYPE_AREA.equals(partType))
    	    		{
    	    			if(areaId.equals(partId))
    	    			{
    	    				CSViewException.apperr(CrmCommException.CRM_COMM_103, "配置PART_TYPE类型A,PART_ID不能等于AREA_ID!");
    	    			}
    	    			AreaMainWrite.renderLayouts(point, partId, page, location, mainComponet, namespace, container);
    	    		}
    	    		else
    	    		{
    	    			PartMainWrite.renderLayouts(point, partId, page, location, mainComponet, namespace, container);
    	    		}
    	    	}
    		}
    	}
    }
}
