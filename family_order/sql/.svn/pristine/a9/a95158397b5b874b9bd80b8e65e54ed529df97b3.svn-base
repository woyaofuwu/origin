UPDATE tf_b_trade_discnt
   SET modify_tag=:MODIFY_TAG_NEW  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=:ACCEPT_MONTH
   AND modify_tag=:MODIFY_TAG_OLD
   AND to_char(discnt_code) = :DISCNT_CODE