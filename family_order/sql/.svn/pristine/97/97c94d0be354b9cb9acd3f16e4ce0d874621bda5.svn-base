UPDATE tf_f_user_discnt t
   SET t.end_date = to_date(to_char(Last_day(to_date(SUBSTR(:TRADE_ID, 3, 4),'yymm')),'yyyy-mm-dd')||' 23:59:59','yyyy-mm-dd hh24:mi:ss'),
       t.update_time = sysdate,
       t.update_staff_id = :UPDATE_STAFF_ID,
       t.update_depart_id = :UPDATE_DEPART_ID
 WHERE t.user_id = TO_NUMBER(:USER_ID)
   AND t.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND t.discnt_code in
       (SELECT D.DISCNT_CODE
          FROM TF_B_TRADE_DISCNT D
         WHERE D.TRADE_ID = TO_NUMBER(:TRADE_ID)
           AND D.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND D.USER_ID = TO_NUMBER(:USER_ID))
   AND sysdate between t.start_date and t.end_date