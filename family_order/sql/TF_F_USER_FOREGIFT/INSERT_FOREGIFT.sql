insert into  tf_f_user_foregift(user_id,partition_id,foregift_code,money,cust_name,pspt_id,update_time,update_staff_id,update_depart_id,remark,rsrv_date1,rsrv_date2,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5)
  Select TO_NUMBER(:USER_ID_A),MOD(TO_NUMBER(:USER_ID_A),10000),foregift_code,money,cust_name,pspt_id,to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'),to_char(:UPDATE_STAFF_ID),to_char(:UPDATE_DEPART_ID),to_char(:REMARK),rsrv_date1,rsrv_date2,user_id,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 From tf_f_user_foregift
  Where user_id=to_number(:USER_ID) 
 And partition_id=mod(to_number(:USER_ID),10000)
  and foregift_code<>20