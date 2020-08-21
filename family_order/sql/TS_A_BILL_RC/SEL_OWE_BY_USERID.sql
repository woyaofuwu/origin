SELECT bill_id,acct_id,user_id,partition_id,acyc_id,integrate_item_code,fee,balance,late_fee,late_balance,adjust_after,adjust_before,a_discnt,b_discnt,canpay_tag,pay_tag,bill_pay_tag,nvl(trunc(latecal_date) - to_date('19000101','yyyymmdd'),0) ilatecaldate 
  FROM ts_a_bill_rc
 WHERE user_id=:USER_ID
   AND (partition_id,acct_id) IN (SELECT MOD(acct_id,10000),acct_id 
                                       FROM tf_a_payrelation 
                                       WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=mod(:USER_ID,10000))
   AND acyc_id<=:ACYC_ID
   AND canpay_tag<>'0'
   AND pay_tag<>'1'
   AND pay_tag<>'2'
   AND pay_tag<>'5'
   AND pay_tag<>'9'
   AND bill_pay_tag='0'