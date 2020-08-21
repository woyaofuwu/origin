
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UElementLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class ElemLimitInfoQry
{
    /**
     * 判断校园宽带元素是否互斥
     * 
     * @param newDisnct
     * @param existDisnct
     * @param limitTag
     * @return
     * @throws Exception
     */
    public static IDataset checkElementLimitByElementIdAB(String newDisnct, String existDisnct, String limitTag) throws Exception
    {
        IData param = new DataMap();
        param.put("SPEC_DISCNT_NEW", newDisnct);
        param.put("SPEC_DISCNT_HAVE", existDisnct);
        param.put("LIMIT_TAG", "0");
        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_SPEC_DISCNT_MUTEX", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getElementInfoByElementId(String elementIdA, String elementTypeCodeA, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_ID_A", elementIdA);
        param.put("ELEMENT_TYPE_CODE_A", elementTypeCodeA);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELEMENTID_A_TAGS", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据ELEMENT_ID_A查询元素依赖互斥表中的数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getElementLimitByEleId(String elementIdA, String elementTypeCodeA, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_ID_A", elementIdA);
        param.put("ELEMENT_TYPE_CODE_A", elementTypeCodeA);
        param.put("EPARCHY_CODE", param.getString("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()));

        IDataset dataset = Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELE_ID_A", param, pagination, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    /**
     * 查指定资费元素限制信息
     * 
     * @return
     * @throws Exception
     */

    public static IDataset getElementLimitByElementId(String discntCode) throws Exception
    {
        IData param = new DataMap();
        param.put("DISCNT_CODE", discntCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT ELEMENT_TYPE_CODE_A, ELEMENT_ID_A, ELEMENT_TYPE_CODE_B, ELEMENT_ID_B,  LIMIT_TAG, ");
        parser.addSQL(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL(" TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK ");
        parser.addSQL(" FROM TD_B_ELEMENT_LIMIT T ");
        parser.addSQL(" WHERE T.ELEMENT_ID_B = :DISCNT_CODE ");
        parser.addSQL(" AND T.LIMIT_TAG = '0' ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查VPMN套餐（3/5/8元套餐） 元素限制信息
     * 
     * @return
     * @throws Exception
     */

    public static IDataset getElementLimitByElementId358() throws Exception
    {
        
        return UElementLimitInfoQry.getElementLimitByElementId358();
    }

    /**
     * 获取流量王优惠依赖的gprs套餐(流量王优惠和依赖的gprs套餐是1对1的)
     * 
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getGprsKingDiscntByGprs(String discntCode) throws Exception
    {
        IData param = new DataMap();
        param.clear();
        param.put("DISCNT_CODE", discntCode);

        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_LLWDISCNT_YL_GPRSDISCNT", param, Route.CONN_CRM_CEN);
    }

    /**
     * 通过新产品判断是否需要选择VPMN优惠
     * 
     * @param discntId
     * @param elementIda
     * @param eparchyCode
     * @return
     * @throws Excepion
     */
    public static IDataset getVpmnDiscntByProductIdElementId(String discntId, String elementIda, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.clear();
        params.put("ELEMENT_ID", discntId);
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("ELEMENT_ID_A", elementIda);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT A.ELEMENT_ID_A, A.ELEMENT_ID_B ");
        sql.append("FROM TD_B_ELEMENT_LIMIT A ");
        sql.append("WHERE A.ELEMENT_TYPE_CODE_A = 'D' ");
        sql.append("AND A.ELEMENT_TYPE_CODE_B = 'D' ");
        sql.append("AND (A.ELEMENT_ID_A = NVL(:ELEMENT_ID, '') OR A.ELEMENT_ID_B = NVL(:ELEMENT_ID, '')) ");
        sql.append("AND (A.ELEMENT_ID_A IN (");
        sql.append(elementIda);
        sql.append(") OR A.ELEMENT_ID_B IN (");
        sql.append(elementIda);
        sql.append(")) ");
        sql.append("AND A.LIMIT_TAG = '0' ");
        sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        sql.append("AND (EPARCHY_CODE = :EPARCHY_CODE OR EPARCHY_CODE = 'ZZZZ') ");

        return Dao.qryBySql(sql, params, Route.CONN_CRM_CEN);
    }

    /**
     * 查询与A元素有限制关系的元素集合
     * 
     * @param elementType
     * @param elementId
     * @param limitTag
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryElementLimitByElementIdA(String elementType, String elementId, String limitTag, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_TYPE_CODE_A", elementType);
        param.put("ELEMENT_ID_A", elementId);
        param.put("LIMIT_TAG", limitTag);
        param.put("EPARCHY_CODE", eparchyCode);
        //return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELEMENTID_A", param, Route.CONN_CRM_CEN);
        
        IDataset result=UpcCall.queryOfferRelByCond(elementType,elementId,limitTag);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0; i<result.size(); i++)
        	{
        		IData data = result.getData(i);
        		data.put("ELEMENT_TYPE_CODE_B", data.getString("REL_OFFER_TYPE"));
        		data.put("ELEMENT_ID_B", data.getString("REL_OFFER_CODE"));
        		data.put("ELEMENT_TYPE_CODE_A", elementType);
        		data.put("ELEMENT_ID_A", elementId);
        		data.put("LIMIT_TAG", data.getString("REL_TYPE"));
        	}
        }
        
        return result;
    }

    /**
     * 查询与A元素有限制关系的元素集合
     * 
     * @param elementType
     * @param elementId
     * @param limitTag
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryElementLimitByElementIdB(String elementType, String elementId, String limitTag, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_TYPE_CODE_B", elementType);
        param.put("ELEMENT_ID_B", elementId);
        param.put("LIMIT_TAG", limitTag);
        param.put("EPARCHY_CODE", eparchyCode);
        //return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELEMENTID_B", param, Route.CONN_CRM_CEN);
        
        IDataset result=UpcCall.queryOfferRelByRelOfferIdAndRelType(elementId,elementType,limitTag);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0; i<result.size(); i++)
        	{
        		IData data = result.getData(i);
        		data.put("ELEMENT_TYPE_CODE_A", data.getString("OFFER_TYPE"));
        		data.put("ELEMENT_ID_A", data.getString("OFFER_CODE"));
        		data.put("ELEMENT_TYPE_CODE_B", elementType);
        		data.put("ELEMENT_ID_B", elementId);
        		data.put("LIMIT_TAG", data.getString("REL_TYPE"));
        	}
        }
        return result;
    }

    /**
     * 查询有限制关系的元素集
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryElementLimitByID(String elementIdA, String elementTypeCodeA, String elementIdB, String elementTypeCodeB, String limitTag, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_ID_A", elementIdA);
        param.put("ELEMENT_TYPE_CODE_A", elementTypeCodeA);
        param.put("ELEMENT_ID_B", elementIdB);
        param.put("ELEMENT_TYPE_CODE_B", elementTypeCodeB);
        param.put("LIMIT_TAG", limitTag);

        param.put("EPARCHY_CODE", param.getString("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()));
        return Dao.qryByCodeParser("TD_B_ELEMENT_LIMIT", "SEL_BY_ELEMENTAB", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据USER_ID查询用户是否存在GPRS自由套餐
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getGprsFreeDiscntByuser(IData data) throws Exception
    {
        // TODO
        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_GPRSFREEDIS_BY_DISCODE", data);
    }

    // 判断优惠之间的互斥关系
    public IDataset queryDtypeLimitByID(IData param) throws Exception
    {
        // TODO
        param.put("EPARCHY_CODE", param.getString("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()));
        return Dao.qryByCodeParser("TD_B_ELEMENT_LIMIT", "SEL_DTYPE_LIMIT", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询与A元素有限制关系的元素集
     * 
     * @author liuke
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryElementLimitByIDA(IData param) throws Exception
    {
        // TODO
        param.put("EPARCHY_CODE", param.getString("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()));
        return Dao.qryByCodeParser("TD_B_ELEMENT_LIMIT", "SEL_PACKAGE_LIMIT_ELEMENTID", param, Route.CONN_CRM_CEN);
    }
}
