SELECT p.PARTITION_ID,
       to_char(p.USER_ID) USER_ID,
       p.SERVICE_ID,
       p.BIZ_STATE_CODE,
       to_char(p.FIRST_DATE, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE,
       to_char(p.FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON,
       p.GIFT_SERIAL_NUMBER,
       p.GIFT_USER_ID,
       to_char(p.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(p.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(p.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       p.UPDATE_STAFF_ID,
       p.UPDATE_DEPART_ID,
       p.REMARK,
       p.RSRV_NUM1,
       p.RSRV_NUM2,
       p.RSRV_NUM3,
       to_char(p.RSRV_NUM4) RSRV_NUM4,
       to_char(p.RSRV_NUM5) RSRV_NUM5,
       p.RSRV_STR1,
       p.RSRV_STR2,
       p.RSRV_STR3,
       p.RSRV_STR4,
       p.RSRV_STR5,
       p.RSRV_STR6,
       p.RSRV_STR7,
       p.RSRV_STR8,
       p.RSRV_STR9,
       p.RSRV_STR10,
       to_char(p.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(p.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(p.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3
  FROM TF_F_USER_PLATSVC p
WHERE 1=1
  AND USER_ID = TO_NUMBER(:USER_ID)
  AND PARTITION_ID = MOD(:USER_ID,10000)
  AND  P.BIZ_STATE_CODE IN ('N','A')
  AND  P.END_DATE > SYSDATE
