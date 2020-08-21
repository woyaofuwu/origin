package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class ChangeProductSyncDiscntAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		/**
		 * step.1 查询当前台账产品数据
		 */
		List<ProductTradeData> changeProducts=btd.get("TF_B_TRADE_PRODUCT"); 
		
		String newProductId=null;
    	String oldProductId=null;
    	List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
    	List<OfferRelTradeData> offerRelTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
		for(int i=0,size=changeProducts.size();i<size;i++){
			ProductTradeData data=changeProducts.get(i);
			if(data.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
				newProductId=data.getProductId();
    			if(data.getOldProductId()!=null&&!data.getOldProductId().equals("")){
    				oldProductId=data.getOldProductId();
    			}
    			break;
			}
		}
		
		/**
		 * step.2 查询旧产品是否为9227,9228配置中的产品
		 */
		IDataset commparaInfo9227 = null;
		IDataset commparaInfo9228 = null;
		IDataset commparaInfo9287 = null;
		IDataset commparaInfo9230 = null;
		if(StringUtils.isNotBlank(oldProductId)) {
			commparaInfo9227 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9227",oldProductId, btd.getRD().getUca().getUserEparchyCode());
			commparaInfo9228 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9228",oldProductId, btd.getRD().getUca().getUserEparchyCode());
			commparaInfo9287 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9287",oldProductId, btd.getRD().getUca().getUserEparchyCode());
			commparaInfo9230 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9230",oldProductId, btd.getRD().getUca().getUserEparchyCode());
		}
        String discntCode = "";
        String endFlag = "";
        boolean relOfferFlag = false;
		if (commparaInfo9227!=null && commparaInfo9227.size()>0)
        {
			for(int i=0;i<commparaInfo9227.size();i++){
				discntCode=commparaInfo9227.getData(i).getString("PARA_CODE1");//para_code1=后台绑定优惠
				endFlag = commparaInfo9227.getData(i).getString("PARA_CODE14","");
				relOfferFlag = true;
				if(!"2".equals(endFlag))
				{
					endDiscn(discntCode,btd,discntTradeDatas,relOfferFlag,offerRelTradeDatas,endFlag,"");
				}
				
			}
        }else if(commparaInfo9228!=null && commparaInfo9228.size()>0) {
        	for(int j=0;j<commparaInfo9228.size();j++){
        		discntCode=commparaInfo9228.getData(j).getString("PARA_CODE1");//para_code1=后台绑定优惠
        		endFlag = commparaInfo9228.getData(j).getString("PARA_CODE14","");
        		String smsContent = commparaInfo9228.getData(j).getString("PARA_CODE23","");
        		relOfferFlag = true;
        		if(!"2".equals(endFlag))
				{
					endDiscn(discntCode,btd,discntTradeDatas,relOfferFlag,offerRelTradeDatas,endFlag,smsContent);
				}
        	}
        }else if(commparaInfo9287!=null && commparaInfo9287.size()>0) {
        	for(int j=0;j<commparaInfo9287.size();j++){
        		discntCode=commparaInfo9287.getData(j).getString("PARA_CODE1");//para_code1=后台绑定优惠
        		endFlag = commparaInfo9287.getData(j).getString("PARA_CODE14","");
        		relOfferFlag = true;
        		if(!"2".equals(endFlag))
				{
					endDiscn(discntCode,btd,discntTradeDatas,relOfferFlag,offerRelTradeDatas,endFlag,"");
				}
        	}
        }else if(commparaInfo9230!=null && commparaInfo9230.size()>0) {
        	for(int j=0;j<commparaInfo9230.size();j++){
        		discntCode=commparaInfo9230.getData(j).getString("PARA_CODE1");//para_code1=后台绑定优惠
        		endFlag = commparaInfo9230.getData(j).getString("PARA_CODE14","");
        		relOfferFlag = true;
        		if(!"2".equals(endFlag))
				{
					endDiscn(discntCode,btd,discntTradeDatas,relOfferFlag,offerRelTradeDatas,endFlag,"");
				}
        	}
        }
		
	}
	
	public void endDiscn(String discntCode,BusiTradeData btd,List<DiscntTradeData> discntTradeDatas,boolean relOfferFlag,List<OfferRelTradeData> offerRelTradeDatas,String endFlag, String smsContent)throws Exception{
		//若旧产品非配置中的产品，则结束action
				if(!"".equals(discntCode)) {
					/**
					 * step.3 若旧产品符合条件，则查询绑定的优惠是否在生效期内
					 */
					UcaData uca=btd.getRD().getUca();
					String userId=uca.getUserId();
					String discntInsId = "";
					String serialNumber = btd.getRD().getUca().getSerialNumber();
					IDataset userDiscs=UserDiscntInfoQry.getAllDiscntByUser(userId,discntCode);
					String endDate = SysDateMgr.getAddMonthsLastDay(1);
					
					if("0".equals(endFlag))
					{
						endDate = SysDateMgr.getSysDate();
					}else if ("1".equals(endFlag)) {
						endDate = SysDateMgr.getAddMonthsLastDay(1);
					}
					
					
					
					if(userDiscs!=null && userDiscs.size()>0){
						/**
						 * step.4 取消该生效优惠(台账中有该优惠信息)
						 */
						boolean flag = true;
						
						for(int i = 0 ; i < discntTradeDatas.size() ; i++ ) {
							DiscntTradeData discntData = discntTradeDatas.get(i);
							if(discntCode.equals(discntData.getDiscntCode())) {
								discntData.setEndDate(endDate);
								discntData.setModifyTag(BofConst.MODIFY_TAG_DEL);
								discntData.setRemark("配置关联取消-台账中有该优惠信息");
								
								flag = false;
								discntInsId = discntData.getInstId();
								break;
							}
						}
						/**
						 * step.4 取消该生效优惠(台账中无该优惠信息)
						 */
						if(flag) {
							IData oldDis = userDiscs.getData(0);
							DiscntTradeData newDiscnt = new DiscntTradeData(oldDis);
				            newDiscnt.setEndDate(endDate);
				            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);  
				            
				            newDiscnt.setRemark("配置关联取消-台账中无该优惠信息");
				            
				            btd.add(uca.getSerialNumber(), newDiscnt);
				            discntInsId = newDiscnt.getInstId();
						}
						
						 //REQ201902010004  新增18元流量王卡新入网政策  @tanzheng start
				        if(StringUtils.isNotBlank(smsContent)){
				        	String date = endDate + SysDateMgr.END_DATE;
				        	Date tempDate = SysDateMgr.string2Date(date, SysDateMgr.PATTERN_STAND);
				        	String chinaDate = SysDateMgr.date2String(tempDate, SysDateMgr.PATTERN_CHINA_TIME);
				        	smsContent = smsContent.replace("DATE_TIME", chinaDate);
				        	
				        	IData ObjectsmsData = new DataMap(); // 短信数据
			                ObjectsmsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
			                ObjectsmsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
			                ObjectsmsData.put("FORCE_OBJECT", "10086");// 发送对象
			                ObjectsmsData.put("RECV_OBJECT", serialNumber);// 接收对象
			                ObjectsmsData.put("NOTICE_CONTENT", smsContent);// 短信内容
			                PerSmsAction.insTradeSMS(btd, ObjectsmsData);
				        }
				        //REQ201902010004  新增18元流量王卡新入网政策  @tanzheng end
					}
					/**
					 * step.5 9228中的商品更改offer_rel表
					 */
					if(relOfferFlag) {
						if(!"".equals(discntInsId)) {
							/**
							 * 台账中有数据
							 */
							boolean flag = true;
							for(int i = 0 ; i < offerRelTradeDatas.size() ; i++ ) {
								OfferRelTradeData offerRelData = offerRelTradeDatas.get(i);
								if(discntCode.equals(offerRelData.getRelOfferCode())) {
									offerRelData.setEndDate(endDate);
									offerRelData.setModifyTag(BofConst.MODIFY_TAG_DEL);
									offerRelData.setRemark("配置关联取消-台账中有数据");
									
									flag = false;
									break;
								}
							}
							
							/**
							 * 台账中无数据
							 */
							if(flag) {
								IDataset userOfferRels = UserOfferRelInfoQry.qryUserAllOfferRelByUserIdDiscntCode(userId, discntCode, discntInsId);
								if(userOfferRels!=null && userOfferRels.size()>0) {
									IData userOfferRel = userOfferRels.getData(0);
									OfferRelTradeData newOfferRel = new OfferRelTradeData(userOfferRel);
									newOfferRel.setEndDate(endDate);
									newOfferRel.setModifyTag(BofConst.MODIFY_TAG_DEL);
									newOfferRel.setRemark("配置关联取消-台账中无数据");
									btd.add(uca.getSerialNumber(), newOfferRel);
								}
							}
						}
					}
					
				}
	}
}
