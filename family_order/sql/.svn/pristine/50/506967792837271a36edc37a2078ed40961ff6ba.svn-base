SELECT (select count(1) from tf_b_trade_batdeal t where t.batch_id = TO_NUMBER(:BATCH_ID)) trade_num, batch_id,
       NVL(SUM(CASE WHEN (acct_tag = '2' AND cancel_tag = '0' AND deal_state IN ('0','1','3','6','9','A')) THEN 1 ELSE 0 END),0) cancel_num
from(
select a.serial_number,a.batch_id,a.cancel_tag,a.deal_state,b.acct_tag
FROM tf_b_trade_batdeal a,ucr_crm1.tf_f_user b
 WHERE a.batch_id = TO_NUMBER(:BATCH_ID)
 and a.serial_number=b.serial_number
 and b.open_mode='1'
 and b.remove_tag='0'
) group by batch_id