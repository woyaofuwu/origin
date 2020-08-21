/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.biz.bean.BizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 上午11:17:14
 */
public class ValueCardInfoQry
{

    public static IDataset getgetWorkOrders(String city_id) throws Exception
    {
    	//不显示已经过期的工单  需求优化修改
        String sql = " SELECT T.* FROM TF_M_VALUECARD_SUM_APPROVAL T where 1=1 ";

        String citys = "HAIN,HNSJ,0898,HNHN";

        IData param = new DataMap();
        if (!citys.contains(city_id))
        {
            sql += "and T.AREA_CODE = :AREA_CODE ";
            param.put("AREA_CODE", city_id);
        }
        sql += "and T.RSRV_DATE1> sysdate ";
        
        return Dao.qryBySql(new StringBuilder(sql), param);

    }

    public static IDataset getStaffValueCardAudit(String staffId) throws Exception
    {
        String sql = " SELECT SUM(T.FEE) AS FEE FROM TD_B_VALUECARD_AUDIT  T WHERE T.STAFF_ID=:STAFF_ID ";

        IData param = new DataMap();
        param.put("STAFF_ID", staffId);

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    /**
     * 根据rowid查询记录
     * 
     * @param param
     * @throws Exception
     */
    public static IDataset queryCanGiveValueCardInfo(String rowId) throws Exception
    {
        String sql = "SELECT  AREA_CODE, STAFF_ID,TOTAL_AMOUNT/100 TOTAL_AMOUNT,OPERA_STAFF_ID,COMMIT_TIME,BALANCE,AMOUNTS,REMOVE_TAG,RSRV_STR2 as REMARK,ROWID ROW_ID " + "FROM tf_m_valuecard_sum_approval WHERE ROWID=:ROWID ";
        IData param = new DataMap();
        param.put("ROWID", rowId);

        if (StringUtils.isNotBlank(rowId))
        {
            return Dao.qryBySql(new StringBuilder(sql), param);
        }
        else
        {
            return new DatasetList();
        }

    }

    /**
     * 根据工号查询记录是否存在
     * 
     * @param staffID
     * @return
     * @throws Exception
     */
    public static IDataset queryCanGiveValueCardInfoByStaffID(String staffID) throws Exception
    {
        String sql = "SELECT 1 FROM tf_m_valuecard_sum_approval WHERE STAFF_ID =:STAFF_ID";
        IData param = new DataMap();
        param.put("STAFF_ID", staffID);

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    /**
     * 根据查询条件查询记录
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-5
     */
    public static IDataset queryCanGiveValueCardInfos(IData param, Pagination pagination) throws Exception
    {
    	/**
         * REQ201601070003 关于优化有价卡赠送审批工单配置界面的需求
         * chenxy3
         * 20160108
         * */
        String endDate=param.getString("END_DATE","");
        if(!"".equals(endDate)){
        	endDate=endDate+" 23:59:59";
        	param.put("END_DATE", endDate);
        }
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  t.AREA_CODE, t.STAFF_ID,t.TOTAL_AMOUNT,t.OPERA_STAFF_ID,TO_CHAR(t.COMMIT_TIME,'YYYY-MM-DD HH24:MI:SS') COMMIT_TIME,t.AMOUNTS,t.REMOVE_TAG ,TO_CHAR(T.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') as END_TIME,t.RSRV_STR2 as REMARK,t.ROWID ROW_ID FROM tf_m_valuecard_sum_approval t where 1=1");
        //parser.addSQL("SELECT  t.AREA_CODE, t.STAFF_ID,t.TOTAL_AMOUNT,t.OPERA_STAFF_ID,TO_CHAR(t.COMMIT_TIME,'YYYY-MM-DD HH24:MI:SS') COMMIT_TIME,t.AMOUNTS,t.REMOVE_TAG ,t.ROWID ROW_ID FROM tf_m_valuecard_sum_approval t where 1=1");

        String citys = "HAIN,HNSJ,0898,HNHN";
        String areCode = param.getString("AREA_CODE", "");
        if (!"".equals(areCode) && !citys.contains(areCode))
        {
            parser.addSQL(" AND t.AREA_CODE=:AREA_CODE");
        }

        if (!"".equals(param.getString("STAFF_ID", "")))
        {
            parser.addSQL(" AND t.STAFF_ID LIKE '%" + param.getString("STAFF_ID", "") + "%'");
        }
        
        parser.addSQL(" and t.COMMIT_TIME>=to_date(:START_DATE,'yyyy-mm-dd') ");
        parser.addSQL(" and t.COMMIT_TIME<=to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') ");
        return Dao.qryByParse(parser, pagination);

    }

    /**
     * 根据条件查询有价卡赠送信息 从台账子表捞数据
     * 
     * @param pagination
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-23
     */
    public static IDataset queryGiveCardLogInfos(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select A.* from (select t.DEVICE_NUM,replace(t.remark,'拆分','') staff_id,t.DEVICE_PRICE,t.UPDATE_STAFF_ID,t.UPDATE_TIME,F.TRADE_CITY_CODE AREA_CODE");
        parser.addSQL(" from TF_B_TRADEFEE_DEVICE t,tf_bh_trade f");
        parser.addSQL(" where  t.trade_id = f.trade_id ");
        parser.addSQL(" and f.trade_type_code ='418' ");
        // 分公司查询条件
        String citys = "HAIN,HNSJ,0898,HNHN";
        String areCode = param.getString("CITY_CODE", "");
        if (!"".equals(areCode) && !citys.contains(areCode))
        {
            parser.addSQL(" AND f.CITY_CODE=:CITY_CODE");
        }
        // 工号段查询条件
        if (!"".equals(param.getString("BEGIN_STAFF_ID", "")))
        {
            parser.addSQL(" AND t.UPDATE_STAFF_ID>=:BEGIN_STAFF_ID");
        }
        if (!"".equals(param.getString("END_STAFF_ID", "")))
        {
            parser.addSQL(" AND t.UPDATE_STAFF_ID<=:END_STAFF_ID");
        }

        // 起止时间查询条件
        if (null != param.get("START_DATE"))
        {
            parser.addSQL(" AND t.UPDATE_TIME>=to_date(:START_DATE,'yyyy-MM-dd')");
        }
        if (null != param.get("END_DATE"))
        {
            parser.addSQL(" AND t.UPDATE_TIME-1<=to_date(:END_DATE,'yyyy-MM-dd')");
        }

        parser.addSQL(" ) A ");
        
        //TODO huanghua 27 资料表与台账表解耦---已解决
        IDataset dataset = Dao.qryByParse(parser, pagination, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
        IData temp = new DataMap();
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
				temp = dataset.getData(i);
				temp.put("TOTAL_AMOUNT", queryGiveValueCardInfoByStaffID(temp.getString("STAFF_ID","")));
			}
        }
        return dataset;
    }

    /**
     * 根据审批工号查询的赠送记录
     * 
     * @param staffID
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-5
     */

    public static IDataset queryGiveCardLogInfosByStaffID(String staffID) throws Exception
    {
    	//修改传参数对象，修复空指针问题20170706
    	IData param = new DataMap();
        param.put("STAFF_ID", staffID);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select 1 from TF_B_TRADEFEE_DEVICE t,tf_bh_trade f");
       // parser.addSQL(" where  t.trade_id = f.trade_id and  replace(t.remark,'拆分','')='" + staffID + "'");
        parser.addSQL(" where  t.trade_id = f.trade_id and  replace(t.remark,'拆分','')=:STAFF_ID");
        parser.addSQL(" and f.trade_type_code ='418'");
        //TODO huanghua 14 切换数据源
        return Dao.qryByParse(parser, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
    }
    
    /**
     * 根据工号查询记录信息
     * 
     * @param staffID
     * @return
     * @throws Exception
     */
    public static String queryGiveValueCardInfoByStaffID(String staffID) throws Exception
    {
        String sql = "SELECT TOTAL_AMOUNT FROM tf_m_valuecard_sum_approval WHERE STAFF_ID =:STAFF_ID";
        IData param = new DataMap();
        param.put("STAFF_ID", staffID);

        IDataset dataset = Dao.qryBySql(new StringBuilder(sql), param);
        if(IDataUtil.isNotEmpty(dataset)){
        	return dataset.getData(0).getString("TOTAL_AMOUNT");
        }
        return null;
    }
}
