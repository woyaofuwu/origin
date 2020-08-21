SELECT bill_id,acct_id,user_id,partition_id,acyc_id,integrate_item_code,fee,balance,a_discnt,adjust_before,adjust_after,late_fee,late_balance,to_char(latecal_date,'yyyy/mm/dd hh24:mi:ss') latecal_date,canpay_tag,pay_tag,bill_pay_tag,to_char(update_time,'yyyy/mm/dd hh24:mi:ss') update_time,update_depart_id,update_staff_id,b_discnt,print_fee 
  FROM ts_a_bill
 WHERE user_id=:USER_ID
   AND (partition_id,acct_id) IN (SELECT MOD(acct_id,10000),acct_id 
                                       FROM tf_a_payrelation 
                                       WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=mod(:USER_ID,10000))
   AND acyc_id<=:ACYC_ID