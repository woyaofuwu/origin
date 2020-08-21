   SELECT USER_ID,
          USER_ID_A,
          PLAN_ID,
          PLAN_TYPE_CODE,
          PLAN_NAME,
          PLAN_DESC,
          DEFAULT_TAG,
          RULE_ID,
          START_DATE,
          END_DATE,
          UPDATE_TIME,
          UPDATE_STAFF_ID,
          UPDATE_DEPART_ID,
          REMARK,
          RSRV_STR1,
          RSRV_STR2,
          RSRV_STR3,
          RSRV_STR4,
          RSRV_STR5,
          RSRV_STR6,
          RSRV_STR7,
          RSRV_STR8,
          RSRV_STR9,
          RSRV_STR10
     FROM TF_F_USER_PAYPLAN
    WHERE USER_ID = TO_NUMBER(:USER_ID)
      AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
      AND END_DATE > SYSDATE
