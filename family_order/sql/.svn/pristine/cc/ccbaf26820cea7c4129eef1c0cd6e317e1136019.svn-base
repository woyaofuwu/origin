Update tf_o_addcredit_user a
   Set a.end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'), a.update_time = sysdate
 where a.user_id = TO_NUMBER(:USER_ID)
   And a.end_date > Sysdate
   And a.remark Like '%' || :RELATION_TRADE_ID || '%'