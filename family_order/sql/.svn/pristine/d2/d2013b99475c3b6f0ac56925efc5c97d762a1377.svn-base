SELECT a.trade_id trade_id, a.serial_number serial_number, a.cust_name cust_name,
    a.trade_type_code trade_type_code, b.trade_type trade_type,
    to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
    a.cancel_tag cancel_tag
  FROM tf_bh_trade a, td_s_tradetype b 
 WHERE accept_date > (SELECT max(accept_date) 
                FROM tf_bh_trade 
               WHERE user_id = :USER_ID
               AND trade_type_code = 240 
               AND rsrv_str1 in ('68600850','68300747','67500491',
                                 '67600552','67400438','67700574',
                                 '69100054','67000130','69200116',
                                 '68800915','67300372','67200308','67100199')
               ) 
  AND user_id = :USER_ID
  AND a.trade_type_code in (192, 100, 7230, 7240, 7302, 264)
  AND a.trade_type_code = b.trade_type_code 
  AND a.eparchy_code = b.eparchy_code