SELECT to_char(sms_notice_id) sms_notice_id,eparchy_code,in_mode_code,sms_channel_code,send_object_code,send_time_code,send_count_code,recv_object_type,recv_object,to_char(id) id,sms_type_code,sms_kind_code,notice_content_type,notice_content,refered_count,force_refer_count,force_object,to_char(force_start_time,'yyyy-mm-dd hh24:mi:ss') force_start_time,to_char(force_end_time,'yyyy-mm-dd hh24:mi:ss') force_end_time,sms_priority,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') refer_time,refer_staff_id,refer_depart_id,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_staffid,deal_departid,deal_state,remark,decode(sms_channel_code,
'11','客户服务',
'12','客户管理',
'13','帐务管理',
'14','产品管理',
'15','订单管理',
'16','资源管理',
'17','合作伙伴管理',
'18','渠道管理',
'19','营销管理',
'20','系统管理',
'21','系统监控',
'22','统计分析',
'23','融合计费',
'24','帐务处理',
'25','综合结算',
'26','综合采集',
'27','服务开通',
'28','信用管理','未知')
revc1,revc2,revc3,revc4,month 
  FROM ti_o_sms
 WHERE recv_object=:RECV_OBJECT
   AND sms_kind_code=:SMS_KIND_CODE
   AND refer_time between TO_DATE(:REFER_TIME, 'YYYYMMDD') and TO_DATE(:END_DATE, 'YYYYMMDD')+1
union all
SELECT to_char(sms_notice_id) sms_notice_id,eparchy_code,in_mode_code,sms_channel_code,send_object_code,send_time_code,send_count_code,recv_object_type,recv_object,to_char(id) id,sms_type_code,sms_kind_code,notice_content_type,notice_content,refered_count,force_refer_count,force_object,to_char(force_start_time,'yyyy-mm-dd hh24:mi:ss') force_start_time,to_char(force_end_time,'yyyy-mm-dd hh24:mi:ss') force_end_time,sms_priority,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') refer_time,refer_staff_id,refer_depart_id,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_staffid,deal_departid,deal_state,remark,decode(sms_channel_code,
'11','客户服务',
'12','客户管理',
'13','帐务管理',
'14','产品管理',
'15','订单管理',
'16','资源管理',
'17','合作伙伴管理',
'18','渠道管理',
'19','营销管理',
'20','系统管理',
'21','系统监控',
'22','统计分析',
'23','融合计费',
'24','帐务处理',
'25','综合结算',
'26','综合采集',
'27','服务开通',
'28','信用管理','未知')
revc1,revc2,revc3,revc4,month 
  FROM ti_oh_sms
 WHERE recv_object=:RECV_OBJECT
   AND sms_kind_code=:SMS_KIND_CODE
   AND refer_time between TO_DATE(:REFER_TIME, 'YYYYMMDD') and TO_DATE(:END_DATE, 'YYYYMMDD')+1