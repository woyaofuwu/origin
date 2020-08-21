SELECT COUNT(1)  recordcount
  FROM dual
 WHERE (
       SELECT COUNT(1) FROM tf_bh_trade A
              WHERE trade_type_code=110
                AND brand_code!=rsrv_str1    --品牌变更
                AND user_id=:USER_ID
                AND subscribe_state='9'
                AND exec_time>decode(:TYPE,
                                  '0',SYSDATE+(-1)*:NUM,                         --天
                                  '1',trunc(SYSDATE+(-1)*:NUM),                  --自然天
                                  '2',ADD_MONTHS(SYSDATE,(-1)*:NUM),             --月
                                  '3',trunc(ADD_MONTHS(SYSDATE,(-1)*:NUM),'mm'), --自然月
                                  '4',ADD_MONTHS(SYSDATE,(-12)*:NUM),            --年
                                  '5',trunc(ADD_MONTHS(SYSDATE,(-12)*:NUM),'yy'),--自然年
                                  SYSDATE)
      AND (SELECT SERVICE_ID FROM TD_B_PRODUCT_SVC B
                     WHERE B.PRODUCT_ID = A.RSRV_STR2
                       AND SERVICE_ID IN (16, 17, 18, 19)
                       AND DEFAULT_TAG = '1') >
       (SELECT SERVICE_ID  FROM TD_B_PRODUCT_SVC B
                     WHERE B.PRODUCT_ID = A.PRODUCT_ID
                       AND SERVICE_ID IN (16, 17, 18, 19)
                       AND DEFAULT_TAG = '1')
 ) >= :TIMES