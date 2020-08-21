select r.offer_code PRODUCT_ID,R.GROUP_ID PACKAGE_ID,t.* 
  from TF_F_USER_DISCNT t,TF_F_USER_OFFER_REL R,td_s_commpara t1
 where t.discnt_code = t1.PARA_CODE1
   and t1.param_code = '687'
   and t1.param_attr = '532'
   and t1.SUBSYS_CODE = 'CSM'
   and t.inst_id = r.rel_offer_ins_id
   and r.offer_type = 'P'
   and sysdate < t1.end_date
   and sysdate < r.end_date
   and sysdate < t.end_date
   and t.user_id = :USER_ID