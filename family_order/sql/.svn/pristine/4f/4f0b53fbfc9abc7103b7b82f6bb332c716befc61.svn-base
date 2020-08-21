SELECT a.partition_id,
       to_char(a.user_id) user_id,
       a.service_type service_type, 
       a.deal_flag deal_flag,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.update_date,'yyyy-mm-dd hh24:mi:ss') update_date,
       b.serial_number
  FROM TF_F_USER_SVCALLOWANCE a,TF_F_USER b 
 WHERE a.user_id=b.user_id
       and a.user_id = TO_NUMBER(:USER_ID)
       and a.service_type <>:SERVICE_TYPE
       and sysdate between a.start_date and a.end_date