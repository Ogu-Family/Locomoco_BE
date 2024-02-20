-- 긍정적인 리뷰 데이터 삽입
INSERT INTO review_contents (review_content_id, content, is_positive)
VALUES
    (1, '친절하고 매너있어요', true),
    (2, '시간약속을 잘 지켜요', true),
    (3, '응답이 빨라요', true);

-- 부정적인 리뷰 데이터 삽입
INSERT INTO review_contents (review_content_id, content, is_positive)
VALUES
    (4, '시간약속을 잘 안지켜요', false),
    (5, '불친절해요', false),
    (6, '응답이 느려요', false);
