
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpMebPlatSvcInfoQry
{

    /**
     * @Function: getGrpMemPlatSvcByUserIdEcUserId
     * @Description:查询useID,ecuserId查询集团成员个性参数信息 @param userId 用户编码 @return IData 返回用户实例信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-4-26 上午9:47:43 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 lijm3 v1.0.0 修改原因
     */
    public static IDataset getGrpMemPlatSvcByUserIdEcUserId(String userId, String ecuserId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("EC_USER_ID", ecuserId);
        return Dao.qryByCode("TF_F_USER_GRP_MEB_PLATSVC", "SEL_BY_USERID_ECUSERID", idata);
    }

    /**
     * @Function: getGrpMemPlatSvcByUserIdEcUserId22
     * @Description: 查询useID,ecuserId查询集团成员个性参数信息 @param userId 用户编码 @return IData 返回用户实例信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-4-26 上午9:48:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 lijm3 v1.0.0 修改原因
     */
    public static IDataset getGrpMemPlatSvcByUserIdEcUserId22(String userId, String ecuserId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        idata.put("EC_USER_ID", ecuserId);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_GRP_MEB_PLATSVC", "SEL_BY_USERID_ECUSERID", idata, Route.CONN_CRM_CG);
        return userattrs;
    }

    /**
     * @Function: getMemPlatSvc
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-4-26 上午9:48:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 lijm3 v1.0.0 修改原因
     */
    public static IDataset getMemPlatSvc(String user_id, String user_id_a, String service_id) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", user_id);
        idata.put("EC_USER_ID", user_id_a);
        idata.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_GRP_MEB_PLATSVC", "SEL_BY_USERID_ECUSERID_SERVICEID", idata);
    }

    /**
     * @Function: getMemPlatSvcALL
     * @Description: 该函数的功能描述
     * @param
     */
    public static IDataset getMemPlatSvcAll(String user_id, String service_id, String memEparchyCode) throws Exception
    {
        IData idata = new DataMap();
        idata.put("EC_USER_ID", user_id);
        idata.put("SERVICE_ID", service_id);
        return Dao.qryByCodeAllCrm("TF_F_USER_GRP_MEB_PLATSVC", "SEK_BY_ECUSERID_SERVICEID", idata, false);
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getMemPlatSvcInfoByUserID(String userId) throws Exception
    {
        IData iData = new DataMap();
        iData.put("USER_ID", userId);
        return Dao.qryByCodeParser("TF_F_USER_GRP_MEB_PLATSVC", "SEL_BY_USER_ID", iData);
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getMemPlatSvcInfoByUserIDataset(String userId) throws Exception
    {
        IData iData = new DataMap();
        iData.put("USER_ID", userId);
        return Dao.qryByCodeParser("TF_F_USER_GRP_MEB_PLATSVC", "SEL_BY_USERAID", iData);
    }

    public static IDataset getUserProductBySnUid(String sn, String ecUserId, String servCode) throws Exception
    {
        IData iData = new DataMap();
        if (!"".equals(sn))
            iData.put("SERIAL_NUMBER", sn);
        if (!"".equals(ecUserId))
            iData.put("EC_USER_ID", ecUserId);
        if (!"".equals(servCode))
            iData.put("SERV_CODE", servCode);

        IDataset dataset = IDataUtil.isNotEmpty(iData) ? Dao.qryByCodeParserAllCrm("TF_F_USER_GRP_MEB_PLATSVC", "SEL_USERPRODUCT_BY_SERIALNUM", iData, null, true) : null;
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);
                String startDate = data.getString("START_DATE", "");
                String endDate = data.getString("END_DATE", "");
                String bizTypeCode = data.getString("BIZ_TYPE_CODE", "");
                String bizStatus = data.getString("BIZ_STATUS", "");
                String bizAttr = data.getString("BIZ_ATTR", "");
                data.put("START_DATE", !"".equals(startDate) ? SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND) : "");
                data.put("END_DATE", !"".equals(endDate) ? SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND) : "");
                data.put("BIZ_TYPE_CODE", !"".equals(bizTypeCode) ? StaticUtil.getStaticValue("GRP_PLAT_BIZ_TYPE_CODE", bizTypeCode) : "");
                data.put("BIZ_STATUS", !"".equals(bizStatus) ? StaticUtil.getStaticValue("SP_BIZ_STATUS", bizStatus) : "");
                data.put("BIZ_ATTR", !"".equals(bizAttr) ? StaticUtil.getStaticValue("PLATSVC_BIZATTR", bizAttr) : "");
            }
        }
        return dataset;
    }

    public static IDataset getUserProductBySnUidPagination(String sn, String ecUserId, String servCode, Pagination pagination) throws Exception
    {
        IData iData = new DataMap();
        if (!"".equals(sn))
            iData.put("SERIAL_NUMBER", sn);
        if (!"".equals(ecUserId))
            iData.put("EC_USER_ID", ecUserId);
        if (!"".equals(servCode))
            iData.put("SERV_CODE", servCode);

        IDataset dataset = new DatasetList();
        if (IDataUtil.isNotEmpty(iData))
        {
            dataset = Dao.qryByCodeParser("TF_F_USER_GRP_MEB_PLATSVC", "SEL_USERPRODUCT_BY_SERIALNUM", iData, pagination, Route.getCrmDefaultDb());
        }
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);
                String startDate = data.getString("START_DATE", "");
                String endDate = data.getString("END_DATE", "");
                String bizTypeCode = data.getString("BIZ_TYPE_CODE", "");
                String bizStatus = data.getString("BIZ_STATUS", "");
                String bizAttr = data.getString("BIZ_ATTR", "");
                data.put("START_DATE", !"".equals(startDate) ? SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND) : "");
                data.put("END_DATE", !"".equals(endDate) ? SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND) : "");
                data.put("BIZ_TYPE_CODE", !"".equals(bizTypeCode) ? StaticUtil.getStaticValue("GRP_PLAT_BIZ_TYPE_CODE", bizTypeCode) : "");
                data.put("BIZ_STATUS", !"".equals(bizStatus) ? StaticUtil.getStaticValue("SP_BIZ_STATUS", bizStatus) : "");
                data.put("BIZ_ATTR", !"".equals(bizAttr) ? StaticUtil.getStaticValue("PLATSVC_BIZATTR", bizAttr) : "");
            }
        }
        return dataset;
    }
    /**
     * @Function: getMemPlatSvc
     * @Description: 该函数的功能描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: add by sundz
     * @date: 2013-4-26 上午9:48:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 lijm3 v1.0.0 修改原因
     */
    public static IDataset getMemPlatSvcByecUserIdServiceId(String ecUserId,String userId,String serviceId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("EC_USER_ID", ecUserId);
        idata.put("USER_ID", userId);
        idata.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TF_F_USER_GRP_MEB_PLATSVC", "SEL_BY_EC_USER_ID_AND_SERVICE_ID", idata);
    }


    public static IDataset getMemPlatSvcByecUserIdServiceId(String ecUserId,String serviceId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("EC_USER_ID", ecUserId);
        idata.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TF_F_USER_GRP_MEB_PLATSVC", "SEL_BY_ECUSERID_SERVICEID", idata);
    }
}
