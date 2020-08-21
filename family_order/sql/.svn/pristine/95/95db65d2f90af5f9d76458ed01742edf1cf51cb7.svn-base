SELECT   PARTITION_ID,
           USER_ID,
           SERVICE_ID,
           SERIAL_NUMBER,
           SP_CODE,
           BIZ_CODE,
           BIZ_TYPE_CODE,
           BIZ_STATE_CODE,
           PRODUCT_NO,
           ORG_DOMAIN,
           OPER_CODE,      
           OPR_SOURCE,
           BILL_TYPE,
           PRICE,
           FIRST_DATE,
           FIRST_DATE_MON,
           GIFT_SERIAL_NUMBER,
           GIFT_USER_ID,
           SUBSCRIBE_ID,
           START_DATE,
           END_DATE,
           UPDATE_TIME,
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
           RSRV_STR6,
           RSRV_STR7,
           RSRV_STR8,
           RSRV_STR9,
           RSRV_STR10,
           RSRV_DATE1,
           RSRV_DATE2,
           RSRV_DATE3,
           RSRV_TAG1,
           RSRV_TAG2,
           RSRV_TAG3
    FROM TF_F_USER_PLATSVC A
   WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
     AND USER_ID = TO_NUMBER(:USER_ID)
     AND BIZ_TYPE_CODE = :BIZ_TYPE_CODE
     AND BIZ_STATE_CODE IN ('A', 'N')
     AND SYSDATE BETWEEN START_DATE AND END_DATE
     AND EXISTS (SELECT 1
            FROM TD_B_PLATSVC B
           WHERE A.SERVICE_ID = B.SERVICE_ID
             AND B.SERV_TYPE = '1')
