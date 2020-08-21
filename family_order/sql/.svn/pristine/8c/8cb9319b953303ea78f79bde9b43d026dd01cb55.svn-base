insert into tf_b_trade_impu
  (TRADE_ID,
   ACCEPT_MONTH,
   MODIFY_TAG,
   USER_ID,
   TEL_URL,
   SIP_URL,
   IMPI,
   IMS_USER_ID,
   IMS_PASSWORD,
   START_DATE,
   END_DATE,
   RSRV_STR1,
   RSRV_STR2,
   RSRV_STR3,
   RSRV_STR4,
   RSRV_STR5)
  select to_number(:TRADE_ID),
         :ACCEPT_MONTH ,
         '2',
         v.user_id,
         v.TEL_URL,
         v.SIP_URL,
         v.IMPI,
         v.IMS_USER_ID,
         v.IMS_PASSWORD,
         v.START_DATE,
         to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),
         v.RSRV_STR1,
         v.RSRV_STR2,
         v.RSRV_STR3,
         v.RSRV_STR4,
         v.RSRV_STR5
    from tf_f_user_impu v
   where v.partition_id = MOD(to_number(:USER_ID), 10000)
     and v.user_id = to_number(:USER_ID)
     and sysdate between v.start_date and v.end_date