
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.AdcCamponSvrcodeInfoQry;

/**
 * TF_F_SVRCODECAMPON_ADC 逻辑处理，
 *
 * @author J2EE
 */
public class UpdCamponServCodeAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        if("49".equals(tradeTypeCode)){
            return;
        }

        String tradeId = mainTrade.getString("TRADE_ID");
        String intfId = mainTrade.getString("INTF_ID");

        boolean existsGrpSvc = StringUtils.indexOf(intfId, TradeTableEnum.TRADE_GRP_PLATSVC.getValue() + ",") > -1 ? true : false;

        if (existsGrpSvc) {
        	IDataset tradeGrpPlatSvcInfos = TradeGrpPlatSvcInfoQry.getTradeGrpPlatSvcByTradeId(tradeId);
        	for (int i = 0; i < tradeGrpPlatSvcInfos.size(); i++) {
        		String servCode = tradeGrpPlatSvcInfos.getData(i).getString("SERV_CODE");

        		IData data = new DataMap();;
        		data.put("SVR_CODE", servCode);

        		AdcCamponSvrcodeInfoQry.usedCamponServcode(data);
        	}
        }
    }
}
