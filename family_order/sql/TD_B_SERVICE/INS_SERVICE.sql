INSERT INTO td_b_service(service_id,service_name,intf_mode,start_date,end_date,remark,update_staff_id,update_depart_id,update_time)
SELECT :SERVICE_ID,:SERVICE_NAME,:INTF_MODE,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),:REMARK,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,SYSDATE
  FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM td_b_service WHERE service_id = :SERVICE_ID)