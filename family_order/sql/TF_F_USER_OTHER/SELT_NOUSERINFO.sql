SELECT row_num,serial_number,imsi,TO_CHAR(totalfee) TOTAL_FEE,tag,DECODE(tag,'0','纯无资料无主','1','二次放号无主','2','买断开户无主','3','状态不符无主','4','未知原因无主','5','资料同步延迟','6','批开未售卡无主','类型未定义') GROUP_TYPE,eparchy_code,
TO_CHAR(indate,'YYYY-MM-DD HH24:MI:SS') in_date,TO_CHAR(opendate,'YYYY-MM-DD HH24:MI:SS') open_date,TO_CHAR(dealdate,'YYYY-MM-DD HH24:MI:SS') deal_date,
rsvalue1,rsvalue2,TO_CHAR(rnvalue1) rnvalue1,TO_CHAR(rnvalue2) rnvalue2,
TO_CHAR(rdvalue1,'YYYY-MM-DD HH24:MI:SS') rdvalue1,TO_CHAR(rdvalue2,'YYYY-MM-DD HH24:MI:SS') rdvalue2
FROM(SELECT a.*,ROWNUM row_num FROM(SELECT * FROM tf_f_nousrinfo WHERE  rdvalue1 BETWEEN TO_DATE(:START_DATE,'YYYYMMDD') AND TO_DATE(:END_DATE,'YYYYMMDD')+1
AND eparchy_code=:TRADE_EPARCHY_CODE AND 1=1 ) a WHERE ROWNUM <= :X_SELCOUNT*:X_CHANNEL_NUM) b WHERE row_num > (:X_CHANNEL_NUM-1)*:X_SELCOUNT