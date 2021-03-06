--IS_CACHE=Y
SELECT LEVEL LEV,
       T.DEPART_ID,
       T.DEPART_CODE,
       '[' || T.DEPART_CODE || ']' || T.DEPART_NAME DEPART_NAME,
       T.DEPART_KIND_CODE,
       T.DEPART_FRAME,
       T.AREA_CODE,
       T.PARENT_DEPART_ID,
       T.DEPART_LEVEL
  FROM TD_M_DEPART T
CONNECT BY PRIOR T.PARENT_DEPART_ID = T.DEPART_ID
 START WITH T.DEPART_ID = :DEPART_ID