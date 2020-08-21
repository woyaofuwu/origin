SELECT ROWNUM,b.sp_id,sp_name,sp_name_en,sp_short_name,
sp_desc,'['||sp_status||']'||DECODE(sp_status,'A','正常','N','暂停','S','测试','其他') sp_status,
cs_tel,cs_url,
c.sp_svc_id,biz_type,biz_desc,
TO_CHAR(rsrv_date3,'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,
'['||a.opr_source||']'||DECODE(a.opr_source,'01','WEB','02','网上营业厅','03','WAP','04','SMS','05','MMS','06','KJAVA','07','1860','08','BOSS','其他') opr_source,
access_model,
NVL(TRIM(TO_CHAR(c.price/1000,'9999990.00')),'0') price,
'['||billing_type||']'||DECODE(billing_type,'0','免费','1','按条计费','2','包月计费','3','包时计费','4','包次计费','未知') billing_type,
'['||biz_status||']'||DECODE(biz_status,'A','正常','N','暂停','E','终止','S','内部测试','T','测试待审','R','试商用','其他') biz_status,
prov_addr,prov_port,
'['||a.biz_state_code||']'||decode(a.biz_state_code,'A','正常','N','暂停','E','结束','P','预退订','其他') biz_state_code,
'['||a.biz_type_code||']'||param_name biz_type_code,
TO_CHAR(a.start_Date,'YYYY-MM-DD HH24:MI:SS') start_date,
TO_CHAR(a.end_date,'YYYY-MM-DD HH24:MI:SS')end_date
 FROM (SELECT * FROM tf_f_user_mbmp_sub UNION ALL SELECT * FROM tf_fH_user_mbmp_sub) a,td_m_spfactory b,td_m_spservice c,td_s_commpara d
 WHERE a.user_id = TO_NUMBER(:USER_ID)
 AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000) 
 AND b.sp_id(+) = a.sp_id 
 AND c.sp_svc_id(+) = a.sp_svc_id 
 AND c.sp_id(+) = a.sp_id 
 AND param_attr=3 AND SYSDATE BETWEEN d.start_date AND d.end_date 
 AND (eparchy_code=:TRADE_EPARCHY_CODE OR eparchy_code='ZZZZ') 
 AND a.biz_type_code=d.param_code(+) 
 AND a.biz_state_code in('E','A') 
 AND d.para_code1=a.org_domain 
 ORDER BY start_date