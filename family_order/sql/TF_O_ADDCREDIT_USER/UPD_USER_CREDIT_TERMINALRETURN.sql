Update tf_o_addcredit_user a
   Set a.start_Date  = add_months(a.start_Date, TO_NUMBER(:END_OFFDET)),
       a.end_date    = add_months(a.end_date, TO_NUMBER(:END_OFFDET)),
       a.update_time = sysdate
 where a.user_id = TO_NUMBER(:USER_ID)
   And a.end_date > Sysdate
   And a.remark Like '%' || :RELATION_TRADE_ID || '%'