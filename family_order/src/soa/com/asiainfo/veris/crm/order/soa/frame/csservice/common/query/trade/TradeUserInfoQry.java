
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.CrmDAO;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class TradeUserInfoQry
{
    /**
     * 根据tradeId查询所有的用户备份数据
     *
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakUserByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_USER_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    // todo 无SQL
    /**
     * 获取业务台账用户资料子表
     *
     * @param iData
     * @return
     */
    public static IDataset getTradeOpenuserElec(IData data) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_B_ELEC_OPENUSER_TRADE", "SEL_ELEC_BY_PK", data);
        return dataset;

    }

    // todo 无SQL
    /**
     * 获取业务台账用户资料子表
     *
     * @param iData
     * @return
     */
    public static IDataset getTradeOpenuserInfo(IData data) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_B_ELEC_OPENUSER_TRADE", "SEL_ELEC_BY_PK", data);
        return dataset;

    }

    /**
     * 获取业务台账用户资料子表
     *
     * @param iData
     * @return
     */
    public static IDataset getTradeUser(String tradeId, Pagination pagination) throws Exception
    {
        if (tradeId == null || "".equals(tradeId))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_84);
        }
        IData params = new DataMap();
        params.put("VTRADE_ID", tradeId);
        try
        {
            IDataset iDataset = Dao.qryByCode("TF_B_TRADE_USER", "SEL_TRADE_USER", params, pagination, Route.CONN_CRM_CEN);
            return iDataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(TradeException.CRM_TRADE_89);
            return null;
        }
    }

    /**
     * 根据tradeId查询所有的用户台账
     *
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeUserByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_USER", "SEL_TRADE_USER", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 根据tradeId查询所有的用户台账
     *
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeUserByTradeIdForGrp(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_USER", "SEL_TRADE_USER", params, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @Function:
     * @Description: 获取主号的无线传真服务
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-7-29 上午9:56:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserSvc(String tradeId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT *  FROM TF_B_TRADE_SVC a WHERE trade_id = TO_NUMBER(:TRADE_ID)    ");
        parser.addSQL("  AND modify_tag in ('0','4') AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("  AND NOT EXISTS (SELECT 1 FROM td_s_commpara WHERE subsys_code = 'CSM' ");
        parser.addSQL("  AND param_attr = '996'   AND to_number(param_code) = a.service_id AND sysdate BETWEEN start_date AND end_date) ");
        parser.addSQL("  AND NOT EXISTS (SELECT  1 FROM TF_F_USER_SVC r WHERE user_id = TO_NUMBER(:USER_ID) ");
        parser.addSQL("  AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)  AND user_id_a = a.user_id_a ");
        parser.addSQL("  AND product_id = a.product_id AND package_id = a.package_id AND service_id+0 = a.service_id ");
        parser.addSQL("  AND end_date > start_date   AND end_date > a.start_date) ");

        return Dao.qryByParse(parser);
    }

    /**
     * 更新开户时间
     *
     * @author chenzm
     * @param trade_id
     * @param open_date
     * @throws Exception
     */
    public static void updateOpenDate(String trade_id, String open_date) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("OPEN_DATE", open_date);
        param.put("IN_DATE", open_date);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_USER", "UPD_OPENDATE_AND_INDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));


    }

    /**
     * 更新合同编码
     *
     * @author chenzm
     * @param trade_id
     * @param open_date
     * @throws Exception
     */
    public static void updateContractId(String orderId, String contractId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("CONTRACT_ID", contractId);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_USER", "UPD_CONTRACT_ID", param);


    }
    
    
    
    /**
     * 核对宽带用户是否在指定的月当中做过宽带过户信息
     * @param userId
     * @param acceptMonth
     * @return
     * @throws Exception
     */
    public static boolean queryWideNetUserIsChangeOwner(String userId, String acceptMonth)
    	throws Exception{
    	IData param=new DataMap();
    	param.put("USER_ID", userId);
    	param.put("ACCEPT_MONTH", acceptMonth);
    	
    	IDataset set=Dao.qryByCode("TF_B_TRADE_USER", "QRY_WIDEUSER_CHANGEOWNER_MONTH", param,Route.getJourDb(BizRoute.getRouteId()));
    	if(IDataUtil.isNotEmpty(set)){
    		return true;
    	}else{
    		return false;
    	}
    }

    
    
    
    
  
    
    
    /**
	 * 根据tradeid 查询指定优惠
	 * @param userId
	 * @return
	 * @throws Exception
	 * xuzh5 2018-8-2 15:19:19
	 */
	public static IDataset getTradeSvcById(String trade_id) throws Exception {

	   IData   inparam=new  DataMap();
	   inparam.put("TRADE_ID", trade_id);
       IDataset result = Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_SVC_BYID", inparam, Route.getJourDb(BizRoute.getTradeEparchyCode()));
            if(result!=null&&result.size()>0){
            	return result;
	        }
        
	   return null;
	}

    /**
     * 根据tradeid 查询指定优惠
     * @param userId
     * @return
     * @throws Exception
     * xuzh5 2018-8-2 15:19:19
     */
    public static IDataset getTradeChangeSpeedDiscntById(String trade_id) throws Exception {

        IData   inparam=new  DataMap();
        inparam.put("TRADE_ID", trade_id);
        IDataset result = Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_CHANGE_SPEED_BYID", inparam, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        if(result!=null&&result.size()>0){
            return result;
        }

        return null;
    }

	/**
	 * 根据ID 更新指定优惠的时间
	 * @param userId
	 * @return
	 * @throws Exception
	 * xuzh5 2018-8-2 15:19:19
	 */
	public static void updateUserDiscntById(String inst_id,String discnt_code ,String start_date,String end_date,String param) throws Exception {
		
		IData ctrmData = new DataMap();
		ctrmData.put("INST_ID", inst_id);
		ctrmData.put("DISCNT_CODE", discnt_code);
		ctrmData.put("START_DATE", start_date);
		ctrmData.put("END_DATE", end_date);
		Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT",param, ctrmData);
	}
	
	/**
	 * 根据ID 更新指定优惠的时间
	 * @param userId
	 * @return
	 * @throws Exception
	 * xuzh5 2018-8-2 15:19:19
	 */
	public static void updateUserRelDiscntById(String inst_id,String discnt_code ,String start_date,String end_date,String param) throws Exception {
		
		IData ctrmData = new DataMap();
		ctrmData.put("REL_OFFER_INS_ID", inst_id);
		ctrmData.put("DISCNT_CODE", discnt_code);
		ctrmData.put("START_DATE", start_date);
		ctrmData.put("END_DATE", end_date);
		Dao.executeUpdateByCodeCode("TF_F_USER_OFFER_REL",param, ctrmData);
	}
}
