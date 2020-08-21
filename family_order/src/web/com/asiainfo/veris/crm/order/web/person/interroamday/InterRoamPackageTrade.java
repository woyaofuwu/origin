package com.asiainfo.veris.crm.order.web.person.interroamday;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InterRoamPackageTrade extends PersonBasePage{
    
    public abstract void setInfos(IDataset Infos);
    public abstract void setPackageList(IDataset PackageList);
    public abstract void setInfo(IData Info);
    public abstract void setRowIndex(int rowIndex);
    
    /**
     * 专属叠加包领取
     * @param cycle
     * @throws Exception
     */
    public void getInterRoamPackage(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.put("IS_ROAM_PACKAGE", "1");
        param.put("PRE_TYPE","1");
    	param.put("ELEMENT_ID", data.getString("DISCNT_CODE"));
    	param.put("DISCNT_CODE", data.getString("DISCNT_CODE"));
    	param.put("ELEMENT_TYPE_CODE", "D");
    	param.put("MODIFY_TAG", "0");
    	param.put("IS_ROAM_PACKAGE", "true");
        IDataset dataset = CSViewCall.call(this, "SS.InterRoamDayRegSVC.tradeReg", param);
        if(IDataUtil.isNotEmpty(dataset)){
        	dataset.first().put("RESULT", "1");
        }
        setAjax(dataset.first());
    }
    /**
     * 赠送关系查询
     * @param cycle
     * @throws Exception
     */
    public void GiftRelationQuery(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
    	IDataset result = CSViewCall.call(this, "SS.InterRoamPackageTradeSVC.GiftRelationQuery", data);
    	setInfos(result);
        setAjax(result.first());
    }
    
    /**
     * 订购关系查询
     * @param cycle
     * @throws Exception
     */
    public void interRoamPackageQuery(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	IDataset result = CSViewCall.call(this, "SS.InterRoamPackageTradeSVC.interRoamPackageQuery", data);
    	setPackageList(result);
        setAjax(result.first());
    }
    
}
