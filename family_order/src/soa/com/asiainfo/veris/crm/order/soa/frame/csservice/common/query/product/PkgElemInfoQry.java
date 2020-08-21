
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PkgElemInfoQry extends CSBizBean
{
    /**
     * 根据产品编码查询产品模型下所有优惠元素
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author tangxy
     */
    public static IDataset getAllDicsInProduct(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PRODUCT_DISCNT_INTF", data);
    }

    /**
     * 根据产品编码查询产品模型下所有优惠元素，除掉携号转网不能办理的优惠
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author tangxy
     */
    public static IDataset getAllDicsWithoutNpInProduct(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PRODUCT_DISCNT_INTF_NP", data);
    }

    /**
     * 根据产品编码查询产品模型下所有服务元素
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author tangxy
     */
    public static IDataset getAllSvcsInProduct(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PRODUCT_SVC_INTF", data);
    }

    /**
     * 根据产品编码查询产品模型下所有服务元素，除掉携号转网不能办理的服务
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author tangxy
     */
    public static IDataset getAllSvcsWithoutNpInProduct(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PRODUCT_SVC_INTF_NP", data);
    }

    public static IDataset getCombineElementByPkId(String packageId, String combinePackageId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("COMBINE_PACKAGE_ID", combinePackageId);
        IDataset dataset = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_COMELE_BY_PKID", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据package_id查询包中资费相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDiscntElementByPackage(String packageId, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("USER_ID", userId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID", data);
    }

    public static IDataset getDiscntElementByPackageFilter(String conDiscntCode, String conDiscntName, String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("con_DISCNT_CODE", conDiscntCode);
        param.put("con_DISCNT_NAME", conDiscntName);
        param.put("PACKAGE_ID", packageId);
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        param.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        SQLParser parser = new SQLParser(param);
        parser.addSQL("  SELECT d.discnt_name element_name,d.discnt_explain element_explain,");
        parser.addSQL("       d.define_months,d.months, t.package_id, t.element_type_code,  t.element_id,");
        parser.addSQL("       t.default_tag, t.force_tag, t.enable_tag, to_char(t.start_absolute_date, 'yyyy-mm-dd') start_absolute_date,");
        parser.addSQL("       t.start_offset, t.start_unit, t.end_enable_tag,to_char(t.end_absolute_date, 'yyyy-mm-dd') end_absolute_date,");
        parser.addSQL("       t.end_offset,t.end_unit, to_char(t.start_date, 'yyyy-mm-dd') start_date,");
        parser.addSQL("       to_char(t.end_date, 'yyyy-mm-dd') end_date,");
        parser.addSQL("       t.item_index,to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,");
        parser.addSQL("       t.update_staff_id, t.update_depart_id,t.remark, t.CANCEL_TAG, t.rsrv_tag1");
        parser.addSQL("    FROM td_b_package_element t,td_b_discnt d");
        parser.addSQL("   WHERE t.element_id = d.discnt_code");
        if (!"".equals(param.getString("con_DISCNT_CODE")))
        {
            parser.addSQL("   and  d.DISCNT_CODE = :con_DISCNT_CODE");
        }
        if (!"".equals(param.getString("con_DISCNT_NAME")))
        {
            parser.addSQL("   and  d.DISCNT_NAME like '%'||:con_DISCNT_NAME||'%'");
        }
        parser.addSQL("   and  t.element_type_code = 'D'");
        parser.addSQL("   AND t.package_id = :PACKAGE_ID");
        parser.addSQL("   and (d.eparchy_code='ZZZZ' or d.eparchy_code=:EPARCHY_CODE)");
        parser.addSQL("   and (:TRADE_STAFF_ID='SUPERUSR' or exists");
        parser.addSQL("   (select 1");
        parser.addSQL("      from (select b.data_code");
        parser.addSQL("  from tf_m_staffdataright a, tf_m_roledataright b");
        parser.addSQL("     where ");
        parser.addSQL("     a.data_type = 'D'");
        parser.addSQL("     and a.right_attr = 1");
        parser.addSQL("     and a.right_tag = 1");
        parser.addSQL("     and a.data_code = b.role_code");
        parser.addSQL("     and a.staff_id = :TRADE_STAFF_ID");
        parser.addSQL("     union ");
        parser.addSQL("     select a.data_code");
        parser.addSQL("     from tf_m_staffdataright a");
        parser.addSQL("     where ");
        parser.addSQL("     a.data_type = 'D'");
        parser.addSQL("     and a.right_attr = 0");
        parser.addSQL("     and a.right_tag = 1");
        parser.addSQL("     and a.staff_id =:TRADE_STAFF_ID) tmp");
        parser.addSQL("     where tmp.data_code = to_char(d.discnt_code))) ");
        parser.addSQL("     AND SYSDATE BETWEEN t.start_date AND t.end_date");
        parser.addSQL("    and SYSDATE BETWEEN d.start_date AND d.end_date");
        parser.addSQL("   ORDER BY t.ELEMENT_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 根据package_id查询包中资费相关信息 不判断资费元素权限
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDiscntElementByPackageNoPriv(String packageId, String eparchyCode, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("USER_ID", userId);
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID_NO_PRIV", data);
    }

    /**
     * 根据优惠编码+产品ID查询包信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IData getDiscntsByDiscntCode(String element_id, String product_id, String eparchy_code) throws Exception
    {
//        IData param = new DataMap();
//        param.put("ELEMENT_ID", element_id);
//        param.put("PRODUCT_ID", product_id);
//        param.put("EPARCHY_CODE", eparchy_code);
//        IDataset result = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_DISCNT_CODE", param, Route.CONN_CRM_CEN);
        
        IDataset result = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(product_id,element_id,"D");
        return (result != null && result.size() > 0) ? result.getData(0) : new DataMap();
    }

    /**
     * 作用：通过ELEMENT_ID查询包下元素信息
     * 
     * @author liudx 2011-01-10
     * @param params
     * @return
     * @throws Exception
     */
    public static IData getElementByElementId(String package_id, String elementTypeCode, String element_id) throws Exception
    {
        IDataset idataset = UPackageElementInfoQry.getPackageElementInfoByPidEidEtype(package_id, elementTypeCode, element_id);
        return IDataUtil.isNotEmpty(idataset) ? idataset.getData(0) : null;
    }

    /**
     * 根据PRODUCT_ID,ELEMENT_TYPE_CODE,ELEMENT_ID查询包信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getElementByPIdElemId(String productId, String elementTypeCode, String elementId, String eparchyCode) throws Exception
    {
        return UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(productId, elementId, elementTypeCode);
        /*IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("ELEMENT_TYPE_CODE", elementTypeCode);
        data.put("ELEMENT_ID", elementId);
        data.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PID_ELEMID", data, Route.CONN_CRM_CEN);*/
    }

    /**
     * 作用：通过PRODUCT_ID查询产品无素信息
     * 
     * @author luojh 2009-08-23
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset getElementByProductId(String product_id) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", product_id);
        return Dao.qryByCodeParser("TD_B_PACKAGE_ELEMENT", "SEL_ELEMENT_BY_PRODUCTID", params, Route.CONN_CRM_CEN);
    }

    /**
     * 根据PRODUCT_ID,ELEMENT_TYPE_CODE,ELEMENT_ID查询BBOSS附加产品的元素
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getElementByProductId(String product_id, String element_type_code, String element_id) throws Exception
    {
        return UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(product_id, element_id, element_type_code);
        /*IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("ELEMENT_TYPE_CODE", element_type_code);
        data.put("ELEMENT_ID", element_id);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PRODUCTID", data, Route.CONN_CRM_CEN);*/
    }

    @SuppressWarnings("unchecked")
    public static IDataset getElementFDattr(String elementId, String elementTypeCode) throws Exception
    {

        IData param = new DataMap();

        param.put("ELEMENT_ID", elementId);

        param.put("ELEMENT_TYPE_CODE", elementTypeCode);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_ELEMENT_DF_ATTR", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询包优惠订购个数
     */
    public static String getEnableTagDiscnt(String packageId, String type, String discntCode) throws Exception
    {
        return StaticUtil.getStaticValue(getVisit(), "TD_B_PACKAGE_ELEMENT", new String[]
        { "PACKAGE_ID", "ELEMENT_TYPE_CODE", "ELEMENT_ID" }, "ENABLE_TAG", new String[]
        { packageId, "D", discntCode });
    }

    public static IDataset getForceElement(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_ELEMENT", param);
    }

    /**
     * 获取主产品中可选择的资费
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMainDiscntElesByProdId(String product_id, String trade_eparchy_code, String trade_staff_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
        data.put("TRADE_STAFF_ID", trade_staff_id);
        IDataset dataset = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_MAIN_ELE_BY_PROD_ID", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据主产品标识查成员主体服务
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData getMemServiceIdByMainProductId(String productId) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        IDataset result = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_MAIN_PRODUCTID", param, Route.CONN_CRM_CEN);
        return (result != null && result.size() > 0) ? result.getData(0) : new DataMap();
    }

    @SuppressWarnings("unchecked")
    public static IDataset getPackaageIDByPidAndEid(String productId, String elementId) throws Exception
    {

        IData param = new DataMap();

        param.put("ELEMENT_ID", elementId);

        param.put("PRODUCT_ID", productId);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_SERVICE_SEL", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据包ID获取包内元素
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPackageElementByPackageId(String package_id) throws Exception
    {
        return UPackageElementInfoQry.getPackageElementInfoByPackageId(package_id);
    }

    /**
     * 查产品的包元素
     * 
     * @param PRODUCT_ID
     * @param ELEMENT_TYPE_CODE
     * @param VELEMENT_ID
     * @author shixb
     * @version 创建时间：2009-11-6 下午10:33:02
     */
    public static IDataset getPackageElementByProductId(String product_id, String element_type_code, String element_id) throws Exception
    {
        return UPackageElementInfoQry.getPackageElementByProductId(product_id, element_type_code, element_id);
    }

    /**
     * 根据元素ID和产品ID获取包ID
     * 
     * @param data
     * @return
     * @throws Exception
     * @author awx
     * @date 2009-7-28
     */
    public static IDataset getPackageIdByElementIdAndProductId(String element_id, String element_type_code, String product_id) throws Exception
    {
        /*IData data = new DataMap();
        data.put("ELEMENT_ID", element_id);
        data.put("ELEMENT_TYPE_CODE", element_type_code);
        data.put("PRODUCT_ID", product_id);
        IDataset dataset = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_ELEMENT_PRODUCT", data, Route.CONN_CRM_CEN);
        return dataset;*/
    	IData  data = UpcCall.queryAllOffersByOfferId("P" , product_id , "" , "" ,BizRoute.getTradeEparchyCode());
    	
    	IDataset offerComRelList = data.getDataset("OFFER_COM_REL_LIST");
    	IDataset offerJoinRelList = data.getDataset("OFFER_JOIN_REL_LIST");
    	IDataset offerGroupRelList = data.getDataset("OFFER_GROUP_REL_LIST");
    	IDataset resultList = new DatasetList();
    	if(ArrayUtil.isNotEmpty(offerComRelList))
    	{
    		for(int i = 0 ; i < offerComRelList.size() ; i ++)
        	{
        		IData offer = offerComRelList.getData(i);
        		String offerCode = offer.getString("OFFER_CODE");
        		String offerType = offer.getString("OFFER_TYPE");
        		if(StringUtils.equals(element_id, offerCode) && StringUtils.equals(element_type_code, offerType))
        		{
        			offer.put("MAIN_TAG", offer.getString("IS_MAIN"));
        			resultList.add(offer);
        		}
        	}
    	}
    	
    	if(ArrayUtil.isNotEmpty(offerJoinRelList))
    	{
    		for(int i = 0 ; i < offerJoinRelList.size() ; i ++)
        	{
        		IData offer = offerJoinRelList.getData(i);
        		String offerCode = offer.getString("OFFER_CODE");
        		String offerType = offer.getString("OFFER_TYPE");
        		if(StringUtils.equals(element_id, offerCode) && StringUtils.equals(element_type_code, offerType))
        		{
        			offer.put("MAIN_TAG", "0");
        			resultList.add(offer);
        		}
        	}
    	}
    	
    	if(ArrayUtil.isNotEmpty(offerGroupRelList))
    	{
    		for(int i = 0 ; i < offerGroupRelList.size() ; i ++)
        	{
        		IDataset groupComRelList = offerGroupRelList.getData(i).getDataset("GROUP_COM_REL_LIST");
        		
        		if(ArrayUtil.isNotEmpty(groupComRelList))
        		{
        			for(int j = 0 ; j < groupComRelList.size() ; j++)
            		{
            			IData offer = groupComRelList.getData(j);
            			
            			String offerCode = offer.getString("OFFER_CODE");
                		String offerType = offer.getString("OFFER_TYPE");
                		if(StringUtils.equals(element_id, offerCode) && StringUtils.equals(element_type_code, offerType))
                		{
                			offer.put("MAIN_TAG", "0");
                			resultList.add(offer);
                		}
            		}
        		}
        	}
    	}
    	return resultList;
    }

    /**
     * 查询表方法
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author zhuyu 2013-6-24
     */
    public static IDataset getPKGeelementInfosbyDiscnProductId(String TableName, String sqlName, IData inparams) throws Exception
    {

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_ELEMENT_DISCNT_BY_PRODUCT_ID", inparams);

    }

    /**
     * 查询表方法
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author zhuyu 2013-6-24
     */
    public static IDataset getPKGeelementInfosbyproductId(String TableName, String sqlName, IData inparams) throws Exception
    {

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_ELEMENT_SVC_BY_PRODUCT_ID", inparams);

    }

    /**
     * 查询表方法
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author zhuyu 2013-6-24
     */
    public static IDataset getPKGElementInfosbyProduct(String TableName, String sqlName, IData inparams) throws Exception
    {

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_SVC_BY_PRODUCT", inparams);

    }

    /**
     * 根据package_id查询包中平台服务相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcElementByPackage(String package_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("ELEMENT_TYPE_CODE", "Z");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PLATSVC_BY_PACKID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 获取主产品下的附加产品中可选择的资费
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPlusDiscntElesByProdId(String trade_eparchy_code, String product_id, String trade_staff_id) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
        data.put("PRODUCT_ID", product_id);
        data.put("TRADE_STAFF_ID", trade_staff_id);
        IDataset dataset = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PLUS_ELE_BY_PROD_ID", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 获取产品模型配置
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author tangxy
     */
    public static IDataset getProductElementConfig(String productId, String elemId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("ELEMENT_ID", elemId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_ELEMENT_BY_PROID_ELEID", data);
    }

    /**
     * 根据PRODUCT_ID,ELEMENT_TYPE_CODE,ELEMENT_ID查询BBOSS附加产品的元素
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getRsrvByPk(String package_id, String element_type_code, String element_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("ELEMENT_TYPE_CODE", element_type_code);
        data.put("ELEMENT_ID", element_id);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        IDataset datas = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_RSRV_BY_PK", data, Route.CONN_CRM_CEN);
        if (datas == null || datas.size() == 0)
        {
            return new DataMap();
        }
        else
        {
            return datas.getData(0);
        }
    }

    /**
     * 查询包优惠订购个数
     */
    public static String getRsrvStr5ByDiscnt(IData discnt) throws Exception
    {

        return StaticUtil.getStaticValue(getVisit(), "TD_B_PACKAGE_ELEMENT", new String[]
        { "PACKAGE_ID", "ELEMENT_TYPE_CODE", "ELEMENT_ID" }, "RSRV_STR5", new String[]
        { discnt.getString("PACKAGE_ID"), "D", discnt.getString("DISCNT_CODE") });
    }

    public static IDataset getSalePackageDiscntByPkg(String producId, String packageId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", producId);
        inparam.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PACKAGE_DISCNT_ELEMENT", inparam);
    }

    /**
     * 根据package_id查询包中服务相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByPackage(IData data) throws Exception
    {

        data.put("ELEMENT_TYPE_CODE", "S");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        if (ProvinceUtil.isProvince(ProvinceUtil.XINJ))
        {
            data.put("USER_ID", data.getString("USER_ID", "0000000000000000"));
        }
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_SERV_BY_PACKID", data);
    }

    /**
     * 根据package_id查询包中服务相关信息 不判断元素权限
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByPackageNoPriv(IData data) throws Exception
    {

        data.put("ELEMENT_TYPE_CODE", "S");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        if (ProvinceUtil.isProvince(ProvinceUtil.XINJ))
        {
            data.put("USER_ID", data.getString("USER_ID", "0000000000000000"));
        }
        return null;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByPk(String package_id, String element_type_code, String element_id) throws Exception
    {
        return getServElementByPk(package_id, element_type_code, element_id, null);
    }
    
    public static IDataset getServElementByPk(String package_id, String element_type_code, String element_id, String queryCha) throws Exception
    {
        return UPackageElementInfoQry.getPackageElementInfoByPidEidEtype(package_id, element_type_code, element_id, queryCha);
    }
    
    /**
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProdPkgElementByPk(String package_id, String element_type_code, String element_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("ELEMENT_TYPE_CODE", element_type_code);
        data.put("ELEMENT_ID", element_id);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PKGID_ELEMID", data, Route.CONN_CRM_CEN);
    }
    
    
    
    /**
     * 根据服务编码+产品ID查询包信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData getServicesByServiceCode(String serviceId, String productId) throws Exception
    {
//        IData param = new DataMap();
//        param.put("ELEMENT_ID", serviceId);
//        param.put("PRODUCT_ID", productId);
        // TODO
        IDataset result = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode(productId,serviceId,"S");
        return (result != null && result.size() > 0) ? result.getData(0) : new DataMap();
    }

    /**
     * 判断该优惠是否存在
     * 
     * @param discntCode
     * @param eparchyCode
     * @return true 存在 false 不存在
     * @throws Exception
     */
    public static boolean isDiscntByIdAndEparchy(String discntCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_ID", discntCode);
        param.put("EPARCHY_CODE", eparchyCode);
        IDataset ids = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_IS_EPARCHY", param, Route.CONN_CRM_CEN);

        return IDataUtil.isNotEmpty(ids);
    }

    /*
     * 取得成员优惠名称
     */
    public static IDataset qryMemDisName(String discntCode) throws Exception
    {
        IData param = new DataMap();
        param.put("DISCNT_CODE", discntCode);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT discnt_name ");
        parser.addSQL(" FROM   td_b_discnt ");
        parser.addSQL(" WHERE  discnt_code = :DISCNT_CODE ");

        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryConfirmForceElements(String productid, String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "CONFIRM_FORCEELEMENTS_SEL", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询产品下的默认通话级别或漫游级别
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-3-5
     * @param pd
     * @param productId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryDefaultCallRankOrRaomRank(String productId, String eparchyCode, String type) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("MODE", type);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DEFAULT_RANK", param);
    }

    /** 查询某包下必选和默认的元素 */
    public static IDataset queryDefaultForceElemsByPkg(String productId, String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        SQLParser parser = new SQLParser(param);

        parser
                .addSQL(" SELECT package_id, element_type_code, element_id, main_tag, default_tag, force_tag, enable_tag, to_char(start_absolute_date,'yyyy-mm-dd hh24:mi:ss') start_absolute_date, start_offset, start_unit, end_enable_tag, to_char(end_absolute_date,'yyyy-mm-dd hh24:mi:ss') end_absolute_date, end_offset, end_unit, cancel_tag, to_char(cancel_absolute_date,'yyyy-mm-dd hh24:mi:ss') cancel_absolute_date, cancel_offset, cancel_unit, to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date, to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date, item_index, to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time, update_staff_id, update_depart_id, remark ");
        parser.addSQL(" FROM td_b_package_element e ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND package_id IN ( ");
        parser.addSQL("      SELECT package_id FROM td_b_product_package p ");
        parser.addSQL("      WHERE 1 = 1");
        parser.addSQL("		 AND product_id = :PRODUCT_ID ");
        parser.addSQL("      AND package_id = :PACKAGE_ID ");
        parser.addSQL("      AND (sysdate between p.start_date AND p.end_date) ");
        parser.addSQL(" ) ");
        parser.addSQL(" AND (e.main_tag = '1' OR e.force_tag = '1' OR e.default_tag = '1') ");
        parser.addSQL(" AND (SYSDATE BETWEEN e.start_date AND e.end_date) ");

        return Dao.qryByParse(parser);
    }

    /** 查询某个包中的优惠 */
    public static IDataset queryDiscntByPackage(String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select ELEMENT_ID DISCNT_CODE ");
        parser.addSQL(" from TD_B_PACKAGE_ELEMENT ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL(" and PACKAGE_ID = :PACKAGE_ID ");
        parser.addSQL(" and ELEMENT_TYPE_CODE = 'D' ");
        parser.addSQL(" and sysdate between START_DATE and END_DATE ");
        return Dao.qryByParse(parser);
    }

    public static IDataset queryDiscntForceElementsByProductId(String productid, String eparchy_code) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", eparchy_code);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_DEFAULT_DISCNT_BY_PRODUCTID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品编码查询产品必选包下的优惠
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public static IDataset queryDiscntOfForcePackage(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
         IDataset results= UProductElementInfoQry.queryForceDiscntsByProductId(productId);
         return	results;//Daoq.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCEPACKAGE_DISCNT", data);
    }
    
    /**
     * 根据产品编码查询产品必选包下的服务
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author hujj5
     */
    public static IDataset queryServiceOfForcePackage(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
         IDataset results= UProductElementInfoQry.queryForceSvcsByProductId(productId);
         return results;
    }

    public static IDataset queryElemsByPkgIdAndType(String packageId, String elementTypeCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("ELEMENT_TYPE_CODE", elementTypeCode);
        cond.put("PACKAGE_ID", packageId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKID", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryForceDefaultElem(String productId, String packageId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", productId);
        cond.put("PACKAGE_ID", packageId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_DEFAULT_ELEMENT", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryNonOrderElementByProductId(String userId, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_NOORDERELEMENTS_BY_PRODUCTID", param);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryOldConfirmForceElements(String productid, String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        param.put("USER_ID", userId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "OLD_CONFIRM_FORCEELEMENTS_SEL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryOrderElementByProductId(String userId, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_ORDERELEMENTS_BY_PRODUCTID", param);
    }

    public static IDataset queryPackageByElement(String element_id, String element_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_ID", element_id);
        data.put("ELEMENT_TYPE_CODE", element_type_code);
        return Dao.qryByCodeParser("TD_B_PACKAGE_ELEMENT", "SEL_PACKAGE_BY_ELEMENTID", data);
    }

    public static IDataset queryPackageTypeCodes(String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_ELEMENT_TYPE_BY_PACK", param);
    }

    public static IDataset queryPkgElementByEleIdProdId(String element_id, String product_id) throws Exception
    {
        IData param = new DataMap();
        param.put("ELEMENT_ID", element_id);
        param.put("PRODUCT_ID", product_id);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_ELEMENTID_PRODID", param);
    }

    /**
     * @Function: queryPkgElementByPkgIdAndUserId
     * @Description: 根据packageId和UserId查包信息
     * @param: @param packageId
     * @param: @param userId
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 下午02:35:20 2013-9-5 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-5 longtian3 v1.0.0 TODO:
     */
    public static IDataset queryPkgElementByPkgIdAndUserId(String packageId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("USER_ID", userId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKAGEID_USERID", param);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductDiscnt(String productid, String discntCode) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("ELEMENT_ID", discntCode);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_DISCNT_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductDiscnts(String productid) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_DISCNTS_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductElement(String productId, String elementType, String elementId) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productId);

        param.put("ELEMENT_TYPE_CODE", elementType);

        param.put("ELEMENT_ID", elementId);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_ELEMENT_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductElements(String productid) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_ELEMENTS_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductForceElements(String productid) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_FORCEELEMENTS_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductPlatSvc(String productid, String serviceId) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("ELEMENT_ID", serviceId);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_PLATSVC_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductPlatSvcs(String productid) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_PLATSVCS_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductService(String productid, String serviceId) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("ELEMENT_ID", serviceId);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_SERVICE_SEL", param, Route.CONN_CRM_CEN);
    }

    @SuppressWarnings("unchecked")
    public static IDataset queryProductServices(String productid) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "PRODUCT_SERVICES_SEL", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据PRODUCT_ID查询营销活动的产品下的包
     * 
     * @author luojh
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset querySaleProductPackageDeposit(IData param) throws Exception
    {
        // TODO
        return Dao.qryByCodeParser("TD_B_PACKAGE_ELEMENT", "SEL_SALE_DEPOSIT_INTF", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据SvcId查询包元素信息
     * 
     * @param productId
     * @param serviceId
     * @param eparchyCode
     * @return
     * @throws Exception
     *             wangjx 2013-8-1
     */
    public static IDataset querySvcByPrdIdAndSvcId(String productId, String serviceId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("SERVICE_ID", serviceId);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKID", param, Route.CONN_CRM_CEN);
    }

    /** 查询某包下必选和默认的服务和资费元素 */
    public static IDataset querySvcDiscntElemsByPkg(String productid, String packageId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productid);
        param.put("PACKAGE_ID", packageId);
        SQLParser parser = new SQLParser(param);

        parser
                .addSQL(" SELECT package_id, element_type_code, element_id, main_tag, default_tag, force_tag, enable_tag, to_char(start_absolute_date,'yyyy-mm-dd hh24:mi:ss') start_absolute_date, start_offset, start_unit, end_enable_tag, to_char(end_absolute_date,'yyyy-mm-dd hh24:mi:ss') end_absolute_date, end_offset, end_unit, cancel_tag, to_char(cancel_absolute_date,'yyyy-mm-dd hh24:mi:ss') cancel_absolute_date, cancel_offset, cancel_unit, to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date, to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date, item_index, to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time, update_staff_id, update_depart_id, remark ");
        parser.addSQL(" FROM td_b_package_element e ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND package_id IN ( ");
        parser.addSQL("      SELECT package_id FROM td_b_product_package p ");
        parser.addSQL("      WHERE 1 = 1");
        parser.addSQL("		 AND product_id = :PRODUCT_ID ");
        parser.addSQL("      AND package_id = :PACKAGE_ID ");
        parser.addSQL("      AND (sysdate between p.start_date AND p.end_date) ");
        parser.addSQL(" ) ");
        parser.addSQL(" AND e.element_type_code in ('S', 'D') ");
        parser.addSQL(" AND (SYSDATE BETWEEN e.start_date AND e.end_date) ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset querySvcForceElementsByProductId(String productid, String eparchy_code) throws Exception
    {

        IData param = new DataMap();

        param.put("PRODUCT_ID", productid);

        param.put("EPARCHY_CODE", eparchy_code);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_DEFAULT_SVC_BY_PRODUCTID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据营销活动办理进来的DISCNT_CODE查询用户是否存在附加产品下的优惠
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryUserLimitDiscntCode(IData param) throws Exception
    {
        // TODO
        IDataset userDiscntInfo = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PACKAGEDISCNT_BY_DISCNTCODE1", param);
        return userDiscntInfo;
    }

    /**
     * 根据营销活动办理进来的DISCNT_CODE查询用户是否存在主产品下的优惠
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryUserPackageDiscntCode(IData param) throws Exception
    {
        // TODO
        IDataset userDiscntInfo = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PACKAGEDISCNT_BY_DISCNTCODE", param);
        return userDiscntInfo;
    }

    /**
     * 根据产品Id查询产品服务元素
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset getAllSvcByPId(String productId) throws Exception
    {
        if (productId == null || "".equals(productId))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_31);
        }
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        try
        {
            IDataset idataset = Dao.qryByCodeParser("TD_B_PACKAGE_ELEMENT", "SEL_SVC_BY_PRODUCT", params, Route.CONN_CRM_CEN);
            if (!IDataUtil.isNotEmpty(idataset))
            {
                ((IData) (idataset.get(0))).put("X_RESULTCODE", 0);
                ((IData) (idataset.get(0))).put("X_RESULTINFO", "TradeOk!");
            }
            else
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_68);
                IData resultIData = new DataMap();
                resultIData.put("X_RESULTCODE", 0);
                resultIData.put("X_RESULTINFO", "TradeOk!");
                idataset.add(resultIData);
            }
            return idataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
            return null;
        }
    }

    /**
     * 根据package_id、brand_code查询包中资费相关信息
     * 
     * @author wusf
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntByPackageBrand(IData data) throws Exception
    {
        // TODO
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID_BRAND", data);
    }

    /**
     * 根据package_id、brand_code查询包中资费相关信息 不判断资费元素权限
     * 
     * @author wusf
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntByPackageBrandNoPriv(IData data) throws Exception
    {
        // TODO
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID_BRAND_NO_PRIV", data);
    }

    /**
     * 海南根据用户使用年限,开户或续费操作,片区编码查询可以订购的优惠列表
     * 
     * @author zengzb
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntElementByCondition(IData data) throws Exception
    {
        // TODO
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_CONDITION", data);
    }

    /**
     * 海南根据用户使用年限,开户或续费操作,片区编码查询可以订购的优惠列表
     * 
     * @author zengzb
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntElementByConditionNoPriv(IData data) throws Exception
    {
        // TODO
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_CONDITION_NO_PRIV", data);
    }

    /**
     * 根据package_id查询包中资费相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntElementByPackage(String user_id, String package_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("PACKAGE_ID", package_id);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID", data);
    }

    /**
     * 根据package_id查询包中资费相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntElementByPackageBBoss(IData data) throws Exception
    {
        // TODO
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID_BBOSS", data);
    }

    /**
     * 根据package_id查询包中资费相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntElementByPackageForColRing(IData data) throws Exception
    {

        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID_COLRING", data);
    }

    // 特殊产品变更查询优惠
    @SuppressWarnings("unchecked")
    public IDataset getDiscntElementByPackageForSpec(String user_id, String package_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("PACKAGE_ID", package_id);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_SPEC_DISCNT_BY_PACKID", data);
    }

    /**
     * @description:查询集团彩铃优惠信息
     * @author wusf
     * @date Sep 8, 2010
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscnts4ColorRing(String user_id, String package_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("PACKAGE_ID", package_id);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_FOR_COLORRING", data);
    }

    /**
     * @description:查询集团彩铃优惠信息，不判断资费权限
     * @author wusf
     * @date Sep 8, 2010
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscnts4ColorRingNoPriv(String user_id, String package_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("PACKAGE_ID", package_id);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_FOR_COLORRING_NO_PRIV", data);
    }

    /**
     * 根据package_id查询包中宽带资费相关信息 不判断资费元素权限
     * 
     * @param data
     * @return
     * @throws Exception
     * @author chenzm
     */
    public IDataset getWideNetDiscntElementByPackageNoPriv(String user_id, String package_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("PACKAGE_ID", package_id);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_WIDENET_DISCNT_BY_PACKID_NO_PRIV", data);
    }
    
    /**
     * 根据产品ID获取所有包内元素
     * @param product_id
     * @return
     * @throws Exception
     */
    public static IDataset getElementAllByProductId(IData param) throws Exception{
    	return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_ALL_ELEMENT_BY_PRODUCT_ID", param);
    }
    
    /** 查询某包下自定义可选的的元素 */
    public static IDataset queryFreeChoiceElem(String productId, String packageId, String elementId, String elementTypeCode) throws Exception {
    	 IData cond = new DataMap();
         cond.put("PRODUCT_ID", productId);
         cond.put("PACKAGE_ID", packageId);
         cond.put("ELEMENT_ID", elementId);
         cond.put("ELEMENT_TYPE_CODE", elementTypeCode);
         return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FREE_CHOICET_ELEMENT", cond, Route.CONN_CRM_CEN);
    }
    public static IDataset getElementAttr4ProFlow(IData param) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_DISCNT_FEE", param);
    }
}
