SELECT a.partition_id,to_char(discnt_code) user_id,to_char(a.user_id_a) user_id_a,to_number(b.para_code3) DISCNT_CODE,a.spec_tag,a.relation_type_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a,td_s_commpara b
 WHERE a.user_id=to_number(:USER_ID)
   AND a.partition_id=mod(to_number(:USER_ID),10000)
   AND a.discnt_code=b.para_code1
   AND b.para_code2 = :OLD_BRAND
   and b.para_code4 = :NEW_BRAND
   and (b.eparchy_code = :EPARCHY_CODE or b.eparchy_code = 'ZZZZ')
   and b.subsys_code='CSM'
   and b.param_attr=22