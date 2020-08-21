
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;



/**
 * TD二代开户当月首次变更立即生效
 * @author tz
 *
 */
public class OpenFirstMonthChangeStartAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(OpenFirstMonthChangeStartAction.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	logger.debug(">>>>>>>>>>>>>>>>>>>OpenFirstMonthChangeStartAction execute start!>>>>>>>>>>>");
    	UcaData uca =btd.getRD().getUca();
    	String userId = uca.getUserId();
        String serialNumber = uca.getSerialNumber();
		String brandCode = uca.getBrandCode();
		if(!"TDYD".equals(brandCode)){
			logger.debug(serialNumber+"品牌"+brandCode);
			return ;
		}
		String startDate = SysDateMgr.getSysDate();
		String openMonth = uca.getUser().getOpenDate().substring(0,7);
		String currentMonth = startDate.substring(0,7);
		//如果开户日期和产品生效时间不在同一个月则直接返回
		if(!openMonth.equals(currentMonth)){
			logger.debug(serialNumber+"openMonth"+openMonth);
			return;
		}
		String acceptDate = SysDateMgr.getAddMonthsLastDayNoEnv(-1,SysDateMgr.getSysTime());

		String month = SysDateMgr.getCurMonth();
    	IDataset tradeData110 = TradeHistoryInfoQry.queryHisTradeAfterAcceptDateAndMonth(userId,"110",acceptDate,month);
    	IDataset tradeData3803 = TradeHistoryInfoQry.queryHisTradeAfterAcceptDateAndMonth(userId,"3803",acceptDate,month);
    	//如果用户当月已经有110或者3803的工单就直接返回
    	if(IDataUtil.isNotEmpty(tradeData110)||IDataUtil.isNotEmpty(tradeData3803)){
    		logger.debug(serialNumber+"当月已经办理过产品变更");
    		return ;
    	}
		
		

		List<ProductTradeData> changeProducts=btd.get("TF_B_TRADE_PRODUCT"); 
		List<DiscntTradeData> changeDiscnts=btd.get("TF_B_TRADE_DISCNT"); 
		
		String relStartDate = SysDateMgr.getSysTime();
		String relEndData = SysDateMgr.addSecond(relStartDate, -1);
		
		if(changeProducts!=null&&changeProducts.size()>0){
			 for(ProductTradeData proData : changeProducts){
				 if(BofConst.MODIFY_TAG_ADD.equals(proData.getModifyTag())){
					 proData.setStartDate(relStartDate);
				 }else if(BofConst.MODIFY_TAG_DEL.equals(proData.getModifyTag())){
					 proData.setEndDate(relEndData);
				 }
			 }
		}
		
		if(changeDiscnts!=null&&changeDiscnts.size()>0){
			 for(DiscntTradeData discntData : changeDiscnts){
				 if(BofConst.MODIFY_TAG_ADD.equals(discntData.getModifyTag())){
					 discntData.setStartDate(relStartDate);
				 }else if(BofConst.MODIFY_TAG_DEL.equals(discntData.getModifyTag())){
					 discntData.setEndDate(relEndData);
				 }
			 }
		}
        
    }
}
