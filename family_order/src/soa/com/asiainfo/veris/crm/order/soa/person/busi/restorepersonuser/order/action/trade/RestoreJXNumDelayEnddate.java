
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;


import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;   

/**
 * 吉祥号码复机对营销活动、优惠终止日期进行顺延
 * chenxy3 20160728
 */
public class RestoreJXNumDelayEnddate implements ITradeAction
{
	private static final Logger logger = Logger.getLogger(RestoreJXNumDelayEnddate.class);
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();  
        
        String destroyTradeId = "";// 获取最后销户的订单id
        int delayMon=0;  //需要延期的月份
        String destroyFinishYean="";//销户完工--年
    	String destroyAcceptMon="";//销户完工--月
        //获取最近的销户流水
        IDataset desDatInfos = TradeHistoryInfoQry.queryLastDestroyTradeByUserId(btd.getRD().getUca().getUserId());
        if (IDataUtil.isEmpty(desDatInfos) )
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：获取用户最后销户流水为空");
        }else{
        	destroyTradeId = desDatInfos.getData(0).getString("TRADE_ID","");
        	IDataset destroyTradeInfos=TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(destroyTradeId);
        	if(IDataUtil.isNotEmpty(destroyTradeInfos)){
	        	destroyFinishYean=destroyTradeInfos.getData(0).getString("FINISH_DATE").substring(0,4);//2016-7-28 10:27:36
	        	destroyAcceptMon=destroyTradeInfos.getData(0).getString("ACCEPT_MONTH");
	        	destroyAcceptMon=String.format("%02d", Integer.parseInt(destroyAcceptMon));
	        	//计算延期月份  当前年月 - 销户年月  -1 
	        	String sysYear=SysDateMgr.getSysDateYYYYMMDD().substring(0,4);
	        	String sysMon=SysDateMgr.getSysDateYYYYMMDD().substring(4,6);
	        	/**
	        	 * 《***计算规则***》：当前复机年月  - 销户年月  -1  + （原优惠终止日期）
	        	 * 如：原优惠终止20160731；  销户：201606； 复机：201612，  则  201612-201606-1=5，再加上20160731=20161231；
	        	 * 因为销户当月201606已经给钱，则还剩1个月前没给；复机当月201612也给钱了，原来差一个月钱没给则在201612复机的时候给了，所以在201612月就结束了
	        	 * delayMon算法不再-1，因为延期函数SysDateMgr.getAddMonthsLastDay(delayMon, activeEndDate)还要再+1
	        	 */
	        	delayMon=(Integer.parseInt(sysYear)-Integer.parseInt(destroyFinishYean))*12+ 
	        	(Integer.parseInt(sysMon)-Integer.parseInt(destroyAcceptMon));
        	}
        }
        if(destroyTradeId!=null && !"".equals(destroyTradeId) && !"null".equals(destroyTradeId) && !"NULL".equals(destroyTradeId)){
	        //查询全部的销户的备份优惠
	        IDataset dicnts=TradeDiscntInfoQry.getAllTradeBakDiscntByTradeId(destroyTradeId);
	        for(int k=0; k < dicnts.size(); k++){
	        	String prodId=dicnts.getData(k).getString("PRODUCT_ID","");
	        	
	        	if("69900703".equals(prodId)){
	        		String oldEndDate=dicnts.getData(k).getString("END_DATE","");//yyyy-MM-dd HH24:mi:ss
	        		int destroyYearMon=Integer.parseInt(destroyFinishYean+destroyAcceptMon);//销户完工年月 
	        		String oldEndMon=oldEndDate.substring(oldEndDate.indexOf("-")+1,oldEndDate.lastIndexOf("-"));
	        		String oldEndYearMon=oldEndDate.substring(0,4)+String.format("%02d", Integer.parseInt(oldEndMon));//原优惠终止年月
	        		if(Integer.parseInt(oldEndYearMon)-destroyYearMon>0 ){
	        			//只有销户时的年月，小于终止月份，才进行处理
	        			DiscntTradeData discntTradeData = new DiscntTradeData(dicnts.getData(k));
	            		discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
	            		if(delayMon>0 && !"2050".equals(oldEndDate.substring(0,4))){
	            			//延期
	            			discntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(delayMon, oldEndDate));
	            		}else{
	            			discntTradeData.setEndDate(oldEndDate);//不需要延期的就按原有的日期
	            		}
	                    btd.add(serialNumber, discntTradeData);
	        		} 
	        	}
	        } 
        }
        IDataset userSaleActives =SaleActiveInfoQry.getUserAllSaleActiveInfo(btd.getRD().getUca().getUserId(),"69900703");
        if(IDataUtil.isNotEmpty(userSaleActives)){
	        for(int j=0; j <1; j++){
	        	String prodId=userSaleActives.getData(j).getString("PRODUCT_ID","");//activeTradeData.getProductId();
	        	if("69900703".equals(prodId)){
	        		String activeEndDate=userSaleActives.getData(j).getString("END_DATE","");//activeTradeData.getEndDate();//原营销活动终止日期
	        		String endMod=activeEndDate.substring(activeEndDate.indexOf("-")+1,activeEndDate.lastIndexOf("-"));
	        		String activeEndYeanMon=activeEndDate.substring(0,4)+String.format("%02d", Integer.parseInt(endMod));//原营销活动终止年月
	        		int destroyYearMon=Integer.parseInt(destroyFinishYean+destroyAcceptMon);//销户完工年月 
	        		if(Integer.parseInt(activeEndYeanMon)-destroyYearMon>0 ){
	        			SaleActiveTradeData tradeActiveData=new SaleActiveTradeData(userSaleActives.getData(j));
	        			tradeActiveData.setModifyTag(BofConst.MODIFY_TAG_UPD);
	        			if(delayMon>0 && !"2050".equals(activeEndDate.substring(0,4))){
	            			//延期
	        				tradeActiveData.setEndDate(SysDateMgr.getAddMonthsLastDay(delayMon, activeEndDate));
	        				tradeActiveData.setRsrvDate2(SysDateMgr.getAddMonthsLastDay(delayMon, activeEndDate));
	            		}else{
	            			tradeActiveData.setEndDate(activeEndDate);//不需要延期的就按原有的日期
	            			tradeActiveData.setRsrvDate2(activeEndDate);
	            		}
	        			btd.add(serialNumber, tradeActiveData);
	        		}
	        	}
	        }
        }
    }
}
