UPDATE tl_i_paycardlog
   SET stream_no=:STREAM_NO,rsp_retn=:RSP_RETN,rsp_desc=:RSP_DESC,stat_code=:STAT_CODE,stat_desc=:STAT_DESC,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),remark=:REMARK  
 WHERE serial_number=:SERIAL_NUMBER
   AND card_serial=:CARD_SERIAL
   AND requst_time=TO_DATE(:REQUST_TIME, 'YYYY-MM-DD HH24:MI:SS')