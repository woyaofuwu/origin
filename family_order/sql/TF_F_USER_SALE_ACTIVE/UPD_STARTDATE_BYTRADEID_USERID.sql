UPDATE TF_F_USER_SALE_ACTIVE
   SET start_date=to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   
 WHERE user_id =to_number(:USER_ID) 
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) 
   and RELATION_TRADE_ID=:RELATION_TRADE_ID 