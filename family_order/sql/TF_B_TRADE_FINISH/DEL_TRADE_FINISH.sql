DELETE FROM TF_B_TRADE_FINISH
 WHERE trade_id = to_number(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG