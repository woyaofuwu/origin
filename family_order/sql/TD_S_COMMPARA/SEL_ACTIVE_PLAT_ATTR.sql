--IS_CACHE=Y
Select a.Param_Name, a.Para_Code1, a.Para_Code2
  From Td_s_Commpara a
 Where a.Subsys_Code = 'CSM'
   And a.Param_Attr = 957
   And a.Param_Code = :SERVICE_ID
   And a.Para_Code3 = :PRODUCT_ID
   And Nvl(a.Para_Code4, :PACKAGE_ID) = :PACKAGE_ID
   And Sysdate Between a.Start_Date And a.End_Date