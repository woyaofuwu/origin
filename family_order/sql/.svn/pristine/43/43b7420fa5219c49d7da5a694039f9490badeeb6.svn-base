UPDATE tf_f_user_svc a 
SET (a.end_date,product_id,package_id) =(SELECT b.end_date,product_id,package_id FROM tf_b_trade_svc_bak b
                 WHERE b.trade_id=to_number(:TRADE_ID)
                 AND b.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                 and b.service_id =:SERVICE_ID   
                 And a.inst_id = b.inst_id  
                 And b.inst_id =:INST_ID )   ,update_time=sysdate                                
WHERE USER_ID=TO_NUMBER(:USER_ID)
AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)
AND a.service_id =:SERVICE_ID
And a.inst_id =:INST_ID 
And Exists ( SELECT 1 FROM tf_b_trade_svc_bak b
                 WHERE b.trade_id=to_number(:TRADE_ID)
                 AND b.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                 and b.service_id =:SERVICE_ID   
                 And a.inst_id = b.inst_id  
                 And b.inst_id =:INST_ID )