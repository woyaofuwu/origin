SELECT to_char(trade_id) trade_id,to_char(user_id) user_id,serial_number,biz_type_code,org_domain,decode(opr_source,'01','WEB','02','网上营业厅','03','WAP','04','SMS','05','MMS','06','KJAVA','07','10060','08','BOSS','其他接入') opr_source
,passwd,decode(biz_state_code,'A','正常使用','N','暂停使用','S','内部测试','T','测试待审','R','试商用','E','终止') biz_state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,DECODE(modify_tag,'0','新增','1','删除','2','修改') modify_tag,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4, to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,trade_staff_id,trade_depart_id,to_char(trade_time,'yyyy-mm-dd hh24:mi:ss') trade_time 
  FROM tf_b_trade_mbmp
 WHERE trade_id=TO_NUMBER(:TRADE_ID)