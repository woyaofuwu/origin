Select a.cust_name,
       (Select f.attr_value
          From ucr_crm1.tf_b_trade_dataline_attr f
         Where c.trade_id = f.trade_id
           And f.attr_code = 'TRADENAME') TRADE_NAME,
       a.product_name,
       d.rsrv_str7,
       d.rsrv_str2,
       d.rsrv_str3,
       d.rsrv_str4,
       d.rsrv_str5,
       d.rsrv_str6,
       d.rsrv_str8,
       d.rsrv_str10
  From uop_eos.tf_b_subscribe_groupbiz@DBLNK_PFDBN1 a,
       ucr_crm1.tf_b_trade                         b,
       ucr_crm1.tf_b_trade                         c,
       ucr_crm1.tf_b_trade_other                   d
 Where a.trade_id = b.trade_id
   And b.order_id = c.order_id
   And c.trade_id = d.trade_id
   And d.modify_tag In ('0','2')
   And a.ibsysid = :CRM_NO
   And d.rsrv_str7 Is Not Null
Union All
Select a.cust_name,
       (Select f.attr_value
          From ucr_crm1.tf_b_trade_dataline_attr f
         Where c.trade_id = f.trade_id
           And f.attr_code = 'TRADENAME') TRADE_NAME,
       a.product_name,
       d.rsrv_str7,
       d.rsrv_str2,
       d.rsrv_str3,
       d.rsrv_str4,
       d.rsrv_str5,
       d.rsrv_str6,
       d.rsrv_str8,
       d.rsrv_str10
  From uop_eos.tf_b_subscribe_groupbiz@DBLNK_PFDBN1 a,
       ucr_crm1.tf_bh_trade                        b,
       ucr_crm1.tf_b_trade                         c,
       ucr_crm1.tf_b_trade_other                   d
 Where a.trade_id = b.trade_id
   And b.order_id = c.order_id
   And c.trade_id = d.trade_id
   And d.modify_tag In ('0','2')
   And a.ibsysid = :CRM_NO
   And d.rsrv_str7 Is Not Null
Union All
Select a.cust_name,
       (Select f.attr_value
          From ucr_crm1.tf_b_trade_dataline_attr f
         Where c.trade_id = f.trade_id
           And f.attr_code = 'TRADENAME') TRADE_NAME,
       a.product_name,
       d.rsrv_str7,
       d.rsrv_str2,
       d.rsrv_str3,
       d.rsrv_str4,
       d.rsrv_str5,
       d.rsrv_str6,
       d.rsrv_str8,
       d.rsrv_str10
  From uop_eos.tf_b_subscribe_groupbiz@DBLNK_PFDBN1 a,
       ucr_crm1.tf_bh_trade                        b,
       ucr_crm1.tf_bh_trade                        c,
       ucr_crm1.tf_b_trade_other                   d
 Where a.trade_id = b.trade_id
   And b.order_id = c.order_id
   And c.trade_id = d.trade_id
   And d.modify_tag In ('0','2')
   And a.ibsysid = :CRM_NO
   And d.rsrv_str7 Is Not Null
Union All
Select a.cust_name,
       (Select f.attr_value
          From ucr_crm1.tf_b_trade_dataline_attr f
         Where c.trade_id = f.trade_id
           And f.attr_code = 'TRADENAME') TRADE_NAME,
       a.product_name,
       d.rsrv_str7,
       d.rsrv_str2,
       d.rsrv_str3,
       d.rsrv_str4,
       d.rsrv_str5,
       d.rsrv_str6,
       d.rsrv_str8,
       d.rsrv_str10
  From uop_eos.tf_bh_subscribe_groupbiz@DBLNK_PFDBN1 a,
       ucr_crm1.tf_bh_trade                         b,
       ucr_crm1.tf_bh_trade                         c,
       ucr_crm1.tf_b_trade_other                    d
 Where a.trade_id = b.trade_id
   And b.order_id = c.order_id
   And c.trade_id = d.trade_id
   And d.modify_tag In ('0','2')
   And a.ibsysid = :CRM_NO
   And d.rsrv_str7 Is Not Null
 Order By rsrv_str7