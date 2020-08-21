SELECT 
       TO_CHAR(trade_id) trade_id,
       a.trade_type_code,
       a.serial_number,
       a.cust_name,
       TO_CHAR(accept_date, 'YYYY-MM-DD HH24:MI:SS') accept_date,
       a.trade_staff_id,
       a.accept_month,
       a.order_id,
       a.user_id,
       a.user_id,
	   a.RSRV_STR4,
	   a.RSRV_STR5,
       a.RSRV_STR6,
       a.eparchy_code
  FROM (SELECT trade_id,
               serial_number,
               trade_type_code,
               cust_name,
               accept_date,
               trade_staff_id,
               accept_month,
               order_id,
               user_id,
			   RSRV_STR4,
			   RSRV_STR5,
               RSRV_STR6,
               eparchy_code
          FROM tf_bh_trade
         WHERE to_char(accept_date,'yyyy-mm-dd hh:mm:ss')
          between to_char(trunc(add_months(last_day(to_date(:START_DATE, 'yyyy-mm')), -1) + 1), 'yyyy-mm-dd hh:mm:ss') and
          to_char(last_day(to_date(:END_DATE, 'yyyy-mm')), 'yyyy-mm-dd hh:mm:ss')
           AND serial_number = :SERIAL_NUMBER
           AND RSRV_STR4 = 'UMMP_FLOW'
           AND TO_NUMBER(RSRV_STR5) > 0
           AND TRADE_TYPE_CODE='110'
        UNION ALL
        SELECT trade_id,
               serial_number,
               trade_type_code,
               cust_name,
               accept_date,
               trade_staff_id,
               accept_month,
               order_id,
               user_id,
			   RSRV_STR4,
	           RSRV_STR5,
               RSRV_STR6,
               eparchy_code
          FROM tf_b_trade
         WHERE to_char(accept_date,'yyyy-mm-dd hh:mm:ss')
          between to_char(trunc(add_months(last_day(to_date(:START_DATE, 'yyyy-mm')), -1) + 1), 'yyyy-mm-dd hh:mm:ss') and
          to_char(last_day(to_date(:END_DATE, 'yyyy-mm')), 'yyyy-mm-dd hh:mm:ss')
           AND serial_number = :SERIAL_NUMBER
           AND RSRV_STR4 = 'UMMP_FLOW'
           AND TO_NUMBER(RSRV_STR5) > 0
            AND TRADE_TYPE_CODE='110') a