select to_char(cust_contact_trace_id) cust_contact_trace_id,
to_char(cust_contact_id) cust_contact_id,
accept_month,contact_mode,contact_sub_mode,
to_char(sub_id) sub_id,to_char(cust_id) cust_id,cust_name,
to_char(user_id) user_id,serial_number,eparchy_code,
product_id,city_code,in_mode_code,in_media_code,
channel_id,sub_channel_id,
to_char(start_time,'yyyy-mm-dd hh24:mi:ss') start_time,
rsrv_str1,rsrv_str2,rsrv_str3,
rsrv_str4,rsrv_str5,rsrv_str6,
rsrv_str7,rsrv_str8,rsrv_str9,
rsrv_str10,remark
  FROM TF_B_CUST_CONTACT_TRACE
 WHERE cust_contact_id=TO_NUMBER(:CUST_CONTACT_ID) 
   AND accept_month=TO_NUMBER(SUBSTR(:CUST_CONTACT_ID,5,2))