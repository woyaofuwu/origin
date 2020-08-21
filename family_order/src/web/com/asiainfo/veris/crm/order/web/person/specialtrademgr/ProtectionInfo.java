
package com.asiainfo.veris.crm.order.web.person.specialtrademgr;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ProtectionInfo extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(ProtectionInfo.class);

    public static final String msgSuc = "S";

    public static final String msgFail = "F";

    public static final String msgKey = "MSG";

    public static final String msgTypeKey = "MSG_TYPE";

    public static final String msgNameKey = "NAME";

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();
        IData userInfo = new DataMap(pageData.getString("USER_INFO"));
        pageData.putAll(userInfo);
        IData custInfo = new DataMap(pageData.getString("CUST_INFO"));
        pageData.putAll(custInfo);

        IData UserInparam = new DataMap();
        UserInparam.put("USER_ID", userInfo.getString("USER_ID"));
        UserInparam.put("SERVICE_MODE", "2");
        UserInparam.put("PROCESS_TAG", "0");

        IDataset rs = CSViewCall.call(this, "SS.ProtectionInfoSVC.getForbidenInfo", pageData);
        if (IDataUtil.isNotEmpty(rs))
        {
            if (!"".equals(((IData) rs.getData(0)).getString("PARTITION_ID", "")))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_1078);
            }
        }

        IDataset userAttr = CSViewCall.call(this, "SS.ProtectionInfoSVC.getUserAttrBySvcId", pageData);

        IData info = new DataMap();
        info.put("FLAG", "0");
        if (IDataUtil.isNotEmpty(userAttr))
        {
            if (IDataUtil.isNotEmpty(userAttr.getData(0)))
            {
                info.put("SERV_PARAM", userAttr.getData(0).toString());
                info.put("FLAG", "1");
                info.put("USER_PASSWD", userAttr.getData(0).getString("ATTR_VALUE"));
            }

        }

        setUserInfo(userInfo);

        setInfo(info);

        setAjax(info);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     *             zhuyu 2014-3-18
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData authData = getData("AUTH", true);
        data.putAll(authData);
        IData condData = getData("cond", true);
        data.putAll(condData);
        IDataset dataset = CSViewCall.call(this, "SS.ProtectInfoRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData Info);

    public abstract void setUserInfo(IData userInfo);

}
