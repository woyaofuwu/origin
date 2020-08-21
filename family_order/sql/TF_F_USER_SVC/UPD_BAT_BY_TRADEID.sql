UPDATE TF_F_USER_SVC s set(end_date,update_time,update_staff_id,update_depart_id)=
(
select end_date,update_time,update_staff_id,update_depart_id from tf_b_trade_svc t
where trade_id = TO_NUMBER(:TRADE_ID)
AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
and modify_tag = '1'
and s.user_id = t.user_id
and s.user_id_a = t.user_id_a
and s.service_id = t.service_id
)
where exists(
select 1 from tf_b_trade_svc v
where s.user_id = v.user_id
and s.user_id_a = v.user_id_a
and s.service_id = v.service_id
and modify_tag = '1'
AND trade_id = TO_NUMBER(:TRADE_ID)
AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
)
and end_date  > sysdate