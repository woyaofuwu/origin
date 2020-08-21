SELECT count(1) recordcount
  FROM tf_f_user_svc a
 WHERE user_id=to_number(:USER_ID)
   AND partition_id=mod(to_number(:USER_ID),10000)
   AND SYSDATE BETWEEN start_date+0 AND end_date+0
   AND exists (select 1 from td_s_commpara
               where subsys_code='CSM'
                 and param_attr=133
                 and param_code=to_char(a.service_id)
                 and para_code1=:TRADE_TYPE_CODE
                 and (eparchy_code=:TRADE_EPARCHY_CODE or eparchy_code='ZZZZ')
                 and sysdate between start_date and end_date)