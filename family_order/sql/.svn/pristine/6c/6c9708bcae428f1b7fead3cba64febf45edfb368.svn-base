select to_char(acct_id) acct_id,to_char(bill_id) bill_id,integrate_item_code,to_char(old_balance-new_balance) old_balance,to_char(old_late_balance-new_late_balance) old_late_balance,new_paytag
  from tf_a_writeofflog
  where charge_id=to_number(:CHARGE_ID)
    and partition_id >=:PARTITION_ID - 1
    and partition_id <=:PARTITION_ID + 1
    and cancel_tag='1'