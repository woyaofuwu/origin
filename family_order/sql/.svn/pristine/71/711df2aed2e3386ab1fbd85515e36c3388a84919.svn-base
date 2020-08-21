Update  td_b_product_tradefee  a
Set fee =:NEW_FEE,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,remark = '资源终端调价',rsrv_str1=:RSRV_STR1
Where (campn_id =:CAMPN_ID  Or  :CAMPN_ID = -1)
And   element_id=:ELEMENT_ID
And a.element_type_code='G'
And a.fee_mode='0' And a.fee_type_code='60' 
and fee = :FEE
And  (a.product_id =-1 Or a.product_id =:PRODUCT_ID)
And  (a.package_id =-1 Or a.package_id =:PACKAGE_ID)
and a.TRADE_FEE_TYPE ='3'