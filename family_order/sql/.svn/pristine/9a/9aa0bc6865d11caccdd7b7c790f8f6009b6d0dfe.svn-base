--IS_CACHE=Y
SELECT subsys_code,
       param_attr,
       param_code,
       param_name,
       para_code1,
       para_code2,
       para_code3,
       para_code4,
       para_code5,
       para_code6,
       para_code7,
       para_code8,
       para_code9,
       para_code10,
       para_code11,
       para_code12,
       para_code13,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       eparchy_code,
       update_staff_id,
       update_depart_id,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       remark
  FROM td_s_commpara
 WHERE param_attr = :PARAM_ATTR
   AND (param_code = :PARAM_CODE OR :PARAM_CODE = '' OR
       :PARAM_CODE IS NULL)
   AND (param_name = :PARAM_NAME OR :PARAM_NAME = '' OR
       :PARAM_NAME IS NULL)
   AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
   AND (para_code5 = :PARA_CODE5 OR :PARA_CODE5 = '' OR
       :PARA_CODE5 IS NULL)
   AND (para_code6 = :PARA_CODE6 OR :PARA_CODE6 = '' OR
       :PARA_CODE6 IS NULL)
   AND (para_code7 = :PARA_CODE7 OR :PARA_CODE7 = '' OR
       :PARA_CODE7 IS NULL)
   AND (para_code8 = :PARA_CODE8 OR :PARA_CODE8 = '' OR
       :PARA_CODE8 IS NULL)
   AND sysdate > end_date