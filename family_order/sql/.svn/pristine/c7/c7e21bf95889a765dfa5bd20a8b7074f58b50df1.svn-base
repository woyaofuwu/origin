--IS_CACHE=Y
SELECT A.SP_ID,
       B.SP_NAME,
       B.SP_NAME_EN,
       A.SP_SVC_ID,
       A.BIZ_TYPE,
       A.BIZ_DESC,
       A.BIZ_TYPE_CODE,
       A.ACCESS_MODEL,
       TRIM(TO_CHAR(A.PRICE / 1000, '99990.00')) PRICE,
       DECODE(A.BILLING_TYPE,
              '0',
              '免费',
              '1',
              '按条计费',
              '2',
              '包月计费',
              '3',
              '包时计费',
              '4',
              '包次计费',
              '未知') BILLING_TYPE,
       A.BIZ_STATUS,
       A.PROV_ADDR,
       A.PROV_PORT,
       A.USAGE_DESC,
       A.INTRO_URL,
       A.FOREGIFT_CODE,
       TO_CHAR(A.FOREGIFT) FOREGIFT,
       A.RSRV_STR1,
       A.RSRV_STR2,
       A.RSRV_STR3,
       A.RSRV_STR4,
       A.RSRV_STR5,
       A.RSRV_STR9,
       A.REMARK,
       A.UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID,
       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       B.CS_TEL
  FROM TD_M_SPSERVICE A, TD_M_SPFACTORY B
 WHERE  B.SP_ID = A.SP_ID
   AND A.BIZ_STATUS NOT IN ('N', 'E')   
   AND EXISTS
 (SELECT 1
          FROM TD_S_TAG
         WHERE TAG_CODE = 'PUB_CUR_PROVINCE'
           AND USE_TAG = '0'
           AND START_DATE + 0 < SYSDATE
           AND SUBSYS_CODE = 'PUB'
           AND END_DATE + 0 >= SYSDATE
           AND INSTR(DECODE(TAG_INFO, 'HAIN', '1', NVL(B.RSRV_STR4, '0')),
                     NVL(B.RSRV_STR4, '0')) > 0
           AND INSTR(DECODE(TAG_INFO, 'HAIN', '1', NVL(A.RSRV_STR4, '0')),
                     NVL(A.RSRV_STR4, '0')) > 0)