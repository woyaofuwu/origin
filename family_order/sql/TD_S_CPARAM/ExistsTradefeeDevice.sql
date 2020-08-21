SELECT count(1) recordcount
  FROM tf_b_tradefee_device
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND (device_type_code= :DEVICE_TYPE_CODE OR :DEVICE_TYPE_CODE='*')