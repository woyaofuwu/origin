SELECT partition_id,to_char(user_id) user_id,to_char(USER_ID_A) USER_ID_A,res_type_code,res_code,imsi,KI,to_char(INST_ID) INST_ID,to_char(CAMPN_ID) CAMPN_ID,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,to_char(rsrv_num1) rsrv_num1,to_char(rsrv_num2) rsrv_num2,to_char(rsrv_num3) rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
  FROM tf_f_user_res
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)   
   AND START_DATE > = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')
   AND START_DATE < = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')