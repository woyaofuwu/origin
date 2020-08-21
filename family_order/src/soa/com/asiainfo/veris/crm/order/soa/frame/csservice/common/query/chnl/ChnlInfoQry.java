
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ChnlInfoQry
{
    /**
     * 根据渠道直销经理编码查询该编码是否存在
     * 
     * @return
     * @throws Exception
     * @date 2010-5-21
     */
    public static IDataset getInfoByChnlCode(IData param) throws Exception
    {

        return Dao.qryByCode("TF_CHL_CHANNEL", "SEL_BY_CHNL_CODE", param, Route.CONN_CRM_CEN);
    }

    public static IData queryAreaInfo(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select CHNL_AREA_CODE data_code, CHNL_AREA_NAME data_name from TD_CHL_AREA  ");
        parser.addSQL(" where chnl_area_code = :ROOT_ID");
        IDataset tmp = Dao.qryByParse(parser);
        return tmp.size() > 0 ? (IData) tmp.get(0) : null;
    }

    /*
     * 查询渠道区域信息
     */
    public static IDataset queryAreaTree(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select a.CHNL_AREA_CODE data_code, a.CHNL_AREA_NAME data_name ,decode(b.NODE_COUNT,null,'0',b.NODE_COUNT) NODE_COUNT");
        parser.addSQL(" from TD_CHL_AREA a, ");
        parser.addSQL(" (select  PARENT_CHNL_AREA,count(1) NODE_COUNT from TD_CHL_AREA  group by PARENT_CHNL_AREA) b ");
        parser.addSQL(" where a.CHNL_AREA_CODE = b.PARENT_CHNL_AREA(+) and ");
        parser.addSQL(" a.PARENT_CHNL_AREA = :ROOT_ID  ");
        return Dao.qryByParse(parser);
    }

    public static IDataset queryChnlInfoByCode(String chnlCode) throws Exception
    {
        IData param = new DataMap();
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select a.eparchy_code, a.city_code, a.chnl_name, a.chnl_code,");
        parser.addSQL("  a.chnl_kind_id,b.address,b.linkman,b.juri_pspt_id, ");
        parser.addSQL(" b.principal_name,b.principal_phone");
        parser.addSQL(" from TF_CHL_CHANNEL a, TF_CHL_ARCHIVE b");
        parser.addSQL(" where a.chnl_id = b.chnl_id");
        parser.addSQL(" and a.chnl_code in (");
        parser.addSQL(chnlCode);
        parser.addSQL(" )");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /*
     * 查询业务区下的渠道树
     */
    public static IDataset queryChnlTreeByArea(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select a.chnl_id data_code, a.chnl_name data_name,'0' NODE_COUNT ");
        parser.addSQL(" from tf_chl_channel a ,td_chl_area b ");
        parser.addSQL(" where a.area_code = b.chnl_area_code ");
        parser.addSQL(" and a.area_code = :AREA_CODE ");
        parser.addSQL(" and a.area_type = :AREA_TYPE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /*
     * 查询渠道类型树
     */
    public static IDataset queryChnlType(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select chnl_kind_id data_code, chnl_kind_name data_name from td_chl_kinddef ");
        parser.addSQL(" where parent_chnl_kind_id =  :parentChnlType  ");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据传入的参数拼查询sql
     */
    public static IDataset queryDataByCode(String str_ParentCode, String str_TableName, String str_DataCode, String str_DataName, String str_ParentColName, String str_EparchyCode) throws Exception
    {

        StringBuilder sqlstr = new StringBuilder();
        sqlstr.delete(0, sqlstr.length());
        sqlstr.append(" select a." + str_DataCode + " data_code, a." + str_DataName + " data_name ");
        sqlstr.append(" from " + str_TableName + " a ");
        sqlstr.append(" where " + str_DataCode + " = ?");
        if (!str_EparchyCode.equals("null") && !str_EparchyCode.equals(""))
        {
            sqlstr.append(" and (" + str_EparchyCode + " = '" + CSBizBean.getVisit().getStaffEparchyCode() + "'");
            sqlstr.append(" or " + str_EparchyCode + " = 'ZZZZ')");
        }

        // Dao.qryBySql(sqlstr, new Object[] { str_ParentCode });
        IData paramData = new DataMap();
        paramData.put("str_ParentCode", str_ParentCode);

        return Dao.qryBySql(sqlstr, paramData);
    }

    /**
     * 根据传入的参数拼查询sql
     */
    public static IDataset queryDataByParent(String str_ParentCode, String str_TableName, String str_DataCode, String str_DataName, String str_ParentColName, String str_ValidCond, String str_EparchyCode) throws Exception
    {

        StringBuilder sqlstr = new StringBuilder();
        sqlstr.delete(0, sqlstr.length());
        sqlstr.append(" select a." + str_DataCode + " data_code, a." + str_DataName + " data_name,decode(b.cnt,null,'0',b.cnt) NODE_COUNT  ");
        sqlstr.append(" from " + str_TableName + " a, ");
        sqlstr.append("      (select " + str_ParentColName + " pcn,count(*) cnt from " + str_TableName + " group by " + str_ParentColName + ") b");
        sqlstr.append(" where " + str_ValidCond + " and a." + str_DataCode + " = b.pcn(+)");
        sqlstr.append("   and " + str_ParentColName + " = ? ");
        if (!str_EparchyCode.equals("null") && !str_EparchyCode.equals(""))
        {
            sqlstr.append(" and (" + str_EparchyCode + " = '" + CSBizBean.getVisit().getStaffEparchyCode() + "'");
            sqlstr.append(" or " + str_EparchyCode + " = 'ZZZZ')");
            sqlstr.append(" order by " + str_EparchyCode + " asc");
        }

        // Dao.qryBySql(sqlstr, new Object[] { str_ParentCode });
        IData paramData = new DataMap();
        paramData.put("str_ParentCode", str_ParentCode);

        return Dao.qryBySql(sqlstr, paramData);
    }

    /**
     * 代理商终端押金查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-08 21:40:16
     */
    public static IDataset queryDepartMoney(String chnlId) throws Exception
    {
        IData param = new DataMap();
        param.put("CHNL_ID", chnlId);

        return Dao.qryByCode("OTHER", "SEL_DEPART_MONEY", param);
    }

    /*
     * 查询部门下子节点
     */
    public static IDataset queryDeptNode(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select a.*,a.depart_id data_code,a.depart_name data_name  ,decode(b.s,null,'0',b.s) NODE_COUNT ");
        parser.addSQL(" from td_m_depart a, ");
        parser.addSQL(" (select count(1) s,parent_depart_id from td_m_depart group by parent_depart_id) b ");
        parser.addSQL(" where 1=1 and ");

        parser.addSQL(" a.parent_depart_id = :DEPART_ID  ");
        parser.addSQL(" and a.depart_id = b.parent_depart_id(+) ");
        // 显示集团客户营销服务部下的8个部门
        if (data.getString("DEPART_ID").equals("50000"))
        {
            parser.addSQL(" and a.depart_id in ('50004','50005','50006','50007','50008','50009','50010','50011') ");
        }
        // 非集团服务部下的部门才有类型的约束
        else if (!data.getString("DEPART_ID").matches("[50004-50011]"))
        {
            parser.addSQL(" and a.depart_kind_code in ('00X','002','100','101','102','200','201','202','203','204','205','206','300','301','302','303','304','305','306')");// 加入"集团客户服务部"节点及其子节点
        }
        // parser.addSQL(" and a.depart_kind_code in
        // ('00X','100','101','102','200','201','202','203','204','205','300','301','302','303','304','305')");
        return Dao.qryByParse(parser);
    }

    /*
     * 查询渠道类型子节点
     */
    public static IDataset queryTreeTypeByParent(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("  select a.*,a.chnl_kind_id data_code,a.chnl_kind_name data_name ,decode(b.s,null,'0',b.s) NODE_COUNT ");
        parser.addSQL(" from td_chl_kinddef a,");
        parser.addSQL(" (select parent_chnl_kind_id,count(1) s from td_chl_kinddef group by parent_chnl_kind_id ) b");
        parser.addSQL("  where a.parent_chnl_kind_id = :PARENT_ID and a.chnl_kind_id = b.parent_chnl_kind_id(+)");
        parser.addSQL("  and (a.selectable = '1' or a.selectable = :SELECTABLE_PHONE or a.selectable = :SELECTABLE_UNPHONE)");
        return Dao.qryByParse(parser);
    }

    public static IDataset selByBook(String cumu_id) throws Exception
    {
        IData param = new DataMap();
        param.put("CUMU_ID", cumu_id);
        IDataset out = Dao.qryByCode("CHNL_CU_CUMUINFO", "SEL_BY_BOOK", param);
        return out;
    }

    public static IDataset selByCommpara(String cumu_id, String cu_pass) throws Exception
    {
        IData param = new DataMap();
        IDataset out = Dao.qryByCode("CHNL_CU_CUMUINFO", "SEL_BY_COMMPARA", param);
        return out;
    }

    public static IDataset selByCumuinfo(String cumu_id, String cu_pass) throws Exception
    {
        IData param = new DataMap();
        param.put("CUMU_ID", cumu_id);
        param.put("CU_PASS", cu_pass);
        IDataset out = Dao.qryByCode("CHNL_CU_CUMUINFO", "SEL_BY_CUMUINFO", param);
        return out;
    }

    public static IDataset selByRegi(String cumu_id, String para_code2) throws Exception
    {
        IData param = new DataMap();
        param.put("CUMU_ID", cumu_id);
        param.put("PARA_CODE2", para_code2);
        IDataset out = Dao.qryByCodeParser("CHNL_CU_CUMUINFO", "SEL_BY_REGI", param);
        return out;
    }

    public static IDataset selByRegiTwo(String cumu_id, String para_code2) throws Exception
    {
        IData param = new DataMap();
        param.put("CUMU_ID", cumu_id);
        param.put("PARA_CODE2", para_code2);
        IDataset out = Dao.qryByCodeParser("CHNL_CU_CUMUINFO", "SEL_BY_REGI_TWO", param);
        return out;
    }

    public static IDataset selBySmsRec(String cumu_id) throws Exception
    {
        IData param = new DataMap();
        param.put("CUMU_ID", cumu_id);
        IDataset out = Dao.qryByCode("CHNL_CU_CUMUINFO", "SEL_BY_SMSREC", param);
        return out;
    }
    
    public static IDataset getGlobalChlId(String Chnl_Id) throws Exception
    {
    	IData param = new DataMap();
        param.put("CHNL_ID", Chnl_Id);
        return Dao.qryByCode("TF_CHL_CHANNEL", "SEL_BY_CHNL_ID", param, Route.CONN_CRM_CEN);
    }
}
