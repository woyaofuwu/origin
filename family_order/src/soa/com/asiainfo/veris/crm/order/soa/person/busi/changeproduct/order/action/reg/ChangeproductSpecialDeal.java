package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class ChangeproductSpecialDeal implements ITradeAction{
	private static final Logger log = Logger.getLogger(ChangeproductSpecialDeal.class); 

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		
		List<ProductTradeData> changeProducts=btd.get("TF_B_TRADE_PRODUCT"); 
		BaseChangeProductReqData request = (BaseChangeProductReqData) btd.getRD();
		
		
		if(changeProducts!=null&&changeProducts.size()>0){
 			
			String newProductId=null;
        	String oldProductId=null;
			String newStarDate = "";
			for(int i=0,size=changeProducts.size();i<size;i++){
				
				ProductTradeData data=changeProducts.get(i);
				
				if(data.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
					newProductId=data.getProductId();
					newStarDate = data.getStartDate();
        			if(data.getOldProductId()!=null&&!data.getOldProductId().equals("")){
        				oldProductId=data.getOldProductId();
        			}
        			
        			break;
					
				}
				
			}
			
			
			//核对是否满足特殊判断
        	if(newProductId!=null&&!newProductId.equals("")&&
        			oldProductId!=null&&!oldProductId.equals("")){
        		
        		UcaData uca=btd.getRD().getUca();
				String userId=uca.getUserId();
				String serialNumber=uca.getSerialNumber();
                
                
              //1、要求commpara=9287存在，且要求用户的不存在该优惠（包括本次办理及已有的）        
                IDataset commparaInfostow = CommparaInfoQry.getCommparaAllColByParser("CSM", "9287",newProductId, btd.getRD().getUca().getUserEparchyCode());
                if (commparaInfostow!=null && commparaInfostow.size()>0)
                {
                	
                	String discntCode=commparaInfostow.getData(0).getString("PARA_CODE1");//para_code1=后台绑定优惠
                	String continuous=commparaInfostow.getData(0).getString("PARA_CODE2","");//para_code2=绑定期限(数字代表几个月，null则到2050）
                	String effTime=commparaInfostow.getData(0).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效
                	
                	String discntNew="";//本次新办的该种优惠 
                	
                	boolean flag=true;//允许办理条件
                	
                	//2、本次办理的优惠如果存在该优惠，则不再绑定。
                	List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
                	for (DiscntTradeData discntTradeData : discntTradeDatas)
                    {
                        // 判断R类型优惠 拼入attr
                        if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
                        {
                            discntNew = discntTradeData.getElementId();
                            if(discntNew.equals(discntCode)){
                            	flag=false;
                            	break;
                            }
                        }
                    }
                	//3、要求该用户原来没有有效的该优惠
                	if(flag){
	                	IDataset userDiscs=UserDiscntInfoQry.getAllDiscntByUser_2(userId,discntCode);
	                	if(IDataUtil.isNotEmpty(userDiscs)){
	                		flag=false;
	                	}else{
	                		IDataset tradeDiscs=TradeDiscntInfoQry.queryCountByUidDiscntCode2(userId,discntCode);
	                		if(IDataUtil.isNotEmpty(tradeDiscs)){
		                		flag=false;
		                	}
	                	}
                	}                	                	
                	           	
                	String openMon=newStarDate.substring(0,7);
                	String sysMon=SysDateMgr.getSysDate().substring(0,7);

                	if(sysMon.equals(openMon)){
                			flag=false;
                	}
                	
                	if(flag){
        	        	String startDate="";
        	        	String endData="";
        	        	if("0".equals(effTime)){
        	        		startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
        	        		if(!"".equals(continuous) && !"null".equals(continuous)){
        	            		endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous));//绑定期限(数字代表几个月，null则到2050）
        	            	}else{
        	            		endData= SysDateMgr.END_DATE_FOREVER;
        	            	}
        	        	}else{
        	        		startDate=SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
        	        		if(!"".equals(continuous) && !"null".equals(continuous)){
        	            		endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous)+1);//绑定期限(数字代表几个月，null则到2050）
        	            	}else{
        	            		endData= SysDateMgr.END_DATE_FOREVER;
        	            	}
        	        	}
        	        	/*IData discntData=new DataMap();
        	            discntData.put("USER_ID", userId);
        	            discntData.put("PRODUCT_ID", "-1");
        	            discntData.put("PACKAGE_ID", "-1");
        	            discntData.put("DISCNT_CODE", discntCode);
        	            discntData.put("START_DATE", startDate);
        	            discntData.put("END_DATE", endData);
        	            dealForDiscnt(btd, uca, discntData); *///BUG20180208171355
        	            
        	            DiscntTradeData newDiscnt = new DiscntTradeData();
        	            newDiscnt.setUserId(userId);
        	            newDiscnt.setProductId("-1");
        	            newDiscnt.setPackageId("-1");
        	            newDiscnt.setElementId(discntCode);
        	            newDiscnt.setInstId(SeqMgr.getInstId());
        	            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        	            newDiscnt.setStartDate(startDate);
        	            newDiscnt.setEndDate(endData);
        	            newDiscnt.setRemark("根据业务类型及主产品后台绑定优惠");
        	            btd.add(uca.getSerialNumber(), newDiscnt);
        	            
                	}
                } 
        		
        	}

		}
		
	}
	
	// 准备优惠台帐数据 
    private void dealForDiscnt(BusiTradeData btd, UcaData uca, IData param) throws Exception
    {

        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(param.getString("USER_ID"));
        newDiscnt.setProductId(param.getString("PRODUCT_ID"));
        newDiscnt.setPackageId(param.getString("PACKAGE_ID"));
        newDiscnt.setElementId(param.getString("DISCNT_CODE"));
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        newDiscnt.setStartDate(param.getString("START_DATE"));
        newDiscnt.setEndDate(param.getString("END_DATE"));
        newDiscnt.setRemark("根据业务类型及主产品后台绑定优惠");
        btd.add(uca.getSerialNumber(), newDiscnt);
        
        List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        
        if(productTrade != null && productTrade.size() > 0)
        {
            for(ProductTradeData product : productTrade)
            {
                if(BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()) && "1".equals(product.getMainTag()))
                {
                	//此处prodId与packId为-1，需要手动新增OfferRel
                    OfferRelTradeData offerRel = new OfferRelTradeData();
            		offerRel.setInstId(SeqMgr.getInstId());
            		offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
            		offerRel.setOfferInsId(product.getInstId());
            		offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            		offerRel.setOfferCode(product.getProductId());
            		offerRel.setUserId(product.getUserId());
            		offerRel.setRelOfferInsId(newDiscnt.getInstId());
            		offerRel.setRelOfferCode(newDiscnt.getElementId());
            		offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
            		offerRel.setRelType(BofConst.OFFER_REL_TYPE_LINK);//连带关系
            		offerRel.setStartDate(newDiscnt.getStartDate());
            		offerRel.setEndDate(newDiscnt.getEndDate());
            		offerRel.setRelUserId(newDiscnt.getUserId());
            		offerRel.setGroupId("-1");
            		btd.add(uca.getSerialNumber(), offerRel);
                }
            }
        }
    }
	
    private void removeOfferRel(DiscntTradeData discnt, BusiTradeData btd, UcaData uca, String newProductId) throws Exception{
    	List<OfferRelTradeData> offerRels = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
    	for(OfferRelTradeData offerRel : offerRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && offerRel.getOfferCode().equals(newProductId) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			offerRels.remove(offerRel);
    			break;
    		}
    	}
    	
    	List<OfferRelTradeData> userOfferRels = uca.getOfferRelsByRelUserId();
    	for(OfferRelTradeData offerRel : userOfferRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && offerRel.getOfferCode().equals(newProductId) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			userOfferRels.remove(offerRel);
    			break;
    		}
    	}
    }
    
    private void resetOfferRelDate(DiscntTradeData discnt, BusiTradeData btd, UcaData uca, String productId, boolean isUpdStartDate) throws Exception{
    	List<OfferRelTradeData> offerRels = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
    	for(OfferRelTradeData offerRel : offerRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && offerRel.getOfferCode().equals(productId) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			if(isUpdStartDate)
    				offerRel.setStartDate(discnt.getStartDate());
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    	
    	List<OfferRelTradeData> userOfferRels = uca.getOfferRelsByRelUserId();
    	for(OfferRelTradeData offerRel : userOfferRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && offerRel.getOfferCode().equals(productId) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			if(isUpdStartDate)
    				offerRel.setStartDate(discnt.getStartDate());
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    }
}
