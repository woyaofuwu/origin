INSERT INTO tf_a_writeofflog_rc(writeoff_id,charge_id,bill_id,partition_id,acyc_id,user_id,integrate_item_code,acct_id,deposit_code,fee,writeoff_fee,old_balance,new_balance,old_late_balance,new_late_balance,old_paytag,new_paytag,cancel_tag,can_paytag,operate_time,eparchy_code) 
SELECT writeoff_id,charge_id,bill_id,partition_id,acyc_id,user_id,integrate_item_code,acct_id,deposit_code,fee,writeoff_fee,old_balance,new_balance,old_late_balance,new_late_balance,old_paytag,new_paytag,cancel_tag,can_paytag,operate_time,eparchy_code
 FROM tf_a_writeofflog
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'