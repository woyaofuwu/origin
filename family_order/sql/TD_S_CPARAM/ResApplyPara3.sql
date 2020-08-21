--IS_CACHE=Y
SELECT 'ResApplyPara3' KEY,eparchy_code VALUE1, para_code1 VALUE2, para_code2 VALUE3,para_name VRESULT FROM td_s_resapply_para
 WHERE 'ResApplyPara3'=:KEY
   AND res_type_code='3'