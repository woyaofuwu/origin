SELECT to_char(trade_id) trade_id,accept_month,to_char(USER_ID) USER_ID,discnt_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
  and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  AND DISCNT_CODE=:DISCNT_CODE
  AND MODIFY_TAG=:MODIFY_TAG
  AND ADD_MONTHS(START_DATE,TO_NUMBER(:MONTHS))>END_DATE