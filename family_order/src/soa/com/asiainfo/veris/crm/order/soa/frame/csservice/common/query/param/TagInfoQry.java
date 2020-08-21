
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TagInfoQry
{
    /**
     * 根据tabname,sqlref查询参数信息,不提供分页
     */
    public static IData getCsmTagInfo(String SubSysCode, String tagCode, String sType, String sDefault) throws Exception
    {

        String eparchCode = CSBizBean.getVisit().getStaffEparchyCode();

        String subsysCode = SubSysCode;
        if ("".equals(subsysCode) || subsysCode == null)
        {
            subsysCode = "CSM";
        }

        String TAG_CODE = tagCode;
        String USE_TAG = "0";

        // 用公共方法
        IDataset query = getTagInfoBySubSys(eparchCode, TAG_CODE, USE_TAG, subsysCode, null);
        IData queryResult = new DataMap();
        if (query == null || query.size() < 1)
        {
            if ("0".equals(sType))
            {
                queryResult.put("TAG_CHAR", sDefault);
            }
            else if ("1".equals(sType))
            {
                queryResult.put("TAG_INFO", sDefault);
            }
            else if ("2".equals(sType))
            {
                queryResult.put("TAG_NUMBER", sDefault);
            }
            else if ("3".equals(sType))
            {
                if ("0".equals(sDefault))
                {
                    queryResult.put("TAG_DATE", SysDateMgr.getSysTime());
                }
                else
                {
                    queryResult.put("TAG_DATE", sDefault);
                }
            }
            else if ("4".equals(sType))
            {
                queryResult.put("TAG_SEQUID", sDefault);
            }
            else
            {
                return new DataMap();
            }
            return queryResult;
        }
        else
        {
            return (IData) query.get(0);
        }
    }

    /**
     * 获取tag表数据 给服务调用
     * 
     * @param idata
     * @return
     * @throws Exception
     */

    public static IDataset getSysTagInfo(IData idata) throws Exception
    {
        String tagCode = idata.getString("TAG_CODE");
        String key = idata.getString("KEY");
        String defaultValue = idata.getString("DEFAULT_VALUE");
        String eparchyCode = idata.getString("EPARCHY_CODE");
        String result = getSysTagInfo(tagCode, key, defaultValue, eparchyCode);
        IDataset dataList = new DatasetList();
        IData data = new DataMap();
        data.put("RESULT", result);
        dataList.add(data);
        return dataList;
    }

    /**
     * 获取tag表数据
     * 
     * @param tagCode
     *            tag表的TAG_CODE字段值，用于查询
     * @param key
     *            要查询的tag表字段名
     * @param defaultValue
     *            无查询结果时，返回默认值
     * @param eparchyCode
     * @return 结果
     * @author 孙翰韬
     * @throws Exception
     */
    public static String getSysTagInfo(String tagCode, String key, String defaultValue, String eparchyCode) throws Exception
    {

        // 查询TD_S_TAG表
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("TAG_CODE", tagCode);
        IDataset dataset = Dao.qryByCodeParser("TD_S_TAG", "SEL_BY_TAGCODE", param, null, Route.CONN_CRM_CEN);
        if (dataset.size() > 0)
        {
            IData data = dataset.getData(0);
            return data.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public static IDataset getSysTagInfo2(IData param) throws Exception
    {

        IData data = new DataMap();

        data.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
        data.put("TAG_CODE", param.getString("TAG_CODE"));
        data.put("SUBSYS_CODE", "CSM");
        data.put("USE_TAG", 0);

        return Dao.qryByCode("TD_S_TAG", "SEL_BY_TAGCODE", data, Route.CONN_CRM_CEN);
    }

    /**
     * @param tagCode
     * @param key
     * @param defaultValue
     * @param subsysCode
     * @return
     */
    public static boolean getSysTagInfoCor(String tagCode, String key, String defaultValue, String subsysCode, String eparchyCode) throws Exception
    {

        // 查询TD_S_TAG表
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("TAG_CODE", tagCode);
        param.put("SUBSYS_CODE", subsysCode);
        param.put("USE_TAG", defaultValue);
        IDataset dataset = Dao.qryByCodeParser("TD_S_TAG", "SEL_BY_TAGCODE", param, null, Route.CONN_CRM_CEN);
        if (dataset.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 根据标记对应的地州EPARCHY_CODE、标记编码TAG_CODE和标记对应的作用标志USE_TAG查看系统个性参数表
     * 
     * @author
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    // public static IDataset getTagInfo(IData inparams) throws Exception
    // {
    //
    // return Dao.qryByCode("TD_S_TAG", "SEL_BY_PK", inparams, Route.CONN_CRM_CEN);
    // }
    public static IDataset getTagInfo(String EPARCHY_CODE, String TAG_CODE, String USE_TAG) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("EPARCHY_CODE", EPARCHY_CODE);
        inparams.put("TAG_CODE", TAG_CODE);
        inparams.put("USE_TAG", USE_TAG);
        return Dao.qryByCode("TD_S_TAG", "SEL_BY_PK", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 根据标记对应的地州EPARCHY_CODE、标记编码TAG_CODE和标记对应的作用标志USE_TAG查看系统个性参数表
     * 
     * @author
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getTagInfo(String EPARCHY_CODE, String TAG_CODE, String USE_TAG, Pagination page) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("EPARCHY_CODE", EPARCHY_CODE);
        inparams.put("TAG_CODE", TAG_CODE);
        inparams.put("USE_TAG", USE_TAG);
        return Dao.qryByCode("TD_S_TAG", "SEL_BY_PK", inparams, page, Route.CONN_CRM_CEN);
    }

    /**
     * 根据标记对应的地州EPARCHY_CODE、标记编码TAG_CODE、子系统编码SUBSYS_CODE和标记对应的作用标志USE_TAG查看系统个性参数表
     * 
     * @author
     * @param inparams
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getTagInfoBySubSys(String EPARCHY_CODE, String TAG_CODE, String USE_TAG, String SUBSYS_CODE, Pagination page) throws Exception
    {
        IData inparams = new DataMap();

        inparams.put("EPARCHY_CODE", EPARCHY_CODE);
        inparams.put("TAG_CODE", TAG_CODE);
        inparams.put("USE_TAG", USE_TAG);
        inparams.put("SUBSYS_CODE", SUBSYS_CODE);

        return Dao.qryByCode("TD_S_TAG", "SEL_ALL_BY_TAGCODE", inparams, page, Route.CONN_CRM_CEN);
    }

    /**
     * @Function: getTagInfos
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-7-15 上午11:41:16 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-15 lijm3 v1.0.0 修改原因
     */
    public static IDataset getTagInfosByTagCode(String eparchy_code, String tag_code, String subsys_code, String use_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("TAG_CODE", tag_code);
        param.put("SUBSYS_CODE", subsys_code);
        param.put("USE_TAG", use_tag);

        return Dao.qryByCode("TD_S_TAG", "SEL_BY_TAGCODE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryNormalTagInfoByTagCode(String eparchy_code, String tag_code, String subsys_code, String use_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("TAG_CODE", tag_code);
        param.put("SUBSYS_CODE", subsys_code);
        param.put("USE_TAG", use_tag);

        return Dao.qryByCode("TD_S_TAG", "SEL_ALL_BY_TAGCODE", param, Route.CONN_CRM_CEN);
    }

    public static IData queryTagInfo(String tagCode) throws Exception
    {

        IData param = new DataMap();

        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        param.put("TAG_CODE", tagCode);

        param.put("USE_TAG", "0"); // 有效的TAG

        IDataset tagset = Dao.qryByCode("TD_S_TAG", "SEL_BY_PK", param, Route.CONN_CRM_CEN);

        return tagset.isEmpty() ? new DataMap() : tagset.getData(0);
    }

    /**
     * @author huangsl
     */
    public static IDataset queryTagInfoByTagCode1(String eparchy_code, String tag_code, String subsys_code, String use_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("TAG_CODE", tag_code);
        param.put("SUBSYS_CODE", subsys_code);
        param.put("USE_TAG", use_tag);

        return Dao.qryByCode("TD_S_TAG", "SEL_BY_TAGCODE1", param, Route.CONN_CRM_CEN);
    }

    public static boolean queryTagNoCache(String tagCode, String eparchyCode) throws Exception
    {
        boolean flag = false;

        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("TAG_CODE", tagCode);
        param.put("USE_TAG", "0"); // 有效的TAG

        StringBuilder sql = new StringBuilder(500);
        sql.append(" SELECT TAG_CHAR ");
        sql.append(" FROM TD_S_TAG ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND TAG_CODE = :TAG_CODE");
        sql.append(" AND USE_TAG = :USE_TAG");
        sql.append(" AND (EPARCHY_CODE = :EPARCHY_CODE OR EPARCHY_CODE = 'ZZZZ')");

        IDataset ids = Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(ids))
        {
            return false;
        }
        if ("1".equals(ids.getData(0).getString("TAG_CHAR", "0")))
        {
            flag = true;
        }

        return flag;
    }
}
