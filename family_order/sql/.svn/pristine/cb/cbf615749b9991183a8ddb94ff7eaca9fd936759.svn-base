Select a.Service_Id, To_Char(a.End_Date, 'yyyy-mm-dd hh24:mi:ss') End_Date
  From Tf_f_User_Svc a
 Where a.Partition_Id = Mod(:USER_ID, 10000)
   And a.User_Id = :USER_ID
   And a.Service_Id In (15, 19)
   And a.End_Date > Sysdate - 15