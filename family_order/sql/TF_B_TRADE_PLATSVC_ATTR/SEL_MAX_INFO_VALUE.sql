select max(t.info_value) info_value from
       (select e.info_value
       from TF_F_USER_PLATSVC_ATTR e
       where e.user_id = :USER_ID
       AND e.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
       and e.info_code = :INFO_CODE
       and e.SERVICE_ID = TO_NUMBER(:SERVICE_ID)
        
       union all 
       SELECT e.info_value
       from TF_B_TRADE_PLATSVC_ATTR e, TF_B_TRADE_PLATSVC s
       where e.service_id = s.service_id
       and e.trade_id = s.trade_id  and e.trade_id = :TRADE_ID and e.info_code = :INFO_CODE 
       AND e.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) and e.service_id = TO_NUMBER(:SERVICE_ID))t