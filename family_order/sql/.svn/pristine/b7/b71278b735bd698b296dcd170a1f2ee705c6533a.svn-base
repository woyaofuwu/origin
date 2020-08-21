UPDATE tf_f_blacksmsimpeachplat
   SET deal_time=TO_DATE(:DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS'),deal_staff=:DEAL_STAFF,deal_result=:DEAL_RESULT,deal_tag=:DEAL_TAG  
 WHERE serial_number_b=:SERIAL_NUMBER_B
   AND in_time between TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS') and TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND deal_tag='0'