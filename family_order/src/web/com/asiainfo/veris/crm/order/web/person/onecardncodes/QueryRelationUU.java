
package com.asiainfo.veris.crm.order.web.person.onecardncodes;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryRelationUU extends PersonBasePage
{

    public void queryRelationInfos(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset output = CSViewCall.call(this, "SS.QueryRelationUUSVC.queryRelationInfos", inparam);
        //TODO huanghua 翻译产品名称与品牌名称
        IData userInfo = output.getData(0);
        userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", userInfo.getString("PRODUCT_ID","")));
        userInfo.put("BRAND",UpcViewCall.getBrandNameByBrandCode(this, userInfo.getString("BRAND_CODE","")));
        userInfo.put("STATE_NAME",UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", userInfo.getString("USER_STATE_CODESET","")));
        // setCount(output.getDataCount());
        setAjax(output);
        setInfos(output);
        setInfo(output.getData(0));
        setEditInfo(inparam);

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData data);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset datas);
}
