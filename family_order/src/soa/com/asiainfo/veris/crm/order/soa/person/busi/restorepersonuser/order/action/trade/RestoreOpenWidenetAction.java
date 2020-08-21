
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;


import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;  
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;  
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry; 
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;  
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;

/**
 * 4.1.4手机号码缴费复机
 *  1）关联生成宽带报开工单；
	2）修改宽带用户的TF_F_USER表的 rsrv_tag2 为空；
	3）延长宽带营销活动的时间（修改TF_F_USER_DISCNT和TF_F_USER_SALEACTIVE并同步给账务）；
	4）延长营销活动对应存折的时间（调账务提供的接口）；
	5）时间处理说明： 
	比如手机号码 2017-01-05  欠费销号
	如果手机号码在 2017 年 2月份 缴费复机 ，则宽带营销活动不需要延期；
	如果手机号码在 2017 年 3月份 缴费复机 ，则宽带营销活动需要延期 1个月；
 */
public class RestoreOpenWidenetAction implements ITradeAction
{
	private static final Logger logger = Logger.getLogger(RestoreJXNumDelayEnddate.class);
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();  
        String userId=btd.getRD().getUca().getUserId();
        String tradeId=btd.getTradeId();
        boolean delayTag=false;
        logger.debug("*********手机号码缴费复机<>cxy<>*************serialNumber="+serialNumber);
        //System.out.println("*********手机号码缴费复机<>cxy<>*************serialNumber="+serialNumber);
        String destroyTradeId = "";// 获取最后销户的订单id
        int delayMon=0;  //需要延期的月份
        String destroyFinishYean="";//销户完工--年
    	String destroyAcceptMon="";//销户完工--月
    	/**
    	 * ************************3）延长宽带营销活动的时间（修改TF_F_USER_DISCNT和TF_F_USER_SALEACTIVE并同步给账务）；
    	 * */
        //获取最近的销户流水
        IDataset desDatInfos = TradeHistoryInfoQry.queryLastDestroyTradeByUserId(btd.getRD().getUca().getUserId());
        logger.debug("*********fuji<>cxy<>*************desDatInfos="+desDatInfos);
        //System.out.println("*********fuji<>cxy<>*************desDatInfos="+desDatInfos);
        if (IDataUtil.isEmpty(desDatInfos) )
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机：获取用户最后销户流水为空");
        }else{
        	//处理宽带，只有欠费销号的才处理宽带
        	String lastTradeTypeCode=desDatInfos.getData(0).getString("TRADE_TYPE_CODE",""); 
        	if(!"7240".equals(lastTradeTypeCode)){
        		return;
        	}
        	destroyTradeId = desDatInfos.getData(0).getString("TRADE_ID",""); 
        	IDataset destroyTradeInfos=TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(destroyTradeId);
        	logger.debug("*********fuji<>cxy<>*************destroyTradeId="+destroyTradeId+"*****destroyTradeInfos="+destroyTradeInfos);
        	//System.out.println("*********fuji<>cxy<>*************destroyTradeId="+destroyTradeId+"*****destroyTradeInfos="+destroyTradeInfos);
        	if(IDataUtil.isNotEmpty(destroyTradeInfos)){
        		String finishDate=destroyTradeInfos.getData(0).getString("FINISH_DATE");
	        	destroyFinishYean=finishDate.substring(0,4);//2016-7-28 10:27:36
	        	destroyAcceptMon=finishDate.substring(finishDate.indexOf("-")+1,finishDate.lastIndexOf("-"));
	        	destroyAcceptMon=String.format("%02d", Integer.parseInt(destroyAcceptMon));
	        	
	        	logger.debug("*********fuji<>cxy<>*************finishDate="+finishDate+"**destroyFinishYean="+destroyFinishYean+"**destroyAcceptMon="+destroyAcceptMon);
	        	//System.out.println("*********fuji<>cxy<>*************finishDate="+finishDate+"**destroyFinishYean="+destroyFinishYean+"**destroyAcceptMon="+destroyAcceptMon);
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
	        	logger.debug("*********fuji<>cxy<>*************delayMon="+delayMon);
	        	//System.out.println("*********fuji<>cxy<>*************delayMon="+delayMon);
        	}
        }
        
        //营销活动延期
        String discnts="";
        //List<SaleActiveTradeData> userSaleActives = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE); 
        IDataset userSaleActives =checkTradeSaleActiveBak(destroyTradeId);
        logger.debug("*********fuji<>cxy<>*************userSaleActives="+userSaleActives);
        //System.out.println("*********fuji<>cxy<>*************userSaleActives="+userSaleActives);
        if(userSaleActives!=null && userSaleActives.size()>0){
	        for(int j=0; j <userSaleActives.size(); j++){
	        	String prodId=userSaleActives.getData(j).getString("PRODUCT_ID","");//activeTradeData.getProductId();
	        	String packId=userSaleActives.getData(j).getString("PACKAGE_ID","");
	        	logger.debug("*********延期活动<>cxy<>*************prodId="+prodId+"****packId="+packId );
	        	//System.out.println("*********延期活动<>cxy<>*************prodId="+prodId+"****packId="+packId);
	        	IDataset saleinfos=getDiscntInfo(prodId,packId);//判断是不是宽带的营销活动
	        	if(saleinfos!=null && saleinfos.size()>0){
	        		String discntCode=saleinfos.getData(0).getString("PARAM_CODE");//取优惠
	        		discnts=discnts+"#"+discntCode;
	        		String sysdateYYYYMM=SysDateMgr.getSysDateYYYYMMDD().substring(0,6);
	        		String activeEndDate=userSaleActives.getData(j).getString("END_DATE","");//activeTradeData.getEndDate();//原营销活动终止日期
	        		String endMod=activeEndDate.substring(activeEndDate.indexOf("-")+1,activeEndDate.lastIndexOf("-"));
	        		String activeEndYeanMon=activeEndDate.substring(0,4)+String.format("%02d", Integer.parseInt(endMod));//原营销活动终止年月
	        		logger.debug("*********延期活动<>cxy<>*************activeEndYeanMon="+activeEndYeanMon );
	        		//System.out.println("*********延期活动<>cxy<>*************activeEndYeanMon="+activeEndYeanMon);
	        		if(Integer.parseInt(activeEndYeanMon)-Integer.parseInt(sysdateYYYYMM)<0 ){
	        			activeEndDate=SysDateMgr.getSysDate()+" 23:59:59";
	        			//如果营销活动已经过期，则要判断销户的时候，营销活动还有几个月，要延期的，是今天+剩余的月份
	        			int destroyYearMon=Integer.parseInt(destroyFinishYean+destroyAcceptMon);//销户完工年月 
	        			String endYear=activeEndYeanMon.substring(0,4);
	        			String endMon=activeEndYeanMon.substring(4);
	        			logger.debug("*********延期活动<>cxy<>*************destroyYearMon="+destroyYearMon );
	        			//System.out.println("*********延期活动<>cxy<>*************destroyYearMon="+destroyYearMon);
		        		if(Integer.parseInt(activeEndYeanMon)-destroyYearMon>0 ){
		        			delayMon=(Integer.parseInt(endYear)-Integer.parseInt(destroyFinishYean))*12+ 
		        		        	(Integer.parseInt(endMon)-Integer.parseInt(destroyAcceptMon));
		        		}
	        		}
	        		//如果终止日期仍然有效，则延长的月份为销户到今天的间隔月份	
	        		/**
	        		 * 这里要注意：如果直接new一个活动的工单，而原有的活动没有到期，则会有2笔一样的工单，所以这里要判断
	        		 * 是否在复机的时候已经一并将工单都生成了，如果存在，则在原有的工单进行日期延续。
	        		 * */
	        		logger.debug("*********延期活动<>cxy<>*************userSaleActives="+userSaleActives);
	        		//System.out.println("*********延期活动<>cxy<>*************userSaleActives="+userSaleActives);
        			//SaleActiveTradeData tradeActiveData=new SaleActiveTradeData(userSaleActives.getData(j));
	        		List<SaleActiveTradeData> userSaleActivesBtd = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE); 
	        		String ifExistBtd="0";
	        		for(int k=0; k<userSaleActivesBtd.size(); k++){
	        			SaleActiveTradeData userSaleActiveData=userSaleActivesBtd.get(k);
	        			String btd_prodId=userSaleActiveData.getProductId();
	        			String btd_packId=userSaleActiveData.getPackageId();
	        			if(btd_prodId.equals(btd_prodId) && packId.equals(btd_packId)){
	        				userSaleActiveData.setModifyTag(BofConst.MODIFY_TAG_UPD);
	        				if(delayMon>0 && !"2050".equals(activeEndDate.substring(0,4))){
		            			//延期
		        				String enddate=SysDateMgr.getAddMonthsLastDayNoEnv(delayMon, activeEndDate);
		        				userSaleActiveData.setEndDate(enddate);
		        				userSaleActiveData.setRsrvDate2(enddate);
		        				logger.debug("*********延期活动<>cxy<>*************enddate="+enddate);
		        				//System.out.println("*********延期活动<>cxy<>*************enddate="+enddate);
		            		}else{
		            			userSaleActiveData.setEndDate(activeEndDate);//不需要延期的就按原有的日期
		            			userSaleActiveData.setRsrvDate2(activeEndDate);
		            		}
	        				
	        				ifExistBtd="1";
	        			} 
	        		}
	        		//如果没有相同的宽带工单生成，则生成新的工单。如果有了，在原有的工单进行延期。
	        		if("0".equals(ifExistBtd)){
	        			SaleActiveTradeData tradeActiveData=new SaleActiveTradeData(userSaleActives.getData(j));
	        			tradeActiveData.setModifyTag(BofConst.MODIFY_TAG_UPD);
	        			if(delayMon>0 && !"2050".equals(activeEndDate.substring(0,4))){
	            			//延期
	        				String enddate=SysDateMgr.getAddMonthsLastDayNoEnv(delayMon, activeEndDate);
	        				tradeActiveData.setEndDate(enddate);
	        				tradeActiveData.setRsrvDate2(enddate);
	        				logger.debug("*********延期活动<>cxy<>*************enddate="+enddate);
	        				//System.out.println("*********延期活动<>cxy<>*************enddate="+enddate);
	            		}else{
	            			tradeActiveData.setEndDate(activeEndDate);//不需要延期的就按原有的日期
	            			tradeActiveData.setRsrvDate2(activeEndDate);
	            		}
	        			btd.add(serialNumber, tradeActiveData);
	        		}
        			
        			delayTag=true;	
	        		//}
	        		 /**
	                 * ************************延长营销活动对应存折的时间（调账务提供的接口）；
	            	 * */
	                if(delayTag){
	                	IDataset offerList=UpcCallIntf.queryOfferByOfferCodeAndOfferType(packId,"K");
	                	if(offerList!=null && offerList.size()>0){ 
	                		IDataset gifts=UpcCallIntf.qryOfferGiftsByParamOfferId(offerList.getData(0).getString("OFFER_ID",""));
	                		logger.debug("*********call 账务<>cxy<>*************gifts="+gifts);
	                		//System.out.println("*********call 账务<>cxy<>*************gifts="+gifts);
	                		if(gifts!=null && gifts.size()>0){ 
		        	        	IData param=new DataMap();
			        	        param.put("SERIAL_NUMBER", serialNumber);
			        	        param.put("USER_ID", userId);
			        	        param.put("OUTER_TRADE_ID", tradeId); //活动流水
			        	        param.put("ACTION_CODE", gifts.getData(0).getString("GIFT_OBJ_ID"));//营销活动编码
			        	        param.put("LATE_MONTHS", delayMon);//延迟营销活动存折月份数量
			        	        logger.debug("*********call 账务<>cxy<>*************param="+param);
			        	        //System.out.println("*********call 账务<>cxy<>*************param="+param);
			        	        IData callData=AcctCall.delayAccountTime(param);
			        	        logger.debug("*********call 账务<>cxy<>*************callData="+callData);
			        	        //System.out.println("*********call 账务<>cxy<>*************callData="+callData);
		        	        } 
	                	} 
	                }
	        	}
	        }
        }
        //优惠的延期
        if(destroyTradeId!=null && !"".equals(destroyTradeId) && !"null".equals(destroyTradeId) && !"NULL".equals(destroyTradeId)){
	        //查询全部的销户的备份优惠
	        IDataset dicnts=TradeDiscntInfoQry.getAllTradeBakDiscntByTradeId(destroyTradeId);
	        logger.debug("*********优惠的延期<>cxy<>*************dicnts="+dicnts);
	        //System.out.println("*********优惠的延期<>cxy<>*************dicnts="+dicnts);
	        for(int k=0; k < dicnts.size(); k++){
	        	String discntcode=dicnts.getData(k).getString("DISCNT_CODE","");
	        	//这种情况特殊，营销活动备份表没有记录，用户的营销活动还是有效的2050，导致上面的代码取不到值discnt串为空。
	        	if(discnts==null || "".equals(discnts)){
	        		IDataset wideInfo=getDiscntInfo2(discntcode);
	        		for(int q=0; q< wideInfo.size(); q++){
	        			discnts=discnts+"#"+discntcode;
	        		}
	        	}
	        	//与上面的营销活动对应的优惠就是宽带优惠
	        	if(discnts.indexOf(discntcode)>-1){
	        		String oldEndDate=dicnts.getData(k).getString("END_DATE","");//yyyy-MM-dd HH24:mi:ss 
	        		int destroyYearMon=Integer.parseInt(destroyFinishYean+destroyAcceptMon);//销户完工年月 
	        		logger.debug("*********优惠的延期<>cxy<>*************oldEndDate="+oldEndDate+"***destroyYearMon="+destroyYearMon);
	        		//System.out.println("*********优惠的延期<>cxy<>*************oldEndDate="+oldEndDate+"***destroyYearMon="+destroyYearMon);
	        		String oldEndMon=oldEndDate.substring(oldEndDate.indexOf("-")+1,oldEndDate.lastIndexOf("-"));
	        		String oldEndYearMon=oldEndDate.substring(0,4)+String.format("%02d", Integer.parseInt(oldEndMon));//原优惠终止年月
	        		
	        		String sysdateYYYYMM=SysDateMgr.getSysDateYYYYMMDD().substring(0,6); 
	        		if(Integer.parseInt(oldEndYearMon)-Integer.parseInt(sysdateYYYYMM)<0 ){
	        			oldEndDate=SysDateMgr.getSysDate()+" 23:59:59";
	        			//如果营销活动已经过期，则要判断销户的时候，营销活动还有几个月，要延期的，是今天+剩余的月份 
	        			String endYear=oldEndYearMon.substring(0,4);
	        			String endMon=oldEndYearMon.substring(4);
		        		if(Integer.parseInt(oldEndYearMon)-destroyYearMon>0 ){
		        			delayMon=(Integer.parseInt(endYear)-Integer.parseInt(destroyFinishYean))*12+ 
		        		        	(Integer.parseInt(endMon)-Integer.parseInt(destroyAcceptMon));
		        			logger.debug("*********优惠的延期<>cxy<>*************delayMon="+delayMon);
		        			//System.out.println("*********优惠的延期<>cxy<>*************delayMon="+delayMon);
		        		}
	        		}
	        		
	        		List<DiscntTradeData> userDiscntBtd = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT); 
	        		String ifExistBtd1="0";
	        		for(int h=0; h<userDiscntBtd.size(); h++){
	        			DiscntTradeData userDiscnt=userDiscntBtd.get(h);
	        			String discntbtd=userDiscnt.getDiscntCode();
	        			if(discntcode.equals(discntbtd)){
	        				userDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
		            		if(delayMon>0 && !"2050".equals(oldEndDate.substring(0,4))){
		            			//延期
		            			String endDate=SysDateMgr.getAddMonthsLastDayNoEnv(delayMon, oldEndDate);
		            			userDiscnt.setEndDate(endDate);
		            			logger.debug("*********优惠的延期<>cxy<>*************endDate="+endDate);
		            			//System.out.println("*********优惠的延期<>cxy<>*************endDate="+endDate);
		            		}else{
		            			userDiscnt.setEndDate(oldEndDate);//不需要延期的就按原有的日期
		            		}
	        				ifExistBtd1="1";
	        			}
	        		}
	        		//循环(查询全部的销户的备份优惠)处理
	        		if("0".equals(ifExistBtd1)){
	        			DiscntTradeData discntTradeData = new DiscntTradeData(dicnts.getData(k));
	            		discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
	            		if(delayMon>0 && !"2050".equals(oldEndDate.substring(0,4))){
	            			//延期
	            			String endDate=SysDateMgr.getAddMonthsLastDayNoEnv(delayMon, oldEndDate);
	            			discntTradeData.setEndDate(endDate);
	            			logger.debug("*********优惠的延期<>cxy<>*************endDate="+endDate);
	            			//System.out.println("*********优惠的延期<>cxy<>*************endDate="+endDate);
	            		}else{
	            			discntTradeData.setEndDate(oldEndDate);//不需要延期的就按原有的日期
	            		}
	                    btd.add(serialNumber, discntTradeData);
	        		}
                    delayTag=true;
	        	}else{
	        		/**
	        		 * BUG20180704153028_手机销户再复机宽带数据问题优化
	        		 * <br/>
	        		 * 处理手机号码的保底优惠
	        		 */
	        		if("5908".equals(discntcode) || "5906".equals(discntcode)){
	        			restoreDiscnt(k, dicnts.getData(k), discntcode, serialNumber, btd);
	        		}
	        		/******************BUG20180704153028_手机销户再复机宽带数据问题优化_end*******************************/
	        	}
	        }
    		/**
    		 * BUG20180704153028_手机销户再复机宽带数据问题优化
    		 * <br/>
    		 * 恢复47UU关系表的信息
    		 */
	        restoreRela(destroyTradeId, serialNumber, btd);
        }  
        /**
         * ************************关联生成宽带报开工单；
    	 * ************************这里不处理了，action类CreateWidenetTradeAction.java已经一并处理
    	 * */
        
        String kd_sn="";
        if(!serialNumber.startsWith("KD_")){
        	kd_sn="KD_"+serialNumber;
        }else{
        	kd_sn=serialNumber;
        } 
        
        
        IDataset kdusers=checkWideUserExist(kd_sn);
        if(kdusers!=null && kdusers.size()>0){
        	String kd_userId=kdusers.getData(0).getString("USER_ID");
        	IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(kd_userId);
        	String tradeTypeCode="7306";// GPON宽带缴费开机
        	if(IDataUtil.isNotEmpty(widenetInfos)){
        		String widenetType = widenetInfos.getData(0).getString("RSRV_STR2"); 
                if ("2".equals(widenetType))
                {

                    tradeTypeCode = "7307";// ADSL宽带缴费开机

                }
                else if ("3".equals(widenetType))
                {

                    tradeTypeCode = "7308";// 光纤宽带缴费开机 

                } 
        	}
        	
	        IData data = new DataMap();
	        data.put("SERIAL_NUMBER", kd_sn);
	        data.put("TRADE_TYPE_CODE", tradeTypeCode);
	        CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", data);
        }
        //删除20170731宽带减免套餐
