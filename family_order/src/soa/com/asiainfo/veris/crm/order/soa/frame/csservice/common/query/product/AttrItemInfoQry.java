
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class AttrItemInfoQry
{
    /**
     * 查询是否用户群录入
     */
    public static IDataset CanInserUserName(String id, Pagination pagination) throws Exception
    {
        String idType = "P";
        String attrCode = "InstUser";
        String attrObj = "InstUser";
        String eparchyCode = CSBizBean.getUserEparchyCode();

        return qryTemplate(id, idType, attrCode, attrObj, eparchyCode, pagination);

    }

    /**
     * 查询是否支持合户
     */
    public static IDataset CanSameAcct(String id, Pagination pagination) throws Exception
    {
        String idType = "P";
        String attrCode = "CanSameAct";
        String attrObj = "CanSameAct";
        String eparchyCode = CSBizBean.getUserEparchyCode();

        return qryTemplate(id, idType, attrCode, attrObj, eparchyCode, pagination);

    }

    public static IDataset getAttrItemAByADC(String id, String idtype, String eparchyCode, String attrtypecode) throws Exception
    {
        return UItemAInfoQry.qryOfferChaSpecByOfferIdShowMode(id, idtype, attrtypecode);
    }

    public static IDataset getAttrItemAByIDTO(IData param, Pagination pagination) throws Exception
    {
        return UItemAInfoQry.queryOfferChaByCond(param.getString("ID"), param.getString("ID_TYPE"), param.getString("ATTR_OBJ"), param.getString("EPARCHY_CODE"), pagination);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     * @author ykx
     */
    public static IDataset getAttrItemAByIDTO(String id, String idType, String attrObj, String eparchyCode, Pagination pagination) throws Exception
    {
        //return UItemAInfoQry.queryOfferChaByCond(id, idType, attrObj, eparchyCode, pagination);
        return UItemAInfoQry.queryOfferChaAndValByCond(id, idType, attrObj, null);
    }

    public static IDataset getAttrItemBInfoByFieldCode(String id, String idtype, String attrCode, String attrFieldCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        if (StringUtils.isEmpty(idtype))
            idtype = "P";
        if (StringUtils.isEmpty(eparchyCode))
            eparchyCode = CSBizBean.getTradeEparchyCode();
        param.put("ID", id);
        param.put("ID_TYPE", idtype);
        param.put("ATTR_CODE", attrCode);
        param.put("ATTR_FIELD_CODE", attrFieldCode);
        param.put("EPARCHY_CODE", eparchyCode);
//        IData resultData = UItemBInfoQry.qryChaSpecialVal(attrCode, idtype, attrCode, attrFieldCode);  //modify hefeng
           IData resultData = UItemBInfoQry.queryChaSpecByfieldNameAndvalueAndOfferId(idtype, id, attrCode, attrFieldCode);
        if (IDataUtil.isEmpty(resultData))
        {
            return null;
        }
        IDataset results = new DatasetList();
        results.add(resultData);
        return results;
    }

    public static IDataset getElementAttrs(String idType, String elementId, String productId, String eparchyCode) throws Exception
    {
        IDataset elementAttrs = new DatasetList();
        
        //查询自定义弹窗配置
        if(StringUtils.isNotBlank(idType) && ("S".equals(idType)||"D".equals(idType))){
            IDataset servPageSet = AttrBizInfoQry.getBizAttr(elementId, idType, "ServPage", productId, null);
            if(IDataUtil.isNotEmpty(servPageSet))
            {
                IData servPage = servPageSet.getData(0);
                IData attrItemData = new DataMap();
                attrItemData.put("ID", elementId);
                attrItemData.put("ID_TYPE", idType);
                attrItemData.put("ATTR_CODE", servPage.getString("ATTR_CODE"));
                attrItemData.put("ATTR_TYPE_CODE", "9");
                attrItemData.put("ATTR_LABLE", "自定义弹窗");
                attrItemData.put("ATTR_HINT", "自定义弹窗");
                attrItemData.put("ATTR_INIT_VALUE", "");
                attrItemData.put("ATTR_CAN_NULL", "");
                attrItemData.put("ATTR_FIELD_CODE", servPage.getString("ATTR_NAME"));
                attrItemData.put("ATTR_FIELD_NAME", servPage.getString("ATTR_VALUE"));
                attrItemData.put("ATTR_FIELD_TYPE_WADE", "");
                attrItemData.put("RSRV_STR1", servPage.getString("RSRV_STR1"));
                attrItemData.put("RSRV_STR2", servPage.getString("RSRV_STR2"));
                attrItemData.put("RSRV_STR3", servPage.getString("RSRV_STR3"));
                attrItemData.put("RSRV_STR4", servPage.getString("RSRV_STR4"));
                attrItemData.put("RSRV_STR5", servPage.getString("RSRV_STR5"));
                elementAttrs.add(attrItemData);
                
                return elementAttrs;
            }
        }
        
        elementAttrs = UItemAInfoQry.qryOfferChaSpecsByIdAndIdType(idType, elementId, eparchyCode );
        return elementAttrs;
        
    }

    public static IDataset getElementAttr4ProFlow(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select para_code1 from td_s_commpara where PARAM_ATTR =:PARAM_ATTR and SUBSYS_CODE=:SUBSYS_CODE and PARAM_CODE =:PARAM_CODE ");
        parser.addSQL(" and start_date<sysdate and end_date>sysdate ");
        return Dao.qryByParse(parser);
    }
    
    public static IDataset getElementItemA(String idType, String elementId, String eparchyCode) throws Exception
    {
        /*IData params = new DataMap();
        params.put("ID", elementId);
        params.put("ID_TYPE", idType);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_PK", params, Route.CONN_CRM_CEN);*/
    	
//        IDataset upcDatas = UpcCall.queryOfferChaValByOfferIdAndMgmtDistict(idType,elementId, eparchyCode);
//        for(int i = 0 ; i < upcDatas.size() ; i ++)
//        {
//        	IData upcData = upcDatas.getData(i);
//        	String attrCode = upcData.getString("FIELD_NAME");
//        	String rsrvStr2 = upcData.getString("FIELD_VALUE");
//        	
//        	upcData.put("ATTR_CODE", attrCode);
//        	upcData.put("RSRV_STR2", rsrvStr2);
//        }
//        return upcDatas;
        
        return UItemAInfoQry.queryOfferChaByIdAndIdType(idType, elementId, eparchyCode );
        
    }
    
    public static IDataset getElementItemA4Plat(String elementId, String eparchyCode) throws Exception
    {
    	IDataset offerChaAndVals = UpcCall.queryOfferChaValByOfferIdAndMgmtDistict(elementId, eparchyCode);
    	
    	for (int i = 0, size = offerChaAndVals.size(); i < size; i++)
        {
            IData offerChaAndVal = offerChaAndVals.getData(i);
            offerChaAndVal.put("ATTR_CAN_NULL", offerChaAndVal.getString("IS_NULL"));
            offerChaAndVal.put("DISPLAY_CONDITION", offerChaAndVal.getString("RSRV_STR2"));
            offerChaAndVal.put("ATTR_LABLE", offerChaAndVal.getString("CHA_SPEC_NAME"));
        }
    	return offerChaAndVals;
    }

    public static IDataset getelementItemaByPk(IData param) throws Exception
    {
        return UItemAInfoQry.queryOfferChaByIdAndIdType(param.getString("ID_TYPE"), param.getString("ID"), null);
    }

    /**
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author zouli
     */
    public static IDataset getelementItemaByPk(String id, String idType, String eparchyCode, Pagination pagination) throws Exception
    {
        return UItemAInfoQry.queryOfferChaByIdAndIdType(idType, id, eparchyCode);
    }

    /**
     * 根据产品ID 元素类型 参数显示类型 查询itema表元素配置情况 入参 ELEMENT_TYPE_CODE 元素类型 ATTR_TYPE_CODE 元素显示类型 PRODUCT_ID 产品ID
     * ProductDom::ATTR_ITEMA::TD_B_ATTR_ITEMA::SEL_SEVITEM_BY_PID_IDTYEP
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author zouli
     */
    public static IDataset getelementItemaByProductId(String elementTypeCode, String attrTypeCode, String productId, Pagination pagination) throws Exception
    {
//        IData param = new DataMap();
//        param.put("ELEMENT_TYPE_CODE", elementTypeCode);
//        param.put("ATTR_TYPE_CODE", attrTypeCode);
//        param.put("PRODUCT_ID", productId);
//
//        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_SEVITEM_BY_PID_IDTYEP", param, pagination, Route.CONN_CRM_CEN);
    	return UItemAInfoQry.qryChaSpeByOfferCodeP(productId, elementTypeCode,attrTypeCode);
    }

    /**
     * 根据RATEPLANID，查ICB参数
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getIcbsByRatePlan(String rateplanid) throws Exception
    {
        IData param = new DataMap();
        param.put("RATEPLANID", rateplanid);

        return Dao.qryByCode("TD_F_PORATEPLANICB", "SEL_BY_RATEPLANID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品ID 元素类型 参数显示类型 查询itemb表元素配置情况
     * 
     * @author luojh 2009-08-08 11:28
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     */
    public static IDataset getItembByIdAndType(String id, String idType, String attrCode, String eparchyCode) throws Exception
    {
        return UItemBInfoQry.qryOfferChaValByFieldNameOfferCodeAndOfferType(id, idType, attrCode);
    }

    public static IDataset qryAttrBiz(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_BY_PK", inparam, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据服务标志查询平台服务信息
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrItemForPlatSvc(String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", serviceId);

        SQLParser parser = new SQLParser(param);

        /*parser.addSQL(" select * from td_b_attr_itema t where to_char(t.id)=t.attr_code and t.attr_type_code='9' ");
        parser.addSQL("  and exists ( select 1 from td_b_attr_itemb b where b.attr_field_code in('group.param.adc.UserParamInfo','group.param.mas.UserParamInfo','group.param.qadc.UserParamInfo') ");
        parser.addSQL("  and t.id = b.id ");
        parser.addSQL("  and t.id = :ID) ");*/
        
        parser.addSQL(" SELECT * FROM TD_B_ATTR_BIZ T ");
        parser.addSQL("  WHERE T.ID = :ID ");
        parser.addSQL("  AND T.ID_TYPE = 'S' AND T.ATTR_OBJ = 'ServPage' ");
        parser.addSQL("  AND T.ATTR_NAME IN ('group.param.adc.UserParamInfo','group.param.mas.UserParamInfo','group.param.qadc.UserParamInfo') ");

        return Dao.qryByParse(parser);
    }

    /**
     * 查询itema获取attr_code对应的attr_lable
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryItemA(String id, String idType, String attrCode, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_CODE", attrCode);
        param.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.attr_lable ATTR_NAME from td_b_attr_itema a ");
        parser.addSQL(" where a.id = :ID and a.attr_code = :ATTR_CODE and a.id_type = :ID_TYPE and a.start_date < sysdate and a.end_date > sysdate ");
        parser.addSQL(" and (a.eparchy_code = :EPARCHY_CODE or a.eparchy_code = 'ZZZZ') ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    /**
     * 查询详细账目
     * 
     * @param td
     * @throws Exception
     * @author xj
     */
    public static IDataset qryItems(String id, String idType, String attrObj, String attrCode, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("ATTR_CODE", attrCode);
        param.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * ");
        parser.addSQL(" from TD_B_ATTR_BIZ ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" AND ID = :ID ");
        parser.addSQL(" AND ID_TYPE = :ID_TYPE ");
        parser.addSQL(" AND ATTR_OBJ = :ATTR_OBJ ");
        parser.addSQL(" AND ATTR_CODE = :ATTR_CODE ");
        parser.addSQL(" AND (EPARCHY_CODE = :EPARCHY_CODE OR EPARCHY_CODE = 'ZZZZ') ");
        parser.addSQL(" AND sysdate between start_date and end_date ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询模板
     * 
     * @author 廖翊 @
     * @param td
     * @throws Exception
     */
    public static IDataset qryTemplate(String id, String idType, String attrObj, String attrCode, String eparchyCode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ID", id);
        param.put("ID_TYPE", idType);
        param.put("ATTR_OBJ", attrObj);
        param.put("ATTR_CODE", attrCode);
        param.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * ");
        parser.addSQL(" from TD_B_ATTR_BIZ ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" AND ID = :ID ");
        parser.addSQL(" AND ID_TYPE = :ID_TYPE ");
        parser.addSQL(" AND ATTR_OBJ = :ATTR_OBJ ");
        parser.addSQL(" AND ATTR_CODE = :ATTR_CODE ");
        parser.addSQL(" AND (EPARCHY_CODE=:EPARCHY_CODE OR EPARCHY_CODE='ZZZZ') ");
        parser.addSQL(" AND sysdate between start_date and end_date ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 根据服务ID查服务参数A,这个方法的作用是为了解决SI_BASE_IN_CODE 是原始的接入码 而不是在地市基础上扩展两位后的编码。原先的处理办法是截掉后两位，考虑到以后业务发展，扩展规则可能
     * 会变，就没有办法支持了。采用新增配置的方法，就没有问题。
     */
    public static String queryServiceItemA(String serviceId, String attrCode) throws Exception
    {
        IDataset idset = UItemAInfoQry.queryOfferChaByCond(BofConst.ELEMENT_TYPE_CODE_SVC, serviceId, "S", null, "ZZZZ", attrCode);
        return (IDataUtil.isNotEmpty(idset)) ? idset.getData(0).getString("ATTR_INIT_VALUE", "") : "000000";
    }

    /**
     * 查询模板，支持区分地州
     */
    public static IDataset showTemplate(String id, Pagination pagination) throws Exception
    {
        String idType = "P";
        String attrCode = "GrpPayCst";
        String attrObj = "GrpPayCst";
        String eparchyCode = CSBizBean.getUserEparchyCode();

        return qryTemplate(id, idType, attrCode, attrObj, eparchyCode, pagination);

    }

    /**
     * 查询TD_B_ATTR_ITEMB信息
     * 
     * @param id
     * @param idType
     * @param attrCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrItemBByIdAndIdtypeAttrCode(String id, String idType, String attrCode, String eparchyCode) throws Exception
    {
        return UItemBInfoQry.qryOfferChaValByFieldNameOfferCodeAndOfferType(id, BofConst.ELEMENT_TYPE_CODE_PRODUCT, attrCode);
    	/*IData param = new DataMap();
    	
    	param.put("ID", id);
    	param.put("ID_TYPE", idType);
    	param.put("ATTR_CODE", attrCode);
    	param.put("EPARCHY_CODE", eparchyCode);
    	
    	return Dao.qryByCode("TD_B_ATTR_ITEMB", "SEL_BY_PK_SORT", param);*/
    }
}
