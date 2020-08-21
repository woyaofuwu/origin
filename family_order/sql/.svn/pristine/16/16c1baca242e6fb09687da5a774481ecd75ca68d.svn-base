Select a.param_code,param_name,para_code1
  From Td_s_Commpara a
 Where a.Subsys_Code = 'CSM'
   And a.Param_Attr = 151
   And a.Param_Code = :TRADE_TYPE_CODE
   And a.Para_Code1 = :PRODUCT_ID
   And Sysdate Between a.Start_Date And a.End_Date
   And Exists (Select 1
          From Tf_b_Trade b
         Where b.User_Id = TO_NUMBER(:USER_ID)
           And b.Trade_Type_Code = 110
           And b.Exec_Time > Sysdate
           And Substr(b.Process_Tag_Set, 9, 1) = '4')