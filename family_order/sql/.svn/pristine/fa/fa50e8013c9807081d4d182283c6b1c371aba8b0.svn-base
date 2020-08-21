SELECT a.service_id element_id,
       'S' element_type_code,a.main_tag,a.inst_id,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,'EXIST' STATE,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       b.attr_code,b.attr_value,'0' force_tag
       FROM tf_f_user_svc a,tf_f_user_attr b
       WHERE a.partition_id=b.partition_id
	   AND a.user_id=b.user_id
       AND a.inst_id=b.RELA_INST_ID
       AND a.product_id=-1
       AND a.package_id=-1
       AND a.User_Id= TO_NUMBER(:USER_ID)
       AND a.partition_id= MOD(TO_NUMBER(:USER_ID), 10000)
       AND b.Inst_Type='S'
       AND a.end_date>SYSDATE
       AND b.end_date>SYSDATE
UNION
SELECT a.service_id element_id,
       'S' element_type_code,a.main_tag,a.inst_id,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,'EXIST' STATE,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       '' attr_code,'' attr_value,'0' force_tag
       FROM tf_f_user_svc a
       WHERE  a.product_id=-1
       AND a.package_id=-1
       AND a.User_Id= TO_NUMBER(:USER_ID)
       AND a.partition_id= MOD(TO_NUMBER(:USER_ID), 10000)
       AND a.end_date>SYSDATE
       AND NOT EXISTS (SELECT 1 FROM tf_f_user_attr c
       WHERE a.partition_id=c.partition_id
	   AND c.partition_id= MOD(TO_NUMBER(:USER_ID), 10000)
	   AND a.User_Id=c.User_Id
       AND a.Inst_Id=c.RELA_INST_ID
       AND c.Inst_Type='S'
       AND c.end_date>SYSDATE)