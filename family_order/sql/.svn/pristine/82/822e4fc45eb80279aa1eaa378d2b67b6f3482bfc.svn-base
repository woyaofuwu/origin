SELECT serial_number,card_serial,pps_card_pin,stream_no,rsp_retn,rsp_desc,stat_code,stat_desc,to_char(requst_time,'yyyy-mm-dd hh24:mi:ss') requst_time,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,remark 
  FROM tl_i_paycardlog
 WHERE serial_number=:SERIAL_NUMBER
   AND card_serial=:CARD_SERIAL
   AND requst_time=TO_DATE(:REQUST_TIME, 'YYYY-MM-DD HH24:MI:SS')