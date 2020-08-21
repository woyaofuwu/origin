UPDATE TF_B_TRADE_SALE_ACTIVE  
 SET end_date=to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  , PROCESS_TAG= :PROCESS_TAG ,CANCEL_DATE =to_date(:CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS'), 
   CANCEL_STAFF_ID =:CANCEL_STAFF_ID , CANCEL_CAUSE =:CANCEL_CAUSE   ,remark ='业务取销'
 WHERE  TRADE_ID=:RELATION_TRADE_ID 
   and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))  
   and user_id =to_number(:USER_ID)   
   AND end_date > Sysdate      
   And modify_tag ='0'