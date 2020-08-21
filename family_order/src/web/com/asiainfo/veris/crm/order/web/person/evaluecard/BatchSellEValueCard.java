package com.asiainfo.veris.crm.order.web.person.evaluecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatchSellEValueCard extends PersonBasePage
{
	public abstract void setCondition(IData condition);	
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);
	public abstract void setPageCount(long count);
    public abstract void setEditInfo(IData editInfo);

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData ajaxData = new DataMap();
        IData data = getData();

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));        
        IData editInfo = new DataMap();
        editInfo.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        editInfo.put("PSPT_ADDR", custInfo.getString("PSPT_ADDR"));
        editInfo.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
        editInfo.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        IData conditionInfo = new DataMap();
        conditionInfo.put("cond_SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        
        setEditInfo(editInfo);
        setCondition(conditionInfo);
        setAjax(ajaxData);
    }
    
    /**
     * 业务提交
     * 
     * @param clcle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset result = CSViewCall.call(this, "SS.EValueCardRegSVC.tradeReg", data);    
        IDataset result0 =CSViewCall.call(this,"SS.TelValueCardSVC.batchSellStoreEValueCard",data);
        IData returnData = result0.getData(0);
        //有价卡批量销售 
        if (IDataUtil.isNotEmpty(returnData) && returnData.containsKey("TRANSACTION_ID")) {
        	data.put("TRANSACTION_ID", returnData.getString("TRANSACTION_ID"));
        	IData condition = new DataMap();
        	condition.put("cond_TRANSACTION_ID", returnData.getString("TRANSACTION_ID"));
        	this.setCondition(condition);     	
        	result.getData(0).put("BatchID", returnData.getString("TRANSACTION_ID"));
        }   
        setAjax(result);
    }
	
	/**
	 * 按条件查询要批量销售请求记录
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryBatchReqInfo(IRequestCycle cycle) throws Exception {
		
        IData temp = getData("cond", true);        
        Pagination pagination = getPagination("pageNav");
        IDataOutput output= CSViewCall.callPage(this,"SS.TelValueCardSVC.queryBatchSellReqInfo",temp,pagination);
        IData idata = output.getData().getData(0);
        IDataset idatas = idata.getDataset("REQ_INFO");
        setPageCount(output.getDataCount());
		setInfos(idatas);		
	}
}
