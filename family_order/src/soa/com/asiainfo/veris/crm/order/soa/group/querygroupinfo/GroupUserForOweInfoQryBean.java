package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GroupUserForOweInfoQryBean extends GroupBean
{
    
    /**
     * 查询集团客户下的办理的集团产品
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUserOweInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT T.CITY_CODE       CITY_CODE, ");
        parser.addSQL("       T.CUST_NAME       CUST_NAME, ");
        parser.addSQL("       T.GROUP_ID        GROUP_ID, ");
        parser.addSQL("       T.CUST_MANAGER_ID CUST_MANAGER_ID, ");
        parser.addSQL("       T.CUST_NAME PRODUCT_NAME, ");
        parser.addSQL("       U.USER_ID         USER_ID, ");
        parser.addSQL("       U.SERIAL_NUMBER   SERIAL_NUMBER, ");
        parser.addSQL("       U.CUST_ID         CUST_ID, ");
        parser.addSQL("       U.USER_STATE_CODESET PRODUCT_STATE ");
        parser.addSQL("  FROM TF_F_CUST_GROUP T, TF_F_USER U ");
        parser.addSQL(" WHERE T.REMOVE_TAG = '0' ");
        parser.addSQL("   AND T.CITY_CODE = :CITY_CODE ");
        parser.addSQL("   AND T.GROUP_ID = :GROUP_ID ");
        parser.addSQL("   AND T.CUST_MANAGER_ID = :CUST_MANAGER_ID ");
        parser.addSQL("   AND T.CUST_ID = U.CUST_ID ");
        parser.addSQL("   AND U.REMOVE_TAG = '0' ");
        parser.addSQL("   AND U.USER_STATE_CODESET != '0' ");
        
        return Dao.qryByParse(parser, pagination);

    }
    
}