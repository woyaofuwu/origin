SELECT A.RES_NO,A.RES_NO AS TERMINAL_ID,'此终端串号发货记录表中不存在' ERROR_REASON,
'如需入库，请核查发货记录表' PROMPT , '1' AS CROSS_TYPE,'0' AS VALID_TAG
FROM TF_B_CEN_RES_BATCH_TMP A 
 WHERE A.LOG_ID=:LOG_ID 
 AND NOT EXISTS (SELECT 1 FROM TF_R_CEN_TERMINAL_DIS B WHERE  A.res_no=B.terminal_id 
 AND B.USE_TYPE=:USE_TYPE)