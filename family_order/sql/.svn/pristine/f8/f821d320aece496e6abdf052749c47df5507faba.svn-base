SELECT PARTITION_ID,
       USER_ID_A,
       SERIAL_NUMBER_A,
       USER_ID_B,
       SERIAL_NUMBER_B,
       RELATION_TYPE_CODE,
       ROLE_TYPE_CODE,
       ROLE_CODE_A,
       ROLE_CODE_B,
       ORDERNO,
       SHORT_CODE,
       INST_ID,
       to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_NUM4,
       RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  FROM TF_F_RELATION_UU 

WHERE 1=1
AND ROLE_CODE_B = :ROLE_CODE_B
AND sysdate < end_date+0
AND USER_ID_A IN

(SELECT USER_ID_A FROM TF_F_RELATION_UU 

WHERE RELATION_TYPE_CODE = :RELATION_TYPE_CODE

HAVING count(*)=1 

group by user_id_A)
