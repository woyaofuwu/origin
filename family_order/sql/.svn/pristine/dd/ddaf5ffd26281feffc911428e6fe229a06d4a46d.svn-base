UPDATE TF_F_PRODUCT_POLICY a
   SET end_date=(SELECT start_date-1/24/3600 FROM TF_B_TRADE_PRODUCTPOLICY b
                  WHERE trade_id = :TRADE_ID
                    and sysdate between a.start_date and a.end_date
                    and rownum<2)
 WHERE exists (select 1 from TF_B_TRADE_PRODUCTPOLICY  c where a.PORateNumber=c.PORateNumber
   and c.user_id=a.user_id
   and c.POSPECNUMBER=a.POSPECNUMBER
   and c.PRODUCTSPECNUMBER =a.PRODUCTSPECNUMBER 
   and trade_id = :TRADE_ID
   and sysdate between a.start_date and a.end_date
   and oper_code <> '1')