
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class OCNCMidifyPayRelationBean extends CSBizBean
{

    public IDataset checkSn(IData userInfo) throws Exception
    {
        String sn = userInfo.getString("SERIAL_NUMBER");
        IDataset relationInfosB = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userInfo.getString("USER_ID"), "30", null);
        if (IDataUtil.isEmpty(relationInfosB))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户关系信息出错,无用户关系资料!");
        }
        IDataset relationInfosA = RelaUUInfoQry.getUserRelationAll(relationInfosB.getData(0).getString("USER_ID_A", ""), "30");
        if (IDataUtil.isEmpty(relationInfosA))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户关系信息出错,无用户关系资料!");
        }

        IData acct = new DataMap();
        IData acctOther = new DataMap();

        IDataset dataset = new DatasetList();
        IData userInfoPK = new DataMap();
        IData da = new DataMap();
        String osn = "";
        String custName ="";
        da.clear();
        for (int i = 0; i < relationInfosA.size(); i++)
        {
            da.clear();
            userInfoPK.clear();
            if (relationInfosA.getData(i).getString("ROLE_CODE_B").equals("1"))
            {
                acct = getAcct(relationInfosA.getData(i).getString("USER_ID_B"));
                if (sn.equals(relationInfosA.getData(i).getString("SERIAL_NUMBER_B")))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入的服务号码不是一卡双号副号码，业务无法继续！");
                }
                osn = relationInfosA.getData(i).getString("SERIAL_NUMBER_B");
                IDataset ouserinfo = UserInfoQry.getUserinfo(osn);

                custName = ouserinfo.getData(0).getString("CUST_NAME","***");
            }
            if (relationInfosA.getData(i).getString("ROLE_CODE_B").equals("2"))
            {
                acctOther = getAcct(relationInfosA.getData(i).getString("USER_ID_B"));
            }
        }
        if(acct.getString("ACCT_ID").equals(acctOther.getString("ACCT_ID"))){
        	da.put("ISACCT", "1");
        }else{
        	da.put("ISACCT", "0");
        }
        da.put("ACCTIDA", acct.getString("ACCT_ID"));
        da.put("OTHERSN", osn);
        da.put("CUST_NAME", custName.substring(0, 1)+"**");

        dataset.add(da);
        return dataset;
    }

    // 海南一卡双号绑定付费不能取消一卡双号关系
    // 根据服务号码获取帐户
    public IData getAcct(String userId) throws Exception
    {
        IDataset account = null;
        IData param = new DataMap();
        param.put("USER_ID", userId);
        // 获取用户当前的默认帐户
        account = PayRelaInfoQry.getPayRelaByUserId(userId);
        if (IDataUtil.isEmpty(account))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "无默认付费帐户！");
        }
        String accountId = account.getData(0).getString("ACCT_ID");
        return queryAcctInfo(accountId);
    }

    public IData queryAcctInfo(String accountId) throws Exception
    {
        IData accountInfo = null;
        IData param = new DataMap();
        param.put("ACCT_ID", accountId);
        // 获取用户当前的默认帐户资料
        accountInfo = UcaInfoQry.qryAcctInfoByAcctId(accountId);
        if (IDataUtil.isEmpty(accountInfo))
        {

            CSAppException.apperr(CrmCommException.CRM_COMM_103, "无帐户资料！");
        }

        return accountInfo;
    }
}
