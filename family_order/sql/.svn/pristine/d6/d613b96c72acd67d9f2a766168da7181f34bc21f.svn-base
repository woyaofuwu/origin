select trade_id 
from TF_B_TRADE_SCORE 
where accept_month >= to_number(to_char(to_date(:START_TIME,'yyyy-mm-dd hh24:mi:ss'),'mm')) 
and accept_month  <= to_number(to_char(to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss'),'mm')) 
and update_time between to_date(:START_TIME,'yyyy-mm-dd hh24:mi:ss') and to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss')
and serial_number = :SERIAL_NUMBER 
and rsrv_str6 = :FLAG 
and rule_id = :RULE_ID 
and cancel_tag = 0 
