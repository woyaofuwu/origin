UPDATE tf_f_user_otherserv 
SET end_date = SYSDATE ,process_tag='1',rsrv_date1 = SYSDATE,staff_id = :STAFF_ID,depart_id = :DEPART_ID
WHERE user_id=:USER_ID AND partition_id=MOD(:USER_ID,10000) AND service_mode='2' AND process_tag='0'
AND SYSDATE<end_date