SELECT PARTITION_ID,
USER_ID,
a.SP_ID,
a.biz_type_code,
ORG_DOMAIN,
OPR_SOURCE,
a.SP_SVC_ID,
a.START_DATE,
a.END_DATE,
decode(a.biz_state_code,'A','正常使用','N','暂停使用','S','内部测试','T','测试待审','R','试商用','E','终止','其他') biz_state_code,
decode(a.BILLFLG,'0','免费','1','按条计费','2','包月计费','3','包时计费','4','包次计费','其他方式') BILLFLG,
a.RSRV_NUM1,
a.RSRV_NUM2,
a.RSRV_NUM3,
to_char(a.RSRV_NUM4) RSRV_NUM4,
to_char(a.RSRV_NUM5) RSRV_NUM5,
b.RSRV_STR4  rsrv_str1,
c.sp_name   rsrv_str2,
a.RSRV_STR3 RSRV_STR3 ,
a.RSRV_STR4 RSRV_STR4,
a.RSRV_STR5 RSRV_STR5,
to_char(a.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,
to_char(a.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,
to_char(a.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,
a.REMARK,
a.UPDATE_STAFF_ID,
a.UPDATE_DEPART_ID,
a.UPDATE_TIME,
a.SERIAL_NUMBER  
FROM tf_f_user_mbmp_sub a ,td_m_spservice b , td_m_spfactory c
WHERE a.sp_svc_id=b.sp_svc_id(+)
AND a.sp_id=b.sp_id(+)
AND b.sp_id= c.sp_id(+)
AND a.biz_state_code='A'
AND a.user_id=to_number(:USER_ID)
AND a.partition_id=MOD(to_number(:USER_ID),10000)
and sysdate between a.start_date and end_date