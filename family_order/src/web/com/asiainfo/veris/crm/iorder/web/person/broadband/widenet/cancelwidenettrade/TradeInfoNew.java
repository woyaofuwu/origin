/**
 * 
 */

package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.cancelwidenettrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TradeInfo.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-24 上午10:07:47 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
 */

public abstract class TradeInfoNew extends PersonBasePage 
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-24 上午10:08:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
     */
    public void queryTradeInfo(IRequestCycle cycle) throws Exception
    {
    	IData info = new DataMap();
        IData data = this.getData();
        StringBuilder params = new StringBuilder();
        params.append("&SERIAL_NUMBER=").append(data.getString("SERIAL_NUMBER"));
        params.append("&TRADE_ID=").append(data.getString("TRADE_ID"));
        params.append("&HIS_FLAG=").append(data.getString("HIS_FLAG"));
        info.put("PARAMS", params.toString());
        setInfo(info);
        
        IDataset result = CSViewCall.call(this, "SS.CancelWidenetTradeService.queryTradeInfo", data);
        
        if (IDataUtil.isNotEmpty(result))
        {
            IData resultData = result.first();
            
            resultData.put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, resultData.getString("BRAND_CODE")));
            resultData.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT,resultData.getString("PRODUCT_ID")));
        }
        
        this.setTrade(result.first());
    }
    

    public abstract void setInfo(IData info);

	public abstract void setTrade(IData info);
}
