
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryTypeSmsQry
{

    public static IDataset queryTdbSmsDef(String smstype) throws Exception
    {
        IData params = new DataMap();
        params.put("SMS_TYPE", smstype);
        SQLParser parser = new SQLParser(params);

        parser.addSQL("SELECT A.SMS_TYPE_CODE,A.SMS_STAFF_ID,A.SMS_NAME");
        parser.addSQL(" FROM UCR_CEN1.TD_B_SMS_DEF A ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.SMS_CODE = :SMS_TYPE ");
        return Dao.qryByParse(parser);
    }

    public static IDataset queryTiOhSms(String month, String serialnumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialnumber);
        SQLParser parser = new SQLParser(params);

        parser.addSQL("SELECT B.SMS_NOTICE_ID ,'10086' USE_NUMBER ,B.RECV_OBJECT  ,B.NOTICE_CONTENT ,TO_CHAR(B.REFER_TIME,'YYYY-MM-DD HH24:MI:SS') REFER_TIME ,B.SMS_TYPE_CODE ,B.REFER_STAFF_ID ");
        parser
                .addSQL(" ,DECODE(B.DEAL_STATE,'0','已处理','1','消息结构错','2','命令字错','3','消息序号重复','4','消息长度错','5','资费代码错','6','超过最大信息长','7','业务代码错','8','流量控制错','9','本网关不负责服务此计费号码','10','Src_Id错误','11','Msg_src错误','12','Fee_terminal_Id错误','13','Dest_terminal_Id错误','14','消息结构错','15','未处理','16','超时未处理','17','超时已处理','18','短信待发送','19','短信等待响应包',B.DEAL_STATE) DEAL_STATE ");
        parser.addSQL(" FROM UCR_UEC.TI_OH_SMS_");
        parser.addSQL(month);
        parser.addSQL(" B WHERE 1=1 ");
        parser.addSQL(" AND B.RECV_OBJECT = :SERIAL_NUMBER ");
        parser.addSQL(" ORDER BY B.REFER_TIME ");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryTypeCode(IData input) throws Exception
    {
        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT DISTINCT T.SMS_CODE, T.SMS_NAME FROM UCR_CEN1.TD_B_SMS_DEF T WHERE 1 = 1");
        return Dao.qryByParse(parser);
    }
}
