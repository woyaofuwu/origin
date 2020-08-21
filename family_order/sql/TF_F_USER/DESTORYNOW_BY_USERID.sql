Update Tf_f_User
   Set Remove_Tag          = :remove_Tag,
       Destroy_Time        = Sysdate,
       Update_Time         = Sysdate,
       Remove_Eparchy_Code = :remove_Eparchy_Code,
       Remove_City_Code    = :remove_City_Code,
       Remove_Depart_Id    = :remove_Depart_Id,
       Remove_Reason_Code  = :remove_Reason_Code,
       Remark              = :remark,
       Score_Value         = 0
 Where User_Id = To_Number(:user_Id)
   And Partition_Id = Mod(To_Number(:user_Id), 10000)