UPDATE tf_f_user_discnt
   SET end_date=TRUNC(sysdate) - 1/24/3600  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND end_date > sysdate