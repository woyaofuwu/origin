DELETE FROM tf_f_user_discnt
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND inst_id = TO_NUMBER(:INST_ID)
   AND discnt_code = TO_NUMBER(:DISCNT_CODE) 
   AND spec_tag = :SPEC_TAG 
   AND start_date > end_date