select * from TF_F_USER_OTHER t
where rsrv_str1 = :RSRV_STR1
and  SYSDATE BETWEEN T.START_DATE AND T.END_DATE
and rsrv_tag2 = '1'