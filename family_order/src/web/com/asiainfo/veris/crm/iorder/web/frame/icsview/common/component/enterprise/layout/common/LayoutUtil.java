package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;

public class LayoutUtil 
{
    public final static String LOADEXPRESS="&FLOW_ID&[PUBLIC],&NODE_ID";
    
    /**
    /**
     * 获取页面读取初始化数据配置
     * 
     * @param bpm
     * @param nodeid
     * @return
     */
    public static String getLoadExpression(IData layout) throws Exception
    {
    	String bpm = layout.getString("FLOW_ID", ""); 
    	String nodeid = layout.getString("NODE_ID", ""); ;
        String expression = "";
        if (StringUtils.isNotEmpty(bpm) && StringUtils.isNotEmpty(nodeid))
        {
            IDataInput dataInput = new DataInput();
            dataInput.getData().put("BPM_TEMPLET_ID", bpm);
            dataInput.getData().put("NODE_ID", nodeid);
            dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
            
            IDataOutput output = ServiceFactory.call("SS.TempletGroupBizSVC.queryInfo", dataInput);
            IDataset dataset = output.getData();
            if(null != dataset && dataset.size() > 0)
            {
               IData dataInfo = dataset.getData(0);
               expression = dataInfo.getString("PAGELOAD_EXPRESS","");
            }
        }
        if (StringUtils.isEmpty(expression))
        {
            expression = LOADEXPRESS;
        }

        return expression;
	}
    
    public static String getPageAreaInfos(String templetId, String templetType) throws Exception
    {
    	String result = "";
    	IDataInput dataInput = new DataInput();
        dataInput.getData().put("TEMPLET_ID", templetId);
        dataInput.getData().put("TEMPLET_TYPE", templetType);
        dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
        
        IDataOutput output = ServiceFactory.call("SS.PageTempletSVC.queryInfo", dataInput);
        IDataset dataset = output.getData();
        if(DataUtils.isNotEmpty(dataset))
        {
        	result = CommonTools.spellStr(dataset.first(), "DATA0","DATA1","DATA2","DATA3","DATA4","DATA5","DATA6","DATA7","DATA8","DATA9");
        }
        return result;
    }
    
    public static IDataset getAreaInfos(String areaId) throws Exception
    {
    	IDataInput dataInput = new DataInput();
        dataInput.getData().put("AREA_ID", areaId);
        dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
        
        IDataOutput output = ServiceFactory.call("SS.PageAreaPartSVC.queryInfo", dataInput);
        IDataset dataset = output.getData();
        return dataset;
    }
    
    public static IData getElementInfos(String elementId) throws Exception
    {
    	IDataInput dataInput = new DataInput();
        dataInput.getData().put("ELEMENT_ID", elementId);
        dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
        
        IDataOutput output = ServiceFactory.call("SS.PageElementSVC.queryInfo", dataInput);
        IDataset dataset = output.getData();
        if(DataUtils.isNotEmpty(dataset))
        {
        	return dataset.first();
        }
        else
        {
        	return null;
        }
    }
    
    public static IDataset getElmentInfos(String partId) throws Exception
    {
    	IDataInput dataInput = new DataInput();
        dataInput.getData().put("PART_ID", partId);
        dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
        
        IDataOutput output = ServiceFactory.call("SS.PagePartEleSVC.queryInfo", dataInput);
        IDataset dataset = output.getData();
        return dataset;
    }
    
    public static IData getPartInfo(String partId) throws Exception
    {
    	IDataInput dataInput = new DataInput();
        dataInput.getData().put("PART_ID", partId);
        dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
        
        IDataOutput output = ServiceFactory.call("SS.PagePartSVC.queryInfo", dataInput);
        IDataset dataset = output.getData();
        if(DataUtils.isNotEmpty(dataset))
        {
        	return dataset.first();
        }
        else
        {
        	return null;
        }
    }
    
    public static String getExtraInfos(String extraId, String extraKey, String extraType) throws Exception
    {
    	String result = "";
    	IDataInput dataInput = new DataInput();
        dataInput.getData().put("EXTRA_ID", extraId);
        dataInput.getData().put("EXTRA_KEY", extraKey);
        dataInput.getData().put("EXTRA_TYPE", extraType);
        dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
        
        IDataOutput output = ServiceFactory.call("SS.ExtraSVC.queryInfo", dataInput);
        IDataset dataset = output.getData();
        if(DataUtils.isNotEmpty(dataset))
        {
        	result = dataset.first().getString("EXTRA_VALUE", "");
        }
        
        return result;
    }
    
    public static IDataset getAllExtraInfos(String extraId, String extraType) throws Exception
    {
    	IDataInput dataInput = new DataInput();
        dataInput.getData().put("EXTRA_ID", extraId);
        dataInput.getData().put("EXTRA_TYPE", extraType);
        dataInput.getData().put("STATUS",LayoutConstants.STATUS_VALID);
        IDataOutput output = ServiceFactory.call("SS.ExtraSVC.queryAllInfos", dataInput);
        IDataset dataset = output.getData();
        return dataset;
    }
}
