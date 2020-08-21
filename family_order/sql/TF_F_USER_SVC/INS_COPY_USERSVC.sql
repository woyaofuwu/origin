INSERT INTO tf_f_user_svc (partition_id,user_id,service_id,main_tag,start_date,end_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,update_time)
SELECT MOD(TO_NUMBER(:USER_ID_A),10000),TO_NUMBER(:USER_ID_A),a.service_id,a.main_tag,a.start_date,a.end_date,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,sysdate
 FROM tf_f_user_svc a
 WHERE a.user_id = to_number(:USER_ID_B)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND a.end_date >= sysdate