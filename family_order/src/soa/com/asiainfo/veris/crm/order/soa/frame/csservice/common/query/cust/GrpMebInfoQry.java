
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.SqlUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPayModeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class GrpMebInfoQry
{
    public static IDataset CheckUserGruMeb(IData param, Pagination page) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT cust_id ,group_id FROM TF_F_CUST_GROUPMEMBER WHERE REMOVE_TAG = '0' ");
        parser.addSQL("  AND USER_ID = TO_NUMBER( :USER_ID) ");
        parser.addSQL("  AND PARTITION_ID = MOD(TO_NUMBER( :USER_ID), 10000) ");
        parser.addSQL("  AND CUST_ID= :CUST_ID ");
        return Dao.qryByParse(parser, page);
    }

    /**
     * 查询groupmember中定位服务号码归属的集团
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getGroupInfoByMember(String user_id, String eparchyCode, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_USERID_2", data, pagination, eparchyCode);
    }

    /**
     * 根据成员userid查出客户管理关系表有效信息
     * 
     * @param user_id
     * @param eparchyCode
     * @return
     * @throws Exception
     */

    public static IDataset getGroupMemberInfoByUserId(String user_id, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_VAILD_BY_USERID", data, eparchyCode);
    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员个人业务信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getGrpMebBusinessByGIdSN(IData idata, Pagination pg) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_SN_2", idata, pg);
    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员个人业务信息总记录数
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getGrpMebBusinessByGIdSNTotalNum(IData idata) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_GROUP", "CNT_BY_GRPID_SN_2", idata);
    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员订购关系
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getGrpMebProOrderByGIdSN(IData idata, Pagination pg) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_SN", idata, pg);
    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员订购关系总记录数
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset getGrpMebProOrderByGIdSNTotalNum(IData idata) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_GROUP", "CNT_BY_GRPID_SN", idata);
    }

    /**
     * 查询集团成员VPMN短号码
     * 
     * @author lishouguo Dec 9, 2011
     * @参数
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset getGrpMebVPMNShortCode(IData params) throws Exception
    {

        IDataset result = Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GRPMEMVPMNSHORTCODE_BY_GID", params, Route.CONN_CRM_CG);
        return result;
    }

    /**
     * 根据集团编号GROUP_ID进行集团成员用户信息查询 add by zhangbs
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGrpMemUserInfoByGrpId(IData data) throws Exception
    {

        // 集团编码
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_GRPID_PK", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据手机号码查询客户成员关系
     * 
     * @author tengg
     * @date 2009-9-21
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getGrpMenberInfo(IData data) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_SERIALNUMBER", data, Route.CONN_CRM_CG);
        IData info = dataset.size() > 0 ? (IData) dataset.get(0) : null;
        return info;
    }

    public static IDataset getGrpMenberInfos(IData data) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_SERIALNUMBER", data, Route.CONN_CRM_CG);
        return dataset;
    }

    /**
     * 根据group_id查询集团成员业务预约信息
     * 
     * @param param
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getGrpPreInfoByGrpId(String group_id, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("GROUP_ID", group_id);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT g.CLASS_ID, ");
        parser.addSQL(" t.GROUP_ID, ");
        parser.addSQL(" t.PRODUCT_ID, ");
        parser.addSQL(" t.CANCEL_TAG, ");
        parser.addSQL(" t.PHONE, ");
        parser.addSQL(" t.PAY_FEE_MODE, ");
        parser.addSQL(" t.ROLEID, ");
        parser.addSQL(" TO_CHAR(t.EXE_DATE,'YYYY-MM-DD HH:MM:SS') EXE_DATE, ");
        parser.addSQL(" t.SERVICE_ID, ");
        parser.addSQL(" t.SHORT_CODE, ");
        parser.addSQL(" t.DISCNT_CODE, ");
        parser.addSQL(" t.REMARK, ");
        parser.addSQL(" t.RSRV_STR1, ");
        parser.addSQL(" t.EPARCHY_CODE ");
        parser.addSQL(" FROM TF_F_PGROUPMEMBER t, TF_F_CUST_GROUP g ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND g.GROUP_ID = :GROUP_ID ");
        parser.addSQL(" AND t.group_id = g.GROUP_ID ");

        IDataset idsRet = Dao.qryByParse(parser, page, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(idsRet))
            return idsRet;
        for (int i = 0; i < idsRet.size(); i++)
        {
            IData map = idsRet.getData(i);
            map.put("CLASS_NAME", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", map.getString("CLASS_ID")));
            map.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(map.getString("PRODUCT_ID")));
            map.put("CANCEL_NAME", StaticUtil.getStaticValue("CANCEL_TAG", map.getString("CANCEL_TAG")));
            map.put("PAY_FEE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(map.getString("PAY_FEE_MODE")));
            map.put("ROLENAME", StaticUtil.getStaticValue("ROLEID", map.getString("ROLEID")));
            map.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(map.getString("SERVICE_ID")));
            map.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("DISCNT_CODE")));
            map.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(map.getString("EPARCHY_CODE")));

        }
        return idsRet;
    }

    /**
     * 集团产品优惠查询
     */
    public static IDataset getProductMemberDiscnt(IData inparams) throws Exception
    {

        return Dao.qryByCode("TD_B_PROD_DISCNT_MEMBER", "SEL_BY_PID_5", inparams);
    }

    /**
     * 根据集团编码查询成员资料信息
     * 
     * @param groupId
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupAllMebList(String groupId, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        return Dao.qryByCodeAllCrm("TF_F_CUST_GROUPMEMBER", "SEL_BY_GROUP_ID", param, true);
    }

    public static IDataset qryGroupInfoByMemberCustId(String memberCustId) throws Exception
    {
        IData param = new DataMap();
        param.put("MEMBER_CUST_ID", memberCustId);
        return Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_MEMBERCUSTID", param);

    }

    /**
     * 根据集团编码查询集团成员信息(存在资料关联信息)
     * 
     * @param groupId
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryGroupMemberInfo(String groupId, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        IDataset retDataset = new DatasetList();

        String[] routeArray = Route.getAllCrmDb();

        for (int i = 0; i < routeArray.length; i++)
        {
            IDataset memberList = Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_GROUP_ID", param, pg, routeArray[i]);

            retDataset.addAll(memberList);
        }

        return retDataset;
    }

    /**
     * 增加显示字段g.city_code fengsl 2013-04-16
     * 
     * @param data
     * @param pagination
     * @param flag
     * @return
     * @throws Exception
     */

    public static IDataset qryGrpMebProductInfo(IData data, Pagination pagination) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER");

        // 先根据服务号码查询用户信息
        IData idUser = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(idUser))
        {
            return new DatasetList();
        }

        String userId = idUser.getString("USER_ID");

        // 返回结果集
        IDataset idsRet = new DatasetList();

        IDataset idsDataset = RelaUUInfoQry.qryRelaAllByUserIdB(userId, pagination);

        if (IDataUtil.isEmpty(idsDataset))
        {
            return idsRet;
        }

        IData udata = null;
        String userIdA = "";

        for (int i = 0, sz = idsDataset.size(); i < sz; i++)
        {
            udata = idsDataset.getData(i);

            userIdA = udata.getString("USER_ID_A");

            // 查集团用户
            IData userGroup = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);

            if (IDataUtil.isEmpty(userGroup))
            {
                continue;
            }

            String groupCustId = userGroup.getString("CUST_ID");

            // 查集团客户
            IData custGroup = UcaInfoQry.qryGrpInfoByCustId(groupCustId);

            if (IDataUtil.isEmpty(custGroup))
            {
                continue;
            }

            // 查集团客户经理
            String custManagerId = custGroup.getString("CUST_MANAGER_ID", "");

            if (StringUtils.isNotEmpty(custManagerId))
            {
                String StaffName = UStaffInfoQry.getStaffNameByStaffId(custManagerId);
                String StaffSerialNumber = UStaffInfoQry.getStaffSnByStaffId(custManagerId);

                udata.put("STAFF_NAME", StaffName);
                udata.put("STAFF_SERIAL_NUMBER", StaffSerialNumber);
            }

            String productId = userGroup.getString("PRODUCT_ID");

            udata.put("PRODUCT_ID", productId);
            udata.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));

            udata.put("CUST_ID", groupCustId);
            udata.put("CUST_NAME", custGroup.getString("CUST_NAME"));
            udata.put("CITY_CODE", userGroup.getString("CITY_CODE"));
            udata.put("USER_ID", userIdA);
            udata.put("SERIAL_NUMBER", userGroup.getString("SERIAL_NUMBER"));
            udata.put("GROUP_ID", custGroup.getString("GROUP_ID"));
            udata.put("OPEN_DATE", userGroup.getString("OPEN_DATE"));
            udata.put("EPARCHY_CODE", userGroup.getString("EPARCHY_CODE"));
            udata.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userGroup.getString("CITY_CODE")));
            // 加到结果集里面
            idsRet.add(udata);
        }

        return idsRet;
    }

    public static String queryGroupMembers(IData indata) throws Exception
    {

        SQLParser parser = new SQLParser(indata);
        parser.addSQL("select count(*) COUNTS from tf_f_cust_groupmember t ");
        parser.addSQL("WHERE t.group_id=:GROUP_ID ");
        parser.addSQL("AND t.remove_tag='0' ");
        IDataset dataset = Dao.qryByParse(parser, "0871");
        String fact_members = "0";
        if (dataset != null && dataset.size() > 0)
        {
            fact_members = (dataset.getData(0)).getString("COUNTS", "0");
        }
        return fact_members;
    }

    /**
     * 原小栏框逻辑，根据USER_ID判断是否集团成员 TODO 看谁倒霉，迁移sql
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PARTITION_ID", userId.substring(12, 16));

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select * from TF_F_CUST_GROUPMEMBER where user_id = :USER_ID and PARTITION_ID = :PARTITION_ID and remove_tag = '0' ");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据服务号码查询成员归属集团信息
     * 
     * @param param
     * @throws Exception
     */
    public static IDataset queryGrpMebBySN(String serial_number) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serial_number);

        return Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_SN", data);
    }

    /**
     * 根据集团编码和联系号码查询集团成员信息
     * 
     * @param groupId
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpMebInfoByGroupIdSN(String groupId, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_GROUPID_SN", param, Route.CONN_CRM_CG);
    }

    public static IData queryMebInfo() throws Throwable
    {

        IData conParams = new DataMap();// AppCtx.getData("cond", true);

        // 查询成员用户信息
        String strMebSn = conParams.getString("SERIAL_NUMBER");

        IData idUserInfo = UcaInfoQry.qryUserMainProdInfoBySn(strMebSn);

        if (idUserInfo == null || idUserInfo.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_472, strMebSn);
        }

        // 是否集团服务号码
        String strGrpSn = idUserInfo.getString("IsGrpSn", "");

        if ("Yes".equals(strGrpSn))
        {
            CSAppException.apperr(GrpException.CRM_GRP_270, strMebSn);
        }

        // 得到当前成员用户的归属地州
        // String strRouteEparchCode = idUserInfo.getString("EPARCHY_CODE");

        // 设置全局路由地州信息
        // getVisit().setRouteEparchyCode(strRouteEparchCode);

        // 查询成员客户信息
        String cust_id = idUserInfo.getString("CUST_ID");

        IData idsCustInfo = UcaInfoQry.qryCustomerInfoByCustId(cust_id);

        if (null == idsCustInfo || idsCustInfo.size() == 0)
        {
            CSAppException.apperr(CustException.CRM_CUST_51, strMebSn);
        }

        return idUserInfo;
    }

    public IDataset qryGrpMemInfo(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.cust_id,f.group_id,a.user_id,a.serial_number,a.month_fee,a.remove_tag, ");
        parser.addSQL("       a.join_date open_date,a.remove_date destroy_date,a.remove_staff_id destroy_staff_id,");
        parser.addSQL("		  a.remove_reason, a.join_staff_id open_staff_id, ");
        parser.addSQL("		  c.rsrv_str1,c.rsrv_str2,c.rsrv_str3,c.rsrv_str4,c.rsrv_str5, ");
        parser.addSQL("		  c.rsrv_num1,c.rsrv_num2,c.rsrv_num3,c.rsrv_num4,c.rsrv_num5,c.rsrv_date1,c.rsrv_date2,c.rsrv_date3, ");
        parser.addSQL("       c.rsrv_tag1,c.rsrv_tag2,	 ");
        parser.addSQL("       a.contact_order,a.cust_name,c.eparchy_code,c.city_code,a.score_value,f.group_type	 ");
        parser.addSQL(" FROM   TF_F_CUST_GROUPMEMBER A, TF_F_CUST_PERSON M,  ");
        parser.addSQL("  	   TF_F_USER C, TF_F_CUSTOMER D, TF_F_CUST_VIP E, TF_F_CUST_GROUP F, ");
        parser.addSQL("        TF_F_MANAGERSTAFF  G,TD_M_DEPART h,TD_M_STAFF I ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.serial_number =:SERIAL_NUMBER ");
        parser.addSQL(" AND D.cust_id = A.member_cust_id  AND D.partition_id=MOD(a.member_cust_id,10000) AND D.remove_tag='0' ");
        parser.addSQL(" AND M.cust_id=A.member_cust_id  AND M.partition_id=MOD(A.member_cust_id,10000) ");
        parser.addSQL(" AND C.user_id = A.user_id       AND C.Partition_Id = MOD(A.user_id,10000) AND C.remove_tag='0' ");
        parser.addSQL(" AND E.usecust_id(+) = A.member_cust_id AND E.REMOVE_TAG(+)='0' ");
        parser.addSQL(" AND A.member_cust_id=C.cust_id ");
        parser.addSQL(" AND A.group_id = F.group_id ");
        parser.addSQL(" AND F.group_type=:GROUP_TYPE");
        parser.addSQL(" AND A.remove_tag = '0'      AND F.remove_tag = '0' ");
        parser.addSQL(" AND G.vip_manager_id(+)=f.cust_manager_id ");
        parser.addSQL(" AND I.staff_id=f.cust_manager_id ");
        parser.addSQL(" AND H.DEPART_ID(+)=I.depart_id  ");
        return Dao.qryByParse(parser);
    }
    
    /**
     * 根据集团客户ID查询集团联系人/管理员手机号码
     * @param groupId
     * @param serialNumber
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2016-12-8
     */
    public static IDataset queryGrpMebManagerAndLinkerByCustId(String custId) throws Exception{
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlUtil.trimSql("SELECT A.USER_ID, A.SERIAL_NUMBER	"));
        sql.append(SqlUtil.trimSql("  FROM TF_F_CUST_GROUPMEMBER A      "));
        sql.append(SqlUtil.trimSql(" WHERE A.CUST_ID = :CUST_ID         "));
        sql.append(SqlUtil.trimSql("   AND A.REMOVE_TAG = '0'           "));
        sql.append(SqlUtil.trimSql("   AND A.MEMBER_KIND IN ('1', '2')  "));
        sql.append(SqlUtil.trimSql("   AND A.RSRV_STR2 IN ('1')  		"));  //标识发送欠费预警短信
        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
}
