--IS_CACHE=Y
SELECT acyc_id,bcyc_id,use_tag
FROM td_a_acycpara
WHERE acyc_start_time <= ADD_MONTHS(SYSDATE,:GET_MODE)
  AND ADD_MONTHS(SYSDATE,:GET_MODE) < acyc_end_time