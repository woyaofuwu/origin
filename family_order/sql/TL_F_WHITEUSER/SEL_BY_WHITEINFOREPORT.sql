SELECT ACCEPT_MONTH,
       TO_CHAR(IN_TIME, 'yyyy-mm-dd hh24:mi:ss') IN_TIME,
       SERIAL_NUMBER,
       USER_ID,
       USER_ATTR,
       USER_LEVEL,
       PROVINCE_CODE,
       EFFECT_TAG,
       TO_CHAR(BEGIN_TIME, 'yyyy-mm-dd hh24:mi:ss') BEGIN_TIME,
       TO_CHAR(END_TIME, 'yyyy-mm-dd hh24:mi:ss') END_TIME,
       OPERATE_FLAG,
       TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,
       PROCESS_TAG,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3,
       REMARK
  FROM TL_F_WHITEUSER
 WHERE 1 = 1
   AND IN_TIME BETWEEN TO_DATE(:PARA_CODE1, 'yyyy-mm-dd hh24:mi:ss') AND
       TO_DATE(:PARA_CODE2, 'yyyy-mm-dd hh24:mi:ss')
   AND (PROVINCE_CODE = :PARA_CODE3 OR :PARA_CODE3 IS NULL)