SELECT count(*) recordcount
  FROM td_a_transmode_limit
 WHERE PARA_CODE=:PARA_CODE
   AND ID in (select service_id from tf_f_user_svc a where user_id=:USER_ID
                and partition_id = MOD(to_number(:USER_ID),10000)
                            AND end_date>sysdate  AND main_tag='0')
   AND ID_TYPE='1'
   AND LIMIT_TAG='2'