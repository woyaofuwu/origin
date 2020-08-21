package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.write;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.ILocation;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IPage;
import org.apache.tapestry.parse.TextToken;
import org.dom4j.Attribute;
import org.dom4j.Element;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.CommonTools;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutUtil;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.ComponentFactory;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.Point;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public class TempletComWrite 
{
	public static String dealStr(String str) throws Exception
	{
		String result = "";
		//去掉回车，前后空格，等号左右空格
		result = str.trim();
		result = result.replace("\n", "");
		while(result.indexOf(" =") != -1)
		{
			result = result.replace(" =", "=");
		}
		while(result.indexOf("= ") != -1)
		{
			result = result.replace("= ", "=");
		}
		return result;
	}

	public static String replaceStr(String str, String extraId,String extraType) throws Exception
	{
		while(str.indexOf(LayoutConstants.EXTRA_START)!=-1)
		{
			int strStart = str.indexOf(LayoutConstants.EXTRA_START);
			int strEnd = str.indexOf(LayoutConstants.EXTRA_END);
			if(strEnd == -1)
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "模版格式不正确!");
			}
			String extraKey = str.substring(strStart+2, strEnd);
			
			String extraValue=LayoutUtil.getExtraInfos(extraId, extraKey, extraType);
			
			str = str.substring(0,strStart) + "\"" + extraValue + "\"" + str.substring(strEnd+1,str.length());
		}
		
		while(str.indexOf(LayoutConstants.EXTRA_START_N)!=-1)
		{
			int strStart = str.indexOf(LayoutConstants.EXTRA_START_N);
			int strEnd = str.indexOf(LayoutConstants.EXTRA_END_N);
			if(strEnd == -1)
			{
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "模版格式不正确!");
			}
			String extraKey = str.substring(strStart+2, strEnd);
			
			String extraValue=LayoutUtil.getExtraInfos(extraId, extraKey, extraType);
			
			str = str.substring(0,strStart) + extraValue + str.substring(strEnd+1,str.length());
		}
		
		return str;
	}
	
	public static String replaceStrData(String str, IData temp) throws Exception
	{
		if(DataUtils.isEmpty(temp))
		{
			return str;
		}
		Iterator iterator = temp.keySet().iterator();
        while (iterator.hasNext())
        {
        	String key = (String)iterator.next();
        	if(str.indexOf(LayoutConstants.EXTRA_START+key)!=-1)
        	{
        		int strStart = str.indexOf(LayoutConstants.EXTRA_START+key);
        		int strEnd = str.indexOf(key+LayoutConstants.EXTRA_END)+key.length();
        		
        		str = str.substring(0,strStart) + "\"" + temp.getString(key, "") + "\"" + str.substring(strEnd+1,str.length());
        	}
        	if(str.indexOf(LayoutConstants.EXTRA_START_N+key)!=-1)
        	{
        		int strStart = str.indexOf(LayoutConstants.EXTRA_START_N+key);
        		int strEnd = str.indexOf(key+LayoutConstants.EXTRA_END_N)+key.length();
        		str = str.substring(0,strStart) + temp.getString(key, "") + str.substring(strEnd+1,str.length());
        	}
        }
        
        return str;
	}
	
	public static void spellTemplet(Point point, String areaId, IComponent mainComponet, Element element, ILocation location, INamespace namespace, IComponent container, IPage page) throws Exception
	{
		//获取元素属性
		List<Attribute> tempAttributes = element.attributes();
		boolean flag = false;
		IData temp = new DataMap();
		String divName = element.getName();
		String tempTarget = "";
		String divValue = element.getText();
		for(int i = 0 ; i < element.attributes().size() ; i ++)
		{
			Attribute tempAttribute = (Attribute)tempAttributes.get(i);
			String attrbuteName = tempAttribute.getName();
			if(LayoutConstants.JWCID.equals(attrbuteName))
			{
				flag = true;
				tempTarget = tempAttribute.getStringValue();
			}
			else
			{
				temp.put(tempAttribute.getName(), tempAttribute.getValue());
			}
		}
		
		if(flag)
		{
			IComponent childComponent = ComponentFactory.builderComponent(page, location, container, namespace, tempTarget, temp);
			if(divValue.indexOf("$$") != -1)
			{
				AreaMainWrite.renderLayouts(point, areaId, page, location, childComponent, namespace, container);
			}
			//获取根目录下所有子元素
			List<Element> subElts = element.elements();
			if(subElts != null && subElts.size() > 0)
			{
				for(int i = 0 ; i < subElts.size() ; i ++)
				{
					Element subElt = subElts.get(i);
					spellTemplet(point, areaId, childComponent, subElt, location, namespace,  container, page);
				}
			}
			
			mainComponet.addBody(childComponent);
		}
		else
		{
			StringBuilder sbd = new StringBuilder();
			sbd.append("<").append(divName);
			Iterator iterator = temp.keySet().iterator();
	        while (iterator.hasNext())
	        {
	            String key = (String)iterator.next();
	            sbd.append(" ").append(key).append("=\"").append(temp.getString(key, "")).append("\"");
	        }
	        sbd.append(">");
	        if(divValue.indexOf("$$") == -1)
	        {
	        	sbd.append(divValue);
	        }
			TextToken ttstart = CommonTools.dealTextToken(sbd, location);
			mainComponet.addBody(ttstart);
			
			if(divValue.indexOf("$$") != -1)
			{
				AreaMainWrite.renderLayouts(point, areaId, page, location, mainComponet, namespace, container);
			}
			
			//获取根目录下所有子元素
			List<Element> subElts = element.elements();
			if(subElts != null && subElts.size() > 0)
			{
				for(int i = 0 ; i < subElts.size() ; i ++)
				{
					Element subElt = subElts.get(i);
					spellTemplet(point, areaId, mainComponet, subElt, location, namespace,  container, page);
				}
			}
			
			StringBuilder sbdEnd = new StringBuilder();
			sbdEnd.append("</").append(divName).append(">");
			TextToken ttEnd = CommonTools.dealTextToken(sbdEnd, location);
			mainComponet.addBody(ttEnd);
		}
	}
	
	public static void spellTempletArea(Point point, String partId, String partType, IComponent mainComponet, Element elemet, ILocation location, INamespace namespace, IComponent container, IPage page) throws Exception
	{
		//获取元素属性
		List<Attribute> tempAttributes = elemet.attributes();
		boolean flag = false;
		IData temp = new DataMap();
		String divName = elemet.getName();
		String tempTarget = "";
		String divValue = elemet.getText();
		for(int i = 0 ; i < elemet.attributes().size() ; i ++)
		{
			Attribute tempAttribute = (Attribute)tempAttributes.get(i);
			String attrbuteName = tempAttribute.getName();
			if(LayoutConstants.JWCID.equals(attrbuteName))
			{
				flag = true;
				tempTarget = tempAttribute.getStringValue();
			}
			else
			{
				temp.put(tempAttribute.getName(), tempAttribute.getValue());
			}
		}
		
		if(flag)
		{
			IComponent childComponent = ComponentFactory.builderComponent(page, location, container, namespace, tempTarget, temp);
			String compname = childComponent.getClass().getName();
			if(compname.toUpperCase().indexOf("FOREACH") != -1)//特殊处理foreach组件，原因不知道如何加载jwc里面配置成custom属性的值，这个后期处理
			{
				DatasetList  tempDataset = (DatasetList)childComponent.getProperty("source");
				String value = temp.getString("value", "");
				String index = temp.getString("index","");
				String source = temp.getString("source","");
				value = dealOgnl(value);
				index = dealOgnl(index);
				source = dealOgnl(source);
				if(DataUtils.isNotEmpty(tempDataset))
				{
					for(int i = 0 ; i < tempDataset.size() ; i ++)
					{
						page.setProperty(value, tempDataset.getData(i));
						page.setProperty(index, i);
						IDataset tempSource = new DatasetList();
						tempSource.add( tempDataset.getData(i));
						page.setProperty(source, tempSource);
						IComponent childComponentB = ComponentFactory.builderComponent(page, location, container, namespace, tempTarget, temp);
						
						if(divValue.indexOf("$$") != -1)
						{
							if(LayoutConstants.PART_TYPE_AREA.equals(partType))
							{
								AreaMainWrite.renderLayouts(point, partId, page, location, mainComponet, namespace, container);
							}
							else
							{
								PartMainWrite.renderLayouts(point, partId, page, location, childComponentB, namespace, container);
							}
						}
						//获取根目录下所有子元素
						List<Element> subElts = elemet.elements();
						if(subElts != null && subElts.size() > 0)
						{
							for(int j = 0 ; j < subElts.size() ; j ++)
							{
								Element subElt = subElts.get(j);
								spellTempletArea(point, partId, partType, childComponentB, subElt, location, namespace,  container, page);
							}
						}
						mainComponet.addBody(childComponentB);
					}
				}
				
			}
			else
			{
				if(divValue.indexOf("$$") != -1)
				{
					if(LayoutConstants.PART_TYPE_AREA.equals(partType))
					{
						AreaMainWrite.renderLayouts(point, partId, page, location, mainComponet, namespace, container);
					}
					else
					{
						PartMainWrite.renderLayouts(point, partId, page, location, childComponent, namespace, container);
					}
				}
				//获取根目录下所有子元素
				List<Element> subElts = elemet.elements();
				if(subElts != null && subElts.size() > 0)
				{
					for(int j = 0 ; j < subElts.size() ; j ++)
					{
						Element subElt = subElts.get(j);
						spellTempletArea(point, partId, partType, childComponent, subElt, location, namespace,  container, page);
					}
				}
				mainComponet.addBody(childComponent);
			}
		}
		else
		{
			StringBuilder sbd = new StringBuilder();
			sbd.append("<").append(divName);
			Iterator iterator = temp.keySet().iterator();
	        while (iterator.hasNext())
	        {
	            String key = (String)iterator.next();
	            sbd.append(" ").append(key).append("=\"").append(temp.getString(key, "")).append("\"");
	        }
	        sbd.append(">");
	        if(divValue.indexOf("$$") == -1)
	        {
	        	sbd.append(divValue);
	        }
			TextToken ttstart = CommonTools.dealTextToken(sbd, location);
			mainComponet.addBody(ttstart);
			
			if(divValue.indexOf("$$") != -1)
			{
				if(LayoutConstants.PART_TYPE_AREA.equals(partType))
				{
					AreaMainWrite.renderLayouts(point, partId, page, location, mainComponet, namespace, container);
				}
				else
				{
					PartMainWrite.renderLayouts(point, partId, page, location, mainComponet, namespace, container);
				}
			}
			
			//获取根目录下所有子元素
			List<Element> subElts = elemet.elements();
			if(subElts != null && subElts.size() > 0)
			{
				for(int i = 0 ; i < subElts.size() ; i ++)
				{
					Element subElt = subElts.get(i);
					spellTempletArea(point, partId, partType, mainComponet, subElt, location, namespace,  container, page);
				}
			}
			
			StringBuilder sbdEnd = new StringBuilder();
			sbdEnd.append("</").append(divName).append(">");
			TextToken ttEnd = CommonTools.dealTextToken(sbdEnd, location);
			mainComponet.addBody(ttEnd);
		}
		
	}
	
	public static void spellTempletPart(Point point, IData tempData, String tempJwcid, IComponent mainComponet, Element elemet, ILocation location, INamespace namespace, IComponent container, IPage page) throws Exception
	{
		//获取元素属性
		List<Attribute> tempAttributes = elemet.attributes();
		boolean flag = false;
		IData temp = new DataMap();
		String divName = elemet.getName();
		String tempTarget = "";
		String divValue = elemet.getText();
		for(int i = 0 ; i < elemet.attributes().size() ; i ++)
		{
			Attribute tempAttribute = (Attribute)tempAttributes.get(i);
			String attrbuteName = tempAttribute.getName();
			if(LayoutConstants.JWCID.equals(attrbuteName))
			{
				flag = true;
				tempTarget = tempAttribute.getStringValue();
			}
			else
			{
				temp.put(tempAttribute.getName(), tempAttribute.getValue());
			}
		}
		
		if(flag)
		{
			IComponent childComponent = ComponentFactory.builderComponent(page, location, container, namespace, tempTarget, temp);
			if(divValue.indexOf("$$") != -1)
			{
				ElementMainWrite.renderLayouts(point, tempData, tempJwcid, page, location, childComponent, namespace, container);
			}
			//获取根目录下所有子元素
			List<Element> subElts = elemet.elements();
			if(subElts != null && subElts.size() > 0)
			{
				for(int i = 0 ; i < subElts.size() ; i ++)
				{
					Element subElt = subElts.get(i);
					spellTempletPart(point, tempData, tempJwcid, childComponent, subElt, location, namespace,  container, page);
				}
			}
			mainComponet.addBody(childComponent);
		}
		else
		{
			StringBuilder sbd = new StringBuilder();
			sbd.append("<").append(divName);
			Iterator iterator = temp.keySet().iterator();
	        while (iterator.hasNext())
	        {
	            String key = (String)iterator.next();
	            sbd.append(" ").append(key).append("=\"").append(temp.getString(key, "")).append("\"");
	        }
	        sbd.append(">");
	        if(divValue.indexOf("$$") == -1)
	        {
	        	sbd.append(divValue);
	        }
			TextToken ttstart = CommonTools.dealTextToken(sbd, location);
			mainComponet.addBody(ttstart);
			
			if(divValue.indexOf("$$") != -1)
			{
				ElementMainWrite.renderLayouts(point, tempData, tempJwcid, page, location, mainComponet, namespace, container);
			}
			
			//获取根目录下所有子元素
			List<Element> subElts = elemet.elements();
			if(subElts != null && subElts.size() > 0)
			{
				for(int i = 0 ; i < subElts.size() ; i ++)
				{
					Element subElt = subElts.get(i);
					spellTempletPart(point, tempData, tempJwcid , mainComponet, subElt, location, namespace,  container, page);
				}
			}
			
			StringBuilder sbdEnd = new StringBuilder();
			sbdEnd.append("</").append(divName).append(">");
			TextToken ttEnd = CommonTools.dealTextToken(sbdEnd, location);
			mainComponet.addBody(ttEnd);
		}
	}
	
	public static String spellXmlStr(String str) throws Exception
	{
		StringBuilder sbd = new StringBuilder();
		sbd.append("<div>").append(str).append("</div>");
		return sbd.toString();
	}
	
	public static String dealEmpty(String str) throws Exception
	{
		while(str.indexOf("${") != -1)
		{
			StringBuilder sb = new StringBuilder();
			int start = str.indexOf("${");
			int end = str.indexOf("}", start);
			if(end == -1)
			{
				sb.setLength(0);
			}
			else
			{
				sb.append(str.substring(0,start)).append("\"").append("\"").append(str.substring(end+1,str.length()));
			}
			str = sb.toString();
		}
		
		return str;
	}
	
	public static IData dealAttr(IData data, String pre, boolean flag) throws Exception
	{
		IData result = new DataMap();
		if(DataUtils.isEmpty(data))
		{
			return result;
		}
		if(flag)
		{
			result.put((pre+"id").toLowerCase(), data.getString(pre+"ID", ""));
		}
		data.remove(pre+"STA");
		data.remove(pre+"ID");
		data.remove(pre+"KEY");
		Iterator iterator = data.keySet().iterator();
		while (iterator.hasNext())
        {
            String key = (String)iterator.next();
            if(key.startsWith(pre) && StringUtils.isNotEmpty(data.getString(key, "")))
            {
            	String extraKey = key.substring(pre.length()).toLowerCase();
            	result.put(extraKey, data.getString(key, ""));
            }
        }
		
		return result;
	}
	
	public static IData spellElementAttr(IDataset dataset, IData result) throws Exception
	{
		if(DataUtils.isNotEmpty(dataset))
		{
			for(int i = 0 ; i < dataset.size() ; i ++)
			{
				IData data = dataset.getData(i);
				String extraKey = data.getString("EXTRA_KEY", "");
				if(StringUtils.isNotEmpty(data.getString("EXTRA_VALUE", "")))
				{
					result.put(extraKey, data.getString("EXTRA_VALUE", ""));
				}
			}
		}
		return result;
	}
	
	public static IData spellExtraElementAttr(IData data, String pre, Point point) throws Exception
	{
		String pointTwo = point.getPointTwo();
		//是否可以为空
        String nullable = data.getString("nullable","");
        nullable = CommonTools.dealStr(nullable,pointTwo,true);
        if(LayoutConstants.STATIC_ZERO.equals(nullable))
        {
        	data.put("nullable", "no");
        }
        else if(LayoutConstants.STATIC_ONE.equals(nullable))
        {
        	data.put("nullable", "yes");
        }
        else
        {
        	
        }
        
        //是否显示
        String display = data.getString("display","");
        display = CommonTools.dealStr(display,pointTwo,false);
        if(LayoutConstants.STATIC_ZERO.equals(display))
        {
        	data.put("style", "display:none;");
        }
        else if(LayoutConstants.STATIC_ONE.equals(display))
        {
        	data.remove("display");
        }
        else
        {
        	data.remove("display");
        	data.put("style", display);
        }
        
        //是否可编辑
        String disabled = data.getString("disabled","");
        disabled = CommonTools.dealStr(disabled,pointTwo,false);
        if(LayoutConstants.STATIC_ZERO.equals(disabled))
        {
        	data.put("disabled", "true");
        }
        else if(LayoutConstants.STATIC_ONE.equals(disabled))
        {
        	data.remove("disabled");
        }
        else
        {
            
        }
        if(data.getString("nullable","").equalsIgnoreCase("no"))
        {
        	data.put("linkClass", "link required");
        }
        else
        {
        	data.put("linkClass", "link");
        }
		
		IData result = new DataMap();
		if(DataUtils.isEmpty(data))
		{
			return result;
		}
		Iterator iterator = data.keySet().iterator();
		while (iterator.hasNext())
        {
            String key = (String)iterator.next();
            if(key.equalsIgnoreCase("ID") || key.equalsIgnoreCase("NAME") && StringUtils.isNotEmpty(pre))
            {
            	result.put(key, pre+"_"+data.getString(key, ""));
            }
            else if(LayoutConstants.JWCID.equalsIgnoreCase(key))
            {
            	
            }
            else
            {
            	result.put(key, data.getString(key, ""));
            }
        }
		return result;
	}
	
	public static String dealOgnl(String str) throws Exception
	{
		String result = "";
		String [] tempStrs = str.split(":");
		if(tempStrs.length != 2)
		{
			
		}
		else
		{
			result = tempStrs[1];
		}
		return result;
	}
	
	public static String getInnerHtml(IData data) throws Exception
	{
		String result = "";
		if(data.containsKey("$innerHtml"))
		{
			result = data.getString("$innerHtml", "");
		}
		return result;
	}
}
