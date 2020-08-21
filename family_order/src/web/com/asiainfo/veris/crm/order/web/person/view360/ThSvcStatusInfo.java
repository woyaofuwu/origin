
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThSvcStatusInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 服务状态变化
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
        IDataset svcStatusInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThSvcStatusInfo", data);
        if(IDataUtil.isNotEmpty(svcStatusInfo)){
        	for(Object info : svcStatusInfo){
        		IData svcStatuData = (IData) info;
        		svcStatuData.put("STATE_NAME", UpcViewCall.queryStateNameBySvcId(this, svcStatuData.getString("SERVICE_ID",""), svcStatuData.getString("STATE_CODE","")));
        	}
        }

        setInfos(svcStatusInfo);
    }

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
