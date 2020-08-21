
package com.asiainfo.veris.crm.order.web.person.bank;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 银行签约信息查询
 */
public abstract class SignedBankPaymentDeal extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(SignedBankPaymentDeal.class);

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();

        IData userInfo = new DataMap(pageData.getString("USER_INFO"));

        pageData.putAll(userInfo);

        IDataset infos = CSViewCall.call(this, "SS.SignedBankPaymentDealSVC.getBankInfo", pageData);

        setEditInfo(infos.getData(0));
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("bank", true);
        IData pagedata = getData();

        if (StringUtils.isBlank(pagedata.getString("SERIAL_NUMBER", "")))
        {
            pagedata.put("SERIAL_NUMBER", pagedata.getString("AUTH_SERIAL_NUMBER"));
        }

        data.putAll(pagedata);

        IDataset dataset = CSViewCall.call(this, "SS.SignedBankPaymentDealRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setEditInfo(IData bankInfo);

}
