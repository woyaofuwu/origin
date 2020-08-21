UPDATE tf_a_payrelation a
SET end_cycle_id=to_char(add_months(to_date(:END_CYCLE_ID,'yyyymmdd'),-1),'yyyymmdd'),update_time=sysdate
WHERE user_id=TO_NUMBER(:USER_ID)
  AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
  AND start_cycle_id<=end_cycle_id
  AND end_cycle_id>=:END_CYCLE_ID
  AND exists (SELECT 1 from TF_F_USER_SPECIALEPAY
               WHERE user_id=TO_NUMBER(:USER_ID)
                 AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
                 AND acct_id=a.acct_id
                 AND nvl(rsrv_str1,'*')='0'
                 AND payitem_code=a.payitem_code
                 AND start_cycle_id<=end_cycle_id
                 AND end_cycle_id>=:END_CYCLE_ID)