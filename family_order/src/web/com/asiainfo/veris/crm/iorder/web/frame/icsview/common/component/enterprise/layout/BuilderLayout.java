package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout;

import java.util.List;

import org.apache.tapestry.BindingException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutUtil;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.pagebuilder.BuilderComponent;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.PointCal;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.Point;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.write.PageMainWrite;

public abstract class BuilderLayout extends BuilderComponent
{
    public abstract boolean getMultimatch();
    
    public abstract String getUseTag();
    
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        try
        {
            renderLayouts(writer, cycle);
        }
        catch (BindingException be)
        {
            // 存在不能解析的组件,组件属性未绑定或绑定无法获取数据
            be.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 页面生成组件 
     * 
     * @param writer
     * @param cycle
     * @throws Exception
     */
    protected void renderLayouts(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
    	Object layoutObject = getLayouts();
    	IData layout = new DataMap();
    	if(layoutObject instanceof IData)
    	{
    		layout = (IData)layoutObject;
    	}
    	else
    	{
    		return;
    	}
    	String expression = LayoutUtil.getLoadExpression(layout);
    	String useTag = getUseTag();
    	if(StringUtils.isEmpty(useTag))
    	{
    		useTag = Point.DEFAUL_USE_TAG; 
    	}
    	
    	List<Point> ps = PointCal.getContexts(expression, layout, getMultimatch(), Point.POINT_TYPE_PAGEAREA, getPage());//获取子页面块
    	
    	IDataset jsDataset = new DatasetList();
    	
    	if(ps != null && ps.size() > 0)
    	{
    		for (Point p : ps)
            {
    			if(p.getUseTag().equals(useTag))
    			{
    				String jsStr = LayoutUtil.getPageAreaInfos(p.getTempletId(), LayoutConstants.TEMPLET_JS);//获取模版
    				if(StringUtils.isEmpty(jsStr))
    				{
    					PageMainWrite.renderLayouts(p, getPage(), getLocation(), getContainer(), getNamespace(), writer, cycle, getPage());
    				}
    				else
    				{
    					jsDataset.add(jsStr);
    				}
    			}
            }
    	}
    	
    	if(DataUtils.isEmpty(jsDataset))
    	{
    		return;
    	}
    	for(int i = 0 ; i < jsDataset.size() ; i ++)
    	{
    		String tempJsStr = jsDataset.get(i).toString();
    		addInitJs(tempJsStr, writer, cycle);
    	}
    	addInitJsFunction(writer);
    }
    
    public void addInitJsFunction(IMarkupWriter writer) throws Exception
    {
    	writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("initPageParamCommon();\n"); // 初始化方法
                                                    // init前缀+product_id
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
    }
    
    public void addInitJs(String jsStr, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
    	if(StringUtils.isNotEmpty(jsStr))
    	{
    		String [] srcs = jsStr.split(LayoutConstants.SPLIT_TAG);
    		for(int i = 0 ; i < srcs.length ; i ++)
    		{
    			String script = srcs[i];
                boolean isAjax = isAjaxServiceReuqest(cycle);
                if(StringUtils.isNotEmpty(script))
                {
                    if (isAjax)
                    {
                        includeScript(writer, script);
                    }
                    else
                    {
                        this.getPage().addResBeforeBodyEnd(script);
                    }
                }
    		}
    	}
    }
}