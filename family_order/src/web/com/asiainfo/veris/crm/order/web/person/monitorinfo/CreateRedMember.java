
package com.asiainfo.veris.crm.order.web.person.monitorinfo;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户动态策略计费控制信息导入
 */
public abstract class CreateRedMember extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(CreateRedMember.class);

    public static final String msgSuc = "S";

    public static final String msgFail = "F";

    public static final String msgKey = "MSG";

    public static final String msgTypeKey = "MSG_TYPE";

    public static final String msgNameKey = "NAME";

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();
        IData userInfo = new DataMap(pageData.getString("USER_INFO"));
        userInfo.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        pageData.putAll(userInfo);
        IData custInfo = new DataMap(pageData.getString("CUST_INFO"));
        pageData.putAll(custInfo);
        
        //add by duhj 通过调产商品接口,查询产品与名牌名称
        IData result = CSViewCall.callone(this, "SS.CreateRedMemberSVC.getUserName", pageData);
        userInfo.putAll(result);
        
        // 交易用户短信服务是否开通
        IDataset infos = CSViewCall.call(this, "SS.CreateRedMemberSVC.checkUserInfo", pageData);

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

        IDataset dataset = CSViewCall.call(this, "SS.CreateRedMemberSVC.createRedMember", data);

        for (int i = 0; i < dataset.size(); i++)
        {
            IData ret = dataset.getData(i);
            String SucFlag = ret.getString("F");

            if (StringUtils.isNotBlank(SucFlag))
            {
                setAjaxMsg();
            }
        }
    }

    protected void setAjaxMsg()
    {
        IData ajax = new DataMap();
        ajax.put(msgKey, msgSuc);
        setAjax(ajax);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);

}
