Select Count(1) Recordcount
  From Tf_f_User_Svc a
 Where a.Partition_Id = Mod(:USER_ID, 10000)
   And a.User_Id = :USER_ID
   And a.Service_Id = :SERVICE_ID
   And Months_Between(Trunc(Sysdate, 'mm'), Trunc(a.End_Date, 'mm')) < 3