--IS_CACHE=Y
SELECT discnt_code,DISCNT_TYPE_CODE,to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
       FROM TD_B_DTYPE_DISCNT  
       WHERE  TRIM(discnt_type_code)=:DISCNT_TYPE_CODE
       AND end_date > SYSDATE