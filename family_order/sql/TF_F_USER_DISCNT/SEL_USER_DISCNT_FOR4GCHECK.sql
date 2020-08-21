Select Partition_Id,
       To_Char(User_Id) User_Id,
       To_Char(User_Id_a) User_Id_a,
       Discnt_Code,
       Spec_Tag,
       Relation_Type_Code,
       To_Char(Inst_Id) Inst_Id,
       To_Char(Campn_Id) Campn_Id,
       To_Char(Start_Date, 'yyyy-mm-dd hh24:mi:ss') Start_Date,
       To_Char(End_Date, 'yyyy-mm-dd hh24:mi:ss') End_Date,
       To_Char(Update_Time, 'yyyy-mm-dd hh24:mi:ss') Update_Time,
       Update_Staff_Id,
       Update_Depart_Id,
       Remark,
       Rsrv_Num1,
       Rsrv_Num2,
       Rsrv_Num3,
       To_Char(Rsrv_Num4) Rsrv_Num4,
       To_Char(Rsrv_Num5) Rsrv_Num5,
       Rsrv_Str1,
       Rsrv_Str2,
       Rsrv_Str3,
       Rsrv_Str4,
       Rsrv_Str5,
       To_Char(Rsrv_Date1, 'yyyy-mm-dd hh24:mi:ss') Rsrv_Date1,
       To_Char(Rsrv_Date2, 'yyyy-mm-dd hh24:mi:ss') Rsrv_Date2,
       To_Char(Rsrv_Date3, 'yyyy-mm-dd hh24:mi:ss') Rsrv_Date3,
       Rsrv_Tag1,
       Rsrv_Tag2,
       Rsrv_Tag3
  From Tf_f_User_Discnt a
 Where a.Partition_Id = Mod(:USER_ID, 10000)
   And a.User_Id = :USER_ID
   And a.End_Date > Sysdate
   And Exists
 (Select 1
          From Td_s_Commpara b
         Where b.Subsys_Code = 'CSM'
           And b.Param_Attr = 8550
           And b.Param_Code = '4G'
           And b.Para_Code1 = a.Discnt_Code
           And Sysdate Between b.Start_Date And b.End_Date)
