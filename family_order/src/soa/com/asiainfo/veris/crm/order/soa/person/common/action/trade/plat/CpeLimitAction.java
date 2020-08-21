
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
//
public class CpeLimitAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
		UcaData uca = btd.getRD().getUca();
		String brand_code = uca.getBrandCode();
		List<PlatSvcTradeData> platSvcTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        if (platSvcTradeDataList == null || platSvcTradeDataList.size() <= 0)
        {
            return;
        }

        int size = platSvcTradeDataList.size();
        for (int i = 0; i < size; i++)
        {
        	PlatSvcTradeData tradePlatsvc = platSvcTradeDataList.get(i);
            String modifyTag = tradePlatsvc.getModifyTag();
            if ((BofConst.MODIFY_TAG_ADD.equals(modifyTag)||BofConst.MODIFY_TAG_UPD.equals(modifyTag)) && (brand_code != null && brand_code.equals("CPE1")))
            {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码是CPE号码，不能办理平台业务!");
            }
        }
	}

}
