INSERT INTO tf_f_user_validchange(partition_id,user_id,start_date,end_date,update_time)
 VALUES(TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),NVL(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),sysdate),NVL(TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),sysdate)+:NUM,SYSDATE)