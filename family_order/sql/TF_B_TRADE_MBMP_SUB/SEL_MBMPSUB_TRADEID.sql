SELECT to_char(a.trade_id) trade_id,to_char(a.user_id) user_id,a.sp_id,d.sp_name rsrv_str2,a.biz_type_code,a.org_domain,
decode(a.opr_source,'01','WEB','02','网上营厅','03','WAP','04','SMS','05','MMS','06','KJAVA','07','10060','08','BOSS','其他接入') opr_source,a.sp_svc_id,b.biz_type rsrv_str1 ,
to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
decode(a.biz_state_code,'A','正常使用','N','暂停使用','S','内部测试','T','测试待审','R','试商用','E','终止','其他状态') biz_state_code,
billflg,DECODE(a.modify_tag,'A','新增','B','新增','N','暂停','P','预退定','E','退定','其他') modify_tag,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,to_char(a.rsrv_num4) rsrv_num4,to_char(a.rsrv_num5) rsrv_num5,
a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
a.remark,a.trade_staff_id,a.trade_depart_id,to_char(a.trade_time,'yyyy-mm-dd hh24:mi:ss') trade_time 
 FROM tf_b_trade_mbmp_sub a,td_m_spservice b ,td_m_spfactory d
  WHERE  a.sp_id=b.sp_id(+)
   AND a.sp_svc_id=b.sp_svc_id(+)
   AND a.sp_id=d.sp_id(+)
   AND a.trade_id=TO_NUMBER(:TRADE_ID)