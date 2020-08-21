
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThSvcInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 服务变化
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String trade_id = data.getString("TRADE_ID", "").trim();
        if (StringUtils.isBlank(trade_id) || "null".equals(trade_id))
        {
            return;
        }
        IDataset svcInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThSvcInfo", data);
        if(IDataUtil.isNotEmpty(svcInfo)){
        	for(Object info : svcInfo){
        		IData svcData = (IData) info;
        		svcData.put("SERVICE_NAME", UpcViewCall.queryOfferNameByOfferId(this, "S", svcData.getString("SERVICE_ID","")));
        	}
        }

        setInfos(svcInfo);
    }

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
