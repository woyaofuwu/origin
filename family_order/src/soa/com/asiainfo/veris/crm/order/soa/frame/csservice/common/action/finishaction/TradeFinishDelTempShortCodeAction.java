
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

public class TradeFinishDelTempShortCodeAction implements ITradeFinishAction
{
    private final static Logger logger = Logger.getLogger(TradeFinishDelTempShortCodeAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
        String grpUserId = mainTrade.getString("USER_ID_B");
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String shortCode = "";
        if ("3034".equals(tradeTypeCode) || "3035".equals(tradeTypeCode)) // 集团VPMN成员新增和成员变更
        {
            IDataset resDataset = TradeResInfoQry.getTradeResByTradeIdAndModify(tradeId, "0");
            if (IDataUtil.isNotEmpty(resDataset))
            {
                shortCode = resDataset.getData(0).getString("RES_CODE");
            }
        }
        if (StringUtils.isNotBlank(grpUserId) && StringUtils.isNotBlank(shortCode))
        {
            IDataset uuInfos = RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(grpUserId, "40");
            if (IDataUtil.isNotEmpty(uuInfos))
            {
                grpUserId = uuInfos.getData(0).getString("USER_ID_A");
            }
            IData param = new DataMap();
            param.put("USER_ID_A", grpUserId);
            param.put("SHORT_CODE", shortCode);
            boolean bool = Dao.delete("TF_F_TEMPORARY_SHORTCODE", param, new String[]
            { "USER_ID_A", "SHORT_CODE" }, Route.getCrmDefaultDb());

        }

    }
}
