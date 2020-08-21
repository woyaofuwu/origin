--IS_CACHE=Y
SELECT count(1) RECORDCOUNT
  FROM td_s_commpara
 WHERE subsys_code = 'BRS' AND param_attr = 272
   AND (eparchy_code = 'ZZZZ' OR eparchy_code = :EPARCHY_CODE)
   AND param_code = :PARAM_CODE
   AND para_code1 = TO_CHAR(:PRODUCT_ID)
   AND SYSDATE BETWEEN start_date AND end_date