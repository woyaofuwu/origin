SELECT COUNT(*) recordcount
  FROM tf_o_trashmsguser
 WHERE serial_number = :SERIAL_NUMBER
   AND id_type = :ID_TYPE
   AND cust_type = '1'---黑名单名单
   AND state_code = '0'
   AND sysdate between start_date and end_date
   AND not exists(select 1 from tf_o_trashmsguser
                          where serial_number = :SERIAL_NUMBER
                            and id_type = :ID_TYPE
                            and cust_type = '0'---白名单
                            and state_code = '0'
                            and sysdate between start_date and end_date)