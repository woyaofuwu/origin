SELECT *
  FROM (
         SELECT a.trade_id deal_id,
                a.cancel_tag deal_tag,
                DECODE(a.subscribe_state,'0','1','G','J') deal_state,
                a.trade_eparchy_code deal_eparchy_code
           FROM tf_b_trade a
          WHERE a.exec_time <= SYSDATE - F_SYS_GET_TIMEOFFSET(a.subscribe_state,a.trade_eparchy_code,a.trade_type_code,NULL,NULL,NULL)
            AND a.subscribe_state IN ('0','G')
            AND a.subscribe_type >= 200 
            AND a.subscribe_type <  300 
            $CONDITIONSQL
         ORDER BY a.exec_time,a.subscribe_state
       )
 WHERE ROWNUM <= :ROW_COUNT