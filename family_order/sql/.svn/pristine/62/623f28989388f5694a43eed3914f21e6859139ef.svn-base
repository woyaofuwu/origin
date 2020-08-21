SELECT count(*) recordcount
  FROM td_a_ingw_commpara a, tf_b_trade_discnt b
 WHERE a.para_attr = 1280
   AND b.trade_id = :TRADE_ID
   AND b.modify_tag = '0'
   AND to_number(a.para_code) = b.discnt_code
   AND SYSDATE BETWEEN a.para_date7 AND a.para_date8
   AND to_number(para_code1) <= to_number(para_code2)
   AND substr(a.eparchy_code,3,2)=substr(b.trade_id,1,2)