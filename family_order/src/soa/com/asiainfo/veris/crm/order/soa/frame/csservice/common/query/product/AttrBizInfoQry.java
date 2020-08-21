
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UAttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class AttrBizInfoQry
{
    public static String getAttrValueBy1BAttrCodeObj(String attrCode, String attrObj) throws Exception
    {
        String attrValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
        { "1", "B", attrCode, attrObj });

        return attrValue;
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ、ATTR_CODE查询
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getBizAttr(String id, String idType, String attrObj, String attrCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("ATTR_CODE", attrCode);

        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_BY_PK", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ、ATTR_VALUE查询，用于根据BBOSS数据查询产品模型数据
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author sht
     */
    public static IDataset getBizAttrByAttrValue(String id, String idType, String attrObj, String attrValue, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("ATTR_VALUE", attrValue);

        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_BY_ATTR_VALUE", param, pagination, Route.CONN_CRM_CEN);
    }



    /**
     * 根据ID、ID_TYPE、ATTR_OBJ、ATTR_CODE查询Attr_Biz表
     * 
     * @author luojh 2009-08-18
     * @param param查询参数
     * @return 产品信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getBizAttrByDynamic(String id, String idType, String attrObj, String attrCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("ATTR_CODE", attrCode);

        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_BY_PK", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、类型、类别、地区查询产品控制信息
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author liaoyi
     */
    public static IDataset getBizAttrByGrpSms(String tradeTypeCode, String smsType, String eparchyCode, String inModeCode, String productId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SMS_TYPE", smsType);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_GRP_SMS", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ、ATTR_CODE查询
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author ykx
     */
    public static IData getBizAttrByIdTypeObj2Data(String id, String idType, String attrObj) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);

        IDataset ids = UAttrBizInfoQry.getBizAttrByIdTypeObj(id, idType, attrObj, null);

        IData para = new DataMap();

        if (IDataUtil.isEmpty(ids))
        {
            return para;
        }

        for (int index = 0, isize = ids.size(); index < isize; index++)
        {
            IData map = ids.getData(index);

            String key = map.getString("ATTR_CODE");
            String value = map.getString("ATTR_VALUE", "");

            para.put(key, value);
        }

        return para;
    }

    /**
     * 根据ID、类型、类别、代码、地区查询产品控制信息
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author liaoyi
     */
    public static IDataset getBizAttrByIdTypeObjCodeEparchy(String id, String idType, String attrObj, String attrCode, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("ATTR_CODE", attrCode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_BY_ID_TYPE_OBJ_CODE_EPARCHY", param, pagination, Route.CONN_CRM_CEN);
    }

    // add by MengJunhua 2011-06-22 begin copy from hunan
    /**
     * 根据ID、类型、类别、地区查询产品控制信息
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author liaoyi
     */
    public static IDataset getBizAttrByIdTypeObjEparchy(String id, String idType, String attrObj, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_BY_ID_TYPE_OBJ_EPARCHY", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ID、类型、类别查询产品控制信息
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author xiajj
     */
    public static IDataset getBizAttrByIdTypeObjRemark(IData param) throws Exception
    {
        // TODO
        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_BY_ID_TYPE_OBJ1", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getBizAttrCount(String id, String idType, String attrObj, String attrCode, String attrValue) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("ATTR_CODE", attrCode);
        param.put("ATTR_VALUE", attrValue);

        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_BY_ATTRCODE_ATTRVALUE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询BBOSS产品的分省支付资费
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getProvPayDiscnt(String merchpId) throws Exception
    {

        IData param = new DataMap();
        param.put("MERCHPID", merchpId);
        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_PROVPAYDISCNT_BYMERCHPID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getTradeTypeCode1(String tradeTypeCode, String attrName, String attrCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SMS_TYPE", attrName);
        param.put("EPARCHY_CODE", attrCode);
        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_BY_TRADE_TYPE_CODE1", param, Route.CONN_CRM_CEN);
    }
    public static IDataset getPfByAttrCode(String pId) throws Exception
    {

        IData param = new DataMap();
        param.put("ATTR_CODE", pId);
        return Dao.qryByCodeParser("TD_B_ATTR_BIZ", "SEL_ATTRCODE_BYID", param, Route.CONN_CRM_CEN);
    }
}
