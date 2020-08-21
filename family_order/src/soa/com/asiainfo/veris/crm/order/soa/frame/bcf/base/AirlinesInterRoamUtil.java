package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 航空公司专属国漫资费工具类
 */
public class AirlinesInterRoamUtil {

    // 获取国漫数据流量日套餐业务界面TRADE_TYPE_CODE = 300的业务里的航空公司专属优惠的开通方向
    public static String getInterRoamDiscnts() throws Exception {
        IDataset paramInfos = CommparaInfoQry.getCommpara("CSM", "2743", "1", CSBizBean.getVisit().getStaffEparchyCode());
        String discntCode = paramInfos.getData(0).getString("PARA_CODE23");
        return discntCode;
    }

    // 获取全球通无限尊享计划套餐八折优惠
    public static String getUnLimitProduct8Discnt() throws Exception {
        IDataset paramInfos = CommparaInfoQry.getCommpara("CSM", "2743", "1", CSBizBean.getVisit().getStaffEparchyCode());
        String discntCode = paramInfos.getData(0).getString("PARA_CODE1");
        return discntCode;
    }

    // 获取国漫标准资费八折优惠
    public static String getInterRoamStandard8Discnt() throws Exception {
        IDataset paramInfos = CommparaInfoQry.getCommpara("CSM", "2743", "1", CSBizBean.getVisit().getStaffEparchyCode());
        String discntCode = paramInfos.getData(0).getString("PARA_CODE2");
        return discntCode;
    }

    // 获取国漫专属叠加日包优惠
    public static String getInterRoamCasDayDiscnt() throws Exception {
        IDataset paramInfos = CommparaInfoQry.getCommpara("CSM", "2743", "1", CSBizBean.getVisit().getStaffEparchyCode());
        String discntCode = paramInfos.getData(0).getString("PARA_CODE3");
        return discntCode;
    }

    // 获取国漫专属叠加月包优惠
    public static String getInterRoamCasMonthDiscnt() throws Exception {
        IDataset paramInfos = CommparaInfoQry.getCommpara("CSM", "2743", "1", CSBizBean.getVisit().getStaffEparchyCode());
        String discntCode = paramInfos.getData(0).getString("PARA_CODE4");
        return discntCode;
    }

    // 获取128元专属叠加月包需要承诺6个月的最低消费的优惠
    public static String getGuaranteeMoney6MonthDiscnt() throws Exception {
        IDataset paramInfos = CommparaInfoQry.getCommpara("CSM", "2743", "1", CSBizBean.getVisit().getStaffEparchyCode());
        String discntCode = paramInfos.getData(0).getString("PARA_CODE5");
        return discntCode;
    }

    // 将原来白名单中REMOVE_TAG为2的记录改为1，彻底删除。
    public static int updateRemoveWhiteList(IData input) throws Exception {
        SQLParser parser = new SQLParser(input);
        parser.addSQL(" UPDATE TF_F_AIRLINES_WHITE ");
        parser.addSQL(" SET REMOVE_TAG = '1' ");
        parser.addSQL(" ,UPDATE_TIME = SYSDATE ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND REMOVE_TAG = '2' ");
        return Dao.executeUpdate(parser, Route.getCrmDefaultDb());
    }

