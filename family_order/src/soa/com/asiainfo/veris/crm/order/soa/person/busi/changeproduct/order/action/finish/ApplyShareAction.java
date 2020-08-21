package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

public class ApplyShareAction implements ITradeFinishAction
{
	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String execTime = mainTrade.getString("ACCEPT_DATE", SysDateMgr.getSysDate());
		String rsrvStr3 = mainTrade.getString("RSRV_STR3", execTime);
		String rsrvStr7 = mainTrade.getString("RSRV_STR7", "");
		String rsrvStr8 = mainTrade.getString("RSRV_STR8", "");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		IDataset productTrades = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
    	if (IDataUtil.isNotEmpty(productTrades))
        {
    		for (int i=0;i<productTrades.size();i++)
            {
    			IData product = productTrades.getData(i);
    			String productId = product.getString("PRODUCT_ID", "");
                String modifyTag = product.getString("MODIFY_TAG", "");
                
//                if ("80003014".equals(productId) && "0".equals(modifyTag) )
//    			{
//                	Thread.sleep(2000);
//    				//共享业务办理
//    				IData params = new DataMap();
//    				IDataset mebs = new DatasetList();
//    	
//    				// 新增的号码
//    				IData addMebone = new DataMap();
//    				IData addMebtwo = new DataMap();
//    				addMebone.put("SERIAL_NUMBER", rsrvStr7);
//    				addMebone.put("tag", "0");
//    	
//    				mebs.add(addMebone);
//    	
//    				addMebtwo.put("SERIAL_NUMBER", rsrvStr8);
//    				addMebtwo.put("tag", "0");
//    	
//    				mebs.add(addMebtwo);
//    	
//    				// 调用业务服务
//    				params.put("SERIAL_NUMBER", serialNumber);
//    				params.put("MEB_LIST", mebs);
//    				params.put("MEMBER_CANCEL", 0);
//    				params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
//    				params.put("REMARK", "158不限量套餐连带办理"+tradeId);
//    				params.put("NO_TRADE_LIMIT","TRUE");
//    				IDataset rtDataset = CSAppCall.call("SS.ShareMealRegSVC.tradeReg", params);
//
//    			}
                
                if ("80003014".equals(productId) && "1".equals(modifyTag) )
    			{
                	boolean flag = false;
                	IDataset MemberData = ShareInfoQry.queryMemberRela(userId, "01");
                    if(IDataUtil.isNotEmpty(MemberData))
                    {
                        IDataset mebList = ShareInfoQry.queryRelaByShareIdAndRoleCode(MemberData.getData(0).getString("SHARE_ID"), "02");
                        //添加重复取消校验
                        if(IDataUtil.isNotEmpty(mebList))
                        {
                        	IDataset mebs = new DatasetList();
                        	IDataset memberdatas = new DatasetList();
                        	for(int j=0;j<mebList.size();j++)
                        	{
                        		IData meb = mebList.getData(j);
                        		if(SysDateMgr.compareTo(meb.getString("END_DATE"), SysDateMgr.getSysDateYYYYMMDDHHMMSS()) >0)
                        		{
                                    String instId = meb.getString("INST_ID");
                                    // 删除的号码
                                    IData delMeb = new DataMap();
                                    delMeb.put("SERIAL_NUMBER_B", meb.getString("SERIAL_NUMBER"));
                                    delMeb.put("tag", "1");
                                    delMeb.put("INST_ID", instId);

                                    mebs.add(delMeb);          
                        		}
                        		
                        		String rsrvTag1 = meb.getString("RSRV_TAG1","");
                        		if("1".equals(rsrvTag1))
                        		{
                        			IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");
                        			if(IDataUtil.isNotEmpty(uuDs))
                        			{
                        				IDataset uuDb = RelaUUInfoQry.qryRelaUUBySerNumAndSerNumB(uuDs.getData(0).getString("SERIAL_NUMBER_A"), meb.getString("SERIAL_NUMBER"), "56");
                        				if(IDataUtil.isNotEmpty(uuDb))
                        				{
                        					flag = true;
                        					IData membone = new DataMap();
                            				membone.put("SERIAL_NUMBER_B", meb.getString("SERIAL_NUMBER"));
                            				//membone.put("START_DATE", rsrvStr3);
                            				membone.put("END_DATE", SysDateMgr.getSysDate());
                            				membone.put("MODIFY_TAG", "1");
                            				memberdatas.add(membone);
                        				}
                        			}      
                        		}
                        	}
                        	IData params = new DataMap();
                        	// 调用业务服务
                            params.put("SERIAL_NUMBER", serialNumber);
                            params.put("MEB_LIST", mebs);
                            params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
                            params.put("REMARK", "158不限量套餐连带删除"+tradeId);
                            params.put("NO_TRADE_LIMIT","TRUE");
                            params.put("SKIP_RULE","TRUE");

                            IDataset rtDataset = CSAppCall.call("SS.ShareMealRegSVC.tradeReg", params);
                            
                            if(flag)
                            {
                            	IData tradeData = new DataMap();
                				tradeData.put("SERIAL_NUMBER", serialNumber);
                				tradeData.put("AUTH_SERIAL_NUMBER", serialNumber);
                				tradeData.put("MEMBER_DATAS", memberdatas);
                				tradeData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
                				tradeData.put("REMARK", "158不限量套餐连带删除"+tradeId);
                				params.put("NO_TRADE_LIMIT","TRUE");
                				params.put("SKIP_RULE","TRUE");
                				IDataset dataset = CSAppCall.call("SS.FamilyUnionPayRegSVC.tradeReg", tradeData);
                            }
                            
                        }   
                    }
    			}
            }
        }
	}
}
