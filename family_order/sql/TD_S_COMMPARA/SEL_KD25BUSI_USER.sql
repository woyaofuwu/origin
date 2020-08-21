Select c.User_Id,
       c.Serial_Number,
       To_Char(c.Open_Date, 'yyyy-mm-dd hh24:mi:ss') open_Date
  From Tf_f_User a, Tf_f_User c
 Where a.Partition_Id = Mod(:USER_ID, 10000)
   And a.User_Id = :USER_ID
   And c.Serial_Number = 'KD_' || a.Serial_Number
   And a.Remove_Tag = '0'
   And c.Remove_Tag = '0'
   And Trunc(Sysdate, 'mm') = Trunc(c.Open_Date, 'mm')
   And To_Number(To_Char(c.Open_Date, 'DD')) >= 25
   And Exists
 (Select 1
          From Td_s_Commpara b
         Where b.Subsys_Code = 'CSM'
           And b.Param_Attr = 1024
           And b.Param_Code = :ELEMENT_TYPE_CODE
           And b.Para_Code1 = :PRODUCT_ID
           And Sysdate Between b.Start_Date And b.End_Date)