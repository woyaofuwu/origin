UPDATE tf_a_trade_pos
   SET deal_tag=:DEAL_TAG,
       rsrv_info1=:RSRV_INFO1,
       rsrv_info2=:RSRV_INFO2  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)