
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.action.trade;

import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.ModifyUserPwdInfoReqData;

public class ModifyUserPwdInfoMainAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ModifyUserPwdInfoReqData modifyUserPwdInfoRD = (ModifyUserPwdInfoReqData) btd.getRD();
        // 修改主台账
        String passwdType = modifyUserPwdInfoRD.getPasswdType();
        String newPwd = modifyUserPwdInfoRD.getNewPasswd();
        if (StringUtils.isNotBlank(newPwd))
        {
            // newPwd = CrmEncrypt.EncryptPasswd(newPwd);//对密码进行可逆加密
            newPwd = DESUtil.encrypt(newPwd);// 对密码进行可逆加密
        }
        btd.getMainTradeData().setRsrvStr1(newPwd);

        if ("3".equals(passwdType))
        {// 随机密码
            btd.getMainTradeData().setRsrvStr2("3");
            if (btd.getTradeTypeCode().equals("73"))// 重置密码（其实就是随机密码）
            {// 如果是重置密码功能)
                btd.getMainTradeData().setRsrvStr2("9");
            }
        }
        else
        {
            if ("2".equals(passwdType))
            {// 新增密码
                btd.getMainTradeData().setRsrvStr2("0");
            }
            else if ("1".equals(passwdType))
            {// 修改密码
                btd.getMainTradeData().setRsrvStr2("2");
            }
            else if ("4".equals(passwdType))
            { // 取消密码
                btd.getMainTradeData().setRsrvStr2("1");
            }
            else if ("5".equals(passwdType))
            {// 重置密码
                btd.getMainTradeData().setRsrvStr2("9");
            }
        }

        // 外围接口过来时modifyUserPwdInfoRD.getManagemode()才有值
        if (StringUtils.isNotBlank(modifyUserPwdInfoRD.getManagemode()))
        {
            btd.getMainTradeData().setRsrvStr2(modifyUserPwdInfoRD.getManagemode());
            // btd.getMainTradeData().setRsrvStr10(data.getString("DEVELOP_STAFF_ID"));
            if (StringUtils.equals("8", modifyUserPwdInfoRD.getManagemode()))
            {
                btd.getMainTradeData().setRsrvStr2("2");

            }
        }
        // btd.getMainTradeData().setRsrvStr3(modifyUserPwdInfoRD.getManagemode());
    }

}
