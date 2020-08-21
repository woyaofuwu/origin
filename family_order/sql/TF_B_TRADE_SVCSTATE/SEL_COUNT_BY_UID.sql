SELECT COUNT(1) ROW_NUM
  FROM TF_B_TRADE_SVCSTATE T,tf_bh_trade a
 WHERE T.SERVICE_ID = '22'
   AND T.USER_ID = :USER_ID
   AND t.state_code= :STATE_CODE
   AND T.MODIFY_TAG='0'
   AND TRUNC(T.UPDATE_TIME) = TRUNC(SYSDATE)
   AND A.USER_ID=T.USER_ID
   AND a.trade_id=t.trade_id
   AND a.trade_type_code= '130'
   AND a.in_mode_code = '5'
   AND a.accept_month = TO_NUMBER(TO_CHAR(SYSDATE,'MM'))