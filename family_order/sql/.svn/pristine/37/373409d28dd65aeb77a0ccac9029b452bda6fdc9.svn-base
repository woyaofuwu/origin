SELECT COUNT(1) recordcount
FROM tf_b_trade_discnt WHERE trade_id = :TRADE_ID   AND (modify_tag = '1' or modify_tag = '2' )
AND EXISTS (select 1 from tf_f_user_saleproject_sub a,tf_f_user_saleproject b where a.user_id =:USER_ID
and a.sale_project_id=b.sale_project_id and a.element_type_code='D' and b.end_date>sysdate
and a.element_id=discnt_code)