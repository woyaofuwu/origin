UPDATE tf_f_user_validchange
SET end_date=NVL(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),END_DATE+:NUM)
WHERE partition_id=TO_NUMBER(:PARTITION_ID)
  AND user_id=TO_NUMBER(:USER_ID)
  AND sysdate BETWEEN start_date AND end_date
  AND START_DATE= (SELECT MAX(START_DATE) 
                   FROM tf_f_user_validchange 
                   WHERE partition_id=TO_NUMBER(:PARTITION_ID)
                     AND user_id=TO_NUMBER(:USER_ID)   
                     AND sysdate BETWEEN start_date AND end_date
                  )