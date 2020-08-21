/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.bank;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by wukw3
 */
public abstract class bankCancelBind extends PersonBasePage
{
	private static Logger logger = Logger.getLogger(bankBind.class);

    /**
     * 重载父类函数 获取三户以外的参数、业务数据
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();

        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));

        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));
        
        IData acctInfo = new DataMap(pagedata.getString("ACCT_INFO"));

        IDataset results = CSViewCall.call(this, "SS.BankSVC.getBankBindDatas", userInfo);

        this.setBankBinds(results);

        this.setCustInfoView(custInfo);

        this.setUserInfoView(userInfo);
        
        this.setAcctInfoView(acctInfo);

        IData bankInfo = new DataMap();
        bankInfo.put("BANK_NAME", results.getData(0).getString("RSRV_STR2"));
        bankInfo.put("BANK_CARD_NO", results.getData(0).getString("RSRV_STR1"));
        this.setCommInfo(bankInfo);

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.BankCancelBindDealRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData commInfo);

    public abstract void setBankBinds(IDataset bankBind);// 手机和银联卡绑定信息

}
