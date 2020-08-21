
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class NpOutEffectiveFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");

        modifyUserNpInfoNpOut(tradeId, userId);
        // NpUserTagUpd42,ResRelaForNp40
    }

    /**
     * @Function: modifyUserNpInfoNpOut
     * @Description: 老系统完工节点CreateOutNpUserInfo
     * @param tradeId
     * @param userId
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月4日 下午2:34:51
     */
    public void modifyUserNpInfoNpOut(String tradeId, String userId) throws Exception
    {
        IDataset ids = TradeNpQry.getTradeNpByUserId(userId);
        IData param = new DataMap();

        if (IDataUtil.isNotEmpty(ids))
        {
            param.put("USER_ID", userId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "INS_NPHIS_FROM_NP", param);// 记录到历史表
            Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "DEL_BY_USERID", param); // 删除np信息
        }

       

        param.clear();
        param.put("USER_ID", ids.getData(0).getString("USER_ID"));
        param.put("NP_SERVICE_TYPE", ids.getData(0).getString("NP_SERVICE_TYPE"));
        param.put("SERIAL_NUMBER", ids.getData(0).getString("SERIAL_NUMBER"));
        param.put("PORT_OUT_NETID", ids.getData(0).getString("PORT_OUT_NETID"));
        param.put("PORT_IN_NETID", ids.getData(0).getString("PORT_IN_NETID"));
        param.put("HOME_NETID", ids.getData(0).getString("HOME_NETID"));
        param.put("B_NP_CARD_TYPE", ids.getData(0).getString("B_NP_CARD_TYPE"));
        param.put("A_NP_CARD_TYPE", ids.getData(0).getString("A_NP_CARD_TYPE"));
        param.put("NP_TAG", ids.getData(0).getString("HOME_NETID").substring(0, 3).equals("002") ? "4" : "8");
        param.put("APPLY_DATE", SysDateMgr.getSysTime());
        param.put("NP_DESTROY_TIME", "");
        param.put("PORT_IN_DATE", ids.getData(0).getString("PORT_IN_DATE"));
        param.put("REMARK", "携出生效");
        param.put("RSRV_STR1", "");
        param.put("RSRV_STR2", ids.getData(0).getString("RSRV_STR1"));// 携出申请时间
        param.put("RSRV_STR3", "");
        param.put("RSRV_STR4", "");
        param.put("RSRV_STR5", "");

        Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "INS_OUTNP_USER", param);
    }
}
