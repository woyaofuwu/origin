
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SaleDetailQry
{

    public static int cancelThisTerminal(String importId) throws Exception
    {
        IData param = new DataMap();
        param.put("IMPORT_ID", importId);
        return Dao.executeUpdateByCodeCode("TF_F_CMS_IMPORT_BAT", "CANCEL_BAT", param, Route.CONN_CRM_CEN);
    }

    public static int doThisTerminalImport(String dealTime, String importId) throws Exception
    {
        IData param = new DataMap();
        param.put("DEAL_TIME", dealTime);
        param.put("IMPORT_ID", importId);
        return Dao.executeUpdateByCodeCode("TF_F_CMS_IMPORT_BAT", "DEAL_BAT", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getContractByType(String contractType, String contractStatus) throws Exception
    {
        IData data = new DataMap();
        data.put("RSRV_STR5", contractType);
        data.put("PRODUCT_MODE", contractStatus);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT *  FROM TD_B_PRODUCT A ");
        parser.addSQL(" WHERE A.PRODUCT_MODE = :PRODUCT_MODE ");
        parser.addSQL(" AND A.RSRV_STR5 = :RSRV_STR5");

        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return dataset;
    }

    /* 根据编码获取名字 */
    public static String getNameByCode(String strKey, String strValue, String eparchyCode) throws Exception
    {
        String strColName, strTabName, strColKey, strsqlrre;
        IDataset result = null;
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);

        if (strKey.equals("S"))
        {
            strColKey = "SERVICE_ID";
            strColName = "SERVICE_NAME";
            strTabName = "TD_B_SERVICE";
            strsqlrre = "SEL_BY_PK";
            param.put(strColKey, strValue);
            result = Dao.qryByCode("TD_B_SERVICE", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
        }
        else if (strKey.equals("D"))
        {
            strColKey = "DISCNT_CODE";
            strColName = "DISCNT_NAME";
            strTabName = "TD_B_DISCNT";
            strsqlrre = "SEL_BY_PK";
            param.put(strColKey, strValue);
            result = Dao.qryByCode("TD_B_DISCNT", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
        }
        else if (strKey.equals("Z"))
        {
            strColKey = "SERVICE_ID";
            strColName = "SERVICE_NAME";
            strTabName = "TD_B_PLATSVC";
            strsqlrre = "SEL_BY_PK";
            param.put(strColKey, strValue);
            result = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
        }
        else if (strKey.equals("A"))
        {
            strColKey = "DISCNT_GIFT_ID";
            strColName = "DISCNT_GIFT_NAME";
            strTabName = "TD_B_SALE_DEPOSIT";
            strsqlrre = "SEL_BY_DDISCNT_ID";
            param.put(strColKey, strValue);
            result = Dao.qryByCode("TD_B_SALE_DEPOSIT", "SEL_BY_DDISCNT_ID", param, Route.CONN_CRM_CEN);
        }
        else if (strKey.equals("G"))
        {
            strColKey = "GOODS_ID";
            strColName = "GOODS_NAME";
            strTabName = "TD_B_SALE_GOODS";
            strsqlrre = "SEL_BY_GOODS_ID";
            param.put(strColKey, strValue);
            result = Dao.qryByCode("TD_B_SALE_GOODS", "SEL_BY_GOODS_ID", param, Route.CONN_CRM_CEN);
        }
        else
        // 默认取包名
        {
            strColKey = "PACKAGE_ID";
            strColName = "PACKAGE_NAME";
            strTabName = "TD_B_PACKAGE";
            strsqlrre = "SEL_BY_CODE_TO_NAME";
            param.put(strColKey, strValue);
            result = Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_CODE_TO_NAME", param, Route.CONN_CRM_CEN);
        }
        IData rsData = new DataMap();
        if (result == null || result.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "对应配置不存在! 配置如下:[" + strColKey + "][" + strColName + "][" + strTabName + "][" + strsqlrre + "][" + param.toString() + "]");
        }
        else
        {
            rsData = result.getData(0);
        }
        return rsData.getString(strColName);
    }

    public static IDataset getSaleContractInfo(String userId, String processTag) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("PROCESS_TAG", processTag);
        IDataset dataset = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_SALE_CONTRACT_NEW", data);
        return dataset;
    }

    public static IDataset getTerminalByContract(String contractId, String modelDesc, String eparchyCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTRACT_ID", contractId);
        param.put("MODEL_DESC", modelDesc);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TD_B_CONTRACT", "SEL_TERMINAL_BYCONTRACT", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getTradeAttrs(String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_ATTR", "SEL_ALL_BY_TRADE", data);
    }

    public static IDataset getTradeDiscnt(String tradeId, String acceptMonth) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("ACCEPT_MONTH", acceptMonth);
        return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID", data);
    }

    public static IDataset getTradePlatSvc(String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_PLATSVC", "SEL_BY_TRADEID", data);
    }

    public static IDataset getTradeSaleDeposit(String tradeId, String modifyTag) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_SALE_DEPOSIT", "SEL_BY_TRADE_ID", data);
    }

    public static IDataset getTradeSaleGoods(String tradeId, String modifyTag) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_SALE_GOODS", "SEL_BY_TRADE_ID", data);
    }

    public static IDataset getTradeSvc(String tradeId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BY_TRADEID", data);
    }

    public static IDataset queryBatInfo(String batId, String batName, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("BAT_NAME", batName);
        data.put("EPARCHYCODE", eparchyCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_BAT_FORSMHY", data, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset queryCommparaDetail(String paramValue, String paraCode1, String paraCode2) throws Exception
    {
        IData data = new DataMap();
        data.put("PARAM_VALUE", paramValue);
        data.put("PARA_CODE1", paraCode1);
        data.put("PARA_CODE2", paraCode2);
        IDataset dataset = Dao.qryByCode("TD_B_SALESERV_COMMPARA", "SEL_BY_PK1", data);
        return dataset;
    }

    public static IDataset queryDiscntActionRuleInfo(String actionCode) throws Exception
    {
        IData data = new DataMap();
        data.put("ACTION_CODE", actionCode);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select ACTION_RULE_ID,ACTION_RULE_NAME,EPARCHY_CODE,DISCNT_ITEM_ID,MONEY,MONTHS," + "AMONTHS,LIMIT_MONEY,DES_DEPOSIT_CODE,RSRV_FEE  from ucr_param.td_b_discnt_action_rule@dblnk_bossceni1 ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND action_rule_id in( select exec_id from ucr_param.td_b_discnt_action_comp@dblnk_bossceni1 where action_code = :ACTION_CODE) ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryDiscntPlusInfo(String discntCode) throws Exception
    {
        IData data = new DataMap();
        data.put("DISCNT_CODE", discntCode);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT prevaluen1,prevaluen2,prevaluen3 FROM ucr_param.TD_B_DISCNT_PLUS@dblnk_bossceni1 ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND discnt_code = :DISCNT_CODE ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查看四码合一机型合约价
     */
    public static IDataset queryFourCodesToOnePriceInfo(String batId, String modelCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_CODE", modelCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_TEMINALLEVEL_SIMA", data, page, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset queryIsExistsModelCode(String batId, String modelCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_CODE", modelCode);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_BAT_ISEXISTS", data, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset queryModelCodeInfo(String modelCode, String modelDesc, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("MODEL_CODE", modelCode);
        data.put("MODEL_DESC", modelDesc);
        return Dao.qryByCode("TD_S_RES_MODEL", "SEL_ALL_BY_CODE", data, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryModelInfoByBatId(String batId, String modelDesc) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_DESC", modelDesc);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_MODELCODE_IN_BAT", data, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset queryPriceLevelInfo(String batId, String modelCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_CODE", modelCode);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_LEVEL_BY_MODELCODE", data, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset querySaleActiveHis(String userId, String startTime, String endTime, String validFlag, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT t.SERIAL_NUMBER,t.PRODUCT_ID,t.PRODUCT_NAME,t.PACKAGE_ID,t.PACKAGE_NAME,(t.OPER_FEE + t.ADVANCE_PAY) / 100 CAMPN_FEE,t.TRADE_STAFF_ID,t.START_DATE,t.END_DATE,t.ACCEPT_DATE,t.REMARK ");
        sql.addSQL("FROM TF_F_USER_SALE_ACTIVE t ");
        sql.addSQL("WHERE t.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.addSQL("AND t.USER_ID = :USER_ID ");
        sql.addSQL("AND t.ACCEPT_DATE >= to_date(:START_TIME,'yyyy-MM-dd') ");
        sql.addSQL("AND t.ACCEPT_DATE <= to_date(:END_TIME,'yyyy-MM-dd')+1 ");
        if ("1".equals(validFlag))
        {
            sql.addSQL("AND sysdate between t.START_DATE and t.END_DATE ");
        }
        else if ("0".equals(validFlag))
        {
            sql.addSQL("AND (sysdate > t.END_DATE or sysdate < t.START_DATE) ");
        }
        return Dao.qryByParse(sql, page);
    }

    public static IDataset queryTerminalImport(String importId, String importType, String dealState, String importFileName, String cityCode, String staffId, String startDate, String endDate, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("IMPORT_ID", importId);
        param.put("IMPORT_TYPE", importType);
        param.put("DEAL_STATE", dealState);
        param.put("IMPORT_FILENAME", importFileName);
        param.put("CITY_CODE", cityCode);
        param.put("STAFF_ID", staffId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCodeParser("TF_F_CMS_IMPORT_BAT", "SEL_IMPORTBAT", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryTerminalPrice(String productId, String modelCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("MODEL_CODE", modelCode);
        return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TERMINALPRICE_BYMODEL", param, page);
    }
    
    /**
     * @Description 获取营销活动办理中收取预存款的活动编码
     * @param tradeId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static IDataset getTradeDiscntCode(String tradeId, String modifyTag) throws Exception{
        IData data = new DataMap();
        data.put("TRADE_ID", tradeId);
        data.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_SALE_DEPOSIT", "SEL_DISCNT_CODE_BY_TRADE_ID", data,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
}
