UPDATE tf_f_user_deduct
   SET bank_account_no=:BANK_ACCOUNT_NO,bank_usrp_id=:BANK_USRP_ID,deduct_type_code=:DEDUCT_TYPE_CODE,deduct_money=TO_NUMBER(:DEDUCT_MONEY),deduct_step=TO_NUMBER(:DEDUCT_STEP)  
 WHERE user_id=TO_NUMBER(:USER_ID)