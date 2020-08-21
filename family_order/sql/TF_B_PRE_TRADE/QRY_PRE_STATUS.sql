select * 
from TF_B_PRE_TRADE
where ( SERIAL_NUMBER =:SERIAL_NUMBER or RSRV_STR1 = :SERIAL_NUMBER ) 
and TRADE_TYPE_CODE = :TRADE_TYPE_CODE 
and PRE_INVALID_TIME > sysdate