select a.serial_number,a.user_id,a.product_id,a.product_name,a.package_id,a.package_name from tf_f_user_sale_active a
Where a.partition_id=Mod(:USER_ID,10000)
And a.user_id=to_number(:USER_ID)
And a.end_date>Sysdate
And a.process_tag='0'
And Exists(
    Select 1 From td_s_commpara b
    Where b.subsys_code='CSM'
    And b.param_attr=152
    And b.param_code='2'
    And b.para_code1=to_char(a.product_id)
    And a.package_id=to_number(nvl(b.para_code2,a.package_id))
    And Sysdate Between b.start_date And b.end_Date
)