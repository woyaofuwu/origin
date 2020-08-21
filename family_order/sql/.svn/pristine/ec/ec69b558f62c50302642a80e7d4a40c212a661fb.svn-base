SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,inst_id,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt
  WHERE user_id = TO_NUMBER(:USER_ID)
  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
  AND discnt_code = :DISCNT_CODE
  AND INST_ID = :INST_ID
  AND SYSDATE > START_DATE
