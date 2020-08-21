
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order.trade;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DESUtil;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order.requestdata.WidenetPswChgRequestData;

public class WidenetPswChgTrade extends BaseTrade implements ITrade
{

    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        WidenetPswChgRequestData reqData = (WidenetPswChgRequestData) btd.getRD();
        btd.getMainTradeData().setRsrvStr1(DESUtil.encrypt(reqData.getNewPasswd()));// 可逆密文
        btd.getMainTradeData().setRsrvStr2(reqData.getQueryType());
        btd.getMainTradeData().setSubscribeType("300");
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createTradeUser(btd);
        createTradeWidenet(btd);
        appendTradeMainData(btd);
    }

    /**
     * 用户子台账
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    protected void createTradeUser(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        WidenetPswChgRequestData reqData = (WidenetPswChgRequestData) btd.getRD();
        UserTradeData userTD = btd.getRD().getUca().getUser().clone();
        userTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        String newpasswd = Encryptor.fnEncrypt(reqData.getNewPasswd(), genUserId(btd.getRD().getUca().getUserId()));
        userTD.setUserPasswd(newpasswd);
        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), userTD);

    }

    /**
     * 用户宽带台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeWidenet(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        WidenetPswChgRequestData reqData = (WidenetPswChgRequestData) btd.getRD();
        IDataset dataset = WidenetInfoQry.getUserWidenetInfo(btd.getRD().getUca().getUserId());
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_1);
        }
        IData wideData = dataset.getData(0);
        WideNetTradeData wtd = new WideNetTradeData(wideData);
        String newpasswd = Encryptor.fnEncrypt(reqData.getNewPasswd(), genUserId(btd.getRD().getUca().getUserId()));
        wtd.setAcctPasswd(newpasswd);
        wtd.setModifyTag(BofConst.MODIFY_TAG_UPD);
        wtd.setRemark("宽带密码变更");
        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), wtd);
    }

    // 产生用户id后九位的
    public String genUserId(String userId) throws Exception
    {
        String userIdTemp = "";
        if (userId.length() >= 9) // 加密那里是userid的后九位，不足前面补零
            userIdTemp = userId.substring(userId.length() - 9, userId.length());
        else
        {
            for (int i = 0; i < 9 - userId.length(); i++)
            {
                userIdTemp += "0";
            }
            userIdTemp += userId;
        }
        return userIdTemp;
    }

}
