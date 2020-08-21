
package com.asiainfo.veris.crm.order.web.person.infomanage;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户动态策略计费控制信息导入
 */
public abstract class CustomerTitle extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(CustomerTitle.class);

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

        IDataset infos = CSViewCall.call(this, "SS.CustomerTitleSVC.getUserOtherInfo", pageData);

        if (IDataUtil.isNotEmpty(infos))
        {
            if (IDataUtil.isNotEmpty(infos.getData(0)))
            {
                userInfo.put("RSRV_VALUE", infos.getData(0).getString("RSRV_VALUE"));
            }
        }

        // 校验该用户是否可以办理该项业务
        // CSViewCall.call(this, "SS.CustomerTitleSVC.checkUserState", userInfo);

        setUserInfo(userInfo);
        setCustInfo(custInfo);

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

        IDataset dataset = CSViewCall.call(this, "SS.CustomerTitleRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);

}
