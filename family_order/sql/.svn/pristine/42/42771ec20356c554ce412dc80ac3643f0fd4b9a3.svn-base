INSERT INTO TI_BI_SMS(sms_notice_id,eparchy_code,in_mode_code,sms_channel_code,recv_object_type,recv_object,id,sms_type_code,sms_kind_code,notice_content_type,notice_content,force_refer_count,sms_priority,refer_time,refer_staff_id,refer_depart_id,deal_time,deal_state,remark,send_time_code,send_object_code)
SELECT TO_NUMBER(f_sys_getseqid(:EPARCHY_CODE,'seq_smssend_id')),:EPARCHY_CODE,:IN_MODE_CODE,
       '11',                            --短信渠道编码:客户服务
       '00',                            --被叫对象类型:00－手机号码
       :SERIAL_NUMBER,                 --被叫对象:传手机号码
       NVL(TO_NUMBER(:USER_ID),0),     --被叫对象标识:传用户标识
       '20',                            --短信类型:20-业务通知
       '02',                            --短信种类:02－短信通知
       '0',                             --短信内容类型:0－指定内容发送
       :NOTICE_CONTENT,                --短信内容
       1,                               --指定发送次数
       :PRIORITY,                      --短信优先级
       SYSDATE,                         --提交时间
       :STAFF_ID,                      --提交员工
       :DEPART_ID,                     --提交部门
       SYSDATE,                         --处理时间
       '0',                             --处理状态:0－未处理
       :REMARK,                        --备注
       1,1 
  FROM dual