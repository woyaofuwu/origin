SELECT count(*) recordcount
  FROM td_a_transmode_limit b
 WHERE PARA_CODE=:PARA_CODE
   AND ID in (select product_id
              from tf_f_user_infochange a
              where user_id=:USER_ID
              and (
                   (b.rsrv_str1='1' and sysdate between a.start_date and a.end_date) or
                   (rsrv_str1='0' and end_date>last_day(trunc(sysdate))+1 )
                  )
             )
   AND ID_TYPE='0'
   AND LIMIT_TAG='2'