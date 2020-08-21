select product_id,product_name,product_explain,brand_code,group_brand_code,product_obj_type,res_type_code,comp_tag,
       enable_tag,release_tag,prepay_tag,tag_set,
   (
   select a.para_code1 from td_s_commpara a ,td_b_package_element b ,td_b_product_package c
        where c.product_id=p.product_id and c.package_id = b.package_id and b.element_id = a.param_code
        and b.element_type_code='S' and a.param_attr='4000'  and sysdate between b.start_date and b.end_date
        and sysdate between c.start_date and c.end_date   and sysdate between a.start_date and a.end_date
   ) as wide_rate
  FROM td_b_product p
 WHERE product_mode = :PRODUCT_MODE
   AND release_tag = '1'
   AND SYSDATE BETWEEN start_date AND end_date
   AND EXISTS (SELECT 1
          FROM td_b_product_release
         WHERE (release_eparchy_code = :EPARCHY_CODE OR
               release_eparchy_code = 'ZZZZ')
           AND SYSDATE BETWEEN start_date AND end_date
           AND p.product_id = product_id)