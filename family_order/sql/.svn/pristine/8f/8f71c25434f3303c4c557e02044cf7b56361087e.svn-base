UPDATE tf_f_user_other a
   SET a.end_date=sysdate,a.rsrv_str10=:RSRV_STR10  
 WHERE a.partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND a.rsrv_value_code=:RSRV_VALUE_CODE
   and  (EXISTS (select 1 from tf_b_trade_svc b
               where a.USER_ID=b.USER_ID
               and a.RSRV_STR2=to_char(b.SERVICE_ID)
               and a.RSRV_STR1='2'
               and b.TRADE_ID=to_number(:RSRV_VALUE))
   or  EXISTS (select 1 from tf_b_trade_discnt c
               where a.USER_ID=c.ID
               and a.RSRV_STR2=to_char(c.discnt_code)
               and a.RSRV_STR1='1'
               and c.TRADE_ID=to_number(:RSRV_VALUE))
   or  EXISTS (select 1 from tf_f_user_svc d
               where a.USER_ID=d.USER_ID
               and a.RSRV_STR2=to_char(d.SERVICE_ID)
               and a.RSRV_STR1='2'
               and d.end_date>sysdate)
   or EXISTS (select 1 from tf_f_user_discnt e
               where a.USER_ID=e.USER_ID
               and a.RSRV_STR2=to_char(e.discnt_code)
               and a.RSRV_STR1='1'
               and e.end_date>sysdate))
   and a.end_date>sysdate