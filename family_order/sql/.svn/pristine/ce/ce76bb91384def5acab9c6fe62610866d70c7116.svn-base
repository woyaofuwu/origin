SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,a.discnt_code,spec_tag,relation_type_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.discnt_code = :DISCNT_CODE
   AND to_char(a.end_date,'yyyymm') = to_char(SYSDATE,'yyyymm')