
package com.asiainfo.veris.crm.order.web.person.saleactivetd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/** 营销活动受理 */
public abstract class SaleActiveTradeTD extends PersonBasePage
{

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String stuMobilePhone = data.getString("STU_MOBILPHONE", "");
        setStuMobilePhone(stuMobilePhone);
        String paramCode = data.getString("PARAM_CODE", "");
        setParamCode(paramCode);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        // 测试下看看
        IData data = getData();

        data.putAll(new DataMap(data.getString("SALEACITVEDATA")));
        data.remove("SALEACITVEDATA");
        // param.put("PRODUCT_TYPE", data.getString("CAMPN_TYPE"));
        data.put("TRADE_TYPE_CODE", "3814");
        data.put("ORDER_TYPE_CODE", "3814");
        IDataset rtDataset = CSViewCall.call(this, "SS.SaleActiveRegSVC.tradeReg", data);
        this.setAjax(rtDataset);
        // setProducts(products);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setGoods(IDataset goods);

    public abstract void setInfo(IData info);

    public abstract void setParamCode(String paramCode);

    public abstract void setProducts(IDataset products);

    public abstract void setStuMobilePhone(String stuMobilePhone);

    public abstract void setYcPackages(IDataset ycPackages);
}
