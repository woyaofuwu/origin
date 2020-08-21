SELECT COUNT(1) recordcount FROM tf_bh_trade
       WHERE trade_type_code=:TRADE_TYPE_CODE
         AND user_id=:USER_ID
         AND cancel_tag='0'
         and (trade_depart_id=:TRADE_DEPART_ID or :TRADE_DEPART_ID is null)