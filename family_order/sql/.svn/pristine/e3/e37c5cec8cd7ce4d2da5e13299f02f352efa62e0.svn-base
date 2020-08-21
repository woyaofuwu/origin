SELECT apply_id,apply_name,oper_type,apply_mode,apply_type,apply_state,apply_province,to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date,to_char(valid_date,'yyyy-mm-dd hh24:mi:ss') valid_date,father_cust_id,applyer,applyer_phone,rela_si_id,group_linkman,group_link_no,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,contract_code,contract_no,contract_info,rela_apply_id,tag_set,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_b_trade_span_apply
 WHERE apply_id=:APPLY_ID
   AND apply_state=:APPLY_STATE
   AND SYSDATE BETWEEN start_date AND end_date