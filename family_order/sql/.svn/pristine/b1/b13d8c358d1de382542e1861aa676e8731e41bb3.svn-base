SELECT PARA_CODE1
  FROM td_s_commpara
 WHERE param_attr = TO_NUMBER(:PARAM_ATTR)
   AND subsys_code = :SUBSYS_CODE
   AND sysdate BETWEEN start_date AND end_date 
   AND param_code in (SELECT SERVICE_ID
                        FROM TF_F_USER_SVC
                       where user_id = TO_NUMBER(:USER_ID)
                         AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                         AND end_date > SYSDATE
                         AND START_DATE < SYSDATE
                         AND MAIN_TAG = :MAIN_TAG)