--IS_CACHE=Y
SELECT DEPART_ID, DEPART_NAME, depart_code
  FROM td_m_depart  
 WHERE rsvalue2 = '0871'
   AND validflag = '0'
   AND SYSDATE BETWEEN start_date AND end_date
   AND a.area_code = :AREA_CODE
 ORDER BY depart_code