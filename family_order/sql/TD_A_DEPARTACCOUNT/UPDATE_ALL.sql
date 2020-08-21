UPDATE td_a_departaccount
   SET depart_code=:DEPART_CODE,bank_code=:BANK_CODE,bank=:BANK,bank_acct_no=:BANK_ACCT_NO,pay_name=:PAY_NAME,assure_no=:ASSURE_NO,para_code1=:PARA_CODE1,trade_staff_id=:TRADE_STAFF_ID
 WHERE depart_id = :DEPART_ID