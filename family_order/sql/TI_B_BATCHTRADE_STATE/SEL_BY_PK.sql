SELECT TO_CHAR(record_no) record_no,
       TO_CHAR(record_time,'yyyy-mm-dd hh24:mi:ss') record_time, 
	   update_state,
	   TO_CHAR(trade_id) trade_id,
	   deal_state,
	   deal_result,
	   TO_CHAR(operate_id) operate_id,
	   TRUNC(SYSDATE)-TRUNC(record_time) days
  FROM ti_b_batchtrade_state
 WHERE record_no = TO_NUMBER(:RECORD_NO)