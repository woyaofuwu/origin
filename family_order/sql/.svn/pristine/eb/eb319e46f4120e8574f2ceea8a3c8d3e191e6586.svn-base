Insert INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time)
SELECT TO_NUMBER(:PARTITION_ID),user_id,service_id,main_tag,'0',Sysdate,max(end_date),SYSDATE
FROM tf_f_user_svc a
WHERE user_id=to_number(:USER_ID)
  AND partition_id=TO_NUMBER(:PARTITION_ID)  
  AND end_date>Sysdate 
  AND NOT EXISTS(SELECT 1 FROM tf_f_user_svcstate 
                      WHERE user_id=:USER_ID    
                      AND partition_id=TO_NUMBER(:PARTITION_ID)
                      AND service_id+0=a.service_id
                      AND end_date+0 > sysdate)
  AND EXISTS(SELECT 1 FROM TD_S_SERVICESTATE WHERE service_id=a.service_id
                      AND a.start_date BETWEEN start_date AND end_date) 
  Group By user_id,service_id ,main_tag