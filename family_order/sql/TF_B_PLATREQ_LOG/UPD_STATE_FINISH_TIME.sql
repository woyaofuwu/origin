UPDATE TF_B_PLATREQ_LOG
   SET STATE            = '1',
       FINISH_TIME      = SYSDATE,
       FINISH_DEPART_ID = :FINISH_DEPART_ID,
       FINISH_STAFF_ID  = :FINISH_STAFF_ID,
       REMARK = :REMARK
 WHERE INDICT_SEQ = :INDICT_SEQ