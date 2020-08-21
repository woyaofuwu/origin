update Tf_f_User_Other t
   set t.end_date = sysdate,t.rsrv_tag2 = '3',t.rsrv_str7 = '2'
 where T.RSRV_VALUE_CODE = 'FTTH_GROUP'
   AND T.USER_ID = :USER_ID
   AND T.USER_ID = :INST_ID
   AND SYSDATE < T.END_DATE
   AND T.START_DATE < T.END_DATE