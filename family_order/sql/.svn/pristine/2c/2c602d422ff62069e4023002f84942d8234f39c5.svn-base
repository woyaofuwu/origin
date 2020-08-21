select TRANSACTIONID,BUSITRANSID,PAYTRANSID,SETTLEDATE,case when  ORDERTYPE='01' then '从电商侧发起的成功缴费交易' when ORDERTYPE='10' 
then '从电商发起的成功退款的交易' end as ORDERTYPE,case when TRANSTYPE='01' then '正常交易' when TRANSTYPE='02' then '费用补扣交易' 
when TRANSTYPE='10' then '退款记录' end as TRANSTYPE,'0051' ORGANID,case when IDTYPE='01' then '手机号码' when IDTYPE='02' then '邮箱' when IDTYPE='03' then '固话' 
when IDTYPE='04' then '宽带' when IDTYPE='05' then '物联网号码'  end as IDTYPE ,IDVALUE,CHARGEMONEY, 
case when (CNLTYP>=0 and CNLTYP<=49) then '移动渠道' when (CNLTYP>=50 and CNLTYP<=80) then '银行侧渠道' when (CNLTYP>=81 and CNLTYP<=90)  
then '电商渠道' end as CNLTYP ,PAYEDTYPE,ORDERNO,PRODUCTNO,PAYMENT,SETTLEMONEY,ORDERCNT,ACTIVITYNO,PRODUCTSHELFNO, 
COMMISON,SERVICEFEE,REBATEFEE,CREDITCARDFEE,TRANSSTATUS,RESERVEFEE1,RESERVEFEE2,RESERVEFEE3,RESERVEFEE4, 
RESERVEFEE5,(Select data_name From UCR_cen1.td_s_static t Where a.DIFFTYPE=t.data_id And TYPE_ID='TO_O_TMALLACCOUNTDIFFER' And Rownum<=1) DIFFTYPE,REMARK1,REMARK2 
from  ucr_uif1.TO_O_TMALLACCOUNTDIFFER a 
where IDVALUE=:IDVALUE  AND to_char(to_date(SETTLEDATE,'yyyy-mm-dd'),'yyyy-mm-dd')=:SETTLEDATE