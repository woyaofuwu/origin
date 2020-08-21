
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BlackPsptManageQry
{

    public static void delete(IData params) throws Exception
    {
        Dao.delete("TF_F_PSPT_BLACK", params);
    }

    public static void insert(IDataset params) throws Exception
    {
        Dao.insert("TF_F_PSPT_BLACK", params);
    }

    public static IData qryByPk(IData params) throws Exception
    {
        return Dao.qryByPK("TF_F_PSPT_BLACK", params);
    }

    /**
     * 黑名单证件是否存在
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryBlackExist(String psptid, String pspttypecode) throws Exception
    {
        IData params = new DataMap();
        params.put("PSPT_ID", psptid);
        params.put("PSPT_TYPE_CODE", pspttypecode);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT t.* FROM TF_F_PSPT_BLACK t WHERE 1 = 1 ");
        parser.addSQL("AND PSPT_TYPE_CODE = :PSPT_TYPE_CODE ");
        parser.addSQL("AND PSPT_ID = :PSPT_ID ");
        return Dao.qryByParse(parser);
        // return Dao.qryByPK("TF_F_PSPT_BLACK", params, new String[]{"PSPT_ID","PSPT_TYPE_CODE"});

    }

    /**
     * 黑名单证件查询
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryBlackList(String eparchycode, String pspttypecode, String psptid, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("EPARCHY_CODE", eparchycode);
        params.put("PSPT_TYPE_CODE", pspttypecode);
        params.put("PSPT_ID", psptid);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT t.* FROM TF_F_PSPT_BLACK t WHERE 1 = 1 ");
        parser.addSQL("AND EPARCHY_CODE = :EPARCHY_CODE ");
        parser.addSQL("AND PSPT_TYPE_CODE = :PSPT_TYPE_CODE ");
        parser.addSQL("AND PSPT_ID LIKE '%'||:PSPT_ID||'%' ");
        return Dao.qryByParse(parser, pagination);
    }

    public static boolean save(IData params) throws Exception
    {
        return Dao.save("TF_F_PSPT_BLACK", params);
    }
}
