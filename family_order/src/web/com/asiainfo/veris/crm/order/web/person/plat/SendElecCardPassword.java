
package com.asiainfo.veris.crm.order.web.person.plat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * WLAN电子卡密码下发
 * 
 * @author xiekl
 */
public abstract class SendElecCardPassword extends PersonBasePage
{

    /**
     * 调用一级BOSS接口获取电子卡密码并以短信方式告知用户
     * 
     * @param cycle
     * @throws Exception
     */
    public void getCardPasswd(IRequestCycle cycle) throws Exception
    {

        IData param = new DataMap();

        param.put("SEQ_ID", this.getParameter("SEQ_ID", ""));
        param.put("OPR_NUMB", this.getParameter("OPR_NUMBER", ""));
        param.put("SERIAL_NUMBER", this.getParameter("SERIAL_NUMBER", ""));
        param.put("WLAN_CARD_SEQ", this.getParameter("WLAN_CARD_SEQ", ""));

        IDataset dataset = CSViewCall.call(this, "SS.WlanElecCardSVC.sendCardPassword", param);

        this.setAjax(dataset);
    }

    public void queryElecCardSaleList(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData("cond");
        cond.put("X_RESULTCODE", "");
        this.setCondition(cond);
        IData param = this.getData("cond", true);
        param.put("OPR_TYPE", "F0A");
        param.put("STATE", "Y0A");

        IDataset dataset = CSViewCall.call(this, "SS.WlanElecCardSVC.queryElecCardSaleList", param);
        if (IDataUtil.isEmpty(dataset))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "该用户没有购买WLAN电子扣费卡");
        }

        this.setInfos(dataset);
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset dataset);

}
