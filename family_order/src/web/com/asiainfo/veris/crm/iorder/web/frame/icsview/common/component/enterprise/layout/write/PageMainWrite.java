package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.write;

import java.util.List;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.ILocation;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.biz.view.BizPage;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.CommonTools;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutUtil;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.Point;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public class PageMainWrite
{
    public static void renderLayouts(Point point, IPage page ,ILocation location, IComponent container, INamespace namespace, IMarkupWriter writer, IRequestCycle cycle,BizPage Page) throws Exception
    {
    	String templetStr = LayoutUtil.getPageAreaInfos(point.getTempletId(), LayoutConstants.TEMPLET_PAGE);//获取模版
    	String areaId = point.getPointName();
    	if(StringUtils.isNotEmpty(templetStr))
    	{
    		templetStr = TempletComWrite.dealStr(templetStr);
    		templetStr = TempletComWrite.replaceStr(templetStr, point.getExtraId(), LayoutConstants.EXTRA_PAGE);
    		
    		String[] strStrs = templetStr.split("\\$\\$");
    		if(strStrs.length != 2)
    		{
    			CSViewException.apperr(CrmCommException.CRM_COMM_103, "模版替换格式不正确!");
    		}
    		IComponent mainComponet = CommonTools.anyComponents(page, location, container, namespace);
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
    			for(int i = 0 ; i < subElts.size() ; i ++)
    			{
    				TempletComWrite.spellTemplet(point, areaId, mainComponet, subElts.get(i), location, namespace,  container, page);
    			}
    		}
    		mainComponet.render(writer, cycle);
    	}
    	else
    	{
    		IComponent mainComponet = CommonTools.anyComponents(page, location, container, namespace);
    		AreaMainWrite.renderLayouts(point, areaId, page, location, mainComponet, namespace, container);
    		mainComponet.render(writer, cycle);
    	}
    }
}
