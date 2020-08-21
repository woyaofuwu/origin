Select DECODE(b.param_code, '', 'AUTH', 'NOTAUTH') AUTH_CODE, c.*
  From (select *
          From Tf_f_Terminalorder a
         Where a.Serial_Number = :SERIAL_NUMBER
           And a.Rsrv_Str2 = '0'
           And a.End_Time > Sysdate) c
  left join Td_s_Commpara b on b.Subsys_Code = 'CSM'
                           And b.Param_Attr = 1642
                           And b.Param_Code = c.Order_Type
                           And Sysdate Between b.Start_Date And b.End_Date