Select A.Trade_Id,
       A.Serial_Number,
       A.Cust_Name,
       A.User_Id,
       A.Trade_Staff_Id,
       to_char(A.Accept_Date, 'yyyy-mm-dd hh24:mi:ss') as Accept_Date,
       decode(B.Rsrv_Value,'1','退订','2','变更','其他') as Rsrv_Value,
       B.Rsrv_Str1,
       B.Rsrv_Str2,
       B.Rsrv_Str3,
       B.Rsrv_Str4,
       B.Rsrv_Str5,
       B.Rsrv_Str6,
       B.Rsrv_Str7,
       B.Rsrv_Str8,
       B.Rsrv_Str9/100 as Rsrv_Str9,
       B.Rsrv_Str10
  From tf_bh_trade A, tf_f_user_other B
 Where B.rsrv_value_code = 'CAMPUS_TRANS_FEE'
   AND A.Trade_Id = B.Trade_Id
   AND A.User_Id = B.User_Id
   AND TRIM(A.TRADE_STAFF_ID) >= :START_STAFF_ID
   AND TRIM(A.TRADE_STAFF_ID) <= :END_STAFF_ID
   AND A.ACCEPT_DATE >= TO_DATE(:START_DATE, 'yyyy-mm-dd')
   AND A.ACCEPT_DATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd') + 1 - 0.00001
   AND B.Rsrv_Value = :TRANS_TYPE
   AND B.rsrv_str7 = :CAMPUS_ID
   AND A.Cancel_Tag = '0'