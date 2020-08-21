UPDATE tf_f_relation_uu
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND end_date>start_date
   AND (role_code_b=:ROLE_CODE_B OR :ROLE_CODE_B='*')
   AND end_date=(SELECT MAX(end_date) From tf_f_relation_uu WHERE user_id_a=TO_NUMBER(:USER_ID_A) AND end_date<SYSDATE)