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
