Select b.Fee_Code_Rule_Code,
       b.Rsrv_Str5,
       Substr(b.Rsrv_Str5, 0, 8) PARA_CODE1,
       Substr(b.Rsrv_Str5, 10, 8) PARA_CODE2
  From Td_b_Feecodetype b, Tf_r_Mphonecode_Use a
 Where Match_Tag = '0'
   And Valid_Tag = '0'
   And a.Fee_Code_Rule_e = b.Fee_Code_Rule_Code
   And a.Serial_Number = :SERIAL_NUMBER