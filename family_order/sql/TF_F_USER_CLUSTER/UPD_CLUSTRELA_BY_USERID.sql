update TF_F_USER_CLUSTER_RELA 
         set end_date = sysdate , rsrv_str1 ='用户销户终止群关系'
         where  group_id =:GROUP_ID