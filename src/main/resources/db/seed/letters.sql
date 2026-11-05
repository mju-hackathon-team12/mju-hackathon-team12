-- Initial letters: 10 existing examples and 50 imported letters.
-- Insert missing IDs and content positions only; preserve existing edits on restart.
-- Places are seeded by seodaemun.sql before this script.

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-01', 'sample-data', NULL, NULL, 'falls', '엄마랑 앉아서 아무 말 안 했다. 물소리 덕분에 그게 어색하지 않았다. 집에서는 그렇게 조용히 있으면 꼭 싸운 사람들 같았는데. 돌아오는 길에 엄마가 “또 오자”고 했다. 그 말이 오늘의 전부였다.', 'comfort', FALSE, TRUE, NULL, '휴대폰을 가방에 넣고 5분 동안 물가를 바라보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-01');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-01', 0, 'DRAMA', '나의 아저씨'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-01' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-01' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-01', 1, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-01' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-01' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-01', 2, 'MUSIC', '밤편지'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-01' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-01' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-02', 'sample-data', NULL, NULL, 'ansan', '회사 그만둔다는 말을 여기서 처음 소리 내어 해봤다. 나무들은 별말이 없었다. 그게 좋았다. 아직 아무에게도 말하지 못했지만, 내 목소리로 한 번 들으니까 조금 진짜가 된 것 같다.', 'rest', FALSE, TRUE, NULL, '아무도 없는 구간에서 오늘 못 한 말을 작게 소리 내어 해보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-02');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-02', 0, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-02' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-02' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-02', 1, 'MUSIC', '사건의 지평선'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-02' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-02' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-02', 2, 'DRAMA', '미스터 션샤인'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-02' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-02' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-03', 'sample-data', NULL, NULL, 'film', '크레딧이 끝날 때까지 앉아 있던 사람이 나 말고 한 명 더 있었다. 서로 쳐다보지 않고 같이 나왔다. 계단을 다 올라와서야 그 사람도 울었다는 걸 알았다. 말을 걸지는 않았다. 그래도 오늘 혼자 본 것 같지 않았다.', 'comfort', FALSE, TRUE, NULL, '영화가 끝나면 불이 켜질 때까지 일어나지 말고 앉아 있어 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-03');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-03', 0, 'DRAMA', '나의 아저씨'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-03' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-03' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-03', 1, 'DRAMA', '응답하라 1988'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-03' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-03' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-03', 2, 'MUSIC', '밤편지'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-03' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-03' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-04', 'sample-data', NULL, NULL, 'film', '공모전에 떨어진 날 혼자 표를 끊었다. 영화 속 사람들도 다 잘되진 않았다. 이상하게 그게 위로가 됐다. 나오면서 다음 공모전 마감일을 찾아봤다. 아직 포기할 만큼 해본 것도 아니니까.', 'youth', FALSE, TRUE, NULL, '나오는 길에 다음에 해볼 일 하나를 메모해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-04');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-04', 0, 'DRAMA', '스물다섯 스물하나'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-04' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-04' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-04', 1, 'MUSIC', '한 페이지가 될 수 있게'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-04' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-04' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-04', 2, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-04' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-04' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-05', 'sample-data', NULL, NULL, 'prison', '교과서에서는 숫자였던 사람들이 여기서는 이름이었다. 이름을 하나씩 읽다가 걸음이 느려졌다. 나는 오늘 무엇 때문에 힘들다고 말했던가. 비교할 일은 아니지만, 오래 기억하고 싶어서 여기에 적어둔다.', 'courage', FALSE, TRUE, NULL, '오래 남은 이름 하나를 조용히 기억해 두세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-05');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-05', 0, 'DRAMA', '미스터 션샤인'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-05' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-05' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-05', 1, 'MUSIC', '사건의 지평선'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-05' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-05' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-05', 2, 'DRAMA', '나의 아저씨'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-05' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-05' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-06', 'sample-data', NULL, NULL, 'gate', '출근길에 매일 지나면서 한 번도 올려다본 적이 없었다. 오늘 처음 멈춰 섰다. 아버지가 어릴 때 여기서 사진을 찍었다고 하셨는데, 그 사진을 찾아봐야겠다. 매일 지나는 곳에도 내가 모르는 시간이 있었다.', 'miss', FALSE, TRUE, NULL, '매일 지나치던 것을 오늘 한 번만 올려다보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-06');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-06', 0, 'DRAMA', '응답하라 1988'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-06' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-06' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-06', 1, 'DRAMA', '미스터 션샤인'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-06' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-06' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-06', 2, 'MUSIC', '밤편지'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-06' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-06' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-07', 'sample-data', NULL, NULL, 'gate', '혼자 서울에 왔을 때 처음으로 괜찮다고 느낀 곳이 이 공원이에요. 매일 아침 어떤 할아버지가 저에게 고개를 끄덕여 주셨어요. 우리는 한 번도 말을 나눈 적이 없어요.', 'comfort', FALSE, TRUE, NULL, '오늘 마주친 낯선 사람에게 가볍게 고개를 숙여보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-07');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-07', 0, 'DRAMA', '나의 아저씨'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-07' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-07' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-07', 1, 'MUSIC', '사건의 지평선'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-07' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-07' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-07', 2, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-07' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-07' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-08', 'sample-data', NULL, NULL, 'market', '아버지가 다니던 시장을 이제 내가 다닌다. 단골이던 가게 사장님이 나를 알아보고 아버지 안부를 물으셨다. 잘 지내신다고 답했다. 집에 가는 길에 오랜만에 전화를 걸었다.', 'miss', FALSE, TRUE, NULL, '시장을 나서며 오래 연락 못 한 사람에게 안부를 보내보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-08');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-08', 0, 'DRAMA', '응답하라 1988'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-08' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-08' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-08', 1, 'DRAMA', '나의 아저씨'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-08' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-08' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-08', 2, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-08' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-08' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-09', 'sample-data', NULL, NULL, 'market', '떡볶이 집 사장님이 “또 왔네” 하셨다. 서울에서 처음 들은 말이었다. 혼자 산 지 석 달째인데, 누가 나를 기억한다는 게 이렇게 큰 일인 줄 몰랐다. 오늘은 먼저 인사해야지.', 'youth', FALSE, TRUE, NULL, '가게에 들어갈 때 먼저 인사해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-09');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-09', 0, 'DRAMA', '스물다섯 스물하나'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-09' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-09' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-09', 1, 'MUSIC', '한 페이지가 될 수 있게'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-09' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-09' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-09', 2, 'DRAMA', '응답하라 1988'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-09' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-09' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'sample-10', 'sample-data', NULL, NULL, 'ansan', '면접에 떨어진 날 제일 높은 데까지 올라갔다. 여기서 보니까 내가 떨어진 회사 건물도 작았다. 내려오면서 이력서 첫 줄을 다시 썼다. 아직 끝난 얘기가 아니다.', 'youth', FALSE, TRUE, NULL, '가장 멀리 보이는 곳을 찾아 1분만 서 있어 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-10');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-10', 0, 'MUSIC', '사건의 지평선'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-10' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-10' AND content_order = 0);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-10', 1, 'DRAMA', '스물다섯 스물하나'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-10' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-10' AND content_order = 1);
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'sample-10', 2, 'DRAMA', '미스터 션샤인'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'sample-10' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'sample-10' AND content_order = 2);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-007', 'sample-data', NULL, NULL, 'ansan', '아침에 알람 끄고 다시 잘까 하다가 그냥 나왔다. 안산자락길은 처음 가봤는데 생각보다 길이 편해서 다행이었다. 올라가면서 머릿속에 있던 할 일이 자꾸 떠올랐는데 내려올 때쯤엔 좀 잠잠해졌다. 이럴 거면 진작 나올걸.', 'rest', FALSE, TRUE, NULL, '자락길에서 무리하지 않는 구간을 걷고, 돌아오는 길에 오늘 미뤄둬도 되는 일 하나를 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-007');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-007', 0, 'MUSIC', '마음'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-007' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-007' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-008', 'sample-data', NULL, NULL, 'tourapi-2996477', '친구가 여기 물소리 들으면 좋다길래 왔는데 처음엔 사람이 많아서 살짝 당황했다. 그래도 앉아서 한참 보고 있으니까 생각보다 시원했다. 커피는 금방 다 마셨는데 자리를 떠나기 아쉬웠다. 사진보다 실제로 볼 때가 훨씬 좋더라.', 'youth', FALSE, TRUE, NULL, '안전한 관람 구역에서 폭포를 잠깐 바라보고, 오늘 들린 소리를 한 문장으로 남겨보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-008');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-008', 0, 'MUSIC', '여행'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-008' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-008' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-009', 'sample-data', NULL, NULL, 'tourapi-3452874', '책을 읽으러 갔다가 창밖만 보다가 왔다. 원래 한 챕터는 읽겠다고 마음먹었는데 자꾸 물 떨어지는 쪽으로 눈이 갔다. 그래도 핸드폰 덜 본 것만으로도 만족한다. 다음엔 읽던 책 들고 다시 와야지.', 'rest', FALSE, TRUE, NULL, '책 한 권을 골라 짧게 읽고, 기억에 남는 문장이나 느낌을 본인 말로 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-009');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-009', 0, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-009' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-009' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-010', 'sample-data', NULL, NULL, 'gate', '지나갈 때마다 보면서도 제대로 멈춰 본 적은 없었다. 오늘은 약속까지 시간이 남아서 근처에서 잠깐 서 있었다. 학교 다닐 땐 역사 시간에 외우기만 했는데 직접 보니까 기분이 좀 달랐다. 다음엔 설명도 읽어봐야겠다.', 'miss', FALSE, TRUE, NULL, '독립문 주변의 안내 설명을 읽고 처음 알게 된 점 하나를 메모해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-010');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-010', 0, 'FILM', '암살'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-010' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-010' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-011', 'sample-data', NULL, NULL, 'prison', '친구랑 전시를 보고 나왔는데 둘 다 한동안 말을 안 했다. 어떤 이야기를 쉽게 감상했다고 말하기가 어렵더라. 집에 가는 버스에서 본 내용들을 다시 찾아봤다. 가볍게 놀러 온 건 아니었지만 오길 잘했다는 생각은 들었다.', 'miss', FALSE, TRUE, NULL, '전시를 관람한 뒤 인상 깊었던 역사적 사실 하나를 공식 안내 자료와 함께 확인해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-011');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-011', 0, 'FILM', '항거: 유관순 이야기'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-011' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-011' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-012', 'sample-data', NULL, NULL, 'tourapi-2813170', '과제 때문에 들어간 곳이었는데 생각보다 오래 있었다. 내가 알고 있다고 생각한 이야기에도 모르는 부분이 많았다. 전시 보다가 메모한 게 몇 개 있어서 나중에 다시 찾아볼 예정이다. 처음엔 귀찮았는데 안 왔으면 아쉬웠을 듯.', 'youth', FALSE, TRUE, NULL, '전시에서 새롭게 알게 된 역사적 인물이나 사건 하나를 기록해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-012');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-012', 0, 'FILM', '밀정'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-012' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-012' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-013', 'sample-data', NULL, NULL, 'tourapi-128622', '주말에 그냥 바람 쐬고 싶어서 공원으로 갔다. 산책하는 사람이 많았고 나도 따라 천천히 걸었다. 요즘은 쉬어도 계속 뭘 확인하게 되는데 여기서는 폰을 가방에 넣어뒀다. 삼십 분쯤 있다 왔는데 그걸로 충분했다.', 'rest', FALSE, TRUE, NULL, '공원을 천천히 둘러보고 마음이 조금 편해지는 장소에서 잠깐 쉬어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-013');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-013', 0, 'MUSIC', '밤편지'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-013' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-013' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-014', 'sample-data', NULL, NULL, 'tourapi-264375', '신촌에서 친구 기다리는데 지각한다고 연락이 왔다. 짜증낼까 하다가 그냥 길 구경했다. 오가는 사람들 옷 보는 재미가 있어서 생각보다 금방 시간이 갔다. 나중엔 오히려 친구보다 내가 먼저 사진 찍느라 바빴음.', 'youth', FALSE, TRUE, NULL, '연세로를 걸으며 눈에 띄는 간판이나 색 조합 하나를 사진으로 남겨보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-014');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-014', 0, 'MUSIC', 'Super Shy'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-014' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-014' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-015', 'sample-data', NULL, NULL, 'tourapi-3082166', '벽화 앞에서 사진 찍는 사람들이 있길래 나도 슬쩍 찍었다. 원래 셀카 거의 안 찍는데 색이 예뻐서 친구한테 찍어 달라고 했다. 스무 장 찍었는데 마음에 드는 건 두 장. 그래도 오랜만에 같이 웃었다.', 'youth', FALSE, TRUE, NULL, '벽화 중 마음에 드는 색을 찾아 사진을 찍고, 그 색에 어울리는 노래 한 곡을 골라보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-015');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-015', 0, 'MUSIC', '한 페이지가 될 수 있게'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-015' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-015' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-016', 'sample-data', NULL, NULL, 'tourapi-1019327', '라이브 공연은 오랜만이었다. 처음 보는 팀이었는데 드럼 소리가 시작하자마자 집중하게 됐다. 음원으로 듣는 거랑 정말 다르더라. 끝나고 나오는데 귀에 아직도 후렴이 남아 있었다. 다음엔 친구도 데려오고 싶다.', 'youth', FALSE, TRUE, NULL, '공연 일정과 관람 조건을 확인한 뒤 라이브 무대에서 기억에 남는 순간을 한 줄로 남겨보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-016');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-016', 0, 'MUSIC', 'TOMBOY'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-016' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-016' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-017', 'sample-data', NULL, NULL, 'tourapi-3076677', '혼자 영화를 보러 간 건 진짜 오랜만이다. 끝나고 바로 나오기 아까워서 근처에서 잠깐 앉아 있었다. 결말이 마음에 들었다기보다는 계속 생각나는 장면이 있었다. 누구랑 같이 봤으면 바로 이야기했겠지만 혼자 보니까 오히려 오래 남는다.', 'miss', FALSE, TRUE, NULL, '영화 관람 후 가장 오래 기억에 남는 장면을 스포일러 없이 한 문장으로 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-017');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-017', 0, 'FILM', '벌새'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-017' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-017' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-018', 'sample-data', NULL, NULL, 'tourapi-2581396', '포스터 보고 무슨 공간인지 궁금해서 들렀다. 생각보다 조용해서 좋았고 이런 데가 있는 줄도 몰랐다. 신촌 오면 늘 똑같은 곳만 갔는데 이제 볼거리가 하나 더 생긴 느낌. 다음에 프로그램 있을 때 다시 와봐야겠다.', 'youth', FALSE, TRUE, NULL, '공식 프로그램 정보를 확인하고 관심이 가는 문화 행사 하나를 골라보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-018');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-018', 0, 'MUSIC', '사건의 지평선'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-018' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-018' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-019', 'sample-data', NULL, NULL, 'tourapi-3078407', '클래식 공연은 어렵다는 생각이 있었는데 막상 앉아서 들으니까 그렇게 긴장할 필요는 없었다. 모르는 곡이라도 좋은 부분은 있더라. 박수 언제 치는지 몰라 옆 사람 눈치 본 건 비밀. 집에 와서 같은 곡을 다시 찾아 들었다.', 'youth', FALSE, TRUE, NULL, '공연 일정을 확인하고 관심 있는 연주 한 곡을 찾아 들어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-019');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-019', 0, 'MUSIC', 'River Flows in You'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-019' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-019' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-020', 'sample-data', NULL, NULL, 'tourapi-2914737', '친구가 만나자고 해서 연희동까지 왔다. 공간이 생각보다 넓어서 여기저기 구경하다가 한참 수다 떨었다. 평소 같으면 카페에서 휴대폰만 봤을 텐데 오늘은 사진도 몇 장 찍고 얘기도 많이 했다. 별 얘기 아니어도 괜찮았다.', 'comfort', FALSE, TRUE, NULL, '공간에서 마음에 드는 디테일 하나를 찾아 친구에게 보여주고 이야기해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-020');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-020', 0, 'MUSIC', 'Hype Boy'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-020' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-020' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-021', 'sample-data', NULL, NULL, 'tourapi-3544787', '오늘 좀 별로인 일이 있어서 바로 집에 들어가기 싫었다. 가까운 데 잠깐 앉아 있었는데 나무 사이 바람 소리가 들렸다. 기분이 갑자기 좋아진 건 아닌데 조금 가라앉기는 했다. 아무한테도 말 안 하고 나만의 시간을 보낸 게 좋았다.', 'comfort', FALSE, TRUE, NULL, '쉼터에서 잠깐 쉬며 오늘 가장 힘들었던 순간과 괜찮았던 순간을 하나씩 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-021');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-021', 0, 'MUSIC', '주저하는 연인들을 위해'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-021' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-021' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-022', 'sample-data', NULL, NULL, 'tourapi-2044896', '책 구경만 하려고 들어갔다가 표지가 마음에 드는 책을 발견했다. 내용을 조금 읽어보니 딱 지금 내 기분 같았다. 결국 한 권 샀다. 집에 쌓아둔 책도 많은데 또 샀지만 오늘은 변명 안 하기로 했다.', 'youth', FALSE, TRUE, NULL, '서점에서 처음 보는 책 한 권을 골라 표지나 제목이 끌린 이유를 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-022');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-022', 0, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-022' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-022' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-023', 'sample-data', NULL, NULL, 'tourapi-2899951', '엽서 고르다가 시간을 꽤 썼다. 예쁜 게 너무 많아서 다 사고 싶었는데 결국 하나만 골랐다. 누구한테 보낼지는 아직 못 정했다. 그냥 요즘의 나한테 보내도 될 것 같기도 하다. 집에 와서 책상 위에 올려뒀다.', 'miss', FALSE, TRUE, NULL, '마음에 드는 엽서나 종이를 골라 미래의 나에게 두 문장 정도 써보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-023');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-023', 0, 'MUSIC', '밤편지'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-023' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-023' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-024', 'sample-data', NULL, NULL, 'tourapi-2839897', '빵 사러 잠깐 들렀다가 생각보다 오래 고민했다. 집에서 먹으려고 샀는데 나오자마자 냄새 때문에 하나 뜯을 뻔했다. 엄마가 좋아할 만한 것도 골랐다. 별일 아닌데 같이 먹을 생각하니까 괜히 좋았다.', 'comfort', FALSE, TRUE, NULL, '가족이나 친구가 좋아할 만한 간식을 하나 골라 같이 나눠보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-024');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-024', 0, 'MUSIC', '선물'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-024' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-024' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-025', 'sample-data', NULL, NULL, 'tourapi-2851700', '돈까스 먹으려고 갔는데 예전에 대학 다닐 때 친구랑 밥값 계산하던 생각이 났다. 메뉴 고를 때마다 돈부터 보던 시절이 있었지. 이날도 별생각 없이 먹었는데 나오면서 괜히 오래된 사진첩을 뒤졌다. 음식이 기억을 꺼내는 게 신기하다.', 'miss', FALSE, TRUE, NULL, '식사 후 예전에 자주 먹던 음식과 연결된 기억을 짧게 기록해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-025');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-025', 0, 'MUSIC', '좋은 날'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-025' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-025' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-026', 'sample-data', NULL, NULL, 'market', '시장 가면 꼭 계획에 없던 걸 사게 된다. 오늘도 구경만 하려고 했는데 냄새 때문에 간식 하나 샀다. 사람들 오가는 거 보면서 먹으니까 더 맛있었다. 대형마트랑은 또 다른 재미가 있다. 다음엔 빈속으로 와야겠다.', 'youth', FALSE, TRUE, NULL, '시장을 천천히 둘러보고 처음 눈에 들어온 먹거리나 가게를 기록해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-026');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-026', 0, 'MUSIC', '여행'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-026' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-026' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-027', 'sample-data', NULL, NULL, 'tourapi-2773278', '친구가 여기 맛있는 거 많다고 해서 따라왔다. 뭐 먹을지 정하는 데만 한참 걸렸는데 둘 다 결국 평소 먹던 걸 골랐다. 다 먹고 나서도 골목을 계속 돌아다녔다. 웃긴 건 다음에 또 오자고 한 게 제일 먼저 나온 말이었다.', 'comfort', FALSE, TRUE, NULL, '먹자골목에서 서로 고른 메뉴가 왜 달랐는지 이야기해 보고 기억에 남는 가게를 메모해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-027');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-027', 0, 'MUSIC', 'Dynamite'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-027' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-027' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-028', 'sample-data', NULL, NULL, 'tourapi-130448', '어릴 때 갔던 기억이 나서 오랜만에 들렀다. 예전엔 큰 전시물이 마냥 신기했는데 지금은 설명을 더 오래 읽게 된다. 옆에서 신나게 이야기하는 아이들 보니까 나도 저랬겠지 싶었다. 혼자 와도 생각보다 재밌었다.', 'youth', FALSE, TRUE, NULL, '전시에서 가장 신기했던 자연 현상 하나를 골라 설명을 읽고 짧게 정리해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-028');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-028', 0, 'FILM', '월-E'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-028' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-028' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-029', 'sample-data', NULL, NULL, 'tourapi-129815', '전시 보다가 생각보다 시간이 많이 지났다. 원래는 일정 사이에 잠깐 들를 생각이었는데 오래된 물건 하나를 한참 봤다. 설명을 읽으니 그냥 물건이 아니더라. 사진 대신 기억해 두고 싶어서 메모만 했다.', 'miss', FALSE, TRUE, NULL, '관람 가능한 전시를 확인한 뒤 유물 하나를 골라 인상 깊었던 이유를 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-029');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-029', 0, 'FILM', '리틀 포레스트'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-029' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-029' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-030', 'sample-data', NULL, NULL, 'tourapi-130949', '친구가 자연사박물관 가자길래 솔직히 좀 의외였다. 막상 가니 구경할 게 많아서 내가 더 오래 서 있었다. 모르는 게 많아서 서로 찾아보면서 웃었다. 다음에 어디 갈지 물어보면 이런 곳도 괜찮다고 할 듯.', 'youth', FALSE, TRUE, NULL, '전시에서 처음 알게 된 생물이나 자연 이야기를 하나 찾아 친구에게 설명해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-030');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-030', 0, 'MUSIC', 'Blueming'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-030' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-030' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-031', 'sample-data', NULL, NULL, 'tourapi-130767', '시험 공부하려고 갔는데 처음 20분은 자리 정리만 했다. 그래도 집에 있을 때보단 집중이 잘돼서 미뤄둔 걸 좀 끝냈다. 나올 때 괜히 뿌듯했다. 공부를 엄청 많이 한 건 아니지만 오늘 할 일 하나는 줄었다.', 'courage', FALSE, TRUE, NULL, '도서관 이용 규정을 지키며 할 일 하나를 30분간 진행하고 끝낸 내용을 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-031');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-031', 0, 'MUSIC', '수고했어, 오늘도'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-031' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-031' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-032', 'sample-data', NULL, NULL, 'tourapi-4013035', '약속 전에 시간이 붕 떠서 서점에 들렀다. 제목만 보고 집어 든 책이 생각보다 재밌어서 몇 페이지 읽었다. 사려고 했던 책은 따로 있었는데 결국 다른 책을 샀다. 책 고르는 취향도 그날 기분 따라 바뀌는 것 같다.', 'youth', FALSE, TRUE, NULL, '서점에서 평소 읽지 않던 분야의 책을 하나 골라 첫인상을 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-032');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-032', 0, 'MUSIC', 'Love poem'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-032' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-032' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-033', 'sample-data', NULL, NULL, 'tourapi-3456362', '이대 근처에서 친구 기다리다가 공원에 잠깐 앉았다. 날씨가 애매해서 금방 갈 줄 알았는데 의외로 괜찮았다. 사람들 지나가는 거 보다 보니 친구가 도착했다. 기다리는 시간도 꼭 낭비는 아닌 것 같다.', 'rest', FALSE, TRUE, NULL, '공원에서 5분 정도 주변을 살피고 오늘 마음에 든 작은 풍경을 찾아보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-033');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-033', 0, 'MUSIC', '200%'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-033' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-033' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-034', 'sample-data', NULL, NULL, 'tourapi-2667611', '친구랑 옷 구경하러 왔다가 둘 다 정작 아무것도 안 샀다. 대신 평소에 안 입는 스타일을 서로 추천해 줬는데 그게 더 재밌었다. 사진만 잔뜩 남았다. 다음에 진짜 필요한 거 있을 때 다시 와야겠다.', 'youth', FALSE, TRUE, NULL, '거리의 쇼윈도에서 평소와 다른 스타일 하나를 찾아보고 어떤 점이 마음에 드는지 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-034');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-034', 0, 'MUSIC', 'Attention'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-034' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-034' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-035', 'sample-data', NULL, NULL, 'tourapi-2915659', '엄마 선물 사러 갔는데 뭐가 좋을지 몰라 한 바퀴만 세 번 돌았다. 결국 전화해서 슬쩍 물어봤더니 필요한 게 따로 있었다. 처음부터 물어볼걸. 그래도 고르면서 엄마 취향을 좀 더 알게 된 느낌이다.', 'comfort', FALSE, TRUE, NULL, '누군가에게 선물하고 싶은 작은 물건을 떠올리고 그 이유를 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-035');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-035', 0, 'MUSIC', '선물'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-035' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-035' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-036', 'sample-data', NULL, NULL, 'tourapi-2718417', '친구가 평소에 일회용 컵 줄이려고 노력한다고 해서 같이 들렀다. 생각보다 알아보는 재미가 있었다. 거창한 결심은 못 해도 오늘 텀블러 하나 챙긴 건 잘한 일 같다. 작게 시작하는 게 중요한 듯.', 'miss', FALSE, TRUE, NULL, '일상에서 일회용품을 한 번 줄일 방법을 골라 실천해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-036');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-036', 0, 'MUSIC', '작은 것들을 위한 시'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-036' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-036' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-037', 'sample-data', NULL, NULL, 'tourapi-2918764', '비 오는 날 따뜻한 게 먹고 싶어서 수제비를 먹었다. 같이 간 친구는 날씨랑 딱 맞는 메뉴라고 계속 이야기했다. 사실 특별한 일은 없었는데 수다 떨다 보니 한 시간이 훌쩍 갔다. 이런 날이 나중에 더 기억날지도.', 'comfort', FALSE, TRUE, NULL, '따뜻한 식사를 하며 오늘 있었던 소소한 일을 누군가와 나눠보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-037');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-037', 0, 'MUSIC', '비도 오고 그래서'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-037' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-037' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-038', 'sample-data', NULL, NULL, 'tourapi-2918773', '아침을 대충 먹고 나왔더니 점심때 너무 배고팠다. 친구랑 김치찌개 먹으면서 밥 한 그릇 뚝딱했다. 둘 다 말도 안 하고 먹다가 다 먹고서야 오늘 일정 얘기했다. 배고플 때는 일단 먹는 게 먼저인가 보다.', 'youth', FALSE, TRUE, NULL, '든든하게 식사한 뒤 오늘 힘이 됐던 작은 순간 하나를 떠올려보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-038');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-038', 0, 'MUSIC', 'After LIKE'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-038' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-038' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-039', 'sample-data', NULL, NULL, 'tourapi-2853889', '바쁜 날이라 점심을 건너뛸 뻔했다. 김밥 사서 먹는데 이상하게 그게 제일 맛있었다. 요즘 일 벌여 놓은 게 많아서 정신이 없는데 밥 먹는 시간만큼은 좀 천천히 가져야겠다고 생각했다.', 'rest', FALSE, TRUE, NULL, '바쁜 날에도 식사 시간을 잠시 확보하고 오늘 챙긴 작은 습관을 기록해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-039');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-039', 0, 'MUSIC', '좋아'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-039' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-039' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-040', 'sample-data', NULL, NULL, 'tourapi-2842966', '오랜만에 가족이랑 밥 먹었다. 다들 별 얘기 안 하는 것 같으면서도 한참 떠들었다. 음식 나올 때까지 서로 요즘 뭐 하는지 묻는데 괜히 웃겼다. 자주 보는 사이도 가끔은 이런 시간이 필요한 것 같다.', 'comfort', FALSE, TRUE, NULL, '가까운 사람과 식사하며 최근 좋았던 일 하나씩 이야기해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-040');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-040', 0, 'MUSIC', '가족사진'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-040' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-040' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-041', 'sample-data', NULL, NULL, 'tourapi-2900221', '친구가 가보고 싶다길래 같이 갔다. 맛있게 먹는 것도 좋았지만 오랜만에 둘이 오래 얘기한 게 더 기억난다. 요즘 서로 바쁜 얘기만 하다가 예전 추억까지 나왔다. 다음엔 내가 장소 골라보기로 했다.', 'comfort', FALSE, TRUE, NULL, '함께 식사한 사람에게 최근 가장 기억에 남는 하루를 물어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-041');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-041', 0, 'MUSIC', '우리의 밤은 당신의 낮보다 아름답다'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-041' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-041' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-042', 'sample-data', NULL, NULL, 'tourapi-2751441', '동네 시장이 궁금해서 들렀다. 그냥 지나칠 뻔한 골목에도 가게가 많더라. 가게마다 분위기가 다 달라서 구경하는 맛이 있었다. 이것저것 사고 싶었지만 오늘은 필요한 것만 샀다. 다음에 더 천천히 돌아보고 싶다.', 'youth', FALSE, TRUE, NULL, '시장에서 처음 보는 가게를 하나 찾아 어떤 물건을 파는지 살펴보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-042');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-042', 0, 'MUSIC', '산책'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-042' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-042' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-043', 'sample-data', NULL, NULL, 'falls', '친구가 사진 보여줘서 와봤다. 실제로는 물소리가 꽤 커서 처음에 놀랐다. 그 앞에 잠깐 서 있었는데 바람까지 시원했다. 도심에서 이런 풍경을 볼 수 있다는 게 조금 신기했다.', 'youth', FALSE, TRUE, NULL, '공개된 관람 공간에서 폭포를 감상하고 사진으로 담기 어려운 느낌 하나를 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-043');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-043', 0, 'MUSIC', '봄날'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-043' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-043' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-044', 'sample-data', NULL, NULL, 'tourapi-2783698', '오후에 머리가 복잡해서 근처를 걸었다. 물이 떨어지는 걸 한참 보니 시간 가는 줄 몰랐다. 사실 문제는 그대로인데 조금은 정리된 느낌이었다. 집에 가는 길엔 좋아하는 노래도 다시 들었다.', 'comfort', FALSE, TRUE, NULL, '폭포를 바라보며 천천히 숨을 고르고 오늘 생각을 복잡하게 만든 일을 한 줄로 정리해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-044');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-044', 0, 'MUSIC', '숨'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-044' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-044' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-045', 'sample-data', NULL, NULL, 'tourapi-2406564', '운동하자고 마음만 먹다가 드디어 걸었다. 처음에는 몸이 무겁더니 조금 지나니까 괜찮아졌다. 중간중간 쉬면서 물도 마셨다. 걷는 동안은 괜히 남이랑 비교할 일이 없어서 좋았다. 다음엔 조금 더 일찍 나와야지.', 'rest', FALSE, TRUE, NULL, '걷기 편한 구간을 선택해 자기 속도로 산책하고 끝나면 몸의 느낌을 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-045');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-045', 0, 'MUSIC', '걷자, 집앞이야'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-045' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-045' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-046', 'sample-data', NULL, NULL, 'tourapi-128028', '주변이 조용하다는 얘길 듣고 방문했다. 입구에 들어설 때부터 괜히 걸음이 느려졌다. 잠깐 머물렀는데도 생각보다 마음이 차분해졌다. 이런 곳에 올 땐 소리 낮추는 게 먼저라는 걸 새삼 느꼈다.', 'rest', FALSE, TRUE, NULL, '방문 예절을 지키며 주변을 조용히 둘러보고 기억에 남는 풍경을 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-046');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-046', 0, 'MUSIC', '바람이 불어오는 곳'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-046' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-046' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-047', 'sample-data', NULL, NULL, 'tourapi-126503', '날씨 좋은 날 천천히 둘러봤다. 도심에서 조금만 벗어나도 분위기가 달라지는 게 신기했다. 평소엔 사진부터 찍는데 오늘은 그냥 먼저 눈으로 봤다. 집에 와서는 어떤 장면이 가장 좋았는지 생각해 봤다.', 'rest', FALSE, TRUE, NULL, '방문 예절을 지키며 가장 마음에 남은 건축물이나 풍경 하나를 관찰해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-047');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-047', 0, 'MUSIC', '꽃'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-047' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-047' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-048', 'sample-data', NULL, NULL, 'tourapi-337557', '오랜만에 혼자 조용한 곳으로 가보고 싶었다. 주변을 둘러보면서 폰도 거의 안 봤다. 친구한테 답장해야 하는 게 생각났는데 집에 가서 해도 되겠더라. 급한 일 아닌 건 잠깐 미뤄도 괜찮다.', 'rest', FALSE, TRUE, NULL, '방문 예절을 지키며 5분 동안 휴대폰을 보지 않고 주변을 관찰해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-048');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-048', 0, 'MUSIC', '그대라는 시'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-048' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-048' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-049', 'sample-data', NULL, NULL, 'tourapi-3456475', '점심 먹고 바로 들어가기 애매해서 공원에 들렀다. 햇빛이 생각보다 좋아서 벤치에 앉아 있었다. 딱 십 분 쉬었는데 오후가 조금 덜 피곤했다. 커피 한 잔 더 마시는 것보다 나은 날도 있네.', 'rest', FALSE, TRUE, NULL, '식사 후 공원에서 잠깐 쉬고 오후에 하고 싶은 작은 일 하나를 정해보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-049');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-049', 0, 'MUSIC', '나의 사춘기에게'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-049' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-049' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-050', 'sample-data', NULL, NULL, 'tourapi-2759917', '어릴 때 자주 놀던 동네가 갑자기 생각나서 근처 공원을 걸었다. 놀이터는 여전히 아이들 웃음소리로 가득했다. 나도 저렇게 뛰어놀던 시절이 있었는데. 바쁜 척하며 지나온 시간이 꽤 긴 것 같다.', 'miss', FALSE, TRUE, NULL, '공공장소 이용 예절을 지키며 어린 시절 좋아했던 놀이 하나를 떠올려 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-050');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-050', 0, 'MUSIC', '오래된 노래'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-050' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-050' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-051', 'sample-data', NULL, NULL, 'tourapi-126531', '역사 안내를 읽다가 여기가 어떤 역할을 했는지 처음 알았다. 평소라면 사진 한 장 찍고 지나쳤을 텐데 이번엔 설명을 끝까지 읽었다. 비슷해 보이는 장소도 알고 보면 다 이야기가 있구나 싶었다.', 'youth', FALSE, TRUE, NULL, '안내판을 읽고 봉수대의 역할에 대해 새로 알게 된 내용을 한 문장으로 써보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-051');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-051', 0, 'FILM', '암살'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-051' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-051' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-052', 'sample-data', NULL, NULL, 'tourapi-131333', '운동해야지 말만 하다가 오랜만에 수영을 했다. 처음엔 몸이 무겁고 금방 지쳤는데 끝나고 나니까 기분은 좋았다. 다들 자기 페이스대로 하고 있어서 나도 괜히 조급하지 않았다. 다음 주에도 오면 좋겠다.', 'courage', FALSE, TRUE, NULL, '시설 이용 안내를 확인하고 자신에게 맞는 강도로 운동한 뒤 기분을 기록해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-052');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-052', 0, 'MUSIC', 'RUN'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-052' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-052' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-053', 'sample-data', NULL, NULL, 'tourapi-3082176', '공연 정보를 보다가 궁금해서 찾아갔다. 평소와 다른 방식으로 무대를 바라볼 수 있을 것 같아 기대됐다. 공연 보고 나오면서 친구랑 각자 기억에 남은 부분이 완전 달라서 재미있었다. 같은 걸 봐도 느끼는 게 다르다.', 'miss', FALSE, TRUE, NULL, '공식 공연 일정을 확인하고 관람한 뒤 인상 깊었던 장면을 자신의 말로 적어보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-053');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-053', 0, 'MUSIC', 'Love poem'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-053' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-053' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-054', 'sample-data', NULL, NULL, 'tourapi-252533', '학교 근처를 걷다가 건물이 예뻐서 한참 바라봤다. 지나다닐 때는 바빠서 그냥 지나쳤는데 자세히 보니 분위기가 정말 다르다. 친구한테 사진 보냈더니 어디냐고 물어봤다. 늘 가던 동네에도 처음 보는 장면이 있다.', 'youth', FALSE, TRUE, NULL, '출입 가능한 구역에서 건물 외관을 관찰하고 마음에 든 디테일 하나를 기록해 보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-054');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-054', 0, 'MUSIC', '첫사랑'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-054' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-054' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-055', 'sample-data', NULL, NULL, 'tourapi-252532', '근처에 갔다가 건물 외관이 눈에 띄어서 잠시 멈췄다. 학교 건물은 다 비슷할 거라 생각했는데 자세히 보니 느낌이 다르다. 오래된 것들은 한 번 더 들여다볼 만한 이유가 있는 것 같다.', 'youth', FALSE, TRUE, NULL, '출입 가능한 공간에서 외관을 살펴보고 건축에서 눈에 띄는 부분을 하나 찾아보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-055');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-055', 0, 'MUSIC', '좋은 사람'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-055' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-055' AND content_order = 0);

INSERT INTO experience_letter (id, owner, request_key, review_id, place_id, letter_text, tag, spoiler, sample, source_letter_id, routine, created_at)
SELECT 'dummy-letter-056', 'sample-data', NULL, NULL, 'tourapi-4025583', '약속보다 일찍 도착해서 카페에 들어갔다. 기다리는 동안 이것저것 할 생각이었는데 음악 듣다가 그냥 멍하니 있었다. 원래 혼자 있으면 시간 안 갈 줄 알았는데 금방 지나갔다. 다음엔 일부러 일찍 와도 괜찮겠다.', 'rest', FALSE, TRUE, NULL, '카페에서 잠깐 휴대폰을 내려놓고 오늘 가장 좋았던 순간을 하나 떠올려보세요.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-056');
INSERT INTO experience_letter_content (letter_id, content_order, content_type, content_title)
SELECT 'dummy-letter-056', 0, 'MUSIC', '밤편지'
WHERE EXISTS (SELECT 1 FROM experience_letter WHERE id = 'dummy-letter-056' AND sample = TRUE AND owner = 'sample-data')
  AND NOT EXISTS (SELECT 1 FROM experience_letter_content WHERE letter_id = 'dummy-letter-056' AND content_order = 0);

