UPDATE td_a_recvbank
   SET eparchy_code=:EPARCHY_CODE,city_code=:SERV_PARA1,bank_code=:SERV_PARA2,bank=:BANK,   bank_account_no=:BANK_ACCOUNT_NO,recv_name=:RECV_NAME  
 WHERE eparchy_code=:EPARCHY_CODE
   AND city_code=:CITY_CODE
   AND bank_code=:BANK_CODE