Select a.product_id,a.product_name,a.package_id,a.package_name
  From Tf_f_User_Saleactive_Book a, Td_s_Commpara b
 Where a.Product_Id = To_Number(b.Para_Code1)
   And a.Package_Id = To_Number(Nvl(b.Para_Code2, a.Package_Id))
   And a.Partition_Id = Mod(to_number(:USER_ID), 10000)
   And a.User_Id = to_number(:USER_ID)
   And b.Subsys_Code = 'CSM'
   And b.Param_Attr = 1558
   And (b.Param_Code = '-1' Or b.Param_Code = :TRADE_TYPE_CODE)
   And a.End_Date > Sysdate
   And a.Deal_State_Code = '0'
   And a.Process_Tag = '0'
   And Sysdate Between b.Start_Date And b.End_Date