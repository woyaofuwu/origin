
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class UserGrpInfoQry
{

    /**
     * @Function: checkGroupUserInfo
     * @Description: 根据服务号码获取用户信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:45:19 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset checkGroupUserInfo(String serial_number, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select user_id,cust_id,product_id from tf_f_user  ");
        parser.addSQL("  where 1 = 1 ");
        parser.addSQL("    and serial_number = :SERIAL_NUMBER ");
        parser.addSQL("    and remove_tag='0' ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * @Description:一级BBOSS业务产品订购状态查询客户用户信息
     * @author wanglq
     * @date 2017-02-28
     */
    public static IDataset qryCustUserInfo(String serialNumber, String custName, String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("GROUP_ID", groupId);
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  ");
        parser.addSQL("F.CITY_CODE, ");
        parser.addSQL("F.CUST_NAME, ");
        parser.addSQL("F.GROUP_ID, ");
        parser.addSQL("U.USER_ID, ");
        parser.addSQL("U.CUST_ID, ");
        parser.addSQL("U.SERIAL_NUMBER ");
        parser.addSQL("FROM TF_F_CUST_GROUP F, TF_F_USER U ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND F.GROUP_ID = :GROUP_ID ");
        parser.addSQL("AND F.CUST_ID = U.CUST_ID ");
        parser.addSQL("AND F.CUST_NAME LIKE '%' || :CUST_NAME || '%' ");
        parser.addSQL("AND U.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND F.REMOVE_TAG = '0' ");
        parser.addSQL("AND U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");

        return Dao.qryByParse(parser);
    }
    
    
    /**
     * @Function: getDumpIdByajax
     * @Description: 判断ADC的业务接入号是否重复(天津)
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:46:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getDumpIdByajax(String strBizInCode) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("BIZ_IN_CODE", strBizInCode);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_BIZINCODE", inparam, Route.CONN_CRM_CG);
        if (result.size() > 0)
        {
            return result;
        }
        // 20090608 复从台帐表判断唯一
        IDataset result1 = Dao.qryByCode("TF_B_TRADE_GRP_PLATSVC", "SEL_BY_BIZINCODE", inparam, Route.getJourDb());
        if (result1.size() > 0)
        {
            DataHelper.sort(result1, "TRADE_ID", IDataset.TYPE_DOUBLE, IDataset.ORDER_DESCEND); // soresult1.sort("TRADE_ID",
            // IDataset.TYPE_DOUBLE,
            // IDataset.ORDER_DESCEND);
            if ("1".equals(result1.getData(0).get("MODIFY_TAG")))
            {
                return new DatasetList();
            }
        }
        return result1;
    }

    /**
     * @Function: getDumpIdByajax
     * @Description: 判断ADC的业务接入号是否重复
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:47:22 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getDumpIdByajax(String strBizInCode, String group_id) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("BIZ_IN_CODE", strBizInCode);
        inparam.put("GROUP_ID", group_id);
        IDataset result = Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_BIZINCODE", inparam, Route.CONN_CRM_CG);
        if (result.size() > 0)
        {
            return result;
        }
        // 20090608 复从台帐表判断唯一
        IDataset result1 = Dao.qryByCode("TF_B_TRADE_GRP_PLATSVC", "SEL_BY_BIZINCODE", inparam, Route.getJourDb(Route.CONN_CRM_CG));
        return result1;
    }

    public static IDataset getGrpAcctUserInfo(String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_3", param);
    }

    public static IDataset getGrpAcctUserInfoTotalNum(String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "CNT_BY_GRPID_3", param);
    }

    public static IDataset getGrpEparchyCodeByGId(String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_EPARCHYCODE_BY_GID", param);
    }

    public static IDataset getGrpMebProOrderByGIdSN(String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_SN", param);
    }

    public static IDataset getGrpMebProOrderByGIdSN(String serialNumber, String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_SN", param);
    }

    public static IDataset getGrpPubUserInfo(String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_0", param);
    }

    /**
     * todo getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);怎么处理 根据SERIAL_NUMBER查是否已经存在该用户了
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean getIsExistSerialNumber(IData param) throws Exception
    {

        // TODO getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select COUNT(*) COUNTS from tf_f_user a ");
        parser.addSQL("where 1=1 ");
        parser.addSQL("and a.serial_number=:SERIAL_NUMBER ");
        parser.addSQL("and rownum<2 ");
        IDataset temp_set = Dao.qryByParse(parser);
        String is_exist = "0";
        if (temp_set != null && temp_set.size() > 0)
        {
            is_exist = temp_set.getData(0).getString("COUNTS");
        }

        if (Integer.parseInt(is_exist) > 0)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    /**
     * todo getVisit().setRouteEparchyCode(usereaprchy_code);怎么处理 查询手机号码查询成员user信息
     * 
     * @param serialNumber
     *            成员手机号码
     * @return IDataset 成员用户信息
     * @author xiajj
     * @throws Throwable
     */
    public static IDataset getMemberUserInfoBySn(String serialNumber) throws Exception
    {

        // 查询手机号码归属地
        String usereaprchy_code = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);

        if (StringUtils.isNotBlank(usereaprchy_code))
        {
            IDataset userInfos = IDataUtil.idToIds(UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, usereaprchy_code));
            return userInfos;
        }

        return new DatasetList();
    }

    /*
     * 根据biz_code serv_code,group_id查询集团订购的adcmas业务 add by jiudian 20120208
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
        IDataset result1 = Dao.qryByCode("TF_B_TRADE_GRP_PLATSVC", "SEL_BY_BIZINCODE_BIZCODE_GID", inparam, Route.getJourDb(Route.CONN_CRM_CG));
        return result1;
    }

    /**
     * todo code_code 表里没有SEL_RELAY_NUMBER_BY_STR19
     * 
     * @Function: getRelaynumberByajax
     * @Description: 判断ADC的业务接入号是否重复
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:51:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getRelaynumberByajax(String relaynumber) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("RSRV_STR19", relaynumber);
        IDataset result = Dao.qryByCode("TF_F_USER_OTHER", "SEL_RELAY_NUMBER_BY_STR19", inparam, Route.CONN_CRM_CG);
        if (result.size() > 0)
        {
            return result;
        }
        // 20090608 复从台帐表判断唯一
        IDataset result1 = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_RELAY_NUMBER_BY_STR19", inparam, Route.getJourDb());
        return result1;
    }

    public static IDataset getUsrMem(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  u.serial_number,  up.product_id, p.product_name, to_char(uu.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date  ");
        parser.addSQL("   FROM tf_f_user u, tf_f_relation_uu uu, td_b_product p ,tf_f_user_product up");
        parser.addSQL("  WHERE 1 = 1  ");
        parser.addSQL("    AND u.user_id = uu.user_id_b ");
        parser.addSQL("    AND u.user_id = up.user_id ");
        parser.addSQL("    AND up.product_id = p.product_id ");
        parser.addSQL("    AND u.partition_id = mod(u.user_id,10000) ");
        parser.addSQL("    AND sysdate between uu.start_date and uu.end_date ");
        parser.addSQL("    AND sysdate between p.start_date and p.end_date ");
        parser.addSQL("    AND u.remove_tag = '0' ");
        parser.addSQL("    AND uu.user_id_a = :USER_ID_A ");
        return Dao.qryByParse(parser);
    }

    public static IDataset qryGrpExitsByGIdAndPId(String groupId, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRPEXITS_BY_GID_PID", param);
    }

    public static IDataset qryGrpMebInfo(String groupId) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_2", param);
    }

    public static IDataset qryTabSqlFromAllDb(String serial_number, String remove_tag) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serial_number);
        data.put("REMOVE_TAG", remove_tag);
        return Dao.qryByCodeAllCrm("TF_F_USER", "SEL_BY_SNO", data, true);
    }

    public static IDataset qryVpnMemShrtCod(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT b.short_code short_code ");
        parser.addSQL(" FROM tf_f_user_vpn v,tf_f_user_vpn_meb b ");
        parser.addSQL(" WHERE v.user_id = b.user_id_a  ");
        parser.addSQL("       AND v.vpn_no= :VPN_NO ");
        parser.addSQL("       AND b.serial_number = :SERIAL_NUMBER_B ");
        parser.addSQL("       AND b.remove_tag='0' ");

        return Dao.qryByParse(parser);

    }

    /**
     * @author chenkh
     * @param groupCustCode
     * @return
     * @throws Exception
     */
    public static IDataset queryCustGrpByGID(String groupCustCode) throws Exception
    {
        IData param = new DataMap();
        param.put("MP_GROUP_CUST_CODE", groupCustCode);
        IDataset dataset = new DatasetList();
        dataset = Dao.qryByCode("TF_F_CUST_GROUP", "SEL_ALL_BY_GID", param, Route.CONN_CRM_CG);

        return dataset;
    }

    /**
     * @Function: queryGroupRelation
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午3:10:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset queryGroupRelation(String relation_type, String rela_cust_id, String cust_id) throws Exception
    {

        IData param = new DataMap();
        param.put("RELATION_TYPE", relation_type);
        param.put("RELA_CUST_ID", rela_cust_id);
        param.put("CUST_ID", cust_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select 1 FROM TF_F_CUST_GROUP_RELATION ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND RELATION_TYPE = :RELATION_TYPE ");
        parser.addSQL(" and RELA_CUST_ID = :RELA_CUST_ID");
        parser.addSQL(" and CUST_ID = :CUST_ID");
        return Dao.qryByParse(parser);
    }

    /**
     * @Function: queryGroupRelationUUS
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午3:11:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset queryGroupRelationUUS(String user_id_a, String user_id_b, String relation_type_code, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID_B", user_id_b);
        param.put("USER_ID_A", user_id_a);
        param.put("RELATION_TYPE_CODE", relation_type_code);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT u.user_id_a,u.serial_number_a,u.user_id_b,u.serial_number_b,u.relation_type_code,p.GROUP_ID PAR_GROUP_ID,s.GROUP_ID SUB_GROUP_ID, ");
        parser.addSQL(" u.start_date,u.end_date,u.UPDATE_STAFF_ID,u.UPDATE_DEPART_ID,u.UPDATE_TIME ");
        parser.addSQL(" FROM tf_f_relation_uu u,tf_f_cust_group p ,tf_f_user pu,tf_f_cust_group s,tf_f_user su ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND u.USER_ID_B = :USER_ID_B ");
        parser.addSQL(" AND u.USER_ID_A = :USER_ID_A");
        parser.addSQL(" AND u.PARTITION_ID =MOD( :USER_ID_B,10000)");
        parser.addSQL(" AND u.RELATION_TYPE_CODE = :RELATION_TYPE_CODE");
        parser.addSQL(" AND u.END_DATE >SYSDATE ");
        parser.addSQL(" AND su.USER_ID = u.USER_ID_B ");
        parser.addSQL(" AND su.PARTITION_ID =MOD( :USER_ID_B,10000)");
        parser.addSQL(" AND pu.USER_ID = u.USER_ID_A ");
        parser.addSQL(" AND pu.PARTITION_ID =MOD( :USER_ID_A,10000)");
        parser.addSQL(" AND pu.CUST_ID =p.CUST_ID ");
        parser.addSQL(" AND su.CUST_ID =s.CUST_ID ");
        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryGrpCenPayInfo(String userID) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userID);

        return Dao.qryByCode("TF_F_USER_GRP_CENPAY", "SEL_ALL_BY_USERID", param);
    }
    
    public static IDataset queryGrpCenPayInfo2(String userID) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userID);

        return Dao.qryByCode("TF_F_USER_GRP_CENPAY", "SEL_ALL_BY_USERID2", param);
    }

    public static IDataset queryMebCenPayInfo(String userID,String productOfferId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userID);
        param.put("PRODUCT_OFFER_ID",productOfferId);

        return Dao.qryByCode("TF_F_USER_MEB_CENPAY", "SEL_ALL_BY_USERID", param);
    }

    public static IDataset querySvcInfoByUserIdAndSvcId(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_USERID_SVCID", param);
    }

    public static IDataset querySvcInfoByUserIdAndSvcIdPf(String userId, String serviceId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TF_F_USER_GRP_PLATSVC", "SEL_GRPPLATSVC_BY_USERID_SVCID_PF", param);
    }

    public static IDataset queryMebCenPayInfoByUserIdAll(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_MEB_CENPAY", "SEL_ALL_BY_USERID_OFFERID", param);
    }
    
    public static IDataset queryMebCenPayDiscntCountByUserId(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_MEB_CENPAY", "SEL_GFFFMEM_DISCOUNT_BY_USERID", param);
    }
    
    public static IDataset queryAllMebCenPayInfoByUserId(IData param, Pagination page) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_MEB_CENPAY", "SEL_ALL_MEBCENPAY_BY_USERID", param ,page);
    }
    /**
     * 查询集团是否订购流量卡产品 
     * <p>Title: qryGrpByGIdAndPId</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param groupId
     * @param productId
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2017-1-4 下午05:49:08
     */
     public static IDataset qryGrpByGIdAndPId(IData param) throws Exception
     {
         return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRP_BY_GID_PID", param);
     }
     
     /**
      * 根据卡号查询修改优惠台账USER_ID_A
      * <p>Title: qryGrpUserIDAByCard</p>
      * <p>Description: </p>
      * <p>Company: AsiaInfo</p>
      * @param param
      * @return
      * @throws Exception
      * @author XUYT
      * @date 2017-1-10 下午04:22:10
      */
     public static IDataset qryGrpUserIDAByCard(IData param) throws Exception
     {
         return Dao.qryByCode("TL_B_VALUECARD_DETAILED", "SEL_GRP_BY_CARD_NUMBER", param);
     } 
     
     /**
      * @Description:根据服务代码查询是否在表中已经存在
      * @author songxw
      * @date 2017-11-13
      */
     public static IDataset getServCodeByajax(IData param) throws Exception
     {
         SQLParser parser = new SQLParser(param);

         parser.addSQL("SELECT  ");
         parser.addSQL("A.SERV_CODE ");
         parser.addSQL("FROM UCR_CRM1.TF_F_USER_GRP_PLATSVC A ");
         parser.addSQL("WHERE 1 = 1 ");
         parser.addSQL("AND A.SERV_CODE = :SERV_CODE ");
         parser.addSQL("UNION ");
         parser.addSQL("SELECT  ");
         parser.addSQL("B.SERV_CODE ");
         parser.addSQL("FROM UCR_CRM1.TF_B_TRADE_GRP_PLATSVC B ");
         parser.addSQL("WHERE 1 = 1 ");
         parser.addSQL("AND B.SERV_CODE = :SERV_CODE ");

         return Dao.qryByParse(parser);
     }
    
}
