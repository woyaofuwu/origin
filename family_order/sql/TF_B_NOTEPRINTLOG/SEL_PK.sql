SELECT to_char(PRINT_ID) PRINT_ID,
       to_char(TRADE_ID) TRADE_ID,
       to_char(TEMPLET_CODE) TEMPLET_CODE,
       TEMPLET_TYPE,
       NOTE_NO,
       TAX_NO,
       SOURCE_TYPE,
       SERIAL_NUMBER,
       to_char(ACCT_ID) ACCT_ID,
       PAY_NAME,
       PRINT_MODE,
       START_CYCLE_ID,
       END_CYCLE_ID,
       to_char(TRADE_TIME, 'yyyy-mm-dd hh24:mi:ss') TRADE_TIME,
       TRADE_STAFF_ID,
       TRADE_DEPART_ID,
       TRADE_CITY_CODE,
       TRADE_EPARCHY_CODE,
       TRADE_REASON_CODE,
       to_char(TOTAL_FEE) TOTAL_FEE,
       REPRINT_FLAG,
       PRINTED_FEE,
       SPECITEM_PRINTFLAG,
       PREPRINT_FLAG,
       REMARK,
       CANCEL_TAG,
       POST_TAG,
       EPARCHY_CODE,
       to_char(CANCEL_TIME, 'yyyy-mm-dd hh24:mi:ss') CANCEL_TIME,
       CANCEL_STAFF_ID,
       CANCEL_DEPART_ID,
       CANCEL_CITY_CODE,
       CANCEL_EPARCHY_CODE,
       CANCEL_REASON_CODE,
       RSRV_FEE1,
       RSRV_FEE2,
       RSRV_INFO1,
       RSRV_INFO2,
       RSRV_INFO3,
       RSRV_INFO4,
       RSRV_INFO5
  FROM TF_B_NOTEPRINTLOG
 WHERE SERIAL_NUMBER = :SERIAL_NUMBER
   AND (TRADE_ID = TO_NUMBER(:TRADE_ID) OR :TRADE_ID IS NULL)
   AND (NOTE_NO = :NOTE_NO OR :NOTE_NO IS NULL)
   AND REPRINT_FLAG = :REPRINT_FLAG 
   AND (:TRADE_STAFF_ID = 'SUPERUSR' OR TRADE_STAFF_ID = :TRADE_STAFF_ID OR
       EXISTS (SELECT 1
                 FROM tf_m_staffdataright C, tf_m_roledataright D
                WHERE C.staff_id = :TRADE_STAFF_ID
                  AND C.data_code = D.role_code
                  AND C.right_attr = '1' --权限属性：1-数据角色权限
                  AND C.right_tag = '1' --权限标志：1-有效
                  AND D.data_type = '0' --数据类型：0-资源权限
               ))