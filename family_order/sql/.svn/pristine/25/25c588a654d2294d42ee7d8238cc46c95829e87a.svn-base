SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt
 WHERE USER_ID= TO_NUMBER(:USER_ID)
 AND RSRV_STR1 = TO_NUMBER(:RSRV_STR1)
 AND RELATION_TYPE_CODE =:RELATION_TYPE_CODE
 AND DISCNT_CODE =:DISCNT_CODE
 AND sysdate < end_date + 0
 

