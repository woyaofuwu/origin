select  to_char(trade_id) trade_id,accept_month,to_char(cust_id) cust_id,cust_name,simple_spell,cust_type,cust_kind,cust_state,
   pspt_type_code,pspt_id,open_limit,eparchy_code,city_code,cust_passwd,to_char(score_value) score_value,credit_class,
   to_char(basic_credit_value) basic_credit_value,to_char(credit_value) credit_value,develop_staff_id,develop_depart_id,
   to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date,in_staff_id,in_depart_id,remove_tag,to_char(remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,
   remove_staff_id,remove_change,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,
   rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,
   to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
   to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,to_char(rsrv_date4,'yyyy-mm-dd hh24:mi:ss') rsrv_date4,
   to_char(rsrv_date5,'yyyy-mm-dd hh24:mi:ss') rsrv_date5,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_tag4,rsrv_tag5,
   is_real_name,city_code_a,remark,modify_tag,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,MODIFY_TAG,UPDATE_DEPART_ID
from  tf_b_trade_customer t
where t.trade_id = TO_NUMBER(:TRADE_ID)
  and t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))