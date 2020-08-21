UPDATE tf_f_user_discnt
   SET user_id_a=TO_NUMBER(:USER_ID_A),spec_tag=:SPEC_TAG,relation_type_code=:RELATION_TYPE_CODE,update_time=sysdate  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=:DISCNT_CODE
   AND sysdate between start_date  AND end_date