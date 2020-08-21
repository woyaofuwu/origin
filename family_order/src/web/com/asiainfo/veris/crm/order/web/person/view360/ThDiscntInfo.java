
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThDiscntInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 优惠变化
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // 判断TRADE_ID是否存在
        String trade_id = data.getString("TRADE_ID", "").trim();
        if (StringUtils.isBlank(trade_id) || "null".equals(trade_id))
        {
            return;
        }
        IDataset discntInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThDiscntInfo", data);
        if(IDataUtil.isNotEmpty(discntInfo)){
        	for(Object info : discntInfo){
        		IData discntData = (IData) info;
        		discntData.put("DISCNT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "D", discntData.getString("DISCNT_CODE","")));
        	}
        }
        setInfos(discntInfo);
    }

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
