SELECT to_char(bill_id) bill_id,to_char(acct_id) acct_id,to_char(user_id) user_id,partition_id,acyc_id,integrate_item_code,to_char(fee) fee,to_char(balance) balance,to_char(a_discnt) a_discnt,to_char(adjust_before) adjust_before,to_char(adjust_after) adjust_after,to_char(late_fee) late_fee,to_char(late_balance) late_balance,to_char(latecal_date,'yyyy-mm-dd hh24:mi:ss') latecal_date,canpay_tag,pay_tag,bill_pay_tag,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id,to_char(nvl(b_discnt,'0')) b_discnt,to_char(nvl(print_fee,'0')) print_fee 
  FROM ts_a_bill_rc
 WHERE acct_id=TO_NUMBER(:ACCT_ID) AND partition_id=:PARTITION_ID AND acyc_id>=:ACYC_ID_S AND
       acyc_id<=:ACYC_ID_E
union all
SELECT to_char(bill_id) bill_id,to_char(acct_id) acct_id,to_char(user_id) user_id,partition_id,acyc_id,integrate_item_code,to_char(0) fee,to_char(balance-fee) balance,to_char(0) a_discnt,to_char(0) adjust_before,to_char(0) adjust_after,to_char(0) late_fee,to_char(0) late_balance,to_char(latecal_date,'yyyy-mm-dd hh24:mi:ss') latecal_date,canpay_tag,pay_tag,bill_pay_tag,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id,to_char(0) b_discnt,to_char(0) print_fee 
  FROM ts_a_bill_r
 WHERE acct_id=TO_NUMBER(:ACCT_ID) AND partition_id=:PARTITION_ID AND acyc_id>=:ACYC_ID_S AND
       acyc_id<=:ACYC_ID_E