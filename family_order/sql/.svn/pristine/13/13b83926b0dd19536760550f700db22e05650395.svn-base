INSERT INTO ti_o_sms(
   sms_notice_id,eparchy_code,recv_object,id,notice_content,refer_staff_id,refer_depart_id,
   in_mode_code,sms_channel_code,send_object_code,send_time_code,
   recv_object_type,sms_type_code,sms_kind_code,notice_content_type,
   force_refer_count,sms_priority,refer_time,deal_time,deal_state
)
SELECT a.sms_notice_id,a.eparchy_code,a.serial_number recv_object,a.user_id id,
       '尊敬的客户，您目前正在使用的'||NVL(a.discnt_explain,SUBSTR(a.discnt_name,1,40))||
       '优惠将于'||TO_CHAR(end_date,'yyyy-mm-dd')||'日到期，如需继续使用请到营业厅办理，谢谢！' notice_content,
       a.deal_staff_id refer_staff_id,a.deal_depart_id refer_depart_id,
       '0' in_mode_code,'11' sms_channel_code,'1' send_object_code,'2' send_time_code,
       '00' recv_object_type,'20' sms_type_code,'02' sms_kind_code,'0' notice_content_type,
       1 force_refer_count,50 sms_priority,SYSDATE refer_time,SYSDATE deal_time,'0' deal_state
  FROM tf_b_discnt_end_notice a
 WHERE a.bcyc_id = :BCYC_ID
   AND a.eparchy_code = :TRADE_EPARCHY_CODE
   AND a.discnt_code = :DISCNT_CODE
   AND TO_CHAR(a.end_date,'yyyy-mm') = :END_DATE
   AND a.deal_state = '1'