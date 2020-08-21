--IS_CACHE=Y
Select Act_Type,
       Pay_Fee_Mode,
       Code1,
       Name1,
       Code2,
       Name2,
       Code3,
       Name3,
       Act_Flag,
       Remark,
       Update_Time,
       Update_Staff_Id,
       Update_Depart_Id
  From Td_Sd_Payfeemode a
 Where a.Fee_Type_Code = '0'
   And a.Pay_Fee_Mode_Code = :PAY_MONEY_CODE