
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IPrintFinishAction;

/**
 * 机场易登机服务，afterAction 等级服务台帐完成后，TF_B_VIPAIRDROME_SERVICE插入一条记录，服务次数
 * 
 * @author ray
 */
public class AirportVipServerAction implements IPrintFinishAction
{
	private static transient final Logger logger = Logger.getLogger(AirportVipServerAction.class);
	 /**
     * 生成俱乐部易登机服务表数据
     * 
     * @author songzy
     * @param pd
     * @param td
     * @throws Exception
     */
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub
		IData data = new DataMap();
		IData param = new DataMap(mainTrade.getString("RSRV_STR10"));
		IData lastBakInfo = new DataMap(mainTrade.getString("RSRV_STR8"));
        data.put("SERVICE_ID", param.getString("SERVICE_ID",""));
        data.put("AIRDROME_ID", param.getString("AIRDROME_ID",""));
        data.put("AIRDROME_NAME", param.getString("AIRDROME_NAME",""));
        data.put("VIP_ID", param.getString("VIP_ID","")); // 全球通商旅非VIP客户 VIP_ID为0
        data.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER",""));
        data.put("CUST_NAME", mainTrade.getString("CUST_NAME",""));
        data.put("VIP_TYPE_CODE", param.getString("VIP_TYPE_CODE","")); // 全球通商旅非VIP客户 VIP_TYPE_CODE为S
        data.put("CLASS_ID", param.getString("CLASS_ID","")); // 全球通商旅非VIP客户 VIP_CLASS_ID为S
        data.put("VIP_NO", param.getString("VIP_NO",""));
        data.put("PLANE_LINE", param.getString("PLANE_LINE",""));
        data.put("BEGIN_CITY", "");
        data.put("ARRIVE_CITY", "");
        data.put("FOLLOW_NUM", param.getString("FOLLOW_NUM",""));
        data.put("SERVICE_CONTENT", mainTrade.getString("RSRV_STR9",""));
        data.put("OLD_SCORE_VALUE", param.getString("OLD_SCORE_VALUE",""));
        data.put("CONSUME_SCORE", param.getString("CONSUME_SCORE","")); // 消耗积分
        data.put("SERVICE_TYPE", param.getString("SERVICE_TYPE",""));
        data.put("HANDING_CHARGE", param.getString("HANDING_CHARGE","")); // 手续费
        data.put("SERVICE_CHARGE", param.getString("SERVICE_CHARGE","")); // 服务费
        data.put("SERVICE_STAFF", param.getString("TRADE_STAFF_ID",""));
        data.put("SERVICE_DATE", param.getString("SERVICE_DATE",""));
        data.put("STATE", "0"); // 已受理
        data.put("RESERVICE_ID", param.getString("SERVICE_ID",""));
        data.put("RETURN_EXPLAIN", "");
        data.put("RETURN_DATE", "");
        data.put("RETURN_STAFF", "");
        data.put("REMARK", "VIP机场易登机服务");
        data.put("RSRV_TAG1", "1");
        data.put("RSRV_STR2", param.getString("RSRV_STR2","")); // 当次受理使用的免费次数
        data.put("RSRV_STR3", param.getString("RSRV_STR3","")); // 剩余免费次数
        data.put("INDIV_INFO", lastBakInfo.getString("INDIV_INFO",""));
        data.put("FEEDBACK", lastBakInfo.getString("FEEDBACK",""));
        data.put("INNOVATION", lastBakInfo.getString("INNOVATION",""));
        data.put("ADVICES", lastBakInfo.getString("ADVICES",""));
        data.put("OTHERS", lastBakInfo.getString("OTHERS",""));
        Dao.insert("TF_B_VIPAIRDROME_SERVICE", data, Route.getCrmDefaultDb());

	}
}
