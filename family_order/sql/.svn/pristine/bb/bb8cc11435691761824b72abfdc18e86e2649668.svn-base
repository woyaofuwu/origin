Update Tf_f_User_SaleActive_book a
   Set a.Deal_State_Code  = '2',
       a.Accept_Trade_Id  = :TRADE_ID,
       a.Update_Time      = Sysdate,
       a.Update_Staff_Id  = :STAFF_ID,
       a.Update_Depart_Id = :DEPART_ID
 Where 1 = 1
   And a.Serial_Number = :SERIAL_NUMBER
   And a.Relation_Trade_Id = :RELATION_TRADE_ID
   AND a.book_type = '0'