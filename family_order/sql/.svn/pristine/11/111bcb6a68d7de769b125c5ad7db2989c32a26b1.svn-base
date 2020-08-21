update tf_b_trade 
SET subscribe_state=:subscribe_state , bpm_id=:bpm_id, finish_date=sysdate 
WHERE trade_id=TO_NUMBER(:TRADE_ID) and cancel_tag =:cancel_tag
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))