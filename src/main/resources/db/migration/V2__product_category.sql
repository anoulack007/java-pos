create table if not exists categories (
  id uuid primary key,
  name varchar(255) not null unique
);

create table if not exists products (
  id uuid primary key,
  sku varchar(255) not null unique,
  name varchar(255) not null,
  price numeric(18,2) not null,
  active boolean not null default true,
  category_id uuid null,
  constraint fk_products_category
    foreign key (category_id) references categories(id)
);

create index if not exists idx_products_name on products(name);
create index if not exists idx_products_sku on products(sku);
