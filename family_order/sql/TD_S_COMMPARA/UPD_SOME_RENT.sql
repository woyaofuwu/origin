UPDATE td_s_commpara
   SET para_code3=:PARA_CODE3
 WHERE subsys_code=:SUBSYS_CODE
   AND param_attr=TO_NUMBER(:PARAM_ATTR)
   AND param_code=:PARAM_CODE
   AND sysdate between start_date and end_date
   AND para_code2 =:PARA_CODE2
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code = 'ZZZZ')