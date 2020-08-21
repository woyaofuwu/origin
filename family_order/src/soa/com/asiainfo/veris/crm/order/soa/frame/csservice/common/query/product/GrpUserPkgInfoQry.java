
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class GrpUserPkgInfoQry
{
    /**
     * 查询集团定制的包中相关优惠元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getDiscntElementByGrpCustomize(String packageId, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("USER_ID", userId);
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_PACKID", data, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关优惠元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getDiscntElementByGrpCustomize(String packageId, String userId, String memUserId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if (ProvinceUtil.getProvinceCode().equals(ProvinceUtil.HNAN))
        {
            String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
            if ("0898".equals(eparchyCode) && !"0".equals(memUserId))
            {
                return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_PACKID_HAIN", param, pagination, Route.CONN_CRM_CG);
            }
            else
            {
                return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_PACKID", param, pagination, Route.CONN_CRM_CG);
            }
        }
        else
        {
            return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_PACKID", param, pagination, Route.CONN_CRM_CG);
        }
    }

    public static IDataset getDiscntElementByGrpCustomizeNoPriv(String packageId, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_PACKID_NO_PRIV", data, Route.CONN_CRM_CG);
    }

    public static IDataset getDiscntElementByGrpCustomizeNoPriv(String packageId, String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_PACKID_NO_PRIV", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关优惠元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGrpCustomizeDiscntByUserId(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_USERID", param, pagination, Route.CONN_CRM_CG);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
        		IData data = dataset.getData(i);
        		String elementId = data.getString("ELEMENT_ID");
        		String elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
        		data.put("ELEMENT_NAME", elementName);
        	}
        }
        return dataset;
    }

    /**
     * 查询集团定制的产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGrpCustomizeProductByUserId(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_PRODUCT_DISTINCT_BY_USERID", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关服务元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGrpCustomizeServByUserId(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SERV_BY_USERID", param, pagination, Route.CONN_CRM_CG);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
        		IData data = dataset.getData(i);
        		String elementId = data.getString("ELEMENT_ID");
        		String elementName = USvcInfoQry.getSvcNameBySvcId(elementId);
        		data.put("ELEMENT_NAME", elementName);
        	}
        }
        return dataset;
    }

    /**
     * 查询集团定制的包中相关SP服务元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGrpCustomizeSpByUserId(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SP_BY_USERID", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关服务元素
     * 
     * @param userId
     * @return
     * @throws Exception
     */

    public static IDataset getGrpPackageByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_USERID_NOW", param, Route.CONN_CRM_CG);
    }

    public static IDataset getMembDiscntByGrpProductIdSX(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCodeParser("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_USERID", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团产品product_id查询成员可订购的产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getMemProductsByProdId(String packageId, String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_USERID_PID", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询父集团定制的可继承的产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getParentGrpCustomizeProductByUserId(String packageId, String custId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("CUST_ID", custId);

        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_PARENT_PCK_PRODUCT", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关平台元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcElementByGrpCustomize(String packageId, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_PLATSVC_BY_PACKID", data, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关平台元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcElementByGrpCustomize(String packageId, String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_PLATSVC_BY_PACKID", param, Route.CONN_CRM_CG);
    }

    /**
     * 查询集团定制的包中相关服务元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByGrpCustomize(String packageId, String userId, String memUserId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if (ProvinceUtil.getProvinceCode().equals(ProvinceUtil.HAIN))
        {
            String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
            if ("0898".equals(eparchyCode) && !"0".equals(memUserId))
            {
                return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SERV_BY_PACKID_HAIN", param, pagination);
            }
            else
            {
                return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SERV_BY_PACKID", param, pagination);
            }
        }
        else
        {
            return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SERV_BY_PACKID", param, pagination, Route.CONN_CRM_CG);
        }
    }

    public static IDataset getServElementByGrpCustomizeNoPriv(String packageId, String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SERV_BY_PACKID_NO_PRIV", param, pagination, Route.CONN_CRM_CG);
    }

    /**
     * 获取跟新产品必选优惠不互斥的集团优惠
     * 
     * @param userId
     * @param productId
     * @param elementIda
     * @param tradeStaffId
     * @return
     * @throws Exception
     */
    public static IDataset getVpmnDiscntByRightLimit(String userId, String productId, String elementIda, String tradeStaffId) throws Exception
    {
        IDataset result = new DatasetList();
        
        //查询出集团定制的资费列表
        IDataset grpPackgeDiscnts = getMembDiscntByGrpProductIdSX(userId, null);
        if(IDataUtil.isEmpty(grpPackgeDiscnts))
            return result;
        //查询出与新产品互斥的资费
        IDataset limitDiscnts =  new DatasetList();
        String[] elemetnIdaArray = elementIda.split(",");
        if(elemetnIdaArray != null && elemetnIdaArray.length > 0)
        {
            for(int i = 0,isize = elemetnIdaArray.length; i < isize; i++)
            {
                limitDiscnts.addAll(UpcCall.qryOfferRelWithInverse(elemetnIdaArray[i], BofConst.ELEMENT_TYPE_CODE_DISCNT, "0"));
            }
        }
        if(IDataUtil.isEmpty(limitDiscnts))
            return result;
        IDataset commParamInfos = CommparaInfoQry.getCommByParaAttr("CSM", "8860", "0898");
        //排除互斥的资费
        for(int i = 0,isize = grpPackgeDiscnts.size(); i < isize; i++)
        {
            IData grpPackage = grpPackgeDiscnts.getData(i);
            String elementId = grpPackage.getString("ELEMENT_ID", "");
            String elementTypeCode = grpPackage.getString("ELEMENT_TYPE_CODE", "");
            
            boolean ifLimit = false;
            for(int j = 0,jsize = limitDiscnts.size(); j < jsize; j++)
            {
                IData limitDiscnt = limitDiscnts.getData(j);
                String limitElementId = limitDiscnt.getString("REL_OFFER_CODE", "");
                String limitElementTypeCode = limitDiscnt.getString("REL_OFFER_TYPE", "");
                
                if(limitElementId.equals(elementId) && limitElementTypeCode.equals(elementTypeCode))
                {
                    ifLimit = true;
                    break;
                }
             }
            
            if(!ifLimit)
            {
                IData tempResult = new DataMap();
                tempResult.put("ELEMENT_ID", elementId);
                tempResult.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(elementId));
                Boolean isCommPram = false;
                for(Object obj : commParamInfos){
                	IData commParamInfo = (IData) obj;
                	if(StringUtils.equals(elementId, commParamInfo.getString("PARAM_CODE"))){
                		isCommPram = true;
                		break;
                	}
                }
                if(!isCommPram){
                	result.add(tempResult);
                }
            }
            
        }
        return result;
        
//        IData params = new DataMap();
//        params.clear();
//        params.put("USER_ID", userId);
//        params.put("PRODUCT_ID", productId);
//        params.put("TRADE_STAFF_ID", tradeStaffId);
//        params.put("ELEMENT_ID_A", elementIda);
//
//        StringBuilder sql = new StringBuilder(1000);
//
//        sql.append("SELECT D.DISCNT_NAME ELEMENT_NAME, D.DISCNT_CODE ELEMENT_ID ");
//        sql.append("FROM TF_F_USER_GRP_PACKAGE T, TD_B_DISCNT D ");
//        sql.append("WHERE 1 = 1 ");
//        sql.append("AND T.ELEMENT_ID = D.DISCNT_CODE ");
//        sql.append("AND T.ELEMENT_TYPE_CODE = 'D' ");
//        sql.append("AND T.USER_ID = TO_NUMBER(:USER_ID) ");
//        sql.append("AND (T.PRODUCT_ID = :PRODUCT_ID OR :PRODUCT_ID = '-1') ");
//        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
//        sql.append("AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
//        sql.append("AND T.ELEMENT_ID NOT IN (SELECT PARAM_CODE ");
//        sql.append("FROM TD_S_COMMPARA ");
//        sql.append("WHERE PARAM_ATTR = '8860' ");
//        sql.append("AND SUBSYS_CODE = 'CSM') ");
//        sql.append("AND T.ELEMENT_ID NOT IN ");
//        sql.append("(SELECT TMP.ELEMENT_ID ");
//        sql.append("FROM TD_B_ELEMENT_LIMIT A, ");
//        sql.append("(SELECT D.DISCNT_NAME ELEMENT_NAME, D.DISCNT_CODE ELEMENT_ID ");
//        sql.append("FROM TF_F_USER_GRP_PACKAGE T, TD_B_DISCNT D ");
//        sql.append("WHERE 1 = 1 ");
//        sql.append("AND T.ELEMENT_ID = D.DISCNT_CODE ");
//        sql.append("AND T.ELEMENT_TYPE_CODE = 'D' ");
//        sql.append("AND T.USER_ID = TO_NUMBER(:USER_ID) ");
//        sql.append("AND (T.PRODUCT_ID = :PRODUCT_ID OR :PRODUCT_ID = '-1') ");
//        sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
//        sql.append("AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE) TMP ");
//        sql.append("WHERE A.ELEMENT_ID_A IN (");
//        sql.append(elementIda);
//        sql.append(") ");
//        sql.append("AND A.ELEMENT_ID_B = TMP.ELEMENT_ID ");
//        sql.append("AND A.ELEMENT_TYPE_CODE_A = 'D' ");
//        sql.append("AND A.ELEMENT_TYPE_CODE_B = 'D' ");
//        sql.append("AND A.LIMIT_TAG = '0') ");

//        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
        
        
    }
    
    public static IDataset getSvcElementByGrpUserIdandPackageId(String packageId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SVC_BY_PACKAGEID_USERID", param, Route.CONN_CRM_CG);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
        		IData data = dataset.getData(i);
        		String elementId = data.getString("ELEMENT_ID");
        		String elementName = USvcInfoQry.getSvcNameBySvcId(elementId);
        		if(StringUtils.isNotBlank(elementName))
        		{
        			data.put("ELEMENT_NAME", elementName);
        		}else{
        			dataset.remove(i);
        			i--;
        		}
        	}
        }
        return dataset;
    }
    
    /**
     * 查询服务
     * @param userId
     * @param svcId
     * @return
     * @throws Exception
     */
    public static IDataset getGrpPkgSvcElementByUserId(String userId, String svcId) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_ID", svcId);
        data.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SVC_BY_USERID_ELEMENTID", data, Route.CONN_CRM_CG);
    }
    
    /**
     * 查询优惠
     * @param userId
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getGrpPkgDiscntElementByUserId(String userId, String discntCode) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_ID", discntCode);
        data.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNT_BY_USERID_ELEMENTID", data, Route.CONN_CRM_CG);
    }
    
    /**
     * 查询成员订购的优惠
     * @param mebUserId
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset getUserGrpPkgDiscntElementByUserId(String mebUserId, String userIdA) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", mebUserId);
        data.put("USER_ID_A", userIdA);
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_DISCNTCODE_BY_USERID_AND_A", data, Route.CONN_CRM_CG);
    }
    
}
