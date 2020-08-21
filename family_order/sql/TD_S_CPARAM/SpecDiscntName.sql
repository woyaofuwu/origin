--IS_CACHE=Y
SELECT 'SpecDiscntName' KEY,param_code VALUE1,'-1' VALUE2,param_name VRESULT
FROM td_s_commpara
WHERE 'SpecDiscntName'=:KEY AND param_attr=92