UPDATE tf_f_relation_uu a
   SET short_code=:SHORT_CODE, a.rsrv_date1=SYSDATE
 WHERE a.user_id_a=TO_NUMBER(:USER_ID_A)
   AND a.user_id_b=TO_NUMBER(:USER_ID_B)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND a.relation_type_code=:RELATION_TYPE_CODE
   and a.end_date>sysdate