UPDATE TF_F_USER_SALE_DEPOSIT
   SET end_date=to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  ,remark ='业务返销'
 WHERE  RELATION_TRADE_ID=:RELATION_TRADE_ID
  And end_date+0 > Sysdate