UPDATE ts_a_bill_rc
   SET balance=TO_NUMBER(:BALANCE),adjust_after=TO_NUMBER(:ADJUST_AFTER)  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND bill_id=TO_NUMBER(:BILL_ID)
   AND (partition_id,acct_id) IN (SELECT MOD(acct_id,10000),acct_id 
                                       FROM tf_a_payrelation 
                                       WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=mod(:USER_ID,10000))
   AND integrate_item_code=:INTEGRATE_ITEM_CODE