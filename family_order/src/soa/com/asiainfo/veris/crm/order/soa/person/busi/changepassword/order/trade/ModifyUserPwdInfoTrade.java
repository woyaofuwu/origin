
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.trade;

import com.ailk.org.apache.commons.lang3.RandomStringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.ModifyUserPwdInfoReqData;

/**
 * 用户密码变更写台账
 * 
 * @author liutt
 */
public class ModifyUserPwdInfoTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createCustUserTradeInfo(btd);

    }

    private void createCustUserTradeInfo(BusiTradeData btd) throws Exception
    {
        ModifyUserPwdInfoReqData reqData = (ModifyUserPwdInfoReqData) btd.getRD();
        UserTradeData userTradeData = btd.getRD().getUca().getUser().clone();
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

        String queryType = reqData.getPasswdType();
        String userId = btd.getRD().getUca().getUser().getUserId();
        if ("3".equals(queryType))// 随机密码
        {
            String passwd = RandomStringUtils.randomNumeric(6);
            String newpasswd = PasswdMgr.encryptPassWD(passwd, userId);// 密文密码
            reqData.setNewPasswd(passwd);
            userTradeData.setUserPasswd(newpasswd);
        }
        else
        {
            String newpasswd = PasswdMgr.encryptPassWD(reqData.getNewPasswd(), userId);// 密文密码
            if ("2".equals(queryType) || "1".equals(queryType))
            {// 新增密码 || 修改密码
                userTradeData.setUserPasswd(newpasswd);
            }
            else if ("4".equals(queryType))
            {// 取消密码
                userTradeData.setUserPasswd("");
            }
        }
        btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);
    }

}
