
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.reg;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;

/**
 * Copyright: Copyright 2014 Asiainfo
 * 
 * @ClassName: DealBirdsDiscntAction.java
 * @Description: 处理无手机宽带候鸟套餐action
 * @version: v1.0.0
 */
public class DealBirdsDiscntAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        
		List<DiscntTradeData> discntTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if (ArrayUtil.isNotEmpty(discntTradeData)){
			for (int i =0; i < discntTradeData.size(); i++){
				String discntCode = discntTradeData.get(i).getDiscntCode();
		        String modifyTag = discntTradeData.get(i).getModifyTag();
		        String startDate = discntTradeData.get(i).getStartDate();
		        String endDate = discntTradeData.get(i).getEndDate();

		        //1.无手机宽带产品变更：候鸟->包年，终止候鸟相关套餐
		        if("681".equals(tradeTypeCode))
		        {
					if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && ("84014240".equals(discntCode) || "84014241".equals(discntCode)
							|| "84014242".equals(discntCode) ) )
					{					
				        String discntCodeList="84014054,84003843";
				        List<DiscntTradeData> discntData = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList);
				        //System.out.println("==============DealBirdsDiscntAction==========discntData1:"+discntData);
				        if (ArrayUtil.isNotEmpty(discntData)){
				        	for (int j =0; j < discntData.size(); j++){
					        	DiscntTradeData delDiscntTD = discntData.get(j).clone();
					        	
					        	delDiscntTD.setEndDate(endDate);
					        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
					        	delDiscntTD.setRemark("无手机宽带度假变更为包年时，终止度假套餐！");
					        	//System.out.println("==============DealBirdsDiscntAction==========delDiscntTD:"+delDiscntTD);
					            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
				        	}
				            
				        }
				        
					}
					if( BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "84071448".equals(discntCode) || "84071449".equals(discntCode))
					{
						String discntCodeList="84071457,84071447";
				        List<DiscntTradeData> discntData = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList);
				        //System.out.println("==============DealBirdsDiscntAction==========discntData1:"+discntData);
				        if (ArrayUtil.isNotEmpty(discntData)){
				        	for (int j =0; j < discntData.size(); j++){
					        	DiscntTradeData delDiscntTD = discntData.get(j).clone();
					        	String endDateStr = SysDateMgr.getAddMonthsLastDayNoEnv(0,SysDateMgr.getSysDate());
					        	delDiscntTD.setEndDate(endDateStr);
					        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
					        	delDiscntTD.setRemark("无手机宽带度假宽带2019变更为包年时，终止度假套餐！");
					        	//System.out.println("==============DealBirdsDiscntAction==========delDiscntTD:"+delDiscntTD);
					            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
				        	}
				            
				        }
					}
		        
		        }
				//2.无手机宽带开户：办理候鸟套餐时，绑定候鸟相关套餐
				if( ("680".equals(tradeTypeCode) || "681".equals(tradeTypeCode)))
				{					
			        if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && ("84014240".equals(discntCode) || "84014241".equals(discntCode)
						|| "84014242".equals(discntCode)))
			        {
						DiscntTradeData newDiscnt = new DiscntTradeData();
				        newDiscnt.setUserId(btd.getRD().getUca().getUserId());
				        newDiscnt.setProductId("-1");
				        newDiscnt.setPackageId("-1");
				        newDiscnt.setElementId("84003843");
				        newDiscnt.setInstId(SeqMgr.getInstId());
				        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
				        newDiscnt.setStartDate(startDate);
				        newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDayNoEnv(12, startDate));
				        newDiscnt.setRemark("无手机宽带开户办理候鸟套餐时后台绑定优惠");
				        //System.out.println("==============DealBirdsDiscntAction==========newDiscnt:"+newDiscnt);
	
				        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
				        
				        DiscntTradeData newDiscnt1 = new DiscntTradeData();
				        newDiscnt1.setUserId(btd.getRD().getUca().getUserId());
				        newDiscnt1.setProductId("-1");
				        newDiscnt1.setPackageId("-1");
				        newDiscnt1.setElementId("84014054");
				        newDiscnt1.setInstId(SeqMgr.getInstId());
				        newDiscnt1.setModifyTag(BofConst.MODIFY_TAG_ADD); 
				        newDiscnt1.setStartDate(startDate);
				        newDiscnt1.setEndDate(SysDateMgr.END_DATE_FOREVER);
				        newDiscnt1.setRemark("无手机宽带开户办理候鸟套餐时后台绑定优惠");
				        //System.out.println("==============DealBirdsDiscntAction==========newDiscnt1:"+newDiscnt1);
				        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt1);
				        
				        DiscntTradeData newDiscnt2 = new DiscntTradeData();
				        newDiscnt2.setUserId(btd.getRD().getUca().getUserId());
				        newDiscnt2.setProductId("-1");
				        newDiscnt2.setPackageId("-1");
				        newDiscnt2.setElementId("84018442");
				        newDiscnt2.setInstId(SeqMgr.getInstId());
				        newDiscnt2.setModifyTag(BofConst.MODIFY_TAG_ADD); 
				        newDiscnt2.setStartDate(startDate);
				        newDiscnt2.setEndDate(SysDateMgr.END_DATE_FOREVER);
				        newDiscnt2.setRemark("无手机宽带开户办理候鸟套餐时后台绑定优惠");
				        
				        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt2);
			        }
			        if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && ("84071448".equals(discntCode) || "84071449".equals(discntCode)))
			        {
			        	DiscntTradeData newDiscnt = new DiscntTradeData();
				        newDiscnt.setUserId(btd.getRD().getUca().getUserId());
				        newDiscnt.setProductId("-1");
				        newDiscnt.setPackageId("-1");
				        newDiscnt.setElementId("84071457");
				        newDiscnt.setInstId(SeqMgr.getInstId());
				        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
				        newDiscnt.setStartDate(startDate);
				        newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDayNoEnv(12, startDate));
				        newDiscnt.setRemark("无手机宽带办理度假宽带2019，绑定减免宽带停机保号费套餐！");
				        //System.out.println("==============DealBirdsDiscntAction==========newDiscnt:"+newDiscnt);
	
				        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
				        
				        
			        }
				}
				//3.无手机宽带续约：办理候鸟套餐时，绑定候鸟相关套餐
				if ("682".equals(tradeTypeCode))
				{
					if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && ("84014240".equals(discntCode) || "84014241".equals(discntCode)
							|| "84014242".equals(discntCode)))
					{					
						String discntCodeList="84003843";
				        List<DiscntTradeData> discntData = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList);
				        //System.out.println("==============DealBirdsDiscntAction==========discntData2:"+discntData);
				        if (ArrayUtil.isNotEmpty(discntData)){
				        	for (int j =0; j < discntData.size(); j++){
					        	DiscntTradeData delDiscntTD = discntData.get(j).clone();
				
					        	delDiscntTD.setEndDate(SysDateMgr.getAddMonthsLastDayNoEnv(12, startDate));
					        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
					        	delDiscntTD.setRemark("无手机宽带续约,办理候鸟套餐时，更新相关候鸟套餐结束时间！");
					            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
				        	}
				            
				        }
					}
					
					if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && ("84071448".equals(discntCode) || "84071449".equals(discntCode)
							|| "84074442".equals(discntCode)))
					{					
						String discntCodeList="84071457";
				        List<DiscntTradeData> discntData = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList);
				        //System.out.println("==============DealBirdsDiscntAction==========discntData2:"+discntData);
				        if (ArrayUtil.isNotEmpty(discntData)){
				        	for (int j =0; j < discntData.size(); j++){
					        	DiscntTradeData updDiscntTD = discntData.get(j).clone();
				
					        	updDiscntTD.setEndDate(SysDateMgr.getAddMonthsLastDayNoEnv(12, startDate));
					        	updDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
					        	updDiscntTD.setRemark("无手机宽带续约,办理度假宽带2019时，更新相关度假宽带套餐结束时间！");
					            btd.add(btd.getRD().getUca().getSerialNumber(), updDiscntTD);
				        	}
				            
				        }
				        
				        String discntCodeList2="84071447";
				        List<DiscntTradeData> discntData2 = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList2);
				        //System.out.println("==============DealBirdsDiscntAction==========discntData1:"+discntData);
				        if (ArrayUtil.isNotEmpty(discntData2)){
				        	for (int j =0; j < discntData2.size(); j++){
					        	DiscntTradeData delDiscntTD = discntData2.get(j).clone();
					        	String endDateStr = SysDateMgr.getAddMonthsLastDayNoEnv(0,SysDateMgr.getSysDate());
					        	delDiscntTD.setEndDate(endDateStr);
					        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
					        	delDiscntTD.setRemark("无手机宽带办理度假宽带2019，如存在度假宽带40元月功能费（无手机），则终止到本月底！");
					        	//System.out.println("==============DealBirdsDiscntAction==========delDiscntTD:"+delDiscntTD);
					            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
				        	}
				            
				        }
					}
				}
			}
		}
		
    }
}
