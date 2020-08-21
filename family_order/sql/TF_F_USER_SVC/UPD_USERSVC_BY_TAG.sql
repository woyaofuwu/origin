UPDATE tf_f_user_svc a 
SET (a.end_date,product_id,package_id) =(SELECT b.end_date,product_id,package_id FROM tf_b_trade_svc_bak b
                 WHERE b.trade_id=to_number(:TRADE_ID)
                 AND b.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                 And b.service_id=a.service_id
                 and b.service_id =:SERVICE_ID
                 and a.user_id = b.user_id
                 and b.start_date = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')) ,update_time=Sysdate                                  
WHERE USER_ID=TO_NUMBER(:USER_ID)
AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)
AND a.service_id =:SERVICE_ID
And a.start_date = to_date(:START_DATE ,'yyyy-mm-dd hh24:mi:ss')
And Exists ( SELECT 1 FROM tf_b_trade_svc_bak b
                 WHERE b.trade_id=to_number(:TRADE_ID)
                 AND b.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))   
                 And b.service_id=a.service_id
                 and b.service_id =:SERVICE_ID   
                 And a.user_id = b.user_id
                 and b.start_date = to_date(:START_DATE ,'yyyy-mm-dd hh24:mi:ss'))