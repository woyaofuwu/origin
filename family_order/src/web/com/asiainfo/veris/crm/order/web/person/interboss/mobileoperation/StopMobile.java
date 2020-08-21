
package com.asiainfo.veris.crm.order.web.person.interboss.mobileoperation;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class StopMobile extends PersonBasePage
{
    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
    	 IData inparam = getData();
         inparam.put("BIZ_TYPE", "1010");
         inparam.put("password",inparam.getString("USER_PASSWD"));
         inparam.put("PSPT_ID",inparam.getString("IDCARDNUM"));
         inparam.put("CUST_NAME",inparam.getString("CUST_NAME1"));
         IData info = new DataMap();
         IDataset dataset = CSViewCall.call(this, "SS.RemoteResetPswdSVC.openResultAuthF", inparam);
         if (IDataUtil.isNotEmpty(dataset))
         {
         	 info = dataset.getData(0);
         	 setInfo(info);
             setAjax(info);
         }
    }

    public void loadPrintData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        IDataset rePrintDatas = CSViewCall.call(this, "SS.IBossMobileSVC.loadPrintData", data);
        returnData.put("PRINT_DATA", rePrintDatas);
        setAjax(returnData);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        // IData data = getData();
        // setCondition(data);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
 		data.put("OPR_CODE","17");
        data.put("TKJ_TYPE","0202");
 		IDataset dataset = CSViewCall.call(this, "SS.StopOpenMobileSVC.applyStopOpenMobile", data);
 		IData result = dataset.getData(0);
 		if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
 		{
// 		    dataset.getData(0).put("ORDER_ID", result.getString("OPR_NUMB"));
            this.setAjax(dataset);
 		} else {
         	CSViewException.apperr(PlatException.CRM_PLAT_74,result.getString("X_RSPDESC"));
 		}
    }

    public abstract void setCondition(IData condition);

    public abstract void setHiddenInfo(IData info);

    public abstract void setInfo(IData info);
}