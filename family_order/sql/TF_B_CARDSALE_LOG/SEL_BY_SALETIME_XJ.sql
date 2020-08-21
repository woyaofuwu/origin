select eparchy_code,city_code,stock_id,res_kind_code,value_code,sum(sale_num) sale_num,sum(sale_money) sale_money,sum(old_money) old_money
  from tf_b_cardsale_log
 where eparchy_code=:EPARCHY_CODE
   and res_type_code = '3' 
   and (:CITY_CODE is null or city_code=:CITY_CODE)
   and (:STOCK_ID is null or stock_id =:STOCK_ID)
   and (:SALE_TYPE_CODE is null or sale_type_code = :SALE_TYPE_CODE)
   and sale_time >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and sale_time <=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')   
 group by eparchy_code,city_code,stock_id,res_kind_code,value_code