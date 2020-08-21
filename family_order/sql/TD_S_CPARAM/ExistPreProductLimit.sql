SELECT count(*) recordcount
  FROM td_a_transmode_limit
 WHERE PARA_CODE=:PARA_CODE
   AND ID in (select product_id from tf_f_user_infochange a where user_id=:USER_ID
              and partition_id=MOD(to_number(:USER_ID),10000)
              and end_date>last_day(trunc(sysdate))+1  )
   AND ID_TYPE='0'
   AND LIMIT_TAG='2'