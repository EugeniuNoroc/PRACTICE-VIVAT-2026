alter table users alter column username set not null;
alter table users add constraint users_username_unique unique (username);
alter table users alter column email set not null;
alter table users add constraint users_email_unique unique (email);