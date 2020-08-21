--IS_CACHE=Y
SELECT A.TYPE_ID,
       A.DATA_ID,
       A.DATA_NAME,
       A.SUBSYS_CODE,
       A.EPARCHY_CODE,
       A.REMARK,
       A.UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID,
       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       A.VALID_FLAG
  FROM TD_S_STATIC A
 WHERE A.TYPE_ID = 'PAY_ITEM_NAME'
   AND A.DATA_NAME LIKE '%' || :DATA_NAME || '%'
   AND A.UPDATE_TIME BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd') AND TO_DATE(:END_DATE, 'yyyy-mm-dd') + 1
 ORDER BY to_number(A.DATA_ID) ASC