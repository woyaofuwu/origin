select count(1) recordcount
  from tf_f_cust_groupmember g,tf_f_cust_group grp
 where g.user_id=:USER_ID
  and g.remove_tag ='0'
  and grp.remove_tag ='0' and
  grp.cust_id = g.cust_id
 and (grp.cust_id in
              (select cust_id
                from TF_F_CUST_GROUP_RELATION a where relation_type ='1'
                START WITH  rela_cust_id =:CUST_ID
                CONNECT BY PRIOR a.cust_id = a.rela_cust_id
                union
               select rela_cust_id
                from TF_F_CUST_GROUP_RELATION a where relation_type ='1'
                START WITH  rela_cust_id = :CUST_ID
                CONNECT BY PRIOR a.cust_id = a.rela_cust_id
                ) or grp.cust_id=:CUST_ID)