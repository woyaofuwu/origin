SELECT sms_notice_id para_code1, sms_recv_object para_code2, notice_content para_code3, refer_time para_code4, refer_staff_id para_code5, deal_time para_code6,
decode(deal_state,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') para_code7, 
'' para_code8, nvl(sms_send_object,'10086') para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name  
FROM UCR_UEC.tl_bh_sms_:STAT_MONTH_BEFORE
where sms_recv_object = :PARA_CODE1
AND deal_time >= to_date(:PARA_CODE2,'yyyy-mm-dd')
AND deal_time <= to_date(:PARA_CODE3,'yyyy-mm-dd') + 1
AND notice_content  NOT LIKE '%密码%'
union all
SELECT sms_notice_id para_code1, sms_recv_object para_code2, notice_content para_code3, refer_time para_code4, refer_staff_id para_code5, deal_time para_code6,
decode(deal_state,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') para_code7, 
'' para_code8, nvl(sms_send_object,'10086') para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name  
FROM UCR_UEC.tl_bh_sms_:STAT_MONTH_AFTER
where sms_recv_object = :PARA_CODE1
AND deal_time >= to_date(:PARA_CODE2,'yyyy-mm-dd')
AND deal_time <= to_date(:PARA_CODE3,'yyyy-mm-dd') + 1
AND notice_content  NOT LIKE '%密码%'