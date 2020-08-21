UPDATE TF_B_RES_PRODUCE_MAIN
   SET AUDIT_STATE_CODE = :AUDIT_STATE_CODE,
       CANCEL_REASON = decode(:CANCEL_REASON,null,cancel_reason,:CANCEL_REASON),
       RSRV_DATE1 = sysdate,
       RSRV_STR3 = :RSRV_STR3,
       REMARK = decode(:REMARK,null,remark,:REMARK)
 WHERE PRODUCE_ID = :PRODUCE_ID
   AND (:BATCH_ID is null or BATCH_ID = to_number(:BATCH_ID))