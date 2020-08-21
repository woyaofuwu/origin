UPDATE TF_F_USER_SALE_NEW
   SET end_date=to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  , PROCESS_TAG= :PROCESS_TAG ,CANCEL_DATE =to_date(:CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS'), 
   CANCEL_STAFF_ID =:CANCEL_STAFF_ID , CANCEL_CAUSE =:CANCEL_CAUSE   ,remark ='业务返销'  ,update_time=sysdate
 WHERE user_id =to_number(:USER_ID) 
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date>Sysdate
   And RELATION_TRADE_ID=:RELATION_TRADE_ID