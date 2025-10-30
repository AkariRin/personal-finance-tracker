create table
    user(
            username varchar(16) not null primary key,
            name varchar(50) not null comment 'display name',
            password varchar(60) not null comment 'bcrypt hash',
            balance decimal(10, 2) default 0.00 null
);

create table
    bill (
             uuid char(36) not null primary key,
             amount decimal(10, 2) not null,
             type enum('in', 'out') not null,
             method enum(
                 'cash',
                 'wx',
                 'alipay',
                 'card',
                 'btc',
                 'paypal',
                 'apple'
                 ) not null,
             counterparty varchar(100) not null,
             time datetime not null,
             category varchar(50) null,
             note text null,
             user_username varchar(16) not null,
             constraint bill_user_1 foreign key (user_username) references user(username)
);

create index user_username on bill (user_username);
