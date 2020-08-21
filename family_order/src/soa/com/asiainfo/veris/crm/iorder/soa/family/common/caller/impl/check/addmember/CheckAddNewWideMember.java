
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.check.addmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.ICheck;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyCallerUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

/**
 * @Description 新开宽带成员校验
 * @Auther: duhj
 * @Date:
 * @version:
 */
public class CheckAddNewWideMember implements ICheck
{
    @Override
    public boolean check(IData input) throws Exception
    {
        String roleCode = input.getString("ROLE_CODE");
        String roleType = input.getString("ROLE_TYPE");
        String serial_number = input.getString("SERIAL_NUMBER");
        if (!StringUtils.equals(FamilyRolesEnum.WIDENET.getRoleCode(), roleCode) || FamilyConstants.TYPE_OLD.equals(roleType))
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
        
        
        // 成员手机号码资料查询
        IData userPhoneInfo = UcaInfoQry.qryUserInfoBySn(phoneSn);

        if (IDataUtil.isEmpty(userPhoneInfo))
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-001", "该用户" + phoneSn + "资料不存在");
        }

        // 规则检验
        IData param = new DataMap();
        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userPhoneInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(customerInfo))
        {
            FamilyCallerUtil.addErrorInfo(input, "CheckAddMember", "check-002", "该用户" + phoneSn + "查询不到有效的客户信息");
        }

        param.putAll(userPhoneInfo);
        param.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
        param.put("TRADE_TYPE_CODE", FamilyConstants.ROLE_TRADE_TYPE.WIDE_OPEN);
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        param.put("X_CHOICE_TAG", "0");
        IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", param);
        FamilyCallerUtil.addBreerr(input, infos);
        
        return false;
    }



}
