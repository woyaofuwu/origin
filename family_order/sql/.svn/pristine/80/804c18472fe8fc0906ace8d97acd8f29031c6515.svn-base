SELECT count(1) RECORDCOUNT
  FROM TF_F_USER_PLATSVC a
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN start_date AND end_date
   AND biz_state_code = 'A' --正常
  
   AND EXISTS (SELECT 1 FROM TF_F_USER_PLATSVC
                WHERE user_id = a.user_id
                  AND partition_id = a.partition_id
                  AND SYSDATE BETWEEN start_date AND end_date
                  AND biz_state_code = 'A') --正常