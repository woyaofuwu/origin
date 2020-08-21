Select Subsys_Code,
       Param_Attr,
       Param_Code,
       Param_Name,
       Para_Code1,
       Para_Code2,
       Para_Code3,
       Para_Code4,
       Para_Code5,
       Para_Code6,
       Para_Code7,
       Para_Code8,
       Para_Code9,
       Para_Code10,
       Para_Code11,
       Para_Code12,
       Para_Code13,
       Para_Code14,
       Para_Code15,
       Para_Code16,
       Para_Code17,
       Para_Code18,
       Para_Code19,
       Para_Code20,
       Para_Code21,
       Para_Code22,
       Para_Code23,
       Para_Code24,
       Para_Code25,
       To_Char(Para_Code26, 'yyyy-mm-dd hh24:mi:ss') Para_Code26,
       To_Char(Para_Code27, 'yyyy-mm-dd hh24:mi:ss') Para_Code27,
       To_Char(Para_Code28, 'yyyy-mm-dd hh24:mi:ss') Para_Code28,
       To_Char(Para_Code29, 'yyyy-mm-dd hh24:mi:ss') Para_Code29,
       To_Char(Para_Code30, 'yyyy-mm-dd hh24:mi:ss') Para_Code30,
       To_Char(Start_Date, 'yyyy-mm-dd hh24:mi:ss') Start_Date,
       To_Char(End_Date, 'yyyy-mm-dd hh24:mi:ss') End_Date,
       Eparchy_Code,
       Remark,
       Update_Staff_Id,
       Update_Depart_Id,
       To_Char(Update_Time, 'yyyy-mm-dd hh24:mi:ss') Update_Time
  From Td_s_Commpara a
 Where a.Subsys_Code = 'CSM'
   And a.Param_Attr = 1559
   And a.Param_Code = :PRODUCT_ID
   And Decode(Para_Code1, '-1', :PACKAGE_ID, Para_Code1) = :PACKAGE_ID
   And Sysdate Between a.Start_Date And a.End_Date
   And Exists (Select 1
          From Tf_b_Trade_Credit a
         Where a.Trade_Id = :TRADE_ID)