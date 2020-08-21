SELECT decode(b.param_code,'3','1','4','2',b.param_code) para_code1,'' para_code2,
'' para_code3,
'' para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_user_discnt a,td_s_commpara b
 WHERE a.user_id=TO_NUMBER(:USER_ID)
  AND a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
 and a.discnt_code=b.para_code3
  AND sysdate between trunc(a.start_date) AND trunc(add_months(a.end_date,1),'MM')-1
  AND b.param_attr=:PARAM_ATTR
  AND (b.eparchy_code=:EPARCHY_CODE 
   OR b.eparchy_code='ZZZZ')
　AND b.subsys_code=:SUBSYS_CODE
  and length(b.param_code)=1
  order by b.param_code desc