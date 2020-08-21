INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time)
SELECT TO_NUMBER(:PARTITION_ID),to_number(:USER_ID_A),a.service_id,a.main_tag,a.state_code,a.start_date,a.end_date,SYSDATE
  from tf_f_user_svcstate a
 where a.user_id = to_number(:USER_ID_B)
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   --and a.start_date <= sysdate
   and a.end_date >= sysdate