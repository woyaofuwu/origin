DELETE FROM TS_S_TFEE_DAY_STAFF A
 WHERE A.CLCT_DAY = :CLCT_DAY
   AND A.STAFF_ID IN (SELECT B.STAFF_ID
                        FROM TD_M_STAFF B
                       WHERE B.DEPART_ID >= :START_DEPART_ID
                         AND B.DEPART_ID <= :END_DEPART_ID
                     )