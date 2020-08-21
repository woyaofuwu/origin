select to_char(USER_ID) PARENT_GRP_USER_ID,PRODUCT_ID
   from tf_f_user_grp_package grpPkg where exists (
   select 1 from tf_f_user usr where exists
   (
      select 1 from tf_f_cust_group_relation grpRela where grpRela.Rela_Cust_Id = usr.cust_id
         and grpRela.Relation_Type='1' and grpRela.Cust_Id=:CUST_ID
   ) and usr.remove_tag='0' and usr.product_id=:PRODUCT_ID and usr.user_id=grpPkg.User_Id
) and rsrv_str2=:RSRV_STR2 and sysdate <end_date group by user_id,product_id