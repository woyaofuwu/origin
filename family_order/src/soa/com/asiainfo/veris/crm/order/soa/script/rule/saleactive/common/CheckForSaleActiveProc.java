
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 营销活动存储过程校验 
 */
public class CheckForSaleActiveProc extends BreBase implements IBREScript
{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CheckForSaleActiveProc.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckForSaleActiveProc() >>>>>>>>>>>>>>>>>>");
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> databus：" + databus.toString());
        }
        
        //入参获取
        
//        String productId = databus.getString("PRODUCT_ID","");//获取当前预受理的产品ID
//        String packageId = databus.getString("PACKAGE_ID","");//获取当前预受理的包ID
        String productId = databus.getString("RSRV_STR1","");//获取当前预受理的产品ID
        String packageId = databus.getString("RSRV_STR2","");//获取当前预受理的包ID
        
        
        String userId = databus.getString("USER_ID");
        
        //增加产品校验配置
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> PRODUCT_ID：" + productId);
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> PRODUCT_ID limit：" + ProductInfoQry.checkSaleActiveLimitProd(productId));
  		if(!ProductInfoQry.checkSaleActiveLimitProd(productId)){
  			return true;
  		}
        
        //获取CUST_ID参数
        IDataset userInfos = UserInfoQry.getUserInfoByUserId(userId); 
        if (userInfos == null || userInfos.size() <= 0) 
        {
        	return false;
        }
        String custId = userInfos.getData(0).getString("CUST_ID");
                
        
        String strCheckInfo = null;
        int iReusltCode = 0;
        String strResultInfo = null; 
        
        String paramName[] = {
        		"v_event_type", "v_eparchy_code", "v_city_code", "v_depart_id", "v_staff_id",
        		"v_user_id", "v_deposit_gift_id", "v_purchase_mode", "v_purchase_attr", "v_trade_id",
        		"v_checkInfo", "v_resultcode", "v_resultinfo", "v_sale_type", "v_vip_type_id",
        		"v_vip_class_id"};
		IData paramValue = new DataMap();
		paramValue.put("v_event_type", "TRADE");
		paramValue.put("v_eparchy_code", databus.getString("TRADE_EPARCHY_CODE"));
		paramValue.put("v_city_code", databus.getString("TRADE_CITY_CODE"));
		paramValue.put("v_depart_id", databus.getString("TRADE_DEPART_ID"));
		paramValue.put("v_staff_id", databus.getString("TRADE_STAFF_ID"));
		
		paramValue.put("v_user_id", userId);
		paramValue.put("v_deposit_gift_id", custId);
		paramValue.put("v_purchase_mode", productId);
		paramValue.put("v_purchase_attr", packageId);
		paramValue.put("v_trade_id", "-1");
		
		paramValue.put("v_checkInfo", strCheckInfo);
		paramValue.put("v_resultcode", iReusltCode);
		paramValue.put("v_resultinfo", strResultInfo);
		paramValue.put("v_sale_type", databus.getString("CAMPN_TYPE", ""));
		paramValue.put("v_vip_type_id", databus.getString("VIP_TYPE_ID", "-1"));
		
		paramValue.put("v_vip_class_id", databus.getString("VIP_CLASS_ID", "-1"));
		
		Dao.callProc("p_csm_CheckForSaleActive", paramName, paramValue,Route.CONN_CRM_CEN);
		iReusltCode = paramValue.getInt("v_resultcode");
		strResultInfo = paramValue.getString("v_resultinfo");
		
		logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>p_csm_CheckForSaleActive paramValue：" + paramValue);
        /*IData paramValue=new DataMap();
        Dao.callProc("p_csm_CheckForSaleActive", null, paramValue);
        
        int iReusltCode = paramValue.getInt("v_resultcode");
		String strResultInfo = paramValue.getString("v_resultinfo");*/
        
        /*//调用存储过程
        DBConnection conn = null;
        CallableStatement cs = null;
        conn = new DBConnection(BizConstants.DBCONN_CEN1_NAME, false, false);
        conn.setStmtTimeout(600);
        cs = conn.prepareCall("{call p_csm_CheckForSaleActive(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
        cs.setString(1, "TRADE");
        cs.setString(2, databus.getString("TRADE_EPARCHY_CODE"));
        cs.setString(3, databus.getString("TRADE_CITY_CODE"));
        cs.setString(4, databus.getString("TRADE_DEPART_ID"));
        cs.setString(5, databus.getString("TRADE_STAFF_ID"));
        cs.setString(6, userId);
        cs.setString(7, custId);
        cs.setString(8, productId);
        cs.setString(9, packageId);
        cs.setString(10, "-1");
        cs.registerOutParameter(11, java.sql.Types.VARCHAR);
        cs.registerOutParameter(12, java.sql.Types.INTEGER);
        cs.registerOutParameter(13, java.sql.Types.VARCHAR);
        cs.setString(14, databus.getString("CAMPN_TYPE", ""));
        cs.setString(15, databus.getString("VIP_TYPE_ID", "-1"));
        cs.setString(16, databus.getString("VIP_CLASS_ID", "-1"));
        //cs.setString(17, databus.getString("TROLLEY_TAG","-1"));        
        cs.execute();
        //String strCheckInfo = cs.getString(11);
		int iReusltCode = cs.getInt(12);
		String strResultInfo = cs.getString(13);
        cs.close();
        conn.close();*/
        
        
        
        if (iReusltCode != 0)
		{
        	//common.error(strResultInfo);
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, iReusltCode, strResultInfo);
            return false;
		}
        
        return true;
    }

}
