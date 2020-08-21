INSERT INTO TF_B_TRADE_FINISH(TRADE_ID, ACCEPT_MONTH, ACCEPT_DATE, FINISH_DATE, CANCEL_TAG, CANCEL_DATE, UPDATE_TIME, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10) 
select TRADE_ID, ACCEPT_MONTH, ACCEPT_DATE, Sysdate, CANCEL_TAG, CANCEL_DATE, UPDATE_TIME, REMARK,Null,Null,Null,Null,Null,Null,Null,Null,Null,Null
  FROM tf_b_trade a
 WHERE trade_id = to_number(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG
   AND (subscribe_state <> '0' OR subscribe_type = '300')
   AND EXISTS
 (SELECT 1
          FROM td_s_tag
         WHERE (eparchy_code = a.eparchy_code OR eparchy_code = 'ZZZZ')
           AND tag_code = 'CS_INSERT_TRADE_FININSH'
           AND subsys_code = 'CSM'
           AND start_date + 0 < SYSDATE
           AND end_date + 0 >= SYSDATE)