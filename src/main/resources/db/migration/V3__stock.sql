create table if not exists stocks (
    id uuid primary key,
    product_id uuid not null unique,
    qty integer not null,
    updated_at timestamp not null default now(),
    constraint fk_stocks_product foreign key (product_id) references products(id)
);

create index idx_stocks_product_id on stocks(product_id);

