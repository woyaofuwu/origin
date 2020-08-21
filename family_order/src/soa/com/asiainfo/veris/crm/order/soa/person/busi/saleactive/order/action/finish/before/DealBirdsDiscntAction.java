package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.before;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;

public class DealBirdsDiscntAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		//System.out.println("---------------DealBirdsDiscntAction--------------"+btd.getRD().getPreType());
		if (btd.getRD().getPreType().equals(BofConst.PRE_TYPE_CHECK))
		{
			return;
		}

	    //1.如果用户不存saleactive子台账记录，则退出
		List<SaleActiveTradeData> saleActvieTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
		if (ArrayUtil.isEmpty(saleActvieTradeData))
		{
			return;
		}
		String productId = saleActvieTradeData.get(0).getProductId();
		String modifyTag = saleActvieTradeData.get(0).getModifyTag();
		//System.out.println("=============DealBirdsDiscntAction=============modifyTag"+modifyTag+",productId:"+productId);
		//2.判断用户saleactive子台账中是否存在新增的宽带1+、宽带包年活动
		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && ("69908001".equals(productId) || "67220428".equals(productId)))
		{
			
			//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	        String discntCodeList="84013241,84013242,84014054,84003439,84003843,84003842,84003039,84003040,84071446,84071456";
	        List<DiscntTradeData> discntData5 = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList);
	        //System.out.println("==============DealBirdsDiscntAction==========discntData5:"+discntData5);
	        if (ArrayUtil.isNotEmpty(discntData5)){
	        	for (int i =0; i < discntData5.size(); i++){
		        	DiscntTradeData delDiscntTD5 = discntData5.get(i).clone();
	
		        	delDiscntTD5.setEndDate(SysDateMgr.getLastDateThisMonth());
		        	delDiscntTD5.setModifyTag(BofConst.MODIFY_TAG_DEL);
		        	delDiscntTD5.setRemark("度假宽带变更为宽带1+或宽带包年时，终止度假宽带相关套餐！");
		            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD5);
	        	}
	            
	        }
	        //add by zhangxing3 for 候鸟月、季、半年套餐（海南）

		}
		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && ("66000602".equals(productId)|| "66002202".equals(productId))){
			//查询用户是否存在候鸟按天计费套餐  ，如果存在终止。
			String discntCodeList = "84003039,84003040,84013242";

	        List<DiscntTradeData> discntData6 = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList);    
	        //System.out.println("==============DealBirdsDiscntAction==========discntData6:"+discntData6);
	        if (ArrayUtil.isNotEmpty(discntData6)){
	        	for (int i =0; i < discntData6.size(); i++){
		        	DiscntTradeData delDiscntTD6 = discntData6.get(i).clone();	
		        	String discntCode = delDiscntTD6.getDiscntCode();
			        delDiscntTD6.setEndDate(SysDateMgr.getSysTime());
		        	delDiscntTD6.setModifyTag(BofConst.MODIFY_TAG_DEL);
		        	delDiscntTD6.setRemark("办理度假活动时，终止按天计费套餐！");
		            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD6);
	        	}
	        }
		}
        //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "66004809".equals(productId))
		{
			//查询用户是否存在候鸟按天计费套餐  ，如果存在终止。
			String discntCodeList = "84003039,84003040,84013242,84013241,84014054,84003439,84003843";

	        List<DiscntTradeData> discntData7 = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeList);    
	        //System.out.println("==============DealBirdsDiscntAction==========discntData6:"+discntData6);
	        if (ArrayUtil.isNotEmpty(discntData7)){
	        	for (int i =0; i < discntData7.size(); i++){
		        	DiscntTradeData delDiscntTD7 = discntData7.get(i).clone();	
		        	String discntCode = delDiscntTD7.getDiscntCode();
		        	delDiscntTD7.setEndDate(SysDateMgr.getLastDateThisMonth());
		        	delDiscntTD7.setModifyTag(BofConst.MODIFY_TAG_DEL);
		        	delDiscntTD7.setRemark("办理度假活动时，终止原度假活动相关套餐！");
		            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD7);
	        	}
	        }
		}
		else
		{
			return;
		}       
	}

}
