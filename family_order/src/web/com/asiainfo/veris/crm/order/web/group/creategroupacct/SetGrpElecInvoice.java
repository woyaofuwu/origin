
package com.asiainfo.veris.crm.order.web.group.creategroupacct;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
public abstract class SetGrpElecInvoice extends  GroupBasePage
{
	public void initial(IRequestCycle cycle) throws Exception
    {  	
       
	}
	/**
     * 作用：根据group_id查询集团基本信息 默认传入为cond_GROUP_ID
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData result = queryGroupCustInfo(cycle);
        String custId = result.getString("CUST_ID");
        queryUserInfoList(custId);
        this.setAjax(result);
    }
    /**
     * 作用：查询用户信息
     * 
     * @author 
     */
    public void queryUserInfoList(String custId) throws Throwable
    {   	
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        IDataset idata = CSViewCall.call(this, "CS.SetGrpElecInvoiceSVC.queryUserInfo", param);
        setInfos(idata);
    }
    public abstract void setInfos(IDataset infos);
    public abstract void setCondition(IData condition);
//    public abstract void setCustparam(IData custparam);
}
