SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,inst_id,campn_id,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,
rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
  FROM tf_f_user_discnt
 WHERE user_id = TO_NUMBER(:USER_ID)
 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
 AND sysdate < end_date
 AND EXISTS(
  SELECT 1 FROM td_s_commpara a WHERE 
   a.param_attr = '1551'
   AND  a.param_code = discnt_code
 )
