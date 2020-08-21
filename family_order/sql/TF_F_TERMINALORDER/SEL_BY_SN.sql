SELECT t.*,round((sysdate-t.start_time )*24,1) MINUS_HOUR
  FROM TF_F_TERMINALORDER t
 WHERE t.serial_number = :SERIAL_NUMBER
 AND t.RSRV_STR2='0'
 AND t.END_TIME>SYSDATE