--IS_CACHE=Y
SELECT DEPART_ID, DEPART_NAME, DEPART_CODE
  FROM td_m_depart  
 WHERE rsvalue2 = :RSVALUE2
   AND validflag = '0'
   AND SYSDATE BETWEEN start_date AND end_date
   AND AREA_CODE = :AREA_CODE
 ORDER BY DEPART_CODE