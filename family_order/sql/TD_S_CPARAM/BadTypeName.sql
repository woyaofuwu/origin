--IS_CACHE=Y
SELECT 'BadTypeName' KEY,res_type_code VALUE1,para_code1 VALUE2,para_code2 VALUE3,para_name VRESULT 
  FROM td_s_resapply_para
 WHERE 'BadTypeName'=:KEY