SELECT (select count(1) from tf_b_trade_batdeal t where t.batch_id = TO_NUMBER(:BATCH_ID)) trade_num, batch_id, 
       NVL(SUM(CASE WHEN (cancel_tag = '0' AND deal_state IN ('0','1','3','6','9','A')) THEN 1 ELSE 0 END),0) cancel_num
from(
select a.serial_number,a.batch_id,a.cancel_tag,a.deal_state 
FROM tf_b_trade_batdeal a,ucr_crm1.tf_f_user b
 WHERE a.batch_id = TO_NUMBER(:BATCH_ID)
 and a.serial_number=b.serial_number
 and b.open_mode='1' 
 and b.remove_tag='0'
 union
 select a.serial_number ,a.batch_id,a.cancel_tag,a.deal_state 
 from tf_b_trade_batdeal a
 where  a.batch_id = TO_NUMBER(:BATCH_ID)
 and not exists(select 1 from ucr_crm1.tf_f_user b where a.serial_number=b.serial_number)  ) group by batch_id 