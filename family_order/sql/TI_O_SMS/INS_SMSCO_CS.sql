DECLARE
v_seqid number(16);
BEGIN
SELECT f_sys_getseqid(:EPARCHY_CODE,'seq_smssend_id') into v_seqid FROM dual;
INSERT INTO ti_o_sms(sms_notice_id,partition_id,send_count_code,refered_count,eparchy_code,in_mode_code,chan_id,recv_object_type,recv_object,recv_id,sms_type_code,sms_kind_code,notice_content_type,notice_content,force_refer_count,sms_priority,refer_time,refer_staff_id,refer_depart_id,deal_time,deal_state,remark,send_time_code,send_object_code,force_object,FORCE_START_TIME,FORCE_END_TIME)
SELECT v_seqid,mod(v_seqid,1000),'1','0',:EPARCHY_CODE,:IN_MODE_CODE,
       'C006',                          --短信渠道编码销售管理
       '00',                            --被叫对象类型00－手机号码
       :SERIAL_NUMBER,                 --被叫对象传手机号码
       NVL(TO_NUMBER(:USER_ID),0),     --被叫对象标识传用户标识
       '20',                            --短信类型20-业务通知
       '08',                            --短信种类个人业务，集团，客户管理都填08
       '0',                             --短信内容类型0－指定内容发送
       substrb(:NOTICE_CONTENT,0,500),                --短信内容
       1,                               --指定发送次数
       :PRIORITY,                      --短信优先级
       SYSDATE,                         --提交时间
       :STAFF_ID,                      --提交员工
       :DEPART_ID,                     --提交部门
       SYSDATE,                        --处理时间
       '15',                            --处理状态0－未处理
       :REMARK,                        --备注
       1,6  ,
       :FORCE_OBJECT,                  --指定发送号码
       TO_DATE(:FORCE_START_TIME, 'YYYY-MM-DD HH24:MI:SS') ,             --指定发送起始时间
       TO_DATE(:FORCE_END_TIME, 'YYYY-MM-DD HH24:MI:SS')                --指定发送终止时间           
  FROM dual;
END;