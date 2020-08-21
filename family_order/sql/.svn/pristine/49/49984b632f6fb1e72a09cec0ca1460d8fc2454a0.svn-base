SELECT to_char(receipts_id) receipts_id,receipts_code,receipts_type,receipts_count,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,stock_id,depart_id,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(receipts_time,'yyyy-mm-dd hh24:mi:ss') receipts_time,remove_tag 
  FROM tf_f_receipts
 WHERE depart_id=:DEPART_ID
   AND (TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') between start_date AND end_date
        OR TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') between start_date AND end_date
       )
   AND receipts_type=:RECEIPTS_TYPE