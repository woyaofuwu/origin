select cl.acct_id MAIN_ACCT_ID,p.acct_id SUB_ACCT_ID,p.INST_ID INST_ID,p.START_CYCLE_ID START_CYCLE_ID,p.END_CYCLE_ID END_CYCLE_ID,u.SERIAL_NUMBER SERIAL_NUMBER,u.NET_TYPE_CODE NET_TYPE_CODE,u.PRODUCT_ID PRODUCT_ID,u.EPARCHY_CODE EPARCHY_CODE,u.CITY_CODE CITY_CODE,u.USER_ID USER_ID,r.ROLE_CODE ROLE_CODE from TF_F_USER_CLUSTER_RELA r, TF_F_USER u,TF_A_PAYRELATION p,TF_F_USER_CLUSTER cl
  where u.user_id = r.user_id
   and r.user_id = p.user_id
   and cl.group_id = r.group_id
   and p.act_tag = '1'
   and p.default_tag = '1'
   and TO_CHAR(SYSDATE,'YYYYMM')between  p.start_cycle_id and p.end_cycle_id
    and exists
       (select 1
         from TF_F_USER_CLUSTER c
         where
         c.group_id = r.group_id 
         and c.type_id = 'HA'
         and c.leader_user_id = TO_NUMBER(:USER_ID)
         and sysdate between c.START_DATE and c.END_DATE
         )