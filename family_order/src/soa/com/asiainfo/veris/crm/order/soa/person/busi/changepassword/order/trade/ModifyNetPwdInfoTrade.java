
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.trade;

import com.ailk.org.apache.commons.lang3.RandomStringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.ModifyNetPwdInfoReqData;

/**
 * 互联网密码变更写台账
 * 
 * @author liutt
 */
public class ModifyNetPwdInfoTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createUserTradeInfo(btd);

    }

    private void createUserTradeInfo(BusiTradeData btd) throws Exception
    {
        ModifyNetPwdInfoReqData reqData = (ModifyNetPwdInfoReqData) btd.getRD();
        UserTradeData userTradeData = btd.getRD().getUca().getUser().clone();
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

        String queryType = reqData.getPasswdType();
        String userId = btd.getRD().getUca().getUser().getUserId();
        if ("5".equals(queryType))// 重置密码
        {
            String passwd = RandomStringUtils.randomAlphabetic(3) + RandomStringUtils.randomNumeric(3);
            reqData.setNewPasswd(passwd);
            String netpasswd = PasswdMgr.encryptPassWD(passwd, userId);// 密文密码
            reqData.setRandomPassWD(passwd);
            userTradeData.setRsrvStr5(netpasswd);
        }
        else
        {// 2新增密码 1修改密码
            String netpasswd = PasswdMgr.encryptPassWD(reqData.getNewPasswd(), userId);// 密文密码
            userTradeData.setRsrvStr5(netpasswd);
        }
        btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);
    }

}
