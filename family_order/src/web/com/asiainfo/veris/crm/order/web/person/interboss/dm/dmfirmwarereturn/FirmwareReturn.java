
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dmfirmwarereturn;

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

public abstract class FirmwareReturn extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(FirmwareReturn.class);

    /**
     * 获取可用固件升级包
     * 
     * @param cycle
     * @throws Exception
     */
    public void getFirmwareReturnDs(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset softwareInfos = CSViewCall.call(this, "SS.FirmwareReturnSVC.getFirmwareReturnDs", pageData);
        setTypes(softwareInfos);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData custInfo = new DataMap(pageData.getString("CUST_INFO"));
        setCustInfo(custInfo);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        IDataset resultInfo = CSViewCall.call(this, "SS.FirmwareReturnSVC.submitConfirmForm", pageData);

        if (IDataUtil.isEmpty(resultInfo))
        {
            CSViewException.apperr(DMBusiException.CRM_DM_151);
        }

        setAjax(resultInfo);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setTypes(IDataset types);
}
