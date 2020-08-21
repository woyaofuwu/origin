select /*+ first_rows(1) index(d IDX_TF_B_TRADE_DISCNT_TID)*/ 
      count(1) recordcount
 from  td_b_package_element p, tf_b_trade_discnt d
 where d.accept_month = to_number(substr(:TRADE_ID, 5, 2))
  and d.trade_id = :TRADE_ID
  and d.user_id = :USER_ID
  and d.modify_tag in('4','0')
  and p.element_type_code = 'D'
  and p.enable_tag != '1'
  and p.package_id = :PACKAGE_ID
  and d.discnt_code = p.element_id
  and rownum < 2