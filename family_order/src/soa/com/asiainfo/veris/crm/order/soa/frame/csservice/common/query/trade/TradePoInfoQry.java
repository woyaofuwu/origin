
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;

public class TradePoInfoQry
{
    /**
     * 查询集团工单
     */
    public static IDataset orderInfoQry(String OPERATIONSUBTYPEID, String START_DATE, String END_DATE, Pagination pagination) throws Exception
    {
        IData para = new DataMap();
        para.put("OPERATIONSUBTYPEID", OPERATIONSUBTYPEID);
        para.put("START_DATE", START_DATE);
        para.put("END_DATE", END_DATE);
        SQLParser parser = new SQLParser(para);

        parser.addSQL("SELECT T.TRADE_ID,");
        parser.addSQL("       T.TRADE_STATE,");
        parser.addSQL("       T.ACCEPT_MONTH,");
        parser.addSQL("       T.PRODUCTORDERNUMBER,");
        parser.addSQL("       T.PRODUCTSPECNUMBER,");
        parser.addSQL("       T.PRIACCESSNUMBER,");
        parser.addSQL("       T.LINKMAN,");
        parser.addSQL("       T.ORDERTYPE,");
        parser.addSQL("       DECODE(T.ORDERTYPE, 1, '正常下达', 2, '撤销工单') AS ORDERTYPENAME,");
        parser.addSQL("       T.CONTACTPHONE,");
        parser.addSQL("       T.TERMINALCONFIRM,");
        parser.addSQL("       T.DESCRIPTION,");
        parser.addSQL("       DECODE(T.TERMINALCONFIRM, 0, '不需要', 1, '需要') AS TERMINALCONFIRMNAME,");
        parser.addSQL("       T.SERVICELEVELID,");
        parser.addSQL("       T.OPERATIONSUBTYPEID,");
        parser.addSQL("       T.RSRV_STR5");
        parser.addSQL("  FROM TF_B_POTRADE T");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL("   AND T.OPERATIONSUBTYPEID = :OPERATIONSUBTYPEID");
        parser.addSQL("   AND T.ACCEPT_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        parser.addSQL("   AND T.ACCEPT_DATE > TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        parser.addSQL("   AND T.PRODUCTORDERNUMBER = :PRODUCTORDERNUMBER");
        parser.addSQL("   AND T.PRODUCTSPECNUMBER = :PRODUCTSPECNUMBER");
        parser.addSQL("   AND T.TRADE_STATE = '0'");

        IDataset resultset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

        for (int i = 0; i < resultset.size(); i++)
        {
            IData result = resultset.getData(i);
            result.put("OPERATIONSUBTYPENAME", StaticInfoQry.getStaticInfoByTypeIdDataId("OPERATIONSUBTYPEID", result.getString("OPERATIONSUBTYPEID")).getString("DATA_NAME"));
            result.put("TRADE_STATE_NAME", StaticInfoQry.getStaticInfoByTypeIdDataId("BBOSS_TRADE_STATE", result.getString("TRADE_STATE")).getString("DATA_NAME"));
            //result.put("PRODUCTSPECNAME", PoProductQry.getProductSpecNameByProductSpecNumber(result.getString("PRODUCTSPECNUMBER")));
            String mpGrpCustCode = result.getString("RSRV_STR5", "");
            if(StringUtils.isNotBlank(mpGrpCustCode)){
            	IDataset ids = GrpInfoQry.queryGrpInfoByGrpCustCode(mpGrpCustCode);
            	if(IDataUtil.isNotEmpty(ids)){
            		result.put("CUST_NAME", ids.getData(0).getString("CUST_NAME", ""));
            		result.put("GROUP_ID", ids.getData(0).getString("CUST_NAME", ""));
            		result.put("CUST_ID", ids.getData(0).getString("CUST_NAME", ""));
            	}
            }
            
        }

        return resultset;
    }

    /**
     * 查询成员工单
     */
    public static IDataset orderMemQry(String START_DATE, String END_DATE, Pagination pagination) throws Exception
    {
        IData para = new DataMap();
        para.put("START_DATE", START_DATE);
        para.put("END_DATE", END_DATE);

        SQLParser parser = new SQLParser(para);
        parser.addSQL("SELECT trade_id,DECODE(trade_state,'0','未处理','1','营业已经处理','2','已同步竣工报文','未知状态') trade_state,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,accept_month,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,productordernumber,ordertype,productspecnumber,accessnumber,priaccessnumber,linkman,contactphone,description,servicelevelid,DECODE(terminalconfirm,'0','不需要','1','需要','工单错误') terminalconfirm,DECODE(operationsubtypeid,'1','业务开通','2','业务取消','6','变更成员','工单错误') operationsubtypeid,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,to_char(a.rsrv_num1) rsrv_num1,to_char(a.rsrv_num2) rsrv_num2,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,a.remark ,b.membernumber rsrv_str1 ");
        parser.addSQL("  FROM tf_b_potrade a, UOP_UIF1.TI_B_BBOSSMEMBERDATA_UDR b");
        parser.addSQL("   WHERE a.productordernumber=b.ordernumber and a.ACCEPT_DATE<to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') and a.ACCEPT_DATE>to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 一级BBOSS业务成员订购处理查询 关联查询拆除SQL---处理失败
     * 
     * @author liuxx3
     * @date 2014 -07-14
     */
    public static IDataset qryReslutExistByProOfferId(String product_offer_id, String serial_number, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("PROCUDT_OFFER_ID", product_offer_id);
        param.put("SERIAL_NUMBER", serial_number);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT i.MEMBERNUMBER, ");
        parser.addSQL("        i.PRODUCTID, ");
        parser.addSQL("        i.RSRV_STR7, ");
        parser.addSQL("        i.RSRV_STR4  ");
        parser.addSQL("  FROM UOP_UIF1.TI_B_BBOSSMEMBERDATA_UDR i ");
        parser.addSQL(" WHERE i.RSRV_STR4 != '00' ");
        parser.addSQL("   AND i.RSRV_STR7 != '00' ");
        parser.addSQL("   AND i.MEMBERNUMBER = :SERIAL_NUMBER ");
        parser.addSQL("   AND i.PRODUCTID = :PRODUCT_OFFER_ID ");

        return Dao.qryByParse(parser, pg, Route.CONN_CRM_CEN);
    }

    /**
     * chenyi
     * 2015-1-27
     * 查询工单记录表是否有对应的成功处理记录
     * @param OrderNumber
     * @param dealState
     * @return
     * @throws Exception
     */
    public   static  IDataset qryProNumberInfo( String OrderNumber) throws Exception{

        IData param = new DataMap();
        param.put("ORDERNUMBER", OrderNumber);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT SEQ_ID,ORDERNUMBER,DEAL_STATE,UPDATE_TIME ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("RSRV_STR6, ");
        sql.append("RSRV_STR7, ");
        sql.append("RSRV_STR8, ");
        sql.append("RSRV_STR9, ");
        sql.append("RSRV_STR10 ");
        sql.append("FROM TF_B_PO_PRONUMBER t ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND t.ORDERNUMBER= :ORDERNUMBER  ");
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    
}
