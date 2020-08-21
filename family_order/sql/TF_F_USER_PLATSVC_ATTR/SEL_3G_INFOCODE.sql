select e.info_value 
       from TF_F_USER_PLATSVC_ATTR e 
       where e.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
       AND e.user_id = :USER_ID
       and e.SERVICE_ID = TO_NUMBER(:SERVICE_ID)
       and e.info_code = :INFO_CODE
       and e.info_value = :INFO_VALUE