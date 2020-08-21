SELECT T.LOG_ID,
       T.TERMINAL_ID,
       T.OPER_STAFF_ID,
       TO_CHAR(T.OPER_DATE, 'yyyy-mm-dd hh24:mi:ss') OPER_DATE,
       F_RES_GETCODENAME('depart_id', T.OPER_DEPART_ID, '', '') OPER_DEPART_NAME,
       F_RES_GETCODENAME('td_m_restrade', T.RES_TRADE_ID, '', '') RES_TRADE_NAME,
       F_RES_GETCODENAME('depart_id', T.DEPART_ID_O, '', '') DEPART_O_NAME,
       F_RES_GETCODENAME('staff_id', T.OPER_STAFF_ID, '', '') OPER_STAFF_NAME,
       DECODE(T.DEPART_ID_N,
              '00004',
              '最终用户',
              F_RES_GETCODENAME('depart_id', T.DEPART_ID_N, '', '')) DEPART_N_NAME
  FROM (SELECT A.LOG_ID,
               A.RES_TRADE_ID,
               A.OPER_DATE,
               A.OPER_STAFF_ID,
               A.OPER_DEPART_ID,
               B.START_VALUE TERMINAL_ID,
               A.DEPART_ID_O,
               A.DEPART_ID_N
          FROM TF_B_RES_ASSIGN_LOG A, TF_B_TERMINAL_ASSIGN_DETAIL B
         WHERE A.LOG_ID = B.LOG_ID
           AND B.START_VALUE = :TERMINAL_ID
        UNION ALL
        SELECT X.LOG_ID,
               X.RES_TRADE_ID,
               X.SALE_TIME      OPER_DATE,
               X.SALE_STAFF_ID  OPER_STAFF_ID,
               X.SALE_DEPART_ID OPER_DEPART_ID,
               Y.TERMINAL_ID,
               X.SALE_DEPART_ID DEPART_ID_O,
               X.STOCK_ID       DEPART_ID_N
          FROM TF_B_RES_SALE_LOG X, TF_B_TERMINAL_SALE_DETAIL Y
         WHERE X.LOG_ID = Y.LOG_ID
           AND Y.TERMINAL_ID = :TERMINAL_ID) T
 ORDER BY T.OPER_DATE