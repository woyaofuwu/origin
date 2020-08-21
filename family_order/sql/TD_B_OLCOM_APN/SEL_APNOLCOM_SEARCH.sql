--IS_CACHE=Y
SELECT para_code,para_type_code,switch_type_code,para_code1,para_code2,rsrv_tag1,open_olcom_serv_code,close_olcom_serv_code,serv_order,order_no,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_olcom_apn
 WHERE para_code=:PARA_CODE
   AND switch_type_code=:SWITCH_TYPE_CODE
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE or eparchy_code = 'ZZZZ')