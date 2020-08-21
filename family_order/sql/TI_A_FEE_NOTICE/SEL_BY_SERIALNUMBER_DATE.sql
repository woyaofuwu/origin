SELECT date_id,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,serial_number,to_char(user_id) user_id,to_char(cust_id) cust_id,cust_name,eparchy_code,city_code,product_id,brand_code,to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date,to_char(credit_value) credit_value,red_tag,prepay_tag,to_char(fee_avg) fee_avg,to_char(fee_cur) fee_cur,to_char(send_time,'yyyy-mm-dd hh24:mi:ss') send_time,rsrv_str1,rsrv_str2,rsrv_str3 
  FROM ti_a_fee_notice
 WHERE exec_time=TO_DATE(:EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND serial_number=:SERIAL_NUMBER
   AND eparchy_code=:EPARCHY_CODE