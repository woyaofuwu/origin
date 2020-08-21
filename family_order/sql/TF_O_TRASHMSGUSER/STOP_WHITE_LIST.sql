UPDATE tf_o_trashmsguser
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE serial_number=:SERIAL_NUMBER
   AND id_type=:ID_TYPE
   AND cust_type=:CUST_TYPE
   AND state_code=:STATE_CODE