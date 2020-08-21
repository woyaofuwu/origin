--IS_CACHE=Y
SELECT org_param_code,org_param_code1,org_param_code2,org_param_code3,org_param_code4,org_param_code5, 
       home_param_code,home_param_code1,home_param_code2,home_param_code3,home_param_code4,home_param_code5 
  FROM td_b_convert
 WHERE eparchy_code=:EPARCHY_CODE
   AND config_domain=:CONFIG_DOMAIN
   AND config_type=:CONFIG_TYPE
   AND config_attr=:CONFIG_ATTR
   AND org_param_code=:ORG_PARAM_CODE