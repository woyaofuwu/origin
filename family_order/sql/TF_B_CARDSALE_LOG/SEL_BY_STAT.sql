SELECT res_kind_code,to_char(sum(sale_num)) sale_num,to_char(sum(sale_money)/100) sale_money,to_char(sum(old_money)/100) old_money,value_code,ROUND(sum(sale_money)/(sum(sale_num)*100),2) rsrv_num1,b.para_value2 rsrv_str1
  FROM tf_b_cardsale_log a,(select para_code2,para_value2 from td_m_assignpara where para_attr=1001) b
 WHERE log_id=TO_NUMBER(:LOG_ID)
 and to_char(a.product_id)=b.para_code2(+)
 GROUP BY res_kind_code,value_code,sale_money,old_money,b.para_value2