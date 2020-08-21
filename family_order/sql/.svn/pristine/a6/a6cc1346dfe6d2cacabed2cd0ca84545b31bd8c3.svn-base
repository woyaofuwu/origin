SELECT to_char(a.user_id) user_id,a.partition_id,a.serial_number,a.eparchy_code,a.channel_code,
       a.execute_id,a.execute_desc,a.id_type,a.id,to_char(a.trade_id) trade_id,
       to_char(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,a.deal_tag,a.deal_staff_id,a.deal_depart_id,a.deal_eparchy_code,
       a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,
       b.para_code2 para_code1,c.para_code2 para_code2,DECODE(a.deal_tag,'0','接受','1','犹豫','2','拒绝','') para_code3,
       '' para_code4,'' para_code5,'' para_code6,'' para_code7,'' para_code8,'' para_code9,'' para_code10
  FROM tf_f_recommend_new_result a,td_s_commpara b,td_s_commpara c
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (a.channel_code = :CHANNEL_CODE OR :CHANNEL_CODE IS NULL)
   AND a.deal_time+0 >= TO_DATE(:START_TIME,'yyyy-mm-dd hh24:mi:ss')
   AND a.deal_time+0 <= TO_DATE(:END_TIME,'yyyy-mm-dd hh24:mi:ss')
   AND (:PARA_CODE1 = '' OR :PARA_CODE1 IS NULL)
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND b.subsys_code(+) = 'CSM'
   AND b.param_attr(+) = '9996'
   AND b.param_code(+) = '1'
   AND b.para_code1(+) = a.channel_code
   AND b.start_date(+) <= SYSDATE
   AND b.end_date(+) >= SYSDATE
   AND UPPER(b.eparchy_code(+)) = 'ZZZZ'
   AND c.subsys_code(+) = 'CSM'
   AND c.param_attr(+) = '9996'
   AND c.param_code(+) = '2'
   AND c.para_code1(+) = a.id_type
   AND c.start_date(+) <= SYSDATE
   AND c.end_date(+) >= SYSDATE
   AND UPPER(c.eparchy_code(+)) = 'ZZZZ'
ORDER BY a.deal_time,a.execute_id