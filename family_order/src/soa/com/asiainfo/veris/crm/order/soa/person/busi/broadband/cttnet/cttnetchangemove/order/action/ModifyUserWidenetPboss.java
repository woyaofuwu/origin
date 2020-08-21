
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePbossAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;

public class ModifyUserWidenetPboss implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        int inum = 0;
        // 获取PBOSS回单时的数据,更新宽带资料表
        String portNum = "";
        String roomName = "";
        String areaName = "";
        IDataset attrBufDataset = TradePbossAttrInfoQry.getTradePbossAttrByTrade(tradeId);
        for (int t = 0; t < attrBufDataset.size(); t++)
        {
            IData attrBuf = attrBufDataset.getData(t);
            String attrcode = attrBuf.getString("ATTR_CODE", "");
            String attrvalue = attrBuf.getString("ATTR_VALUE", "");
            if (StringUtils.equals(attrcode, "CF_PORT"))
            {// 端口号
                portNum = attrvalue;
            }
            else if (StringUtils.equals(attrcode, "CF_ROOM"))
            {// 机房号
                roomName = attrvalue;
            }
            else if (StringUtils.equals(attrcode, "CF_TTAREA"))
            {// 片区名称
                areaName = attrvalue;
            }
        }

        if (attrBufDataset.size() > 0)
        {
            inum = TradeWideNetInfoQry.updateTradeWidenetPboss(tradeId, portNum, roomName, areaName);
            if (inum < 1)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据PBOSS返单的数据更新宽带台帐表出错!");
            }
        }
    }

}
