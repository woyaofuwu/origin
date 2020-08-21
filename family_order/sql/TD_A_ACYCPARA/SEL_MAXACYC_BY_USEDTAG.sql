--IS_CACHE=Y
SELECT nvl(max(acyc_id),0) acyc_id 
FROM td_a_acycpara 
WHERE use_tag = '1'