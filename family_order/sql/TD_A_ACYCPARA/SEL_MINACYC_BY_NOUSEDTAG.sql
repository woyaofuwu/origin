--IS_CACHE=Y
SELECT nvl(min(acyc_id),0) acyc_id 
FROM td_a_acycpara 
WHERE use_tag = '0'