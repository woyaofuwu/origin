Select a.Cust_Name, b.Detail_Address
  From Tf_b_Trade a, Tf_b_Trade_Widenet b
 Where a.Trade_Id = b.Trade_Id
   And a.Trade_Id = :Trade_Id
   And b.modify_tag = '0'
 Order By a.Accept_Date Desc