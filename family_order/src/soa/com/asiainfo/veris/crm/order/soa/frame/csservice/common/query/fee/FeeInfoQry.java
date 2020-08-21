
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FeeInfoQry
{

    /**
     * 查询一次性费用信息
     * 
     * @param groupId
     *            集团编码
     * @param startDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @return
     * @throws Exception
     */
    public static IDataset qryOneOffFeeList(String groupId, String startDate, String endDate) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("GROUP_ID", groupId);
        paramData.put("START_DATE", startDate);
        paramData.put("END_DATE", endDate);

        SQLParser parser = new SQLParser(paramData);

        parser.addSQL("select f.flowid,");
        parser.addSQL("		  to_char(f.accept_time, 'yyyy-mm-dd hh24:mi:ss') accept_time,");
        parser.addSQL("		  fb.fee_type_code,");
        parser.addSQL("		  fb.rsrv_str1,");
        parser.addSQL("		  fb.rsrv_str2,");
        parser.addSQL("		  fb.rsrv_str3,");
        parser.addSQL("		  fb.rsrv_str4,");
        parser.addSQL("		  fb.rsrv_str5,");
        parser.addSQL("		  fb.fee,");
        parser.addSQL("		  fb.fee_type_code,");
        parser.addSQL("		  fb.rsrv_str1,");
        parser.addSQL("		  fb.rsrv_str2,");
        parser.addSQL("		  fb.rsrv_str3,");
        parser.addSQL("		  fb.rsrv_str4,");
        parser.addSQL("		  fb.rsrv_str5,");
        parser.addSQL("		  fb.fee,");
        parser.addSQL("		  fb.note_id");
        parser.addSQL("	 from tf_b_grp_oneoff_fee f, tf_b_grp_oneoff_fee_sub fb");
        parser.addSQL("	where f.group_id = :GROUP_ID");
        parser.addSQL("	  and f.flowid = fb.flowid");
        parser.addSQL("	  and f.rsrv_tag1 = '0'");
        parser.addSQL("	  and f.accept_time >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')");
        parser.addSQL("	  and f.accept_time <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')");

        return Dao.qryByParse(parser);
    }
    /**
     * 查询集团一次性费用(移动云)
     * @param groupId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset qryOneOffFeeCloudList(String groupId, String startDate, String endDate) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("GROUP_ID", groupId);
        paramData.put("START_TIME", startDate);
        paramData.put("END_TIME", endDate);

        SQLParser parser = new SQLParser(paramData);

        parser.addSQL("select f.flowid,");
        parser.addSQL("		  to_char(f.accept_time, 'yyyy-mm-dd hh24:mi:ss') accept_time,");
        parser.addSQL("		  fb.fee_type_code,");
        parser.addSQL("		  fb.rsrv_str1,");
        parser.addSQL("		  fb.rsrv_str2,");
        parser.addSQL("		  fb.rsrv_str3,");
        parser.addSQL("		  fb.rsrv_str4,");
        parser.addSQL("		  fb.rsrv_str5,");
        parser.addSQL("		  fb.rsrv_str6,");
        parser.addSQL("		  fb.fee,");
        parser.addSQL("		  fb.fee_type_code,");
        parser.addSQL("		  fb.note_id");
        parser.addSQL("	 from tf_b_grp_oneoff_fee f, tf_b_grp_oneoff_fee_sub fb");
        parser.addSQL("	where f.group_id = :GROUP_ID");
        parser.addSQL("	  and f.flowid = fb.flowid");
        parser.addSQL("	  and f.rsrv_tag1 = '1'");
        parser.addSQL("	  and f.accept_time >= to_date(:START_TIME, 'yyyy-mm-dd hh24:mi:ss')");
        parser.addSQL("	  and f.accept_time <= to_date(:END_TIME, 'yyyy-mm-dd hh24:mi:ss')");

        return Dao.qryByParse(parser);
    }
    
    /**
	 * 查询用户缴费数据记录信息
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset getReplenishBySn(IData data) throws Exception
	{
		return Dao.qryByCode("TO_O_REPLENISH", "SEL_BY_SN_TIME", data, Route.CONN_CRM_CEN);
	}
}
