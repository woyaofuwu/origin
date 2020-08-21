UPDATE TI_B_EC A
   SET (A.STATUS, A.SUBSTATUS, A.STATUSDESC) =  (SELECT :DEAL_STATE,
                                                       '99',
                                                       SUBSTRB(B.ERR_DETAIL_DESC,
                                                              0,
                                                              512)
                                                  FROM TL_BPM_ERROR_LOG B
                                                 WHERE FLOW_ID IN
                                                       (SELECT MAX(FLOW_ID)
                                                          FROM TL_BPM_ERROR_LOG
                                                         WHERE TRADE_ID =
                                                               A.OPR_SEQ))
   WHERE a.trade_id=:DEAL_ID
   AND (:DEAL_TAG IS NULL OR :DEAL_TAG = :DEAL_TAG)
   AND (:DEAL_EPARCHY_CODE IS NULL OR :DEAL_EPARCHY_CODE = :DEAL_EPARCHY_CODE)