package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckFubaoRuleAction implements ITradeAction
{
	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		String sysDate = SysDateMgr.getSysDate();
		String userId=btd.getRD().getUca().getUserId();

		//String userId=databus.getString("USER_ID","");
		String tradeTypeCode=btd.getTradeTypeCode();
		//System.out.println("=======CheckFubaoRuleAction======tradeTypeCode:"+tradeTypeCode);
		//限制携入开户、复机时办理咪咕福包优惠
		if( "40".equals(tradeTypeCode) || "310".equals(tradeTypeCode) )
		{
			/* 获取业务台账 */
			List<DiscntTradeData> listTradeDiscnt = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			//System.out.println("=======CheckFubaoRuleAction======listTradeDiscnt:"+listTradeDiscnt);

	        /* 开始逻辑规则校验 */
			for (int i = 0; i < listTradeDiscnt.size(); i++)
	        {
	        	DiscntTradeData tradeDiscnt = listTradeDiscnt.get(i);

	        	String discntCode = tradeDiscnt.getDiscntCode();
				//System.out.println("=======CheckFubaoRuleAction======discntCode:"+discntCode);

	        	if ("0".equals(tradeDiscnt.getModifyTag()) && ("84008840".equals(discntCode)
	        			||"84008841".equals(discntCode) || "84008842".equals(discntCode) || "84008843".equals(discntCode)
	        			||"84009642".equals(discntCode) ||"84009643".equals(discntCode)|| "84009644".equals(discntCode) || "84009645".equals(discntCode)))
	        	{
	        			CSAppException.apperr(CrmUserException.CRM_USER_783,"携入开户、复机业务不允许办理咪咕福包优惠！");	        	
	        	}
	        }
		}
		// add by zhangxing3 for REQ201805240038“咪咕流量福包”活动通用流量到期自动赠送的需求 start
		//如果用户领取全国通用流量优惠，需要记录到相应的福包优惠的预留字段中。
		if("110".equals(tradeTypeCode) )
		{
			/* 获取业务台账 */
			List<DiscntTradeData> listTradeDiscnt = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			//System.out.println("=======CheckFubaoRuleAction======listTradeDiscnt:"+listTradeDiscnt);

	        /* 开始逻辑规则校验 */
			for (int i = 0; i < listTradeDiscnt.size(); i++)
	        {
	        	DiscntTradeData tradeDiscnt = listTradeDiscnt.get(i);
	        	String discntCode = tradeDiscnt.getDiscntCode();
	        	//System.out.println("=======CheckFubaoRuleAction======discntCode:"+discntCode);

	        	if ("0".equals(tradeDiscnt.getModifyTag()) && "84009036".equals(discntCode))
	        	{
	        		String modDiscnts="84008841,84009643";
	                List<DiscntTradeData> discntTradeDatas = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(modDiscnts);
		        	//System.out.println("=======CheckFubaoRuleAction======discntTradeDatas:"+discntTradeDatas);

	                if (discntTradeDatas.size() == 0)
	                {
	                    continue;
	                }

    	        	DiscntTradeData modDiscntTD = discntTradeDatas.get(0).clone();
    	        	modDiscntTD.setRsrvStr2(discntCode);
    	        	modDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
    	        	modDiscntTD.setRemark("记录用户领取的通用流量套餐到福包优惠的RSRV_STR2");				          				
    				btd.add(btd.getRD().getUca().getSerialNumber(), modDiscntTD);

	        	}
	        	if ("0".equals(tradeDiscnt.getModifyTag()) && "84009037".equals(discntCode))
	        	{
	        		String modDiscnts="84008842,84008843,84009644,84009645";
	                List<DiscntTradeData> discntTradeDatas = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(modDiscnts);
		        	//System.out.println("=======CheckFubaoRuleAction======discntTradeDatas:"+discntTradeDatas);

	                if (discntTradeDatas.size() == 0)
	                {
	                    continue;
	                }
	                for (int k = 0; k < discntTradeDatas.size(); k++)
	                {
	    	        	DiscntTradeData modDiscntTD = discntTradeDatas.get(k).clone();
			        	//System.out.println("=======CheckFubaoRuleAction======modDiscntTD:"+modDiscntTD);

	    	        	if("".equals(modDiscntTD.getRsrvStr2()) ||  modDiscntTD.getRsrvStr2() == null)
	    	        	{	    	        		
	    	        		modDiscntTD.setRsrvStr2(discntCode);
		    	        	modDiscntTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
		    	        	modDiscntTD.setRemark("记录用户领取的通用流量套餐到福包优惠的RSRV_STR2");				          				
		    				btd.add(btd.getRD().getUca().getSerialNumber(), modDiscntTD);
		    				break;
	    	        	}
	    	        	else{
	    	        		continue;
	    	        	}
	            		
	                }
	        	}
	        }
		}
		// add by zhangxing3 for REQ201805240038“咪咕流量福包”活动通用流量到期自动赠送的需求 end
		
		if( "10".equals(tradeTypeCode) || "150".equals(tradeTypeCode) || "110".equals(tradeTypeCode) )
		{
			/* 获取业务台账 */
			List<DiscntTradeData> listTradeDiscnt = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
	
	        /* 开始逻辑规则校验 */
	        for (Iterator iterTradeDiscnt = listTradeDiscnt.iterator(); iterTradeDiscnt.hasNext();)
	        {
	        	DiscntTradeData tradeDiscnt = (DiscntTradeData) iterTradeDiscnt.next();

	        	String discntCode = tradeDiscnt.getDiscntCode();
	        	if ("0".equals(tradeDiscnt.getModifyTag()) && ("84008840".equals(discntCode)
	        			||"84008841".equals(discntCode) || "84008842".equals(discntCode) || "84008843".equals(discntCode)
	        			||"84009642".equals(discntCode) ||"84009643".equals(discntCode)|| "84009644".equals(discntCode) || "84009645".equals(discntCode)))
	        	{
	        		IDataset idsUserDiscnt =  UserDiscntInfoQry.getAllDiscntsByUD(userId,discntCode);
	        		if (IDataUtil.isNotEmpty(idsUserDiscnt))
	        		{
	        			CSAppException.apperr(CrmUserException.CRM_USER_783,"咪咕流量福包，用户仅可办理同一咪咕流量福包和首月免费体验各1次");
	        		}
	        		String bookingDate=btd.getRD().getPageRequestData().getString("BOOKING_DATE","");

	        		if(!"".equals(bookingDate) && bookingDate.substring(0,10).compareTo(sysDate.substring(0,10))>0 )
	        		{
	        			CSAppException.apperr(CrmUserException.CRM_USER_783,"咪咕流量福包，不支持预约订购！");

	        		}
	        		String startDate = tradeDiscnt.getStartDate();
	        		if(startDate.substring(0,10).compareTo(sysDate.substring(0,10))>0 )
					{
	        			CSAppException.apperr(CrmUserException.CRM_USER_783,"咪咕流量福包，不支持预约订购！");
					}
	        	}
	        	
	        	if ("1".equals(tradeDiscnt.getModifyTag()) && ("84008840".equals(discntCode)||"84008841".equals(discntCode)
	        			 || "84008842".equals(discntCode) || "84008843".equals(discntCode) ||"84009642".equals(discntCode)
	        			 ||"84009643".equals(discntCode)|| "84009644".equals(discntCode) || "84009645".equals(discntCode)))
	        	{
	        		IDataset idsUserDiscnt =  UserDiscntInfoQry.getAllDiscntsByUD(userId,discntCode);
	        		if (IDataUtil.isNotEmpty(idsUserDiscnt))
	        		{
		        		String endDate = idsUserDiscnt.getData(0).getString("END_DATE", "");
		        		String sysdate = SysDateMgr.getSysDate();
						int eMonth =SysDateMgr.monthInterval(endDate,sysdate);//计算剩余多少天

						if(eMonth <= 3){
			        		CSAppException.apperr(CrmUserException.CRM_USER_783,"合约期内，不支持退订咪咕福包！");
						}
	        		}
	        	}
	        }
		}
		
        if( "3700".equals(tradeTypeCode) )
        {
    		
        	List<PlatSvcTradeData> listTradePlatSvc = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        	if (listTradePlatSvc == null || listTradePlatSvc.size() <= 0)
            {
                return;
            }
        	for (int i = 0; i < listTradePlatSvc.size(); i++)
            {
        		PlatSvcTradeData pstd = listTradePlatSvc.get(i);
	        
	        	String serviceId = pstd.getElementId(); String operCode = pstd.getOperCode();
	        	IDataset bindPlatSvcList = UserDiscntInfoQry.getBindPlatSvcByUD(userId,tradeTypeCode);
	
		        if (IDataUtil.isNotEmpty(bindPlatSvcList))
				{
		        	for (Iterator iterPlatSvcList = bindPlatSvcList.iterator(); iterPlatSvcList.hasNext();)
		        	{
		        		IData bindPlatSvc = (IData) iterPlatSvcList.next();
		        		String bindServiceId = bindPlatSvc.getString("SERVICE_ID", "");
		        		String endDate = bindPlatSvc.getString("END_DATE", "");
		        		String sysdate = SysDateMgr.getSysDate();
						int eMonth =SysDateMgr.monthInterval(endDate,sysdate);//计算剩余多少天
	
		        		if ("07".equals(operCode)  && bindServiceId.equals(serviceId) && eMonth <= 3)
	                	{
	                		CSAppException.apperr(CrmUserException.CRM_USER_783,"咪咕福包合约期内，不支持退订相应的平台服务！");
	                	}
		        	}
				}
            }
        }
	}


}
