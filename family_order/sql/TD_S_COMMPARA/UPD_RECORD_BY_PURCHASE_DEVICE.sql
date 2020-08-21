UPDATE td_s_commpara SET para_code5 = to_char(to_number(para_code5)+1)
 WHERE param_attr=:PARAM_ATTR
  AND param_code=:PARAM_CODE
  AND para_code1 = :PARA_CODE1
  AND subsys_code=:SUBSYS_CODE
  AND (EPARCHY_CODE=:EPARCHY_CODE OR :EPARCHY_CODE='ZZZZ')
  AND SYSDATE BETWEEN start_date AND end_date