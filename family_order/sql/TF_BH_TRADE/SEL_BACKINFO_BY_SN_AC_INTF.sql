SELECT b.trade_type,
       TO_CHAR(a.trade_id) trade_id,
       a.accept_month,
       a.trade_type_code,
       a.serial_number,
       TO_CHAR(a.accept_date, 'yyyy-mm-dd hh24:mi:ss') accept_date,
	   a.rsrv_str2,
	   a.rsrv_str4
  FROM tf_bh_trade a,td_s_tradetype b,
       (SELECT trade_type_code,MAX(accept_date) accept_date
          FROM tf_bh_trade
         WHERE serial_number = :SERIAL_NUMBER
           AND cancel_tag = '0'
           AND accept_date+0 BETWEEN to_date(:BEGIN_DATE,'YYYY-MM-DD') AND TRUNC(to_date(:END_DATE,'YYYY-MM-DD') + 1) - 1/24/3600
		GROUP BY trade_type_code    
       ) c
 WHERE a.serial_number = :SERIAL_NUMBER
   AND (a.trade_eparchy_code = :TRADE_EPARCHY_CODE OR :TRADE_EPARCHY_CODE IS NULL)
   AND a.cancel_tag = '0'
   AND a.accept_date+0 = c.accept_date
   AND b.trade_type_code = a.trade_type_code
   AND b.eparchy_code = a.trade_eparchy_code
   AND SYSDATE BETWEEN b.start_date AND b.end_date
   AND a.accept_date+0 BETWEEN to_date(:BEGIN_DATE,'YYYY-MM-DD') AND TRUNC(to_date(:END_DATE,'YYYY-MM-DD') + 1) - 1/24/3600
   AND b.back_tag <> '0'