package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class ChangeproductSuperCardSpecialDeal implements ITradeAction{
	private static final Logger log = Logger.getLogger(ChangeproductSuperCardSpecialDeal.class); 

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		
		List<ProductTradeData> changeProducts=btd.get("TF_B_TRADE_PRODUCT"); 
		BaseChangeProductReqData request = (BaseChangeProductReqData) btd.getRD();
		boolean booktag = request.isBookingTag(); 
		/**
		 * BUG20160803095440超享卡旧版换新版问题
		 * chenxy3 2016-08-11 
		 * */
//		//log.info("("**cxy***changeProducts=["+changeProducts);
		if(changeProducts!=null&&changeProducts.size()>0){ 
			String newProdId=null;
        	String oldProdId=null;
        	for(int i=0,size=changeProducts.size();i<size;i++){
				
				ProductTradeData data=changeProducts.get(i);
				UcaData uca=btd.getRD().getUca();
				String userId=uca.getUserId();
				String startDate=data.getStartDate();//预约时间，新产品的开始时间就是预约的时间。
				if(data.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
					newProdId=data.getProductId(); 
        			if(data.getOldProductId()!=null&&!data.getOldProductId().equals("")){
        				oldProdId=data.getOldProductId();
        			} 
        			//旧35元超享卡换新30元
        			if("10003370".equals(oldProdId)&&"10003373".equals(newProdId)){
	        			List<DiscntTradeData> discDatas=btd.get("TF_B_TRADE_DISCNT");
	        			for(int k=0; k< discDatas.size(); k++){
	        				DiscntTradeData discntData=discDatas.get(k); 
	        				if("3371".equals(discntData.getDiscntCode()) && discntData.getModifyTag().equals(BofConst.MODIFY_TAG_INHERIT)){
	        					//删除原有优惠3371
	        					//discntData.setProductId(oldProdId);
	        					//discntData.setPackageId("10003370");
	        					discntData.setElementId(discntData.getDiscntCode());
	        					//需要增加处理关系数据
	        					
	        					/**
	        					 * 预约的情况：
	        					 * 1、如果预约那个月的不是1号，则终止到预约日期的当月底
	        					 * 2、如果预约的那个月的1号，则终止到上月底
	        					 * */  
	        					if(booktag){ 
	        						String firstDate=SysDateMgr.addMonths(SysDateMgr.getDateNextMonthFirstDay(startDate), -1);
	        						String lastDate="";
	        						if(firstDate.equals(startDate)){
	        							lastDate=SysDateMgr.addSecond(firstDate, -1);
	        						}else{
	        							lastDate=SysDateMgr.addSecond(SysDateMgr.getDateNextMonthFirstDay(startDate), -1);
	        						} 
//	        						//log.info("("**cxy***startDate="+startDate+"$$$$预约月份的第一天=*"+firstDate+"###取预约月份的最后一天日期=="+lastDate);
	        						discntData.setEndDate(lastDate) ;//到预约当月的月底
	        					}else{
	        						discntData.setEndDate(SysDateMgr.getLastDateThisMonth());//到月底
	        					}
	        					discntData.setModifyTag(BofConst.MODIFY_TAG_UPD);
	        					this.removeOfferRel(discntData, btd, uca, newProdId);
	        					this.resetOfferRelDate(discntData, btd, uca, oldProdId, false);
	    						
	    						//新增当前优惠3371
	    						IData param=new DataMap();
	    						param.put("USER_ID" ,userId);
	    						param.put("PRODUCT_ID",newProdId);
	    						param.put("PACKAGE_ID","10003373");
	    						param.put("DISCNT_CODE",discntData.getDiscntCode());  
	    						if(booktag){
	    							param.put("START_DATE",startDate);//预约的以新的时间开始
	        					}else{
	        						param.put("START_DATE",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        					}
	    						param.put("END_DATE",SysDateMgr.END_DATE_FOREVER);
	    						dealForDiscnt(btd,uca, param);
	    						break;
	        				}	
	        			} 
            		} 
            		//旧55元换新50元
            		if("10003372".equals(oldProdId)&&"10003374".equals(newProdId)) {
            			List<DiscntTradeData> discDatas=btd.get("TF_B_TRADE_DISCNT");
	        			for(int k=0; k< discDatas.size(); k++){
	        				DiscntTradeData discntData=discDatas.get(k);
	        				if("3373".equals(discntData.getDiscntCode()) && discntData.getModifyTag().equals(BofConst.MODIFY_TAG_INHERIT)){
	        					//删除原有优惠3373
	        					//discntData.setProductId(oldProdId);
	        					//discntData.setPackageId("10003372");
	        					discntData.setElementId(discntData.getDiscntCode());
	    						if(booktag){
	    							String firstDate=SysDateMgr.addMonths(SysDateMgr.getDateNextMonthFirstDay(startDate), -1);
	        						String lastDate="";
	        						if(firstDate.equals(startDate)){
	        							lastDate=SysDateMgr.addSecond(firstDate, -1);
	        						}else{
	        							lastDate=SysDateMgr.addSecond(SysDateMgr.getDateNextMonthFirstDay(startDate), -1);
	        						} 
	        						discntData.setEndDate(lastDate) ;//到预约当月的月底
	        					}else{
	        						discntData.setEndDate(SysDateMgr.getLastDateThisMonth());//到月底
	        					}
	        					discntData.setModifyTag(BofConst.MODIFY_TAG_UPD);  
	        					this.removeOfferRel(discntData, btd, uca, newProdId);
	        					this.resetOfferRelDate(discntData, btd, uca, oldProdId, false);
	    						
	    						//新增当前优惠3373
	    						IData param=new DataMap();
	    						param.put("USER_ID" ,userId);
	    						param.put("PRODUCT_ID",newProdId);
	    						param.put("PACKAGE_ID","10003374");
	    						param.put("DISCNT_CODE",discntData.getDiscntCode());  
	    						if(booktag){
	    							param.put("START_DATE",startDate);//预约的以新的时间开始
	        					}else{
	        						param.put("START_DATE",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        					}
	    						param.put("END_DATE",SysDateMgr.END_DATE_FOREVER);
	    						dealForDiscnt(btd,uca, param);
	    						break;
	        				}	
	        			} 	
            			
        			}
        			
				} 
			}
		}
		
		
		if(booktag){
			return;
		} 
		if(changeProducts!=null&&changeProducts.size()>0){
 			
			String newProductId=null;
        	String oldProductId=null;
			
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
			
			
			//核对是否满足特殊判断
        	if(newProductId!=null&&!newProductId.equals("")&&
        			oldProductId!=null&&!oldProductId.equals("")){
        		IDataset checkDatas=CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3737", "CHAOXIANG_PROD_FEE_DISCNT",
        				newProductId, oldProductId, "ZZZZ");
        		
        		UcaData uca=btd.getRD().getUca();
				String userId=uca.getUserId();
				String serialNumber=uca.getSerialNumber();
        		//如何核对的数据不为空
        		if(IDataUtil.isNotEmpty(checkDatas)){
        			String discntCode=checkDatas.getData(0).getString("PARA_CODE3","");
        			
        			if(discntCode!=null&&!discntCode.equals("")){
        				List<DiscntTradeData> discntDatas=btd.get("TF_B_TRADE_DISCNT");
        				
        				if(discntDatas!=null&&discntDatas.size()>0){
        					
        					
        					
        					
        					//核对用户是否已经存在了此优惠
        					IDataset haveDiscntDatas=UserDiscntInfoQry.getUserByDiscntCode(userId, discntCode);
        					if(IDataUtil.isNotEmpty(haveDiscntDatas)){
        						for(int i=0,size=discntDatas.size();i<size;i++){
            						DiscntTradeData discntData=discntDatas.get(i);
            						
            						/*
            						 * 如果是新增的首免套餐，就修改套餐的生失效时间
            						 */
            						if(discntData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)&&
            								discntData.getElementId().equals(discntCode)){
            							
            							discntData.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            							discntData.setEndDate(SysDateMgr.getLastDateThisMonth());
            							this.resetOfferRelDate(discntData, btd, uca, newProductId, true);
            							break;
            						}
            						
            					}
        						
        						/*
        						 * 删除掉原来已经存在的首免套餐
        						 */
        						for(int j=0,sizej=haveDiscntDatas.size();j<sizej;j++){
        							IData delFeeDiscntData=haveDiscntDatas.getData(j);
        							
        							DiscntTradeData delFeeDiscntTradeData=new DiscntTradeData(delFeeDiscntData);
        							delFeeDiscntTradeData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        							delFeeDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        							
        							btd.add(serialNumber, delFeeDiscntTradeData);
        						}
        						
        					}

        				}
        				
        			}
        			
        		}
        		//1、要求commpara=9227存在，且要求用户的不存在该优惠（包括本次办理及已有的）        
                IDataset commparaInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "9227",newProductId, btd.getRD().getUca().getUserEparchyCode());
                if (commparaInfos!=null && commparaInfos.size()>0)
                {
                	
                	String discntCode=commparaInfos.getData(0).getString("PARA_CODE1");//para_code1=后台绑定优惠
                	String continuous=commparaInfos.getData(0).getString("PARA_CODE2","");//para_code2=绑定期限(数字代表几个月，null则到2050）
                	String effTime=commparaInfos.getData(0).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效
                	
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
	                	IDataset userDiscs=UserDiscntInfoQry.getAllDiscntByUser(userId,discntCode);
	                	if(userDiscs!=null && userDiscs.size()>0){
	                		flag=false;
	                	}
                	}
                	
                	//4、要求用户是本月开户的，即OPEN_DATE为当月。
                	if(flag){
	                	String openMon=uca.getUser().getOpenDate().substring(0,7);
	                	String sysMon=SysDateMgr.getSysDate().substring(0,7);
	                	if(!sysMon.equals(openMon)){
	                		if(!"1".equals(commparaInfos.getData(0).getString("PARA_CODE4",""))){
	                			flag=false;
	                		} 
	                	}
                	}
                	
            		if("1".equals(commparaInfos.getData(0).getString("PARA_CODE4",""))){
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
