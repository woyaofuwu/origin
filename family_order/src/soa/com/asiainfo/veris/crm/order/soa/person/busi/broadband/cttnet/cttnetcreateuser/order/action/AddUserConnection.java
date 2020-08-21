
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;

/**
 * 开户时modem方式选择的是租用，则给绑定modem优惠
 */
public class AddUserConnection implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeWidenetInfos = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
        if (IDataUtil.isNotEmpty(tradeWidenetInfos))
        {
            for (int i = 0; i < tradeWidenetInfos.size(); i++)
            {
                IData tradeDatainfo = tradeWidenetInfos.getData(i);
                String cttPhone = tradeDatainfo.getString("RSRV_STR5");
                if (StringUtils.isNotBlank(cttPhone))
                {
                    IData insertData = new DataMap();
                    insertData.put("USER_ID_A", tradeDatainfo.getString("USER_ID"));
                    insertData.put("USER_ID_B", cttPhone);
                    insertData.put("TYPE", "BK");
                    insertData.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    insertData.put("UPDATE_STAFF_ID", tradeDatainfo.getString("UPDATE_STAFF_ID"));
                    insertData.put("UPDATE_DEPART_ID", tradeDatainfo.getString("UPDATE_DEPART_ID"));
                    insertData.put("REMARK", "宽带与固话共线关系");
                    insertData.put("RSRV_STR1", tradeDatainfo.getString("TRADE_ID"));
                    Dao.insert("TF_F_USER_CONNECTION", insertData);

                }
            }
        }

    }

}
