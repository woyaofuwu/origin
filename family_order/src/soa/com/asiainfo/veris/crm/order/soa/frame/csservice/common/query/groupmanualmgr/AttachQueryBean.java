
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.groupmanualmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AttachQueryBean
{

    private static IDataset queryFilefromTDMFile(String fileName, String startDate, String endDate, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("FILE_NAME", fileName);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT T.FILE_ID,T.FILE_NAME,T.FILE_SIZE,T.CREA_TIME");
        parser.addSQL(" FROM TD_M_FILE T");
        parser.addSQL(" WHERE 1=1");
        if (StringUtils.isNotEmpty(fileName))
            parser.addSQL(" AND T.FILE_NAME =:FILE_NAME");
        if (StringUtils.isNotEmpty(startDate))
            parser.addSQL(" AND T.CREA_TIME >= TO_DATE(:START_DATE, 'yyyy-MM-dd')");
        if (StringUtils.isNotEmpty(endDate))
            parser.addSQL(" AND T.CREA_TIME <= TO_DATE(:END_DATE, 'yyyy-MM-dd')");

        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    private static IDataset queryFilefromTiMFile(String fileID, String groupID, String poSpecNumber, String productSpecNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("FILE_ID", fileID);
        param.put("PRODUCTSPECNUMBER", productSpecNumber);
        param.put("POSPECNUMBER", poSpecNumber);
        param.put("GROUP_ID", groupID);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.FILE_ID,T.GROUP_ID, T.MERCH_SPEC_CODE, T.PRODUCT_SPEC_CODE, T.PRODUCT_ORDER_ID");
        parser.addSQL(" FROM TD_M_BBOSS_FILE T");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.FILE_ID = :FILE_ID");
        if (StringUtils.isNotEmpty(groupID))
            parser.addSQL(" AND T.GROUP_ID = :GROUP_ID");
        if (StringUtils.isNotEmpty(poSpecNumber))
            parser.addSQL(" AND T.MERCH_SPEC_CODE =:POSPECNUMBER");
        if (StringUtils.isNotEmpty(productSpecNumber))
            parser.addSQL(" AND T.PRODUCT_SPEC_CODE =:PRODUCTSPECNUMBER");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryFileInfobyName(String condGroupID, String condFileName, String condPoSpecNumber, String condProductSpecNumber, String condStartDate, String condEndDate, Pagination page) throws Exception
    {
        // 查TD_M_File表
        IDataset resultset = queryFilefromTDMFile(condFileName, condStartDate, condEndDate, page);// 从TD_M_FILE表中取出对应的数据
        if (resultset.size() == 0)
        {
            return resultset;
        }
        for (int i = 0; i < resultset.size(); i++)
        {
            // 取出一条查询
            IData fileData = resultset.getData(i);
            // 查询TD_M_File表
            IDataset result = queryFilefromTiMFile(fileData.getString("FILE_ID"), condGroupID, condPoSpecNumber, condProductSpecNumber);
            // 若为空则取下一条数据继续循环
            if (IDataUtil.isEmpty(result))
            {
                continue;
            }
            // 拼接数据
            else
            {
                fileData.put("GROUP_ID", result.getData(0).getString("GROUP_ID"));
                fileData.put("MERCH_SPEC_CODE", result.getData(0).getString("MERCH_SPEC_CODE"));
                fileData.put("PRODUCT_SPEC_CODE", result.getData(0).getString("PRODUCT_SPEC_CODE"));
                fileData.put("PRODUCT_ORDER_ID", result.getData(0).getString("PRODUCT_ORDER_ID"));
            }
        }

        return resultset;
    }
}
