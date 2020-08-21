Select a.User_Id, a.Serial_Number, a.Product_Id, a.Package_Id
  From Tf_f_User_Sale_New a
 Where a.Partition_Id = Mod(To_Number(:USER_ID), 10000)
   And a.User_Id = To_Number(:USER_ID)
   And a.Rsrv_Str2 = :PAY_CHARGE_ID
   And a.End_Date > Sysdate
   And a.Process_Tag = '0'