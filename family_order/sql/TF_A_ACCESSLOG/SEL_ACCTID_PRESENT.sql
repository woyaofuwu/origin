SELECT deposit_code,to_char(SUM(money)) money 
  FROM tf_a_accesslog a, td_a_depositpriorrule  b
 WHERE
   acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id>=:START_PARTITION_ID  
   AND partition_id<=:END_PARTITION_ID
   AND access_tag='0'
   AND cancel_tag='0'
   AND operate_time>=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND operate_time<=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND a.deposit_code=b.deposit_code
   AND b.present_tag='1'