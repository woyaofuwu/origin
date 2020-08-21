Select nvl(b.a_discnt_code,'0') action_code,a.* 
From tf_f_user_sale_active a,tf_f_user_sale_deposit b
Where a.partition_id=b.partition_id(+)
And a.user_id=b.user_id(+)
And a.product_id=b.product_id(+)
And a.package_id=b.package_id(+)
And a.relation_trade_id=b.relation_trade_id(+)
And a.process_tag='0'
And a.partition_id=mod(to_number(:USER_ID),10000)
And a.user_id=to_number(:USER_ID)
And a.end_Date > to_Date(:CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss')
And Exists (
 Select 1 From td_s_commpara c
 Where c.subsys_code='CSM'
    And c.param_attr=1528
    And c.param_code='1'
    And c.para_code1=:OLD_PRODUCT_ID
    And (c.para_code2='-1' Or c.para_code2=:NEW_PRODUCT_ID)
    And a.product_id=to_number(c.para_code3)
    And a.package_id=to_number(nvl(c.para_code4,a.package_id))    
    And Sysdate Between c.start_date And c.end_Date
 Union
 Select 1 From td_s_commpara c
 Where c.subsys_code='CSM'
    And c.param_attr=1528
    And c.param_code='2'
    And c.para_code1=:OLD_BRAND_CODE
    And (c.para_code2='ZZZZ' Or c.para_code2=:NEW_BRAND_CODE)
    And a.product_id=to_number(c.para_code3)
    And a.package_id=to_number(nvl(c.para_code4,a.package_id))    
    And Sysdate Between c.start_date And c.end_Date
 Union
  Select 1 From td_s_commpara c
 Where c.subsys_code='CSM'
    And c.param_attr=1528
    And c.param_code='3'
    And c.para_code1='-1'
    And (c.para_code2=:NEW_PRODUCT_ID)
    And a.product_id=to_number(c.para_code3)
    And a.package_id=to_number(nvl(c.para_code4,a.package_id))    
    And Sysdate Between c.start_date And c.end_Date
)
And Not Exists(
    Select 1 From td_s_commpara c0
    Where c0.subsys_code = 'CSM'
      And c0.param_attr = 1531
      And c0.param_code = '1'
      And c0.para_code1 = :NEW_PRODUCT_ID
      And a.product_id = to_number(c0.para_code2)
      And Sysdate Between c0.start_date And c0.end_Date
    Union
    Select 1 From td_s_commpara c1 
    Where c1.subsys_code='CSM'
      And c1.param_attr=1529
      And c1.param_code='1'
      And c1.para_code1=:OLD_PRODUCT_ID
      And (c1.para_code2='-1' Or c1.para_code2=:NEW_PRODUCT_ID)
      And a.product_id=to_number(c1.para_code3)
      And a.package_id=to_number(nvl(c1.para_code4,a.package_id))
      And Sysdate Between c1.start_date And c1.end_Date
    Union
    Select 1 From td_s_commpara c
    Where c.subsys_code='CSM'
      And c.param_attr=1529
      And c.param_code='2'
      And c.para_code1=:OLD_BRAND_CODE
      And (c.para_code2='ZZZZ' Or c.para_code2=:NEW_BRAND_CODE)
      And a.product_id=to_number(c.para_code3)
      And a.package_id=to_number(nvl(c.para_code4,a.package_id))    
      And Sysdate Between c.start_date And c.end_Date
)
Order By a.accept_Date Asc