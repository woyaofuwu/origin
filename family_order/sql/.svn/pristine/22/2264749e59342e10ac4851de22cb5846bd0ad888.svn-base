
SELECT TRADE_ID,
       ACCEPT_MONTH,
       PARTITION_ID,
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
       to_char(START_DATE, 'yyyy-MM-dd HH24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-MM-dd HH24:mi:ss') END_DATE,
       to_char(UPDATE_TIME, 'yyyy-MM-dd HH24:mi:ss') UPDATE_TIME,
       UPDATE_DEPART_ID,
       UPDATE_STAFF_ID,
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
       to_char(RSRV_DATE1, 'yyyy-MM-dd HH24:mi:ss') RSRV_DATE1,
       to_char(RSRV_DATE2, 'yyyy-MM-dd HH24:mi:ss') RSRV_DATE2,
       to_char(RSRV_DATE3, 'yyyy-MM-dd HH24:mi:ss') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  FROM TF_B_TRADE_RELATION_UU_BAK
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND user_id_b = TO_NUMBER(:USER_ID)
   AND EXISTS
 (SELECT 1
          FROM td_s_commpara a
         where a.subsys_code = 'CSM'
           and a.param_attr = '6017'
           And a.param_code = '1'
           and a.para_code1 = relation_type_code
           And (a.eparchy_code = :EPARCHY_CODE Or eparchy_code = 'ZZZZ')
           and a.end_date > sysdate)
   AND end_date > SYSDATE