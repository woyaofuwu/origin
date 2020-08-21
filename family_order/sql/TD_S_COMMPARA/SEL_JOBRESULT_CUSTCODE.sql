select t.campn_name, ----呼出项目名称
       a.exec_staff_id, -----呼出工号
       to_char(a.exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time, -----呼出时间
       f.item_desc, --项目【题目】标示 --业务名称
       d.option_desc, -----问卷答案   是否要办理
       a.cust_code
  from ucr_crm1.TF_SMH_JOB         a,
       ucr_crm1.TF_SM_JOB_ITEM     b,
       ucr_cen1.TF_SM_TEMPLET_ITEM c,
       ucr_cen1.TF_SM_ITEM_OPTION  d,
       ucr_cen1.TF_SM_ITEM         f,
       ucr_cen1.tf_sm_campaign     t
 where 1 = 1
   and a.JOB_ID = b.JOB_ID
   and a.cust_code = :SERIAL_NUMBER 
   AND a.user_id = :USER_ID
   and a.campn_id = t.campn_id
   and c.ITEM_ID = b.item_id
   and f.item_id = b.item_id
   and d.item_id = b.item_id
   and d.option_id = b.item_result
   and c.temp_id = t.temp_id