Select u.Serial_Number,
       u.Brand_Code,
       u.Product_Id,
       To_Char(u.Open_Date, 'YYYY-MM-DD HH24:MI:SS') Open_Date,
       Nvl(Uc.City_Code, u.City_Code) City_Code,
       c.Cust_Name,
       v.Vip_Class_Id,
       v.Vip_Type_Code,
       v.Cust_Manager_Id,
       '否' Is_Succ,
       Decode(Substr(Ns.Result_Info, 1, 12),
              'APPEXCEPTION',
              '系统错误',
              Ns.Result_Info) Result_Message,
       Ns.Create_Time Accept_Date,
       Decode(Ns.Rsrv_Str1, '', '09998980', Ns.Rsrv_Str1) Port_In_Netid,
       u.Cust_Id User_Cust_Id,
       Cg.User_Id Cust_User_Id,
       Uu.User_Id_a,
       To_Char(ns.create_time, 'YYYY-MM-DD HH24:MI:SS') APPLY_DATE,
       ns.Rsrv_Str4 NP_REASON,
       ns.Rsrv_Str5 REMARK1,
       ns.Rsrv_Str6 NP_MEASURE,
       ns.Rsrv_Str7 REMARK2
  From Tl_b_Nptrade_Soa      Ns,
       Tf_f_User             u,
       Tf_f_Customer         c,
       Tf_f_User_City        Uc,
       Tf_f_Cust_Vip         v,
       Tf_f_Cust_Groupmember Cg,
       Tf_f_Relation_Uu      Uu
 Where Ns.Command_Code = 'APPLY_REQ'
   And Uc.User_Id(+) = u.User_Id
   And Uc.End_Date(+) > Sysdate
   And c.Cust_Id = u.Cust_Id
   And c.Partition_Id = Mod(u.Cust_Id, 10000)
   and u.User_Id = Ns.User_Id
   And u.Partition_Id = Mod(Ns.User_Id, 10000)
   AND v.Cust_Id(+) = u.Cust_Id
   And v.Remove_Tag(+) = '0'
   and (Nvl(v.vip_class_id, 'n') != '1' or Nvl(v.vip_type_code, 'n') != '0')
   And Cg.Member_Cust_Id(+) = u.Cust_Id
   AND Cg.Remove_Tag(+) = '0'
   And Uu.Relation_Type_Code(+) = '20'
   And Uu.End_Date(+) > Sysdate
   AND Uu.User_Id_b(+) = u.User_Id
   And Uu.Partition_Id(+) = Mod(u.User_Id, 10000)
   AND u.SERIAL_NUMBER = :SERIAL_NUMBER
   AND Ns.CREATE_TIME >=
       TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (Ns.CREATE_TIME <
       TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1 OR
       :END_DATE IS NULL)
   and ns.month = :MONTH
   AND (NVL(uc.city_code, u.city_code) = :AREA_CODE OR
       'HAIN' = :AREA_CODE)
 ORDER BY Ns.CREATE_TIME Desc