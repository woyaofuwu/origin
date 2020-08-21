
package com.asiainfo.veris.crm.order.web.person.fixedtelephone.changepayrel;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 普通付费关系变更
 */
public abstract class PayrelaNorChg extends PersonBasePage
{
    /**
     * 普通付费关系变更，输入新号码后的校验
     * 
     * @param cycle
     * @throws Exception
     * @author xj
     */
    public void getNorChgNewSnInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String pdata_id = "";
        String bank_name = "";
        IData inParams = new DataMap();
        inParams.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        inParams.put("OLDUSEREREA", data.getString("OLDUSEREREA"));
        inParams.put("OLDACCTID", data.getString("OLDACCTID"));
        //inParams.put("NET_TYPE_CODE", "12");
        inParams.put("NET_TYPE_CODE", "00");

        IDataset dataset = CSViewCall.call(this, "SS.changePayRelaSVC.getNewSerInfo", inParams);

        IData newUserInfo = dataset.getData(0);
        IData newCustInfo = dataset.getData(1);
        IData newAcctInfo = dataset.getData(2);
        IData newOtherInfo = dataset.getData(3);

        IData iparam = new DataMap();

        iparam.put("EPARCHY_CODE", newUserInfo.getString("EPARCHY_CODE"));
        iparam.put("USER_ID", newUserInfo.getString("USER_ID"));
        iparam.put("ID", newUserInfo.getString("USER_ID"));
        iparam.put("ID_TYPE", "1");

        inParams.put("ACCT_ID", newAcctInfo.getString("ACCT_ID"));
        inParams.put("DEFAULT_TAG", newAcctInfo.getString("DEFAULT_TAG"));
        inParams.put("ACT_TAG", newAcctInfo.getString("ACT_TAG"));
        // 判断是否独立帐户
        String acct_only = CSViewCall.call(this, "SS.changePayRelaSVC.isAcctOnly", inParams).getData(0).getString("ACCT_ONLY");
        IDataset set = new DatasetList();
        IData ajaxNewSnInfo = new DataMap();
        ajaxNewSnInfo.put("USER_ID", newUserInfo.get("USER_ID"));
        ajaxNewSnInfo.put("SERIAL_NUMBER", newUserInfo.get("SERIAL_NUMBER"));
        ajaxNewSnInfo.put("CUST_ID", newCustInfo.get("CUST_ID"));
        ajaxNewSnInfo.put("CUST_NAME", newCustInfo.get("CUST_NAME"));
        ajaxNewSnInfo.put("ACCT_ID", newAcctInfo.get("ACCT_ID"));
        ajaxNewSnInfo.put("PAY_NAME", newAcctInfo.get("PAY_NAME"));
        ajaxNewSnInfo.put("PAY_MODE_CODE", newAcctInfo.get("PAY_MODE_CODE"));
        ajaxNewSnInfo.put("SUPER_BANK_CODE", pdata_id);
        ajaxNewSnInfo.put("BANK_CODE", newAcctInfo.getString("BRAND_CODE1"));
        ajaxNewSnInfo.put("BANK_NAME", bank_name);
        ajaxNewSnInfo.put("BANK_ACCT_NO", newAcctInfo.get("BANK_ACCT_NO"));
        ajaxNewSnInfo.put("DEBUTY_CODE", newAcctInfo.get("DEBUTY_CODE"));
        ajaxNewSnInfo.put("CONTRACT_NO", newAcctInfo.get("CONTRACT_NO"));
        ajaxNewSnInfo.put("BANK_DEPOSIT_TYPE", newAcctInfo.get("RSRV_STR7"));// 分散账期修改 modify by lujun
        String startCyclId = SysDateMgr.getNextCycle();
        ajaxNewSnInfo.put("START_CYCLE_ID", startCyclId);
        // ajaxNewSnInfo.put("START_CYCLE_ID", newOtherInfo.get("START_CYCLE_ID"));
        ajaxNewSnInfo.put("NEWSN_CHECK_FLAG", "TRUE");
        ajaxNewSnInfo.put("RSRV_NUM3", newAcctInfo.getString("RSRV_NUM3"));
        ajaxNewSnInfo.put("ACCT_ONLY", acct_only);
        set.add(ajaxNewSnInfo);

        this.setAjax(set);
    }

    /**
     * 普通付费关系,点Auth组件后的方法 子类重写
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData baseCommInfo = new DataMap();
        IData data = getData();
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        data.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        data.put("USER_ID", userInfo.getString("USER_ID"));
        IData result = CSViewCall.callone(this, "SS.ModifyPayRelationSVC.getBusiParam", data);
        String startCycleId = result.getString("START_CYCLE_ID");
        startCycleId = startCycleId.substring(0, startCycleId.length() - 2);// 账期为年月格式
        baseCommInfo.put("START_CYCLE_ID", startCycleId);
        IData acctInfo = new DataMap(data.getString("ACCT_INFO", ""));
        setAcctInfo(acctInfo);
        setBaseCommInfo(baseCommInfo);
        setUserInfo(userInfo);
        setNewUserInfo(userInfo);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.PayrelaRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setBaseCommInfo(IData baseCommInfo);

    public abstract void setNewUserInfo(IData userInfo);

    public abstract void setUserInfo(IData userInfo);

}
