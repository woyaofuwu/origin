Select b.Para_Code1,
       b.Para_Code23,
       b.Para_Code24,
       Case
         When To_Number(To_Char(a.Start_Date, 'dd')) >= 20 Then
          b.Para_Code24
         Else
          b.Para_Code23
       End Notice_Content
  From Tf_b_Trade_Discnt a, Td_s_Commpara b
 Where a.Discnt_Code = b.Para_Code1
   And a.Trade_Id = :TRADE_ID
   And b.Subsys_Code = 'CSM'
   And b.Param_Attr = 1608
   And b.Param_Code = :PARAM_CODE
   And a.Modify_Tag = '0'
   And Sysdate Between b.Start_Date And b.End_Date