SELECT T.NEW_POINT,
       T.POINT_ONE,
       T.POINT_TWO,
       T.SEQ,
       T.POINT_TYPE,
       T.USE_TAG,
       T.TEMPLET_ID,
       T.EXTRA_ID,
       T.RSRV_STR1,
       T.RSRV_STR2,
       T.RSRV_STR3,
       T.RSRV_STR4
  FROM TD_B_EOP_POINT T
 WHERE T.POINT_ONE = :POINT_ONE
   AND T.POINT_TWO = :POINT_TWO
   ORDER BY T.SEQ