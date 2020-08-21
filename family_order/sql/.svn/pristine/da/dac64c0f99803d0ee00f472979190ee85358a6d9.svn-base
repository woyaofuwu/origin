SELECT to_char(a.trade_id) trade_id,serial_number,a.cust_name,decode(trade_type_code,'2026','装机工单','120','移机工单') trade_type,
       b.phone,b.post_address,decode(b.pay_mode_code,'0','现金','1','托收') pay_mode,to_char(a.accept_date,'yyyy-mm-dd') open_date,NULL prevaluec1
FROM tf_b_trade a,tf_b_trade_detail b 
WHERE a.trade_id=b.trade_id
      AND(trade_type_code=:TRADE_TYPE_CODE OR :TRADE_TYPE_CODE IS NULL)
      AND (a.trade_id=:TRADE_ID OR :TRADE_ID IS NULL)
      AND (cust_name=:CUST_NAME OR :CUST_NAME IS NULL)
      AND (trunc(a.accept_date)=to_date(:OPEN_DATE,'yyyy-mm-dd') OR :OPEN_DATE IS NULL)