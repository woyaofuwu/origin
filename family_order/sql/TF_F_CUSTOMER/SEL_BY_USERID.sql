SELECT b.partition_id,to_char(b.cust_id) cust_id,b.cust_name,b.cust_type,b.cust_state,b.pspt_type_code,b.pspt_id,b.open_limit,b.eparchy_code,b.city_code,b.cust_passwd,to_char(b.score_value) score_value,b.credit_class,to_char(b.basic_credit_value) basic_credit_value,to_char(b.credit_value) credit_value,b.remove_tag,to_char(b.remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,b.develop_depart_id,b.develop_staff_id,b.in_depart_id,b.in_staff_id,to_char(b.in_date,'yyyy-mm-dd hh24:mi:ss') in_date,b.remark,b.rsrv_str1,b.rsrv_str2,b.rsrv_str3,b.rsrv_str4,b.rsrv_str5,b.rsrv_str6,b.rsrv_str7,b.rsrv_str8,b.rsrv_str9,b.rsrv_str10
  FROM tf_f_user a, tf_f_customer b
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND b.cust_id = a.cust_id
   AND b.partition_id = MOD(a.cust_id,10000)