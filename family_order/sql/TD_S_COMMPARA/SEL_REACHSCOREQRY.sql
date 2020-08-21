SELECT a.user_id ,a.serial_number,b.cust_name,
a.score_value, to_char(a.open_date, 'yyyy-mm-dd hh24:mi:ss') open_date
  FROM tf_f_user a ,tf_f_customer b
 WHERE a.cust_id = b.cust_id
    AND b.partition_id=MOD(TO_NUMBER(a.cust_id),10000)
    AND a.serial_number >= :SERIAL_NUMBER_S
    AND a.serial_number <= :SERIAL_NUMBER_E
    AND a.remove_tag = '0'
    AND a.score_value >= :SCORE_VALUE_S     
    AND a.score_value <= :SCORE_VALUE_E
    AND a.open_date >= to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
    AND a.open_date <= to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
    AND (:CITY_CODE IS NULL OR a.city_code = :CITY_CODE)