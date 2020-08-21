SELECT 'SolicItem' KEY,rsrv_num1 VALUE1,rsrv_num2 VALUE2,rsrv_str1 VRESULT
  FROM td_o_custmgr_commpara
 WHERE 'SolicItem'=:KEY AND para_type='SOLIC' AND rsrv_num1 IS NOT NULL AND rsrv_str1 IS NOT NULL GROUP BY rsrv_num1,rsrv_num2,rsrv_str1