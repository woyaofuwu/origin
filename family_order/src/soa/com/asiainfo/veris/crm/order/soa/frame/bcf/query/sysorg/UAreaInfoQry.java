
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UAreaInfoQry
{
    /**
     * 根据部门/渠道标识查询归属地域编码
     * 
     * @param departId
     * @return
     * @throws Exception
     */
    public static String getAreaCodeByDepartId(String departId) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "AREA_CODE", departId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_DEPART", "DEPART_ID", "AREA_CODE", departId);
    }

    /**
     * 根据地域编码查询地域名称
     * 
     * @param areaCode
     * @return
     * @throws Exception
     */
    public static String getAreaFrameByAreaCode(String areaCode) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_FRAME", areaCode);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_AREA", "AREA_CODE", "AREA_FRAME", areaCode);
    }

    public static String getAreaLevelByAreaCode(String areaCode) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_LEVEL", areaCode);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_AREA", "AREA_CODE", "AREA_LEVEL", areaCode);
    }

    /**
     * 根据地域编码查询地域名称
     * 
     * @param areaCode
     * @return
     * @throws Exception
     */
    public static String getAreaNameByAreaCode(String areaCode) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", areaCode);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_AREA", "AREA_CODE", "AREA_NAME", areaCode);
    }

    /**
     * 根据地域编码查询上级地域名称
     * 
     * @param areaCode
     * @return
     * @throws Exception
     */
    public static String getParentAreaCodeByAreaCode(String areaCode) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_AREA", "AREA_CODE", "PARENT_AREA_CODE", areaCode);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_AREA", "AREA_CODE", "PARENT_AREA_CODE", areaCode);
    }

    public static IDataset qryAeraLikeAreaFrame(String areaFrame) throws Exception
    {
        IData param = new DataMap();
        param.put("AREA_FRAME", areaFrame);

        return Dao.qryByCodeParser("TD_M_AREA", "SEL_LIKE_AREA_FRAME", param, Route.CONN_SYS);
    }

    /**
     * 根据部门/渠道级别查询部门/渠道配置信息
     * 
     * @param areaLevel
     * @return
     * @throws Exception
     */
    public static IDataset qryAreaByAreaLevel(String areaLevel) throws Exception
    {
        IData param = new DataMap();

        param.put("AREA_LEVEL", areaLevel);
        param.put("VALIDFLAG", "0");

        return Dao.qryByCode("TD_M_AREA", "SEL_AREA_1", param, Route.CONN_SYS);
    }

    /**
     * 查询30级别的部门/渠道配置信息
     * 
     * @return
     * @throws Exception
     */
    public static IDataset qryAreaByAreaLevel30() throws Exception
    {
        return qryAreaByAreaLevel("30");
    }

    /**
     * 查询部门/渠道归属地州配置信息
     * 
     * @param areaCode
     * @return
     * @throws Exception
     */
    public static IData qryAreaByCode(String areaCode) throws Exception
    {
        IData param = new DataMap();
        param.put("AREA_CODE ", areaCode);

        IDataset ids = Dao.qryByCode("TD_M_AREA", "SEL_BY_AREA_CODE", param, Route.CONN_SYS);

        if (IDataUtil.isEmpty(ids))
        {
            return new DataMap();
        }

        return ids.getData(0);
    }

    /**
     * 根据上级部门/渠道归属地址查询部门/渠道配置信息
     * 
     * @param parentAreaCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAreaByParentAreaCode(String parentAreaCode) throws Exception
    {
        IData data = new DataMap();

        data.put("PARENT_AREA_CODE", parentAreaCode);

        IDataset dataset = Dao.qryByCode("TD_M_AREA", "SEL_AREA", data, Route.CONN_SYS);

        return dataset;
    }

    /**
     * 根据部门/渠道地址查询部门/渠道配置信息
     * 
     * @param area_code
     * @param this_tag
     * @param use_tag
     * @return
     * @throws Exception
     */
    public static IDataset qryAreaByPk(String area_code, String this_tag, String use_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("AREA_CODE", area_code);
        param.put("THIS_TAG", this_tag);
        param.put("USE_TAG", use_tag);

        return Dao.qryByCodeParser("TD_M_AREA", "SEL_BY_PK", param, Route.CONN_SYS);
    }
}
