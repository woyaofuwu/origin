SELECT COUNT(1) recordcount
  FROM tf_f_user_other
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND RSRV_VALUE_CODE = 'MMCU'
   AND RSRV_VALUE = '5'
   AND SYSDATE - end_date < = 1