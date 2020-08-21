Select a.EPARCHY_CODE,
       a.STAFF_ID      AUDIT_STAFF_ID,
       a.STAFF_NAME,
       a.SERIAL_NUMBER STAFF_PHONE,
       a.DEPART_ID,
       b.DEPART_NAME,
       a.CITY_CODE
  From TD_M_STAFF a, TD_M_DEPART b
 Where 1 = 1
   And a.DEPART_ID = b.DEPART_ID
   And a.STAFF_ID in
       (Select c.attr_value
          From ucr_crm1.TF_B_TRADE_DATALINE_ATTR c
         Where c.attr_code = 'TRADE_STAFF_ID'
           And c.trade_id in
               (Select d.trade_id
                  From ucr_crm1.TF_B_TRADE_DATALINE_ATTR d
                 Where d.Attr_Code = 'CRMNO'
                   And d.attr_value = :CRMNO))
 Order By a.STAFF_ID