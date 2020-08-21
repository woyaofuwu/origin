
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.addmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * @Description 添加手机成员校验
 * @Auther: zhenggang
 * @Date: 2020/8/6 19:28
 * @version: V1.0
 */
public class CheckAddPhoneMember implements ICheck
{
    @Override
    public boolean check(IData input) throws Exception
    {
        String roleCode = input.getString("ROLE_CODE");
        String roleType = input.getString("ROLE_TYPE");
        String busiType = input.getString("BUSI_TYPE");
        String serialNumber = input.getString("SERIAL_NUMBER");

        if (!StringUtils.equals(FamilyRolesEnum.PHONE.getRoleCode(), roleCode) || FamilyConstants.TYPE_NEW.equals(roleType))
        {
            return false;
        }

        // 成员号码资料查询
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfo))
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-001", "获取用户" + serialNumber + "资料失败");
        }

        // 成员号码未完工工单校验
        IDataset tradeInfos = TradeInfoQry.getTradeInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
            IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, CSBizBean.getTradeEparchyCode());
            String tradeType = tradeTypeInfo.getString("TRADE_TYPE");
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-002", "该用户有" + tradeType + "未完工工单");
        }

        // 成员欠费校验
        String userId = userInfo.getString("USER_ID");
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        double acctBalance = oweFee.getDouble("ACCT_BALANCE", 0);
        if (acctBalance < 0)
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-003", "该用户" + serialNumber + "已经欠费");
        }

        // 是否已经是家庭用户
        IDataset userMembers = FamilyUserMemberQuery.queryFamilyMemInfoByMemberUserIdAndRole(userId, roleCode);
        if (IDataUtil.isNotEmpty(userMembers))
        {
            if (userMembers.size() > 1)
            {
                FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-004", "该用户" + serialNumber + "有多条有效的家庭关系，请检查资料");
            }
            else if (userMembers.size() > 0)
            {
                FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-005", "该用户" + serialNumber + "已经是家庭成员，不能再加入家庭");
            }
        }
        return false;
    }
}
