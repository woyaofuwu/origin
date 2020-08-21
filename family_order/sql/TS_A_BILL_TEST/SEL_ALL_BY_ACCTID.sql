SELECT bill_id,acct_id,user_id,partition_id,acyc_id,integrate_item_code,fee,balance,a_discnt,adjust_before,adjust_after,late_fee,late_balance,to_char(latecal_date,'yyyy/mm/dd hh24:mi:ss') latecal_date,canpay_tag,pay_tag,bill_pay_tag,to_char(update_time,'yyyy/mm/dd hh24:mi:ss') update_time,update_depart_id,update_staff_id,b_discnt,print_fee 
  FROM ts_a_bill_test
 WHERE acct_id=:ACCT_ID
   AND partition_id=:PARTITION_ID
   AND acyc_id<=:ACYC_ID