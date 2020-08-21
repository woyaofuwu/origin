Select Decode(T2.White_Type, '1', '黑名单', '0', '白名单') White_Type,

       T2.White_Serinum,

       T2.Eparchy_Code,

       T2.Join_Reason,

       T2.Start_Time,

       T2.End_Time,

       T2.Remark,

       T2.Update_Time,

       T2.Update_Staff,

       T2.Update_Depart,

       T2.Rsrv_Str1,

       T2.Rsrv_Str2,

       T2.Rsrv_Str3,

       0 x_Tag

  From Tf_f_User_Svc@Tobosscrm1 T1,

       (Select a.*, b.User_Id

          From Td_o_Whiteuser_Sms a, Tf_f_User@Tobosscrm1 b

         Where b.Serial_Number = a.White_Serinum

           And b.Remove_Tag = '0'

           And a.Start_Time >=

               To_Date(:start_Date, 'yyyy-mm-dd hh24:mi:ss')) T2

 Where T1.User_Id = T2.User_Id

   And T1.Service_Id = '5'

   And T1.End_Date < Sysdate

   And T1.Start_Date >= (Select Max(t.Start_Date)

                           From Tf_f_User_Svc@Tobosscrm1 t

                          Where t.User_Id = T2.User_Id

                            And T1.Service_Id = '5')

Union All

Select Decode(T2.White_Type, '1', '黑名单', '0', '白名单') White_Type,

       T2.White_Serinum,

       T2.Eparchy_Code,

       T2.Join_Reason,

       T2.Start_Time,

       T2.End_Time,

       T2.Remark,

       T2.Update_Time,

       T2.Update_Staff,

       T2.Update_Depart,

       T2.Rsrv_Str1,

       T2.Rsrv_Str2,

       T2.Rsrv_Str3,

       0 x_Tag

  From Tf_f_User_Svc@Tobosscrm2 T1,

       (Select a.*, b.User_Id

          From Td_o_Whiteuser_Sms a, Tf_f_User@Tobosscrm2 b

         Where b.Serial_Number = a.White_Serinum

           And b.Remove_Tag = '0'

           And a.Start_Time >=

               To_Date(:start_Date, 'yyyy-mm-dd hh24:mi:ss')) T2

 Where T1.User_Id = T2.User_Id

   And T1.Service_Id = '5'

   And T1.End_Date < Sysdate

   And T1.Start_Date >= (Select Max(t.Start_Date)

                           From Tf_f_User_Svc@Tobosscrm2 t

                          Where t.User_Id = T2.User_Id

                            And T1.Service_Id = '5')

Union All

Select Decode(T2.White_Type, '1', '黑名单', '0', '白名单') White_Type,

       T2.White_Serinum,

       T2.Eparchy_Code,

       T2.Join_Reason,

       T2.Start_Time,

       T2.End_Time,

       T2.Remark,

       T2.Update_Time,

       T2.Update_Staff,

       T2.Update_Depart,

       T2.Rsrv_Str1,

       T2.Rsrv_Str2,

       T2.Rsrv_Str3,

       0 x_Tag

  From Tf_f_User_Svc@Tobosscrm4 T1,

       (Select a.*, b.User_Id

          From Td_o_Whiteuser_Sms a, Tf_f_User@Tobosscrm4 b

         Where b.Serial_Number = a.White_Serinum

           And b.Remove_Tag = '0'

           And a.Start_Time >=

               To_Date(:start_Date, 'yyyy-mm-dd hh24:mi:ss')) T2

 Where T1.User_Id = T2.User_Id

   And T1.Service_Id = '5'

   And T1.End_Date < Sysdate

   And T1.Start_Date >= (Select Max(t.Start_Date)

                           From Tf_f_User_Svc@Tobosscrm4 t

                          Where t.User_Id = T2.User_Id

                            And T1.Service_Id = '5')