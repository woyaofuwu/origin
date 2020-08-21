package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;



import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


/**
 * 根据活动包平台服务元素绑定优惠
 */
public class PlatSvcBindDiscntAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		UcaData uca = btd.getRD().getUca();
		String userId=uca.getUserId();
		List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		IDataset commparaInfos2509 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","2509","SALEACTIVE",null);//根据服务绑定减免优惠套餐
		if(pstds!=null&&pstds.size()>0){
			for(PlatSvcTradeData pstData:pstds){
				//IData platSvcTrade = platSvcTrades.getData(i);
				String modify = pstData.getModifyTag();
				String serviceId = pstData.getElementId(); 
				if(BofConst.MODIFY_TAG_ADD.equals(modify)){
					for(int j=0;j<commparaInfos2509.size();j++){
						if(serviceId.equals(commparaInfos2509.getData(j).getString("PARA_CODE4"))){
							String discntCode = commparaInfos2509.getData(j).getString("PARA_CODE5");
							int months = Integer.parseInt(commparaInfos2509.getData(j).getString("PARA_CODE6","1"));//优惠偏移时间（单位月）
							if(StringUtils.isNotBlank(discntCode)){
								DiscntTradeData newDiscnt = new DiscntTradeData();
					            newDiscnt.setUserId(userId);
					            newDiscnt.setProductId("-1");
					            newDiscnt.setPackageId("-1");
					            newDiscnt.setElementId(discntCode);
					            newDiscnt.setInstId(SeqMgr.getInstId());
					            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
					            newDiscnt.setStartDate(SysDateMgr.getSysTime());
					            newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDay(months,SysDateMgr.getSysTime()));
					            newDiscnt.setRemark("根据配置后台绑定优惠,配置类型为2509");
					            btd.add(uca.getSerialNumber(), newDiscnt);	
					            break;
							}
							
						}
					}
				}
			}
        }
	 }	
}
