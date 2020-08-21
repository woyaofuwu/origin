SELECT to_char(start_time,'yyyy-mm-dd hh24:mi:ss') start_time,to_char(excess_time,'yyyy-mm-dd hh24:mi:ss') excess_time,msisdn,other_party,reason_code,excess_count,prevalue1,prevalue2,prevalue3,prevalue4,sms_content,sms_type,infract_level,black_flag,deal_tag,result_code,result_info,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,to_char(finish_time,'yyyy-mm-dd hh24:mi:ss') finish_time,rsrv_str1,rsrv_str2,rsrv_str3 
  FROM ti_bi_mo_mon_other
 WHERE msisdn=:SERIAL_NUMBER 
   AND (deal_tag=:DEAL_TAG OR :DEAL_TAG IS NULL)