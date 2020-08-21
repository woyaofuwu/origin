SELECT bill_id,acct_id,user_id,partition_id,acyc_id,integrate_item_code,fee,balance,late_fee,late_balance,adjust_before,a_discnt,b_discnt,canpay_tag,pay_tag,bill_pay_tag 
  FROM ts_a_bill_r
 WHERE acct_id=:ACCT_ID
   AND partition_id=:PARTITION_ID
   AND canpay_tag='1'
   AND pay_tag='0'
   AND bill_pay_tag='0'