update tF_F_user_res t
set t.end_date=sysdate,T.REMARK=:REMARK
where t.user_id=:USER_ID
AND T.INST_ID=:INST_ID