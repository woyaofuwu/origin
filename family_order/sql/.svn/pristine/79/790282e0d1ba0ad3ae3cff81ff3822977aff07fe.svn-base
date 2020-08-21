SELECT count(*) acyc_id 
  FROM ts_a_bill
 WHERE acct_id=:ACCT_ID
   AND partition_id=:PARTITION_ID
   AND acyc_id<=:ACYC_ID
   AND canpay_tag<>'0'
   AND pay_tag<>'1'
   AND pay_tag<>'2'
   AND pay_tag<>'5'
   AND pay_tag<>'9'
   AND bill_pay_tag='0'