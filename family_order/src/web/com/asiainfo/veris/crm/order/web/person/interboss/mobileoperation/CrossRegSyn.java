
package com.asiainfo.veris.crm.order.web.person.interboss.mobileoperation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CrossRegSyn extends PersonBasePage
{
    /**
     * 校验用户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkUserInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        IData hidParam = getData();
        param.put("USER_ID", hidParam.getString("cond_USER_ID"));
        IDataset dataset = CSViewCall.call(this, "SS.CrossRegSynSVC.checkUserInfo", param);
        IData retData = dataset.getData(0);
        retData.put("cond_MOBILENUM", param.getString("MOBILENUM"));
        setCondition(retData);

    }

    /**
     * 重载父类函数 加载前资料校验 总体校验规则：1、开户时间超过3个月 不允许做该业务； 2、用户已经是大客户，不允许做该业务； 3、找不到对应的跨区入网号码 不允许做该业务； 4、用户品牌 与
     * 跨区入网号码积分品牌不一致，不允许做该业务； 5、做过本业务的跨区号码不允许再做
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData custInfo = new DataMap(param.getString("CUST_INFO"));
        IData userInfo = new DataMap(param.getString("USER_INFO"));

        param.putAll(userInfo);

        IDataset dataset = CSViewCall.call(this, "SS.CrossRegSynSVC.checkAllInfo", param);

        setUserInfo(userInfo);
        setCustInfo(custInfo);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData idata = getData("cond", true);
        IData subParam = getData();
        idata.put("SERIAL_NUMBER", subParam.getString("AUTH_SERIAL_NUMBER"));
        // IData userInfo = new DataMap(idata.getString("USER_INFO"));
        // idata.put("USER_INFO",userInfo);
        IDataset dataset = CSViewCall.call(this, "SS.CrossRegSynRegSVC.tradeReg", idata);
        /*
         * IData data = dataset.getData(0); if (!"0".equals(data.getString("X_RSPTYPE")) ||
         * !"0000".equals(data.getString("X_RSPCODE"))){ if ("2998".equals(data.getString("X_RSPCODE"))){
         * data.put("X_RSPDESC", "落地方：" + data.getString("X_RSPDESC")); }
         * CSViewException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_3,data.getString("X_RSPDESC")); }
         */
        setAjax(dataset);
    }

    public abstract void setAcctInfo(IData info);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData info);

    public abstract void setUserInfo(IData info);

    public abstract void setVipInfo(IData info);

}
