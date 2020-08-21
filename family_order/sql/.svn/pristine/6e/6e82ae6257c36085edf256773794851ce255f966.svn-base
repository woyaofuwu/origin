SELECT accept_month,to_char(cust_contact_id) cust_contact_id,to_char(cust_id) cust_id,in_mode_code,contact_mode,contacter_id,channel_id,to_char(start_time,'yyyy-mm-dd hh24:mi:ss') start_time,to_char(finish_time,'yyyy-mm-dd hh24:mi:ss') finish_time,contact_desc,city_code,eparchy_code,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10 
  FROM tf_b_cust_contact_new
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND start_time BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')