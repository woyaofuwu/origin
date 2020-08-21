--IS_CACHE=Y
SELECT
param_code state_para_code,param_name state_param_name,
0 param_attr
  FROM td_s_commpara
 WHERE param_attr = TO_NUMBER(:STATE_PARA_CODE)
   AND subsys_code = 'CSM'