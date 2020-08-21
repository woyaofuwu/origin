 package com.asiainfo.veris.crm.order.web.person.testcarduser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * 
 * @author zhuoyingzhi
 * 20160923
 *
 */
public abstract class TestCardUserManage extends PersonBasePage {
	
    public abstract void setCondition(IData custInfo);


    public abstract void setInfos(IDataset goods);

    public abstract void setInfo(IData info);


    public abstract void setPageCount(long count);
    
    
    public void initPageInfo(IRequestCycle cycle)throws Exception{
    	
    }
	
	public void queryTestCardUserinfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");
        IDataOutput testCardUserInfo = CSViewCall.callPage(this, "SS.TestCardUserManageSVC.queryTestCardUserinfo", data, getPagination("recordNav"));
        
        setCondition(data);
        setInfos(testCardUserInfo.getData());
        setPageCount(testCardUserInfo.getDataCount());
        
        IData res=new DataMap();
    	res.put("DATA_COUNT", testCardUserInfo.getData().size());
    	setAjax(res);
    }
	
	/**
	 * 工单方式测试卡类型修改
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
		IData data = getData();
		//调用台帐
		IDataset result = CSViewCall.call(this, "SS.TestCardUserManageRegSVC.tradeReg", data);
		setAjax(result);
    }
	
}
