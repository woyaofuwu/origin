--IS_CACHE=Y
SELECT acyc_id, bcyc_id, to_char(acyc_start_time,'yyyy-mm-dd')
 acyc_start_time, to_char(acyc_end_time - 1, 'yyyy-mm-dd') acyc_end_time ,use_tag    
FROM td_a_acycpara
where acyc_id=:ACYC_ID