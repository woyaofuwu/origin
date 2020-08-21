
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThProductInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 产品变化
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
        IDataset productInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThProductInfo", data);
        if(IDataUtil.isNotEmpty(productInfo)){
        	for(Object info : productInfo){
        		IData productData = (IData) info;
        		productData.put("OLD_PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", productData.getString("OLD_PRODUCT_ID","")));
        		productData.put("OLD_BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, productData.getString("OLD_BRAND_CODE","")));
        		productData.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", productData.getString("PRODUCT_ID","")));
        		productData.put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, productData.getString("BRAND_CODE","")));
        	}
        }
        setInfos(productInfo);

    }

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
