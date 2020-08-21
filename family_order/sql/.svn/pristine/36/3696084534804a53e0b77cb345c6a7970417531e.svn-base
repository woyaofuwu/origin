   SELECT  
    a.cust_name
  FROM tf_f_customer a 	
 WHERE 1=1
   AND a.pspt_type_code=:PSPT_TYPE_CODE
   AND a.pspt_id=:PSPT_ID
   AND a.CUST_NAME=:PSPT_NAME
   AND a.CUST_NAME is not null 
   AND a.REMOVE_TAG='0'
   and a.cust_type='0'
   and  exists (select 1 from tf_f_user b where b.cust_id=a.cust_id and b.remove_tag='0' and b.acct_tag='0')
   and not exists (select 1
          from tf_f_relation_uu r, tf_f_user u,td_s_commpara c
         where u.cust_id = a.cust_id
           and u.remove_tag = '0'
           and u.acct_tag = '0'
           and r.user_id_a = u.user_id
           and c.subsys_code = 'CSM'
           and c.param_attr = 1842
           and c.param_code = 'CUSTOMER'
           and c.para_code1 = r.relation_type_code 
           and sysdate between c.start_date and c.end_date
           )
