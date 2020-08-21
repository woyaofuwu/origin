
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ProductdLimitInfoQry extends CSBizBean
{
    /**
     * 查询主产品默认的附加产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDefaultPlusProductByProdId(String productId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_EPARCHY_CODE", eparchyCode);

        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_DEFAULTPRODID_FOR_TREE", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getGprsFreeDiscntList(String eparchyCode) throws Exception
    {

        IData param = new DataMap();

        param.put("ELEMENT_TYPE_CODE", "S");

        param.put("ELEMENT_ID", "22");

        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "FREE_DISCNT_SEL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getGprsMonthDiscntList(String eparchyCode) throws Exception
    {

        IData param = new DataMap();

        param.put("ELEMENT_TYPE_CODE", "S");

        param.put("ELEMENT_ID", "22");

        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "BINDED_DISCNT_SEL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getImmediatelyGprsMonthDiscntList(String eparchyCode) throws Exception
    {

        IData param = new DataMap();

        param.put("ELEMENT_TYPE_CODE", "S");

        param.put("ELEMENT_ID", "22");

        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "IMMEDIT_DISCNT_SEL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getLdtxDiscntList(String eparchyCode) throws Exception
    {

        IData param = new DataMap();

        param.put("ELEMENT_TYPE_CODE", "S");

        param.put("ELEMENT_ID", "102");

        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "BINDED_DISCNT_SEL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getLimitNPProduct(String productId, String brandCode, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        if (StringUtils.isNotBlank(brandCode))
        {
            param.put("BRAND_CODE", brandCode);
        }
        if (StringUtils.isNotBlank(startDate))
        {
            param.put("START_DATE", startDate);
        }
        if (StringUtils.isNotBlank(endDate))
        {
            param.put("END_DATE", endDate);
        }

        return Dao.qryByCodeParser("TD_S_PRODUCTLIMIT", "SEL_PRODUCT_LIST_INTF_NP", param, Route.CONN_CRM_CEN);

    }

    public static IDataset getLimitProduct(String productId, String brandCode, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        if (StringUtils.isNotBlank(brandCode))
        {
            param.put("BRAND_CODE", brandCode);
        }
        if (StringUtils.isNotBlank(startDate))
        {
            param.put("START_DATE", startDate);
        }
        if (StringUtils.isNotBlank(endDate))
        {
            param.put("END_DATE", endDate);
        }
        return Dao.qryByCodeParser("TD_S_PRODUCTLIMIT", "SEL_PRODUCT_LIST_INTF", param, Route.CONN_CRM_CEN);

    }

    /**
     * 查询主产品关联的附加产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPlusProductByProdId(String productId, String eparchycode) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_EPARCHY_CODE", eparchycode);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODID_FOR_TREE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 查询子产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getProductAndSubProudct(String productId, String eparchycode) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_EPARCHY_CODE", eparchycode);

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODID_FOR_TREE", data, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getProductLimtByProductId
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-7-15 下午2:27:23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-15 lijm3 v1.0.0 修改原因
     */
    public static IDataset getProductLimtByProductId(String product_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_CHECK_LIMIT_0", data);
    }

    public static IDataset getProductLimtBySelByProductidaFmy(String product_id_a) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID_A", product_id_a);

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODUCTIDA_FMY", data);
    }

    public static IDataset getProductLimtBySelbyProductidaLimittag(String product_id_a, String limit_tag) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID_A", product_id_a);
        data.put("LIMIT_TAG", limit_tag);

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODUCTIDA_LIMITTAG", data);
    }

    /**
     * 查询主产品能否办理td无线优惠
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getTdLimitInfo(String productIdA, String productIdB) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID_A", productIdA);
        data.put("PRODUCT_ID_B", productIdB);

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_MAIN_TD_LIMIT", data);
    }

    /**
     * 查询主产品能否办理副卡优惠
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getViceLimitInfo(String productIdA, String productIdB) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID_A", productIdA);
        data.put("PRODUCT_ID_B", productIdB);

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_MAIN_VICE_LIMIT", data);
    }

    /**
     * 根据产品id 查询产品关系
     * 
     * @author jch
     */
    public static IDataset queryProductLimtByProductId(String productId, String limit_tag) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("LIMIT_TAG", limit_tag);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select product_id_a,product_id_b,limit_tag from td_s_productlimit where product_id_a=:PRODUCT_ID and limit_tag=:LIMIT_TAG and sysdate>start_date and sysdate<end_date ");
        return Dao.qryByParse(parser);
    }

    /**
     * 判断一个优惠是不是GPRS优惠
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getGprsDiscnt(String discnt_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("DISCNT_CODE", discnt_code);
        data.put("EPARCHY_CODE", eparchy_code);

        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_GPRSFREEDIS_BY_DISCODE1", data);

    }
    
    /**
     * REQ201603280028 关于新增集团机惠专享积分购机活动的需求（优化规则及新增参数）
     */
    public static IDataset getSaleActiveLimitProd(String productId_B) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID_B", productId_B);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * FROM ucr_cen1.TD_B_SALE_TRADE_LIMIT A WHERE limit_type='1587' and A.product_id=:PRODUCT_ID_B ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

}
