select t.user_id, t.product_id, t.package_id
  from Tf_f_User_sale_active t
 where t.user_id = :USER_ID
   and t.product_id in ('66000232','66000233')
union
select t.user_id, t.product_id, t.package_id
  from Tf_f_User_saleactive_book t
 where t.user_id = :USER_ID
   and t.product_id in ('66000232','66000233')