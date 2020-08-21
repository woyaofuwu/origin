DELETE FROM tf_f_user_discnt a
WHERE a.user_id=to_number(:USER_ID)
  AND a.partition_id=MOD(to_number(:USER_ID),10000)
  AND a.start_date>SYSDATE
  AND EXISTS(SELECT * FROM td_s_discnt_limit b WHERE b.discnt_code_a = a.discnt_code AND b.discnt_code_b=:DISCNT_CODE AND b.limit_tag = '0' AND SYSDATE BETWEEN b.start_date AND b.end_date)