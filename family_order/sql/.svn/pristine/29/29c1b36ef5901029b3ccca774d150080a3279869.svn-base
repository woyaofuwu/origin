SELECT COUNT(1) recordcount FROM tf_bh_trade
   WHERE trade_type_code=110
         AND user_id=:USER_ID
         AND subscribe_state='9'
         AND (product_id=:PRODUCT_ID or :PRODUCT_ID='-1')
         AND rsrv_str2=:RSRV_STR2