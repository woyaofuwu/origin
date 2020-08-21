SELECT to_char(sms_notice_id) sms_notice_id,eparchy_code,in_mode_code,recv_object,to_char(id) id,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') refer_time,refer_staff_id,refer_depart_id,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_staffid,deal_departid,deal_state,revc1,revc2,revc3,revc4 
  FROM ti_o_interfacesms
 WHERE deal_state=:DEAL_STATE