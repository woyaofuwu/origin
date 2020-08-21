SELECT (SELECT TRUNC(sysdate-rdvalue2) 
         FROM TD_M_RES_COMMPARA
        WHERE eparchy_code=:EPARCHY_CODE 
          AND para_attr=:PARA_ATTR 
          AND para_code1=:PARA_CODE1 
          AND ((:PARA_CODE2 IS NOT NULL AND para_code2=:PARA_CODE2) OR :PARA_CODE2 IS NULL))
       -
       (SELECT PARA_VALUE2 
         FROM TD_M_RES_COMMPARA
        WHERE eparchy_code=:EPARCHY_CODE 
          AND para_attr=:PARA_ATTR2 
          AND para_code1=:PARA_CODE1_OTHER 
          AND PARA_VALUE1=:PARA_VALUE1) para_value8
FROM DUAL