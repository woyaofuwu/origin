SELECT TO_CHAR(trade_id) trade_id,TO_CHAR(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,trade_type_code,trade_type
  FROM (
           SELECT /*+ index(a IDX_TF_BH_TRADE_SN)*/ a.trade_id,a.trade_type_code,b.trade_type,a.trade_eparchy_code,a.accept_date
             FROM tf_bh_trade a,td_s_tradetype b 
            WHERE a.serial_number=:SERIAL_NUMBER 
              AND a.cancel_tag='0' 
              AND a.accept_date>TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') 
              AND a.trade_type_code=b.trade_type_code
              AND b.back_tag<>'0'
              AND b.trade_attr='1'
              AND b.eparchy_code=a.trade_eparchy_code
           UNION ALL
           SELECT a.trade_id,a.trade_type_code,b.trade_type,a.trade_eparchy_code,a.accept_date 
             FROM tf_b_trade a,td_s_tradetype b 
            WHERE a.serial_number=:SERIAL_NUMBER 
              AND a.cancel_tag='0' 
              AND a.accept_date+0>TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') 
              AND a.trade_type_code=b.trade_type_code
              AND b.back_tag<>'0'
              AND b.trade_attr='1'
              AND b.eparchy_code=a.trade_eparchy_code
           UNION ALL
           SELECT /*+ index(a IDX_TF_BH_TRADE_SN)*/ a.trade_id,a.trade_type_code,b.trade_type,a.trade_eparchy_code,a.accept_date 
             FROM tf_bh_trade a,td_s_tradetype b
            WHERE a.serial_number=:SERIAL_NUMBER 
              AND a.cancel_tag='0' 
              AND a.accept_date>TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') 
              AND a.trade_type_code=b.trade_type_code
              AND b.trade_attr='2'
              AND b.eparchy_code=a.trade_eparchy_code
              AND trunc(a.accept_date)!=trunc(TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS'))
           UNION ALL
           SELECT a.trade_id,a.trade_type_code,b.trade_type,a.trade_eparchy_code,a.accept_date 
             FROM tf_b_trade a,td_s_tradetype b
            WHERE a.serial_number=:SERIAL_NUMBER 
              AND a.cancel_tag='0' 
              AND a.accept_date+0>TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') 
              AND a.trade_type_code=b.trade_type_code
              AND b.trade_attr='2'
              AND b.eparchy_code=a.trade_eparchy_code
        )
 WHERE ROWNUM <= 1