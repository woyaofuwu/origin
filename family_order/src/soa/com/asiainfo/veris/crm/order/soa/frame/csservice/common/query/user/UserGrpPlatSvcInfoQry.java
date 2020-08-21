
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;

public class UserGrpPlatSvcInfoQry
{

    /**
     * @Function: chickComboBoxValue
     * @Description:查询基本接入号码
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:18:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset chickComboBoxValue(String ecBaseInCode) throws Exception
    {

        IData idata = new DataMap();
        idata.put("EC_BASE_IN_CODE", ecBaseInCode);
        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_ECBASEINCODE", idata, Route.CONN_CRM_CG);
    }

    /**
     * @Function: chickSvrCode
     * @Description: 查询基本接入号码
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:18:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset chickSvrCode(String accessNumber) throws Exception
    {

        IData idata = new DataMap();
        idata.put("ACCESS_NUMBER", accessNumber);
        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_ACCESSNUMBER", idata, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getAdminValidByajax
     * @Description: ADC 开户，判断管理员手机号码是否是在网用户
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:18:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getAdminValidByajax(String strBizInCode) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("BIZ_IN_CODE", strBizInCode);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_BIZINCODE", inparam, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * @Function: getComboBox
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:19:01 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getComboBox(String groupId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("GROUP_ID", groupId);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_GROUPID", idata, Route.CONN_CRM_CG);
        IDataset dataset = new DatasetList();
        if (result.size() > 0)
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData data = (IData) result.get(i);
                IDataset datasetList = DataHelper.filter(result, "EC_BASE_IN_CODE=" + data.get("EC_BASE_IN_CODE"));
                IData dataMap = (IData) datasetList.get(0);
                if (DataHelper.filter(dataset, "EC_BASE_IN_CODE=" + dataMap.get("EC_BASE_IN_CODE")).size() <= 0)
                {
                    dataset.add(dataMap);
                }
            }
        }
        return dataset;
    }

    /**
     * @Function: getDL100User
     * @Description:查询动力100用户（天津
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:19:11 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getDL100User(String custId) throws Exception
    {

        IData param = new DataMap();

        param.clear();
        param.put("CUST_ID", custId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" to_char(USER_ID) USER_ID, ");
        parser.addSQL(" to_char(CUST_ID) CUST_ID, ");
        parser.addSQL(" to_char(USECUST_ID) USECUST_ID, ");
        parser.addSQL(" BRAND_CODE, ");
        parser.addSQL(" PRODUCT_ID, ");
        parser.addSQL(" EPARCHY_CODE, ");
        parser.addSQL(" CITY_CODE, ");
        parser.addSQL(" CITY_CODE_A, ");
        parser.addSQL(" USER_PASSWD, ");
        parser.addSQL(" USER_DIFF_CODE, ");
        parser.addSQL(" USER_TYPE_CODE, ");
        parser.addSQL(" USER_TAG_SET, ");
        parser.addSQL(" USER_STATE_CODESET, ");
        parser.addSQL(" NET_TYPE_CODE, ");
        parser.addSQL(" SERIAL_NUMBER, ");
        parser.addSQL(" to_char(SCORE_VALUE) SCORE_VALUE, ");
        parser.addSQL(" CONTRACT_ID, ");
        parser.addSQL(" CREDIT_CLASS, ");
        parser.addSQL(" to_char(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE, ");
        parser.addSQL(" to_char(CREDIT_VALUE) CREDIT_VALUE, ");
        parser.addSQL(" CREDIT_CONTROL_ID, ");
        parser.addSQL(" ACCT_TAG, ");
        parser.addSQL(" PREPAY_TAG, ");
        parser.addSQL(" MPUTE_MONTH_FEE, ");
        parser.addSQL(" to_char(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
        parser.addSQL(" to_char(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
        parser.addSQL(" to_char(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
        parser.addSQL(" to_char(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
        parser.addSQL(" IN_NET_MODE, ");
        parser.addSQL(" to_char(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
        parser.addSQL(" IN_STAFF_ID, ");
        parser.addSQL(" IN_DEPART_ID, ");
        parser.addSQL(" OPEN_MODE, ");
        parser.addSQL(" to_char(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, ");
        parser.addSQL(" OPEN_STAFF_ID, ");
        parser.addSQL(" OPEN_DEPART_ID, ");
        parser.addSQL(" DEVELOP_STAFF_ID, ");
        parser.addSQL(" to_char(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
        parser.addSQL(" DEVELOP_DEPART_ID, ");
        parser.addSQL(" DEVELOP_CITY_CODE, ");
        parser.addSQL(" DEVELOP_EPARCHY_CODE, ");
        parser.addSQL(" DEVELOP_NO, ");
        parser.addSQL(" to_char(ASSURE_CUST_ID) ASSURE_CUST_ID, ");
        parser.addSQL(" ASSURE_TYPE_CODE, ");
        parser.addSQL(" to_char(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, ");
        parser.addSQL(" REMOVE_TAG, ");
        parser.addSQL(" to_char(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
        parser.addSQL(" to_char(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
        parser.addSQL(" REMOVE_EPARCHY_CODE, ");
        parser.addSQL(" REMOVE_CITY_CODE, ");
        parser.addSQL(" REMOVE_DEPART_ID, ");
        parser.addSQL(" REMOVE_REASON_CODE, ");
        parser.addSQL(" to_char(UPDATE_TIME, 'yyyy-mm-ddhh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID, ");
        parser.addSQL(" UPDATE_DEPART_ID, ");
        parser.addSQL(" REMARK, ");
        parser.addSQL(" RSRV_NUM1, ");
        parser.addSQL(" RSRV_NUM2, ");
        parser.addSQL(" RSRV_NUM3, ");
        parser.addSQL(" to_char(RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL(" to_char(RSRV_NUM5) RSRV_NUM5, ");
        parser.addSQL(" RSRV_STR1, ");
        parser.addSQL(" RSRV_STR2, ");
        parser.addSQL(" RSRV_STR3, ");
        parser.addSQL(" RSRV_STR4, ");
        parser.addSQL(" RSRV_STR5, ");
        parser.addSQL(" RSRV_STR6, ");
        parser.addSQL(" RSRV_STR7, ");
        parser.addSQL(" RSRV_STR8, ");
        parser.addSQL(" RSRV_STR9, ");
        parser.addSQL(" RSRV_STR10, ");
        parser.addSQL(" to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL(" to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL(" to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL(" RSRV_TAG1, ");
        parser.addSQL(" RSRV_TAG2, ");
        parser.addSQL(" RSRV_TAG3 ");
        parser.addSQL(" FROM TF_F_USER A ");
        parser.addSQL(" WHERE A.CUST_ID = :CUST_ID ");
        parser.addSQL(" AND A.BRAND_CODE = 'DLBG' ");
        parser.addSQL(" AND A.remove_tag = '0' ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * todo code_code 表里没有查到
     * 
     * @Description:根据GROUP_ID查询用户服务信息
     * @author wusf
     * @date 2009-9-17
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGrpPackagePlatService(IData data) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_PACKAGE_SVC_BY_GROUPID", data);
    }

    public static IDataset getGrpPackagePlatService(String groupId) throws Exception
    {
        IDataset platServiceData = new DatasetList();
        IData data = new DataMap();
        data.put("GROUP_ID", groupId);

        IDataset resultset = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_PACKAGE_SVC_BY_GROUPID", data);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String service_id = result.getString("SERVICE_ID");
            String product_id = result.getString("PRODUCT_ID");
            String service_name = USvcInfoQry.getSvcNameBySvcId(service_id);
            result.put("SERVICE_NAME", service_id + "-" + service_name);

            IDataset platSvcItema = AttrBizInfoQry.getBizAttr(service_id, "S", "ServPage", null, null);
            if (IDataUtil.isNotEmpty(platSvcItema)) // 存在这样的一个条件
            {
                IDataset attrBizCount = AttrBizInfoQry.getBizAttrCount(product_id, "P", "CrtMb", "ForbidOutNetMeb", "true");
                if (IDataUtil.isEmpty(attrBizCount)) // 不存在这样的一个条件
                {
                    platServiceData.add(result);
                }
            }
        }
        return platServiceData;
    }

    /**
     * todo code_code 表里没有查到
     * 
     * @Description:根据GROUP_ID查询用户服务信息
     * @author wusf
     * @date 2009-9-17
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGrpPlatService(IData data) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_SERVICE_BY_GROUPID", data);
    }

    /**
     * @Function: getGrpPlatSvcByBizInCode
     * @Description: 根据服务代码获取集团信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:21:12 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getGrpPlatSvcByBizInCode(String biz_in_code) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_IN_CODE", biz_in_code);

        SQLParser gparser = new SQLParser(param);

        gparser.addSQL(" SELECT DISTINCT CGU.USER_ID , ");
        gparser.addSQL(" CG.GROUP_ID, ");
        gparser.addSQL(" CG.GROUP_CONTACT_PHONE PHONE, ");
        gparser.addSQL(" CG.CUST_NAME GROUP_CUST_NAME, ");
        gparser.addSQL(" PCG.PRODUCT_ID, ");
        gparser.addSQL(" PCG.PRODUCT_NAME GROUP_PRODUCT_NAME, ");
        gparser.addSQL(" GP.BIZ_ATTR, ");
        gparser.addSQL(" GP.BIZ_IN_CODE, ");
        gparser.addSQL(" PC.RELATION_TYPE_CODE ");
        gparser.addSQL(" FROM TF_F_CUST_GROUP CG, ");
        gparser.addSQL(" TF_F_USER CGU, ");
        gparser.addSQL(" TF_F_USER_PRODUCT CPU, ");
        gparser.addSQL(" TD_B_PRODUCT PCG, ");
        gparser.addSQL(" TF_F_USER_GRP_PLATSVC GP, ");
        gparser.addSQL(" TD_B_PRODUCT_COMP PC ");
        gparser.addSQL(" WHERE 1=1 AND CG.GROUP_ID = GP.GROUP_ID ");
        gparser.addSQL(" AND CGU.USER_ID = GP.USER_ID ");
        gparser.addSQL(" AND CGU.USER_ID = CPU.USER_ID(+) ");
        gparser.addSQL(" AND CGU.PARTITION_ID = CPU.PARTITION_ID(+) ");
        gparser.addSQL(" AND PCG.PRODUCT_ID = CPU.PRODUCT_ID ");
        gparser.addSQL(" AND GP.BIZ_IN_CODE = :BIZ_IN_CODE ");
        gparser.addSQL(" AND PC.PRODUCT_ID = PCG.PRODUCT_ID ");
        gparser.addSQL(" AND CGU.REMOVE_TAG = '0' ");
        gparser.addSQL(" AND CG.REMOVE_TAG = '0'  ");
        gparser.addSQL(" AND CPU.MAIN_TAG = '1'  ");
        gparser.addSQL(" AND GP.END_DATE > SYSDATE ");
        gparser.addSQL(" AND CPU.END_DATE > SYSDATE ");

        return Dao.qryByParse(gparser, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getGrpPlatSvcByBizInCode
     * @Description:从集团库 查询GRP_PLATSVC
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:22:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IData getGrpPlatSvcByBizInCode(String biz_in_code, String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_IN_CODE", biz_in_code);
        param.put("USER_ID", user_id);

        IDataset idata = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_BIZ", param, Route.CONN_CRM_CG);

        return idata.size() > 0 ? idata.getData(0) : null;
    }

    /**
     * todo getVisit().setRouteEparchyCode(eparchyCode);怎么处理 查询GRP_PLATSVC
     * 
     * @param data
     * @return
     * @throws Exception
     * @author xiajj
     */
    public static IDataset getGrpPlatSvcByBizInCodes(IData data, String eparchyCode) throws Exception
    {

        IDataset idata = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_BIZ", data);

        return idata;
    }

