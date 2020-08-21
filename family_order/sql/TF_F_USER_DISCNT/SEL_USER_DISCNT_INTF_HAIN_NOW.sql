SELECT a.discnt_code element_id,
       'D' element_type_code,'0' main_tag,a.Inst_Id,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,'EXIST' STATE,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       b.attr_code,b.attr_value,a.user_id_a
       FROM tf_f_user_discnt a,tf_f_user_attr b
       WHERE a.partition_id=b.partition_id
	   AND a.User_Id=b.User_Id
       AND a.Inst_Id=b.RELA_INST_ID
       AND a.User_Id= TO_NUMBER(:USER_ID)
       AND a.partition_id= MOD(TO_NUMBER(:USER_ID), 10000)
       AND b.Inst_Type='D'
       AND a.end_date>SYSDATE
       AND b.end_date>SYSDATE
UNION ALL
SELECT a.discnt_code element_id,
       'D' element_type_code,'0' main_tag,a.Inst_Id,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,'EXIST' STATE,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       '' attr_code,'' attr_value,a.user_id_a
       FROM tf_f_user_discnt a
       WHERE  a.User_Id= TO_NUMBER(:USER_ID)
       AND a.partition_id= MOD(TO_NUMBER(:USER_ID), 10000)
       AND ((sysdate between a.start_date and a.end_date) or (a.start_date between sysdate and a.end_date))
       AND NOT EXISTS (SELECT 1 FROM tf_f_user_attr c
       WHERE a.User_Id=c.User_Id
       AND a.Inst_Id=c.RELA_INST_ID
       AND c.Inst_Type='D'
       AND c.end_date>SYSDATE)