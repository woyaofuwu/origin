
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class OneCardNCodesCancleBean extends CSBizBean
{

    public IDataset checkInfos(IData userInfo, IData userResInfo) throws Exception
    {

        // IData userResInfo = new DataMap();
        IData resInfo = new DataMap();
        IData resOtherInfo = new DataMap();
        IDataset relation = new DatasetList();

        if (userInfo == null)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户信息出错，无用户信息!");
        }
        // 是否已经是一卡双号用户

        IDataset userRelationInfos = RelaUUInfoQry.getUserRelation(userInfo.getString("USER_ID"));
        if (IDataUtil.isEmpty(userRelationInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询不到用户关系资料，业务无法继续！");
        }
        for (int i = 0; i < userRelationInfos.size(); i++)
        {
            String userIdA = userRelationInfos.getData(i).getString("USER_ID_A");
            if (userIdA.equals(""))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户不是一卡双号用户，业务无法继续！");
            }
            if (!userRelationInfos.getData(i).getString("ROLE_CODE_B").equals("1"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的主号码信息");
            }
            relation = RelaUUInfoQry.getUserRelationAll(userIdA);
        }

        IData oRelation = new DataMap();
        for (int i = 0; i < relation.size(); i++)
        {
            String role_code_b = relation.getData(i).getString("ROLE_CODE_B");
            if (role_code_b.equals("2"))
            {
                oRelation = relation.getData(i);
            }
        }
        if (IDataUtil.isEmpty(oRelation))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的副号码信息");
        }
        String osn = oRelation.getString("SERIAL_NUMBER_B");

        // 副号码信息
        IData ouser = UcaInfoQry.qryUserInfoBySn(osn);
        IDataset ores = UserResInfoQry.queryUserResByUserIdResType(ouser.getString("USER_ID"), "1");
        IData ocust = UcaInfoQry.qryPerInfoByCustId(ouser.getString("CUST_ID"));

        IDataset userPro = UserProductInfoQry.getProductInfo(ouser.getString("USER_ID"), "-1");
        String brand_code = userPro.getData(0).getString("BRAND_CODE");
        String product_id = userPro.getData(0).getString("PRODUCT_ID");
		String custName =  ocust.getString("CUST_NAME");

        ouser.put("PRODUCT_ID", product_id);
        ouser.put("BRAND_CODE", brand_code);
        ocust.put("CUST_NAME", custName.substring(0, 1)+"**");
		ouser.put("PSPT_ADDR", "*****");

		
        IData acctM = getAcct(userInfo.getString("USER_ID"));
        IData acctO = getAcct(ouser.getString("USER_ID"));
        if (!(IDataUtil.isEmpty(acctM) || IDataUtil.isEmpty(acctO)))
        {
            if (acctM.getString("ACCT_ID").equals(acctO.getString("ACCT_ID")))
            {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "主副卡已经绑定付费，无法受理一卡双号取消业务！");
            }
        }
        
        if (IDataUtil.isEmpty(ores))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的用户SIM卡资源信息！");
        }

        IDataset dataset = new DatasetList();
        IData data = new DataMap();

        data.put("OUSERRESINFO", ores.getData(0));
        data.put("OUSERINFO", ouser);
        data.put("OCUSTINFO", ocust);
        data.put("USERINFO", userInfo);
        data.put("USERRESINFO", userResInfo);
        dataset.add(data);

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
