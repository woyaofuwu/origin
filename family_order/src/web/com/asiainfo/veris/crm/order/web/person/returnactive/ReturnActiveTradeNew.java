
package com.asiainfo.veris.crm.order.web.person.returnactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReturnActiveTradeNew extends PersonBasePage
{

    public void checkCardCodeRes(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String cardCode_s = data.getString("START_CARD", "");
        String cardCode_e = data.getString("END_CARD", "");
        if ((StringUtils.isNotBlank(cardCode_s)) && StringUtils.isNotBlank(cardCode_e))
        {
            IData param = new DataMap();
            param.put("TRADE_TYPE_CODE", "426");
            param.put("CARDCODE_S", cardCode_s);
            param.put("CARDCODE_E", cardCode_e);

            IDataset infos = CSViewCall.call(this, "SS.ReturnActiveTradeNewSVC.getResInfo", param);
            // IDataset infos = new DatasetList();

            /*
             * param.clear(); param.put("VALUE_CARD_NO", cardCode_s); infos.add(param);
             */

            this.setAjax(infos);
        }
    }

    public void loadInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData info = CSViewCall.callone(this, "SS.ReturnActiveTradeNewSVC.getReturnActiveNewInfo", data);
        setAjax(info.getData("RETURNACTIVE_INFO"));

        // IDataset tt= new DatasetList();
        // IData aa = new DataMap();
        // aa.put("PARAM_NAME", "aaa111");
        // tt.add(aa);
        // aa.put("PARAM_NAME", "aaa222");
        // tt.add(aa);
        // aa.put("HAVE_NUM_FLOW", 2);

        setInfos(info.getDataset("FLOWACTIVE_INFO"));
        setCommInfo(info.getData("RETURNACTIVE_INFO"));

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        // 测试下看看
        IData data = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.ReturnActiveNewRegSVC.tradeReg", data);
        this.setAjax(rtDataset);
    }

    public abstract void setCommInfo(IData info);

    /**
     * 流量王勾选领卡活动
     * 
     * @param info
     */

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
