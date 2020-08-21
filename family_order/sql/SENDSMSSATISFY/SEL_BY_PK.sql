SELECT staff_id,ani,agent_id,event_guid,serial_number,recordno,string1,string2,to_char(date1,'yyyy-mm-dd hh24:mi:ss') date1,to_char(date2,'yyyy-mm-dd hh24:mi:ss') date2,deal_flag,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM sendsmssatisfy
 WHERE serial_number = :SERIAL_NUMBER
   AND trunc(update_time) = trunc(sysdate)