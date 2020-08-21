SELECT A.USER_ID,
       A.SERVICE_ID,
       A.SERIAL_NUMBER,
       A.SP_CODE,
       A.BIZ_CODE,
       A.BIZ_TYPE_CODE,
       A.BIZ_STATE_CODE,
       A.OPER_CODE,
       A.OPR_SOURCE,
       A.PRICE,
       A.FIRST_DATE,
       A.GIFT_SERIAL_NUMBER,
       A.BILL_TYPE,
       A.ORG_DOMAIN,
       A.FIRST_DATE_MON,
       A.GIFT_USER_ID,
       A.RSRV_TAG3,
       A.START_DATE,
       A.PARTITION_ID,
       A.RSRV_NUM5,
       A.UPDATE_TIME
  FROM TF_F_USER_PLATSVC A
 WHERE A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)
 AND   A.SERIAL_NUMBER = :SERIAL_NUMBER
 AND   A.SERVICE_ID IN (99817947,99817948,99817949)
 AND   A.USER_ID = :USER_ID
 AND   (A.BIZ_STATE_CODE = 'A' OR A.BIZ_STATE_CODE = 'N' OR A.BIZ_STATE_CODE = 'L')
 AND   SYSDATE BETWEEN A.START_DATE AND A.END_DATE