
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.action.trade;

import com.ailk.common.util.DESUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.ModifyNetPwdInfoReqData;

public class ModifyNetPwdInfoMainAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ModifyNetPwdInfoReqData reqData = (ModifyNetPwdInfoReqData) btd.getRD();
        // 修改主台账
        btd.getMainTradeData().setRemark(btd.getRD().getRemark());
        String passwdType = reqData.getPasswdType();
        String netPasswd = DESUtil.encrypt(reqData.getNewPasswd());
        if ("2".equals(passwdType))
        {// 新增密码
            btd.getMainTradeData().setRsrvStr1(netPasswd);
            btd.getMainTradeData().setRsrvStr2("0");
        }
        else if ("1".equals(passwdType))
        {// 修改密码
            btd.getMainTradeData().setRsrvStr1(netPasswd);
            btd.getMainTradeData().setRsrvStr2("2");
        }
        else if ("5".equals(passwdType))
        {// 重置密码
            btd.getMainTradeData().setRsrvStr1(netPasswd);
            btd.getMainTradeData().setRsrvStr2("9");
        }

    }

}
