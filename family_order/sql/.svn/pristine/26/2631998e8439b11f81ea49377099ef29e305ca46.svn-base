Insert Into Tf_b_Tradefee_Defer
  (Trade_Id,
   Accept_Month,
   User_Id,
   Money,
   Defer_Cycle_Id,
   Defer_Item_Code,
   Fee_Mode,
   Fee_Type_Code,
   Act_Tag)
  Select To_Number(:trade_Id),
         :accept_Month,
         To_Number(:user_Id),
         Money,
         to_Number(to_Char(Sysdate,'yyyymm')),
         Null,
         '0',
         '0',
         '1'
    From Tf_b_Tradefee_Paymoney
   Where Trade_Id = To_Number(:trade_Id)
     And Accept_Month = :accept_Month
     And Pay_Money_Code = 'A'