    // 查询用户是否为白名单号码
    public static IDataset qryInterRoamAirWhite(String serial_number) throws Exception {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER", serial_number);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT SERIAL_NUMBER,AIRLINES_NAME,MAINAIR_PRV_NAME,PROVINCE_NAME,CITY_NAME,STAFF_NAME,IMPORT_TIME ");
        parser.addSQL(" ,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_STR1 ");
        parser.addSQL(" ,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3 ");
        parser.addSQL(" FROM TF_F_AIRLINES_WHITE A ");
        parser.addSQL(" WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND A.REMOVE_TAG = '0' ");
        return Dao.qryByParse(parser, Route.getCrmDefaultDb());
    }

    // AEE扫描的时候查询白名单表中REMOVE_TAG为2的数据
    public static IDataset queryWhiteUserCancel() throws Exception {
        IData inparams = new DataMap();
        inparams.put("REMOVE_TAG", "2");
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT SERIAL_NUMBER,AIRLINES_NAME,MAINAIR_PRV_NAME,PROVINCE_NAME,CITY_NAME,STAFF_NAME,IMPORT_TIME ");
        parser.addSQL(" ,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_STR1 ");
        parser.addSQL(" ,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3 ");
        parser.addSQL(" FROM TF_F_AIRLINES_WHITE A ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.REMOVE_TAG = :REMOVE_TAG ");
        return Dao.qryByParse(parser, Route.getCrmDefaultDb());
    }

    // 将白名单用户删除的sql，将REMOVE_TAG由0改为2，后期会有aee定时任务去扫描REMOVE_TAG为2的记录
    public static int deleteWhiteList(IData input) throws Exception {
        input.put("STAFF_NAME", CSBizBean.getVisit().getStaffName());
        input.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        input.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        SQLParser parser = new SQLParser(input);
        parser.addSQL(" UPDATE TF_F_AIRLINES_WHITE ");
        parser.addSQL(" SET REMOVE_TAG = '2' ");
        parser.addSQL(" ,STAFF_NAME = :STAFF_NAME ");
        parser.addSQL(" ,UPDATE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" ,UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        parser.addSQL(" ,UPDATE_TIME = SYSDATE ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND REMOVE_TAG = '0' ");
        return Dao.executeUpdate(parser, Route.getCrmDefaultDb());
    }

    //假如原来数据库里有数据，再新增的时候假如手机号码相同则更新同一条记录，调用此接口前得先查出数据库中原有的数据
    public static int updateNewWhiteList(IData input) throws Exception {
        SQLParser parser = new SQLParser(input);
        parser.addSQL(" UPDATE TF_F_AIRLINES_WHITE ");
        parser.addSQL(" SET REMOVE_TAG = '0' ");
        parser.addSQL(" ,AIRLINES_NAME = :AIRLINES_NAME ");
        parser.addSQL(" ,MAINAIR_PRV_NAME = :MAINAIR_PRV_NAME ");
        parser.addSQL(" ,PROVINCE_NAME = :PROVINCE_NAME ");
        parser.addSQL(" ,CITY_NAME = :CITY_NAME ");
        parser.addSQL(" ,STAFF_NAME = :STAFF_NAME ");
        parser.addSQL(" ,UPDATE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" ,UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        parser.addSQL(" ,UPDATE_TIME = SYSDATE ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND REMOVE_TAG = '0' ");
        return Dao.executeUpdate(parser, Route.getCrmDefaultDb());
    }

    // 批量更新白名单数据
    public static int[] updateBatchAddWhiteList(IDataset input) throws Exception {
        StringBuilder sql = new StringBuilder("");
        sql.append(" UPDATE TF_F_AIRLINES_WHITE ");
        sql.append(" SET REMOVE_TAG = '0' ");
        sql.append(" ,AIRLINES_NAME = :AIRLINES_NAME ");
        sql.append(" ,MAINAIR_PRV_NAME = :MAINAIR_PRV_NAME ");
        sql.append(" ,PROVINCE_NAME = :PROVINCE_NAME ");
        sql.append(" ,CITY_NAME = :CITY_NAME ");
        sql.append(" ,STAFF_NAME = :STAFF_NAME ");
        sql.append(" ,UPDATE_STAFF_ID = :UPDATE_STAFF_ID ");
        sql.append(" ,UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        sql.append(" ,UPDATE_TIME = SYSDATE ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append(" AND REMOVE_TAG = '0' ");
        return Dao.executeBatch(sql, input, Route.getCrmDefaultDb());
    }

    // 批量删除白名单数据
    public static int[] updateBatchDeleteWhiteList(IDataset input) throws Exception {
        StringBuilder sql = new StringBuilder("");
        sql.append(" UPDATE TF_F_AIRLINES_WHITE ");
        sql.append(" SET REMOVE_TAG = '2' ");
        sql.append(" ,AIRLINES_NAME = :AIRLINES_NAME ");
        sql.append(" ,MAINAIR_PRV_NAME = :MAINAIR_PRV_NAME ");
        sql.append(" ,PROVINCE_NAME = :PROVINCE_NAME ");
        sql.append(" ,CITY_NAME = :CITY_NAME ");
        sql.append(" ,STAFF_NAME = :STAFF_NAME ");
        sql.append(" ,UPDATE_STAFF_ID = :UPDATE_STAFF_ID ");
        sql.append(" ,UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
        sql.append(" ,UPDATE_TIME = SYSDATE ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append(" AND REMOVE_TAG = '0' ");
        return Dao.executeBatch(sql, input, Route.getCrmDefaultDb());
    }

}
