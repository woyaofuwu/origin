SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8 
  FROM tf_f_user_svc
 WHERE (partition_id=MOD(:USER_ID, 10000) OR :USER_ID IS NULL)
   AND (user_id=TO_NUMBER(:USER_ID) OR :USER_ID IS NULL)
   AND (service_id=:SERVICE_ID OR :SERVICE_ID IS NULL)
   AND (start_date<=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :START_DATE IS NULL)
   AND (end_date>=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') OR :END_DATE IS NULL)
   AND (serv_para1=:SERV_PARA1 OR :SERV_PARA1 IS NULL)