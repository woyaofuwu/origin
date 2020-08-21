
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CampaignInfoQry
{

    /** 查活动信息 */
    public static IDataset queryCampnInfo(String campnId) throws Exception
    {
        IData param = new DataMap();
        param.put("CAMPN_ID", campnId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * ");
        parser.addSQL(" from TF_SM_CAMPAIGN ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL(" and CAMPN_ID = :CAMPN_ID ");
        return Dao.qryByParse(parser);
    }

    public static IDataset queryCampnNames(String campnName, String campnId, String campnType, String validFlag, String campnStatus, String campnClass, String eparchyCode, String startTime, String endTime, String custStorageMothod) throws Exception
    {

        IData param = new DataMap();
        param.put("CAMPN_NAME", campnName);
        param.put("CAMPN_ID", campnId);
        param.put("CAMPN_TYPE", campnType);
        param.put("VALID_FLAG", validFlag);
        param.put("CAMPN_STATUS", campnStatus);
        param.put("CAMPN_CLASS", campnClass);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("CUST_STORAGEMOTHOD", custStorageMothod);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.*   ");
        parser.addSQL(" FROM TF_SM_CAMPAIGN T  ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND T.CAMPN_NAME LIKE '%' || :CAMPN_NAME || '%' ");
        parser.addSQL(" AND T.CAMPN_ID = :CAMPN_ID");
        parser.addSQL(" AND T.CAMPN_TYPE = :CAMPN_TYPE ");
        parser.addSQL(" AND T.VALID_FLAG = :VALID_FLAG ");
        parser.addSQL(" AND T.CAMPN_STATUS = :CAMPN_STATUS ");
        parser.addSQL(" AND T.CAMPN_CLASS = :CAMPN_CLASS ");
        parser.addSQL(" AND sysdate between T.DEMAND_BEGIN_TIME and t.DEMAND_END_TIME");
        parser.addSQL(" AND (T.EPARCHY_CODE = :EPARCHY_CODE or T.EPARCHY_CODE = 'ZZZZ')");
        parser.addSQL(" AND T.CREATE_TIME >= TO_DATE(:START_TIME, 'YYYY-MM-DD')");
        parser.addSQL(" AND T.CREATE_TIME < TO_DATE(:END_TIME, 'YYYY-MM-DD') + 1");
        parser.addSQL(" AND T.CUST_STORAGEMOTHOD = :CUST_STORAGEMOTHOD");

        parser.addSQL(" ORDER BY T.CREATE_TIME DESC ");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryCampnProudcts(String campnId) throws Exception
    {
        IData param = new DataMap();
        param.put("CAMPN_ID", campnId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" select b.product_id, b.product_name");
        parser.addSQL(" from tf_sm_campaign_product a, td_b_product b");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL(" and a.product_id = b.product_id");
        parser.addSQL(" and a.campn_id = :CAMPN_ID ");

        return Dao.qryByParse(parser);
    }
}
