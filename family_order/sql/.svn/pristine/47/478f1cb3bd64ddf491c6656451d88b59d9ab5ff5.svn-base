Insert Into Tf_b_Tradefee_Sub
  (Trade_Id, Accept_Month, User_Id, Fee_Mode, Fee_Type_Code, Oldfee, Fee)
  Select To_Number(:trade_Id), :accept_Month, :user_Id, '0', 10, 0, 0
    From Tf_b_Trade_Res
   Where Trade_Id = To_Number(:trade_Id)
     And Accept_Month = :accept_Month
     And User_Id = To_Number(:user_Id)
     And Res_Type_Code = '1' --SIM卡
     And Modify_Tag = '0' --新增
     And Not Exists (Select 1
            From Tf_b_Tradefee_Sub
           Where Trade_Id = To_Number(:trade_Id)
             And Accept_Month = :accept_Month
             And User_Id = To_Number(:user_Id)
             And Fee_Mode = '0'
             And Fee_Type_Code = 10)