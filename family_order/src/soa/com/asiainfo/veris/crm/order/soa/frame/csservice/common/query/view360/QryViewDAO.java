
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QryViewDAO
{
    /**
     * 根据SIM卡号查询手机号码
     * 
     * @param param
     * @return
     */
    public IDataset qrySerialNumberBySim(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select u.serial_number from tf_f_user u where exists(");
        parser.addSQL(" select 1 from tf_f_user_res where res_type_code='1' and");
        parser.addSQL(" res_code=:SIM_NUMBER and user_id=u.user_id )");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 查询用户信息（通过serial_number） used
     * 
     * @param pd
     * @return IDataset
     * @throws Exception
     */
    public IDataset qryUserInfoBySerialNumber(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT t.partition_id,t.user_id,t.cust_id,t.usecust_id,t.eparchy_code,t.city_code,t.city_code_a,t.user_passwd,t.user_diff_code, ");
        parser.addSQL(" t.user_type_code,t.user_tag_set,t.user_state_codeset,t.net_type_code,t.serial_number,t.contract_id,t.acct_tag, ");
        parser.addSQL(" t.prepay_tag,t.mpute_month_fee,t.mpute_date,t.first_call_time,t.last_stop_time,t.changeuser_date,t.in_net_mode, ");
        parser.addSQL(" t.in_date,t.in_staff_id,t.in_depart_id,t.open_mode,t.open_date,t.open_staff_id,t.open_depart_id,t.develop_staff_id, ");
        parser.addSQL(" t.develop_date,t.develop_depart_id,t.develop_city_code,t.develop_eparchy_code,t.develop_no,t.assure_cust_id, ");
        parser.addSQL(" t.assure_type_code,t.assure_date,t.remove_tag,t.pre_destroy_time,t.destroy_time,t.remove_eparchy_code,t.remove_city_code,t.remove_depart_id, ");
        parser.addSQL(" t.remove_reason_code,t.update_time,t.update_staff_id,t.update_depart_id,t.remark,t.rsrv_num1,t.rsrv_num2,t.rsrv_num3 ");
        parser.addSQL(" ,t.rsrv_num4,t.rsrv_num5,t.rsrv_str1,t.rsrv_str2,t.rsrv_str3,t.rsrv_str4,t.rsrv_str5,t.rsrv_str6,t.rsrv_str7,t.rsrv_str8  ");
        parser.addSQL(" ,t.rsrv_str9,t.rsrv_str10,t.rsrv_date1,t.rsrv_date2,t.rsrv_date3,t.rsrv_tag1,t.rsrv_tag2,t.rsrv_tag3  ");
        parser.addSQL(" from tf_f_user t ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and t.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" and t.cust_id=:CUST_ID");
        parser.addSQL(" and t.REMOVE_TAG = :REMOVE_TAG ");
        if ("true".equals(param.getString("REMOVE_USER_QRY", "")))
        {
            parser.addSQL(" and t.REMOVE_TAG <>'0' ");
        }
        if (StringUtils.isBlank(param.getString("NORMAL_USER_CHECK", "")))
        {
            parser.addSQL(" union ");
            parser.addSQL(" SELECT a.partition_id,a.user_id,a.cust_id,a.usecust_id,a.eparchy_code,a.city_code,a.city_code_a,a.user_passwd,a.user_diff_code, ");
            parser.addSQL(" a.user_type_code,a.user_tag_set,a.user_state_codeset,a.net_type_code,a.serial_number,a.contract_id,a.acct_tag, ");
            parser.addSQL(" a.prepay_tag,a.mpute_month_fee,a.mpute_date,a.first_call_time,a.last_stop_time,a.changeuser_date,a.in_net_mode, ");
            parser.addSQL(" a.in_date,a.in_staff_id,a.in_depart_id,a.open_mode,a.open_date,a.open_staff_id,a.open_depart_id,a.develop_staff_id, ");
            parser.addSQL(" a.develop_date,a.develop_depart_id,a.develop_city_code,a.develop_eparchy_code,a.develop_no,a.assure_cust_id, ");
            parser.addSQL(" a.assure_type_code,a.assure_date,a.remove_tag,a.pre_destroy_time,a.destroy_time,a.remove_eparchy_code,a.remove_city_code,a.remove_depart_id, ");
            parser.addSQL(" a.remove_reason_code,a.update_time,a.update_staff_id,a.update_depart_id,a.remark,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3 ");
            parser.addSQL(" ,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8  ");
            parser.addSQL(" ,a.rsrv_str9,a.rsrv_str10,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3  ");
            parser.addSQL(" from tf_fh_user a ");
            parser.addSQL(" where 1=1 ");
            parser.addSQL(" and a.SERIAL_NUMBER = :SERIAL_NUMBER");
            parser.addSQL(" and a.cust_id=:CUST_ID");
        }
        IDataset out = Dao.qryByParse(parser, pagination);
        return out;
    }
    public IDataset qry_tf_sm_bi_mmsfunc_InfoByUserId(IData param, Pagination pagination) throws Exception 
    {
	SQLParser parser = new SQLParser(param);
	parser.addSQL(" select  decode( t.att_flag1, '2', '2G','3','3G','4','4G', '无法确定') att_flag1_name , att_flag1  from tf_sm_bi_mmsfunc t  where 1=1 ");
	parser.addSQL(" and t.user_id=:USER_ID   ");
	return Dao.qryByParse(parser);
    } 
}
