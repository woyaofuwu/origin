
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserVpnInfoQry
{

    /**
     * @Function: checkShortQuery
     * @Description: 检查号码是不是VPN集团用户
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:30:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset checkShortQuery(String serial_number) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT COUNT(*) as NUM FROM tf_f_user_vpn  t,tf_f_relation_uu u ");
        parser.addSQL("  WHERE t.user_id=u.user_id_a ");
        parser.addSQL("  AND relation_type_code='20' ");
        parser.addSQL("  AND u.serial_number_b=:SERIAL_NUMBER");
        parser.addSQL(" AND SYSDATE BETWEEN u.start_date AND u.end_date ");
        return Dao.qryByParse(parser);
    }

    /**
     * @Function: getGrpOutInfo
     * @Description: 往外号码组查询
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:30:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getGrpOutInfo(String user_id, String eparchyCode) throws Exception
    {

        // getVisit().setRouteEparchyCode( eparchyCode);
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_VPMN_GRPOUT", "SEL_OUTGROUP_BY_USERID", param, eparchyCode);
    }

    /**
     * todo getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);怎么处理 根据VPN_NO检查查是否已经存在该用户了
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean getIsExistVpnNo(IData param) throws Exception
    {

        // TODO getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select COUNT(*) COUNTS from tf_f_user_vpn a ");
        parser.addSQL("where 1=1 ");
        parser.addSQL("and a.vpn_no=:VPN_NO ");
        parser.addSQL("and rownum<2 ");
        IDataset temp_set = Dao.qryByParse(parser);
        String is_exist = "0";
        if (IDataUtil.isNotEmpty(temp_set))
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
     * @author chenyi
     * @param params
     * @return
     * @throws Exception
     */
    public static IData getMemberVpnByUserId(IData params) throws Exception
    {

        String eparchyCode = params.getString("EPARCHY_CODE");
        String user_id = params.getString("USER_ID");
        String user_id_a = params.getString("USER_ID_A");
        return getMemberVpnByUserId(user_id, user_id_a, eparchyCode);

    }

    /**
     * getMemberVpnByUserId
     * 
     * @param userId
     * @return
     * @throws Exception
     */

    public static IDataset getMemberVpnByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_VPN_MEB", "SEL_BY_USER_ID", param);
    }

    /**
     * @Function: getMemberVpnByUserId
     * @Description: 作用：根据user_id查询对应的成员实例化资料信息 对于vpmn业务直接查表TF_F_USER_VPN_MEB 得到用户个性化数据
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:32:28 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IData getMemberVpnByUserId(String user_id, String user_id_a, String eparchyCode) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("USER_ID_A", user_id_a);
        // TODO getVisit().setRouteEparchyCode(eparchyCode);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_VPN_MEB", "SEL_BY_USERID", param, eparchyCode);
        if (IDataUtil.isNotEmpty(userattrs))
        {
            return userattrs.getData(0);
        }
        return new DataMap();
    }

    /**
     * getMemberVpnByUserID
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getMemberVpnByUserID(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_VPN_MEB", "SEL_BY_USERID1", param);
    }

    /**
     * 作用：根据user_id查询对应的VPMN集团用户的短号长度，用于判断 UserDom::USER_VPN::TF_F_USER_VPN::SEL_SHORTCODELEN_BY_USERID
     * 
     * @author liaoyi
     * @param userId
     * @return String 短号长度
     * @throws Exception
     */
    public static int getShortCodeLenByUserId(String userId) throws Exception
    {

        IData idata = new DataMap();
        idata.put("USER_ID", userId);
        IDataset userattrs = Dao.qryByCode("TF_F_USER_VPN", "SEL_SHORTCODELEN_BY_USERID", idata, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(userattrs))
        {
            return userattrs.getData(0).getInt("SHORT_CODE_LEN", 0);
        }
        return 0;
    }

    /**
     * @Function: getShortQuery
     * @Description: 该电子渠道长短号互查
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:35:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getShortQuery(String serial_number, String sn) throws Exception
    {
        IData idata = new DataMap();
        idata.put("SERIAL_NUMBER", serial_number);
        idata.put("SN", sn);

        SQLParser parser = new SQLParser(idata);
        parser.addSQL("  SELECT  b1.vpn_no VPMN,tt.serial_number_b,tt.short_code,B1.Vpn_Name  ");
        parser.addSQL("   FROM tf_f_relation_uu  tt,  tf_f_user_vpn b1, (");
        parser.addSQL("  SELECT u.user_id_a,u.short_code,u.serial_number_a,b.vpn_no");
        parser.addSQL("   FROM  tf_f_relation_uu u,tf_f_user_vpn b");
        parser.addSQL("  WHERE u.serial_number_b=:SERIAL_NUMBER");
        parser.addSQL("   AND b.user_id=u.user_id_a");
        parser.addSQL(" AND relation_type_code='20' ");
        parser.addSQL("  AND SYSDATE BETWEEN u.start_date AND u.end_date");
        parser.addSQL("   )  t2");
        parser.addSQL("  WHERE relation_type_code='20' ");
        parser.addSQL("  AND (tt.serial_number_b=:SN OR tt.short_code=:SN)");
        parser.addSQL("    AND tt.user_id_a=b1.user_id ");
        parser.addSQL("   AND b1.vpn_no=t2.vpn_no");
        parser.addSQL("    AND SYSDATE BETWEEN tt.start_date AND tt.end_date");
        return Dao.qryByParse(parser);
    }

    /**
     * @Function: getUserVPNInfoByCstId
     * @Description: 根据CustId查询用户vpn信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:37:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset getUserVPNInfoByCstId(String cust_id, Pagination page) throws Exception
    {

        IData idata = new DataMap();
        idata.put("CUST_ID", cust_id);
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_VPN_NO_CENTREX_BY_CUSTID", idata, page);
    }

    /**
     * @Function: getUUandVpnByUerId
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUUandVpnByUerId(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCodeParser("TF_F_USER_VPN", "SEL_BY_USER_ID", param);
    }

    public static IDataset getVpnInfoByUser(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_VPN", "SEL_BY_USERID", param);
    }

    /**
     * @Function: qryCentrexVpmnNOByCustId
     * @Description: 根据集团CUST_ID，查询某集团已订购的Centrex平台下集团产品用户在Centrex平台中的集团标识
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:38:52 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryCentrexVpmnNOByCustId(String cust_id) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_VPNNO_BYCUSTID", param, Route.CONN_CRM_CG);
    }

    public static IDataset qryCriterionVpnInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_CRITERIONVPN_BY_USERID", param, Route.CONN_CRM_CG);
    }

    /**
     * 查询子母VPMN信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryParentUserVpnByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_VPN", "SELALL_BY_USERID_A", param);
    }

    /**
     * 查询集团用户VPN信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset qryUserVpnByUserId(String userId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID", userId);

        IDataset userVpnList = Dao.qryByCode("TF_F_USER_VPN", "SEL_BY_USERID", param, Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(userVpnList))
            return userVpnList;

        for (int i = 0, row = userVpnList.size(); i < row; i++)
        {
            IData userVpnData = userVpnList.getData(i);
            userVpnData.put("SCP_NAME", StaticUtil.getStaticValue("TD_B_SCP", userVpnData.getString("SCP_CODE").trim()));
        }

        return userVpnList;
    }

    public static IDataset qryUserVpnByVpnName(String vpnName) throws Exception
    {
        IData param = new DataMap();
        param.put("VPN_NAME", vpnName);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select v.vpn_no,v.vpn_name,v.user_id from tf_f_user_vpn v ");
        parser.addSQL(" where v.vpn_name like '%'||:VPN_NAME||'%' ");
        parser.addSQL(" and v.remove_tag='0' ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    public static IDataset qryUserVpnInfoBySerialNumber(String serial_number, String accept_date) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("ACCEPT_DATE", accept_date);
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_BY_SERIALNUMBER_ACCEPTDATE", param);
    }

    /**
     * @Function: qryVPMNScpInfo
     * @Description:vpmn scp信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午3:08:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset qryVPMNScpInfo(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT v.vpn_no vpn_no,v.scp_code scp_code");
        parser.addSQL(" FROM   tf_f_user_vpn v ");
        parser.addSQL(" WHERE  v.user_id = TO_NUMBER(:USER_ID)");
        IDataset resIds = Dao.qryByParse(parser);

        return resIds;
    }

    /**
     * @Function: qryVpmnWithoutCentrexByCustId
     * @Description: 根据集团CUST_ID，查询某集团未订购Centrex平台下集团产品，订购的VPMN产品用户
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:40:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryVpmnWithoutCentrexByCustId(String cust_id) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_VPMN_NO_CENTREX_BYCUSTID", param, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团userId和短号查成员vpn信息
     * 
     * @param useridA
     * @param shortCode
     * @return
     * @throws Exception
     */
    public static IDataset qryVpnMemByuserIdaAndShortCode(String useridA, String shortCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", useridA);
        param.put("SHORT_CODE", shortCode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT b.serial_number ");
        parser.addSQL(" FROM  tf_f_user_vpn_meb b ");
        parser.addSQL(" WHERE  b.user_id_a = :USER_ID_A ");
        parser.addSQL(" AND b.SHORT_CODE= :SHORT_CODE ");
        parser.addSQL(" AND b.remove_tag='0' ");
        return Dao.qryByParseAllCrm(parser, true);
    }

    /**
     * @Function: qryVpnMemShrtCod
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:40:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset qryVpnMemShrtCod(String vpn_no, String serial_number_b) throws Exception
    {

        IData param = new DataMap();
        param.put("VPN_NO", vpn_no);
        param.put("SERIAL_NUMBER_B", serial_number_b);

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
     * 根据vpn编码查询有效的vpn信息
     * 
     * @param vpnNo
     * @return
     * @throws Exception
     */
    public static IDataset queryVpnInfoByVpnNo(String vpnNo) throws Exception
    {
        IData param = new DataMap();
        param.put("VPN_NO", vpnNo);
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_VPN_BY_VPNNO", param, Route.CONN_CRM_CG);
    }

    /**
     * @Function: QueryVpnNoByUserIdA
     * @Description:从集团库 根据user_id_a查询VpnNo
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-5-3 下午9:41:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
     */
    public static IDataset QueryVpnNoByUserIdA(String user_id_a) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", user_id_a);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("select v.vpn_no,v.vpn_name from tf_f_user_vpn v ");
        parser.addSQL(" where v.user_id=:USER_ID_A ");
        parser.addSQL(" and v.remove_tag='0' ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    /**
     * 根据CustId查询用户多媒体桌面电话的vpn信息
     * @param custId
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getUserDesktopVPNInfoByCstId(String custId, Pagination page) throws Exception
    {
        IData idata = new DataMap();
        idata.put("CUST_ID", custId);
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_CENTREX_NO_BY_CUSTID", idata, page);
    }
    
}
