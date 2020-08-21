DELETE FROM tf_f_user_discnt a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (end_date > SYSDATE or EXISTS
       (SELECT 1 FROM tf_b_trade_discnt_bak
         WHERE trade_id = TO_NUMBER(:TRADE_ID)
           AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
           AND user_id=a.user_id
           AND discnt_code=a.discnt_code
           AND start_date=a.start_date
     AND end_date > SYSDATE))