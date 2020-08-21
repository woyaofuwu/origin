select a.IMEI as SERIAL_NUMBER,b.brand_code as DEVICE_BRAND_CODE,b.brand_desc AS DEVICE_BRAND,
       c.model_code AS DEVICE_MODEL_CODE,c.model_desc as DEVICE_MODEL,a.supplier_id AS SUPPLY_COOP_ID,
       e.item_val_name,e.item_val_code,
       a.cost_price AS DEVICE_COST,a.sale_price AS RSRV_STR6,
       a.res_state as TERMINAL_STATE,a.mgmt_org_id AS STOCK_ID
from tm_origin a,tm_brand b,tm_model c,res_spu_itemval d,res_item_val e
       WHERE c.model_code= :TERMINAL_MODEL_CODE
       and a.res_sku_id = c.res_type_id
       and b.brand_code = c.brand_code
       AND a.res_sku_id = d.res_spu_id
       AND d.itemval_code = e.item_val_code
       AND d.itemval = e.item_val_value