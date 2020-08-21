select * from TF_F_USER_GRP_PLATSVC t 
where t.biz_in_code = :BIZ_IN_CODE 
   and sysdate between t.start_date and t.end_date
   and t.group_id!=:GROUP_ID