DELETE /*+INDEX(a PK_TD_S_SYSPARA_DEFLIST)*/ FROM td_s_syspara_deflist a
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)