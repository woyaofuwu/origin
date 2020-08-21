
package com.asiainfo.veris.crm.order.web.person.interroamday;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class InterRoamPriorityHisQry extends PersonBasePage
{
    public abstract void setPrioInfoList(IDataset giveInfoList);

    
    /**
     * 手机号码校验
     * 
     * @author dengyi5
     * @param cycle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
    	IData idata = getData();
    	idata.put("X_RESULTCODE", "0");
    	idata.put("TRADE_TYPE_CODE", "300");
    	IDataset infos = CSViewCall.call(this, "CS.GetInfosSVC.getUCAInfos", idata);
    	IData ucaData = infos.getData(0);
    	if(DataUtils.isEmpty(ucaData))
    	{
    		idata.put("X_RESULTCODE", "-1");
    		idata.put("X_RESULTINFO", "查询用户信息结果无数据");
    	}
    	setAjax(idata);
    }
    
    /**
     * 订购关系历史优先级查询
     * 
     * @author dengyi5
     * @param cycle
     * @throws Exception
     */
    public void interRoamPriorityHisQry(IRequestCycle cycle) throws Exception
    {
    	IData idata = getData();
    	idata.put("X_RESULTCODE", "0");
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
    	param.put("BEGIN_TIMSI",  SysDateMgr.decodeTimestamp(idata.getString("BEGIN_TIMSI"), "yyyyMMddHHmmss"));
    	param.put("END_TIMSI",  SysDateMgr.decodeTimestamp(idata.getString("END_TIMSI"), "yyyyMMddHHmmss"));
    	IDataset infos = CSViewCall.call(this, "SS.InterRoamPriorityHisQrySVC.interRoamPriorityHisQry", param);
    	if(DataUtils.isEmpty(infos))
    	{
    		idata.put("X_RESULTCODE", "-1");
    		idata.put("X_RESULTINFO", "查询结果无数据");
    	}
    	else
    	{
    		setPrioInfoList(infos);
    	}
    	setAjax(idata);
    }
}
