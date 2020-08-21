INSERT INTO tf_b_trade_svc(TRADE_ID,ACCEPT_MONTH,USER_ID,SERVICE_ID,SERV_PARA1,SERV_PARA2,SERV_PARA3,SERV_PARA4,SERV_PARA5,SERV_PARA6,SERV_PARA7,SERV_PARA8,MODIFY_TAG,START_DATE,END_DATE)
SELECT trade_id,accept_month,id,:SERVICE_ID,discnt_code,'','','','','','','','2',sysdate,to_date('20501231','yyyymmdd')
  FROM tf_b_trade_discnt a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag=:MODIFY_TAG
   AND EXISTS(SELECT 1 FROM td_b_discnt b
               WHERE a.discnt_code=b.discnt_code
                 AND b.discnt_type_code=:DISCNT_TYPE_CODE
                 AND SYSDATE BETWEEN start_date AND end_date)