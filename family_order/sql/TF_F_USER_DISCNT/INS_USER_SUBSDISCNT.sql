INSERT INTO tf_f_user_subsdiscnt
  (trade_id, accept_month, user_id, user_id_a, package_id, product_id, discnt_code, spec_tag, relation_type_code, inst_id, campn_id, start_date, end_date, modify_tag, update_time, update_staff_id, update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3)
SELECT trade_id, accept_month, user_id, user_id_a, package_id, product_id, discnt_code, spec_tag, relation_type_code, inst_id, campn_id, start_date, end_date, modify_tag, update_time, update_staff_id, update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3
FROM tf_b_trade_discnt a
WHERE a.trade_id = to_number(:TRADE_ID)
AND a.accept_month = to_number(substr(:TRADE_ID,5,2))
AND a.user_id = to_number(:USER_ID)
AND NOT EXISTS (
    SELECT 1 FROM tf_f_user_subsdiscnt b
    WHERE b.trade_id=a.trade_id
    AND b.user_id=a.user_id
    AND b.user_id_a=a.user_id_a
    AND b.discnt_code=a.discnt_code
    AND b.product_id=a.product_id
    AND b.package_id=a.package_id
    AND b.start_date=a.start_date
)
AND a.modify_tag <> '1'
AND a.start_date > SYSDATE
AND a.start_date < a.end_date
AND a.end_date > SYSDATE