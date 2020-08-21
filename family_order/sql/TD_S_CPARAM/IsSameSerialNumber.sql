SELECT COUNT(*) recordcount
  FROM tf_b_trade_attr
where trade_id = :TRADE_ID
  and accept_month = to_number(substr(:TRADE_ID, 5, 2))
  and inst_type = 'S'
  and rsrv_num1 = :SERVICE_ID
  and attr_code = 'V12V1'
  and attr_value = :SERV_PARA1
  AND modify_tag='0'