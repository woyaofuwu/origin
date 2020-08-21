
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThPlatSvcInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 平台业务
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryThPlatSvcInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String trade_id = data.getString("TRADE_ID", "").trim();
        if (StringUtils.isBlank(trade_id) || "null".equals(trade_id))
        {
            return;
        }
        IDataset userInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThPlatSvcInfo", data);
        if(IDataUtil.isNotEmpty(userInfo)){
        	for(Object info : userInfo){
        		IData platSvcData = (IData) info;
        		platSvcData.put("SERVICE_NAME", UpcViewCall.queryOfferNameByOfferId(this, "Z", platSvcData.getString("SERVICE_ID","")));
        	}
        }
        setBaseInfo(userInfo);
        setCommInfo(data);
    }

    /**
     * 根据Trade_Id查询平台业务历史
     * 
     * @throws Exception
     */
    public void queryPlatAttrInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset userInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.queryPlatAttrInfo", data);
        setInfos(userInfo);
    }

    public abstract void setBaseInfo(IDataset baseInfo);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setInfos(IDataset indos);
}
