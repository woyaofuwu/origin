--IS_CACHE=Y
SELECT relation_type_code,role_code_a,role_code_b,role_a,role_b 
  FROM td_s_relation_role
 WHERE relation_type_code=:RELATION_TYPE_CODE