
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserpwd.order.trade;

import com.ailk.common.util.DESUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserpwd.order.requestdata.CttModifyUserPwdInfoReqData;

/**
 * 用户密码变更写台账
 */
public class CttModifyUserPwdInfoTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createCustUserTradeInfo(btd);

    }

    private void createCustUserTradeInfo(BusiTradeData btd) throws Exception
    {
        CttModifyUserPwdInfoReqData reqData = (CttModifyUserPwdInfoReqData) btd.getRD();
        MainTradeData mainTrade = btd.getMainTradeData();
        UserTradeData userTradeData = btd.getRD().getUca().getUser().clone();
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

        String queryType = reqData.getPasswdType();
        String userId = btd.getRD().getUca().getUser().getUserId();
        if ("3".equals(queryType))// 随机密码
        {
            // String passwd = RandomStringUtils.randomNumeric(6);
            String passwd = "123456";
            String newpasswd = PasswdMgr.encryptPassWD(passwd, PasswdMgr.genUserId(userId));// 密文密码
            mainTrade.setRsrvStr1(DESUtil.encrypt(passwd));// 可逆的
            mainTrade.setRsrvStr2("9");// 重置密码
            btd.setMainTradeProcessTagSet(1, "3");
            userTradeData.setUserPasswd(newpasswd);
        }
        else
        {
            mainTrade.setRsrvStr1(DESUtil.encrypt(reqData.getNewPasswd()));// 可逆的
            String newpasswd = PasswdMgr.encryptPassWD(reqData.getNewPasswd(), PasswdMgr.genUserId(userId));// 密文密码
            if ("2".equals(queryType))
            {// 新增密码
                mainTrade.setRsrvStr2("0");// 新增密码
                btd.setMainTradeProcessTagSet(1, "3");
            }
            else if ("1".equals(queryType))
            {// 修改密码
                mainTrade.setRsrvStr2("2");// 修改密码
                btd.setMainTradeProcessTagSet(1, "1");
            }
            userTradeData.setUserPasswd(newpasswd);
        }
        mainTrade.setRemark(reqData.getRemark());
        btd.add(btd.getRD().getUca().getSerialNumber(), userTradeData);
    }

}
