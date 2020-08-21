SELECT COUNT(1) recordcount
  FROM tf_b_serv_request_info
 WHERE trade_staff_id=:STAFF_ID
    AND start_date BETWEEN to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') AND  to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')
    AND (request_type_code = :REQUEST_TYPE_CODE OR :REQUEST_TYPE_CODE='-1')