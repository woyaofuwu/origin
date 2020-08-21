SELECT to_char(bill_id) bill_id,to_char(acct_id) acct_id,to_char(user_id) user_id,partition_id,acyc_id,integrate_item_code,to_char(nvl(fee,0)) fee,to_char(nvl(balance,0)) balance,to_char(nvl(a_discnt,0)) a_discnt,to_char(nvl(adjust_before,0)) adjust_before,to_char(nvl(adjust_after,0)) adjust_after,to_char(nvl(late_fee,0)) late_fee,to_char(nvl(late_balance,0)) late_balance,to_char(latecal_date,'yyyy-mm-dd hh24:mi:ss') latecal_date,canpay_tag,pay_tag,bill_pay_tag,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id,to_char(nvl(b_discnt,0)) b_discnt,to_char(nvl(print_fee,0)) print_fee 
  FROM ts_a_bill
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND (partition_id,acct_id) IN (SELECT MOD(acct_id,10000),acct_id 
                                       FROM tf_a_payrelation 
                                       WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=mod(:USER_ID,10000))
   AND acyc_id=:ACYC_ID
   AND pay_tag='0'
   AND canpay_tag<>'0'
   AND canpay_tag<>'2'