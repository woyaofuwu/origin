
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dmtradecancel;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DMTradeCancel extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(DMTradeCancel.class);

    /**
     * 查询是触发
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData("baseCommInfo", true);

        IData iparam = new DataMap();

        String paramValue = pageData.getString("VALUE");
        String operType = pageData.getString("OPER_TYPE");

        IDataset dmInfos = null;

        // 根据手机号码
        if ("1".equals(operType))
        {
            iparam.put("SERIAL_NUMBER", paramValue);

            IDataset userInfos = CSViewCall.call(this, "SS.DMTradeCancelSVC.getUserInfo", iparam);

            if (IDataUtil.isNotEmpty(userInfos))
            {
                IData userInfo = userInfos.getData(0);

                dmInfos = CSViewCall.call(this, "SS.DMTradeCancelSVC.loadChildTradeInfo", userInfo);
            }
            else
            {
                CSViewException.apperr(DMBusiException.CRM_DM_154);
            }
        }
        // 根据IMEI号码
        else
        {
            iparam.put("RES_CODE", paramValue);
            dmInfos = CSViewCall.call(this, "SS.DMTradeCancelSVC.queryDmByImei", iparam);
        }
        setInfos(dmInfos);

        IData tempData = dmInfos.getData(0);
        pageData.put("USER_ID", tempData.getString("USER_ID"));
        pageData.put("RSRV_STR1", tempData.getString("RSRV_STR1"));
        pageData.put("RES_CODE", tempData.getString("RES_CODE"));

        setBaseCommInfo(pageData);
    }

    /**
     * 提交时触发
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData("baseCommInfo", true);
        pageData.put("DM_TAG", "DMCANCEL");

        IDataset result = CSViewCall.call(this, "SS.DMTradeBusiRegSVC.tradeReg", pageData);

        setAjax(result);
    }

    public abstract void setBaseCommInfo(IData baseCommInfo);

    public abstract void setInfos(IDataset infos);
}
