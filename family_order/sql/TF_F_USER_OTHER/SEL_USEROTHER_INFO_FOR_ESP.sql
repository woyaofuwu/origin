select  USER_ID,
RSRV_VALUE_CODE,
RSRV_VALUE,
RSRV_STR1,
RSRV_STR2,
RSRV_STR3,
RSRV_STR4,
RSRV_STR5,
RSRV_STR6,
RSRV_STR7
from TF_F_USER_OTHER
where 1=1 
AND RSRV_VALUE_CODE=:RSRV_VALUE_CODE
AND RSRV_STR4=:RSRV_STR4 
AND RSRV_STR6=:RSRV_STR6 
AND USER_ID=:USER_ID