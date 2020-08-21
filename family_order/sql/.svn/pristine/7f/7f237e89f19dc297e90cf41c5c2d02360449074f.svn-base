update Tf_f_User_Other t
   set t.end_date = sysdate
 where T.RSRV_VALUE_CODE = 'FTTH_GROUP'
   AND T.USER_ID = :USER_ID
   AND SYSDATE < T.END_DATE
   AND T.START_DATE < T.END_DATE