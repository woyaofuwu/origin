package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;

public class RestoreFamilyAction implements ITradeFinishAction {
	
	static Logger logger = Logger.getLogger(RestoreFamilyAction.class); 

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		/**
		 * 1恢复亲亲网关系
		 */
		if("1".equals(mainTrade.getString("RSRV_STR8",""))){
			String userId = mainTrade.getString("USER_ID");
			String serialNumber = mainTrade.getString("SERIAL_NUMBER");
			String eparchyCode = mainTrade.getString("EPARCHY_CODE");
			IDataset tradeDatas = TradeHistoryInfoQry.getInfosOrderByDesc(serialNumber, userId, null); 
			if(IDataUtil.isNotEmpty(tradeDatas)){
				IData tradeData = tradeDatas.first();
				String tradeId = tradeData.getString("TRADE_ID");
				IDataset tradeRelaInfos = TradeRelaInfoQry.queryTradeRelaByTradeIdModTag(tradeId,"45",BofConst.MODIFY_TAG_DEL);
				IData param = new DataMap();
				for(int i=0;i<tradeRelaInfos.size();i++){
					IData tradeRelaInfoData = tradeRelaInfos.getData(i);
					String roleCodeB = tradeRelaInfoData.getString("ROLE_CODE_B");
					String serialNumberB = tradeRelaInfoData.getString("SERIAL_NUMBER_B");
					String shortCode = tradeRelaInfoData.getString("SHORT_CODE");
					if("1".equals(roleCodeB)&&serialNumber.equals(serialNumberB)){
						param.put("SERIAL_NUMBER", serialNumber);
						param.put("PRODUCT_ID", "99000001");
						param.put("DISCNT_CODE", "3410");
						param.put("SHORT_CODE", shortCode);
						break;
					}
					
				}
				IDataset elements = new DatasetList();
				for(int i=0;i<tradeRelaInfos.size();i++){
					IData tradeRelaInfoData = tradeRelaInfos.getData(i);
					String roleCodeB = tradeRelaInfoData.getString("ROLE_CODE_B");
					String serialNumberB = tradeRelaInfoData.getString("SERIAL_NUMBER_B");
					String shortCode = tradeRelaInfoData.getString("SHORT_CODE");
					IDataset elements2 = new DatasetList();
					if("2".equals(roleCodeB)){
						IData para = new DataMap();
						para.put("SERIAL_NUMBER_B", serialNumberB);
						para.put("DISCNT_CODE_B", "3411");
						para.put("SHORT_CODE_B", shortCode);
						para.put("checkTag", "1");
						para.put("MEB_VERIFY_MODE", "2");
						para.put("tag", "0");
						System.out.println("标志RestoreFamilyAction2"+para);
						elements2.add(para);
						param.put("MEB_LIST", elements2);
						try 
						{
							param.put("PRE_TYPE", "1");
							IDataset set  =  CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", param);
							IData retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
							if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
		                    {
								param.remove("MEB_LIST");
								param.remove("PRE_TYPE");
								elements.add(para);
		                    }
						}catch(Exception e){
                	        	logger.error("RestoreFamilyAction复机恢复亲亲网校验失败号码："+serialNumberB);	
						}
						
					}
					
				}
				if(elements.size()>0){
					System.out.println("标志RestoreFamilyAction"+elements);
					param.put("MEB_LIST", elements);
				    CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", param);
				}
				
			}
			
		}
	}

}
