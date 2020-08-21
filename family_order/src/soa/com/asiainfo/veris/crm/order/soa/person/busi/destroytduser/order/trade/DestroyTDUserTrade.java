
package com.asiainfo.veris.crm.order.soa.person.busi.destroytduser.order.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.DestroyUserComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.requestdata.DestroyUserNowReqData;

public class DestroyTDUserTrade extends BaseTrade implements ITrade
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-2 下午03:32:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 chengxf2 v1.0.0 修改原因
     */
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        DestroyUserComm destroyComm = new DestroyUserComm();
        destroyComm.createEndRelationUUTrade(btd);// uu关系

        destroyComm.createEndUserTrade(btd);// 用户
        modifyUserRemoveReasonCode(btd);
        destroyComm.createEndSvcInfoTrade(btd);// 服务
        destroyComm.createEndDiscntInfoTrade(btd);// 优惠
        destroyComm.createEndProductTrade(btd);// 产品
        destroyComm.createEndAttrInfoTrade(btd);// 属性
        destroyComm.createEndResInfoTrade(btd);// 资源
        destroyComm.createEndElementInfo(btd);// 元素
        destroyComm.createEndOtherTrade(btd);// 其他信息
        destroyComm.createEndShareRelaInfoTrade(btd);// 共享关系
        destroyComm.createEndSaleActiveTrade(btd);// 营销活动
        destroyComm.createEndScoreAcctAndPlanTrade(btd); // 积分账户

        if (!StringUtils.equals("7230", btd.getTradeTypeCode()))
        {
            destroyComm.createEndPayRelationInfoTrade(btd);// 付费关系
        }

        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.modifyMainSvcStateByUserId(btd);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-2 下午03:35:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 chengxf2 v1.0.0 修改原因
     */
    private void modifyUserRemoveReasonCode(BusiTradeData btd)
    {
        List<UserTradeData> utds = btd.get("TF_B_TRADE_USER");
        if (utds != null)
        {
            DestroyUserNowReqData destroyUserNowReqData = (DestroyUserNowReqData) btd.getRD();
            String tradeTypeCode = btd.getTradeTypeCode();
            String strRemoveReasonCode = destroyUserNowReqData.getRemoveReasonCode();
            if (StringUtils.equals("7230", tradeTypeCode)) // 欠费预销
            {
                strRemoveReasonCode = "17";
            }
            else if (StringUtils.equals("7240", tradeTypeCode) || StringUtils.equals("7241", tradeTypeCode) || StringUtils.equals("7242", tradeTypeCode) || StringUtils.equals("7243", tradeTypeCode) || StringUtils.equals("7244", tradeTypeCode)) // 欠费注销和3种宽带销户
            {
                strRemoveReasonCode = "18";
            }
            else if (StringUtils.equals("100", tradeTypeCode))// 过户注销
            {
                strRemoveReasonCode = "16";
            }
            else if (StringUtils.equals("234", tradeTypeCode))// 遗失卡
            {
                strRemoveReasonCode = "1";
            }
            else if (StringUtils.equals("1512", tradeTypeCode))// 旅信通销户
            {
                strRemoveReasonCode = "00";
            }
            else if (StringUtils.equals("47", tradeTypeCode) || StringUtils.equals("48", tradeTypeCode))// 遗失卡
            {
                // 携出欠费注销
            }
            destroyUserNowReqData.setRemoveReasonCode(strRemoveReasonCode);
            for (UserTradeData utd : utds)
            {
                utd.setRemoveReasonCode(strRemoveReasonCode);
            }
        }
    }

}
