SELECT partition_id,to_char(user_id) user_id,user_id_a,res_type_code,res_code,imsi,ki,inst_id,campn_id,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
 FROM tf_f_user_res
 WHERE partition_id=MOD(to_number(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   and end_date 
   in (select max(end_date) from tf_f_user_res where user_id=TO_NUMBER(:USER_ID) and partition_id=MOD(to_number(:USER_ID),10000) and res_type_code in ('0', '1'))