//        List<DiscntTradeData> list = btd.getRD().getUca().getUserDiscnts();
//        for (int i = 0, size = list.size(); i < size; i++)
//        {
//        	DiscntTradeData discntData=list.get(i);
//        	String discntCode=discntData.getDiscntCode();
//        	if("20170731".equals(discntCode)){ 
//        		DiscntTradeData discntTradeData = discntData.clone(); 
//				discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL); 
//				btd.add(serialNumber, discntTradeData);
//        	}
//        }
    }
	
	
	
	
	//取营销活动
	public static IDataset getUserSaleActiveInfo(String userId) throws Exception{
		IData inparams=new DataMap();
		inparams.put("USER_ID", userId);
		SQLParser parser = new SQLParser(inparams); 
		parser.addSQL(" select distinct t.product_id,t.package_id ");
		parser.addSQL(" FROM TF_F_USER_SALE_ACTIVE       ");
		parser.addSQL(" WHERE USER_ID= :USER_ID   ");
		parser.addSQL(" AND PARTITION_ID = MOD(:USER_ID,10000)   ");
		parser.addSQL(" AND PROCESS_TAG <>3   ");
		return Dao.qryByParse(parser,Route.CONN_CRM_CEN);										 
	}
	//根据产品和包取优惠
	public static IDataset getDiscntInfo(String productId,String packageId) throws Exception{
		IData inparams=new DataMap();
		inparams.put("PRODUCT_ID", productId);
		inparams.put("PACKAGE_ID", packageId);
		SQLParser parser = new SQLParser(inparams); 
	    parser.addSQL(" select t.* from td_s_commpara t   ");
	    parser.addSQL(" where t.subsys_code='CSM'  ");
	    parser.addSQL(" AND T.PARAM_ATTR='7113'  ");
	    parser.addSQL(" AND T.PARA_CODE1=:PACKAGE_ID  ");
	    parser.addSQL(" AND T.PARA_CODE2=:PRODUCT_ID  ");
	    parser.addSQL(" AND SYSDATE < T.END_DATE  ");
	    parser.addSQL(" and T.PARA_CODE2 in (select a.param_code from td_s_commpara a where a.param_attr='7114') "); 
	    return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
	
	//根据产品和包取优惠
		public static IDataset getDiscntInfo2(String discntCode) throws Exception{
			IData inparams=new DataMap();
			inparams.put("DISCNT_CODE", discntCode); 
			SQLParser parser = new SQLParser(inparams); 
		    parser.addSQL(" select t.* from td_s_commpara t   ");
		    parser.addSQL(" where t.subsys_code='CSM'  ");
		    parser.addSQL(" AND T.PARAM_ATTR='7113'  ");
		    parser.addSQL(" AND T.PARAM_CODE=:DISCNT_CODE  ");
		    parser.addSQL(" AND SYSDATE < T.END_DATE  ");
		    parser.addSQL(" and T.PARA_CODE2 in (select a.param_code from td_s_commpara a where a.param_attr='7114') "); 
		    return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
		}
	
	/**
	 * 查询用户下是否存在宽带
	 * */
	public static IDataset checkWideUserExist(String kd_sn) throws Exception
    {
		IData inparams=new DataMap();
		inparams.put("KD_SERIAL_NUMBER", kd_sn);
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.* from tf_F_user t ");
        parser.addSQL(" where t.serial_number=:KD_SERIAL_NUMBER  "); 
        parser.addSQL(" and t.remove_tag='0' "); 
        parser.addSQL(" and t.rsrv_tag2='1' "); 
    	return Dao.qryByParse(parser);
    }
	
	
	/**
	 * 查询用户备份的营销活动
	 * */
	public static IDataset checkTradeSaleActiveBak(String tradeId) throws Exception
    {
		IData inparams=new DataMap();
		inparams.put("TRADE_ID", tradeId);
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.* from TF_B_TRADE_SALE_ACTIVE_BAK t ");
        parser.addSQL(" where t.trade_id=:TRADE_ID  "); 
        parser.addSQL(" and  t.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");  
    	return Dao.qryByParse(parser);
    }
	
	/**
	 * 恢复手机号码的优惠
	 * @param k
	 * @param dicnt
	 * @param discntcode
	 * @param serialNumber
	 * @param btd
	 * @throws Exception
	 * @author zhuoyingzhi
	 * @date 20180801
	 */
	public void restoreDiscnt(int k,IData dicnt,String discntcode,String serialNumber,BusiTradeData btd)throws Exception{
		String oldEndDate=dicnt.getString("END_DATE","");//yyyy-MM-dd HH24:mi:ss 
		//判断当前是否已经办理了该优惠
		List<DiscntTradeData> userDiscntBtd = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT); 
		String ifExistBtd1="0";
		for(int h=0; h<userDiscntBtd.size(); h++){
			DiscntTradeData userDiscnt=userDiscntBtd.get(h);
			String discntbtd=userDiscnt.getDiscntCode();
			if(discntcode.equals(discntbtd)){
				    userDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
        			userDiscnt.setEndDate(oldEndDate);//不需要延期的就按原有的日期
				 ifExistBtd1="1";
			}
		}
		//如果没有则新增一条
		if("0".equals(ifExistBtd1)){
			DiscntTradeData discntTradeData = new DiscntTradeData(dicnt);
							discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
			btd.add(serialNumber, discntTradeData);
		}
		System.out.println("RestoreOpenWidenetAction--restoreDiscnt--ifExistBtd1:"+ifExistBtd1);
	}
	
	/**
	 * 恢复47的uu关系表
	 * @param destroyTradeId
	 * @param serialNumber
	 * @param btd
	 * @throws Exception
	 * @author zhuoyingzhi
	 * @date 20180801
	 */
	public void restoreRela(String destroyTradeId,String serialNumber,BusiTradeData btd)throws Exception{
		//查询关系表的备用信息
		IDataset  relaInfoList=TradeRelaInfoQry.getAllTradeBakUURelaByTradeId(destroyTradeId);
		//System.out.println("RestoreOpenWidenetAction--restoreRela--relaInfoList:"+relaInfoList);
		if(IDataUtil.isNotEmpty(relaInfoList)){
			for(int i=0;i<relaInfoList.size();i++){
				String  relaType=relaInfoList.getData(i).getString("RELATION_TYPE_CODE","");
				if("47".equals(relaType)){
					RelationTradeData  relaInfo=new RelationTradeData(relaInfoList.getData(i));
					   relaInfo.setModifyTag(BofConst.MODIFY_TAG_UPD);
					   btd.add(serialNumber, relaInfo);
				}
			}
		}
	}
	
}
