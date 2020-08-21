SELECT COUNT(1) recordcount
  FROM tf_r_tempoccupy WHERE res_no = :TICKET_ID
  AND res_type_code= :RES_TYPE_CODE
  AND occupy_staff_id=:TRADE_STAFF_ID
  AND valid_time>SYSDATE