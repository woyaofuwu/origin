
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.init;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyMemUtil;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.data.UserProd;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * @Description 业务初始化公用校验
 * @Auther: zhenggang
 * @Date: 2020/8/20 10:01
 * @version: V1.0
 */
public class CheckFamilyUserInit implements ICheck
{

    @Override
    public boolean check(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        // 家庭用户号码资料查询
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfo))
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckFamilyUser", "check-001", "获取用户" + serialNumber + "资料失败");
        }

        // 家庭用户号码未完工工单校验
        IDataset tradeInfos = TradeInfoQry.getTradeInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
            IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, CSBizBean.getTradeEparchyCode());
            String tradeType = tradeTypeInfo.getString("TRADE_TYPE");
            FamilyCallerUtil.addErrorInfo(input, "CheckFamilyUser", "check-002", "该用户有" + tradeType + "未完工工单");
        }

        // 家庭用户号码欠费校验
        String userId = userInfo.getString("USER_ID");
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        double acctBalance = oweFee.getDouble("ACCT_BALANCE", 0);
        if (acctBalance < 0)
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckFamilyUser", "check-003", "该用户" + serialNumber + "已经欠费");
        }

        // 获取业务类型
        String busiType = input.getString("BUSI_TYPE");

        // 成员管理校验
        if (FamilyConstants.FamilyTradeType.UPDATE_MEMBER.getValue().equals(busiType))
        {
            UserProd userProd = FamilyMemUtil.getUserAllValidMainProduct(userId);

            if (StringUtils.isNotEmpty(userProd.getNextProductId()))
            {
                String tipName = "[" + userProd.getNextProductId() + "]" + userProd.getNextProductName();
                FamilyCallerUtil.addErrorInfo(input, "CheckFamilyUser", "check-004", "该家庭用户存在预约生效的产品" + tipName + "，不能办理该业务");
            }
        }
        return false;
    }

}
