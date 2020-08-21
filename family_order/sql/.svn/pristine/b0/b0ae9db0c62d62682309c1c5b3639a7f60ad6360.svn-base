--IS_CACHE=Y
SELECT acyc_id,bcyc_id,acyc_type_code,to_char(acyc_start_time,'yyyy-mm-dd hh24:mi:ss') acyc_start_time,to_char(acyc_end_time,'yyyy-mm-dd hh24:mi:ss') acyc_end_time,to_char(recv_start_time,'yyyy-mm-dd hh24:mi:ss') recv_start_time,to_char(recv_end_time,'yyyy-mm-dd hh24:mi:ss') recv_end_time,to_char(rlate_fee_time,'yyyy-mm-dd hh24:mi:ss') rlate_fee_time,to_char(nlate_fee_time1,'yyyy-mm-dd hh24:mi:ss') nlate_fee_time1,to_char(nlate_fee_time2,'yyyy-mm-dd hh24:mi:ss') nlate_fee_time2,late_fee_act_mode,to_char(late_fee_ratio1) late_fee_ratio1,to_char(late_fee_ratio2) late_fee_ratio2,use_tag,add_tag,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id 
  FROM td_a_acycpara
 WHERE acyc_id = (SELECT nvl(min(acyc_id),0) acyc_id
FROM td_a_acycpara a
WHERE use_tag = '0')