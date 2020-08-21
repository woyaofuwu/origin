--IS_CACHE=Y
Select 
 a.Trade_Type_Code Trade_Type_Code,
 a.Brand_Code      Brand_Code,
 a.Product_Id      Product_Id,
 a.Sms_Code        Sms_Code,
 a.Sms_Type_Code   Sms_Type_Code,
 a.Sms_Content     Sms_Content,
 a.Billing_Type    Billing_Type,
 a.Sms_Info        Sms_Info,
 a.Eparchy_Code    Eparchy_Code,
 a.In_Mode_Code    In_Mode_Code,
 a.Biz_Pri         Biz_Pri,
 a.Sms_Process_Tag Sms_Process_Tag,
 a.Oper_Code       Oper_Code
From td_b_platsvc_sms a
Where  (a.service_id = :SERVICE_ID Or a.service_id = '90000000')
And (a.Eparchy_Code = :EPARCHY_CODE Or
      a.Eparchy_Code = 'ZZZZ')
And (a.In_Mode_Code = :IN_MODE_CODE Or a.In_Mode_Code = 'Q')
And (Biz_Type_Code = :BIZ_TYPE_CODE Or Biz_Type_Code = 'xx')
And (a.Oper_Code = :OPER_CODE)
And Sysdate Between a.Start_Date And a.End_Date
And a.Biz_Pri =
      (Select 
        Max(a.Biz_Pri)
       From td_b_platsvc_sms a
       Where  (a.Eparchy_Code = :EPARCHY_COODE Or
             a.Eparchy_Code = 'ZZZZ')
       And (a.In_Mode_Code = :IN_MODE_CODE Or
             a.In_Mode_Code = 'Q')
       And (a.Oper_Code = :OPER_CODE)
       And (Biz_Type_Code = :BIZ_TYPE_CODE Or Biz_Type_Code = 'xx')
       And Sysdate Between a.Start_Date And a.End_Date
       And a.Biz_Pri <> -1)