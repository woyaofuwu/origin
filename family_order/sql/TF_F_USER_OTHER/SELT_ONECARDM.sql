SELECT to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
FROM tf_f_relation_span 
WHERE
    (serial_number_a=:SERIAL_NUMBER_A or serial_number_b=:SERIAL_NUMBER_B)
   AND sysdate BETWEEN start_date+0 AND end_date+0