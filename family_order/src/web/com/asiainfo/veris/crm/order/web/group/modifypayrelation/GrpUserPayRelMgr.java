
package com.asiainfo.veris.crm.order.web.group.modifypayrelation;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;


public abstract class GrpUserPayRelMgr extends CSBasePage
{
	public abstract void setCondition(IData data);
    public abstract void setInfos(IDataset infos);
    public abstract void setPageCount(long pageCount);
	public abstract void setInfo(IData info);
	private static final Logger logger = Logger.getLogger(GrpUserPayRelMgr.class);
	 /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
	}
    /**
     * 查询已被集团信控发起暂停集团代付关系的集团产品用户
     * @param cycle
     * @throws Throwable
     * @Author:chenzg
     * @Date:2017-8-1
     */
    public void queryCreditStopPayRel(IRequestCycle cycle) throws Throwable
    {
    	IData param = getData("cond", true);
    	param.put("ROUTE_EPARCHY_CODE", "0898");
    	IDataOutput dataOutput = CSViewCall.callPage(this, "CS.PayRelaInfoQrySVC.getCreditStopPayRelInfo", param, getPagination("ordnav"));	
		IDataset infos = dataOutput.getData();
    	this.setInfos(infos);
    	setPageCount(dataOutput.getDataCount());
    }
    
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitRestart(IRequestCycle cycle) throws Exception	
    {
		IData param = this.getData();
		String checked_UserInfos = param.getString("checked_UserInfos", "");
		IData userInfo = new DataMap(checked_UserInfos);
		//调用集团代付信控恢复服务
		//ROUTE_EPARCHY_CODE=0898,TRADE_TYPE_CODE=7812,USER_ID=1116120832483632,SERIAL_NUMBER=09955251215
		IData svcData = new DataMap();
		svcData.put("TRADE_TYPE_CODE", "7812");								//集团代付关系恢复
		svcData.put("USER_ID", userInfo.getString("USER_ID"));					//集团产品用户ID
		svcData.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));		//集团产品用户号码
		svcData.put("ROUTE_EPARCHY_CODE", "0898");
		svcData.put("FIRE_SOURCE", "CRMWEB");
		IDataset ds = CSViewCall.call(this, "SS.CreditTradeRegSVC.tradeReg", svcData);
		
		IDataset retDataset = new DatasetList();
		IData retData = new DataMap();
		retData.put("RESULT_CODE", "0");
		retData.put("RESULT_INFO", "发起集团代付信控恢复成功,请查询批量任务查看处理结果,"+ds.getData(0).getString("ORDER_ID", ""));
		retDataset.add(retData);
		// 设置返回值
		setAjax(retDataset);
    }
    

}
