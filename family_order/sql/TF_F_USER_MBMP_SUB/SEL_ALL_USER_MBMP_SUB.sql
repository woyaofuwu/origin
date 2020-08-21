SELECT a.org_domain||decode(a.biz_state_code,'A','正常','N','暂停','E','结束','P','预退订','其他') sp_name_en,

       '['||a.sp_svc_id||']'||c.biz_type biz_type,

       a.sp_id sp_id,

       a.sp_svc_id sp_svc_id,

       a.biz_type_code biz_type_code,

       '['||a.sp_id||']'||b.sp_name sp_name,

       start_date,

       end_date,

       b.sp_short_name,

       b.sp_svc_id rsrv_str3,

       DEcode(C.billing_type,'0','免费','1','按条计费','2','包月计费','3','包时计费','4','包次计费','未知') billing_type,

       trim(to_char(c.price/1000,'9999990.00')) rsrv_str1,

       decode(a.opr_source,'01','WEB','02','网上营业厅','03','WAP','04','SMS','05','MMS','06','KJAVA','07','1860','08','BOSS','其他') rsrv_str2,

       c.biz_desc biz_desc,

       b.cs_tel cs_tel

 FROM tf_f_user_mbmp_sub a,td_m_spfactory b,td_m_spservice c

WHERE a.user_id = to_number(:USER_ID)

AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)

AND a.end_date>SYSDATE

AND b.sp_id(+) = a.sp_id

AND c.sp_svc_id(+) = a.sp_svc_id

AND c.sp_id(+) = a.sp_id

AND EXISTS(SELECT 1

FROM td_s_tag

WHERE tag_code='PUB_CUR_PROVINCE'

AND use_tag='0'

AND start_date+0<sysdate

AND subsys_code='PUB'

AND end_date+0>=sysdate

AND INSTR(DECODE(tag_info,'HAIN','AN',biz_state_code),biz_state_code)>0

)