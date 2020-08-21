select t.* 
  from TF_F_USER_SALEACTIVE_BOOK t
 where T.PRODUCT_ID IN ('69908001', '69908015', '69908012')
   And Nvl(t.Rsrv_Date2, t.End_Date) > Sysdate   
   and t.process_tag='0'
	 and t.DEAL_STATE_CODE = '0'
   AND T.SERIAL_NUMBER = :SERIAL_NUMBER