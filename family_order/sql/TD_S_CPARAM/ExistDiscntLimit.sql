SELECT count(*)  recordcount
  FROM td_a_transmode_limit t
 WHERE PARA_CODE=:PARA_CODE
   AND ID in (select discnt_code from tf_f_user_discnt a where user_id=:USER_ID
              and partition_id = MOD(to_number(:USER_ID),10000)
              AND  start_date>sysdate and end_date>last_day(trunc(sysdate))+1
             )
and not exists (select 1 from tf_f_user_discnt b where user_id=:USER_ID
               and b.partition_id = MOD(to_number(:USER_ID),10000)
               AND start_date<sysdate and end_date>last_day(trunc(sysdate))-1/3600/24+1
               and t.id=b.discnt_code
               )
   AND ID_TYPE='2'
   AND LIMIT_TAG='2'