SELECT to_char(sms_notice_id) sms_notice_id,eparchy_code,in_mode_code,send_object_code,send_time_code,send_count_code,recv_object_type,recv_object,sms_type_code,sms_kind_code,notice_content_type,notice_content,refered_count,force_refer_count,force_object,to_char(force_start_time,'yyyy-mm-dd hh24:mi:ss') force_start_time,to_char(force_end_time,'yyyy-mm-dd hh24:mi:ss') force_end_time,sms_priority,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') refer_time,refer_staff_id,refer_depart_id,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_staffid,deal_departid,deal_state,remark,
revc1,revc2,revc3,revc4,month 
  FROM ti_o_sms
 WHERE recv_object=:RECV_OBJECT
   and revc1 =:REVC1
   and revc2 =:REVC2
   AND refer_time >= trunc(SYSDATE)
   AND refer_time < trunc(SYSDATE+1)

union all
SELECT to_char(sms_notice_id) sms_notice_id,eparchy_code,in_mode_code,send_object_code,send_time_code,send_count_code,recv_object_type,recv_object,sms_type_code,sms_kind_code,notice_content_type,notice_content,refered_count,force_refer_count,force_object,to_char(force_start_time,'yyyy-mm-dd hh24:mi:ss') force_start_time,to_char(force_end_time,'yyyy-mm-dd hh24:mi:ss') force_end_time,sms_priority,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') refer_time,refer_staff_id,refer_depart_id,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_staffid,deal_departid,deal_state,remark,
revc1,revc2,revc3,revc4,month 
  FROM ti_oh_sms
 WHERE recv_object=:RECV_OBJECT
   and revc1 =:REVC1
   and revc2 =:REVC2
   AND refer_time >= trunc(SYSDATE)
   AND refer_time < trunc(SYSDATE+1)
  
