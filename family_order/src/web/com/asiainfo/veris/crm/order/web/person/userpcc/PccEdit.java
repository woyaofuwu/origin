
package com.asiainfo.veris.crm.order.web.person.userpcc;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户动态策略计费控制信息导入
 */
public abstract class PccEdit extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(PccEdit.class);

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

        IDataset infos = CSViewCall.call(this, "SS.PccEditSVC.getPccUserInfByUserId", pageData);
        setEditList(infos);
        setUserInfo(userInfo);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     *             zhuyu 2014-4-14
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.PccEditSVC.dealInfo", data);

    }

    public abstract void setEditList(IDataset editList);

    public abstract void setUserInfo(IData info);

}
