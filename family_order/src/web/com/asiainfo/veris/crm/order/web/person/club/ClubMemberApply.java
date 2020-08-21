package com.asiainfo.veris.crm.order.web.person.club;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class ClubMemberApply extends PersonBasePage
{
    public abstract void setCondition(IData cond);
    
    public abstract void setCustInfo(IData custInfo);
    
    public abstract void setBookInfo(IData bookInfo);
    
    public abstract void setInModeCodes(IDataset inModeCode);
    
    public abstract void setServiceItem(IDataset serviceItem);
    
    public abstract void setEditInfo(IData editInfo);
    
    
    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	
    	
    	
    	IDataset userList = CSViewCall.call(this,"CS.UserInfoQrySVC.getUserInfoBySN", data);
 		
 		if (userList != null && userList.size() > 0) {
 			IData param = new DataMap();
 			param.put("SERIAL_NUMBER", userList.getData(0).getString("SERIAL_NUMBER", ""));
 			param.put("USER_ID", userList.getData(0).getString("USER_ID", ""));
 			param.put("CUST_ID", userList.getData(0).getString("CUST_ID", ""));
 			IData custInfos = CSViewCall.callone(this,"SS.ModifyCustInfoSVC.getCustInfo", param);
 			IData custInfo = custInfos.getData("CUST_INFO");
 			IDataset custTypeInfo = pageutil.getStaticList("CUST_TYPE",custInfo.getString("CUST_TYPE"));
 			IDataset psptTypeInfo = pageutil.getStaticList("TD_S_PASSPORTTYPE2",custInfo.getString("PSPT_TYPE_CODE"));
 			if((custTypeInfo != null && custTypeInfo.size() > 0 ) && (psptTypeInfo != null && psptTypeInfo.size() > 0))
 			{
 				custInfo.put("CUST_TYPE_INFO", custTypeInfo.getData(0).getString("DATA_NAME"));
 	 			custInfo.put("PSPT_TYPE_INFO", psptTypeInfo.getData(0).getString("DATA_NAME") );
 			}
 			
 	        setCustInfo(custInfo);
 	        setEditInfo(custInfo);
			setCondition(data);
 		}else
 		{
 			IData alertInfo = new DataMap();
 			IDataset result = new DatasetList();
 			alertInfo.put("alertInfo", "查询不到用户信息！");
 			result.add(alertInfo);
 			setAjax(result);
 		}
 		
    	
    }
    
    
    
    public void createApplyServ(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.ApplyServicesSVC.createApplyServ", data);
        setAjax(result);
    }
}
