insert into ti_o_sms(
SMS_NOTICE_ID,PARTITION_ID,EPARCHY_CODE,BRAND_CODE,IN_MODE_CODE,SMS_NET_TAG,CHAN_ID, SEND_OBJECT_CODE, SEND_TIME_CODE,SEND_COUNT_CODE,RECV_OBJECT_TYPE, RECV_OBJECT, RECV_ID,SMS_TYPE_CODE, SMS_KIND_CODE,NOTICE_CONTENT_TYPE,NOTICE_CONTENT,REFERED_COUNT,FORCE_REFER_COUNT,FORCE_OBJECT, FORCE_START_TIME, FORCE_END_TIME, SMS_PRIORITY, REFER_TIME,REFER_STAFF_ID,REFER_DEPART_ID, DEAL_TIME, DEAL_STAFFID,DEAL_DEPARTID, DEAL_STATE, REMARK, REVC1,REVC2,REVC3,REVC4, MONTH, DAY)
select TO_NUMBER(t.rsrv_str1),
       MOD(TO_NUMBER(t.rsrv_str1), 1000),
       b.EPARCHY_CODE,
       b.brand_code,
       b.in_mode_code,
       '0',
       t.UPDATE_DEPART_ID,
       0,
       1,
       1,
       '00',
       to_number(t.remark),
       to_number(t.rsrv_str2),
       t.update_staff_id,
       t.rsrv_str3,
       t.rsrv_str4,
       '0',
       t.rsrv_str8,
       0,
       1,
       t.update_time,
       '',
       1000,
       b.update_time,
       b.update_staff_id,
       b.update_depart_id,
       sysdate,
       '',
       '',
       '0',
       '',
       '',
       '',
       '',
       '',
       TO_NUMBER(TO_CHAR(SYSDATE, 'MM')),
       TO_NUMBER(TO_CHAR(SYSDATE, 'DD'))
from tf_b_trade_ext t,tf_b_trade b
where t.trade_id=b.trade_id 
and  t.ACCEPT_MONTH=b.ACCEPT_MONTH
and  t.attr_code='GrpSms'
and  t.rsrv_str1=to_char(:TRADE_ID)