SELECT to_char(trade_id) trade_id,accept_month,to_char(USER_ID) USER_ID,discnt_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND discnt_code = TO_NUMBER(:DISCNT_CODE)
   AND modify_tag = :MODIFY_TAG