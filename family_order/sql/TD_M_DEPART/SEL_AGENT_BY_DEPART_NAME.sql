--IS_CACHE=Y
SELECT A.DEPART_CODE, A.DEPART_NAME
  FROM TD_M_DEPART A
 WHERE A.DEPART_KIND_CODE NOT IN ('500', '801', '100', '301')
   AND A.VALIDFLAG = '0'
   AND A.DEPART_NAME LIKE '%' || :DEPART_NAME || '%'