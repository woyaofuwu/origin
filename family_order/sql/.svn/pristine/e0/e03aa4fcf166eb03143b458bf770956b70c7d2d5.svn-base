SELECT bill_id,acct_id,user_id,partition_id,acyc_id,integrate_item_code,fee,balance,late_fee,late_balance,adjust_after,adjust_before,a_discnt,b_discnt,canpay_tag,pay_tag,bill_pay_tag,nvl(trunc(latecal_date) - to_date('19000101','yyyymmdd'),0) ilatecaldate 
  FROM ts_a_bill_rc
 WHERE acct_id=:ACCT_ID
   AND partition_id=:PARTITION_ID
   AND acyc_id<=:ACYC_ID
   AND canpay_tag<>'0'
   AND pay_tag<>'1'
   AND pay_tag<>'2'
   AND pay_tag<>'5'
   AND pay_tag<>'9'
   AND bill_pay_tag='0'