DECLARE
V_SEQID NUMBER(16);
BEGIN
  SELECT F_SYS_GETSEQID(:EPARCHY_CODE, 'seq_smssend_id')
    INTO V_SEQID
    FROM DUAL;
  INSERT INTO TI_O_SMS
    (SMS_NOTICE_ID,
     PARTITION_ID,
     SEND_COUNT_CODE,
     REFERED_COUNT,     
     EPARCHY_CODE,
     IN_MODE_CODE,
     CHAN_ID,
     RECV_OBJECT_TYPE,
     RECV_OBJECT,
     RECV_ID,
     SMS_TYPE_CODE,
     SMS_KIND_CODE,
     NOTICE_CONTENT_TYPE,
     NOTICE_CONTENT,
     FORCE_REFER_COUNT,
     FORCE_OBJECT,
     SMS_PRIORITY,
     REFER_TIME,
     REFER_STAFF_ID,
     REFER_DEPART_ID,
     DEAL_TIME,
     DEAL_STATE,
     REMARK,
     SEND_TIME_CODE,
     SEND_OBJECT_CODE)
    SELECT V_SEQID,
           MOD(V_SEQID, 1000),
           '1',
           '0',           
           :EPARCHY_CODE,
           :IN_MODE_CODE,
           '11', --短信渠道编码:客户服务
           '00', --被叫对象类型:00－手机号码
           :SERIAL_NUMBER, --被叫对象:传手机号码
           NVL(TO_NUMBER(:USER_ID), 0), --被叫对象标识:传用户标识
           '20', --短信类型:20-业务通知
           '08', --短信种类:02－短信通知
           '0', --短信内容类型:0－指定内容发送
           :NOTICE_CONTENT, --短信内容
           1, --指定发送次数
           :FORCE_OBJECT,
           :PRIORITY, --短信优先级
           SYSDATE, --提交时间
           :STAFF_ID, --提交员工
           :DEPART_ID, --提交部门
           SYSDATE, --处理时间
           '0', --处理状态:0－未处理
           :REMARK, --备注
           1,
           6
      FROM DUAL;
END;