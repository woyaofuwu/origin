SELECT to_char(c.start_date,'YYYY-MM-DD'),to_char(a.accept_date,'YYYY-MM-DD'),to_char(c.TRADE_ID) TRADE_ID,c.ACCEPT_MONTH,to_char(c.USER_ID) USER_ID,c.SALE_PROJECT_ID,c.SALE_PACKAGE_ID,c.ELEMENT_TYPE_CODE,c.ELEMENT_ID,c.PARA_CODE1,c.PARA_CODE2,c.PARA_CODE3,c.PARA_CODE4,c.PARA_CODE5,c.PARA_CODE6,c.PARA_CODE7,c.PARA_CODE8,c.MODIFY_TAG,to_char(c.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(c.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE
  FROM TF_B_TRADE_SALEPROJECT_SUB c,TF_B_TRADE a
 WHERE c.trade_id=TO_NUMBER(:TRADE_ID)
   AND c.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))  
   AND a.trade_id=TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))    
   AND ((c.start_date>a.accept_date and c.modify_tag ='0'AND 
        ((c.ELEMENT_TYPE_CODE='S' and EXISTS (SELECT element_id  FROM td_o_service_olcom b 
                                 where b.service_id=c.element_id
                                  and exists (SELECT 1 FROM td_o_olcomservcode
                                                             WHERE olcom_serv_code = b.open_olcom_serv_code
                                                              AND SYSDATE BETWEEN start_date AND end_date)))
        or  c.ELEMENT_TYPE_CODE='Y'))
        or (c.end_date>a.accept_date and c.modify_tag ='1' AND
        ((c.ELEMENT_TYPE_CODE='S' and EXISTS (SELECT element_id  FROM td_o_service_olcom b 
                                 where b.service_id=c.element_id
                                  and exists (SELECT 1 FROM td_o_olcomservcode
                                                             WHERE olcom_serv_code = b.close_olcom_serv_code
                                                              AND SYSDATE BETWEEN start_date AND end_date)))
         or  c.ELEMENT_TYPE_CODE='Y')))