SELECT COUNT(*) RECODENUM
  FROM tf_f_user_discnt a ,TD_B_DTYPE_DISCNT  b
  WHERE a.user_id = TO_NUMBER(:USER_ID)
  AND a.discnt_code = b.discnt_code
  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
  AND a.end_date > SYSDATE
  AND a.discnt_code IN
  (SELECT discnt_code
       FROM TD_B_DTYPE_DISCNT  
       WHERE  TRIM(discnt_type_code)= :DISCNT_TYPE_CODE
       AND SYSDATE BETWEEN start_date AND end_date)