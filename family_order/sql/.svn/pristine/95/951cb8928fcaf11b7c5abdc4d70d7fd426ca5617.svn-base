Update Tf_f_User
   Set User_Type_Code    = :user_Type_Code,
       Acct_Tag          = :acct_Tag,
       In_Date           = To_Date(:in_Date, 'YYYY-MM-DD HH24:MI:SS'),
       Open_Date         = To_Date(:open_Date, 'YYYY-MM-DD HH24:MI:SS'),
       Open_Mode         = :open_Mode,
       Update_Time       = To_Date(:update_Time, 'YYYY-MM-DD HH24:MI:SS'),
       Develop_Depart_Id = :develop_Depart_Id
 Where Partition_Id = Mod(To_Number(:user_Id), 10000)
   And User_Id = To_Number(:user_Id)