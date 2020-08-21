SELECT sum(i.integral_fee) integral_fee
  FROM tf_ah_integralbill i,td_a_acycpara a
 WHERE i.user_id=TO_NUMBER(:USER_ID)
  AND i.acyc_id=a.acyc_id
  AND (to_char(a.acyc_start_time,'yyyymm')  between :ACYC_START_TIME and :ACYC_END_TIME)
  AND (to_char(a.acyc_end_time,'yyyymm')  between :ACYC_START_TIME and :ACYC_END_TIME)