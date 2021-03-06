INSERT INTO TF_B_TRADE_RES(TRADE_ID,ACCEPT_MONTH,USER_ID,USER_ID_A,INST_ID,RES_TYPE_CODE,RES_CODE,IMSI,KI,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),USER_ID,USER_ID_A,INST_ID,RES_TYPE_CODE,RES_CODE,IMSI,KI,START_DATE,TO_DATE(:ACCEPT_DATE,'YYYY-MM-DD HH24:MI:SS'),:MODIFY_TAG,TO_DATE(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
  FROM TF_F_USER_RES A
 WHERE USER_ID = TO_NUMBER(:USER_ID)
   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)
   AND END_DATE > SYSDATE AND END_DATE > START_DATE
   AND RES_TYPE_CODE='S'
   AND NOT EXISTS (SELECT 1 FROM TF_B_TRADE_RES
                    WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
                      AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      AND RES_TYPE_CODE ='S'
                      AND RES_CODE = A.RES_CODE)