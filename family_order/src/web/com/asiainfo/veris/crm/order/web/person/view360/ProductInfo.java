
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ProductInfo extends PersonBasePage
{

    /**
     * 产品信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("1".equals(data.getString("SelectTag", "0")))
        {
            IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryProductInfo", data);
            if(IDataUtil.isNotEmpty(output)){
            	for(int i = 0 ; i < output.size() ; i++){
            		output.getData(i).put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, output.getData(i).getString("BRAND_CODE","")));
            	}
            }
            setInfos(output);
        }
        else
        {
            // IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryProductInfo", data,
            // getPagination("ProductNav"));
            // setInfos(output.getData());
            // setInfosCount(output.getDataCount());
            IDataset output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryProductInfo", data);
            if(IDataUtil.isNotEmpty(output)){
            	for(int i = 0 ; i < output.size() ; i++){
            		output.getData(i).put("BRAND_NAME", UpcViewCall.getBrandNameByBrandCode(this, output.getData(i).getString("BRAND_CODE","")));
            	}
            }
            setInfos(output);
        }

    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
