UPDATE tf_f_relation_blacklist
   SET end_date=SYSDATE 
 WHERE user_id_b=TO_NUMBER(:USER_ID_B)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND start_date<=SYSDATE
   AND end_date>=SYSDATE