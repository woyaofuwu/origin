SELECT COUNT(*) recordcount
  FROM tf_f_user_svcstate
 WHERE partition_id = MOD(to_number(:USER_ID),10000)
   AND user_id = to_number(:USER_ID)
   AND state_code IN ('3','4','5','7','E','F')
   AND start_date < sysdate
   AND end_date>ADD_MONTHS(sysdate,-6)-0.00001