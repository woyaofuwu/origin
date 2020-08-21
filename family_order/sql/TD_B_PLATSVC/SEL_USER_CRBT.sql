select * from TF_F_USER_SVC a Where a.partition_id = MOD(TO_NUMBER(:USER_ID),10000) 
 and a.service_id=20 
 and a.user_id = :USER_ID  
 and Sysdate between a.start_date and a.end_date