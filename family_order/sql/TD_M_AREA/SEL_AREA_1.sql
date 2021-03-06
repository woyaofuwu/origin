--IS_CACHE=Y
SELECT AREA_CODE,
       NVL(AREA_NAME, '') AREA_NAME,
       AREA_FRAME,
       THIS_TAG,
       DECODE(THIS_TAG, '0', '非当前地域', '1', '当前地域', ' ') B_THISTAG,
       USE_TAG,
       DECODE(USE_TAG, '0', '禁止', '1', '开放', ' ') B_USETAG,
       EXTEND_TAG,
       DECODE(EXTEND_TAG, '0', '禁止', '1', '开放', ' ') B_EXTENDTAG,
       ORDER_NO,
       NVL(USER_AREA_CODE, '') USER_AREA_CODE,
       NVL(PARENT_AREA_CODE, '') PARENT_AREA_CODE,
       TO_CHAR(START_DATE, 'yyyy/mm/dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy/mm/dd hh24:mi:ss') END_DATE,
       NVL(AREA_LEVEL, -1) AREA_LEVEL,
       VALIDFLAG,
       DECODE(validflag, '0', '有效', '1', '失效', ' ') B_VALIDFLAG,
       NVL(REMARK, '') REMARK,
       NVL(RSVALUE1, '') RSVALUE1,
       NVL(RSVALUE2, '') RSVALUE2,
       TO_CHAR(UPDATE_TIME, 'yyyy/mm/dd hh24:mi:ss') UPDATE_TIME,
       NVL(UPDATE_STAFF_ID, '') UPDATE_STAFF_ID,
       NVL(UPDATE_DEPART_ID, '') UPDATE_DEPART_ID
  FROM TD_M_AREA
 WHERE AREA_LEVEL = :AREA_LEVEL
   AND VALIDFLAG = :VALIDFLAG