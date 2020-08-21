Select * From  TF_F_RELATION_UU t where t.relation_type_code='45' and t.user_id_b=:USER_ID_B and sysdate between t.start_date and t.end_date and t.user_id_a in(
Select a.user_id_a user_id_a From  TF_F_RELATION_UU a where a.relation_type_code='45' and a.user_id_b=:USER_ID_B2 and sysdate between a.start_date and a.end_date
)