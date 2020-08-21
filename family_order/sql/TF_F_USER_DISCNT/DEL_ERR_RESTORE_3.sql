Delete From Tf_f_User_Discnt a
 Where User_Id = To_Number(:user_Id)
   And Partition_Id = Mod(To_Number(:user_Id), 10000)
   And Exists
 (Select 1
          From Tf_b_Trade_Discnt b
         Where b.Trade_Id = To_Number(:trade_Id)
           And b.Accept_Month = To_Number(Substr(:trade_Id, 5, 2))
           And b.User_Id = a.User_Id
           And b.Product_Id = a.Product_Id
           And b.Package_Id = a.Package_Id
           And b.Discnt_Code = a.Discnt_Code
           And b.Start_Date = a.Start_Date)