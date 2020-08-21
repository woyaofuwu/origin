
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.addmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * @Description 添加已有宽带成员校验
 * @Auther: duhj
 * @Date:
 * @version:
 */
public class CheckAddExistWideMember implements ICheck
{
    @Override
    public boolean check(IData input) throws Exception
    {
        String roleCode = input.getString("ROLE_CODE");
        String roleType = input.getString("ROLE_TYPE");
        String serial_number = input.getString("SERIAL_NUMBER");
        if (!StringUtils.equals(FamilyRolesEnum.WIDENET.getRoleCode(), roleCode) || FamilyConstants.TYPE_NEW.equals(roleType))
        {
            return false;
        }

        String wideSn  = "";//宽带账号
        String phoneSn = "";//手机号码
        
        if(StringUtils.startsWith(serial_number, "KD_")){
            wideSn = serial_number;
            phoneSn = StringUtils.substring(serial_number, 3);
        }else{
            phoneSn = serial_number;
            wideSn = "KD_"+serial_number;
        }
        
        // 成员号码资料查询
        IData wideUserInfo = UcaInfoQry.qryUserInfoBySn(wideSn);

        if (IDataUtil.isEmpty(wideUserInfo))
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-001", "获取宽带" + wideSn + "资料失败");
        }

        String wideUserId = wideUserInfo.getString("USER_ID");

        // 成员号码资料查询
        IData userPhoneInfo = UcaInfoQry.qryUserInfoBySn(phoneSn);

        if (IDataUtil.isEmpty(userPhoneInfo))
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-002", "该宽带" + wideSn + "绑定的手机号码不存在");
        }

        String phoneUserId = userPhoneInfo.getString("USER_ID");

        // 成员号码未完工工单校验
        IDataset tradeInfos = TradeInfoQry.getTradeInfoBySn(wideSn);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
            IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, CSBizBean.getTradeEparchyCode());
            String tradeType = tradeTypeInfo.getString("TRADE_TYPE");
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-003", "该宽带有" + tradeType + "未完工工单");
        }

        // 成员欠费校验
        IData oweFee = AcctCall.getOweFeeByUserId(wideUserId);
        double acctBalance = oweFee.getDouble("ACCT_BALANCE", 0);
        if (acctBalance < 0)
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-004", "该用户" + wideSn + "已经欠费");
        }

        // 是否已经是家庭用户
        IDataset userMembers = FamilyUserMemberQuery.queryFamilyMemInfoByMemberUserIdAndRole(wideUserId, roleCode);
        if (IDataUtil.isNotEmpty(userMembers))
        {
            if (userMembers.size() > 1)
            {
                FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-005", "该用户" + wideSn + "有多条有效的家庭关系，请检查资料");
            }
            else if (userMembers.size() > 0)
            {
                FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-006", "该用户" + wideSn + "已经是家庭成员，不能再加入家庭");
            }
        }

        // 宽带有包年资费，包年活动，宽带1+ 不允许办理，需要先去终止活动再去办理家庭业务
        IDataset saleActives = UserSaleActiveInfoQry.queryUserSaleActiveByTag(phoneUserId);
        if (IDataUtil.isNotEmpty(saleActives))
        {
            for (int i = 0; i < saleActives.size(); i++)
            {
                IData tempSale = saleActives.getData(i);
                String productName = FamilyConstants.WIDENET_SALE.get(tempSale.getString("PRODUCT_ID", ""));
                if (StringUtils.isNotBlank(productName))
                {
                    FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-007", "该用户" + wideSn + "有" + productName + "，不能加入家庭，请先取消掉营销活动");
                }

            }
        }

        // 预约宽带营销活动
        IDataset bookWideActives = SaleActiveInfoQry.queryHdfkActivesByUserId(phoneUserId);
        if (IDataUtil.isNotEmpty(bookWideActives))
        {
            for (int i = 0; i < bookWideActives.size(); i++)
            {
                IData tempSale = bookWideActives.getData(i);
                String productName = FamilyConstants.WIDENET_SALE.get(tempSale.getString("PRODUCT_ID", ""));
                if (StringUtils.isNotBlank(productName))
                {
                    FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-008", "该宽带" + wideSn + "有预约的" + productName + "，不能加入家庭");
                }

            }
        }

//        // 规则检验
//        IData param = new DataMap();
//        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(wideUserInfo.getString("CUST_ID"));
//        if (IDataUtil.isEmpty(customerInfo))
//        {
//            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-009", "该宽带" + wideSn + "查询不到有效的客户信息");
//        }
//
//        param.putAll(wideUserInfo);
//        param.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
//        param.put("TRADE_TYPE_CODE", FamilyConstants.ROLE_TRADE_TYPE.WIDE_CHANGE);
//        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
//        param.put("X_CHOICE_TAG", "0");
//        IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", param);
//        FamilyCallerUtil.addBreerr(input, infos);
      
      //宽带产品变更用户校验接口
      IData param = new DataMap();
      param.put("SERIAL_NUMBER", phoneSn);
      IDataset infos = CSAppCall.call("SS.WidenetChangeProductIntfSVC.checkWideUserInfoIntf", param);

        
        return false;
    }


}
