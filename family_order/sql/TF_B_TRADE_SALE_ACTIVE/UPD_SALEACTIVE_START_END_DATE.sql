UPDATE TF_B_TRADE_SALE_ACTIVE  
 SET start_date=to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ,end_date=to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE  TRADE_ID=:TRADE_ID 
   and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))  
   and user_id =to_number(:USER_ID) 