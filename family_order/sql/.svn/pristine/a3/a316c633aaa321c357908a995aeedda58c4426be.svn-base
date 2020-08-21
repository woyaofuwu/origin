SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_b_trade_discnt_bak
 WHERE trade_id = :TRADE_ID
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND end_date > SYSDATE