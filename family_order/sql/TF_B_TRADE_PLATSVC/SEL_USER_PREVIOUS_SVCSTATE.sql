Select State_Code
  From Tf_b_Trade_Svcstate_Bak
 Where Main_Tag = '1'
   And Service_Id = '0'
   And Sysdate Between Start_Date And End_Date
   And Trade_Id = To_Number(:TRADE_ID)
   And Accept_Month = To_Number(Substr(:TRADE_ID, 5, 2))
   And User_Id = To_Number(:USER_ID)