SELECT c.cust_id, c.rsrv_str5 cust_name, c.rsrv_str6 pspt_type_code, c.rsrv_str7 pspt_id, c.rsrv_str8, u.serial_number, u.user_id
  FROM tf_f_cust_person c, tf_f_user u
 WHERE c.pspt_type_code = :PSPT_TYPE_CODE
   AND c.pspt_id = :PSPT_ID
   AND u.serial_number = :SERIAL_NUMBER
   AND c.cust_id = (select cust_id from tf_f_user where serial_number = :SERIAL_NUMBER and remove_tag = 0)