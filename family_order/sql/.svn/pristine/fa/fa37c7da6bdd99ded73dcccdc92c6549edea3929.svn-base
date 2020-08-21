--IS_CACHE=Y
SELECT count(*)
  FROM td_b_product p,td_b_ptype_product b
 WHERE release_tag='1'    --发布状态：1-已发布
   AND sysdate BETWEEN p.start_date AND p.end_date
	 and p.product_id = b.product_id
	 and b.product_type_code = :PRODUCT_TYPE_CODE
	 and sysdate BETWEEN b.start_date AND b.end_date
   AND NOT EXISTS (select 1 from td_s_commpara
                    where subsys_code='CSM'
                     and param_attr=99
                     and param_code=to_char(p.product_id)
                     and PARA_CODE1='10'
                     and EPARCHY_CODE=:TRADE_EPARCHY_CODE
                     and sysdate between start_date and end_date)
   AND EXISTS (SELECT 1 FROM td_b_product_release
                WHERE (release_eparchy_code = :TRADE_EPARCHY_CODE OR release_eparchy_code = 'ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date
                AND product_id = p.product_id)
order by b.order_no