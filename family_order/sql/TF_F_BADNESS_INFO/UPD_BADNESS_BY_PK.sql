UPDATE TF_F_BADNESS_INFO
   SET STATE              = :STATE,
       FINISH_DATE        = TO_DATE(:FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS'),
       DEAL_REMARK_MAKEUP = :DEAL_REMARK_MAKEUP,
       DEAL_DATE          = TO_DATE(:DEAL_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE INFO_RECV_ID = :INFO_RECV_ID
