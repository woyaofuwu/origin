UPDATE TF_F_USER_SALE_DEPOSIT
   SET end_date=to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID   ,remark ='业务取销'
 WHERE user_id =to_number(:USER_ID) 
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)   
   And RELATION_TRADE_ID=:RELATION_TRADE_ID
   And ( DISCNT_GIFT_ID =:DISCNT_GIFT_ID   Or :DISCNT_GIFT_ID  Is Null )
   And (inst_id =:INST_ID    Or :INST_ID  Is Null )
   AND end_date+0 > Sysdate