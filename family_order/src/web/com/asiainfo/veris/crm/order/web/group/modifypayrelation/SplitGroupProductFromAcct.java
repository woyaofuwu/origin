package com.asiainfo.veris.crm.order.web.group.modifypayrelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PayRelationTradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SplitGroupProductFromAcct extends GroupBasePage
{
    public abstract void setAcctInfo(IData idata);

    public abstract void setAcctInfoDesc(IData acctInfoDesc);

    public abstract IData getGroupInfo();

    public abstract void setAcctInfos(IDataset idataset);

    public abstract void setCondition(IData idAcctInfo);

    public abstract void setConsignDesc(IData consignDesc);

    public abstract void setGroupInfo(IData groupInfo);
    
    
    public void queryAcctInfos(IRequestCycle cycle) throws Throwable{
       
        String custId = this.getParameter("CUST_ID");
        String curacctId = this.getParameter("ACCT_ID"); 
        
        if (StringUtils.isEmpty(custId))
            return;

        IData inParam = new DataMap();
        inParam.put("CUST_ID", custId);
        inParam.put("ACCT_ID", curacctId);
        inParam.put("SERIAL_NUMBER", this.getParameter("SERIAL_NUMBER"));
      
        // 查询集团账户信息
        IDataset curAcctInfos  = CSViewCall.call(this, "SS.SplitGroupProductFromAcct.queryDefPayRelationByCustIdAndAcctId", inParam);
        if(curAcctInfos != null && curAcctInfos.size() ==1)
        {
            CSViewException.apperr(PayRelationTradeException.CRM_PAYRELATION_26,this.getParameter("SERIAL_NUMBER"),curacctId);
        }
        //查询当前账户下的集团产品
        IDataset acctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", inParam);
        
        if (IDataUtil.isNotEmpty(acctInfos))
        {
            for (int i = 0, len = acctInfos.size(); i < len; i++)
            {
                IData acctInfoData = acctInfos.getData(i);
                String acctId = acctInfoData.getString("ACCT_ID", "");
                String payName = acctInfoData.getString("PAY_NAME", "");
                String productId = acctInfoData.getString("PRODUCT_ID", "");
                String productName = acctInfoData.getString("PRODUCT_NAME", "");

                String payModeCode = acctInfoData.getString("PAY_MODE_CODE", "");
                String userId = acctInfoData.getString("USER_ID", "");
                String epachyCode = acctInfoData.getString("EPARCHY_CODE", "");
                acctInfoData.put("DISPLAY_NAME", acctId + "|" + payName + "|" + productId + "|" + productName + "|" + userId + "|" + epachyCode + "|" + payModeCode);
            }
        }

        setAcctInfos(acctInfos);
    }

    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData param = new DataMap();
        
        param.putAll(getData("group",true));
        param.put("NEW_ACCT_ID", data.getString("acct_ACCT_ID"));

        IDataset dataset = CSViewCall.call(this, "SS.SplitGroupProductFromAcct.crtTrade", param);

        setAjax(dataset);
    }
}
