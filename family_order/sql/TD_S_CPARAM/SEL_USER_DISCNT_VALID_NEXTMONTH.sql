Select Count(1) Recordcount
  From Tf_f_User_Discnt a
 Where a.Partition_Id = Mod(To_Number(:VUSER_ID), 10000)
   And a.User_Id = To_Number(:VUSER_ID)
   And a.Discnt_Code = To_Number(:VDISCNT_CODE)
   And a.End_Date > Trunc(Last_Day(Sysdate) + 1) - 1 / 24 / 60 / 60