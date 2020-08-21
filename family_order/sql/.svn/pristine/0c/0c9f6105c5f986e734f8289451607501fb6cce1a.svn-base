UPDATE TF_F_USER_SALE_GOODS
   SET goods_state='1', rsrv_date2=TO_DATE(:RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss'), accept_date=SYSDATE, cancel_date =Sysdate ,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  ,remark ='预存话费送礼品领取返销'
 WHERE user_id =to_number(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   And RELATION_TRADE_ID=:RELATION_TRADE_ID  
   AND goods_state<>'1'
   AND RSRV_STR1=:RSRV_STR1