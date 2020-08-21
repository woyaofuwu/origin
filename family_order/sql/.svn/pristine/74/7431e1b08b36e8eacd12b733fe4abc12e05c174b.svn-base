SELECT to_char(trade_id) trade_id,to_char(user_id) user_id,serial_number,to_char(leave_real_fee) leave_real_fee,to_char(credit_value) credit_value,to_char(owe_fee) owe_fee,to_char(late_fee) late_fee,start_acyc_id,end_acyc_id,remark,to_char(finish_time,'yyyy-mm-dd hh24:mi:ss') finish_time 
  FROM tp_o_credit_manualdeal
 WHERE trade_id=TO_NUMBER(:TRADE_ID)