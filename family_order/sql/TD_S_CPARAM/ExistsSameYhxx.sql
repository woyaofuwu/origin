select COUNT(1) recordcount
from tf_b_trade_other a
where trade_id=to_number(:TRADE_ID)
  and rsrv_value_code='YHXX'
  and modify_tag='0'
  and exists (select 1 from tf_f_user_other
	       where rsrv_value_code='YHXX'
		 and rsrv_str6=a.rsrv_str6
		 and end_date>sysdate)