
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.paramtrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.exception.FamilyException;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IParamTrans;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyMemUtil;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.ParamTransUtil;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.data.UserProd;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * @Description 手机成员受理服务参数转换
 * @Auther: zhenggang
 * @Date: 2020/8/3 11:12
 * @version: V1.0
 */
public class FamilyPhoneChangeOffers implements IParamTrans
{
    @Override
    public IData getTransParamters(IData role) throws Exception
    {
        String newProductId = ParamTransUtil.findRoleMainOffer(role);

        if (StringUtils.isEmpty(newProductId))
        {
            return null;
        }
        String serialNumber = role.getString("SERIAL_NUMBER");
        String roleCode = role.getString("ROLE_CODE");
        IData UserData = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(UserData))
        {
            String userId = UserData.getString("USER_ID");
            UserProd userProd = FamilyMemUtil.getUserAllValidMainProduct(userId);
            if (userProd != null && StringUtils.isNotEmpty(userProd.getNextProductId()))
            {
                if (!newProductId.equals(userProd.getNextProductId()))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_12, serialNumber);
                }
                else if (SysDateMgr.compareTo(userProd.getNextProductStartDate(), SysDateMgr.getFirstDayOfNextMonth()) > 0)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_13, serialNumber);
                }
            }
        }
        IData param = new DataMap();
        param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        param.put("ELEMENT_ID", newProductId);
        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ROLE_CODE", roleCode);
        param.put("TRADE_TYPE_CODE", FamilyConstants.ROLE_TRADE_TYPE.PHONE);
        param.put("CALL_REGSVC", "SS.ChangeProductRegSVC.ChangeProduct");
        return param;
    }
}
