SELECT * FROM TF_B_PRE_TRADE where ( SERIAL_NUMBER = :SERIAL_NUMBER or RSRV_STR1 = :SERIAL_NUMBER ) and STATUS = '1' and PRE_INVALID_TIME > sysdate