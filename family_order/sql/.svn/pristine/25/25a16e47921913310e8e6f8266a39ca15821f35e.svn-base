UPDATE tf_f_relation_uu
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') - 1/24/3600  
 WHERE partition_id=TO_NUMBER(:PARTITION_ID)
   AND user_id_a=TO_NUMBER(:USER_ID_A)
   AND user_id_b=TO_NUMBER(:USER_ID_B)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND end_date>=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')