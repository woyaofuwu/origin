SELECT to_char(a.user_id) user_id,a.partition_id,a.serial_number,a.eparchy_code,
       a.channel_code,a.execute_id,b.execute_desc,b.id_type,b.id,b.priority,b.description,
       to_char(NVL(a.start_date,b.start_date),'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.in_time,'yyyy-mm-dd hh24:mi:ss') in_time,
       NVL(c.para_code20,d.para_code20) mod_name,
       to_char(a.trade_id) trade_id,
       to_char(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,
       a.deal_tag,a.deal_staff_id,a.deal_depart_id,a.deal_eparchy_code,
       b.rec_type rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,
       b.channel_name para_code1,b.trade_type para_code2,
       DECODE(a.deal_tag,'0','接受','1','犹豫','2','拒绝','') para_code3,
       '' para_code4,'' para_code5,'' para_code6,'' para_code7,'' para_code8,'' para_code9,'' para_code10
  FROM tf_f_recommend_new a,td_b_recommend b,td_s_commpara c,td_s_commpara d
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (a.channel_code||NULL = '0' OR a.channel_code||NULL = :CHANNEL_CODE OR NVL(:CHANNEL_CODE,'0') = '0')
   AND a.channel_code = b.channel_code
   AND a.execute_id = b.execute_id
   AND NVL(a.start_date,b.start_date) <= SYSDATE
   AND b.end_date >= SYSDATE
   AND b.status_code = '0'
   AND (:PARA_CODE1 = '' OR :PARA_CODE1 IS NULL)
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND c.subsys_code(+) = 'CSM'
   AND c.param_attr(+) = '9996'
   AND c.param_code(+) = '3'
   AND c.para_code1(+) = b.channel_code||b.id_type||b.id     --匹配到具体的业务编码的调用界面
   AND c.start_date(+) <= SYSDATE
   AND c.end_date(+) >= SYSDATE
   AND UPPER(c.eparchy_code(+)) = 'ZZZZ'
   AND d.subsys_code(+) = 'CSM'
   AND d.param_attr(+) = '9996'
   AND d.param_code(+) = '3'
   AND d.para_code1(+) = b.channel_code||b.id_type           --匹配到业务类型级，作为默认调用
   AND d.start_date(+) <= SYSDATE
   AND d.end_date(+) >= SYSDATE
   AND UPPER(D.eparchy_code(+)) = 'ZZZZ'
ORDER BY b.priority,a.channel_code,a.execute_id