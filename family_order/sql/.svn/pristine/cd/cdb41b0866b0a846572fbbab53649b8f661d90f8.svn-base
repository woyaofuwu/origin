Select  u.USER_ID,
		u.Serial_Number,
       P.Brand_Code,
       P.Product_Id,
       To_Char(u.Open_Date, 'YYYY-MM-DD HH24:MI:SS') Open_Date,
       Nvl(Uc.City_Code, u.City_Code) City_Code,
       c.Cust_Name,
       v.Vip_Class_Id,
       v.Vip_Type_Code,
       v.Cust_Manager_Id,
       '·ñ' Is_Succ,
       u.Cust_Id User_Cust_Id,
       u.User_Id User_User_Id,
       Cg.User_Id Cust_User_Id,
       null APPLY_DATE
  From Tf_f_User             u,
       TF_F_USER_PRODUCT     P,
       Tf_f_Customer         c,
       Tf_f_User_City        Uc,
       Tf_f_Cust_Vip         v,
       Tf_f_Cust_Groupmember Cg
 Where P.USER_ID = U.USER_ID
   AND P.PARTITION_ID = MOD(U.USER_ID, 10000)
   AND P.MAIN_TAG = '1'
   And c.Cust_Id = u.Cust_Id
   And c.Partition_Id = Mod(u.Cust_Id, 10000)
   And v.Cust_Id(+) = u.Cust_Id
   And v.Remove_Tag(+) = '0'
   And Cg.Member_Cust_Id(+) = c.Cust_Id
   And Cg.Remove_Tag(+) = '0'
   And Uc.User_Id(+) = u.User_Id
   And Uc.End_Date(+) > Sysdate
   AND u.SERIAL_NUMBER = :SERIAL_NUMBER
   AND u.USER_ID = :USER_ID