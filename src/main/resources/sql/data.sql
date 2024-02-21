-- 긍정적인 리뷰 데이터 삽입
INSERT INTO review_contents (content, is_positive)
VALUES
    ('친절하고 매너있어요', true),
    ('시간약속을 잘 지켜요', true),
    ('응답이 빨라요', true);

-- 부정적인 리뷰 데이터 삽입
INSERT INTO review_contents (content, is_positive)
VALUES
    ('시간약속을 잘 안지켜요', false),
    ('불친절해요', false),
    ('응답이 느려요', false);

-- 모각코 더미 데이터
INSERT INTO mogakko (created_at, deleted_at, updated_at, content, deadline, end_time, like_count, location, max_participants, start_time, title, views, creator_id, inquiry_id, user_id)
VALUES
    (NOW(), NULL, NOW(), '더미 내용 1', '2024-02-21 12:00:00', '2024-02-21 14:00:00', 5, '더미 위치 1', 10, '2024-02-21 10:00:00', '더미 제목 1', 100, 1, NULL, 1),
    (NOW(), NULL, NOW(), '더미 내용 2', '2024-02-22 12:00:00', '2024-02-22 14:00:00', 8, '더미 위치 2', 15, '2024-02-22 10:00:00', '더미 제목 2', 120, 2, NULL, 2),
    (NOW(), NULL, NOW(), '더미 내용 3', '2024-02-23 12:00:00', '2024-02-23 14:00:00', 10, '더미 위치 3', 20, '2024-02-23 10:00:00', '더미 제목 3', 150, 3, NULL, 3);


-- 카테고리 데이터
INSERT INTO categories(name, type) values ('모각코 유형', 'MOGAKKO');
INSERT INTO categories(name, type) values ('개발 언어', 'MOGAKKO');
INSERT INTO categories(name, type) values ('개발 유형', 'MOGAKKO');
INSERT INTO categories(name, type) values ('직업', 'MOGAKKO');
INSERT INTO categories(name, type) values ('연령대', 'MOGAKKO');

INSERT INTO tags(name, category_id) values ('일반', 1);
INSERT INTO tags(name, category_id) values ('JS', 2);
INSERT INTO tags(name, category_id) values ('java', 2);
INSERT INTO tags(name, category_id) values ('코딩 테스트', 3);
INSERT INTO tags(name, category_id) values ('백엔드(스프링)', 3);
INSERT INTO tags(name, category_id) values ('취준생', 4);
INSERT INTO tags(name, category_id) values ('상관 없음', 4);
INSERT INTO tags(name, category_id) values ('10대', 5);
INSERT INTO tags(name, category_id) values ('20대', 5);


