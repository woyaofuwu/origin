
package com.asiainfo.veris.crm.order.soa.group.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GroupProductInfoQry
{

    public IDataset qryProductType(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT DISTINCT A.PRODUCT_TYPE_CODE, A.PRODUCT_TYPE_NAME");
        parser.addSQL("  FROM TD_S_PRODUCT_TYPE A, TD_B_PTYPE_PRODUCT B");
        parser.addSQL(" WHERE A.PRODUCT_TYPE_CODE = B.PRODUCT_TYPE_CODE");
        parser.addSQL("   AND PARENT_PTYPE_CODE = '1000' ORDER BY PRODUCT_TYPE_CODE");

        return Dao.qryByParse(parser);
    }

    public IDataset qryProductList(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT C.PRODUCT_TYPE_CODE, ");
        parser.addSQL("       C.PRODUCT_TYPE_NAME, ");
        parser.addSQL("       A.PRODUCT_ID, ");
        parser.addSQL("       A.BRAND_CODE, ");
        parser.addSQL("       A.PRODUCT_NAME, ");
        parser.addSQL("       A.PRODUCT_MODE, ");
        parser.addSQL("       D.USE_TAG");
        parser.addSQL(" FROM TD_B_PRODUCT       A, ");
        parser.addSQL("      TD_B_PTYPE_PRODUCT B, ");
        parser.addSQL("      TD_S_PRODUCT_TYPE  C, ");
        parser.addSQL("      TD_B_PRODUCT_COMP  D ");
        parser.addSQL("WHERE A.PRODUCT_ID = B.PRODUCT_ID ");
        parser.addSQL("  AND B.PRODUCT_TYPE_CODE = C.PRODUCT_TYPE_CODE ");
        parser.addSQL("  AND A.PRODUCT_ID = D.PRODUCT_ID ");
        parser.addSQL("  AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");
        parser.addSQL("  AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE ");
        parser.addSQL("  AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE ");
        parser.addSQL("  AND A.RELEASE_TAG = '1' ");
        parser.addSQL("  AND A.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL("  AND A.PRODUCT_NAME like '%' || :PRODUCT_NAME || '%' ");
        parser.addSQL("  AND C.PRODUCT_TYPE_CODE = :PRODUCT_TYPE_CODE ");
        parser.addSQL("  AND EXISTS ");
        parser.addSQL("  (SELECT 1 ");
        parser.addSQL("     FROM TD_B_PRODUCT_RELEASE T ");
        parser.addSQL("    WHERE T.PRODUCT_ID = A.PRODUCT_ID ");
        parser.addSQL("      AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE) ");
        parser.addSQL(" ORDER BY C.PRODUCT_TYPE_CODE, A.PRODUCT_ID ");

        IDataset productList = Dao.qryByParse(parser);

        for (int i = 0, row = productList.size(); i < row; i++)
        {
            IData productData = productList.getData(i);

            String productId = productData.getString("PRODUCT_ID");

            productData.put("CrtUs", qryAttrBizInfo(productId, "CrtUs"));
            productData.put("ChgUs", qryAttrBizInfo(productId, "ChgUs"));
            productData.put("DstUs", qryAttrBizInfo(productId, "DstUs"));
            productData.put("CrtMb", qryAttrBizInfo(productId, "CrtMb"));
            productData.put("ChgMb", qryAttrBizInfo(productId, "ChgMb"));
            productData.put("DstMb", qryAttrBizInfo(productId, "DstMb"));
        }

        return productList;
    }

    public IData qryAttrBizInfo(String productId, String operType) throws Exception
    {
        IData param = new DataMap();

        param.put("PRODUCT_ID", productId);
        param.put("ATTR_OBJ", operType);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT ATTR_OBJ, ATTR_VALUE ");
        parser.addSQL("   FROM TD_B_ATTR_BIZ A ");
        parser.addSQL("  WHERE A.ID_TYPE = 'P' ");
        parser.addSQL("    AND A.ATTR_OBJ = :ATTR_OBJ ");
        parser.addSQL("    AND A.ATTR_CODE = 'TradeTypeCode' ");
        parser.addSQL("    AND A.ID = :PRODUCT_ID ");

        IDataset attrBizList = Dao.qryByParse(parser);

        IData retData = new DataMap();

        if (IDataUtil.isEmpty(attrBizList))
        {
            return retData;
        }

        IData attrBizData = attrBizList.getData(0);

        String attrValue = attrBizData.getString("ATTR_VALUE");

        retData.put("TRADE_TYPE_CODE", attrValue);

        retData.put("ISPF", qryTradeCtrl(attrValue, "isPf", "U"));
        retData.put("TRADEBACK", qryTradeCtrl(attrValue, "tradeBack", "0"));

        return retData;
    }

    public String qryTradeCtrl(String tradeTypeCode, String paramName, String defaultValue) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("PARAM_NAME", paramName);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT PARAM_VALUE ");
        parser.addSQL("  FROM TD_S_TRADECTRL ");
        parser.addSQL(" WHERE PARAM_NAME = :PARAM_NAME ");
        parser.addSQL("   AND SYSDATE BETWEEN START_DATE AND END_DATE");
        parser.addSQL("   AND TRADE_TYPE_CODE = :TRADE_TYPE_CODE");

        IDataset tradeCtrlList = Dao.qryByParse(parser);

        if (IDataUtil.isEmpty(tradeCtrlList))
        {
            return defaultValue;
        }

        return tradeCtrlList.getData(0).getString("PARAM_VALUE");
    }
}
