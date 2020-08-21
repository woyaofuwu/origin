
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.print;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class PrintItem
{

    /**
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getTempletItem(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT ti.ITEM_TYPE, ");
        parser.addSQL(" ti.ITEM_CONTENT, ");
        parser.addSQL(" ti.ITEM_WIDTH, ");
        parser.addSQL(" ti.ITEM_HEIGHT, ");
        parser.addSQL(" ti.ITEM_TOP, ");
        parser.addSQL(" ti.ITEM_LEFT, ");
        parser.addSQL(" ti.ITEM_ALIGNMENT, ");
        parser.addSQL(" ti.FONT_SIZE, ");
        parser.addSQL(" ti.FONT_BOLD, ");
        parser.addSQL(" ti.FONT_COLOR, ");
        parser.addSQL(" ti.FONT_UNDERLINE ");
        parser.addSQL(" FROM TD_B_CNOTE_TEMPLET t, ");
        parser.addSQL(" TD_B_CNOTE_TEMPLET_ITEM ti, ");
        parser.addSQL(" TD_B_CNOTE_TEMPLET_RELA r ");
        parser.addSQL(" WHERE t.TEMPLET_TYPE = :TEMPLET_TYPE ");
        parser.addSQL(" AND ti.templet_code = r.TEMPLET_CODE ");
        parser.addSQL(" AND r.TEMPLET_CODE = t.TEMPLET_CODE ");
        parser.addSQL(" AND t.USE_TAG = '0' ");
        parser.addSQL(" AND r.RELATION_KIND = '0' ");
        parser.addSQL(" AND r.RELATION_ATTR = :TRADE_EPARCHY_CODE ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
