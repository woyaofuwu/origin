SELECT A.USER_ID,A.VPN_NAME,A.VPN_NO,A.CUST_MANAGER
 FROM TF_F_USER_VPN A, TF_F_RELATION_UU C
 Where 1=1
 AND C.Serial_Number_b =:SERIAL_NUMBER
 AND (c.End_Date+30) > to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss')
 AND A.USER_ID = c.USER_ID_A
 AND A.Partition_Id = Mod(c.User_Id_a, 10000)
 AND C.Relation_Type_Code = '20'
 Order BY C.End_Date DESC