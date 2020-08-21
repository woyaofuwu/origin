select trade_id,Discnt_Code from TF_B_TRADE_DISCNT t 
  where 1=1
   and t.user_id =:USER_ID
   and t.accept_month= TO_NUMBER(to_char(sysdate,'mm')) 
   and t.start_date>=   trunc(sysdate,'month')