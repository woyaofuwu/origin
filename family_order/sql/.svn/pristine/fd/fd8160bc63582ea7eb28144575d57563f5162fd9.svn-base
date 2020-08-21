Update Tf_f_User_SaleActive_book a
   Set a.Deal_State_Code = '1',
       a.Cancel_Date     = Sysdate,
       a.Cancel_Staff_Id = :STAFF_ID
 Where 1 = 1
   And a.Serial_Number = :SERIAL_NUMBER
   And a.Relation_Trade_Id = :RELATION_TRADE_ID
   and a.book_type = '0'