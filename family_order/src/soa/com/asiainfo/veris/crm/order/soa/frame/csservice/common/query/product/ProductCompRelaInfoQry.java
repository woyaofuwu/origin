
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompRelaInfoQry;

public class ProductCompRelaInfoQry
{

    /**
     * @Description:根据PRODUCT_ID_B、FORCE_TAG、RELATION_TYPE_CODE获取组合产品关系相关信息
     * @author wusf
     * @date 2010-1-19
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getCompRelaInfoByPIDB(String product_id_b, String force_tag, String relation_type_code) throws Exception
    {
//        IData data = new DataMap();
//        data.put("PRODUCT_ID_B", product_id_b);
//        data.put("FORCE_TAG", force_tag);
//        data.put("RELATION_TYPE_CODE", relation_type_code);
//        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_COMP_RELA", "SEL_FORCE_BYPIDB", data);
        IDataset dataset = UProductCompRelaInfoQry.queryProductRealByProductBAndForceTag(product_id_b,force_tag);
        
        
        return dataset;
    }

    /**
     * 查询组合产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getCompReleInfo(String product_id_a) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID_A", product_id_a);
        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_COMP_RELA", "SEL_BY", data, Route.CONN_CRM_CEN);

        return dataset;
    }

    /**
     * 查询某BBOSS商品的子产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qrySubProductInfos(String product_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT t.* FROM td_b_product_comp_rela t ");
        parser.addSQL(" WHERE  t.product_id_a = :PRODUCT_ID AND t.relation_type_code = '1' "); // relation_type_code =
        // '1'包产品关系
        return Dao.qryByParse(parser);

    }

    /**
     * 查资费包信息
     * 
     * @author shixb
     * @version 创建时间：2009-5-12 下午10:10:25
     */
    public static IDataset queryProductComp(String product_id, String relation_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("RELATION_TYPE_CODE", relation_type_code);

        return  UProductCompRelaInfoQry.getCompReleInfo(product_id); //Dao.qryByCode("TD_B_PRODUCT_COMP_RELA", "SEL_PRODUCT_COMP", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据product_id_b获得组合产品关系
     * 
     * @author shixb
     * @version 创建时间：2009-11-10 上午10:07:33
     */
    public static IDataset queryProductRealByProductB(String product_id_b) throws Exception
    {
        return UProductCompRelaInfoQry.queryProductRealByProductB(product_id_b);
    }
}
