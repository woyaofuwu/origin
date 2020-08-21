SELECT RECORDID, MATERIALCODE, DEALPRICE, DEALPRICEFLORATE, SALEPRICE, SALEPRICEFLORATE, TRANSTYPE, 
	STARTDATE, ENDDATE
FROM TI_R_PRICESUPERVISE_DOWN
WHERE 1=1
     AND DEALFLAG =:DEALFLAG
     AND RECORDDATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')