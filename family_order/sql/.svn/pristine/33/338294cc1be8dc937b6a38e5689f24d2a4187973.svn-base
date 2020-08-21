--IS_CACHE=Y
SELECT SP_ID,
       SP_SVC_ID,
       SP_NAME,
       SP_NAME_EN,
       SP_SHORT_NAME,
       SP_STATUS,
       SP_DESC,
       CS_TEL,
       CS_URL,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       REMARK,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
  FROM TD_M_SPFACTORY
 WHERE SP_STATUS NOT IN ('N')
   AND EXISTS
 (SELECT 1
          FROM TD_S_TAG
         WHERE TAG_CODE = 'PUB_CUR_PROVINCE'
           AND USE_TAG = '0'
           AND START_DATE + 0 < SYSDATE
           AND SUBSYS_CODE = 'PUB'
           AND END_DATE + 0 >= SYSDATE
           AND INSTR(DECODE(TAG_INFO, 'HAIN', '1', NVL(RSRV_STR4, '0')),
                     NVL(RSRV_STR4, '0')) > 0)