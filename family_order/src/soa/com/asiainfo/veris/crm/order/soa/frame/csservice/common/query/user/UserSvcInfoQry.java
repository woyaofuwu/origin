
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.PackageElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;

public class UserSvcInfoQry
{
    /**
     * @Function: checkUserWide()
     * @Description: 互联网电视8M带宽校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-1 下午4:37:53 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-1 yxd v1.0.0 修改原因
     */
    public static IDataset checkUserWide(String wSerialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", wSerialNumber);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_COMMPSET", param);
        
    }

    /**
     * 查询用户选择的所有优惠和服务(USER_ID,USER_ID_A)
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getAllElementByUserId(IData data) throws Exception
    {

        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId()); code_cdoe表里没有
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_ALL_SVC_DISCNT_BY_USERID", data);
    }

    /**
     * 判断用户是否签约了自动交费代扣
     * 
     * @param pd
     *            pageData
     * @param param
     *            user_id service_id
     * @return 已经签约： true ； 未签约：false
     * @throws Exception
     */
    public static boolean getAutoPayContractState(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        IDataset list = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_ID_SVC", param);
        if (list != null && list.size() > 0)
        {
            return true;
        }
        else
            return false;
    }

    public static IDataset getByPMode(String userId, String productMode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_MODE", productMode);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SCSVC_BY_PRODUCTMODE", param);
    }

    /**
     * @Function: getElementFromPackageByUser
     * @Description: 查询tf_f_user_element 查询用户选择了那些元素
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:47:01 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getElementFromPackageByUser(String user_id, String product_id, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PRODUCT_ID", product_id);

        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        IDataset elementList = Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_DISCNT_ELE_BY_USERID_PRODID", param, page);
        if (IDataUtil.isNotEmpty(elementList))
        {
            FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(elementList, param, null);// 填充productId和packageId
            
            ElemInfoQry.fillElementName(elementList);
        }
        
        elementList = DataHelper.filter(elementList, "PRODUCT_ID="+product_id);
        
        return elementList;
    }

    /**
     * @Function: getElementFromPackageByUserA
     * @Description: 查询用户选择了那些元素(USER_ID,USER_ID_A)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:48:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getElementFromPackageByUserA(String user_id, String user_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_DISCNT_ELE_BY_USERID_USERIDA", param);
    }

    /**
     * 获取用户订购元素信息
     * 
     * @param pd
     * @param td
     * @return dataset
     * @throws Exception
     */
    public static IDataset getElementInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_ALLELEMENTS", param);
        return dataset;// dataset.isEmpty() ? new DatasetList() : CreatePersonUserBean.changeElements(dataset);
    }

    public static IDataset getElementInfoByUserIdProductId(String userId, String productId) throws Exception
    {
    	//td_b_package_element t WHERE t.rsrv_tag2 IN ('R','A','N')
    	//符合这个条件的只有 S 23 ；D 5921 产生品已废弃
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        IDataset sl=new DatasetList();
        IDataset SVC =getGrpSvcInfoByUserId(userId,"23");
        IDataset DIS =UserDiscntInfoQry.getAllDiscntByUser(userId,"5921");
        if(DataUtils.isNotEmpty(SVC)){
            for (int i = 0; i < SVC.size(); i++)
            {
                IData element = SVC.getData(i);
                element.put("ELEMENT_ID",element.getString("SERVICE_ID") );
                element.put("RSRV_STR5","3元/月" );
                element.put("RSRV_TAG2","A" );
                element.put("SERVICE_NAME","来电显示");
                element.put("ELEMENT_TYPE_CODE","S");
                element.put("ELEMENT_STATE","1");
            }
            sl.addAll(SVC);
        }
        if(DataUtils.isNotEmpty(DIS)){
            for (int i = 0; i < DIS.size(); i++)
            {
                IData element = DIS.getData(i);
                element.put("ELEMENT_ID",element.getString("DISCNT_CODE") );
                element.put("RSRV_STR5","" );
                element.put("RSRV_TAG2","A" );
                element.put("SERVICE_NAME","无线固话国际长途3元优惠包");
                element.put("ELEMENT_TYPE_CODE","D");
                element.put("ELEMENT_STATE","1");
            }
            sl.addAll(DIS);

        }
        return sl;
    }

    /**
     * 根据user_id和service_id查询服务信息
     * 
     * @param user_id
     * @param service_id
     * @return
     * @throws Exception
     */
    public static IDataset getGrpSvcInfoByUserId(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID", param, Route.CONN_CRM_CG);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, param, null);
        
        return userSvcs;
    }
    
    /**
     * 根据user_id和service_id查询服务信息
     * 
     * @param user_id
     * @param service_id
     * @param end_date
     * @return
     * @throws Exception
     */
    public static IDataset getGrpSvcInfoByUserIdEndDate(String user_id, String service_id, String end_date) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        param.put("END_DATE", end_date);
        IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_BY_ENDDATE", param, Route.CONN_CRM_CG);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, param, null);
        
        return userSvcs;
    }

    /**
     * @Function: getMainSvcUserId
     * @Description: 根据userId查询用户主体信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:48:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getMainSvcUserId(String userId) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_MAINSVC", inparams);
    }

    public static IDataset getMainSvcUserId(String userId, String routeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        return Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_USER_MAINSVC", inparams, routeId);
    }

    /*
     * 2017/03/21
     * liaolc
     */
    public static IDataset qryUserSvcByUserIdProId(String userId,  String offerCode) throws Exception
    {
        if (StringUtils.isBlank(userId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("OFFER_CODE", offerCode);
        IDataset resultset = Dao.qryByCodeParser( "TF_F_USER_SVC", "SEL_USERSVC_USERID_PRODUCTID" ,param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(resultset, null, null);// 填充productId和packageId
        
        return resultset;
    }

    /**
     * @Function: getPackagesByUserProd
     * @Description: 该查询用户选择了一个产品中的那些包
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:50:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getPackagesByUserProd(String userId, String product_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", product_id);
        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_DISCNT_BY_PK_USERID", param);
    }

    /**
     * @Function: getSerByBS
     * @Description: 获取服务信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:51:19 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getSerByBS(String biz_code) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_CODE", biz_code);
        return Dao.qryByCode("TF_COP_SERVICE", "SEL_BY_BS", param, Route.CONN_CRM_CEN);
    }

    /**
     * @Function: getServElementByGrpServ
     * @Description: 查询集团定制的包中相关服务元素
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:16:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getServElementByGrpServ(String user_id, String package_id, String trade_staff_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_STAFF_ID", trade_staff_id);
        param.put("PACKAGE_ID", package_id);
        param.put("USER_ID", user_id);
        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_MEBSERV_BY_GRPSVC", param, Route.CONN_CRM_CG);
    }

    /**
     * @Function: getServElementByGrpServNoPriv
     * @Description:集团库 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:52:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getServElementByGrpServNoPriv(String user_id, String package_id) throws Exception
    {

        IData param = new DataMap();
        param.put("PACKAGE_ID", package_id);
        param.put("USER_ID", user_id);
        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_MEBSERV_BY_GRPSVC_NO_PRIV", param, Route.CONN_CRM_CG);
    }

    /**
     * 查询tf_f_user_element 查询用户选择了那些元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getServFromPackageByUser(IData data) throws Exception
    {

        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());code_code表里没有
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_ELE_BY_USERID_PRODID", data);
    }

    /**
     * 根据SEL_BY_USERID_SVC20查询信息
     * 
     * @param userId
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getSvcByUserIdSvc20(String userId, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_SVC20", param);
    }
    
    /**
     * 根据SEL_BY_USERID_SVC20_1查询信息
     * 
     * @param userId
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getSvcByUserIdSvc20_1(String userId, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_SVC20_1", param);
    }

    /**
     * @Function: getSvcChangeTradePara
     * @Description: 获取服务状态参数
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:54:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getSvcChangeTradePara(String trade_type_code, String brand_code, String product_id, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", trade_type_code);
        param.put("BRAND_CODE", brand_code);
        param.put("PRODUCT_ID", product_id);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_S_TRADE_SVCSTATE", "SEL_BY_PK", param);
    }

    /**
     * @Function: getSVCNUM
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:55:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getSVCNUM(String user_id, String user_id_a, String service_id, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("SERVICE_ID", service_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select  COUNT(1) SVC_NUM FROM TF_F_USER_SVC us ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" and us.user_id = to_number(:USER_ID) ");
        parser.addSQL(" and us.user_id_a = to_number(:USER_ID_A) ");
        parser.addSQL(" AND us.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" and us.SERVICE_ID =:SERVICE_ID ");
        parser.addSQL(" AND us.START_DATE<=SYSDATE ");
        parser.addSQL(" AND us.END_DATE>SYSDATE ");
        return Dao.qryByParse(parser, pagination);
    }

    public static String getSvcStateNew(IData inparams) throws Exception
    {
        StringBuilder userStateCode = new StringBuilder(inparams.getString("USER_STATE_CODESET", ""));
        StringBuilder svcState = new StringBuilder();

        IData comData = new DataMap();

        comData.put("USER_ID", inparams.getString("USER_ID", ""));

        IDataset usersvc = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_MAINSVC", comData);

        if (usersvc == null || usersvc.size() <= 0)
        {
            usersvc = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_LAST_MAINSVC", comData);
            if (usersvc == null || usersvc.size() <= 0)
            {
                return svcState.toString();
            }
        }

        comData.put("SERVICE_ID", usersvc.get(0, "SERVICE_ID", ""));

        if (userStateCode.length() == 0)
        {
            IDataset vUserSvcstate = Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC", comData);
            if (vUserSvcstate == null || vUserSvcstate.size() == 0) // 无状态
            {
                return svcState.toString();
            }
            // 将服务状态编码列表拼成状态编码集
            for (int i = 0; i < vUserSvcstate.size(); i++)
            {
                userStateCode.append(((IData) vUserSvcstate.get(i)).getString("STATE_CODE", ""));
            }
        }
        comData.put("STATECODESET", userStateCode.toString());

        IDataset vServicestate = USvcStateInfoQry.qryStateNameBySvcIdStateCode(comData.getString("SERVICE_ID"), comData.getString("STATECODESET"));

        boolean firstState = true;

        for (int i = 0; i < vServicestate.size(); i++)
        {
            if (!firstState)
            {
                svcState.append(",");
            }
            svcState.append(((IData) vServicestate.get(i)).getString("STATE_NAME", ""));
            firstState = false;
        }
        return svcState.toString();
    }

    /**
     * 根据user_id和service_id查询服务信息
     * 
     * @param user_id
     * @param service_id
     * @return
     * @throws Exception
     */
    public static IDataset getSvcUserId(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, param, null);
        
        return userSvcs;
    }

    /**
     * @Function: getSvcUserId
     * @Description: 根据user_id和service_id查询服务信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:56:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getSvcUserId(String user_id, String user_id_a, String service_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("SERVICE_ID", service_id);
        IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_SVCID", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, param, null);
        
        return userSvcs;
    }

    public static IDataset getSvcUserIdPf(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID_PF", param);
    }

    /**
     * @Function: getUSAGE_DESC
     * @Description: 查询用户的基本业务
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:56:22 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUSAGE_DESC(String user_id) throws Exception
    {

        StringBuilder sql = new StringBuilder();
        sql.append("select service_id from tf_f_user_svc where user_id = " + user_id + " and main_tag = '1' and sysdate between start_date and end_date and ");
        sql.append("exists (select service_id from tf_f_user_svcstate where user_id = tf_f_user_svc.user_id and service_id = tf_f_user_svc.service_id and " + "state_code = '0' and sysdate between start_date and end_date)");
        IDataset resultset = Dao.qryBySql(sql, new DataMap());

        return resultset;
    }

    /**
     * @Function: getValidElementFromPackageByUserA
     * @Description: 查询用户选择了那些元素(USER_ID,USER_ID_A)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:10:53 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserElementFromPackageByUserA(String user_id, String user_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        IDataset elementList = Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_DISCNT_VAILD_BY_USERID_USERIDA", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(elementList, param, null);// 填充productId和packageId
        
        return elementList;
    }

    /**
     * @Function: getUserEnableElement
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:56:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserEnableElement(String userId, String elementId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        param.put("SERVICE_ID", elementId);

        return Dao.qryByCode("TF_F_USER_SVC", "USER_SVC_SEL", param);
    }

    /**
     * @Function: getUserLongService
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:56:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserLongService(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVC", "USER_LONG_SVC_SEL", param);

    }

    /*
     * 查询用户已经订购的物联网包实例
     */
    public static IDataset getUserPkgIdInstPF(String user_id) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_PKGID_INST_PF", inParams);
    }

    /**
     * @Function: getUserProductSvc
     * @Description: 查询一个用户定购某个集团用户的产品的服务信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:57:48 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductSvc(String userId, String user_id_a, Pagination pagination) throws Exception
    {
        if (StringUtils.isBlank(userId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID不能为空");
        }

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", user_id_a);
        IDataset svcListDataset = Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_USERSVC_USERID_USERIDA", param, pagination);
        if (IDataUtil.isNotEmpty(svcListDataset))
        {
            FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(svcListDataset, param, null);// 填充productId和packageId
            for (int i = 0; i < svcListDataset.size(); i++)
            {
                IData svcData = svcListDataset.getData(i);
                String serviceId = svcData.getString("SERVICE_ID", "");
                String service_name = USvcInfoQry.getSvcNameBySvcId(serviceId);
                svcData.put("ELEMENT_NAME", service_name);
            }
        }

        return svcListDataset;
    }

    /**
     * @Function: getUserRomService
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午8:58:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserRomService(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVC", "USER_ROM_SVC_SEL", param);

    }

    /**
     * @Function: getUserServByPk
     * @Description: 获取用户服务信息, 必需要USER_ID,PRODUCT_ID,PACKAGE_ID,SERVICE_ID,不需要USER_ID_A,INST_ID
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:00:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserServByPk(String userId, String user_id_a, String product_id, String package_id, String service_id, String inst_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", user_id_a);
        param.put("PRODUCT_ID", product_id);
        param.put("PACKAGE_ID", package_id);
        param.put("SERVICE_ID", service_id);
        param.put("INST_ID", inst_id);
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_USRSVC_BYID", param);
        return dataset;
    }

    /**
     * 获取产品信息
     * 
     * @author tengg
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset getUserService1(IData params, String eparchycode) throws Exception
    {

        // TODO getVisit().setRouteEparchyCode( eparchycode);code_code表里没有
        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_NOW_1", params, eparchycode);
        return dataset;
    }

    /**
     * @Function: getUserSingleProductSvc
     * @Description: 通过USER_ID、USER_ID_A、PRODUCT_ID、PACKAGE_ID、SERVICE_ID、INST_ID查询用户某条服务
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:03:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserSingleProductSvc(String userId, String user_id_a, String product_id, String package_id, String service_id, String inst_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", user_id_a);
        param.put("PRODUCT_ID", product_id);
        param.put("PACKAGE_ID", package_id);
        param.put("SERVICE_ID", service_id);
        param.put("INST_ID", inst_id);
        IDataset userSvcs = Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_BY_SERVICE_ID", param, pagination);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, param, null);// 填充productId和packageId
        
        userSvcs = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(userSvcs, product_id, package_id);
        
        return userSvcs;
    }
    
    /**
     * @Function: getUserProductSvcByUserIdAndInstId
     * @Description: 通过USER_ID、INST_ID查询用户某条服务
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:03:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserProductSvcByUserIdAndInstId(String userId, String inst_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("INST_ID", inst_id);
        IDataset userSvcs = Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_BY_SERVICE_ID", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, null, null);// 填充productId和packageId
        
        return userSvcs;
    }

    /**
     * @Function: getUserSvcBycon
     * @Description:从集团库 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:04:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserSvcBycon(String userId, String user_id_a, String product_id, String package_id, String service_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", user_id_a);
        param.put("PRODUCT_ID", product_id);
        param.put("PACKAGE_ID", package_id);
        param.put("SERVICE_ID", service_id);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_PID_PAGID_SVCID", param, Route.CONN_CRM_CG);
    }

    public static IDataset getUserSvcById(String user_id, String service_id) throws Exception
    {
        IData temp = new DataMap();
        temp.put("USER_ID", user_id);
        temp.put("SERVICE_ID", service_id);

        return Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_USER_SVCEND_PRIV", temp);
    }

    /**
     * 根据userid查询非主体服务的所有服务
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvcByIdForNotMain(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" select *   FROM TF_F_USER_SVC us ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" and us.user_id = to_number(:USER_ID) ");
        parser.addSQL(" AND us.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" AND START_DATE<=SYSDATE ");
        parser.addSQL(" AND END_DATE>SYSDATE ");
        parser.addSQL(" AND us.MAIN_TAG!='1'");
        return Dao.qryByParse(parser);

    }

    public static IDataset getUserSvcByPriv(String user_id) throws Exception
    {
        IData temp = new DataMap();
        temp.put("USER_ID", user_id);

        return Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_USER_SVCEND_PRIV", temp);
    }

    /**
     * 根据开始时间，结束时间查询
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvcByStartEndDate(String userId, String startDate, String endDate) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_STARTDATE_ENDDATE", params);
    }

    /**
     * 根据user_id、user_id_a查询集团用户的服务
     * 
     * @return IData 返回用户服务信息
     * @author xiajj
     */
    public static IDataset getUserSvcByUserIdAB(String USER_ID, String user_ida) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", USER_ID);
        params.put("USER_ID_A", user_ida);
        IDataset userdiscnts = Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVC_USERID_USERIDA", params);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userdiscnts, params, null);// 填充productId和packageId
        
        return userdiscnts;
    }

    /**
     * @Function: getUserSvcByUserIdAB
     * @Description:从集团库 根据user_id、user_id_a查询集团用户的服务
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:05:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserSvcByUserIdABForGrp(String userId, String user_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", user_id_a);
        IDataset userdiscnts = Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVC_USERID_USERIDA", param, Route.CONN_CRM_CG);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userdiscnts, param, null);// 填充productId和packageId
        
        return userdiscnts;
    }

    public static IDataset getUserSvcByUserIdAndSvcId(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_ID_SERVICE_ID", param);
    }

    public static int getUserSvcForModify45(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);

        IDataset datas = Dao.qryByCode("TF_F_USER_SVC", "SEL_FOR_MODIFY45", param);
        if (datas != null)
        {
            return datas.size();
        }
        return 0;
    }

    /**
     * 查询用户是否有GPRS服务
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvcGprs(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_GPRS_SVC", params);
    }

    public static IDataset getUserSvcInfoByUserSvcId(IData param) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID", param);

        return result;
    }

    public static int getUserSvcInfoByUserSvcId(String userId, String service_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", service_id);
        IDataset datas = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID", param);
        if (datas != null)
        {
            return datas.size();
        }
        return 0;
    }

    public static IDataset getUserSvcInfos(String userId, String productId, String brandCode, String eparchyCode, String rsrv_str9) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("BRAND_CODE", brandCode);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("RSRV_STR9", rsrv_str9);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_PLATORDERINFO_BY_SN_TEST", param);

    }

    /**
     * 查询表方法
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author zhuyu 2013-6-24
     */
    public static IDataset getUserSvcInfosbyUserID(String TableName, String sqlName, IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_ID8", inparams);

    }

    public static IDataset getUserSvcInfosByUserIdProductId(String userId, String product_id, Pagination pag) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", product_id);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_PRODUCTID_2", param, pag);
    }

    /**
     * @Function: getUserSvcRecord
     * @Description: 得到用户某个服务的订购记录
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:07:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserSvcRecord(String userId, String svcId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("SERVICE_ID", svcId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_COUNT", cond);
    }

    /**
     * 根据服务编码集获取服务列表
     * 
     * @param SERVICEIDS
     *            11,22,111
     * @param INSTIDS
     *            11,22,111
     * @return
     * @throws Exception
     */
    public static IDataset getUserSvcs(IData data) throws Exception
    {
        SQLParser sqlParser = new SQLParser(data);
        sqlParser.addSQL("select INST_ID,partition_id, user_id, user_id_a, service_id, main_tag,  inst_id, campn_id, start_date, end_date,");
        sqlParser.addSQL(" update_time, update_staff_id, update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3,");
        sqlParser.addSQL(" rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3");
        sqlParser.addSQL("  from TF_F_USER_SVC a");
        sqlParser.addSQL(" where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
        sqlParser.addSQL(" and a.user_id = :USER_ID");
        sqlParser.addSQL(" and a.service_id in(" + data.get("SERVICEIDS") + ")");
        sqlParser.addSQL(" and a.inst_id in(" + data.get("INSTIDS") + ")");

        return Dao.qryByParse(sqlParser);
    }

    public static IDataset getUserSvcsByLimitNp(String userId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId); // 用户标识
        params.put("EPARCHY_CODE", eparchyCode); // 兑换优惠代码

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_LIMIT_NP", params);
    }

    public static IDataset getUserSvcStateByUserId(String userId, String serviceId) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("USER_ID", userId);
        inParams.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC", inParams);
    }

    public static IDataset getUSEsvcInfo(String user_id, String user_id_a, String service_id, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("SERVICE_ID", service_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select *   FROM TF_F_USER_SVC us ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" and us.user_id = to_number(:USER_ID) ");
        parser.addSQL(" and us.user_id_a = to_number(:USER_ID_A) ");
        parser.addSQL(" AND us.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
        parser.addSQL(" AND START_DATE<=SYSDATE ");
        parser.addSQL(" AND END_DATE>SYSDATE ");
        parser.addSQL(" AND us.service_id=:SERVICE_ID");
        return Dao.qryByParse(parser, pagination);
    }

    public static IDataset getValidElementFromPackageByUserA(String user_id, String user_id_a) throws Exception
    {
        IDataset elementList = getUserElementFromPackageByUserA(user_id, user_id_a);
        if (IDataUtil.isNotEmpty(elementList))
        {
            ElemInfoQry.fillElementName(elementList);
        }

        return elementList;
    }

    /**
     * @Function: getValidElementFromPackageByUserAndUserA
     * @Description: 查询用户选择了那些元素(USER_ID,USER_ID_A)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:10:53 Modification History: Date Author Version Description 2013-5-3 updata v1.0.0
     *        修改原因:适用动力100服务记在主用户下，资费记在子用户下
     */
    public static IDataset getValidElementFromPackageByUserAndUserA(String user_id, String user_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        IDataset elementList = Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_DISCNT_VAILDELEMENT_BY_USERID_USERIDA", param);
        if (IDataUtil.isNotEmpty(elementList))
        {
            FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(elementList, param, null);// 填充productId和packageId
            for (int i = 0; i < elementList.size(); i++)
            {
                IData element = elementList.getData(i);
                
                if (StringUtils.isBlank(element.getString("PRODUCT_ID")) || StringUtils.isBlank(element.getString("PACKAGE_ID"))
                        || StringUtils.equals("-1", element.getString("PRODUCT_ID")) || StringUtils.equals("-1", element.getString("PACKAGE_ID")) )
                {
                    IDataset userGrpPkgInfos = UserGrpPkgInfoQry.getUserGrpPkgInfoByPk(user_id_a, "", "", element.getString("ELEMENT_ID"), element.getString("ELEMENT_TYPE_CODE"));
                    if (IDataUtil.isNotEmpty(userGrpPkgInfos))
                    {
                        element.put("PACKAGE_ID", userGrpPkgInfos.getData(0).getString("PACKAGE_ID"));
                        element.put("PRODUCT_ID", userGrpPkgInfos.getData(0).getString("PRODUCT_ID"));
                    }
                }
                
            }
            ElemInfoQry.fillElementName(elementList);
        }

        return elementList;
    }

    /**
     * @Function: isMemberHasBWListService
     * @Description: 根据成员用户id和服务id判断该成员有没有订购指定服务 TODO
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:11:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static boolean isMemberHasBWListService(String memberUserId, String serviceId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", memberUserId);
        param.put("SERVICE_ID", serviceId);
        IDataset result = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID", param);
        if (!IDataUtil.isEmpty(result))
        {
            return true;
        }
        return false;
    }
    /**
     * @Function: querySvcByUserIDandSVC
     * @Description: 判断用户依赖服务
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chenmw3
     */
    public static IDataset qrySvcInfoByUserIdSvcId(String user_id, String service_id,String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_SVC_ID", param, routeId);
        return dataset;
    }


    /**
     * @Function: querySvcByUserIDandSVC
     * @Description: 判断用户依赖服务
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:12:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qrySvcInfoByUserIdSvcId(String user_id, String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("SERVICE_ID", service_id);
        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_SVC_ID", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, param, null);
        
        return dataset;
    }

    /**
     * @Function: queryUserSVCInfo
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:14:52 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryUserSvcByUserId(String userId) throws Exception
    {
        return qryUserSvcByUserId(userId, null);
    }

    public static IDataset qryWlwUserSvcByUserId(String userId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID",userId);
    	
    	IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_WLW_BY_USERID_NOW", param);
    	return dataset;
    }
    
    /**
     * @Function: getUserService
     * @Description: 获取产品信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:01:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryUserSvcByUserId(String user_id, String routeId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);

        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_NOW", param, routeId);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, param, null);
        
        return dataset;
    }

    public static IDataset qryUserSvcByUserSvcId(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        IDataset userSvcs = Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_BY_USERSVC_ID", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, param, null);
        
        return userSvcs;
    }

    public static IDataset queryAllUserSvcsInSelectedElements(String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset iDataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_IN_SELECTED_ALL", param, eparchyCode);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(iDataset, param, null);
        
        return iDataset;
    }

    /**
     * 国际及港澳台业务退订后的15天才 允许押金清退查询
     * 
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-17
     */
    public static IDataset queryCancelLongRoamService(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_CANCEL_LONGROAMSERVICE_TIME", param);
    }

    /**
     * @Function: queryGPRSSvcByUserId
     * @Description: 根据userId查询服务功能
     * @param: @param userId
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 4:00:04 PM Jul 25, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Jul 25, 2013 longtian3 v1.0.0 TODO:
     *        
     *        原sql拆分，查询TD_B_SERVICE表改为调产商品接口  modify by duhj  2017/03/19
     */
    public static IDataset queryGPRSSvcByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset results= Dao.qryByCode("TF_F_USER_SVC", "SEL_GPRS_BY_USERID_NEW", param);//原sql  SEL_GPRS_BY_USERID
        
        IData temp = new DataMap();
        String service_Id = "";
        if(IDataUtil.isNotEmpty(results)){
        	for (int i = 0; i < results.size(); i++) {
        		temp = results.getData(i);
        		service_Id = temp.getString("SERVICE_ID");				
				IData  res=USvcInfoQry.qryServInfoBySvcId(service_Id);				
				if(IDataUtil.isNotEmpty(res)&&res.getString("SERVICE_NAME").contains("GPRS")){
					temp.put("SERVICE_NAME", res.getString("SERVICE_NAME"));
				}else{
					results.remove(i);
					i--;
				}
			}
        }
        return results;
    }

    public static IDataset querySerivceByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SERIVCE_BY_USERID", param);
    }
    
    public static IDataset querySerivceByUserIdWithLimit(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT A.SERVICE_ID, A.INST_ID,");
        sql.append("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        sql.append("  FROM TF_F_USER_SVC A ");
        sql.append(" WHERE A.USER_ID = TO_NUMBER(:USER_ID) ");
        sql.append("   AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        sql.append("   AND SYSDATE <= A.END_DATE ");
        sql.append("   AND NOT EXISTS (SELECT 1 ");
        sql.append("          FROM TD_S_COMMPARA C ");
        sql.append("         WHERE C.SUBSYS_CODE = 'CSM' ");
        sql.append("           AND C.PARAM_ATTR = '4' ");
        sql.append("           AND C.PARA_CODE1 = 'SJYYT' ");
        sql.append("           AND C.END_DATE > SYSDATE ");
        sql.append("           AND C.PARAM_CODE = A.SERVICE_ID) ");
        sql.append(" ORDER BY A.SERVICE_ID ");

        return Dao.qryBySql(sql, param);
    }
    
    


    public static IDataset querySpByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SP_BY_USERID", param);
    }

    /**
     * 作用：根据USER_ID\PRODUCT_ID，查询产品中的服务订购的条数
     * 
     * @author luojh
     * @param user_id
     * @param Product_id
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset querySvcByUserIdAndProducId(String user_id, String product_id, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("PRODUCT_ID", product_id);

        // TODO getVisit().setRouteEparchyCode(eparchyCode);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_PRODUCTID", param, eparchyCode);
    }

    public static IDataset querySvcInfosByInstId(String USER_ID, String INST_ID, String routeId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", USER_ID);
        cond.put("INST_ID", INST_ID);
        if (routeId != null)
        {
            return Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVC_BY_INSTID", cond, Route.CONN_CRM_CEN);
        }
        else
        {
            return Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVC_BY_INSTID", cond);
        }
    }

    /**
     * 查询用户所有的服务，包括已生效的和未生效的
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-3-4
     * @param pd
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAllSvc(String userId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_ALL_USERSVC", cond);
    }

    public static IDataset queryUserInfoBySn02(String userId, String tag, String productId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("RSRV_STR9", tag);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVCDERINFO_BY_SN02", param);
    }

    public static IDataset queryUserNormalSvc(String userId, String prouctId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("PRODUCT_ID", prouctId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_INTF_HAIN", params);
    }
    
  //海南第三代订单中心改造
    public static IDataset queryUserNormalSvcNow(String userId, String productId, String offerInstId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        IDataset tempUserSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_INTF_HAIN_NOW", params);
        IDataset userNormalSvcs = new DatasetList();
        if(IDataUtil.isNotEmpty(tempUserSvcs)){
        	String productName = "";
            OfferCfg offerCfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if(offerCfg != null){
            	productName = offerCfg.getOfferName();
            }
        	int tempUserSvcSize = tempUserSvcs.size();
        	for(int i = 0 ; i < tempUserSvcSize ; i++){
        		IData userSvc = tempUserSvcs.getData(i);
        		IDataset svcOfferRelInfos = UserOfferRelInfoQry.queryUserOfferRelInfosByRelOfferInstId(userSvc.getString("INST_ID",""));
        		String groupId = "-1";
        		String offerCode = "-1";
        		String forceOfferType = BofConst.ELEMENT_TYPE_CODE_PRODUCT;
        		if(IDataUtil.isNotEmpty(svcOfferRelInfos)){
        			if(svcOfferRelInfos.size() > 1){//服务有继承的情况 主产品取的是有效的那一条   老系统product_id为新的主产品 所以取开始时间较大那一条
        				IDataset maxStartDatas = OfferUtil.findMPMaxStartDate(svcOfferRelInfos);
        				for(Object obj : maxStartDatas){
        					IData maxStartData = (IData) obj;
        					groupId = maxStartData.getString("GROUP_ID","-1");
        					offerCode = maxStartData.getString("OFFER_CODE","");
        				}
        			}else{
        				IData svcOfferRelInfo = svcOfferRelInfos.getData(0);
        				if("P".equals(svcOfferRelInfo.getString("OFFER_TYPE",""))){
        					groupId = svcOfferRelInfo.getString("GROUP_ID","-1");
                			offerCode = svcOfferRelInfo.getString("OFFER_CODE","");
        				}else if("K".equals(svcOfferRelInfo.getString("OFFER_TYPE",""))){
        					 String offerInsId = svcOfferRelInfo.getString("OFFER_INS_ID");
                             IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByInstId(offerInsId);
                             if(IDataUtil.isNotEmpty(userSaleActiveInfos)){
                            	 offerCode = userSaleActiveInfos.getData(0).getString("PRODUCT_ID");
                            	 groupId = svcOfferRelInfo.getString("OFFER_CODE","");
                             }else{
                            	 offerCode = "-1";
                            	 groupId = "-1";
                             }
                             forceOfferType = svcOfferRelInfo.getString("OFFER_TYPE","");
        				}
        			}
        		}
        		userSvc.put("PACKAGE_ID", groupId);
    			userSvc.put("PRODUCT_ID", offerCode);
    			userSvc.put("PRODUCT_NAME", productName);
    			userSvc.put("OFFER_TYPE", forceOfferType);
    			OfferCfg svcCfg = OfferCfg.getInstance(userSvc.getString("ELEMENT_ID",""), BofConst.ELEMENT_TYPE_CODE_SVC);
    			if(svcCfg == null){
    				continue;
    			}
    			userSvc.put("ELEMENT_NAME", svcCfg.getOfferName());
    			String forceProductId = "";
    			IData elementInfo = null;
        		if("-1".equals(offerCode)){//如果回填的product_id为-1，则查询主产品ID下ELEMENT_ID的FORCE_TAG
        			forceProductId = productId;
        			elementInfo = ProductElementsCache.getElement(forceProductId, userSvc.getString("ELEMENT_ID",""), BofConst.ELEMENT_TYPE_CODE_SVC);
        		}else{//否则查询回填的product_id下ELEMENT_ID的FORCE_TAG
        			if(BofConst.ELEMENT_TYPE_CODE_PACKAGE.equals(forceOfferType)){//如果该元素是营销活动产品下的元素，则查询该营销包下的元素的FORCE_TAG
        				forceProductId = groupId;
        				elementInfo = PackageElementsCache.getElement(forceProductId, userSvc.getString("ELEMENT_ID",""), BofConst.ELEMENT_TYPE_CODE_SVC);
        			}else{//否则查询回填的product_id下元素的FORCE_TAG
        				forceProductId = offerCode;
        				elementInfo = ProductElementsCache.getElement(forceProductId, userSvc.getString("ELEMENT_ID",""), BofConst.ELEMENT_TYPE_CODE_SVC);
        			}
        		}
        		if(IDataUtil.isNotEmpty(elementInfo)){
        			userSvc.put("FORCE_TAG", elementInfo.getString("ELEMENT_FORCE_TAG",""));
        		}else{
        			userSvc.put("FORCE_TAG", "1");// 没有在产品模型下配置的不让删除
        		}
    			userNormalSvcs.add(userSvc);
        	}
        }
        return userNormalSvcs;
    }

    /**
     * 根据营销活动办理进来的service_id查询用户是否存在跟绑定的服务重复的服务
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryUserServiceByServiceId(IData param) throws Exception
    {

        // TODO code_code表里没有
        IDataset userServiceInfo = Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVC_BY_SERVICEID", param);
        return userServiceInfo;
    }

    /**
     * @Function: queryUserServices
     * @Description: 获取用户所有服务
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:13:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserServices(String user_id) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("USER_ID", user_id);
        // TODO inParams.put("EPARCHY_CODE", getVisit().getRouteEparchyCode());
        IDataset resultset=  Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_ALL2", inParams);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++)
        {
            IData result = resultset.getData(i);
            result.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(result.getString("SERVICE_ID")));
        }
        return resultset;
    }

    public static IDataset queryUserSpecSvc(String userId, String prouctId) throws Exception
    {

        IDataset userSvcs = new DatasetList();

        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("PRODUCT_ID", prouctId);

        IDataset tempUserSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SPEC_SVC_INTF_HAIN", params);

        for (int i = 0; i < tempUserSvcs.size(); i++)
        {
            IData svc = tempUserSvcs.getData(i);
            String elementId = svc.getString("ELEMENT_ID");
            IDataset elemConfigs = PkgElemInfoQry.getProductElementConfig(prouctId, elementId);

            if (IDataUtil.isEmpty(elemConfigs))
            {
                svc.put("FORCE_TAG", "1");// 没有在产品模型下配置的不让删除
            }
            else
            {
                String force_tag = elemConfigs.getData(0).getString("FORCE_TAG");
                svc.put("FORCE_TAG", force_tag);
            }
            userSvcs.add(svc);
        }
        return userSvcs;
    }
    
    

    /**
     * 查询用户已有服务，用于产品变更界面展示
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static IDataset queryUserSvc(String userId) throws Exception
    {

        IData param = new DataMap();

        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_SVC", "USER_SVC_SEL", param);
    }

    public static IDataset queryUserSvcByUserId(String userId, String xGetMode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset userSvc = new DatasetList();
        if ("1".equals(xGetMode))
        {// 获取用户有效服务数据
            userSvc = Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSERV_NORMAL", param); // 获取用户有效服务数据
        }
        else if ("2".equals(xGetMode))
        {// 获取用户无效服务数据
            userSvc = Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSERV_CLOSE", param); // 获取用户无效服务数据
        }
        else if ("3".equals(xGetMode))
        {// 获取用户所有状态的服务数据
            userSvc = Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSERV_ALL", param); // 获取用户所有状态的服务数据
        }

        return userSvc;
    }

    /**
     * @param userId
     * @param serviceId
     * @param databaseName
     * @return
     * @throws Exception
     *             wangjx 2013-5-29
     */
    public static IDataset queryUserSvcByUserId(String userId, String serviceId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_AFTER_TRADE", param, routeId);
    }

    public static IDataset queryUserSvcByUserId(String userId, String productId, String tagChar, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("RSRV_STR9", tagChar);// 0,全部；1,自有业务，2,梦网业务

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_PLATORDERINFO_BY_SN11", param);
    }

    /**
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     *             chenyi 2013-7-27
     */
    public static IDataset queryUserSvcByUserIdAll(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_ALL", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, param, null);
        
        return dataset;
    }
    
    public static IDataset queryUserSvcByUserIdAll1(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_ALL", param);
        IDataset userPlats = new DatasetList();
        //通过user_id查出
        for(int i=0;i<dataset.size();i++){
            IData result = dataset.getData(i);
            String serviceId = result.getString("SERVICE_ID");
            String offerType = "S";
            IData name = UpcCall.queryOfferByOfferId(offerType, serviceId);
            String serviceName = name.getString("OFFER_NAME");
            result.put("SERVICE_NAME", serviceName);
            userPlats.add(result);
        }
        return userPlats;
    }

    public static IDataset queryUserSvcByUserIdAndInstId(IData iData) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_INSTID", iData);
    }

    public static IDataset queryUserSvcByUserIdAndRelaTradeId(String userId, String tradeId, String campnId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELATION_TRADE_ID", tradeId);
        param.put("CAMPN_ID", campnId);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_SALEACTIVE", param);
    }

    /**
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     *             wangjx 2013-5-29
     */
    public static IDataset queryUserSvcByUserIdEparchyCode(String userId, String eparchyCode, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID", param, routeId);
    }

    public static IDataset queryUserSvcByUseridSvcid(String USER_ID, String SERVICE_ID) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", USER_ID);
        cond.put("SERVICE_ID", SERVICE_ID);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_SVC_ID", cond);
    }

    /**
     * @Function: queryUserSvcForTime
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:14:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserSvcForTime(String userId, String serviceId, String paraCode13, String paraCode14) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("SERVICE_ID", serviceId);
        cond.put("PARA_CODE13", paraCode13);
        cond.put("PARA_CODE14", paraCode14);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_BY_STARTDATE", cond);
    }

    public static IDataset queryUserSvcInfoByUserId(String productId, String userId, String rsrvStr9, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        // param.put("SERV_MODE", sTagChar);// 0,全部；1,自有业务，2,梦网业务
        param.put("RSRV_STR9", rsrvStr9);// 0,全部；1,自有业务，2,梦网业务

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_PLATORDERINFO_BY_SN01_NEW", param);
    }

    /**
     * 返回自建宽带服务
     * 
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */
    public static IDataset queryUserSvcsBroadband(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_BROADBAND", param);
    }

    /**
     * @Function: queryUserSvcsInSelectedElements
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:15:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserSvcsInSelectedElements(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_IN_SELECTED", param);
    }

    /**
     * @Function: queryUserSvcsInSelectedElements
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:15:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset queryUserSvcsInSelectedElements(String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_IN_SELECTED", param, eparchyCode);
    }

    public static void updateUserSvcByUserIdProductId(String userId, String product_id) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", product_id);
        Dao.executeUpdateByCodeCode("TF_F_USER_SVC", "UPDATE_BY_USERID_PRODUCTID", param);
    }

    public static void updateUserSvcByUserIdServiceId(String userId, String serviceId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);
        Dao.executeUpdateByCodeCode("TF_F_USER_SVC", "UPDATE_BY_USERID_SERVICEID", param);
    }
    
    
    public static IDataset queryUserSvcByUseridDate(String userId, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TIME_POINT", timePoint);
        IDataset iDataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_UID_DATE", param);
        
        FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(iDataset, param, null);
        
        return iDataset;
    }
    
    public static IDataset getUserSvcsByLimitNpWithStarLevel(String userId, String eparchyCode, String starLevel) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId); // 用户标识
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("STAR_LEVEL", starLevel);

        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_LIMIT_NP_STAR_LEVEL", params);
    }
    
    
    public static IDataset checkInternetTvWide(String wSerialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", wSerialNumber);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_INTERNETTV", param);
    }
    
    /**
     * 查询用户已开的服务信息
     * 
     * @param user_id
     * @param user_id_a
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcByUserIdAllNew(String user_id) throws Exception
    {

        IData iData = new DataMap();
        iData.put("USER_ID", user_id);

        SQLParser svcparser = new SQLParser(iData);

        svcparser.addSQL(" SELECT PARTITION_ID,to_char(USER_ID) USER_ID,INST_ID,SERVICE_ID,MAIN_TAG,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        svcparser.addSQL("   FROM tf_f_user_svc ");                                                                                                                                            
        svcparser.addSQL("  WHERE user_id = TO_NUMBER(:USER_ID) ");                                                                                                                    
        svcparser.addSQL("    AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000) ");                                                                                                   
        svcparser.addSQL("    AND Sysdate Between Start_Date And trunc(last_day(END_DATE)+1)- 1/24/60/60 ");                                                                                   
        svcparser.addSQL("  ORDER BY end_date, start_date ");

        IDataset resultset = Dao.qryByParse(svcparser);
        
        //本次改造,调用产商品接口查询服务名称,duhj 2017/03/06
        if(IDataUtil.isNotEmpty(resultset)){
        	for(int i=0;i<resultset.size();i++){
        		String serviceId=resultset.getData(i).getString("SERVICE_ID");
        		String serviceName=USvcInfoQry.getSvcNameBySvcId(serviceId);
        		resultset.getData(i).put("SERVICE_NAME", serviceName);
        	}
        }
        
        return resultset;
    }
    
    public static IDataset queryUserAllSvcForAbility(String userId) throws Exception
	 {
	        IData cond = new DataMap();
	        cond.put("USER_ID", userId);
	        return Dao.qryByCode("TF_F_USER_SVC", "SEL_ALL_USERSVC_FOR_ABILITY", cond);
	  }
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcInstancePf(String userId) throws Exception
	{
    	IData cond = new DataMap();
	    cond.put("USER_ID", userId);
	    return Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVC_INSTANCE_BY_USERID", cond);
	}
    
    /**
     * 根据userID查询所有服务
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcByUserId(String userId) throws Exception
	{
    	IData cond = new DataMap();
	    cond.put("USER_ID", userId);
	    SQLParser svcparser = new SQLParser(cond);

        svcparser.addSQL(" SELECT * ");
        svcparser.addSQL("   FROM tf_f_user_svc ");                                                                                                                                            
        svcparser.addSQL("  WHERE user_id = TO_NUMBER(:USER_ID) ");                                                                                                                    
        svcparser.addSQL("    AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000) ");                                                                                                   
        svcparser.addSQL("    AND Sysdate Between Start_Date And end_date ");                                                                                   
        svcparser.addSQL("  ORDER BY end_date, start_date ");
        IDataset resultset = Dao.qryByParse(svcparser);
	    return resultset;
	}
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcPolicyByUserId(String userId) throws Exception
	{
    	IData cond = new DataMap();
	    cond.put("USER_ID", userId);
	    return Dao.qryByCode("TF_F_USER_SVC", "SEL_USERSVC_POLICY_BY_USERID", cond);
	}
    
    /**
	 * @description 查找用户失效的服务
	 * @param @param userId
	 * @param @param serviceId
	 * @return IDataset 
	 * @author tanzheng
	 * @throws Exception 
	 * @date 2019年4月17日
	 */
	public static IDataset getExpireSvcUserId(String userId, String serviceId) throws Exception {
		 IData param = new DataMap();
	        param.put("USER_ID", userId);
	        param.put("SERVICE_ID", serviceId);
	        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_SVC_EXPIRE", param);
	}
	
	/**
     * 
     * @param userId
     * @param serviceId
     * @param instId
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcByUserIdAndInstId(String userId,String serviceId,String instId) throws Exception
	{
    	IData cond = new DataMap();
	    cond.put("USER_ID", userId);
	    cond.put("SERVICE_ID", serviceId);
	    cond.put("INST_ID", instId);
	    IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_ID_BY_SVCINSTID", cond);
	    FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userSvcs, cond, null);
	    return userSvcs;
	}
    
    /**
     * 查询机器卡绑定的服务属性
     * @param userId
     * @param serviceId
     * @param attrCode
     * @param paramCode1
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvcAttrByUserIdAttrCode(String userId, String serviceId,String attrCode,String paramCode1) throws Exception {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("SERVICE_ID", serviceId);
    	param.put("ATTR_CODE", attrCode);
    	param.put("PARA_CODE1", paramCode1);
    	return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_ID_FOR_JKBIND", param);
	}


    public static IDataset getUserSvcForByUserIdA(String user_id, String user_id_a,String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        param.put("SERVICE_ID", service_id);
        // TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_AND_USERID_USERIDA", param);
    }

    /**
     * 查询集团共享流量服务
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpWlwShareUserSvcByUserIdA(String userIdA) throws Exception {
    	IData param = new DataMap();
    	param.put("USER_ID", userIdA);
    	return Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_IDA_FOR_WLWSHARE", param);
	}
    
}
 