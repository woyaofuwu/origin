BEGIN
DELETE FROM tf_f_user_discnt a
WHERE user_id=TO_NUMBER(:USER_ID)
AND  partition_id=TO_NUMBER(:PARTITION_ID)
AND user_id_a+0=TO_NUMBER(:USER_ID_A)
AND EXISTS
(SELECT 1
 FROM tf_f_user_discnt
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID_A),10000)
   AND user_id=TO_NUMBER(:USER_ID_A)
   AND end_date > SYSDATE
AND start_date=a.start_date
   AND discnt_code+0=a.discnt_code);
INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time) 
SELECT TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),discnt_code,'2',:RELATION_TYPE_CODE,trunc(sysdate),end_date,sysdate
  FROM tf_f_user_discnt
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID_A),10000)
   AND user_id=TO_NUMBER(:USER_ID_A)
   AND end_date > sysdate;
END;