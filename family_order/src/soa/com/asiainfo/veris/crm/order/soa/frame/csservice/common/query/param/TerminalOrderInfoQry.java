
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;

public class TerminalOrderInfoQry
{
    public static void insertTerminalOrderInfo(IData data) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ID", data.getString("ID"));
        inparam.put("ORDER_ID", data.getString("ORDER_ID"));
        inparam.put("ORDER_STATE", data.getString("ORDER_STATE"));
        inparam.put("ORDER_TYPE", data.getString("ORDER_TYPE"));
        inparam.put("ORDER_PRICE", data.getString("ORDER_PRICE"));
        inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        inparam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        inparam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        inparam.put("DEVICE_MODEL_CODE", data.getString("DEVICE_MODEL_CODE"));
        inparam.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
        inparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
        inparam.put("START_TIME", SysDateMgr.decodeTimestamp(data.getString("START_TIME"), SysDateMgr.PATTERN_STAND));
        inparam.put("END_TIME", SysDateMgr.decodeTimestamp(data.getString("END_TIME"), SysDateMgr.PATTERN_STAND));
        inparam.put("REMARK", data.getString("REMARK"));
        inparam.put("USER_ID", "");
        inparam.put("USE_PRODUCT_ID", "");
        inparam.put("USE_PACKAGE_ID", "");
        inparam.put("UPDATE_TIME", "");
        inparam.put("UPDATE_STAFF_ID", "");
        inparam.put("TERMINAL_ID", "");
        inparam.put("RSRV_STR1", ""); // 后期记录trade_id
        inparam.put("RSRV_STR2", "0"); // 记录CRM侧该订单状态，0：已录入 1：资源状态已修改（资源已交付）
        inparam.put("RSRV_STR3", data.getString("PRODUCT_NAME")); // 记录网厅预约产品中文名
        inparam.put("RSRV_STR4", data.getString("PACKAGE_NAME")); // 记录网厅预约包中文名
        inparam.put("RSRV_STR5", data.getString("DEVICE_STAFF_ID"));
        String departId = UDepartInfoQry.getDepartIdByStaffId(data.getString("DEVICE_STAFF_ID"));
        inparam.put("RSRV_STR6", UDepartInfoQry.getDepartNameByDepartId(departId));
        /**
         * REQ201603310007 老客户回馈购机活动（第二季）线下营业员推荐到网厅购买活动机型可获得酬金的需求
         * */
        inparam.put("RSRV_STR7", data.getString("RSRV_STR7",""));
        inparam.put("RSRV_STR8", data.getString("RSRV_STR8",""));
        inparam.put("RSRV_STR9", data.getString("RSRV_STR9",""));
        inparam.put("RSRV_STR10", data.getString("RSRV_STR10",""));
        /**
         * REQ201601240002 关于修改电子渠道终端销售数据统计的需求
         * */
        inparam.put("RSRV_NUM1", data.getString("RSRV_NUM1","24"));  
        inparam.put("RSRV_NUM2", data.getString("RSRV_NUM2",""));  
        inparam.put("RSRV_NUM3", data.getString("RSRV_NUM3",""));  
        inparam.put("RSRV_NUM4", data.getString("RSRV_NUM4",""));  
        inparam.put("RSRV_NUM5", data.getString("RSRV_NUM5",""));  
        inparam.put("RSRV_NUM6", data.getString("RSRV_NUM6",""));  
        inparam.put("RSRV_NUM7", data.getString("RSRV_NUM7",""));  
        inparam.put("RSRV_NUM8", data.getString("RSRV_NUM8",""));  
        inparam.put("RSRV_NUM9", data.getString("RSRV_NUM9",""));  
        inparam.put("RSRV_NUM10", data.getString("RSRV_NUM10",""));

        Dao.insert("TF_F_TERMINALORDER", inparam);
    }

    /**
     * 查询可以免身份校验的网上预约活动订单
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryTerminalOrderInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_TERMINALORDER", "SEL_NOT_AUTHCHECK_BY_SN2", param);
    }

    public static IDataset qryTerminalOrderInfoById(String id) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        return Dao.qryByCode("TF_F_TERMINALORDER", "SEL_BY_PK", param);
    }

    public static IDataset qryTerminalOrderInfoByOrderId(String orderId, String state) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("RELATION_TRADE_ID", orderId);
        inparams.put("RSRV_STR2", state);
        return Dao.qryByCode("TF_F_TERMINALORDER", "SEL_BY_ORDER_ID", inparams);
    }

    public static IDataset qryTerminalOrderInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_TERMINALORDER", "SEL_BY_SN", param);
    }
    
    /**
     * @param serialNumber
     * @param orderId
     * @param orderState: 0：未处理、1：已受理、2：取消'
     * @param rsrvStr2: 记录CRM侧该订单状态，0：已录入 1：资源状态已修改（资源已交付）
     * @return
     * @throws Exception
     */
    public static IDataset qryTerminalOrderInfoForDeal(String serialNumber,String orderId,String orderState,String rsrvStr2) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ORDER_ID", orderId);
        param.put("ORDER_STATE",orderState);  
        param.put("RSRV_STR2",rsrvStr2); 
        return Dao.qryByCode("TF_F_TERMINALORDER", "SEL_ORDER_FOR_DEAL", param);
    }
    
    /**
     * REQ201604180021 网上营业厅合约终端销售价格调整需求
     * chenxy3 20160428 
     */
    public static IDataset qryTerminalOrderInfoForCheck(String orderId,String productId,String packageId,String serialNumber,String orderState,String rsrvStr2) throws Exception {
        IData param = new DataMap();
        param.put("NET_ORDER_ID", orderId);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ORDER_STATE",orderState);  
        param.put("RSRV_STR2",rsrvStr2); 
        return Dao.qryByCode("TF_F_TERMINALORDER", "SEL_ORDER_FOR_CHECK", param);
    }
    /**
     *REQ201604180021 网上营业厅合约终端销售价格调整需求
     * chenxy3 20160428 
     * */
    public static IDataset qryLabelByProdId(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" SELECT A.* ");
        parser.addSQL(" FROM TD_B_ELEMENT_LABEL A ");
        parser.addSQL(" WHERE A.ELEMENT_ID = :PRODUCT_ID ");
        parser.addSQL(" AND A.ELEMENT_TYPE_CODE = 'P' ");
        parser.addSQL(" AND A.STATE = '1'"); 

        return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
    }
}