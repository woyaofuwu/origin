package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.reg;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class NoPhoneWideUserHaiGsRegAction implements ITradeAction {
    protected static Logger log = Logger.getLogger(NoPhoneWideUserHaiGsRegAction.class);

    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        List discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if (discntTradeDatas == null || discntTradeDatas.size() == 0){
            return;
        }
        String tradeTypeCode = btd.getTradeTypeCode();
        String eparchyCode = btd.getRD().getUca().getUserEparchyCode();
        if(StringUtils.equals("680", tradeTypeCode) ||StringUtils.equals("681", tradeTypeCode)){
        	for(int i=0;i<discntTradeDatas.size();i++){
        		DiscntTradeData discntTradeData = (DiscntTradeData) discntTradeDatas.get(i);
        		String startDate = discntTradeData.getStartDate();
        		IDataset comparaList = CommparaInfoQry.getCommpara("CSM","3579", discntTradeData.getDiscntCode(),eparchyCode);
                if (IDataUtil.isNotEmpty(comparaList) && SysDateMgr.getIntDayByDate(startDate) >= 25){
                	startDate=SysDateMgr.getFirstDayOfNextMonth(startDate)+" 00:00:00";
                    discntTradeData.setStartDate(startDate);
                }
        	}
        }

    }
}
