SELECT sms_notice_id,eparchy_code,in_mode_code,
	send_object_code,send_time_code,send_count_code,recv_object_type,
	recv_object,sms_type_code,sms_kind_code,notice_content_type,
	notice_content,refered_count,force_refer_count,force_object,force_start_time,
	force_end_time,sms_priority,refer_time,refer_staff_id,refer_depart_id,
	deal_time,deal_staffid,deal_departid,
	decode(deal_state,'0','未处理','1','已处理','2','超时未处理','3','超时已处理',NULL) deal_state,
	remark,revc1,revc2,revc3,revc4,month
  FROM UCR_UEC.TI_O_SMS 
 WHERE notice_content NOT LIKE '%密码%' 
   AND recv_object=:SERIAL_NUMBER 
   AND (eparchy_code=:EPARCHY_CODE OR :EPARCHY_CODE IS NULL)
   AND sms_type_code = :SMS_TYPE_CODE
   UNION
   SELECT sms_notice_id,eparchy_code,in_mode_code,
	send_object_code,send_time_code,send_count_code,recv_object_type,
	recv_object,sms_type_code,sms_kind_code,notice_content_type,
	notice_content,refered_count,force_refer_count,force_object,force_start_time,
	force_end_time,sms_priority,refer_time,refer_staff_id,refer_depart_id,
	deal_time,deal_staffid,deal_departid,
	decode(deal_state,'0','未处理','1','已处理','2','超时未处理','3','超时已处理',NULL) deal_state,
  remark,revc1,revc2,revc3,revc4,month
  FROM UCR_UEC.TI_OH_SMS 
 WHERE notice_content NOT LIKE '%密码%' 
   AND recv_object=:SERIAL_NUMBER 
   AND (eparchy_code=:EPARCHY_CODE OR :EPARCHY_CODE IS NULL)
   AND sms_type_code = :SMS_TYPE_CODE
   ORDER BY deal_time DESC