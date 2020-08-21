
package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;


/**
 * 
 * 魔百和状态变更时，调用魔百和订单实时同步接口
 * REQ202003050012关于开发融合套餐增加魔百和业务优惠体验权益的需求
 * @author chenyw7
 *
 */
public class TopSetBoxStateChangeAction implements ITradeFinishAction
{

	@Override
    public void executeAction(IData mainTrade) throws Exception
    {
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");

		
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");//宽带账号
		String opType = "";//操作类型：0-退订；1-订购；  2、续订成功；3、魔百和报开；4、魔百和报停；5、关联欠停；6、关联报开 7、体验基础包到期未续订导致报停；8、续订登记
		String serviceId = "";//服务编码
		String renewFlag = "";//续订结果标识  Y：已登机续订   N：已登机不续订 字段为空：用户未登记
		
		
		
		
		IData param = new DataMap();//返回结果
		
		if("3800".equals(tradeTypeCode)){//魔百和开户
			opType = "1";
		}else if("3806".equals(tradeTypeCode)){//魔百和拆机
			opType = "0";
		}else if("3900".equals(tradeTypeCode)){//魔百和停机
			opType = "4";
			
		}else if("3901".equals(tradeTypeCode)){//魔百和开机
			opType = "3";
			
		}else if("7220".equals(tradeTypeCode) || "459".equals(tradeTypeCode) ){//欠费停机，459-家庭欠费停机
			opType = "5";
			
		}else if("7301".equals(tradeTypeCode) || "460".equals(tradeTypeCode)){//缴费开机  460-家庭缴费开机
			opType = "6";
			
		}


		IDataset platSvcList = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
		
		if(IDataUtil.isNotEmpty(platSvcList)){
			for (int i = 0; i < platSvcList.size(); i++) {
				IData  platSvcInfo =  platSvcList.getData(i);
				serviceId = platSvcInfo.getString("SERVICE_ID");
				String rsrvStr8 = platSvcInfo.getString("RSRV_STR8");
				
				if("EXPERIENCE_CONTINUE".equals(rsrvStr8)){
					renewFlag = "Y";
				}else if("EXPERIENCE_STOP".equals(rsrvStr8)){//体验基础包到期未续订导致报停
					renewFlag = "N";
					if("3900".equals(tradeTypeCode)){
						opType = "7";
					}
				}
				
				if("40227762".equals(serviceId)){//办理未来电视魔百和基础包
					param.put("oldTradeId", tradeId);
					param.put("opType", opType);
					param.put("serviceId", serviceId);
					param.put("serialNumber", serialNumber);
					param.put("renewFlag", renewFlag);

					IDataset userSaleActiveInfo = UserSaleActiveInfoQry.queryUserSaleActiveProdId(userId,"66005203","0");
					IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId_1(userId,"84076649",Route.CONN_CRM_CG);
					String activityId="";
					if (IDataUtil.isNotEmpty(userSaleActiveInfo))
					{
						activityId = userSaleActiveInfo.first().getString("PACKAGE_ID");
					}
					String activityBeginDate="";
					String activityEndDate="";
					if (IDataUtil.isNotEmpty(discntInfo))
					{
						activityBeginDate = discntInfo.first().getString("START_DATE");
						activityEndDate = discntInfo.first().getString("END_DATE");
					}

					param.put("activityId", activityId);
					param.put("activityPrice", "100");
					param.put("activityBeginDate", activityBeginDate);
					param.put("activityEndDate", activityEndDate);
					//调用魔百和订单实时同步接口
					CSAppCall.call("SS.TopSetBoxSVC.syncOrderState", param);
				}
			}
			
		}
		
    }
	
}
