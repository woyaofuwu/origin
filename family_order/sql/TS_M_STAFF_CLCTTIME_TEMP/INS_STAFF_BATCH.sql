INSERT INTO TS_M_STAFF_CLCTTIME_TEMP 
      (clct_day,staff_id,depart_id,start_date,end_date)
SELECT :clct_day,a.staff_id,a.depart_id,NVL(b.start_date,NVL(c.end_date,TRUNC(SYSDATE))),NVL(b.end_date,SYSDATE)
  FROM td_m_staff a , 
       (SELECT * 
          FROM ts_m_staff_clcttime 
         WHERE clct_day = :clct_day
       ) b ,
       (SELECT * 
          FROM ts_m_staff_clcttime 
         WHERE clct_day = TO_CHAR(TO_DATE(:clct_day,'YYYYMMDD') - 1,'YYYYMMDD')
       ) c
 WHERE a.dimission_tag = '0'
   AND a.staff_id = b.staff_id(+)
   AND a.staff_id = c.staff_id(+)
   AND a.staff_id >= :start_staff_id
   AND a.staff_id <= :end_staff_id