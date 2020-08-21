
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.flow;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.util.SqlUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class FlowInfoQry
{
    public static boolean isNull(String p_str)
    {
        if (p_str != null && !p_str.trim().equals(""))
            return false;
        else
            return true;
    }
    
    /**
	 * 根据代理商编码查代理商信息
	 * @param pd
	 * @param channelCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryChannel(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT D.DEPART_ID,D.DEPART_CODE,D.DEPART_NAME FROM TD_M_DEPART D  WHERE 1=1 ");
		parser.addSQL(" AND D.DEPART_CODE = :CHANNEL_CODE ");
		parser.addSQL(" AND D.DEPART_ID = :CHANNEL_ID ");
		parser.addSQL(" AND D.DEPART_NAME LIKE '%'||:CHANNEL_NAME||'%' ");
		parser.addSQL(" AND EXISTS (SELECT 1 FROM TD_M_DEPARTKIND K WHERE K.CODE_TYPE_CODE = '0' AND K.EPARCHY_CODE=D.RSVALUE2 AND K.DEPART_KIND_CODE=D.DEPART_KIND_CODE )");
		return Dao.qryByParse(parser, Route.CONN_SYS);
	}
	
	/**
	 * 根据代理商编码查代理商信息
	 * @param pd
	 * @param channelCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryChannel(String channelCode) throws Exception
	{
		IData param = new DataMap();
		param.put("CHANNEL_CODE", channelCode);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("select d.depart_id,d.depart_code,d.depart_name from td_m_depart d  WHERE 1=1 ");
		parser.addSQL(" AND d.depart_code = :CHANNEL_CODE");
		parser.addSQL(" AND d.depart_id = :CHANNEL_ID");
		parser.addSQL(" AND d.depart_name like '%'||:CHANNEL_NAME||'%'");
		parser.addSQL(" AND EXISTS (SELECT 1 FROM td_m_departkind k WHERE k.code_type_code = '0' AND k.eparchy_code=d.rsvalue2 AND k.depart_kind_code=d.depart_kind_code )");
		return Dao.qryByParse(parser, Route.CONN_SYS);
	}
    
	/**
	 * 查渠道的联系电话，用于接收短信
	 * @param pd
	 * @param channelId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getChannelLinkPhone(String channelId) throws Exception
	{
		IData param = new DataMap();
		param.put("CHNL_ID", channelId);
		return Dao.qryByCode("TF_CHL_ACCT", "SEL_BY_CHNL_ID", param ,Route.CONN_CRM_CG);
	}
    
	/**
	 * 查询代理商流量
	 * @data 2014-3-19
	 * @param pd
	 * @param channelId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserDatapckStock(String channelId) throws Exception
	{
		IData param = new DataMap();
		param.put("ID", channelId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.* FROM TF_F_CUST_DATAPCK A WHERE ID = :ID AND SYSDATE BETWEEN START_DATE  AND END_DATE ");
		return Dao.qryByParse(parser);
	}
	
	/**
	 * 获得流量包信息根据流量包ID
	 * @data 2014-3-19
	 * @param pd
	 * @param datapckId
	 * @return
	 * @throws Exception
	 */
	public static IData getDatapckTrafficByDatapckId(String datapckId) throws Exception
	{
		IData param = new DataMap();
		param.put("DATAPCK_ID", datapckId);
		return Dao.qryByPK("TD_B_DATAPCK_TRAFFIC", param, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 获得流量包信息根据流量包ID
	 * @data 2014-3-19
	 * @param pd
	 * @param datapckId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDatapckTrafficPriv(String staff_id) throws Exception
	{
		
		IData param = new DataMap();
		param.put("TRADE_STAFF_ID", staff_id);
		return Dao.qryByCode("TD_B_DATAPCK_TRAFFIC", "SEL_ALL_TRAFFICS", param ,Route.CONN_CRM_CEN);
	}
	
	/**
	 * 流量包定义表查询
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDatapckTraffic(IData param) throws Exception{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.* FROM TD_B_DATAPCK_TRAFFIC A WHERE 1 = 1");
		return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
	
	/**
	 * 查客户库存
	 * @param pd
	 * @param isntId
	 * @return
	 * @throws Exception
	 */
	public static IData getCustDatapckStock(String isntId) throws Exception
	{
		IData param = new DataMap();
		param.put("INST_ID", isntId);
		return Dao.qryByPK("TF_F_CUST_DATAPCK", param,Route.CONN_CRM_CG);
	}
	
	/**
	 * 查询已订购流量包(cy)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryOrderedPackge(IData params,Pagination pg) throws Exception
	{
		SQLParser parser = new SQLParser(params);
		parser.addSQL("select  t1.stock_value,t1.stock_count,(t.datapck_count-t1.stock_count) use_count,t.inst_id,t.id,t.id_type,t.datapck_type," +
				"t.datapck_value,t.datapck_count,t.price,t.total_fee,t.start_date,t.end_date,t.rsrv_str1,t.rsrv_str2 ");
		parser.addSQL(" from tf_f_cust_datapck t,TF_F_CUST_DATASTOCK t1");
		parser.addSQL(" where  1=1 ");
		parser.addSQL(" and t.inst_id=t1.inst_id ");
		parser.addSQL(" and t.id=:CUST_ID ");
		parser.addSQL(" and t.end_date>sysdate ");
		parser.addSQL(" and t.datapck_type='D' ");
		parser.addSQL(" order by t.start_date desc ");
		return Dao.qryByParse(parser, pg);

	}
	
	/**
	 * 查询已订购流量包(cy)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryGroupFlowOrderDetail(IData params,Pagination pg) throws Exception
	{
		SQLParser parser = new SQLParser(params);
		parser.addSQL("select t.id,t.serial_number_b,t.datapck_value,t.datapck_count,t.start_date,t.end_date,t.update_time,t.rsrv_str1 ");
		parser.addSQL(" from tf_f_cust_meb_outstock t ");
		parser.addSQL(" where  1=1 ");
		parser.addSQL(" and t.id=:CUST_ID ");
		parser.addSQL(" and t.rela_inst_id=:RELA_INST_ID ");
		parser.addSQL(" and t.end_date>sysdate ");
		return Dao.qryByParse(parser, pg);

	}  
	
	   /**
     * 查客户订购的流量包
     * @param pd
     * @param isntId
     * @return
     * @throws Exception
     */
    public static IDataset getCustDatapckByIDValue(String custId , String pckValue , String pckMoney) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", custId);
        param.put("DATAPCK_VALUE", pckValue);
        param.put("PRICE", pckMoney);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.* FROM TF_F_CUST_DATAPCK A WHERE ID = :ID AND DATAPCK_VALUE = :DATAPCK_VALUE AND PRICE = :PRICE ");
        parser.addSQL(" AND SYSDATE BETWEEN START_DATE  AND END_DATE  ORDER BY END_DATE, START_DATE ");
        return Dao.qryByParse(parser,Route.CONN_CRM_CG);
    }
            
    
    /**
     * 查客户订购的流量包余额
     * @param pd
     * @param isntId
     * @return
     * @throws Exception
     */
    public static IDataset getCustDatapckStock(String custId , String instId) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", custId);
        param.put("INST_ID", instId);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.* FROM TF_F_CUST_DATASTOCK A WHERE ID = :ID  AND INST_ID = :INST_ID ");
        return Dao.qryByParse(parser,Route.CONN_CRM_CG);
    }
       
    /**
	 *更新流量包余额
	 * @param pd
	 * @param isntId
	 * @return
	 * @throws Exception
	 */ 
    public static void updateCustDatapckStock(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL("UPDATE TF_F_CUST_DATASTOCK A SET A.STOCK_VALUE = :STOCK_VALUE , ");
        parser.addSQL(" STOCK_COUNT = :STOCK_COUNT ,UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-MM-dd hh24:mi:ss'), ");
        parser.addSQL(" UPDATE_STAFF_ID = :UPDATE_STAFF_ID, UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        parser.addSQL(" WHERE ID = :ID AND INST_ID = :INST_ID ");
        Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    } 
    /**
     * 查询“集团电子流量包”产品
     * @REQ201702100017虚拟流量包优化需求
     * @param params
     * @param pg
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-2-24
     */
    public static IDataset queryOrderedGprsPrdId(String groupId, Pagination pg) throws Exception
	{
    	IData params = new DataMap();
    	params.put("GROUP_ID", groupId);
		SQLParser parser = new SQLParser(params);
		parser.addSQL(SqlUtil.trimSql("SELECT CG.GROUP_ID, US.SERIAL_NUMBER, US.USER_ID, CG.CUST_ID			"));
		parser.addSQL(SqlUtil.trimSql("  FROM TF_F_CUST_GROUP CG, TF_F_USER US                              "));
		parser.addSQL(SqlUtil.trimSql(" WHERE CG.CUST_ID = US.CUST_ID                                       "));
		parser.addSQL(SqlUtil.trimSql("   AND CG.GROUP_ID = :GROUP_ID                                       "));
		parser.addSQL(SqlUtil.trimSql("   AND CG.REMOVE_TAG = '0'                                           "));
		parser.addSQL(SqlUtil.trimSql("   AND US.REMOVE_TAG = '0'                                           "));
		parser.addSQL(SqlUtil.trimSql("   AND EXISTS (SELECT 1                                              "));
		parser.addSQL(SqlUtil.trimSql("          FROM TF_F_USER_PRODUCT UP                                  "));
		parser.addSQL(SqlUtil.trimSql("         WHERE UP.PARTITION_ID = US.PARTITION_ID                     "));
		parser.addSQL(SqlUtil.trimSql("           AND UP.MAIN_TAG = '1'                                     "));
		parser.addSQL(SqlUtil.trimSql("           AND UP.USER_ID = US.USER_ID                               "));
		parser.addSQL(SqlUtil.trimSql("           AND SYSDATE BETWEEN UP.START_DATE AND UP.END_DATE         "));
		parser.addSQL(SqlUtil.trimSql("           AND UP.PRODUCT_ID = 5201)                                 "));
		return Dao.qryByParse(parser, pg);
	}
    /**
     * 根据业务流水查询本次本次流量包充值的总卡面金额、总销售金额
     * @REQ201702100017虚拟流量包优化需求
     * @param tradeId
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-1
     */
    public static IDataset queryGrpFlowPackTransFee(String tradeId, String userIda) throws Exception
	{
    	IData params = new DataMap();
    	params.put("TRADE_ID", tradeId);
    	params.put("USER_ID", userIda);
		SQLParser parser = new SQLParser(params);
		parser.addSQL(SqlUtil.trimSql("SELECT MAX(X1.USER_ID) GRP_USER_ID,															"));
		parser.addSQL(SqlUtil.trimSql("       MAX(U.SERIAL_NUMBER) SERIAL_NUMBER,                                                   "));
		parser.addSQL(SqlUtil.trimSql("       SUM(X1.FACE_VALUE) FACE_VALUE,                                                        "));
		parser.addSQL(SqlUtil.trimSql("       SUM(X1.SAIL_VALUE) SAIL_VALUE                                                         "));
		parser.addSQL(SqlUtil.trimSql("  FROM (SELECT A.ID,                                                                         "));
		parser.addSQL(SqlUtil.trimSql("               A.USER_ID,                                                                    "));
		parser.addSQL(SqlUtil.trimSql("               A.PRICE,                                                                      "));
		parser.addSQL(SqlUtil.trimSql("               A.RSRV_STR2,                                                                  "));
		parser.addSQL(SqlUtil.trimSql("               B.DATAPCK_COUNT,                                                              "));
		parser.addSQL(SqlUtil.trimSql("               (A.PRICE * B.DATAPCK_COUNT) FACE_VALUE,                                       "));
		parser.addSQL(SqlUtil.trimSql("               (A.PRICE * (NVL(A.RSRV_STR2, 100) / 100) * B.DATAPCK_COUNT) SAIL_VALUE        "));
		parser.addSQL(SqlUtil.trimSql("          FROM TF_F_CUST_DATAPCK A, TF_B_TRADE_MEB_OUTSTOCK B                                "));
		parser.addSQL(SqlUtil.trimSql("         WHERE A.INST_ID = B.RELA_INST_ID                                                    "));
		parser.addSQL(SqlUtil.trimSql("           AND B.TRADE_ID = :TRADE_ID) X1,                                                   "));
		parser.addSQL(SqlUtil.trimSql("       TF_F_USER U                                                                           "));
		parser.addSQL(SqlUtil.trimSql(" WHERE X1.ID = U.CUST_ID                                                                     "));
		parser.addSQL(SqlUtil.trimSql("   AND U.USER_ID = :USER_ID                                                                  "));
		parser.addSQL(SqlUtil.trimSql("   AND U.REMOVE_TAG = '0'                                                                    "));
		return Dao.qryByParse(parser);
	}
    /**
     * 修改集团流量包有效期
     * @REQ201702100017虚拟流量包优化需求
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-2
     */
    public static void updateCustDataPckContinue(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(SqlUtil.trimSql("UPDATE TF_F_CUST_DATAPCK A										"));
        parser.addSQL(SqlUtil.trimSql("   SET A.END_DATE         = TO_DATE(:END_DATE, 'yyyy-mm-dd'),    "));
        parser.addSQL(SqlUtil.trimSql("       A.UPDATE_TIME      = SYSDATE,                             "));
        parser.addSQL(SqlUtil.trimSql("       A.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,                    "));
        parser.addSQL(SqlUtil.trimSql("       A.UPDATE_DEPART_ID = :UPDATE_DEPART_ID                    "));
        parser.addSQL(SqlUtil.trimSql(" WHERE A.INST_ID = :INST_ID                                      "));
        Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }
    /**
     * 修改集团流量包库存
     * @REQ201702100017虚拟流量包优化需求
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-2
     */
    public static void updateCustDataStockContinue(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(SqlUtil.trimSql("UPDATE TF_F_CUST_DATASTOCK A										"));
        parser.addSQL(SqlUtil.trimSql("   SET A.END_DATE         = TO_DATE(:END_DATE, 'yyyy-mm-dd'),    "));
        parser.addSQL(SqlUtil.trimSql("       A.UPDATE_TIME      = SYSDATE,                             "));
        parser.addSQL(SqlUtil.trimSql("       A.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,                    "));
        parser.addSQL(SqlUtil.trimSql("       A.UPDATE_DEPART_ID = :UPDATE_DEPART_ID                    "));
        parser.addSQL(SqlUtil.trimSql(" WHERE A.INST_ID = :INST_ID                                      "));
        Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }
    /**
     * 根据Inst_id查询流量包信息
     * @REQ201702100017虚拟流量包优化需求
     * @param params
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-3
     */
    public static IDataset qryGrpFlowInfoForContinueByInstId(String instId) throws Exception
	{
    	IData params = new DataMap();
    	params.put("INST_ID", instId);
		SQLParser parser = new SQLParser(params);
		parser.addSQL(SqlUtil.trimSql("SELECT T1.STOCK_VALUE,									"));
		parser.addSQL(SqlUtil.trimSql("       T1.STOCK_COUNT,                                   "));
		parser.addSQL(SqlUtil.trimSql("       (T.DATAPCK_COUNT - T1.STOCK_COUNT) USE_COUNT,     "));
		parser.addSQL(SqlUtil.trimSql("       T.*                                               "));
		parser.addSQL(SqlUtil.trimSql("  FROM TF_F_CUST_DATAPCK T, TF_F_CUST_DATASTOCK T1       "));
		parser.addSQL(SqlUtil.trimSql(" WHERE 1 = 1                                             "));
		parser.addSQL(SqlUtil.trimSql("   AND T.INST_ID = T1.INST_ID                            "));
		parser.addSQL(SqlUtil.trimSql("   AND T.INST_ID = :INST_ID                              "));
		parser.addSQL(SqlUtil.trimSql("   AND T.END_DATE > SYSDATE                              "));
		parser.addSQL(SqlUtil.trimSql("   AND T.DATAPCK_TYPE = 'D'                              "));
		return Dao.qryByParse(parser);
	}  
    /**
     * 查询集团电子流量包延期日志
     * @REQ201702100017虚拟流量包优化需求
     * @param params
     * @param pg
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-3
     */
    public static IDataset qryGrpsContinueLogs(IData params,Pagination pg) throws Exception
	{
		SQLParser parser = new SQLParser(params);
		parser.addSQL(SqlUtil.trimSql("SELECT GRP.GROUP_ID,														"));
		parser.addSQL(SqlUtil.trimSql("       GRP.CUST_NAME,                                                    "));
		parser.addSQL(SqlUtil.trimSql("       A.ORDER_TRADE_ID,                                                 "));
		parser.addSQL(SqlUtil.trimSql("       A.CHANGE_TRADE_ID,                                                "));
		parser.addSQL(SqlUtil.trimSql("       A.INST_ID,                                                        "));
		parser.addSQL(SqlUtil.trimSql("       A.CUST_ID,                                                        "));
		parser.addSQL(SqlUtil.trimSql("       A.DATAPACK_VALUE,                                                 "));
		parser.addSQL(SqlUtil.trimSql("       A.CONTI_COUNT,                                                    "));
		parser.addSQL(SqlUtil.trimSql("       TO_CHAR(A.OLD_END_DATE, 'yyyy-mm-dd hh24:mi:ss') OLD_END_DATE,    "));
		parser.addSQL(SqlUtil.trimSql("       TO_CHAR(A.NEW_END_DATE, 'yyyy-mm-dd hh24:mi:ss') NEW_END_DATE,    "));
		parser.addSQL(SqlUtil.trimSql("       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,      "));
		parser.addSQL(SqlUtil.trimSql("       A.UPDATE_STAFF_ID,                                                "));
		parser.addSQL(SqlUtil.trimSql("       A.UPDATE_DEPART_ID,                                               "));
		parser.addSQL(SqlUtil.trimSql("       A.REMARK,                                                         "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR1,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR2,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR3,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR4,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR5,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR6,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR7,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR8,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR9,                                                      "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR10                                                      "));
		parser.addSQL(SqlUtil.trimSql("  FROM TF_F_CUST_DATAPACK_CHGLOG A, TF_F_CUST_GROUP GRP                  "));
		parser.addSQL(SqlUtil.trimSql(" WHERE A.CUST_ID = GRP.CUST_ID                                           "));
		parser.addSQL(SqlUtil.trimSql("   AND GRP.GROUP_ID = :GROUP_ID                                          "));
		parser.addSQL(SqlUtil.trimSql("   AND A.UPDATE_STAFF_ID = :STAFF_ID                                     "));
		parser.addSQL(SqlUtil.trimSql("   AND A.UPDATE_TIME BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd') AND      "));
		parser.addSQL(SqlUtil.trimSql("       TO_DATE(:END_DATE || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')        "));
		return Dao.qryByParse(parser, pg);
	}
    /**
     * 查询集团电子流量包数据一致性比对结果
     * @REQ201702100017虚拟流量包优化需求
     * @param params
     * @param pg
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-9
     */
    public static IDataset qryGrpsCompareLogs(IData params,Pagination pg) throws Exception
	{
		SQLParser parser = new SQLParser(params);
		parser.addSQL(SqlUtil.trimSql("SELECT GRP.GROUP_ID,													"));
		parser.addSQL(SqlUtil.trimSql("       GRP.CUST_NAME,                                                "));
		parser.addSQL(SqlUtil.trimSql("       (A.PACK_PRICE / 100) PACK_PRICE,                              "));
		parser.addSQL(SqlUtil.trimSql("       A.PACK_VALUE,                                                 "));
		parser.addSQL(SqlUtil.trimSql("       A.STOCK_COUNT,                                                "));
		parser.addSQL(SqlUtil.trimSql("       A.PLAT_COUNT,                                                 "));
		parser.addSQL(SqlUtil.trimSql("       TO_CHAR(A.IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE,          "));
		parser.addSQL(SqlUtil.trimSql("       A.REMARK,                                                     "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR1,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR2,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR3,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR4,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR5,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR6,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR7,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR8,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR9,                                                  "));
		parser.addSQL(SqlUtil.trimSql("       A.RSRV_STR10                                                  "));
		parser.addSQL(SqlUtil.trimSql("  FROM TF_F_CUST_DATASTOCK_COMPARE A, TF_F_CUST_GROUP GRP            "));
		parser.addSQL(SqlUtil.trimSql(" WHERE A.GROUP_ID = GRP.GROUP_ID                                     "));
		parser.addSQL(SqlUtil.trimSql("   AND GRP.GROUP_ID = :GROUP_ID                                      "));
		parser.addSQL(SqlUtil.trimSql("   AND A.IN_DATE BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd') AND      "));
		parser.addSQL(SqlUtil.trimSql("       TO_DATE(:END_DATE || ' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')    "));
		return Dao.qryByParse(parser, pg);
	}
}
