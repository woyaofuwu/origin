SELECT partition_id,to_char(user_id) user_id,serial_number,biz_type_code,org_domain,info_code,info_value,rsrv_num1,rsrv_num2,rsrv_str1,rsrv_str2,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_mbmp_plus
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND biz_type_code=:BIZ_TYPE_CODE
   AND info_code=:INFO_CODE