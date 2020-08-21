
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UecMmsQry
{

    public static String getMmsNoticeId() throws Exception
    {
        StringBuilder sql = new StringBuilder("SELECT UOP_UEC.F_UEC_GETSEQID('SEQ_MMS_ID') MMS_NOTICE_ID FROM DUAL");
        IData param = new DataMap();
        IDataset result = Dao.qryBySql(sql, param, "uec");
        return result.getData(0).getString("MMS_NOTICE_ID", "0");
    }

    public static IDataset queryTemplateInfos(IData data) throws Exception
    {
        // 模板ID及参数校验
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT MMS_TEMPLATE_ID,MMS_ANNEX_TYPE,MMS_ANNEX_TEXT FROM TF_MMS_TEMPLATE ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND MMS_ANNEX_TYPE = 14 ");
        parser.addSQL(" AND MMS_TEMPLATE_ID = :MMS_TEMPLATE_ID");

        return Dao.qryByParse(parser, "uec");// dao.queryList(parser);
    }
}
