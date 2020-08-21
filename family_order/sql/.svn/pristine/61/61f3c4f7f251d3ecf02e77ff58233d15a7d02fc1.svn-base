INSERT INTO TF_F_USER_SVC
  (PARTITION_ID,USER_ID,USER_ID_A,PRODUCT_ID,PACKAGE_ID,INST_ID,CAMPN_ID,SERVICE_ID,MAIN_TAG,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
  SELECT MOD(TO_NUMBER(USER_ID), 10000),TO_CHAR(USER_ID) USER_ID,USER_ID_A,PRODUCT_ID,PACKAGE_ID,INST_ID,CAMPN_ID,SERVICE_ID,NVL(MAIN_TAG, '0') MAIN_TAG,START_DATE,END_DATE,sysdate,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,TO_CHAR(RSRV_NUM4) RSRV_NUM4,TO_CHAR(RSRV_NUM5) RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
    FROM TF_B_TRADE_SVC a
   WHERE trade_id = TO_NUMBER(:TRADE_ID)
     AND modify_tag in ('0','4')
     AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
     AND NOT EXISTS (SELECT 1 FROM td_s_commpara
                      WHERE subsys_code = 'CSM'
                        AND param_attr = '996'
                        AND to_number(param_code) = a.service_id
                        AND sysdate BETWEEN start_date AND end_date)
     AND NOT EXISTS (SELECT 1 FROM TF_F_USER_SVC
                      WHERE user_id = TO_NUMBER(:USER_ID)
                        AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                        AND user_id_a = a.user_id_a
                        AND product_id = a.product_id
                        AND package_id = a.package_id
                        AND service_id = a.service_id                        
                        AND INST_ID = A.INST_ID
                        AND end_date > start_date
                        AND end_date > a.start_date)