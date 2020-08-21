SELECT distinct to_char(trade_id) trade_id,accept_month,to_char(USER_ID) USER_ID,-1 discnt_code,'' modify_tag,'' start_date,'' end_date
  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
and id_type = '1'