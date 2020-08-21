UPDATE tf_f_user_trans
   SET rsrv_info1    = '9',
       rsrv_num1     = :ACYC_ID,
       rsrv_date1    = SYSDATE,
       gleft_months  = gleft_months - 1,
       left_gdeposit = left_gdeposit - TO_NUMBER(:TRANS_FEE)
 WHERE USER_ID = TO_NUMBER(:USER_ID)
   AND para_code = :PARA_CODE
   AND process_tag = '0'