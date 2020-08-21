
package com.asiainfo.veris.crm.order.web.person.specialtrademgr;

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
public abstract class ProtectPassInfo extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(ProtectPassInfo.class);

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

        IDataset userSvcInfos = CSViewCall.call(this, "SS.ProtectPassInfoSVC.queryUserSvcUserId", pageData);

        String flag = "false";
        IData inparam = new DataMap();
        IData userSvc = new DataMap();
        IData userOther = new DataMap();
        if (IDataUtil.isNotEmpty(userSvcInfos))
        {
            if (IDataUtil.isNotEmpty(userSvcInfos.getData(0)))
            {
                userSvc = userSvcInfos.getData(0);
                IDataset userOtherInfos = CSViewCall.call(this, "SS.ProtectPassInfoSVC.queryUserOtherUserId", pageData);
                if (IDataUtil.isNotEmpty(userOtherInfos))
                {
                    if (IDataUtil.isNotEmpty(userOtherInfos.getData(0)))
                    {
                        userOther = userOtherInfos.getData(0);
                        flag = "true";
                        // 自定义问题
                        if ("z".equals(userOther.getString("RSRV_STR6")))
                        {
                            userOther.put("CUSTOM_QUESTION_FIRST", userOther.getString("RSRV_STR11"));
                        }
                        if ("z".equals(userOther.getString("RSRV_STR7")))
                        {
                            userOther.put("CUSTOM_QUESTION_SECOND", userOther.getString("RSRV_STR13"));
                        }
                        if ("z".equals(userOther.getString("RSRV_STR8")))
                        {
                            userOther.put("CUSTOM_QUESTION_THIRD", userOther.getString("RSRV_STR15"));
                        }
                        // 问题字段
                        userOther.put("QUESTION_FIRST", userOther.getString("RSRV_STR6"));
                        userOther.put("QUESTION_SECOND", userOther.getString("RSRV_STR7"));
                        userOther.put("QUESTION_THIRD", userOther.getString("RSRV_STR8"));
                        userOther.put("EMAIL", userOther.getString("RSRV_STR17"));
                        userOther.put("START_TIME", userOther.getString("RSRV_DATE10").substring(0, 19));
                        userOther.put("UPDATE_TIME", userOther.getString("UPDATE_TIME").substring(0, 19));
                        inparam.putAll(userOther);
                    }
                }
            }
        }

        IData info = new DataMap();
        userInfo.put("PROTECT_USER_SVC", userSvc.toString());
        userInfo.put("PROTECT_PASS", inparam.toString());
        userInfo.put("FLAG", flag.toString());
        info.putAll(inparam);

        setUserInfo(userInfo);

        setInfo(info);

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
        IData otherData = getData("otherinfo", true);
        data.putAll(otherData);
        IDataset dataset = CSViewCall.call(this, "SS.ProtectPassInfoRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData Info);

    public abstract void setUserInfo(IData userInfo);

}
