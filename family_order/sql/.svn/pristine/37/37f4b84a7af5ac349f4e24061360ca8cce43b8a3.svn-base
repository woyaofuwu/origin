SELECT /*+ first_rows(1) */count(1) recordcount
  FROM tf_f_relation_uu
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND user_id_b = TO_NUMBER(:USER_ID_B)
   AND relation_type_code = :RELATION_TYPE_CODE
   AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
   AND end_date+0 >= add_months(trunc(sysdate, 'MM'),1)
   and rownum < 2