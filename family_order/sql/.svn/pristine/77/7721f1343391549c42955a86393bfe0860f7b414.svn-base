SELECT to_char(writeoff_id) writeoff_id,to_char(charge_id) charge_id,to_char(bill_id) bill_id,partition_id,acyc_id,to_char(user_id) user_id,integrate_item_code,to_char(acct_id) acct_id,deposit_code,to_char(fee) fee,to_char(writeoff_fee) writeoff_fee,to_char(old_balance) old_balance,to_char(new_balance) new_balance,to_char(old_late_balance) old_late_balance,to_char(new_late_balance) new_late_balance,old_paytag,new_paytag,cancel_tag,can_paytag,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,eparchy_code,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,to_char(rsrv_fee3) rsrv_fee3,to_char(rsrv_number1) rsrv_number1,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,rsrv_num 
  FROM tf_a_writeofflog
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'