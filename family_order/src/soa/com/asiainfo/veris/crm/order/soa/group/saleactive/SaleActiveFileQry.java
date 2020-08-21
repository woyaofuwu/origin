package com.asiainfo.veris.crm.order.soa.group.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleActiveFileQry
{
    
    /**
     * 根据FILE_ID删除表里的数据
     * @param fileId
     * @throws Exception
     */
    public static void delSaleFileByFileId(String fileId) throws Exception
    {
        IData param = new DataMap();
        param.put("FILE_ID", fileId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("DELETE FROM TF_F_GROUP_FTPFILE WHERE FILE_ID = :FILE_ID");
        Dao.executeUpdate(sql, param);
    }
    
}