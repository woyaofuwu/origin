Select Partition_Id,
       To_Char(User_Id) User_Id,
       To_Char(User_Id_a) User_Id_a,
       Discnt_Code,
       Spec_Tag,
       Relation_Type_Code,
       To_Char(Start_Date, 'yyyy-mm-dd hh24:mi:ss') Start_Date,
       To_Char(End_Date, 'yyyy-mm-dd hh24:mi:ss') End_Date
  From Tf_f_User_Discnt
 Where User_Id = To_Number(:user_Id)
   And Partition_Id = Mod(To_Number(:user_Id), 10000)
   And Relation_Type_Code In
       (Select Relation_Type_Code
          From Td_s_Relation t
         Where t.Relation_Kind = 'F') --亲情优惠
   And Sysdate < End_Date