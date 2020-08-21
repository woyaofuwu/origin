--IS_CACHE=Y
SELECT
param_code state_para_code,param_name state_param_name
  FROM td_s_commpara
 WHERE param_attr = TO_NUMBER(:STATE_PARA_CODE)
   AND param_code = :PARAM_CODE
   AND subsys_code = 'CSM'