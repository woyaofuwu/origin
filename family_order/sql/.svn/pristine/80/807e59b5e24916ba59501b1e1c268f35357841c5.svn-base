SELECT COUNT(1) recordcount
  FROM tf_f_user_discnt a
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND end_date > SYSDATE
   AND start_date < end_date
   AND exists (select 1 from td_b_dtype_discnt d
               where d.discnt_code=a.discnt_code
                 and d.discnt_type_code=:DISCNT_TYPE_CODE)