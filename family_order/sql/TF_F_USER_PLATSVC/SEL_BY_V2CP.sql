select RSRV_STR1 ,RSRV_STR2 ,RSRV_STR3 ,RSRV_STR4 ,RSRV_STR5 ,RSRV_STR6 ,RSRV_STR7 ,RSRV_STR8 ,RSRV_STR9 ,RSRV_STR11,RSRV_STR30,RSRV_STR10 
from tf_f_user_other a 
where a.rsrv_value_code='V2CP' and
      a.user_id=:USER_ID
order by a.RSRV_STR1 desc