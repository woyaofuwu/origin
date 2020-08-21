
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class CreateNpUserFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        // 携转标志：0-非携转、1-携入未生效、2-携入被拒、3-已携入、4-携入已销、5-携出中、6-已携出、7-携出已销
        IDataset ids = TradeNpQry.getTradeNpByTradeId(tradeId);

        String np_tag = "1";
        if (IDataUtil.isNotEmpty(ids))
        {
            IData param = new DataMap();
            IData data = ids.getData(0);
            String home_netid = data.getString("HOME_NETID");
            if (StringUtils.isNotBlank(home_netid))
            {
                String str = home_netid.substring(0, 3);
                if ("002".equals(str))
                {
                    np_tag = "6";// 移动携回
                }
            }
            param.put("APPLY_DATE", SysDateMgr.getSysTime());
            param.put("NP_DESTROY_TIME", "");
            param.put("PORT_IN_DATE", SysDateMgr.getSysTime());
            param.put("PORT_OUT_DATE", SysDateMgr.END_TIME_FOREVER);
            param.put("NP_TAG", np_tag);

            param.put("USER_ID", data.getString("USER_ID"));
            param.put("NP_SERVICE_TYPE", data.getString("NP_SERVICE_TYPE"));
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            param.put("HOME_NETID", data.getString("HOME_NETID"));
            param.put("B_NP_CARD_TYPE", data.getString("B_NP_CARD_TYPE"));

            param.put("A_NP_CARD_TYPE", data.getString("A_NP_CARD_TYPE"));
            param.put("PORT_IN_NETID", data.getString("PORT_IN_NETID"));
            param.put("PORT_OUT_NETID", data.getString("PORT_OUT_NETID"));
            param.put("REMARK", data.getString("REMARK"));
            param.put("RSRV_STR1", data.getString("RSRV_STR1"));

            param.put("RSRV_STR2", data.getString("RSRV_STR2"));
            param.put("RSRV_STR3", data.getString("RSRV_STR3"));
            param.put("RSRV_STR4", data.getString("RSRV_STR4"));
            param.put("RSRV_STR5", data.getString("RSRV_STR5"));

            Dao.insert("TF_F_USER_NP", param);
        }
    }

}
