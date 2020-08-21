SELECT COUNT(1) recordcount  From dual  Where
(SELECT /*+ ordered use_nl(s,a)*/ nvl(MAX(s.end_date),to_date('1900','yyyy')) start_date
FROM tf_f_user_sale_active s,td_s_commpara a
WHERE s.user_id = to_number(:USER_ID)
  AND s.partition_id = mod(to_number(:USER_ID),10000)
  AND s.end_date > SYSDATE
  AND s.product_id = to_number(a.para_code2)
  AND s.package_id = nvl(a.para_code4,s.package_id)
  AND a.param_attr='967'
  AND a.subsys_code = 'CSM'
  AND sysdate BETWEEN a.start_date AND a.end_date
  AND (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
  AND (a.para_code1 = :PRODUCT_ID)
  AND (a.para_code3 is null or a.para_code3 = :PACKAGE_ID)
  AND a.param_code = '0'
  ) >  to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')