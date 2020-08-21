SELECT A.RECORDID QUOTE_ID, A.PURCHASEPROJECTNO PURCHASE_NO, A.VENDORCODE VENDOR_CODE, A.MATERIALCODE MATERIAL_CODE, 
	A.LASTUPDATETIME LAST_UPDATE_TIME, A.PURCHASEPRICE PURCHASE_PRICE, A.OPRNUMB OPER_TRADE_ID
FROM TI_R_PRICE_DOWN A
WHERE 1 = 1
AND A.DEALFLAG = :DEALFLAG
AND A.OPRFLAG = :OPRFLAG
AND A.RECORDDATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')