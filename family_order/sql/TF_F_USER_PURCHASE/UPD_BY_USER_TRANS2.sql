UPDATE tf_f_user_trans
   SET rsrv_info1   = '9',
       rsrv_num1    = :ACYC_ID,
       rsrv_date1   = SYSDATE,
       left_months  = left_months - 1,
       left_deposit = left_deposit - TO_NUMBER(:TRANS_FEE)
 WHERE USER_ID = TO_NUMBER(:USER_ID)
   AND para_code = :PARA_CODE
   AND process_tag = '0'