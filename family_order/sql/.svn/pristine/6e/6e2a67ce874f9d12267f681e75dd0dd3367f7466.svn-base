SELECT b.start_value,b.sim_type_code,a.city_code rsrv_str1,a.staff_id rsrv_str2,'补卡费' rsrv_str3,to_char(b.fee/100.00) fee,b.pay_money_code,b.remark,'' trade_id,'' simcardnum,'' total_fee,'' end_value,'' update_staff_id,'' update_depart_id,'' update_time
from tf_r_simcard_use a,tf_b_trade_simcardcompfee b
where a.sim_card_no=b.start_value
  --and a.sim_card_no=b.end_value
  and b.update_staff_id>=:START_STAFF_ID
  and b.update_staff_id<=:END_STAFF_ID
  and b.update_time>=to_date(:START_DATE,'yyyy-mm-dd')
  and b.update_time<=to_date(:END_DATE,'yyyy-mm-dd')+1-0.00001
union all
SELECT b.start_value,b.sim_type_code,a.city_code rsrv_str1,a.staff_id rsrv_str2,'补卡费' rsrv_str3,to_char(b.fee/100.00) fee,b.pay_money_code,b.remark,'' trade_id,'' simcardnum,'' total_fee,'' end_value,'' update_staff_id,'' update_depart_id,'' update_time
from tf_r_simcard_idle a,tf_b_trade_simcardcompfee b
where a.sim_card_no=b.start_value
  --and a.sim_card_no=b.end_value
  and b.update_staff_id>=:START_STAFF_ID
  and b.update_staff_id<=:END_STAFF_ID
  and b.update_time>=to_date(:START_DATE,'yyyy-mm-dd')
  and b.update_time<=to_date(:END_DATE,'yyyy-mm-dd')+1-0.00001