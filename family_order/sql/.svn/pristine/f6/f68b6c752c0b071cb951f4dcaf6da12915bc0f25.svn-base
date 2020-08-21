SELECT to_char(t.sms_prompt_id) trace_id,
       t.eparchy_code,
       i.in_mode in_mode_code,
       sms_kind_code,
       serial_number,
       to_char(decode(sms_type_code,1,'资费',2,'网络',3,'新业务',4,'渠道',5,'服务环节',6,'VIP服务',7,'G3业
务',8,'其他')) sms_type_code,
       to_char(accept_date, 'yyyy-mm-dd hh24:mi:ss') accept_date,
       prompt_content content,
       sms_state state,
       to_char(deal_time, 'yyyy-mm-dd hh24:mi:ss') deal_time,
       deal_staffid,
       deal_departid,
       to_char(decode(deal_state,'0','未处理','1','已处理','2','超时未处理','3','超时已处理')) deal_state,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       to_char(rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(rsrv_date3, 'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
       rsrv_tag1,
       rsrv_tag2,
       rsrv_tag3,
       to_char(rsrv_num1) rsrv_num1,
       to_char(rsrv_num2) rsrv_num2,
       to_char(rsrv_num3) rsrv_num3,
       month
  FROM ti_o_prompt_service t, td_s_inmode i
  WHERE t.in_mode_code = i.in_mode_code
  AND t.serial_number >= :SERIAL_NUMBER1
  AND t.serial_number <= :SERIAL_NUMBER2
  AND to_char(t.accept_date,'yyyy-mm-dd') >= :ACCEPT_DATE1
  AND to_char(t.accept_date,'yyyy-mm-dd') <= :ACCEPT_DATE2
  AND (:SMS_TYPE_CODE IS NULL OR :SMS_TYPE_CODE='' OR T.SMS_TYPE_CODE=:SMS_TYPE_CODE)