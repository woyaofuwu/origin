--IS_CACHE=Y
SELECT TYPE_ID,
       DATA_ID,
       DATA_NAME,
       PDATA_ID,
       SUBSYS_CODE,
       EPARCHY_CODE,
       REMARK,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  FROM TD_S_STATIC
 WHERE TYPE_ID = :TYPE_ID
 order by DATA_ID