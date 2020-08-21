
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PotradeInfoQry
{
    // todo
    /**
     * 查询集团工单
     */
    public static IDataset orderInfoQry(String operationsubtypeid, String endDate, String startDate, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("OPERATIONSUBTYPEID", operationsubtypeid);
        param.put("END_DATE", endDate);
        param.put("START_DATE", startDate);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select * from TF_B_POTrade ");
        parser.addSQL("where  operationsubtypeid =:OPERATIONSUBTYPEID");
        parser.addSQL(" and ACCEPT_DATE<to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') and ACCEPT_DATE>to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') and trade_state = '0' ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    // todo
    /**
     * 查询成员工单
     */
    public static IDataset orderMemQry(String endDate, String startDate, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("END_DATE", endDate);
        param.put("START_DATE", startDate);

        SQLParser parser = new SQLParser(param);
        parser
                .addSQL("SELECT trade_id,DECODE(trade_state,'0','未处理','1','营业已经处理','2','已同步竣工报文','未知状态') trade_state,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,accept_month,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,productordernumber,ordertype,productspecnumber,accessnumber,priaccessnumber,linkman,contactphone,description,servicelevelid,DECODE(terminalconfirm,'0','不需要','1','需要','工单错误') terminalconfirm,DECODE(operationsubtypeid,'1','业务开通','2','业务取消','6','变更成员','工单错误') operationsubtypeid,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,to_char(a.rsrv_num1) rsrv_num1,to_char(a.rsrv_num2) rsrv_num2,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,a.remark ,b.membernumber rsrv_str1 ");
        parser.addSQL("  FROM tf_b_potrade a, UOP_UIF1.TI_B_BBOSSMEMBERDATA_UDR b");
        parser.addSQL("   WHERE a.productordernumber=b.ordernumber and a.ACCEPT_DATE<to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') and a.ACCEPT_DATE>to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
}
