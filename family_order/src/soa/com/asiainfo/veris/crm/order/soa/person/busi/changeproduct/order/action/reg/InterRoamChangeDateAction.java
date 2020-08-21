package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class InterRoamChangeDateAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
	    IData data= btd.getRD().getPageRequestData();
	    String isRoamOpen = btd.getRD().getPageRequestData().getString("IS_ROAM_OPEN","");
        if(StringUtils.equals("true", isRoamOpen)){//如果是国漫开通平台业务，手动设置生效结束时间
            String validDate = data.getString("START_DATE","");
            String expireDate = data.getString("END_DATE","");
            List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
            for(int i=0;i<svcTradeDatas.size();i++){
                SvcTradeData svcTradeData=svcTradeDatas.get(i);
                if("19".equals(svcTradeData.getElementId())&&"0".equals(svcTradeData.getMainTag())){
                    if(!"".equals(validDate)){
                        svcTradeData.setStartDate(validDate);
                    }
                    if(!"".equals(expireDate)){
                        svcTradeData.setEndDate(expireDate);
                    }
                }
            }
            return;
        }
        String isRoamDiscnt= btd.getRD().getPageRequestData().getString("IS_ROAM_DISCNT","");
        if(StringUtils.equals("true", isRoamDiscnt)){//如果是国漫开通平台业务，手动设置生效结束时间
            String validDate = data.getString("START_DATE","");
            String expireDate = data.getString("END_DATE","");
            List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
            for(int i=0;i<discntTradeDatas.size();i++){
                DiscntTradeData discntTradeData=discntTradeDatas.get(i);
                IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2742",discntTradeData.getDiscntCode(),  CSBizBean.getVisit().getStaffEparchyCode());
                if(commparaSet!=null&&commparaSet.size()>0){
                    if(!"".equals(validDate)){
                        discntTradeData.setStartDate(validDate);
                    }
                    if(!"".equals(expireDate)){
                        discntTradeData.setEndDate(expireDate);
                    }
                    
                    String modify_tag = discntTradeData.getModifyTag();
                    if("0".equals(modify_tag)){
                    	 //addTableDeferTradeFee(btd);
                    }
                   
                }
            }
            return;
        }
	}

	

}
