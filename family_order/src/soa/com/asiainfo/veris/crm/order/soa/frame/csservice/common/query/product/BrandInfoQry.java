
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;

public class BrandInfoQry extends CSBizBean
{
    /**
     * 根据package_id查询包中资费相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDiscntElementByPackage(String packageId, String userId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("USER_ID", userId);
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID", data);
    }

    /**
     * 查询品牌信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @author zhuyu
     */
    public static IDataset queryAllBrands() throws Exception
    {
        IData param = new DataMap();
        return Dao.qryByCode("TD_S_BRAND", "SEL_ALL_BRAND", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBrandChangeById(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCodeParser("TF_F_USER_BRANDCHANGE", "SEL_BY_PK", param);
    }

    public static IDataset queryBrandInfos(String BRAND_CODE) throws Exception
    {
        IData cond = new DataMap();
        cond.put("BRAND_CODE", BRAND_CODE);
        return Dao.qryByCode("TD_S_BRAND", "SEL_BRAND_BY_CODE", cond);
    }

    /**
     * 查询个人用户可选品牌
     * 
     * @param param
     * @return
     * @throws Exception
     * @author zhoulin
     */
    public static IDataset queryBrands() throws Exception
    {
        IData param = new DataMap();
        return Dao.qryByCode("TD_S_BRAND", "SEL_PERSON_BRANDS", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据品牌编码查询品牌名称
     * @param brandCode
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-4-13
     */
    public static IDataset queryBrandByBrandCode(String brandCode) throws Exception
    {
    	String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);
    	IData dt = new DataMap();
    	dt.put("BRAND", brandName);
    	dt.put("BRAND_CODE", brandCode);
    	return IDataUtil.idToIds(dt);
    }
}
