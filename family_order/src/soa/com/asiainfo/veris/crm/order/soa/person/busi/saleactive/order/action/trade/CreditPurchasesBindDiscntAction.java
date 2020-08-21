package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;


/**
 * 信用购机活动受理页面办理活动绑定优惠
 */
public class CreditPurchasesBindDiscntAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String userId = btd.getMainTradeData().getUserId();
		
		SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
    	//对于可以办理信用购机的活动   1:勾选了信用购机.0:没勾选信用购机,正常购机活动
        if(!"1".equals(req.getCreditPurchases())){
        	return ;
        }
		
		String source = btd.getRD().getPageRequestData().getString("CREDIT_PURCHASES_SOURCE");//信用购机活动受理页面办理标识
		List<SaleActiveTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        SaleActiveTradeData saleActiveTD = list.get(0);

        //信用购机配置
        IDataset commparas=CommparaInfoQry.getCommNetInfo("CSM","3119",saleActiveTD.getProductId()) ;
		if(DataUtils.isEmpty(commparas)){
			return;
		}
		for(int i=0;i<commparas.size();i++){
			IData data=commparas.getData(i);
			if(StringUtils.isNotEmpty(data.getString("PARA_CODE4"))
				&&"1".equals(source)
			    &&data.getString("PARA_CODE4").equals(saleActiveTD.getPackageId())){
				
				String paraCode6 = data.getString("PARA_CODE6");
				if(StringUtils.isNotBlank(paraCode6)){
					DiscntTradeData newDiscnt = new DiscntTradeData();
		            newDiscnt.setUserId(userId);
		            newDiscnt.setProductId("-1");
		            newDiscnt.setPackageId("-1");
		            newDiscnt.setElementId(paraCode6);
		            newDiscnt.setInstId(SeqMgr.getInstId());
		            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
		            newDiscnt.setStartDate(SysDateMgr.firstDayOfMonth(1));
		            newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDay(25));
		            newDiscnt.setRemark("根据信用购机活动受理绑定优惠,配置类型为3119");
		            btd.add(btd.getMainTradeData().getSerialNumber(), newDiscnt);	
				}
				
				String paraCode7 = data.getString("PARA_CODE7");
				if(StringUtils.isNotBlank(paraCode7)){
					DiscntTradeData newDiscnt = new DiscntTradeData();
		            newDiscnt.setUserId(userId);
		            newDiscnt.setProductId("-1");
		            newDiscnt.setPackageId("-1");
		            newDiscnt.setElementId(paraCode7);
		            newDiscnt.setInstId(SeqMgr.getInstId());
		            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
		            newDiscnt.setStartDate(SysDateMgr.firstDayOfMonth(1));
		            newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDay(25));
		            newDiscnt.setRemark("根据信用购机活动受理绑定优惠,配置类型为3119");
		            btd.add(btd.getMainTradeData().getSerialNumber(), newDiscnt);	
				}
				
			}
		}
		
	}

}
