create table if not exists sales (
    id uuid primary key,
    receipt_no varchar(50) not null unique,
    total numeric(18, 2) not null,
    cashier_username varchar(100) not null,
    created_at timestamp not null default now()
);

CREATE TABLE if not exists sale_items (
    id uuid PRIMARY KEY,
    sale_id uuid NOT NULL,
    product_id uuid NOT NULL,
    sku VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    qty INTEGER NOT NULL,
    unit_price NUMERIC(18, 2) NOT NULL,
    line_total NUMERIC(18,2) NOT NULL,
    CONSTRAINT fk_sale_items_sale Foreign Key (sale_id) REFERENCES sales(id) on delete CASCADE,
    CONSTRAINT fk_sale_items_product Foreign Key (product_id) REFERENCES products(id)
);

create index idx_sale_items_sale_id on sale_items(sale_id);
create index idx_sales_created_at on sales(created_at);
