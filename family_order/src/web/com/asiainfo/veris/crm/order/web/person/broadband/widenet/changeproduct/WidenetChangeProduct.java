
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.changeproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetChangeProduct extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.WidenetChangeProductSVC.loadChildInfo", data);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAjax(result);
        }
        String productList = result.getData(0).getString("PRODUCT_LIST");
        setProductList(new DatasetList(productList));
    }

    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        if ("ADSL".equals(data.getString("WIDE_TYPE")) || "TTADSL".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "614");
        }
        else if ("XIAN".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "616");
        }
        else if ("GPON".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "601");
        }
        else if ("SCHOOL".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "631");
        }
        setInfo(param);
    }

    public abstract void setInfo(IData info);

    public abstract void setProductList(IDataset productList);

    public void submitChangeProduct(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IDataset result = CSViewCall.call(this, "SS.WidenetChangeProductRegSVC.tradeReg", data);
        this.setAjax(result);
    }
}
