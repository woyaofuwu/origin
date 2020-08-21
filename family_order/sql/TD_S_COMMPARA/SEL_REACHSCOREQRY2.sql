SELECT a.user_id,a.serial_number,b.cust_name,a.city_code,
a.score_value, to_char(a.open_date,'yyyy-mm-dd hh24:mi:ss') open_date
  FROM tf_f_user a ,tf_f_customer b
 WHERE a.cust_id = b.cust_id
    AND b.partition_id=MOD(TO_NUMBER(a.cust_id),10000)
    AND a.remove_tag = '0'
    AND a.brand_code = 'G001'
    AND a.score_value >= :SCORE_VALUE_S   
    AND (a.score_value <= :SCORE_VALUE_E OR :SCORE_VALUE_E IS NULL)
    AND a.city_code = :CITY_CODE
    AND a.serial_number >= :SERIAL_NUMBER_S
    AND a.serial_number <= :SERIAL_NUMBER_E