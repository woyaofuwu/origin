select to_char(USER_ID) USER_ID,PRODUCT_ID,PACKAGE_ID,ELEMENT_TYPE_CODE,ELEMENT_ID,
   DEFAULT_TAG,FORCE_TAG,EPARCHY_CODE,rsrv_num1,rsrv_num2,to_char(rsrv_num3) rsrv_num3,
   to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5
   from tf_f_user_grp_package grpPkg where exists (
   select 1 from tf_f_user usr where exists
   (
      select 1 from tf_f_cust_group_relation grpRela where grpRela.Rela_Cust_Id = usr.cust_id
         and grpRela.Relation_Type='1' and grpRela.Cust_Id=:CUST_ID
   ) and usr.remove_tag='0' and usr.product_id=:PRODUCT_ID and usr.user_id=grpPkg.User_Id
) and rsrv_str2=:RSRV_STR2