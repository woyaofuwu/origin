UPDATE TF_F_USER_SALE_GOODS 
   Set goods_state=:GOODS_STATE  ,cancel_date =Sysdate ,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  ,remark ='业务返销'
 WHERE user_id =to_number(:USER_ID) 
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)   
   And RELATION_TRADE_ID=:RELATION_TRADE_ID