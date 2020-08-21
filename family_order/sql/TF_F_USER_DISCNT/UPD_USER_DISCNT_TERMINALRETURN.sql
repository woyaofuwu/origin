Update tf_f_user_discnt a
   Set a.start_Date  = add_months(a.start_Date, TO_NUMBER(:END_OFFDET)),
       a.end_date    = add_months(a.end_date, TO_NUMBER(:END_OFFDET)),
       a.update_time = SYSDATE,
       a.remark      = '终端集采退货优惠前移[' || :TRADE_ID || ']'
 Where a.partition_id = Mod(:USER_ID, 10000)
   And a.user_id = :USER_ID
   And a.end_date > Sysdate
   And Exists (Select 1
          From Tf_b_Trade_Discnt b
         Where b.trade_id = :RELATION_TRADE_ID
           And b.discnt_code = a.discnt_code
           And b.inst_id = a.inst_id
           And b.modify_tag = '0')