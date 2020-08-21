package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckElementIsValidSubScribeAction  implements ITradeAction{
	
	
	public void executeAction(BusiTradeData btd) throws Exception {
		
		List<DiscntTradeData> discntTradeDatas=btd.get("TF_B_TRADE_DISCNT");
		
		if(discntTradeDatas!=null&&discntTradeDatas.size()>0){
			
			String curDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
			
			//因为前台和后台的提示信息不一样，这里需要判断前台标识
			boolean isFromForeground=false;
			IData pageDate=btd.getRD().getPageRequestData();
			
			if(IDataUtil.isNotEmpty(pageDate)){
				String foregroundSign=pageDate.getString("IS_FROM_FOREGROUND","");
				if(foregroundSign!=null&&foregroundSign.equals("1")){
					isFromForeground=true;
				}
			}
			
			for (DiscntTradeData discntTradeData : discntTradeDatas) {
				if(discntTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
					/*
					 * 第一步：验证假日套餐是否在允许的办理时间之内
					 * 第二部，修改假日套餐的办理时间
					 * 
					 */
					//获取元素的办理时间
                	IDataset elementSpecialTimeLimit=CommparaInfoQry.queryComparaByAttrAndCode1
    												("CSM", "7686", discntTradeData.getElementId(), discntTradeData.getElementType());
                	
                	if(IDataUtil.isNotEmpty(elementSpecialTimeLimit)){
                		
                		//验证当前时间是否在有效期之内
                		//获取时间配置
                		IData dateLimitConfig=elementSpecialTimeLimit.getData(0);
                		//有效期配置
                		String validBeginDate=dateLimitConfig.getString("PARA_CODE4", "");
                		String validEndDate=dateLimitConfig.getString("PARA_CODE5", "");
                		String discntName=dateLimitConfig.getString("PARA_CODE2", "");
                		
                		if(StringUtils.isNotBlank(validBeginDate)&&StringUtils.isNotBlank(validEndDate)){
                			
                			//如果在有效期内
                			if(curDate.compareTo(validBeginDate)>0&&curDate.compareTo(validEndDate)<0){
                				
                				//验证用户是否已经办理过套餐
                                String userId=btd.getRD().getUca().getUserId();
                                IDataset userDiscnt=UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, discntTradeData.getElementId(), CSBizBean.getUserEparchyCode());
                                /*if(IDataUtil.isNotEmpty(userDiscnt)){
                                	CSAppException.apperr(ProductException.CRM_PRODUCT_521,"【"+discntTradeData.getElementId()+discntName+"】");
                                }*/
                				
                				/*
                				 * 判断是在哪个规定的时间内办理 
                				 * 如果假期前办理，就在假期第一天开始生效
                				 * 如果是假期当中办理，就是马上生效
                				 * 终止时间都为假期的最后一天
                				 */
                				String holidayBeforeBegin=dateLimitConfig.getString("PARA_CODE6", "");
                				String holidayBeforeEnd=dateLimitConfig.getString("PARA_CODE7", "");
                				String holidayBeforeStartDate=dateLimitConfig.getString("PARA_CODE8", "");
                				
                				String holidayBegin=dateLimitConfig.getString("PARA_CODE9", "");
                				String holidayEnd=dateLimitConfig.getString("PARA_CODE10", "");
                				
                				String allEndDay=dateLimitConfig.getString("PARA_CODE12", "");
                				
                				
                				if(StringUtils.isNotBlank(holidayBeforeBegin)&&StringUtils.isNotBlank(holidayBeforeEnd)){
                					if(curDate.compareTo(holidayBeforeBegin)>0&&curDate.compareTo(holidayBeforeEnd)<0){
                						discntTradeData.setStartDate(holidayBeforeStartDate);
                					}
                				}
                				if(StringUtils.isNotBlank(holidayBegin)&&StringUtils.isNotBlank(holidayEnd)){
                					if(curDate.compareTo(holidayBegin)>0&&curDate.compareTo(holidayEnd)<0){
                						discntTradeData.setStartDate(curDate);
                					}
                				}
                				
                				discntTradeData.setEndDate(allEndDay);                				
                				this.resetOfferRelDate(discntTradeData, btd, btd.getRD().getUca());
                			}else{
                				if(isFromForeground){
                					CSAppException.apperr(ProductException.CRM_PRODUCT_522, "优惠【"+discntTradeData.getDiscntCode()+discntName+"】不在办理的有效期内！");
                				}else{
                					CSAppException.apperr(ProductException.CRM_PRODUCT_522, "尊敬的客户：请您在假日流量套餐开放期间（假日前7天至假日最后一天）发送短信“KTJRTC”或“开通假日套餐”至10086办理。感谢您的参与！中国移动");
                				}
                			}
                		}else{
                			if(isFromForeground){
            					CSAppException.apperr(ProductException.CRM_PRODUCT_522, "优惠【"+discntTradeData.getDiscntCode()+discntName+"】不在办理的有效期内！");
            				}else{
            					CSAppException.apperr(ProductException.CRM_PRODUCT_522, "尊敬的客户：请您在假日流量套餐开放期间（假日前7天至假日最后一天）发送短信“KTJRTC”或“开通假日套餐”至10086办理。感谢您的参与！中国移动");
            				}
                		}
                		
                	}
                	
                	IDataset elementHolidayTimeLimit=CommparaInfoQry.queryComparaByAttrAndCode1
							("CSM", "1806", discntTradeData.getElementId(), discntTradeData.getElementType());

					if(IDataUtil.isNotEmpty(elementHolidayTimeLimit)){
					
						//验证当前时间是否在有效期之内
						//获取时间配置
						IData dateLimitConfig=elementHolidayTimeLimit.getData(0);
						//有效期配置
						//本月第一天
						String validBeginDate = SysDateMgr.getFirstDayOfThisMonth();
						//本月最后一天
						String validEndDate = SysDateMgr.getLastDateThisMonth();
						String discntName=dateLimitConfig.getString("PARA_CODE2", "");
						
						if(StringUtils.isNotBlank(validBeginDate)&&StringUtils.isNotBlank(validEndDate)){
							
							//如果在有效期内
							if(curDate.compareTo(validBeginDate)>0&&curDate.compareTo(validEndDate)<0){
								
								//验证用户是否已经办理过套餐
						        String userId=btd.getRD().getUca().getUserId();
						        IDataset userDiscnt=UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, discntTradeData.getElementId(), CSBizBean.getUserEparchyCode());
						        /*if(IDataUtil.isNotEmpty(userDiscnt)){
						        	CSAppException.apperr(ProductException.CRM_PRODUCT_521,"【"+discntTradeData.getElementId()+discntName+"】");
						        }*/
								
						      //标志：1表示走本月减特定天数；2表示本月几号
								String timeFlag = dateLimitConfig.getString("PARA_CODE6", "1");
								if("1".equals(timeFlag))
								{
									String timeday = dateLimitConfig.getString("PARA_CODE7", "7");
									String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
									String holidayBeforeEnd = SysDateMgr.addDays(validEndDate, -Integer.parseInt(timeday));
									if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
									{
										if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
										{
											discntTradeData.setStartDate(SysDateMgr.addDays(holidayBeforeEnd, 0));
										}
									}
								}else if("2".equals(timeFlag))
								{
									String timeday = dateLimitConfig.getString("PARA_CODE8", "23");
									String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
									//yyyy-MM-dd
									String timeend = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
									String holidayBeforeEnd = timeend.substring(0, 8) + timeday;
									if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
									{
										if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
										{
											discntTradeData.setStartDate(SysDateMgr.addDays(holidayBeforeEnd, 0));
										}
									}
								}else
								{
									discntTradeData.setStartDate(curDate);
								}
								
								if(StringUtils.isBlank(discntTradeData.getStartDate()))
								{
									discntTradeData.setStartDate(curDate);
								}
								discntTradeData.setEndDate(SysDateMgr.getLastDateThisMonth()); 
								               				
								this.resetOfferRelDate(discntTradeData, btd, btd.getRD().getUca());
							}else{
								if(isFromForeground){
									CSAppException.apperr(ProductException.CRM_PRODUCT_522, "优惠【"+discntTradeData.getDiscntCode()+discntName+"】不在办理的有效期内！");
								}else{
									CSAppException.apperr(ProductException.CRM_PRODUCT_522, "尊敬的客户：请您在假日流量套餐开放期间（假日前7天至假日最后一天）发送短信“KTJRTC”或“开通假日套餐”至10086办理。感谢您的参与！中国移动");
								}
							}
						}else{
							if(isFromForeground){
								CSAppException.apperr(ProductException.CRM_PRODUCT_522, "优惠【"+discntTradeData.getDiscntCode()+discntName+"】不在办理的有效期内！");
							}else{
								CSAppException.apperr(ProductException.CRM_PRODUCT_522, "尊敬的客户：请您在假日流量套餐开放期间（假日前7天至假日最后一天）发送短信“KTJRTC”或“开通假日套餐”至10086办理。感谢您的参与！中国移动");
							}
						}
					}
				}
			}
		}
	}
	
	private void resetOfferRelDate(DiscntTradeData discnt, BusiTradeData btd, UcaData uca) throws Exception{
    	List<OfferRelTradeData> offerRels = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
    	for(OfferRelTradeData offerRel : offerRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			offerRel.setStartDate(discnt.getStartDate());
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    	
    	List<OfferRelTradeData> userOfferRels = uca.getOfferRelsByRelUserId();
    	for(OfferRelTradeData offerRel : userOfferRels){
    		if(offerRel.getRelOfferInsId().equals(discnt.getInstId()) && BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getOfferType())){
    			offerRel.setStartDate(discnt.getStartDate());
    			offerRel.setEndDate(discnt.getEndDate());
    			break;
    		}
    	}
    }
}