    /**
     * @Function: getGrpServInfoByCustId
     * @Description: 根据客户编号CUST_ID查询平台服务信
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:27:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getGrpServInfoByCustId(String biz_in_code, String cust_id, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_STATE_CODE", biz_in_code);
        param.put("CUST_ID", cust_id);
        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_NOW_USE", param, pg);
    }

    /*
     * 根据用户ID查询旅游通/旅信通用户平台服务信息 imparams 需要传入字符串：EPARCHY_CODE,USER_ID
     */
    /**
     * @Function: getLxtGrpPlatSvcByUserId
     * @Description: 根据用户ID查询旅游通/旅信通用户平台服务信息 imparams 需要传入字符串：EPARCHY_CODE,USER_ID
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:29:14 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getLxtGrpPlatSvcByUserId(String user_id) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_LXT_BY_USERID", inparam, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * @Function: getplatsvcBybizeservgroup
     * @Description: 根据biz_code serv_code,group_id查询集团订购的adcmas业务 add by jiudian 20120208
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:00:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getplatsvcBybizeservgroup(String strbiz_code, String strBizInCode, String group_id) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("BIZ_IN_CODE", strBizInCode);
        inparam.put("GROUP_ID", group_id);
        inparam.put("BIZ_CODE", strbiz_code);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_BIZINCODE_BIZCODE_GID", inparam, Route.CONN_CRM_CG);
        if (result.size() > 0)
        {
            return result;
        }

        // 20090608 复从台帐表判断唯一
        IDataset result1 = Dao.qryByCode("TF_B_TRADE_GRP_PLATSVC", "SEL_BY_BIZINCODE_BIZCODE_GID", inparam);
        return result1;
    }

    /**
     * todo code_code 表里没有找到
     * 
     * @Description:根据GROUP_ID查询用户服务信息
     * @author wusf
     * @date 2009-9-17
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcByGrpIdBizeCode(IData data) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_PACKAGE_SVC_BY_GROUPID_BIZCODE", data, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getPlatSvcData
     * @Description: 从集团库 查询用户平台信息数据
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:49:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IData getPlatSvcData(String user_id, String service_id, String element_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);

        if (!"S".equals(element_type_code))
        {// 不是服务直接返回
            return null;
        }
        IDataset dataset = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_USERID_SVCID", param, Route.CONN_CRM_CG);

        if (null != dataset && !dataset.isEmpty())
        {
            return dataset.getData(0);
        }
        return null;
    }

    /**
     * @Function: getUserAttrByUserId
     * @Description: 更新useID查询集团用户的 个性化参数信息 @param userId 用户编码 @return IData 返回用户实例信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:49:46 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IData getUserAttrByUserId(String userId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);

        IDataset userattrs = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_USERID", idata, Route.CONN_CRM_CG);

        if (userattrs.size() > 0)
        {
            return userattrs.getData(0);
        }

        return new DataMap();
    }

    /**
     *todo getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);怎么处理
     * 
     * @Function: getUserAttrByUserIda
     * @Description: 更新useID查询集团用户的 个性化参数信息 @param userId 用户编码 @return IData 返回用户实例信息
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:53:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByUserIda(IData param) throws Exception
    {

        // TODO getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_USERID", param);
    }

    /**
     * @Function: getUserAttrByUserIda
     * @Description:从集团库 更新useID查询集团用户的 个性化参数信息 @param userId 用户编码 @return IData 返回用户实例信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:50:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserAttrByUserIda(String userId) throws Exception
    {
        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        IDataset resultset = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_USERID", idata, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++)
        {
            IData result = resultset.getData(i);
            result.put("BIZ_NAME", StaticUtil.getStaticValue("TD_M_BIZBLACKWHITE", result.getString("BIZ_ATTR")));
        }
        return resultset;

    }

    // SEL_ALL_BY_USERID_SVC

    public static IDataset getUserAttrByUserIdandSvc(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_ALL_BY_USERID_SVC", param);
    }

    /**
     * @Function: getUserGrpPlatSvcByUserId
     * @Description: 从集团库 根据用户ID查询用户平台服务信息 imparams 需要传入字符串：EPARCHY_CODE,USER_ID
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:52:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserGrpPlatSvcByUserId(String user_id) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_USERID", inparam, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * @Function: getUserGrpPlatSvcByUserIdSvcID
     * @Description: 从集团 根据用户ID查询, 服务ID查询 用户平台服务信息 imparams 需要传入字符串：USER_ID,SERVICE_ID
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:53:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserGrpPlatSvcByUserIdSvcID(String user_id, String service_id) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        inparam.put("SERVICE_ID", service_id);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_USERID_SVCID", inparam, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * @Function: getuserPlatsvcbybizcodeservcode
     * @Description: 从集团库 根据biz_code serv_code查询集团定制产品
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:55:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getuserPlatsvcbybizcodeservcode(String biz_code, String serv_code) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_CODE", biz_code);
        param.put("SERV_CODE", serv_code);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_BIZCODE_SERVCODE", param, Route.CONN_CRM_CG);
        return userattrs;
    }

    /**
     * @Function: getuserPlatsvcbygroupidbizcode
     * @Description:从集团库 根据groupid biz_code查询集团定制产品
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:57:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getuserPlatsvcbygroupidbizcode(String biz_code, String group_id) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_CODE", biz_code);
        param.put("GROUP_ID", group_id);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_GROUPID_BIZCODE", param, Route.CONN_CRM_CG);
        return userattrs;
    }

    /**
     * @Function: getuserPlatsvcbyservcode
     * @Description:从集团库 根据biz_code serv_code查询集团定制产品
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:58:56 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getuserPlatsvcbyservcode(String biz_state_code, String serv_code) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_STATE_CODE", biz_state_code);
        param.put("SERV_CODE", serv_code);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_SERVCODE", param, Route.CONN_CRM_CG);
        return userattrs;
    }

    /**
     * @Function: getuserPlatsvcbyserverid
     * @Description:从集团库 根据user_id,svcid查询用户订购服务的 plartsvc实例化参数 @param userId 用户编码 @return IData 返回用户实例信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:00:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IData getuserPlatsvcbyserverid(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);

        IDataset userattrs = Dao.qryByCodeParser("TF_F_USER_GRP_PLATSVC", "SEL_BY_USERID_SVCID", param, Route.CONN_CRM_CG);
        if (userattrs.size() > 0)
        {
            return userattrs.getData(0);
        }
        return new DataMap();
    }

    /*
     * 根据user_id,svcid查询用户订购服务的 plartsvc实例化参数 @param userId 用户编码 @return IData 返回用户实例信息
     */
    public static IDataset getuserPlatsvcbyUserId(String userID, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userID);
        param.put("SERVICE_ID", service_id);
        IDataset userattrs = Dao.qryByCodeParser("TF_F_USER_GRP_PLATSVC", "SEL_BY_USERID_SVCID", param, Route.CONN_CRM_CG);
        return userattrs;
    }

    /**
     * @Function: qryAdcMasBizInfo
     * @Description:从集团库 Adc/Mas业务查询(黑白名单查询)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:04:55 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset qryAdcMasBizInfo(String group_id, String product_id, String biz_code, String rb_list, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", group_id);
        param.put("PRODUCT_ID", product_id);
        param.put("BIZ_CODE", biz_code);
        param.put("RB_LIST", rb_list);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.PARTITION_ID,TO_CHAR(A.USER_ID) USER_ID, B.CUST_NAME,a.serial_number,");
        parser.addSQL("       decode(a.BIZ_TYPE_CODE,'001','17201','002','WLAN', '003','WAP', '004','SMS','005','MMS','006','KJAVA', '007','LBS', '008','EMAIL') BIZ_TYPE_CODE,");
        parser.addSQL("       a.SERV_CODE, a.BIZ_NAME,");
        parser.addSQL("       decode(a.ACCESS_MODE,'01','WEB','02','网上营业厅','03','WAP','04','SMS','05','MMS','06','KJAVA','07','1860/营业厅','08','BOSS') ACCESS_MODE,");
        parser.addSQL("       a.ACCESS_NUMBER, decode(a.OPER_STATE,'0','新增','04','暂停','05','恢复','08','变更','1','终止') OPER_STATE, a.PRICE, decode(a.BILLING_TYPE,'0','免费','1','包月','2','按次') BILLING_TYPE,");
        parser.addSQL("       F_SYS_GETCODENAME('static','PLAT_GRP_BIZ_PRI', a.BIZ_PRI, '') BIZ_PRI, ");
        parser.addSQL("       decode(a.BIZ_STATUS,'A','正常商用','N','暂停','S','内部测试','T','测试待审','R','试商用','E','终止') BIZ_STATUS,");
        parser.addSQL("       decode(a.BIZ_ATTR,'0','订购关系','1','白名单','2','黑名单','3','企业级','4','全局级') BIZ_ATTR,");
        parser.addSQL("       a.CS_URL, a.USAGE_DESC,a.INTRO_URL,TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,");
        parser.addSQL("       TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, TO_CHAR(A.FIRST_DATE, 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE, a.BIZ_CODE,");
        parser.addSQL("       F_SYS_GETCODENAME('static','PLAT_BILLINGMODE', a.BILLING_MODE, '') BILLING_MODE, a.rsrv_num1,c.product_id");
        parser.addSQL(" FROM TF_F_USER_GRP_PLATSVC A,TF_F_CUST_GROUP B,TF_F_USER C ");
        parser.addSQL(" WHERE B.REMOVE_TAG = '0' ");
        parser.addSQL("     AND C.REMOVE_TAG = '0' ");
        parser.addSQL("     AND B.CUST_ID=C.CUST_ID ");
        parser.addSQL("     AND C.USER_ID=A.USER_ID ");
        parser.addSQL("     AND (SYSDATE BETWEEN a.START_DATE AND a.END_DATE) ");
        parser.addSQL("     AND B.GROUP_ID=:GROUP_ID ");
        parser.addSQL("     AND C.PRODUCT_ID = TO_NUMBER(:PRODUCT_ID) ");
        parser.addSQL("     AND A.SERV_CODE=:BIZ_CODE ");
        parser.addSQL("     AND A.BIZ_ATTR =:RB_LIST ");// 黑白名单标记
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }

    /**
     * @Function: qryECBizByGrpID
     * @Description:查询集团客户已开通业务的接口
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:05:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset qryECBizByGrpID(String group_id) throws Exception
    {

        IData param = new DataMap();
        param.put("GROUP_ID", group_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select distinct c.biz_name,to_char(a.open_date, 'yyyy-mm-dd hh24:mi:ss') open_date ");
        parser.addSQL(" from tf_f_user a, tf_f_cust_group b, tf_f_user_grp_platsvc c ");
        parser.addSQL(" where 1 = 1  ");
        parser.addSQL(" and a.cust_id = b.cust_id  ");
        parser.addSQL(" and b.group_id = c.group_id ");
        parser.addSQL(" and b.group_id = :GROUP_ID ");
        parser.addSQL(" and a.remove_tag = '0' ");
        parser.addSQL(" and c.biz_status = 'A' ");
        return Dao.qryByParse(parser);
    }

    /**
     * @Function: qryProductInfo
     * @Description:查询集团产品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:07:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset qryProductInfo(String group_id, String serial_number, String biz_code, String biz_attr, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", group_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("BIZ_CODE", biz_code);
        param.put("BIZ_ATTR", biz_attr);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT  t1.brand_code,t.serial_number,t1.product_id,t.biz_code,t1.score_value,t1.credit_class,t1.credit_value,t1.in_date,t1.open_date  ");
        parser.addSQL(" FROM tf_f_user_grp_platsvc t,tf_f_user  t1 ");
        parser.addSQL(" WHERE t.user_id=t1.user_id");
        parser.addSQL(" AND t.group_id=:GROUP_ID ");
        parser.addSQL(" AND t.serial_number=:SERIAL_NUMBER ");
        parser.addSQL(" AND t.biz_code=:BIZ_CODE ");
        parser.addSQL(" AND t1.brand_code IN ('ADCG','MASG')");
        parser.addSQL(" AND t.biz_attr=:BIZ_ATTR ");
        parser.addSQL(" ORDER BY open_date ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Function: qryProductMebInfo
     * @Description:查询集团产品成员信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:09:17 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset qryProductMebInfo(String group_id, String serial_number, String biz_code, String biz_attr, String IS_Flag, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("GROUP_ID", group_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("BIZ_CODE", biz_code);
        param.put("BIZ_ATTR", biz_attr);

        SQLParser parser = new SQLParser(param);

        if ("0".equals(IS_Flag))
        {
            parser.addSQL("SELECT   t.serial_number,t.biz_type_code,t.biz_code,t.biz_name,t.access_mode,t.biz_state_code,t.price,t.billing_type,t.biz_pri,t.biz_status,t.biz_attr,t.intro_url  ");
            parser.addSQL(" FROM tf_f_user_grp_platsvc t,tf_f_user  t1 ");
            parser.addSQL(" WHERE t.user_id=t1.user_id");
            parser.addSQL(" AND t.group_id=:GROUP_ID ");
            parser.addSQL(" AND t.serial_number=:SERIAL_NUMBER ");
            parser.addSQL(" AND t.biz_code=:BIZ_CODE ");
            parser.addSQL(" AND t1.brand_code IN ('ADCG','MASG')");
            parser.addSQL(" AND t.biz_attr=:BIZ_ATTR");
            parser.addSQL(" AND t.end_date>SYSDATE");

        }
        else
        {
            parser.addSQL("SELECT   t.serial_number,t.biz_type_code,t.biz_code,t.biz_name,t.access_mode,t.biz_state_code,t.price,t.billing_type,t.biz_pri,t.biz_status,t.biz_attr,t.intro_url  ");
            parser.addSQL(" FROM tf_f_user_grp_platsvc t,tf_f_user  t1 ");
            parser.addSQL(" WHERE t.user_id=t1.user_id");
            parser.addSQL(" AND t.group_id=:GROUP_ID ");
            parser.addSQL(" AND t.serial_number=:SERIAL_NUMBER ");
            parser.addSQL(" AND t.biz_code=:BIZ_CODE ");
            parser.addSQL(" AND t1.brand_code IN ('ADCG','MASG')");
            parser.addSQL(" AND t.biz_attr=:BIZ_ATTR");

        }

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据USER_ID查询用户平台服务信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserGrpPlatSvcByUserId(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_USERID", param, Route.CONN_CRM_CG);
    }

    /**
     * @Function: queryAdcMasUserInfos
     * @Description: Adc/Mas业务查询(黑白名单查询) 用户信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午11:10:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset queryAdcMasUserInfos(String ec_user_id_str, String biz_code_str, Pagination pagination) throws Exception
    {

        IData data = new DataMap();

        data.put("EC_USER_ID", ec_user_id_str);
        data.put("BIZ_CODE", biz_code_str);
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT PARTITION_ID,TO_CHAR(USER_ID) USER_ID,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,BIZ_CODE,BIZ_NAME,");
        parser.addSQL("       TO_CHAR(START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,");
        parser.addSQL("       TO_CHAR(EC_USER_ID) EC_USER_ID,TO_CHAR(EC_SERIAL_NUMBER) EC_SERIAL_NUMBER,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3, ");
        parser.addSQL("       TO_CHAR(RSRV_NUM4) RSRV_NUM4,TO_CHAR(RSRV_NUM5) RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, ");
        parser.addSQL("       TO_CHAR(RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        parser.addSQL("       TO_CHAR(RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,REMARK,UPDATE_STAFF_ID,UPDATE_DEPART_ID, ");
        parser.addSQL("       TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME ");
        parser.addSQL(" FROM tf_f_user_grp_meb_platsvc ");
        parser.addSQL(" WHERE EC_USER_ID=TO_NUMBER(:EC_USER_ID)  ");
        parser.addSQL("     AND BIZ_CODE=:BIZ_CODE ");
        parser.addSQL("     AND (SYSDATE BETWEEN START_DATE AND END_DATE) ");
        return null;// Dao.qryByParse(parser, pagination, Route.CONN_CRM_1);
    }

    public static IDataset getUserGrpPlatSvcByUserIdDate(String userId, String timePoint) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("TIME_POINT", timePoint);

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_USERID_DATE", param);
    }
    
    /**
     * @Function: getuserPlatsvcbygroupidservcode
     * @Description:从集团库 根据groupid biz_code查询集团定制产品
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午10:57:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getuserPlatsvcbygroupidservcode(String serv_code, String group_id) throws Exception
    {
        IData param = new DataMap();
        param.put("SERV_CODE", serv_code);
        param.put("GROUP_ID", group_id);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_BY_GROUPID_SERVCODE", param, Route.CONN_CRM_CG);
        return userattrs;
    }
}
