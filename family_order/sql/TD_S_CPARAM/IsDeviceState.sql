SELECT COUNT(1) recordcount
  FROM tf_r_mobiledevice
 WHERE device_state NOT IN ('1', 'H')
  AND device_id IN
(SELECT imei FROM tf_b_trade_purchase
  WHERE trade_id=TO_NUMBER(:TRADE_ID))