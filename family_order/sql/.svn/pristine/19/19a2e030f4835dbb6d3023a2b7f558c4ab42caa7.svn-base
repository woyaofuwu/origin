SELECT serial_number,serial_number_b,accept_mode,decode(accept_mode,'00','10086999短信营业厅受理','01','10086营业厅人工受理','未知受理方式') x_accept_mode,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,sms_content,rsrv_str1,rsrv_str2,rsrv_str3,to_char(in_time,'yyyy-mm-dd hh24:mi:ss') in_time,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_staff,deal_result,deal_tag 
  FROM tf_f_blacksmsimpeachplat
 WHERE (deal_staff =:DEAL_STAFF or :DEAL_STAFF is null or :DEAL_STAFF = '')
   AND deal_time between TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND deal_tag=:DEAL_TAG