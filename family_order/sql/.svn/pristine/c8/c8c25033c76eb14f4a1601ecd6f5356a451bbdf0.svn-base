SELECT a.partition_id,
       to_char(a.user_id) user_id,
       a.service_type service_type, 
       a.deal_flag deal_flag,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.update_date,'yyyy-mm-dd hh24:mi:ss') update_date
  FROM TF_F_USER_SVCALLOWANCE a
 WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
       and a.user_id = TO_NUMBER(:USER_ID)
       and (a.service_type =:SERVICE_TYPE or :SERVICE_TYPE is null)
       and (a.deal_flag =:DEAL_FLAG or :DEAL_FLAG is null)
       and (a.start_date>=TO_DATE(:START_DATE,'yyyymmddhh24miss') or :START_DATE is null)
       and (a.start_date<=TO_DATE(:END_DATE,'yyyymmddhh24miss')+1 or :END_DATE is null)
       and sysdate between a.start_date and a.end_date