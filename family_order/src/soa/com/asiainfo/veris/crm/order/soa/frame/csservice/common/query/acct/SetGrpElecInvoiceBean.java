package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

public class SetGrpElecInvoiceBean
{
    // 查询用户信息并显示
    public static IDataset queryUserInfo(IData inparams) throws Exception
    {
        IDataset crmResults = Dao.qryByCode("TF_F_USER", "SEL_CUSTID_GRP_ALL", inparams, Route.CONN_CRM_CG);
        IDataset jourResults = Dao.qryByCode("TF_B_TRADE_USER", "SEL_CUSTID_GRP", inparams, Route.getJourDb("0898"));
        if (IDataUtil.isEmpty(crmResults) && IDataUtil.isEmpty(jourResults))
        {
            return new DatasetList();
        }
        
        IDataset results = new DatasetList();
        results.addAll(crmResults);
        results.addAll(jourResults);

        for (int i = 0, size = results.size(); i < size; i++)
        {
            IData result = results.getData(i);
            // result.put("PAY_MODE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(result.getString("PAY_MODE_CODE")));
            result.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(result.getString("EPARCHY_CODE")));
            result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
        }
        
        DataHelper.sort(results, "REMOVE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND,"OPEN_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);

        return results;
    }

    // 根据userId和custId查询电子发票信息并展示
    public static IDataset qryEPostInfoByUserID(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", input.getString("CUST_ID"));
        param.put("USER_ID", input.getString("USER_ID"));
        return Dao.qryByCode("TF_F_GRPACCT_EPOSTINFO", "SEL_BYUSERID", param, Route.CONN_CRM_CG);

    }

    public static IDataset QueryGrpEpostinfoByUserId(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_GRPACCT_EPOSTINFO", "SEL_EPOSTINFO_BYUSERID", param, Route.CONN_CRM_CG);

    }

    public static IDataset qryEPostInfoByTag(IData param) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", param.getString("USER_ID"));
        data.put("POST_TAG", param.getString("POST_TAG"));
        return Dao.qryByCode("TF_F_GRPACCT_EPOSTINFO", "SEL_EPOSTINFO_BYPOSTTAG", data, Route.CONN_CRM_CG);

    }

    public static IDataset qryAcctInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM tf_a_payrelation ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and user_id=TO_NUMBER(:USER_ID) ");
        parser.addSQL(" and partition_id=mod(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" and end_cycle_id > to_number(to_char(trunc(last_day(sysdate)) + 1, 'yyyymmdd')) ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    // public static String QryEparchyCodeByCustId(String custId) throws Exception
    // {
    // IData param=new DataMap();
    // param.put("CUST_ID", custId);
    // SQLParser parser =new SQLParser(param);
    // parser.addSQL("select *from TF_F_CUST_GROUP  where 1=1 and REMOVE_TAG='0' and CUST_ID=:CUST_ID  ");
    // IDataset ret= Dao.qryByParse(parser,Route.CONN_CRM_CG);
    // String eparchyCode=ret.getData(0).getString("EPARCHY_CODE");
    // return eparchyCode;
    //
    // }
    // 根据CUSTID,POSTTAG查询电子发票信息并展示
    public static IDataset qryEPostInfoByCustID(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", input.getString("CUST_ID"));
        param.put("USER_ID", input.getString("USER_ID"));
        param.put("POST_TAG", "2");
        return Dao.qryByCodeParser("TF_F_GRPACCT_EPOSTINFO", "SEL_EPOSTINFO_BYCUSTID", param, Route.CONN_CRM_CG);
    }

}
