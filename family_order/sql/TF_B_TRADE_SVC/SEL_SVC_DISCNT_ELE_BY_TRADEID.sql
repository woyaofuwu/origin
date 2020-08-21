select us.SERVICE_ID ELEMENT_ID,us.USER_ID,'S' ELEMENT_TYPE_CODE,us.INST_ID,
to_char(us.START_DATE, 'yyyy-mm-dd') START_DATE,us.MODIFY_TAG,
to_char(us.END_DATE, 'yyyy-mm-dd') END_DATE
from tf_b_trade_svc us
where us.trade_id = :TRADE_ID 
and us.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
and us.modify_tag = '0'
union
select ud.DISCNT_CODE ELEMENT_ID,ud.USER_ID,'D' ELEMENT_TYPE_CODE,ud.INST_ID,
to_char(ud.START_DATE, 'yyyy-mm-dd') START_DATE,ud.MODIFY_TAG,
to_char(ud.END_DATE, 'yyyy-mm-dd') END_DATE
from tf_b_trade_discnt ud
where  ud.trade_id = :TRADE_ID 
and ud.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
and ud.modify_tag = '0'