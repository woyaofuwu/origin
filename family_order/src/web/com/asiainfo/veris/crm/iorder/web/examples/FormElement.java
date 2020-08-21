package com.asiainfo.veris.crm.iorder.web.examples;


import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class FormElement extends BizPage{
	
	public abstract void setInfos(IDataset infos);
	public abstract void setLevels(IDataset levels);
	
	public void init(IRequestCycle cycle) throws Exception{
		IDataset list = new DatasetList();
		
		for(int i = 0; i < 10; i++){
			IData info = new DataMap();
			info.put("TEXT", "选项" + i);
			info.put("TITLE", "title_" + i);
			info.put("VALUE", "value_" + i);
			
			list.add(info);
		}
		
		setInfos(list);

		IDataset levels = new DatasetList();
		for(int j = 0 ; j < 3; j++){
			IData info = new DataMap();
			info.put("TEXT", "级别" + j);
			info.put("VALUE", j);
		
			levels.add(info);
		}
		setLevels(levels);
	}
	
	/**
     * 生成打印数据
     * @param cycle
     * @throws Exception
     */
    public void generationPrintData(IRequestCycle cycle) throws Exception
    {
    	String orderId = getData().getString("ORDER_ID");
    	String eparchyCode = getData().getString("EPARCHY_CODE");
    	if(StringUtils.isBlank(eparchyCode) || StringUtils.equals("undefined", eparchyCode))
    	{
    		eparchyCode = getTradeEparchyCode();
    	}
    	
    	IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        
    	CSViewCall.call(this, "CS.PrintNoteSVC.getPrintData", param);
    }
    
    public String getTradeEparchyCode() throws Exception
    {
        String loginEparchyCode = getVisit().getLoginEparchyCode();

        // 非数字地州，都转换成默认地州
        if (!StringUtils.isNumeric(loginEparchyCode))
        {
            loginEparchyCode = Route.getCrmDefaultDb();
        }

        return loginEparchyCode;
    }
}