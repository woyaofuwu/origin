package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
/**
 * 宽带产品变更界面，不变更产品时，对原营销活动是否终止的处理
 * @author zyc
 *
 */
public class NoChangeProductEndSaleActiveAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String changeType = btd.getRD().getPageRequestData().getString("CHANGE_TYPE","");
		if(changeType != null && !"".equals(changeType) && "8".equals(changeType))
		{
			String oldSaleProductId = btd.getRD().getPageRequestData().getString("V_USER_PRODUCT_ID","");
			String oldSalePackageId = btd.getRD().getPageRequestData().getString("V_USER_PACKAGE_ID","");
			
			if(oldSaleProductId != null && !"".equals(oldSaleProductId) 
					&& oldSalePackageId != null && !"".equals(oldSalePackageId))
			{
				String userId = btd.getRD().getUca().getUser().getUserId();
				//查询原营销活动
				IDataset validSaleActiveInfos = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, oldSaleProductId,oldSalePackageId);
				if(validSaleActiveInfos != null && validSaleActiveInfos.size() > 0)
				{
					String bookDate = btd.getRD().getPageRequestData().getString("BOOKING_DATE","");
					if(bookDate != null && !"".equals(bookDate))
					{
						bookDate = SysDateMgr.addDays(bookDate,-1);
						bookDate = SysDateMgr.getDateLastMonthSec(bookDate);
					}
					else
					{
						bookDate = SysDateMgr.getLastDateThisMonth();
					}
					
					//取原活动的结束时间
					for(int i = 0 ; i < validSaleActiveInfos.size() ; i++)
					{
						String endDate = validSaleActiveInfos.getData(i).getString("END_DATE"); //营销活动表记录的END_DATE，有可能是2050年，但已经使用满一周期了，需要终止
						//只有原营销活动的结束时间大于当前月底时间，才需要终止
						if(SysDateMgr.compareTo(endDate, bookDate) > 0)
						{							
							IData endActiveParam = new DataMap();
					        endActiveParam.put("SERIAL_NUMBER", validSaleActiveInfos.getData(i).getString("SERIAL_NUMBER"));
					        endActiveParam.put("PRODUCT_ID", validSaleActiveInfos.getData(i).getString("PRODUCT_ID"));
					        endActiveParam.put("PACKAGE_ID", validSaleActiveInfos.getData(i).getString("PACKAGE_ID"));
					        endActiveParam.put("RELATION_TRADE_ID", validSaleActiveInfos.getData(i).getString("RELATION_TRADE_ID"));
					        endActiveParam.put("IS_RETURN", "0");
					        endActiveParam.put("FORCE_END_DATE", bookDate);
					        endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
					        endActiveParam.put("EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
					        //认证方式
					        String checkMode = btd.getRD().getCheckMode();
					        endActiveParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
					    	
					        CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
						}
					}
				}
			}
		}
		else
		{
			//add by zhangxing3 for 宽带资费的优化
			String saleProductId = btd.getMainTradeData().getRsrvStr1();
			String salePackageId = btd.getMainTradeData().getRsrvStr2();
			IDataset limit1681 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1681", saleProductId, salePackageId);
	        IDataset listTradeBookByUserId = TradeInfoQry.queryUnfinishTradeByTradeTypeCodeAndSerialNumber(btd.getRD().getUca().getSerialNumber(), "237");
	
	    	//System.out.println(">>>>>>>CheckSaleTradeLimit>>>>>>>>>limit1681:"+limit1681);
	    	if (IDataUtil.isNotEmpty(limit1681) && IDataUtil.isEmpty(listTradeBookByUserId))
	        {
	    		String oldPackageIds=limit1681.getData(0).getString("PARA_CODE2", "");
	    		String morePackageIds1=limit1681.getData(0).getString("PARA_CODE23", "");
	    		String morePackageIds2=limit1681.getData(0).getString("PARA_CODE24", "");
	    		if (StringUtils.isNotEmpty(morePackageIds1))
	            {
	    			oldPackageIds = oldPackageIds+morePackageIds1;
	            }
	    		if (StringUtils.isNotEmpty(morePackageIds2))
	            {
	    			oldPackageIds = oldPackageIds+morePackageIds2;
	            }
	        	//System.out.println(">>>>>>CheckSaleTradeLimit>>>>>>>>>>oldPackageId:"+oldPackageIds);
	        	String[] packageIdArray = null;
	        	if (StringUtils.isNotEmpty(oldPackageIds))
	            {
	        		packageIdArray = StringUtils.split(oldPackageIds, "|");
	            }
				IDataset validSaleActiveInfos = new DatasetList();
				for (int i = 0; i < packageIdArray.length; i++)
			    {
		        	//System.out.println(">>>>>>CheckSaleTradeLimit>>>>>>>>>>packageIdArray[i]:"+packageIdArray[i]);
	
					IDataset ids = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(btd.getRD().getUca().getUser().getUserId(), saleProductId,packageIdArray[i]);
					if (IDataUtil.isNotEmpty(ids))
		            {
						validSaleActiveInfos.addAll(ids);
		            }
			    }
				if(validSaleActiveInfos != null && validSaleActiveInfos.size() > 0)
				{				
		        	//System.out.println(">>>>>>CheckSaleTradeLimit>>>>>>>>>>validSaleActiveInfos:"+validSaleActiveInfos);
	
					for(int i = 0 ; i < validSaleActiveInfos.size() ; i++)
					{
						String endDate = validSaleActiveInfos.getData(i).getString("END_DATE",""); //营销活动表记录的END_DATE，有可能是2050年，但已经使用满一周期了，需要终止
						//只有原营销活动的结束时间大于当前月底时间，才需要终止
						if(SysDateMgr.compareTo(endDate, SysDateMgr.getLastDateThisMonth()) > 0)
						{	
						IData endActiveParam = new DataMap();
				        endActiveParam.put("SERIAL_NUMBER", validSaleActiveInfos.getData(i).getString("SERIAL_NUMBER",""));
				        endActiveParam.put("PRODUCT_ID", validSaleActiveInfos.getData(i).getString("PRODUCT_ID",""));
				        endActiveParam.put("PACKAGE_ID", validSaleActiveInfos.getData(i).getString("PACKAGE_ID",""));
				        endActiveParam.put("RELATION_TRADE_ID", validSaleActiveInfos.getData(i).getString("RELATION_TRADE_ID",""));
				        endActiveParam.put("IS_RETURN", "0");
				        endActiveParam.put("FORCE_END_DATE", SysDateMgr.getLastDateThisMonth());
				        endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
				        endActiveParam.put("EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
				        //认证方式
				        String checkMode = btd.getRD().getCheckMode();
				        endActiveParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
			        	//System.out.println(">>>>>>CheckSaleTradeLimit>>>>>>>>>>endActiveParam:"+endActiveParam);
	
				        CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
						}
					}
				}
	
	        }
		}
		//add by zhangxing3 for 宽带资费的优化
	}
}
