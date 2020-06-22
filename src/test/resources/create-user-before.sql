delete from user_role;
delete from usr;

insert into usr(id, username, password, active) values
(1, 'maxim', '$2a$08$/dWD/4dT8puWxFnNVBgWoeRL.yxfh00JSmKjSbSYjRVpWGqoFDx.m', true),
(2, 'mike', '$2a$08$/dWD/4dT8puWxFnNVBgWoeRL.yxfh00JSmKjSbSYjRVpWGqoFDx.m', true);

insert into user_role(user_id, roles) values
(1, 'ADMIN'), (1, 'USER'),
(2, 'USER');