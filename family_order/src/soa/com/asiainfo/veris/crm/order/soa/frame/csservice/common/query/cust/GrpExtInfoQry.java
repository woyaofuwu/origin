
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GrpExtInfoQry
{
    /**
     * 作用：校验基本接入号是否用可
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset checkBaseServCode(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT EX.* FROM TF_F_CUST_GROUP_EXTEND EX");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND EX.EXTEND_TAG = 'BNUMB'");
        parser.addSQL(" AND EX.RSRV_STR1 = :SERVCODE");
        parser.addSQL(" AND NOT EXISTS (");
        parser.addSQL("  select GP.CUST_ID from TF_F_CUST_GROUP GP ");
        parser.addSQL("  WHERE GP.GROUP_ID = :GROUP_ID ");
        parser.addSQL("  AND GP.CUST_ID = EX.EXTEND_VALUE");
        parser.addSQL("  )");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    public static IDataset selectGroupExtendForTag(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT EX.* FROM TF_F_CUST_GROUP_EXTEND EX");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND EX.EXTEND_TAG = :EXTEND_TAG");
        parser.addSQL(" AND EX.RSRV_STR1 = :SERVCODE");
        parser.addSQL(" AND EXISTS (");
        parser.addSQL("  select GP.CUST_ID from TF_F_CUST_GROUP GP ");
        parser.addSQL("  WHERE GP.GROUP_ID = :GROUP_ID ");
        parser.addSQL("  AND GP.Remove_Tag='0'");
        parser.addSQL("  AND GP.CUST_ID = EX.EXTEND_VALUE");
        parser.addSQL("  )");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 作用：校验是否已经基本接入号是否存在
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static int checkBaseServCode2(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT COUNT(*) NUM FROM TF_F_CUST_GROUP_EXTEND EX");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND EX.EXTEND_TAG = 'BNUMB'");
        parser.addSQL(" AND EX.RSRV_STR1 = :SERVCODE");
        parser.addSQL(" AND NOT EXISTS (");
        parser.addSQL("  select GP.CUST_ID from TF_F_CUST_GROUP GP ");
        parser.addSQL("  WHERE GP.GROUP_ID = :GROUP_ID ");
        parser.addSQL("  AND GP.CUST_ID = EX.EXTEND_VALUE");
        parser.addSQL("  )");
        IDataset result = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return result.getData(0).getInt("NUM");

    }

    /**
     * 查询跨省VPN号
     * 
     * @param cust_id
     * @param new_ec_incode_a
     * @return
     * @throws Exception
     */
    public static IDataset getAREAVPNNOBYGROUPID(IData params) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_GROUPAREAVPNNO_BY_TAG", params, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * 查询集团用户对应的跨区VPN编码
     * 
     * @author lishouguo Mar 31, 2011
     * @参数
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset getAreaVPNNOByVPNNO(IData params) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_GROUPAREAVPNNO_BY_VPNNO", params, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * 作用：查询基本接入号码(天津)
     */
    public static IDataset getComboBoxValue(IData param) throws Exception
    {

        IDataset dataset = new DatasetList(); // 最终返回去重并组装好的基本接入号码
        IDataset serDataset = new DatasetList(); // 进行组装的基本接入号码
        // 获得sp基本接入号码
        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_ECINCODE_BY_CODEA", param, Route.CONN_CRM_CG);
        // 获取attra的基本接入号码

        if (result != null && result.size() > 0)
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData data = (IData) result.get(i);
                IData idata = new DataMap();
                if (!"".equals(data.get("RSRV_STR1")) && data.get("RSRV_STR1") != null)
                {
                    idata.put("RSRV_STR1", data.get("RSRV_STR1"));
                    serDataset.add(idata);
                }
            }
        }
        // 去除重复值
        if (serDataset.size() > 0)
        {
            for (int i = 0; i < serDataset.size(); i++)
            {
                IData data = (IData) serDataset.get(i);
                IDataset datasetList = DataHelper.filter(serDataset, "RSRV_STR1=" + data.get("RSRV_STR1"));
                IData dataMap = (IData) datasetList.get(0);
                if (DataHelper.filter(dataset, "RSRV_STR1=" + dataMap.get("RSRV_STR1")).size() <= 0)
                {
                    dataset.add(dataMap);
                }
            }
            DataHelper.sort(dataset, "RSRV_STR1", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        return dataset;
    }

    /**
     * 查询EC基本接入码列表，根据EC基本接入码属性
     * 
     * @param cust_id
     *            ,new_ec_incode_a,extend_tag
     * @return IDataset EC基本接入码列表
     **/
    public static IDataset getEcInCodeListByECA(IData params) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_ECINCODE_BY_CODEA_NEW", params, Route.CONN_CRM_CG);
        return result;
    }

    public static IDataset getWAPPUSHEcInCodeListByECA(IData params) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_ECINCODE_BY_CODEA_WAPPUSH", params, Route.CONN_CRM_CG);
        return result;
    }

    /*
     * 查询EC基本接入码列表，根据EC基本接入码属性 @param cust_id,new_ec_incode_a @return IDataset EC基本接入码列表
     */
    public static IDataset getEcInCodeListByECA(String cust_id, String new_ec_incode_a) throws Exception
    {

        IData idata = new DataMap();
        idata.put("CUST_ID", cust_id);
        idata.put("BNUM_KIND_NEW", new_ec_incode_a);
        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_ECINCODE_BY_CODEA", idata, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * 作用：查询集团扩展信息
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset getExtendLists(String custId) throws Exception
    {

        IData param = new DataMap();
        param.put("EXTEND_VALUE", custId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.EXTEND_ID,T.EXTEND_VALUE CUST_ID,T.EXTEND_TAG,T.RSRV_STR1,");
        parser.addSQL(" T.RSRV_STR2,T.RSRV_STR3,T.RSRV_STR39,T.RSRV_STR40,T.RSRV_TAG1,T.RSRV_TAG11,");
        parser.addSQL(" T.RSRV_TAG12,T.RSRV_TAG13,T.RSRV_TAG14 ");
        parser.addSQL(" FROM TF_F_CUST_GROUP_EXTEND T");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.EXTEND_VALUE = :EXTEND_VALUE");
        parser.addSQL(" AND T.EXTEND_TAG IN ('SYNSTATE','BNUMB')");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 根据客户编码查询客户扩展资料资料
     */
    public static IDataset getGrpExtendInfoByCustIdRS1RS2(IData idata) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_BY_EXTVALUE_EXTTAG_RS1RS2", idata, Route.CONN_CRM_CG);
    }

    /*
     * 物联网使用 RSRV_TAG5 做同步标识
     */
    public static IDataset getIotECSyncState(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("EXTEND_VALUE", custId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  COUNT(1) RECORDCOUNT ");
        parser.addSQL(" FROM TF_F_CUST_GROUP_EXTEND T");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.EXTEND_VALUE = :EXTEND_VALUE AND T.EXTEND_TAG='SYNSTATE' ");
        parser.addSQL(" AND T.RSRV_TAG5='1' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 查询跨省VPN号
     * 
     * @param cust_id
     * @param new_ec_incode_a
     * @return
     * @throws Exception
     */
    public static IDataset getVPNNOByGROUPID(IData params) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_GROUPVPNNO_BY_TAG", params, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * 通过VPNNO查询跨省VPN号资料
     * 
     * @param cust_id
     * @param new_ec_incode_a
     * @return
     * @throws Exception
     */
    public static IDataset getVPNNOByVPNNO(IData params) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_GROUPVPNNO_BY_VPNNO", params, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * 写扩展表中的记录,ADC
     * 
     * @param data
     * @throws Exception
     */
    public static int insertAdcExtendSync(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" INSERT INTO TF_F_CUST_GROUP_EXTEND(EXTEND_ID,EXTEND_VALUE,EXTEND_TAG,RSRV_TAG11) ");
        parser.addSQL(" VALUES (:EXTEND_ID,:EXTEND_VALUE,'SYNSTATE','1')");
        return Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }

    /**
     * 写扩展表中的记录, 物联网
     * 
     * @param data
     * @throws Exception
     */
    public static int insertIotECExtendSync(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" INSERT INTO TF_F_CUST_GROUP_EXTEND(EXTEND_ID,EXTEND_VALUE,EXTEND_TAG,RSRV_TAG5,RSRV_STR34,RSRV_DATE15) ");
        parser.addSQL(" VALUES (:EXTEND_ID,:EXTEND_VALUE,'SYNSTATE',:RSRV_TAG5,:RSRV_STR34,TO_DATE(:RSRV_DATE15,'yyyy-mm-dd hh24:mi:ss') )");
        return Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }

    /**
     * 写扩展表中的记录,MAS
     * 
     * @param data
     * @throws Exception
     */
    public static int insertMasExtendSync(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" INSERT INTO TF_F_CUST_GROUP_EXTEND(EXTEND_ID,EXTEND_VALUE,EXTEND_TAG,RSRV_TAG13) ");
        parser.addSQL(" VALUES (:EXTEND_ID,:EXTEND_VALUE,'SYNSTATE','1')");
        return Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团客户信息By cust_id
     * 
     * @author chenkh
     * @param custID
     * @return
     * @throws Exception
     */
    public static IDataset queryCustGroupInfoByCID(String custID) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custID);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TF_F_CUST_GROUP_EXTEND");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and EXTEND_VALUE = :CUST_ID ");
        parser.addSQL(" and EXTEND_TAG = 'keym' ");
        parser.addSQL(" and (RSRV_DATE10 is null or RSRV_DATE10 > sysdate) ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 查询多媒体桌面电话集团管理员信息
     * 
     * @author tengg
     * @2010-8-20
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryUserManageInfo(IData data) throws Exception
    {

        IDataset dateset = Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_BY_MANAGER", data, Route.CONN_CRM_CG);
        return dateset.size() > 0 ? dateset : null;
    }

    public static int updateAdcExtendSync(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("UPDATE TF_F_CUST_GROUP_EXTEND SET RSRV_TAG11 = '1'");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND EXTEND_VALUE = :EXTEND_VALUE");
        parser.addSQL(" AND EXTEND_TAG = 'SYNSTATE'");
        return Dao.executeUpdate(parser);
    }

    public static int updateIotECExtendSync(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("UPDATE TF_F_CUST_GROUP_EXTEND SET RSRV_TAG5 = :RSRV_TAG5,RSRV_STR34=:RSRV_STR34,RSRV_DATE15 = TO_DATE(:RSRV_DATE15,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND EXTEND_VALUE = :EXTEND_VALUE");
        parser.addSQL(" AND EXTEND_TAG = 'SYNSTATE'");
        return Dao.executeUpdate(parser);
    }

    public static int updateMasExtendSync(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("UPDATE TF_F_CUST_GROUP_EXTEND SET RSRV_TAG13 = '1'");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND EXTEND_VALUE = :EXTEND_VALUE");
        parser.addSQL(" AND EXTEND_TAG = 'SYNSTATE'");
        return Dao.executeUpdate(parser);
    }
    
    /**
     * 写扩展表中的记录, 物联网同步失败
     * 
     * @param data
     * @throws Exception
     */
    public static int insertIotECFailExtendSync(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" INSERT INTO TF_F_CUST_GROUP_EXTEND(EXTEND_ID,EXTEND_VALUE,EXTEND_TAG,RSRV_TAG5,RSRV_STR34,RSRV_DATE15) ");
        parser.addSQL(" VALUES (:EXTEND_ID,:EXTEND_VALUE,'SYNSTATE','3',:RSRV_STR34,TO_DATE(:RSRV_DATE15,'yyyy-mm-dd hh24:mi:ss'))");
        return Dao.executeUpdate(parser, Route.CONN_CRM_CG);
    }
    
    public static int updateIotECFailExtendSync(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("UPDATE TF_F_CUST_GROUP_EXTEND SET RSRV_TAG5 = '3',RSRV_STR34=:RSRV_STR34,RSRV_DATE15 =TO_DATE(:RSRV_DATE15,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND EXTEND_VALUE = :EXTEND_VALUE");
        parser.addSQL(" AND EXTEND_TAG = 'SYNSTATE'");
        return Dao.executeUpdate(parser);
    }
    
    /**
     * 查询物联网同步工单类型
     * 
     * @author chenkh
     * @param custID
     * @return
     * @throws Exception
     */
    public static IDataset queryWLWInfoByTradeId(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" Select * From UCR_CRM1.TF_B_TRADE_SVC");
        parser.addSQL(" Where 1=1 ");
        parser.addSQL(" And TRADE_ID = :TRADE_ID ");
        parser.addSQL(" And MODIFY_TAG = '0' ");
        parser.addSQL(" And OPER_CODE = '02' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    /**
     * 查询是否是测试集团的标识
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpExtendTestByCustId(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("EXTEND_VALUE", custId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT EXTEND_ID, ");
        parser.addSQL("       EXTEND_VALUE, ");
        parser.addSQL("       EXTEND_TAG, ");
        parser.addSQL("       RSRV_NUM1 ");
        parser.addSQL("  FROM TF_F_CUST_GROUP_EXTEND T ");
        parser.addSQL(" WHERE T.EXTEND_VALUE = :EXTEND_VALUE ");
        parser.addSQL(" AND T.EXTEND_TAG = 'GrpTestTag' ");
        parser.addSQL(" AND T.RSRV_NUM1 = 1 ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    /**
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static int updateGrpExtTestByCustId(String custId) throws Exception
    {
    	IData param = new DataMap();
        param.put("EXTEND_VALUE", custId);
        param.put("RSRV_NUM1", 0);
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_F_CUST_GROUP_EXTEND T ");
        parser.addSQL(" SET T.RSRV_NUM1 = :RSRV_NUM1 ");
        parser.addSQL("WHERE T.EXTEND_VALUE = :EXTEND_VALUE");
        parser.addSQL(" AND T.EXTEND_TAG = 'GrpTestTag' ");
        parser.addSQL(" AND T.RSRV_NUM1 = 1 ");
        return Dao.executeUpdate(parser);
    }
}
