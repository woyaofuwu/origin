
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetmodifypwd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttBroadBandModifyPWD extends PersonBasePage
{
    /**
     * 修改密码校验
     */
    public void checkPwd(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadBandModifyPWDSVC.checkPwd", data);
        setAjax(dataset);

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadBandModifyPWDRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public void qryBroadBandUser(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset userInfos = CSViewCall.call(this, "SS.CttBroadBandModifyPWDSVC.qryBroadBandUser", data);
        if (IDataUtil.isEmpty(userInfos))
        {
            CSViewException.apperr(BroadBandException.CRM_BROADBAND_4);
        }
        else
        {
            IData userInfo = new DataMap(data.getString("USER_INFO", ""));
            IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
            IData acctInfo = new DataMap(data.getString("ACCT_INFO", ""));

            String productId = userInfo.getString("PRODUCT_ID");
            
            if (StringUtils.isNotBlank(productId))
            {
                userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", productId));
            }
            
            IData info = new DataMap();
            info.putAll(userInfo);
            info.putAll(acctInfo);
            info.putAll(custInfo);
            info.putAll(userInfos.getData(0));
            setInfo(info);
        }
    }

    public abstract void setDiscntInfo(IData discntInfo);

    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
