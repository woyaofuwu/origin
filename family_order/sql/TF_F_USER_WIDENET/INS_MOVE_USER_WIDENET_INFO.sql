Insert Into Tf_f_User_Widenet
	(Partition_Id, User_Id, Stand_Address, Detail_Address, Sign_Path, Port_Type, Inst_Id, Start_Date, End_Date,
	 Update_Time, Update_Staff_Id, Update_Depart_Id, Acct_Passwd, Stand_Address_Code, Phone, Contact, Contact_Phone)
Values
	(To_Number(:partition_Id), To_Number(:user_Id), :stand_Address, :detail_Address, :sign_Path, :port_Type,
	 To_Number(:inst_Id), To_Date(:start_Date, 'yyyy-mm-dd hh24:mi:ss'), To_Date(:end_Date, 'yyyy-mm-dd hh24:mi:ss'),
	 To_Date(:update_Time, 'yyyy-mm-dd hh24:mi:ss'), :update_Staff_Id, :update_Depart_Id, :acct_Passwd,
	 To_Number(:stand_Address_Code), :phone, :contact, :contact_Phone)