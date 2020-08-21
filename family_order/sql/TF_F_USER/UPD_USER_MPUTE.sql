UPDATE tf_f_user
   SET mpute_month_fee=:MPUTE_MONTH_FEE,mpute_date=TO_DATE(:MPUTE_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)