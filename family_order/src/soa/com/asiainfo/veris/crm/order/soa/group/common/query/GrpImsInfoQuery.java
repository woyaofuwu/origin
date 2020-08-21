
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class GrpImsInfoQuery
{
    // 融合V网 产品、包 常量
    public static final String VPN_MEB_PRODUCTID = "22000020"; // 成员产品id

    public static final String VPN_SVC_PAKAGE_ID = "33001971"; // 成员产品服务包id

    public static final String VPN_DIS_PAKAGE_ID = "33001972"; // 成员产品服务包id

    public static final String VPN_SVC_ID = "860"; // 成员产品服务id

    public static final String VPN_SVC_SHORT_CODE = "861"; // 成员产品短号服务id

    public static final String VPN_GRP_PRODUCTID = "8001"; // 集团产品id

    // 融合一号通 产品、包 常量
    public static final String YHT_MEB_PRODUCTID = "801611"; // 成员产品id

    public static final String YHT_MEBSVC_PAKAGEID = "80161101"; // 成员产品服务包id

    public static final String YHT_SVC_ID = "80161111"; // 成员产品服务id

    public static final String YHT_GRP_PRODUCTID = "8016"; // 集团产品id

    // 融合总机 产品、包 常量
    public static final String SUPTEL_MEB_PRODUCTID = "613001"; // 成员产品id

    public static final String SUPTEL_MEBSVC_PAKAGEID = "61300101"; // 成员产品服务包id

    public static final String SUPTEL_SVC_ID = "6130"; // 成员产品服务id

    public static final String SUPTEL_GRP_PRODUCTID = "6130"; // 集团产品id

    // 多媒体桌面电话 产品、包、服务常量
    public static final String DESKTEL_SVC_PAKAGEID = "22220001"; // 集团产品服务包id

    public static final String GRP_CNTRX_CORP_CLIP = "81701"; // 集团主叫号码显示

    public static final String GRP_CNTRX_CORP_CLIR = "81702"; // 集团主叫号码显示限制

    public static final String GRP_CNTRX_CORP_CLIR_OUT = "81703"; // 集团主叫号码显示限制

    public static final String DESKTEL_GRP_PRODUCTID = "2222"; // 集团产品id

    public static final String DESKTEL_DIS_PAKAGEID = "33001652"; // 集团主叫号码显示限制

    public static final String DESKTEL_DIS_COMMU_COST = "20000000"; // 桌面电话优惠 资费参数

    public static final String DESKTEL_DIS_DISCOUNT = "20000001"; // 桌面电话优惠 折扣参数

    public static final String DESKTEL_MEB_PRODUCTID = "222201"; // 成员产品id

    public static final String DESKTEL_MEBSVC_PAKAGEID = "10122801"; // 成员产品服务包id

    public static final String CNTRX_BASESVC = "8171"; // CENTREX基本服务

    public static final String HSS_BASESVC = "8172"; // HSS基本服务

    public static final String ENUM_BASESVC = "8173"; // ENUM基本服务

    public static final String NOCOND_TRANSFER = "10122806"; // 无条件呼叫前转

    public static final String BUSY_TRANSFER = "10122807"; // 遇忙呼叫前转

    public static final String CNTRX_MEMB_CFNR_BSV = "10122808"; // 无应答呼叫前转

    public static final String CNTRX_MEMB_CFNL_BSV = "10122809"; // 未注册呼叫前转

    public static final String CALLBARRING = "10122813"; // 呼叫限制

    public static final String HSS_IMPIATTR_IMPI_ID = "81717"; // 漫游权限

    public static final String ALARMCALL = "10122819"; // 叫醒服务

    public static final String CNTRX_NO_DISTURB = "10122820"; // 免打扰

    public static final String SHORT_DIAL = "10122821"; // 缩位拨号

    public static final String CALLWAITING = "10122810"; // 呼叫等待

    public static final String CALLHOLD = "10122811"; // 呼叫保持

    public static final String THTEECALL = "10122812"; // 三方通话

    public static final String BLACKLIST = "10122814"; // 黑名单

    public static final String WHITELIST = "10122815"; // 白名单

    public static final String CNTRX_CORP_CLIP = "10122803"; // 主叫号码显示

    public static final String CNTRX_CORP_CLIR = "10122804"; // 主叫号码显示限制

    public static final String ClosedGroup = "10122817"; // 主叫号码显示限制

    public static final String CNTRX_MEMB_FAX = "10122816"; // 传真业务

    public static final String MEMB_ONENUMBER = "10122822"; // 一号通

    public static final String DifferRinging = "10122818"; // 区别振铃

    /**
     * 校验成员的IMS密码，X_GETMODE：0-密码校验,返回密码校验结果1-取用户信息值
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getUserPassword(IData param) throws Exception
    {
        SQLParser parser1 = new SQLParser(param);

        parser1.addSQL("select impu.ims_password,");
        parser1.addSQL(" uu.role_code_b IS_GRPMANAGER,");
        parser1.addSQL(" u.user_id,");
        parser1.addSQL(" c.cust_id,");
        parser1.addSQL(" c.cust_name,");
        parser1.addSQL(" up.product_id,");
        parser1.addSQL(" up.brand_code,");
        parser1.addSQL(" '融合IMS' brand,");
        parser1.addSQL(" u.open_date,");
        parser1.addSQL(" g.group_id,");
        parser1.addSQL(" g.cust_name group_name,");
        parser1.addSQL(" g.cust_id as GRP_CUST_ID,");
        parser1.addSQL(" v.vpn_no");
        parser1.addSQL(" from tf_f_user_impu impu,");
        parser1.addSQL(" tf_f_user u,");
        parser1.addSQL(" tf_f_relation_uu uu,");
        parser1.addSQL(" tf_f_customer c,");
        parser1.addSQL(" tf_f_user_product up,");
        parser1.addSQL(" tf_f_user u2,");
        parser1.addSQL(" tf_f_cust_group g,");
        parser1.addSQL(" TF_F_USER_VPN v");
        parser1.addSQL("  where impu.user_id = u.user_id");
        parser1.addSQL("  and uu.user_id_b = u.user_id");
        parser1.addSQL("  and uu.user_id_a = v.user_id");
        parser1.addSQL("   and u2.user_id = uu.user_id_a");
        parser1.addSQL("  and c.cust_id = u2.cust_id");
        parser1.addSQL("  and u2.cust_id = g.cust_id");
        parser1.addSQL("  and u2.user_id=up.user_id");
        parser1.addSQL("  and u.serial_number = :SERIAL_NUMBER");
        parser1.addSQL("  and sysdate between uu.start_date and uu.end_date");
        parser1.addSQL("   and (up.product_id in ('6130', '8016', '2222') or");
        parser1.addSQL("     (up.product_id = '8001' and exists");
        parser1.addSQL("      (select 1");
        parser1.addSQL("          from tf_f_user_vpn vpn");
        parser1.addSQL("         where vpn.user_id = u2.user_id");
        parser1.addSQL("           and vpn.vpn_user_code = '2'");
        parser1.addSQL("          and vpn.remove_tag = '0')))");
        IDataset temp = Dao.qryByParse(parser1);
        if(IDataUtil.isEmpty(temp)){
        	return null;
        }
        for(int i=0; i<temp.size(); i++){
        	IData data = temp.getData(i);
        	String productId = data.getString("PRODUCT_ID");
        	data.put("PRODUCT_NAME",  UProductInfoQry.getProductNameByProductId(productId));
        }
        return temp;
    }

    /**
     * 根据集团编号GROUP_ID进行集团订购IMS业务的用户信息查询
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getGrpImsUserInfo(IData param) throws Exception
    {
        SQLParser parser_Group_id = new SQLParser(param);
        parser_Group_id.addSQL("select  to_char(u.user_id) user_id_a,m.product_id,u.serial_number,to_char(u.open_date,'yyyy-mm-dd hh24:mi:ss') open_date,a.acct_id");
        parser_Group_id.addSQL(" from  tf_f_cust_group g, tf_f_user u,tf_a_payrelation a ,tf_f_user_product m ");
        parser_Group_id.addSQL(" where   1 = 1 ");
        parser_Group_id.addSQL(" and g.group_id = :GROUP_ID ");
        parser_Group_id.addSQL(" and g.remove_tag = '0'");
        parser_Group_id.addSQL(" and u.remove_tag = '0'");
        parser_Group_id.addSQL(" and g.cust_id = u.cust_id");
        parser_Group_id.addSQL(" and m.main_tag='1' ");
        parser_Group_id.addSQL(" and u.partition_id = mod(u.user_id,10000)");
        parser_Group_id.addSQL(" and m.product_id in(6130,8016,2222) ");
        parser_Group_id.addSQL(" and u.user_id = m.user_id ");
        parser_Group_id.addSQL(" and m.partition_id = mod(u.user_id,10000)");
        parser_Group_id.addSQL(" and a.user_id = u.user_id ");
        parser_Group_id.addSQL("  and a.partition_id =  mod(u.user_id,10000) ");
        parser_Group_id.addSQL(" and a.default_tag ='1' ");
        parser_Group_id.addSQL(" and a.act_tag='1' ");
        parser_Group_id.addSQL(" and to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN a.start_cycle_id AND a.end_cycle_id ");
        parser_Group_id.addSQL(" union ");
        parser_Group_id.addSQL(" select  to_char(u.user_id) user_id_a,m.product_id,u.serial_number,to_char(u.open_date,'yyyy-mm-dd hh24:mi:ss') open_date,a.acct_id");
        parser_Group_id.addSQL(" from  tf_f_cust_group g, tf_f_user u,tf_f_user_vpn v ,tf_a_payrelation a  ,tf_f_user_product m ");
        parser_Group_id.addSQL(" where   1 = 1");
        parser_Group_id.addSQL(" and g.group_id = :GROUP_ID ");
        parser_Group_id.addSQL(" and g.remove_tag = '0'");
        parser_Group_id.addSQL(" and u.remove_tag = '0'");
        parser_Group_id.addSQL(" and g.cust_id = u.cust_id");
        parser_Group_id.addSQL(" and m.main_tag='1' ");
        parser_Group_id.addSQL(" and u.partition_id = mod(u.user_id,10000)");
        parser_Group_id.addSQL(" and m.product_id =8001");
        parser_Group_id.addSQL(" and u.user_id = m.user_id ");
        parser_Group_id.addSQL(" and m.partition_id = mod(u.user_id,10000)");
        parser_Group_id.addSQL(" and v.user_id=u.user_id");
        parser_Group_id.addSQL(" and a.user_id = u.user_id ");
        parser_Group_id.addSQL("  and a.partition_id =  mod(u.user_id,10000) ");
        parser_Group_id.addSQL(" and a.default_tag ='1' ");
        parser_Group_id.addSQL(" and a.act_tag='1' ");
        parser_Group_id.addSQL(" and to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN a.start_cycle_id AND a.end_cycle_id ");
        // 根据集团GROUP_ID查询集团用户信息总记录数
        return Dao.qryByParse(parser_Group_id);
    }

    public static IDataset qryImsUserDiscnts(IData inparam) throws Exception
    { 
    	SQLParser parser = new SQLParser(inparam);
        parser.addSQL(" SELECT ud.user_id user_id, uo.group_id package_id,uo.offer_code product_id,ud.discnt_code,'1' is_order,to_char(ud.start_date,'yyyy-mm-dd') start_date, to_char(ud.end_date,'yyyy-mm-dd') end_date ");
        parser.addSQL(" FROM tf_f_user_discnt ud,tf_f_user_offer_rel uo WHERE 1=1 ");
        parser.addSQL(" and ud.user_id=uo.rel_user_id  ");
        parser.addSQL(" and ud.discnt_code = uo.rel_offer_code ");
        parser.addSQL(" and uo.rel_offer_type='D' ");
        parser.addSQL(" and ud.partition_id=mod(:USER_ID_B ,10000) ");
        parser.addSQL(" and ud.user_id = :USER_ID_B ");
        parser.addSQL(" and ud.user_id_a = :USER_ID_A ");
        parser.addSQL(" and ud.end_date > last_day(trunc(sysdate))+1-1/24/3600");
        IDataset udInfos = Dao.qryByParse(parser);
        if(IDataUtil.isEmpty(udInfos)){
        	return null;
        }
        
        for(int i=0; i<udInfos.size(); i++){
        	IData data = udInfos.getData(i);
        	String productId = data.getString("PRODUCT_ID");
        	String packageId =  data.getString("PACKAGE_ID");
        	String discntCode = data.getString("DISCNT_CODE"); 
        	IData info = UPackageInfoQry.getPackageByProductIdAndPackageId(productId, packageId);
        	data.put("MIN_NUMBER", info.getString("MIN_NUMBER"));
        	data.put("MAX_NUMBER", info.getString("MAX_NUMBER"));
        	data.put("DEFAULT_TAG", info.getString("DEFAULT_TAG"));
        	data.put("FORCE_TAG", info.getString("FORCE_TAG"));
        	data.put("PACKAGE_NAME", info.getString("PACKAGE_NAME"));
        	IData discnt = UDiscntInfoQry.getDiscntInfoByPk(discntCode);
        	data.put("DISCNT_NAME", discnt.getString("DISCNT_NAME"));
        	data.put("DISCNT_EXPLAIN", discnt.getString("DISCNT_EXPLAIN")); 
        }
        
        return udInfos;
    }

    public static IDataset qryImsMutilSuperTel(IData inparam) throws Exception
    {
        SQLParser parser_supertel = new SQLParser(inparam);

        parser_supertel
                .addSQL(" SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value EXCHANGETELE_SN,rsrv_str1 E_CUST_NAME,rsrv_str2 E_BRAND_CODE,rsrv_str3 E_EPARCHY_CODE,rsrv_str4 E_USER_ID,rsrv_str5 E_CUST_ID,rsrv_str6 MAXWAITINGLENGTH,rsrv_str7 CALLCENTERTYPE,rsrv_str8 CALLCENTERSHOW,rsrv_str9 CORP_REGCODE,rsrv_str10 CORP_DEREGCODE,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark ");
        parser_supertel.addSQL(" FROM tf_f_user_other");
        parser_supertel.addSQL(" WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)");
        parser_supertel.addSQL(" AND user_id=TO_NUMBER(:USER_ID) ");
        parser_supertel.addSQL("AND rsrv_value_code= :RSRV_VALUE_CODE ");
        parser_supertel.addSQL("  AND sysdate BETWEEN start_date+0 AND end_date+0 ");

        return Dao.qryByParse(parser_supertel);
    }

    /**
     * 集团群组查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGroupTeam(IData param) throws Exception
    {
        // 根据集团用户id和组类型 查出该集团用户的组
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select to_char(t.user_id_b) TEAM_ID,v.vpn_no VPN_NO,t.serial_number_b TEAM_NUMBER,");
        parser.addSQL(" CASE t.RELATION_TYPE_CODE   ");
        parser.addSQL("         WHEN 'XH' THEN  T.RSRV_STR5 ");
        parser.addSQL("         WHEN 'DD' THEN t.serial_number_b");
        parser.addSQL("         WHEN 'VB' THEN t.serial_number_b ");
        parser.addSQL("         ELSE '' ");
        parser.addSQL("     END AS TEAM_NUMBER,   ");
        parser.addSQL(" CASE t.RELATION_TYPE_CODE   ");
        parser.addSQL("         WHEN 'XH' THEN '0' ");
        parser.addSQL("         WHEN 'DD' THEN '1'");
        parser.addSQL("         WHEN 'VB' THEN '2'");
        parser.addSQL("         ELSE '' ");
        parser.addSQL("     END AS TEAM_TYPE,   ");
        parser.addSQL("  CASE t.RELATION_TYPE_CODE     ");
        parser.addSQL("         WHEN 'XH' THEN T.RSRV_STR1");
        parser.addSQL("         ELSE '' ");
        parser.addSQL("         END AS HUNTING_TYPE,");
        parser.addSQL("  CASE t.RELATION_TYPE_CODE     ");
        parser.addSQL("         WHEN 'DD' THEN T.RSRV_STR1");
        parser.addSQL("         ELSE ''");
        parser.addSQL("         END AS ACCESS_CODE");
        parser.addSQL(" FROM TF_F_RELATION_UU t,TF_F_USER_VPN v");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL(" and t.user_id_a = to_number(:USER_ID_A) ");
        parser.addSQL(" and t.user_id_a = v.user_id(+)");
        parser.addSQL(" and v.PARTITION_ID=mod(to_number(:USER_ID_A),10000)");
        parser.addSQL(" and t.RELATION_TYPE_CODE = :RELATION_TYPE");
        parser.addSQL(" AND START_DATE<=SYSDATE");
        parser.addSQL(" AND END_DATE>SYSDATE ");

        return Dao.qryByParse(parser);
    }

    /**
     * 集团群组成员的查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getTeamMeb(IData param) throws Exception
    {
        // 根据组id 查出该集团成员的组信息
        SQLParser parser = new SQLParser(param);
        parser.addSQL("  SELECT to_char(b.vpn_no) VPN_NO,");
        parser.addSQL("       to_char(t.user_id_a) TEAM_ID,");
        parser.addSQL("                  CASE b.RELATION_TYPE_CODE");
        parser.addSQL("      WHEN 'XH' THEN  b.RSRV_STR5");
        parser.addSQL("        WHEN 'DD' THEN t.serial_number_a");
        parser.addSQL("         WHEN 'VB' THEN t.serial_number_a");
        parser.addSQL("         ELSE ''         ");
        parser.addSQL("      END AS TEAM_NUMBER,");
        parser.addSQL("      to_char(t.user_id_b) USER_ID,");
        parser.addSQL("      t.serial_number_b SERIAL_NUMBER,");
        parser.addSQL("      to_char(t.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,");
        parser.addSQL("        to_char(t.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date");
        parser.addSQL("   FROM tf_f_relation_uu t,");
        parser.addSQL("     (select t.user_id_b, v.vpn_no, t.rsrv_str5 , t.relation_type_code");
        parser.addSQL("        from tf_f_relation_uu t, tf_f_user_vpn v");
        parser.addSQL("       where t.user_id_b = to_number(:USER_ID_A)");
        parser.addSQL("        and t.relation_type_code in ('VB', 'DD', 'XH')");
        parser.addSQL("        AND t.user_id_a = v.user_id");
        parser.addSQL("         AND v.partition_id = mod(to_number(t.user_id_a), 10000)");
        parser.addSQL("        AND t.start_date < t.end_date");
        parser.addSQL("       AND t.end_date > sysdate) b");
        parser.addSQL("  WHERE t.user_id_a = to_number(:USER_ID_A)");
        parser.addSQL("  AND t.user_id_a = b.user_id_b");
        parser.addSQL(" AND t.relation_type_code =:RELATION_TYPE_CODE");
        parser.addSQL(" AND t.end_date > t.start_date");
        parser.addSQL("  AND t.end_date > sysdate");

        return Dao.qryByParse(parser);
    }

    /**
     * 集团产品成员查询
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryMebLists(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select * from tf_f_user u,tf_f_user_product p where u.user_id=:USER_ID_A and u.remove_tag='0'");
        parser.addSQL(" and u.user_id = p.user_id ");
        parser.addSQL(" and (p.product_id in('6130','8016','2222') or (p.product_id='8001' and exists ");
        parser.addSQL(" (select 1 from tf_f_user_vpn vpn where vpn.user_id=u.user_id and vpn.remove_tag='0')))");

        return Dao.qryByParse(parser);
    }

    /**
     * 根据成员用户ID查询UU关系
     * 
     * @param userIdB
     * @param relationTyeCode
     * @param roleCodeB
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaByUserIdaAndRoleB(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT PARTITION_ID,                                                   ");
        parser.addSQL("        TO_CHAR(USER_ID_A) USER_ID_A,                                   ");
        parser.addSQL("        SERIAL_NUMBER_A,                                                ");
        parser.addSQL("        TO_CHAR(USER_ID_B) USER_ID_B,                                   ");
        parser.addSQL("        SERIAL_NUMBER_B,                                                ");
        parser.addSQL("        RELATION_TYPE_CODE,                                             ");
        parser.addSQL("        ROLE_TYPE_CODE,                                                 ");
        parser.addSQL("        ROLE_CODE_A,                                                    ");
        parser.addSQL("        ROLE_CODE_B,                                                    ");
        parser.addSQL("        ORDERNO,                                                        ");
        parser.addSQL("        SHORT_CODE,                                                     ");
        parser.addSQL("        INST_ID,                                                        ");
        parser.addSQL("        TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,        ");
        parser.addSQL("        TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,            ");
        parser.addSQL("        TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,      ");
        parser.addSQL("        UPDATE_STAFF_ID,                                                ");
        parser.addSQL("        UPDATE_DEPART_ID,                                               ");
        parser.addSQL("        REMARK,                                                         ");
        parser.addSQL("        RSRV_NUM1,                                                      ");
        parser.addSQL("        RSRV_NUM2,                                                      ");
        parser.addSQL("        RSRV_NUM3,                                                      ");
        parser.addSQL("        RSRV_NUM4,                                                      ");
        parser.addSQL("        RSRV_NUM5,                                                      ");
        parser.addSQL("        RSRV_STR1,                                                      ");
        parser.addSQL("        RSRV_STR2,                                                      ");
        parser.addSQL("        RSRV_STR3,                                                      ");
        parser.addSQL("        RSRV_STR4,                                                      ");
        parser.addSQL("        RSRV_STR5,                                                      ");
        parser.addSQL("        RSRV_DATE1,                                                     ");
        parser.addSQL("        RSRV_DATE2,                                                     ");
        parser.addSQL("        RSRV_DATE3,                                                     ");
        parser.addSQL("        RSRV_TAG1,                                                      ");
        parser.addSQL("        RSRV_TAG2,                                                      ");
        parser.addSQL("        RSRV_TAG3                                                       ");
        parser.addSQL("   FROM TF_F_RELATION_UU                                                ");
        parser.addSQL("  WHERE USER_ID_A = TO_NUMBER(:USER_ID_A)                               ");
        parser.addSQL("    AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE                        ");
        parser.addSQL("    AND ROLE_CODE_B = :ROLE_CODE_B                                      ");
        parser.addSQL("    AND SYSDATE < END_DATE + 0                                          ");

        return Dao.qryByParse(parser);
    }

    // 通过user_id查询一号通成员的主叫、被叫一号通号码
    public static IDataset GetQryMebYhtNumUserInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT user_id_a user_id,user_id_b,serial_number_b,relation_type_code,serial_number_b serial_number,decode(relation_type_code,'S6',rsrv_tag1,'') SHOW_MAIN ");
        parser.addSQL("FROM tf_f_relation_uu  WHERE user_id_a = TO_NUMBER(:USER_ID) ");
        parser.addSQL("AND relation_type_code IN ('S6','S7') AND end_date > start_date AND end_date > sysdate ORDER BY orderno");

        return Dao.qryByParse(parser);
    }

    // 通过user_id查询一号通成员的主叫、被叫一号通号码
    public static IDataset GetQryMebYhtNumProdAttr(IData param) throws Exception
    {
        SQLParser parser1 = new SQLParser(param);
        parser1.addSQL("SELECT user_id user_id_a,attr_code,'振动方式' ATTR_NAME,ATTR_VALUE FROM TF_F_USER_ATTR attr ");
        parser1.addSQL("WHERE USER_ID=TO_NUMBER(:USER_ID) AND ");
        parser1.addSQL("partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND INST_TYPE='P' ");
        parser1.addSQL("AND  SYSDATE  BETWEEN attr.start_date AND attr.end_date");
        parser1.addSQL(" AND attr_code in ('CNTRX_MEMB_ONE_RTYPE','CallingActivated' ,'CalledActivated')");
        parser1.addSQL("AND exists (");
        parser1.addSQL("  SELECT 1 FROM TF_F_USER_PRODUCT product ");
        parser1.addSQL("   WHERE product.USER_ID = attr.USER_ID ");
        parser1.addSQL("   AND SYSDATE BETWEEN product.start_date AND product.end_date");
        parser1.addSQL("   AND product.INST_ID = attr.INST_ID ");
        parser1.addSQL("   And Exists  ( ");
        parser1.addSQL("      SELECT 1 FROM td_b_product_meb meb Where meb.product_id = '8016' ");
        parser1.addSQL("       and meb.product_id_b = product.product_id   )    )");

        return Dao.qryByParse(parser1);
    }

    /**
     * 校验成员的服务密码，X_GETMODE：0-密码校验,返回密码校验结果1-取用户信息值
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset checkUserPasswordByPer(IData param) throws Exception
    { 
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("select impu.ims_password,uu.role_code_b IS_GRPMANAGER,u.user_id,c.cust_id,c.cust_name,up.product_id,up.brand_code,");
        parser.addSQL(" u.open_date,g.group_id,g.cust_name group_name,g.cust_id as GRP_CUST_ID from ");
        parser.addSQL(" tf_f_user_impu impu,tf_f_user u,tf_f_user_product up,tf_f_relation_uu uu,tf_f_customer c, tf_f_user u2,tf_f_user_product up2,tf_f_cust_group g");
        parser.addSQL(" where impu.user_id=u.user_id and uu.user_id_b=u.user_id and up.user_id=u.user_id ");
        parser.addSQL(" and c.cust_id=u.cust_id");
        parser.addSQL(" and u2.user_id=uu.user_id_a and up2.user_id=u2.user_id and u2.cust_id=g.cust_id");
        parser.addSQL(" and u.serial_number=:SERIAL_NUMBER");
        parser.addSQL(" and sysdate between uu.start_date and uu.end_date");
        parser.addSQL(" and (up2.product_id in('6130','8016','2222') or (up2.product_id='8001' and exists (select 1 from tf_f_user_vpn vpn where vpn.user_id=u2.user_id");
        parser.addSQL(" and vpn.remove_tag='0')))");
        
        IDataset ds = Dao.qryByParse(parser);
        if(IDataUtil.isEmpty(ds)){
        	return null;
        }
        for(int i=0; i<ds.size(); i++){
        	IData data = ds.getData(i);
        	String prodId = data.getString("PRODUCT_ID"); 
        	data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(prodId));
        	data.put("BRAND", UProductInfoQry.getBrandCodeByProductId(prodId)); 
        }
        return ds;
    }

    /**
     * 查询成员用户设置的黑白名单信息 PageData
     * 
     * @param data
     *            IData 接口传入的参数
     * @return IDataset 查询结果
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static IDataset getBwInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT B.USER_TYPE_CODE, B.USER_ID,B.EC_USER_ID, B.GROUP_ID,B.BIZ_CODE, B.RSRV_STR4, B.RSRV_STR5 FROM TF_F_USER_BLACKWHITE B");
        parser.addSQL(" WHERE B.BIZ_NAME =:BIZ_NAME");
        parser.addSQL(" AND B.EC_USER_ID =:EC_USER_ID");
        parser.addSQL(" AND B.USER_TYPE_CODE =:USER_TYPE_CODE");
        parser.addSQL(" AND sysdate BETWEEN start_date AND end_date ");

        return Dao.qryByParse(parser);
    }

    public static IDataset getGrpUserDiscnts(IData param, String xMode) throws Exception
    {
        IDataset disInfos = new DatasetList();

        if ("0".equals(xMode))
        {
            SQLParser parser = new SQLParser(param);
            parser.addSQL("select f.user_id USER_ID_A,f.start_date,f.end_date,G.OFFER_CODE PRODUCT_ID,F.DISCNT_CODE,G.Group_Id PACKAGE_ID ");
            parser.addSQL(" from tf_f_user e,tf_f_user_DISCNT f,TF_F_USER_OFFER_REL g where 1=1 ");
            parser.addSQL(" AND F.USER_ID=G.REL_USER_ID ");
            parser.addSQL(" and e.user_id=f.user_id and e.user_id=g.user_id ");
            parser.addSQL(" and f.user_id=:USER_ID ");
            parser.addSQL(" and sysdate between f.start_date and f.end_date-1 ");
            disInfos = Dao.qryByParse(parser); 

        }
        if ("1".equals(xMode))
        {
            SQLParser parser = new SQLParser(param);
            parser.addSQL(" select g.PRODUCT_ID ");
            parser.addSQL(" from tf_f_user e,tf_f_user_product g where 1=1 ");
            parser.addSQL(" and e.user_id=:USER_ID and e.user_id=g.user_id ");

            IDataset temp = Dao.qryByParse(parser);
            if(IDataUtil.isEmpty(temp)){
            	return null;
            }
            String productId = temp.getData(0).getString("PRODUCT_ID");
            IDataset ds = UProductInfoQry.queryAllProductElements(productId);
            disInfos = DataHelper.filter(ds, "ELEMENT_TYPE_CODE=D");
            if(IDataUtil.isEmpty(disInfos)){
            	return null;
            }
            
            SQLParser parser2 = new SQLParser(param);
            parser2.addSQL(" select DISCNT_CODE from tf_f_user_DISCNT t where t.user_id=:USER_ID and sysdate between start_date and end_date-1 ");
            IDataset disInfos2 = Dao.qryByParse(parser2);
            if(IDataUtil.isNotEmpty(disInfos2)){
            	for (int k = disInfos.size() - 1; k >= 0; k--)
                {
                    IData result = disInfos.getData(k);
                    String elementId = result.getString("ELEMENT_ID", ""); 
                    String packageId = result.getString("PACKAGE_ID", ""); 
                    boolean exist = false;
                    for (int i = 0; i < disInfos2.size(); i++)
                    {
                        IData tmp = disInfos2.getData(i);
                        String disCode = tmp.getString("DISCNT_CODE", ""); 
                        if (disCode.equals(elementId))
                        {
                        	exist = true;
                            break;
                        }
                    }
                    
                    if(exist){
                    	disInfos.remove(k);
                    }else{
                    	IData disInfo = UDiscntInfoQry.getDiscntInfoByPk(elementId);
                    	result.put("DISCNT_NAME", disInfo.getString("DISCNT_NAME"));
                    	result.put("DISCNT_EXPLAIN", disInfo.getString("DISCNT_EXPLAIN"));
                    	IData info = UPackageInfoQry.getPackageByProductIdAndPackageId(productId, packageId);
                    	result.put("MIN_NUMBER", info.getString("MIN_NUMBER"));
                    	result.put("MAX_NUMBER", info.getString("MAX_NUMBER"));
                    	result.put("DEFAULT_TAG", info.getString("DEFAULT_TAG"));
                    	result.put("FORCE_TAG", info.getString("FORCE_TAG"));
                    	result.put("PACKAGE_NAME", info.getString("PACKAGE_NAME"));
                    }
                }
            }
            return disInfos;
            
        }
        else if ("2".equals(xMode))
        {// 查询集团定制成员优惠
            SQLParser parser = new SQLParser(param);
            parser.addSQL(" select e.element_id DISCNT_CODE, g.PRODUCT_ID, e.PACKAGE_ID, e.default_tag, e.force_tag ");
            parser.addSQL(" from TF_F_USER_GRP_PACKAGE e, tf_f_user f,tf_f_user_product g ");
            parser.addSQL(" where 1 = 1 and e.user_id = :USER_ID and e.partition_id = mod(to_number(:USER_ID),10000) ");
            parser.addSQL(" and e.element_type_code = 'D' and f.user_id = e.user_id and g.user_id=f.user_id  ");
            parser.addSQL(" and f.partition_id = e.partition_id and sysdate between e.start_date and e.end_date - 1 ");
            disInfos = Dao.qryByParse(parser);
        }
        
        if(IDataUtil.isEmpty(disInfos)){
        	return null;
        }
        
        for(int i=0; i<disInfos.size(); i++){
        	IData data = disInfos.getData(i);
        	String disCode = data.getString("DISCNT_CODE");
        	String productId = data.getString("PRODUCT_ID");
        	String packageId =  data.getString("PACKAGE_ID"); 
        	IData disInfo = UDiscntInfoQry.getDiscntInfoByPk(disCode);
        	data.put("DISCNT_NAME", disInfo.getString("DISCNT_NAME"));
        	data.put("DISCNT_EXPLAIN", disInfo.getString("DISCNT_EXPLAIN"));
        	
        	IData info = UPackageInfoQry.getPackageByProductIdAndPackageId(productId, packageId);
        	data.put("MIN_NUMBER", info.getString("MIN_NUMBER"));
        	data.put("MAX_NUMBER", info.getString("MAX_NUMBER"));
        	data.put("DEFAULT_TAG", info.getString("DEFAULT_TAG"));
        	data.put("FORCE_TAG", info.getString("FORCE_TAG"));
        	data.put("PACKAGE_NAME", info.getString("PACKAGE_NAME"));
        }
        return disInfos;
    }
    
    public static IDataset getImsParam(String userId, String userIdA, String attrCode, String instId, String grpProductId) throws Exception
    {
        IDataset imsParamRet = new DatasetList();
        
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("USER_ID_A", userIdA);
        param.put("ID", grpProductId);
        param.put("ATTR_CODE", attrCode);
        param.put("INST_ID", instId);
        
        SQLParser parser = new SQLParser(param);
        
        if (StringUtils.equals("2222", grpProductId))   //陕西参数改成服务了，需做特殊处理
        {
            parser.addSQL("select to_char(b.user_id) user_id,decode(a.service_id,'"+NOCOND_TRANSFER+"','NOCOND_TRANSFER','"+BUSY_TRANSFER+"','BUSY_TRANSFER','"+CNTRX_MEMB_CFNR_BSV+"','CNTRX_MEMB_CFNR_BSV'," +
                    "'"+CNTRX_MEMB_CFNL_BSV+"','CNTRX_MEMB_CFNL_BSV','"+CALLBARRING+"','CallBarring','"+HSS_IMPIATTR_IMPI_ID+"','HSS_IMPIATTR_IMPI_ID'," +
                    "'"+ALARMCALL+"','AlarmCall','"+CNTRX_NO_DISTURB+"','CNTRX_NO_DISTURB','"+SHORT_DIAL+"','SHORT_DIAL','"+CALLWAITING+"','CallWaiting'," +
                    "'"+CALLHOLD+"','CallHold','"+THTEECALL+"','ThreePartyService','"+CNTRX_CORP_CLIP+"','CNTRX_CORP_CLIP','"+CNTRX_CORP_CLIR+"','CNTRX_CORP_CLIR',a.service_id) attr_code," +
                    "decode(a.service_id,'"+NOCOND_TRANSFER+"','无条件呼叫前转','"+BUSY_TRANSFER+"','遇用户忙呼叫前转','"+CNTRX_MEMB_CFNR_BSV+"','无应答呼叫前转'," +
                    "'"+CNTRX_MEMB_CFNL_BSV+"','未登录呼叫前转','"+CALLBARRING+"','呼出限制业务','"+HSS_IMPIATTR_IMPI_ID+"','漫游服务','"+ALARMCALL+"','叫醒服务','"+CNTRX_NO_DISTURB+"','免打扰'," +
                    "'"+SHORT_DIAL+"','缩位拨号','"+CALLWAITING+"','呼叫等待','"+CALLHOLD+"','呼叫保持','"+THTEECALL+"','三方通话','"+CNTRX_CORP_CLIP+"','主叫号码显示'," +
                    "'"+CNTRX_CORP_CLIR+"','主叫号码显示限制',a.service_id) ATTR_NAME,'1' is_order,'1' attr_value, '3' attr_type,'1' is_modify " +
                    " from td_b_service a,tf_f_user_svc b where 1=1 and a.service_id=b.service_id " +
                    "and a.service_id in ('"+NOCOND_TRANSFER+"','"+BUSY_TRANSFER+"','"+CNTRX_MEMB_CFNR_BSV+"','"+CNTRX_MEMB_CFNL_BSV+"','"+CALLBARRING+"'," +
                    "'"+HSS_IMPIATTR_IMPI_ID+"','"+ALARMCALL+"','"+CNTRX_NO_DISTURB+"','"+SHORT_DIAL+"','"+CALLWAITING+"','"+CALLHOLD+"','"+THTEECALL+"'," +
                    "'"+CNTRX_CORP_CLIP+"','"+CNTRX_CORP_CLIR+"') ");  
            parser.addSQL(" and  b.user_id= :USER_ID  ");
            parser.addSQL(" and  b.user_id_a= :USER_ID_A  ");
            parser.addSQL(" and sysdate between b.start_date and b.end_date  ");      //查成员订购服务表
            parser.addSQL(" union all "); 
            parser.addSQL(" select '' user_id,decode(a.service_id,'"+NOCOND_TRANSFER+"','NOCOND_TRANSFER','"+BUSY_TRANSFER+"','BUSY_TRANSFER','"+CNTRX_MEMB_CFNR_BSV+"','CNTRX_MEMB_CFNR_BSV'," +
                    "'"+CNTRX_MEMB_CFNL_BSV+"','CNTRX_MEMB_CFNL_BSV','"+CALLBARRING+"','CallBarring','"+HSS_IMPIATTR_IMPI_ID+"','HSS_IMPIATTR_IMPI_ID'," +
                    "'"+ALARMCALL+"','AlarmCall','"+CNTRX_NO_DISTURB+"','CNTRX_NO_DISTURB','"+SHORT_DIAL+"','SHORT_DIAL','"+CALLWAITING+"','CallWaiting'," +
                    "'"+CALLHOLD+"','CallHold','"+THTEECALL+"','ThreePartyService','"+CNTRX_CORP_CLIP+"','CNTRX_CORP_CLIP','"+CNTRX_CORP_CLIR+"','CNTRX_CORP_CLIR',a.service_id) attr_code," +
                    "decode(a.service_id,'"+NOCOND_TRANSFER+"','无条件呼叫前转','"+BUSY_TRANSFER+"','遇用户忙呼叫前转','"+CNTRX_MEMB_CFNR_BSV+"','无应答呼叫前转'," +
                    "'"+CNTRX_MEMB_CFNL_BSV+"','未登录呼叫前转','"+CALLBARRING+"','呼出限制业务','"+HSS_IMPIATTR_IMPI_ID+"','漫游服务','"+ALARMCALL+"','叫醒服务','"+CNTRX_NO_DISTURB+"','免打扰'," +
                    "'"+SHORT_DIAL+"','缩位拨号','"+CALLWAITING+"','呼叫等待','"+CALLHOLD+"','呼叫保持','"+THTEECALL+"','三方通话','"+CNTRX_CORP_CLIP+"','主叫号码显示'," +
                    "'"+CNTRX_CORP_CLIR+"','主叫号码显示限制',a.service_id) ATTR_NAME,'0' is_order,'' attr_value, '3' attr_type,'1' is_modify " +
                    " from td_b_service a where 1=1 and a.service_id in ('"+NOCOND_TRANSFER+"','"+BUSY_TRANSFER+"','"+CNTRX_MEMB_CFNR_BSV+"','"+CNTRX_MEMB_CFNL_BSV+"','"+CALLBARRING+"'," +
                    "'"+HSS_IMPIATTR_IMPI_ID+"','"+ALARMCALL+"','"+CNTRX_NO_DISTURB+"','"+SHORT_DIAL+"','"+CALLWAITING+"','"+CALLHOLD+"','"+THTEECALL+"'," +
                    "'"+CNTRX_CORP_CLIP+"','"+CNTRX_CORP_CLIR+"')" +
                    "and a.service_id not in (select service_id from tf_f_user_svc b where 1=1 "); 
            parser.addSQL(" and  b.user_id= :USER_ID  "); 
            parser.addSQL(" and  b.user_id_a= :USER_ID_A  "); 
            parser.addSQL(" and sysdate between b.start_date and b.end_date  ");
            parser.addSQL(" )");                                                           //查未订购的服务
            parser.addSQL(" union all "); 
            parser.addSQL(" select to_char(b.user_id) user_id,decode(b.attr_code,'ListProperty','NO_DISURB_TYPE','UserCFBInfo','BUSY_TRANSFER_SN','UserCFUInfo','NOCOND_TRANSFER_SN'," +
                    "'UserCFNRInfo','CNTRX_CFR_SN','userCFNLInfo','CNTRX_CFNL_SN',b.attr_code ) attr_code ," +
                    "decode(b.attr_code,'ListProperty','免打扰类型','UserCFBInfo','遇用户忙呼叫前转号码','UserCFUInfo','无条件呼叫前转号码'," +
                    "'UserCFNRInfo','无应答呼叫前转号码','userCFNLInfo','未登录呼叫前转号码'," +
                    "'HSS_AUTH_TYPE','鉴权类型','HSS_CAPS_ID','能力值','HSS_CAPS_TYPE','能力标识类型','MEMB_USERCALLBARRING','呼叫限制'" +
                    ",'MEMB_WAKE_NUMBER','闹醒号码','MEMB_WAKE_TIME','闹醒时间','CNTRX_MEBSN_1','第一个筛选号码','CNTRX_MEBSN_2','第二个筛选号码'" +
                    ",'CNTRX_MEBSN_3','第三个筛选号码','CNTRX_MEBSN_4','第四个筛选号码','CNTRX_MEBSN_5','第五个筛选号码'" +
                    ",'CNTRX_MEBSN_6','第六个筛选号码','CNTRX_MEBSN_7','第七个筛选号码','CNTRX_MEBSN_8','第八个筛选号码'" +
                    ",'CNTRX_MEBSN_9','第九个筛选号码','CNTRX_MEBSN_10','第十个筛选号码',b.attr_code) ATTR_NAME,'1' is_order,b.attr_value,'1' attr_type,'1' is_modify " +
                    "from tf_f_user_svc a,tf_f_user_attr b where a.inst_id=b.inst_id "); 
            parser.addSQL(" and  b.user_id= :USER_ID  "); 
            parser.addSQL(" and a.service_id in ('"+HSS_BASESVC+"','"+NOCOND_TRANSFER+"','"+BUSY_TRANSFER+"','"+CNTRX_MEMB_CFNR_BSV+"','"+CNTRX_MEMB_CFNL_BSV+"'," +
                    "'"+CALLBARRING+"','"+HSS_IMPIATTR_IMPI_ID+"','"+ALARMCALL+"','"+CNTRX_NO_DISTURB+"','"+SHORT_DIAL+"') "); 
            parser.addSQL(" and sysdate between b.start_date and b.end_date  ");          //查出成员的属性表参数
            parser.addSQL(" union all"); 
            parser.addSQL(" select to_char(t.user_id) user_id,'SHORT_CODE' attr_code,'短号' ATTR_NAME,'1' is_order,t.res_code attr_value,'1' attr_type,'1' is_modify" +
                    " from tf_f_user_res t where 1=1 ");  
            parser.addSQL(" and t.user_id= :USER_ID ");
            parser.addSQL(" and t.partition_id=mod(:USER_ID,10000)");
            parser.addSQL(" and t.user_id_a= :USER_ID_A  ");
            parser.addSQL(" and t.end_date>=sysdate ");      //查成员短号
        }
        else if (StringUtils.equals("8001", grpProductId)) //融合V网参数 
        {
            parser.addSQL("select to_char(t.user_id) user_id,'SHORT_CODE' attr_code,'短号' ATTR_NAME,'1' is_order,t.res_code attr_value,'1' attr_type,'1' is_modify" +
                    " from tf_f_user_res t where  1=1 ");  
            parser.addSQL(" and t.user_id= :USER_ID ");
            parser.addSQL(" and t.partition_id=mod(:USER_ID,10000)");
            parser.addSQL(" and t.user_id_a= :USER_ID_A  ");
            parser.addSQL(" and t.end_date>=sysdate ");      //查成员短号
            parser.addSQL(" union all "); 
            parser.addSQL(" select to_char(t.user_id) user_id,'CALL_DISP_MODE' attr_code,'号显' ATTR_NAME,'1' is_order," +
                    "t.call_disp_mode attr_value,'1' attr_type,'1' is_modify from tf_f_user_vpn_meb t WHERE 1=1 "); 
            parser.addSQL(" and  t.user_id= :USER_ID "); 
            parser.addSQL(" and  PARTITION_ID=mod(to_number(:USER_ID),10000) "); 
            parser.addSQL(" and  t.user_id_a= :USER_ID_A  "); 
            parser.addSQL(" and Remove_Tag <> '1' ");       //查号显方式 
        }
        else if (StringUtils.equals("8016", grpProductId)) //融合一号通参数 
        {
            parser.addSQL("select to_char(user_id_b) user_id,'CNTRX_MEMB_ONE_RTYPE' attr_code, '振铃方式' ATTR_NAME,'1' is_order,RSRV_TAG1 attr_value," +
                    " '1' attr_type,'1' is_modify  from tf_f_relation_uu t where 1=1");  
            parser.addSQL(" and t.user_id_b= :USER_ID ");
            parser.addSQL(" and t.partition_id=mod(:USER_ID,10000)");
            parser.addSQL(" and t.user_id_a= :USER_ID_A  ");
            parser.addSQL(" and sysdate between t.start_date and t.end_date ");      //查振铃方式
        }
        else if (StringUtils.equals("6130", grpProductId)) //融合总机参数 
        {
            parser.addSQL("SELECT  to_char(t.user_id_b) user_id,'IS_SUPERTELOPER' attr_code,'话务员标识' ATTR_NAME,'1' is_order, " +
                    "decode(role_code_b,'3','on',role_code_b) attr_value,'1' attr_type,'1' is_modify FROM tf_f_relation_uu t where 1=1 ");  
            parser.addSQL(" and user_id_a = TO_NUMBER(:USER_ID_A) ");
            parser.addSQL(" AND user_id_b = TO_NUMBER(:USER_ID)");
            parser.addSQL(" AND partition_id =MOD(to_number(:USER_ID),10000)");
            parser.addSQL(" AND relation_type_code = '46'");
            parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");       //话务员标识
            parser.addSQL(" union all ");
            /*
            parser.addSQL("select to_char(t.user_id) user_id,'SHORT_CODE' attr_code,'成员分机号' ATTR_NAME,'1' is_order," +
                    " t.res_code attr_value,'1' attr_type,'1' is_modify from tf_f_user_res t,td_s_restype d  where   t.res_type_code=d.res_type_code ");  
            parser.addSQL(" and t.user_id = TO_NUMBER(:USER_ID)");
            parser.addSQL(" and t.partition_id = mod(:USER_ID,10000)");
            parser.addSQL(" and t.user_id_a=TO_NUMBER(:USER_ID_A)");
            parser.addSQL(" and t.end_date>=sysdate");       //查成员分机号
            */
            parser.addSQL("select to_char(t.user_id_b) user_id,'SHORT_CODE' attr_code,'成员分机号' ATTR_NAME,'1' is_order," +
                    " t.short_code attr_value,'1' attr_type,'1' is_modify from tf_f_relation_uu t where   1=1  AND relation_type_code ='46'");  
            parser.addSQL(" and t.user_id_b = TO_NUMBER(:USER_ID)");
            parser.addSQL(" and t.partition_id = mod(:USER_ID,10000)");
            parser.addSQL(" and t.user_id_a=TO_NUMBER(:USER_ID_A)");
            parser.addSQL(" and  sysdate BETWEEN start_date AND end_date AND end_date>last_day(trunc(sysdate))+1-1/24/3600");       //查成员分机号
            parser.addSQL(" union all ");
            parser.addSQL("select to_char(user_id) user_id,'SUPERTELNUMBER' attr_code,'总机号码' ATTR_NAME,'1' is_order," +
                    "rsrv_str2 attr_value,'1' attr_type,'0' is_modify from tf_f_user_vpn_meb  where  1=1 ");  
            parser.addSQL(" and USER_ID = TO_NUMBER(:USER_ID)");
            parser.addSQL(" and partition_id = mod(:USER_ID,10000)");
            parser.addSQL(" and user_id_a=TO_NUMBER(:USER_ID_A)");
            parser.addSQL("  and Remove_Tag <> '1'");       //查总机号
            parser.addSQL(" union all ");
            parser.addSQL("select to_char(user_id) user_id,'OPERATORPRIONTY' attr_code,'轮选组优先级' ATTR_NAME,'1' is_order," +
                    "rsrv_str3 attr_value,'1' attr_type,'1' is_modify from tf_f_user_vpn_meb  where  1=1 ");  
            parser.addSQL(" and USER_ID = TO_NUMBER(:USER_ID)");
            parser.addSQL(" and partition_id = mod(:USER_ID,10000)");
            parser.addSQL(" and user_id_a=TO_NUMBER(:USER_ID_A)");
            parser.addSQL("  and Remove_Tag <> '1'");       //查轮选组优先级
        }
        else    //湖南专用
        {
            if(StringUtils.isEmpty(attrCode))
            {                   //个性化参数 
                //根据产品id,成员用户id 查出所有成员产品参数的成员用户id、参数KEY、参数名、参数值
                parser.addSQL("select to_char(b.user_id) user_id,b.attr_code,a.attr_lable ATTR_NAME,'1' is_order,b.attr_value," +
                        "decode(a.attr_type_code,'0','1','1','2','2','4','3','2','1') attr_type,'1' is_modify from TD_B_ATTR_ITEMA a,tf_f_user_attr b where 1=1 "); 
                parser.addSQL(" and a.attr_code= b.attr_code  and a.id_type='P' and a.rsrv_str1='IMS' and a.ATTR_OBJ='1' ");
                parser.addSQL(" and  b.user_id= :USER_ID  ");
                parser.addSQL(" and  b.inst_id= :INST_ID  ");
                parser.addSQL(" and a.id =:ID "); 
                parser.addSQL(" and sysdate between b.start_date and b.end_date "); 
                parser.addSQL(" union all "); 
                parser.addSQL(" select '' user_id, a.attr_code,a.attr_lable ATTR_NAME,'0' is_order,'' attr_value ," +
                        "decode(a.attr_type_code,'0','1','1','2','2','4','3','2','1') attr_type,'1' is_modify from TD_B_ATTR_ITEMA a where 1=1 ");
                parser.addSQL(" and a.id=:ID ");
                parser.addSQL(" and a.id_type='P' and a.rsrv_str1='IMS' and a.ATTR_OBJ='1' and a.attr_code not in ( ");
                parser.addSQL(" select b.attr_code from TD_B_ATTR_ITEMA a,tf_f_user_attr b where a.attr_code= b.attr_code  " +
                        "and a.id_type='P' and a.rsrv_str1='IMS' and a.ATTR_OBJ='1' ");
                parser.addSQL(" and  b.user_id= :USER_ID  "); 
                parser.addSQL(" and  b.inst_id= :INST_ID  ");
                parser.addSQL(" and a.id=:ID  ");
                parser.addSQL(" and sysdate between b.start_date and b.end_date  ");
                parser.addSQL(" )"); 
                
            }
            else
            { 
                //根据产品id,参数KEY,成员用户id  查出所有成员产品参数的成员用户id、参数KEY、参数名、参数值
                parser.addSQL("select to_char(b.user_id) user_id,b.attr_code,a.attr_lable ATTR_NAME,'1' is_order,b.attr_value,decode(a.attr_type_code,'0','1','1','2','2','4','3','2','1') attr_type,'1' is_modify from TD_B_ATTR_ITEMA a,tf_f_user_attr b where 1=1 "); 
                parser.addSQL(" and a.attr_code= b.attr_code  and a.id_type='P' and a.rsrv_str1='IMS' and a.ATTR_OBJ='1' ");
                parser.addSQL(" and  b.user_id = :USER_ID  ");
                parser.addSQL(" and a.id = :ID "); 
                parser.addSQL(" and  b.inst_id= :INST_ID  ");
                parser.addSQL(" and sysdate between b.start_date and b.end_date "); 
                parser.addSQL(" and  b.attr_code = :ATTR_CODE  ");
                parser.addSQL(" union all "); 
                parser.addSQL(" select '' user_id,t.attr_code,t.attr_lable ATTR_NAME,'0' is_order,'' attr_value,decode(t.attr_type_code,'0','1','1','2','2','4','3','2','1') attr_type,'1' is_modify from TD_B_ATTR_ITEMA t where 1=1 "); 
                parser.addSQL(" and t.id = :ID "); 
                parser.addSQL(" and  t.attr_code = :ATTR_CODE  ");
                parser.addSQL(" and t.id_type='P' and t.rsrv_str1='IMS' and t.ATTR_OBJ='1' and t.attr_code not in ( ");
                parser.addSQL(" select b.attr_code from TD_B_ATTR_ITEMA a,tf_f_user_attr b where a.attr_code= b.attr_code  and a.id_type='P' and a.rsrv_str1='IMS' and a.ATTR_OBJ='1' ");
                parser.addSQL(" and  b.user_id= :USER_ID  "); 
                parser.addSQL(" and  b.inst_id= :INST_ID  ");
                parser.addSQL(" and sysdate between b.start_date and b.end_date "); 
                parser.addSQL(" and a.id=:ID  ");
                parser.addSQL(" )");
                
            }
        }
        
        imsParamRet = Dao.qryByParse(parser);
        
        if (StringUtils.equals("8016", grpProductId))
        {
            if (IDataUtil.isNotEmpty(imsParamRet))
            {
                IDataset idsRet = new DatasetList();
                IData temp = imsParamRet.getData(0);
                idsRet.add(temp);
                
                imsParamRet = idsRet;   //数据排重
            }
        }
        
        if(StringUtils.equals("2222", grpProductId))
        {
            
            if(StringUtils.isEmpty(attrCode) || StringUtils.equals("SHORT_NUMBER", attrCode))
            {
                //处理缩位拨号
                IDataset infos = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userId, "SHORTDIALSN");
                
                if(IDataUtil.isNotEmpty(infos))
                {
                    for(int f = 0, size = infos.size(); f < size; f++)
                    {
                        IData temp = new DataMap();
                        temp.put("USER_ID", userId);
                        temp.put("ATTR_CODE", "SHORT_NUMBER");
                        temp.put("ATTR_NAME", "缩位号码");
                        temp.put("IS_ORDER", "1");
                        
                        IData shortinfo = infos.getData(f);
                        String shortnumber  = shortinfo.getString("RSRV_STR1","");
                        String shortsn = shortinfo.getString("RSRV_VALUE","");
                        shortnumber = shortnumber+"_"+shortsn;
                        
                        temp.put("ATTR_VALUE", shortnumber);
                        temp.put("ATTR_TYPE", "1");
                        temp.put("IS_MODIFY", "0");
                        
                        imsParamRet.add(temp);
                    }
                }
                else
                {
                    IData temp = new DataMap();
                    temp.put("USER_ID", "");
                    temp.put("ATTR_CODE", "SHORT_NUMBER");
                    temp.put("ATTR_NAME", "缩位号码");
                    temp.put("IS_ORDER", "0");
                    temp.put("ATTR_VALUE", "");
                    temp.put("ATTR_TYPE", "1");
                    temp.put("IS_MODIFY", "0");
                    
                    imsParamRet.add(temp);
                }
            }
        }
        
        return imsParamRet;
    }
}
