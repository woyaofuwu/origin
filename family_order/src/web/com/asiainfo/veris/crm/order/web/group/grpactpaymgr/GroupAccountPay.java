
package com.asiainfo.veris.crm.order.web.group.grpactpaymgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class GroupAccountPay extends CSBasePage
{
    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setPayInfos(IDataset infos);

    public abstract void setPayInfo(IData acctInfo);

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setBankList(IDataset bankList);

    public abstract void setConsign(IData consign);

    public abstract void setProductInfo(IDataset product);

    public abstract void setGroupAccountList(IDataset groupAccountList);

    public abstract void setHintInfo(String hintInfo);

    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("帐后代付-集团统付管理，支付账户只能是集团账户。先选择的是支付账户，然后选择的是被支付账户，如果做限额的帐后代付，单位为分~~");

    }

    public void queryGroupAccountInfo(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();
        String custId = param.getString("CUST_ID");
        IDataset tradeInfos = new DatasetList();
        IData inputparam = new DataMap();
        inputparam.put("CUST_ID", custId);

        IDataset acccountInfo = CSViewCall.call(this, "SS.BookTradeSVC.queryGroupAccountInfo", inputparam);
        if (null != acccountInfo && acccountInfo.size() > 0)
        {
            for (int i = 0; i < acccountInfo.size(); i++)
            {
                IData account = acccountInfo.getData(i);
                IData data = new DataMap();
                data.put("ACCT_ID", account.getString("ACCT_ID"));
                data.put("PAY_NAME", account.getString("PAY_NAME"));
                data.put("PAY_MODE_CODE", account.getString("PAY_MODE_CODE"));
                data.put("PRODUCT_ID", account.getString("PRODUCT_ID"));
                data.put("PRODUCT_NAME", account.getString("PRODUCT_NAME"));
                data.put("EPARCHY_CODE", account.getString("EPARCHY_CODE"));
                tradeInfos.add(data);
            }
        }

        // setHintInfo("帐后代付-集团统付，支付账户只能是集团账户。先选择的是支付账户，然后选择的是被支付账户，如果做限额的帐后代付，单位为分~~");

        setGroupAccountList(tradeInfos);
    }

    public void getRelationAA(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();
        String acctId = param.getString("ACCT_ID_A");
        String eparchyCode = param.getString("EPARCHY_CODE_A");

        IData inputparam = new DataMap();

        inputparam.put("ACCT_ID_A", acctId);
        inputparam.put("RELATION_TYPE_CODE", "99");
        inputparam.put("ACT_TAG", "1");
        inputparam.put("EPARCHY_CODE_A", eparchyCode);

        IDataset acccountInfo = CSViewCall.call(this, "SS.BookTradeSVC.getRelationAAByActIdATag", inputparam);

        setPayInfos(acccountInfo);
        IData condition = new DataMap();
        condition.put("ACCT_ID_A", acctId);
        condition.put("EPARCHY_CODE_A", eparchyCode);
        setCondition(condition);
    }

    public void delRelationAA(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String checkedRels = param.getString("CHECKED_VALUES");
        String[] relationInfo = checkedRels.split(";");

        IData inData = new DataMap();
        inData.put("ACCT_ID_A", relationInfo[0]);// 代付账户
        inData.put("ACCT_ID_B", relationInfo[1]);// 被代付账户
        inData.put("ACCT_ID", relationInfo[0]);// 支付账户
        inData.put("RELATION_TYPE_CODE", "99");
        inData.put("STATE", "DEL");
        inData.put("TRADE_TYPE_CODE", "4611");// 统付

        IData data = new DataMap();
        data.put("ACCT_ID", inData.getString("ACCT_ID_A"));
        IDataset acctInfos = CSViewCall.call(this, "SS.BookTradeSVC.getAcctInfoByAcctId", data);

        if (acctInfos.size() < 1)
        {
            CSViewException.apperr(GrpException.CRM_GRP_672);
        }
        IData acctInfo = (IData) acctInfos.get(0);
        String custId = acctInfo.getString("CUST_ID");
        data.put("CUST_ID", custId);

        String eparchyCode = getTradeEparchyCode();
        inData.put("USER_EPARCHY_CODE", eparchyCode);
        inData.put("EPARCHY_CODE", eparchyCode);
        inData.put("CUST_ID", custId);
        inData.put("ROLE_CODE_A", "0");// 0-代付账户 1-被代付账户
        inData.put("ROLE_CODE_B", "1");
        inData.put("ORDERNO", "0");

        IDataset resultData = CSViewCall.call(this, "SS.PayrelaAdvChgSVC.createRelaTrade", inData);
        setAjax(resultData);

    }
}
