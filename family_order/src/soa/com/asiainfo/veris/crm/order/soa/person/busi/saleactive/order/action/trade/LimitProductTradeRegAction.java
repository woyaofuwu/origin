package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveBookTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.BofHelper;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;


public class LimitProductTradeRegAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		/**
		 * 首先判断用户是否订购：
		 * 69908015  宽带1+（含预存款）
		 * 69908001  宽带1+活动
		 * 69908012  和家庭营销包
		 * 69908014  和家庭营销包(免预存款)
		 */
		String tradeTypeCode=btd.getMainTradeData().getTradeTypeCode();
		String orderTypeCode = btd.getRD().getOrderTypeCode();
		boolean isOrderSaleActive=false;
        boolean isMoveAndSaleActive = false;
		String subscribeProductId=null;
		
		if(tradeTypeCode.equals(SaleActiveConst.SALE_ACTIVE_TRADE_TYPE_BOOK)){
			List<SaleActiveBookTradeData> bookSaleActives=btd.get("TF_B_TRADE_SALEACTIVE_BOOK");
			if(bookSaleActives!=null&&bookSaleActives.size()>0){
				for(int i=0,size=bookSaleActives.size();i<size;i++){
					SaleActiveBookTradeData bookSaleActive=bookSaleActives.get(i);
					String productId=bookSaleActive.getProductId();

					if(bookSaleActive.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)
							&&(productId.equals("69908015")||
									productId.equals("69908001")||
									productId.equals("69908012")||
									productId.equals("69908014"))){
						
						subscribeProductId=productId;
						
						isOrderSaleActive=true;
						break;
						
					}
				}
			}
			
		}else if(tradeTypeCode.equals("240")){
			//增加特殊判断，移机同时办理预约营销活动，在预约转正式的时候，有可能存在当前未失效的营销活动
			//此处进行修改，转正式的时候，不判断生效的营销活动
			String serialNumber = btd.getMainTradeData().getSerialNumber();
			String wideSerialSn = "";
			if(serialNumber.startsWith("KD_")){
				wideSerialSn = serialNumber;
				serialNumber = serialNumber.substring(3);
	    	}else{
				wideSerialSn = "KD_" + serialNumber;
	    	}
			//查看是否有移机的营销工单
	    	IData inparams = new DataMap();
	    	inparams.put("SERIAL_NUMBER", wideSerialSn);
	    	inparams.put("TRADE_TYPE_CODE", "606");
	    	inparams.put("ACCEPT_MONTH", "");
	    	inparams.put("CANCEL_TAG", "0");
	        IDataset ExitMoveTrades = TradeInfoQry.queryTradeBySnAndTypeCode(inparams);
			//查看是否有预约的营销活动子订单
	        if(IDataUtil.isNotEmpty(ExitMoveTrades)){
		    	inparams.put("SERIAL_NUMBER", serialNumber);
		    	inparams.put("TRADE_TYPE_CODE", "230");
		        IDataset ExitPreSaleActiveTrades = TradeInfoQry.queryTradeBySnAndTypeCode(inparams);
		        if(IDataUtil.isNotEmpty(ExitPreSaleActiveTrades)){
		        	for(int i=0;i<ExitMoveTrades.size();i++){
		        		for(int j=0;j<ExitPreSaleActiveTrades.size();j++){
		        			if(ExitMoveTrades.getData(i).getString("ORDER_ID").equals(ExitPreSaleActiveTrades.getData(j).getString("ORDER_ID"))){
		        				isMoveAndSaleActive = true;
		        				break;
		        			}
		        		}
		        	}
		        }
	        }
			
			List<SaleActiveTradeData> saleActives=btd.get("TF_B_TRADE_SALE_ACTIVE");
			
			if(saleActives!=null&&saleActives.size()>0){
				for(int i=0,size=saleActives.size();i<size;i++){
					SaleActiveTradeData saleActive=saleActives.get(i);
					String productId=saleActive.getProductId();
					
					if(saleActive.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)
							&&(productId.equals("69908015")||
									productId.equals("69908001")||
									productId.equals("69908012")||
									productId.equals("69908014"))){
						subscribeProductId=productId;
						isOrderSaleActive=true;
						break;
						
					}
				}
			}
			
		}

		//没有订购指定的营销活动，直接退出
		if(!isOrderSaleActive||isMoveAndSaleActive||"606".equals(orderTypeCode)||"601".equals(orderTypeCode)){
			return;
		}
		
		//判断是否是预约的营销活动
		boolean isNotPreTrade = BofHelper.isNotPreTrade(btd.getRD());
		String userId=btd.getRD().getUca().getUserId();
				
		
		//如果不是预约的营销活动
		if(isNotPreTrade)
    	{
			boolean isCancelSaleActive=false;
			
			
			SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();
	        UcaData uca = saleActiveReqData.getUca();

	        String productId = saleActiveReqData.getProductId();
	        String packageId = saleActiveReqData.getPackageId();
	        String eparchyCode = uca.getUserEparchyCode();
	        
	        IDataset commparaDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3800", "SALELIMIT", packageId, productId, eparchyCode);
	        List<SaleActiveTradeData> userActiveList = uca.getUserSaleActives();
	        
	        String endAcctCycleOfThisMonth = SysDateMgr.getLastDateThisMonth();
	        userActiveList = SaleActiveUtil.filterActiesByEendDate(userActiveList, endAcctCycleOfThisMonth);
	        
	        //获取是否有即将要取消的营销活动
	        if (IDataUtil.isNotEmpty(commparaDataset) && CollectionUtils.isNotEmpty(userActiveList))
	        {
	        	userActiveList = SaleActiveUtil.sortUserSaleActivesByEndDateDesc(userActiveList);
	        	
	        	for (int index = 0, size = commparaDataset.size(); index < size; index++)
		        {
		            IData commparaData = commparaDataset.getData(index);

		            String paraCode3 = commparaData.getString("PARA_CODE3");
		            String paraCode4 = commparaData.getString("PARA_CODE4");

		            for (SaleActiveTradeData saleActiveTradeData : userActiveList)
		            {
		                String endPackageId = saleActiveTradeData.getPackageId();
		                String endProductId = saleActiveTradeData.getProductId();

		                if (!endPackageId.equals(paraCode3) || !endProductId.equals(paraCode4))
		                {
		                    continue;
		                }
		                
		                if(endProductId.equals("69908015")||
		                		endProductId.equals("69908001")||
		                		endProductId.equals("69908012")||
		                		endProductId.equals("69908014")){
		                	isCancelSaleActive=true;
			                break;
		                }
		            }
		        }
	        }
	        
	        
	        if(isCancelSaleActive){	//如果存在需要取消的营销活动
	        	//宽带1+
	    		if(subscribeProductId.equals("69908015")||subscribeProductId.equals("69908001")){
	    			verifyIsOrderProductsBook(userId, "69908012");
	    			verifyIsOrderProductsBook(userId, "69908014");
	    		}
	    		//和家庭营销包
	    		else if(subscribeProductId.equals("69908012")||subscribeProductId.equals("69908014")){
	    			verifyIsOrderProductsBook(userId, "69908015");
	    			verifyIsOrderProductsBook(userId, "69908001");
	    		}
	        	
	        }else{
	        	//宽带1+
	    		if(subscribeProductId.equals("69908015")||subscribeProductId.equals("69908001")){
	    			verifyIsOrderProducts(userId, "69908012");
	    			verifyIsOrderProducts(userId, "69908014");
	    		}
	    		//和家庭营销包
	    		else if(subscribeProductId.equals("69908012")||subscribeProductId.equals("69908014")){
	    			verifyIsOrderProducts(userId, "69908015");
	    			verifyIsOrderProducts(userId, "69908001");
	    		}
	        }
			
    	}else{
    		//宽带1+
    		if(subscribeProductId.equals("69908015")||subscribeProductId.equals("69908001")){
    			verifyIsOrderProducts(userId, "69908012");
    			verifyIsOrderProducts(userId, "69908014");
    		}
    		//和家庭营销包
    		else if(subscribeProductId.equals("69908012")||subscribeProductId.equals("69908014")){
    			verifyIsOrderProducts(userId, "69908015");
    			verifyIsOrderProducts(userId, "69908001");
    		}
    		
    	}
	}
	
	
	public void verifyIsOrderProducts(String userId, String productId)throws Exception{
		boolean isOrder=false;
		
		//验证是否订购了和家庭营销包
		IDataset set1=UserSaleActiveInfoQry.queryUserSaleActiveBookProdId(userId, productId);
		if(IDataUtil.isNotEmpty(set1)){
			isOrder=true;
			CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"用户已经预约订购了营销活动【"+
							set1.getData(0).getString("PRODUCT_NAME","")+"】无法继续办理此活动！");
		}
		if(!isOrder){
			IDataset set2=UserSaleActiveInfoQry.queryUserSaleActiveProdIdExcludeMonthEnd(userId, productId,"0");
			
			if(IDataUtil.isNotEmpty(set2)){
				CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"用户已经订购了营销活动【"+
						set2.getData(0).getString("PRODUCT_NAME","")+"】无法继续办理此活动！");
			}
		}
	}
	
	
	public void verifyIsOrderProductsBook(String userId, String productId)throws Exception{
		//验证是否订购了和家庭营销包
		IDataset set1=UserSaleActiveInfoQry.queryUserSaleActiveBookProdId(userId, productId);
		if(IDataUtil.isNotEmpty(set1)){
			CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"用户已经预约订购了营销活动【"+
							set1.getData(0).getString("PRODUCT_NAME","")+"】无法继续办理此活动！");
		}
		
	}
}
