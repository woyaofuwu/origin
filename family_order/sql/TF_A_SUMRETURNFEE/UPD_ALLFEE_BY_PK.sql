UPDATE tf_a_sumreturnfee
   SET cash_return_fee=cash_return_fee + TO_NUMBER(:CASH_RETURN_FEE),adjust_return_fee=adjust_return_fee + TO_NUMBER(:ADJUST_RETURN_FEE),cash_encourage_fee=cash_encourage_fee + TO_NUMBER(:CASH_ENCOURAGE_FEE),adjust_encourage_fee=adjust_encourage_fee + TO_NUMBER(:ADJUST_ENCOURAGE_FEE)  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND acyc_id=:ACYC_ID
   AND integrate_item_code=:INTEGRATE_ITEM_CODE