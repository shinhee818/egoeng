INSERT INTO roles (id, name)
VALUES (1, 'USER'), (2, 'ADMIN')
ON CONFLICT DO NOTHING;

INSERT INTO users (created_at, updated_at, email, name, role_id, password)
VALUES (now(), now(), 'master@naver.com', 'master', 1, '$2a$10$slYQmyNdGzin7olVN.5rGON8E.hQq6/VmPY9kVy1khS5kLwE8Q9/C')
ON CONFLICT DO NOTHING;
INSERT INTO word (text, level)
VALUES
  ('compound', 'ADVANCED'),
  ('yield', 'ADVANCED'),
  ('sanction', 'ADVANCED'),
  ('bear', 'INTERMEDIATE'),
  ('contract', 'INTERMEDIATE'),
  ('subject', 'INTERMEDIATE'),
  ('a', 'BEGINNER'),
  ('ability', 'INTERMEDIATE'),
  ('about', 'BEGINNER'),
  ('absence', 'INTERMEDIATE'),
  ('academic', 'INTERMEDIATE'),
  ('accept', 'BEGINNER'),
  ('accepted', 'BEGINNER'),
  ('accepting', 'BEGINNER'),
  ('access', 'INTERMEDIATE'),
  ('accident', 'BEGINNER'),
  ('accomplish', 'INTERMEDIATE'),
  ('account', 'INTERMEDIATE'),
  ('accurate', 'INTERMEDIATE'),
  ('achieve', 'INTERMEDIATE'),
  ('achieved', 'INTERMEDIATE'),
  ('achieving', 'INTERMEDIATE'),
  ('acknowledge', 'INTERMEDIATE'),
  ('acquire', 'INTERMEDIATE'),
  ('act', 'BEGINNER'),
  ('action', 'BEGINNER'),
  ('active', 'BEGINNER'),
  ('activity', 'BEGINNER'),
  ('actor', 'BEGINNER'),
  ('actual', 'BEGINNER'),
  ('actually', 'BEGINNER'),
  ('add', 'BEGINNER'),
  ('added', 'BEGINNER'),
  ('adding', 'BEGINNER'),
  ('addition', 'BEGINNER'),
  ('address', 'BEGINNER'),
  ('adequate', 'INTERMEDIATE'),
  ('adjust', 'INTERMEDIATE'),
  ('administration', 'INTERMEDIATE'),
  ('admit', 'BEGINNER'),
  ('adult', 'BEGINNER'),
  ('advance', 'INTERMEDIATE'),
  ('advantage', 'INTERMEDIATE'),
  ('advertise', 'INTERMEDIATE'),
  ('advice', 'BEGINNER'),
  ('affect', 'INTERMEDIATE'),
  ('afford', 'BEGINNER'),
  ('afraid', 'BEGINNER'),
  ('after', 'BEGINNER'),
  ('afternoon', 'BEGINNER'),
  ('again', 'BEGINNER'),
  ('against', 'BEGINNER'),
  ('age', 'BEGINNER'),
  ('agency', 'INTERMEDIATE'),
  ('agent', 'INTERMEDIATE'),
  ('ago', 'BEGINNER'),
  ('agree', 'BEGINNER'),
  ('agreed', 'BEGINNER'),
  ('agreeing', 'BEGINNER'),
  ('agreement', 'INTERMEDIATE'),
  ('ahead', 'BEGINNER'),
  ('aid', 'INTERMEDIATE'),
  ('aim', 'INTERMEDIATE'),
  ('air', 'BEGINNER'),
  ('airline', 'BEGINNER'),
  ('airport', 'BEGINNER'),
  ('alarm', 'BEGINNER'),
  ('album', 'BEGINNER'),
  ('alcohol', 'BEGINNER'),
  ('alive', 'BEGINNER'),
  ('all', 'BEGINNER'),
  ('allow', 'BEGINNER'),
  ('allowed', 'BEGINNER'),
  ('allowing', 'BEGINNER'),
  ('almost', 'BEGINNER'),
  ('alone', 'BEGINNER'),
  ('along', 'BEGINNER'),
  ('already', 'BEGINNER'),
  ('also', 'BEGINNER'),
  ('alter', 'INTERMEDIATE'),
  ('alternative', 'INTERMEDIATE'),
  ('although', 'BEGINNER'),
  ('always', 'BEGINNER'),
  ('amazing', 'BEGINNER'),
  ('among', 'INTERMEDIATE'),
  ('amount', 'BEGINNER'),
  ('an', 'BEGINNER'),
  ('analysis', 'INTERMEDIATE'),
  ('analyze', 'INTERMEDIATE'),
  ('ancient', 'INTERMEDIATE'),
  ('and', 'BEGINNER'),
  ('anger', 'BEGINNER'),
  ('angle', 'INTERMEDIATE'),
  ('angry', 'BEGINNER'),
  ('animal', 'BEGINNER'),
  ('announce', 'INTERMEDIATE'),
  ('annual', 'INTERMEDIATE'),
  ('another', 'BEGINNER'),
  ('answer', 'BEGINNER'),
  ('anticipate', 'ADVANCED'),
  ('anxiety', 'INTERMEDIATE'),
  ('any', 'BEGINNER'),
  ('anybody', 'BEGINNER'),
  ('anymore', 'BEGINNER'),
  ('anyone', 'BEGINNER'),
  ('anything', 'BEGINNER'),
  ('anyway', 'BEGINNER'),
  ('anywhere', 'BEGINNER'),
  ('apart', 'BEGINNER'),
  ('apartment', 'BEGINNER'),
  ('apologize', 'BEGINNER'),
  ('apparent', 'INTERMEDIATE'),
  ('apparently', 'INTERMEDIATE'),
  ('appeal', 'INTERMEDIATE'),
  ('appear', 'BEGINNER'),
  ('appearance', 'INTERMEDIATE'),
  ('appeared', 'BEGINNER'),
  ('appearing', 'BEGINNER'),
  ('apple', 'BEGINNER'),
  ('application', 'INTERMEDIATE'),
  ('applied', 'INTERMEDIATE'),
  ('apply', 'INTERMEDIATE'),
  ('applying', 'INTERMEDIATE'),
  ('appoint', 'INTERMEDIATE'),
  ('appointment', 'INTERMEDIATE'),
  ('appreciate', 'INTERMEDIATE'),
  ('approach', 'INTERMEDIATE'),
  ('appropriate', 'INTERMEDIATE'),
  ('approval', 'INTERMEDIATE'),
  ('approve', 'INTERMEDIATE'),
  ('approximately', 'INTERMEDIATE'),
  ('architect', 'INTERMEDIATE'),
  ('are', 'BEGINNER'),
  ('area', 'BEGINNER'),
  ('argue', 'BEGINNER'),
  ('argument', 'INTERMEDIATE'),
  ('arise', 'INTERMEDIATE'),
  ('arm', 'BEGINNER'),
  ('army', 'BEGINNER'),
  ('around', 'BEGINNER'),
  ('arrange', 'BEGINNER'),
  ('arrangement', 'INTERMEDIATE'),
  ('arrest', 'INTERMEDIATE'),
  ('arrival', 'BEGINNER'),
  ('arrive', 'BEGINNER'),
  ('art', 'BEGINNER'),
  ('article', 'BEGINNER'),
  ('artist', 'BEGINNER'),
  ('artistic', 'INTERMEDIATE'),
  ('as', 'BEGINNER'),
  ('ashamed', 'BEGINNER'),
  ('aside', 'INTERMEDIATE'),
  ('ask', 'BEGINNER'),
  ('asked', 'BEGINNER'),
  ('asking', 'BEGINNER'),
  ('asleep', 'BEGINNER'),
  ('aspect', 'INTERMEDIATE'),
  ('assault', 'INTERMEDIATE'),
  ('assess', 'INTERMEDIATE'),
  ('assessment', 'INTERMEDIATE'),
  ('asset', 'INTERMEDIATE'),
  ('assign', 'INTERMEDIATE'),
  ('assignment', 'INTERMEDIATE'),
  ('assist', 'INTERMEDIATE'),
  ('assistance', 'INTERMEDIATE'),
  ('assistant', 'INTERMEDIATE'),
  ('associate', 'INTERMEDIATE'),
  ('association', 'INTERMEDIATE'),
  ('assume', 'INTERMEDIATE'),
  ('assumption', 'INTERMEDIATE'),
  ('assure', 'INTERMEDIATE'),
  ('at', 'BEGINNER'),
  ('ate', 'BEGINNER'),
  ('athlete', 'BEGINNER'),
  ('atmosphere', 'INTERMEDIATE'),
  ('attach', 'INTERMEDIATE'),
  ('attack', 'BEGINNER'),
  ('attempt', 'INTERMEDIATE'),
  ('attend', 'INTERMEDIATE'),
  ('attention', 'BEGINNER'),
  ('attitude', 'INTERMEDIATE'),
  ('attorney', 'INTERMEDIATE'),
  ('attract', 'INTERMEDIATE'),
  ('attractive', 'BEGINNER'),
  ('audience', 'INTERMEDIATE'),
  ('author', 'BEGINNER'),
  ('authority', 'INTERMEDIATE'),
  ('auto', 'BEGINNER'),
  ('available', 'INTERMEDIATE'),
  ('average', 'BEGINNER'),
  ('avoid', 'INTERMEDIATE'),
  ('award', 'BEGINNER'),
  ('aware', 'INTERMEDIATE'),
  ('awareness', 'INTERMEDIATE'),
  ('away', 'BEGINNER'),
  ('awful', 'BEGINNER'),
  ('baby', 'BEGINNER'),
  ('back', 'BEGINNER'),
  ('background', 'INTERMEDIATE'),
  ('bad', 'BEGINNER'),
  ('badly', 'BEGINNER'),
  ('bag', 'BEGINNER'),
  ('bake', 'BEGINNER'),
  ('balance', 'INTERMEDIATE'),
  ('ball', 'BEGINNER'),
  ('ban', 'INTERMEDIATE')
ON CONFLICT (text) DO NOTHING;

-- 4. Word_Meaning 테이블에 의미 삽입
-- compound (2개의 의미)
INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '혼합물, 복합체', 'noun', 'Water is a compound of hydrogen and oxygen.' FROM word WHERE text = 'compound' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '악화시키다', 'verb', 'His rude response compounded the problem.' FROM word WHERE text = 'compound' LIMIT 1;

-- yield (2개의 의미)
INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '생산하다, 산출하다', 'verb', 'The farm yields a good harvest every year.' FROM word WHERE text = 'yield' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '굴복하다, 양보하다', 'verb', 'He refused to yield to pressure.' FROM word WHERE text = 'yield' LIMIT 1;

-- sanction (2개의 의미)
INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '허가하다, 승인하다', 'verb', 'The government sanctioned the new policy.' FROM word WHERE text = 'sanction' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '제재, 처벌', 'noun', 'The country faced international sanctions.' FROM word WHERE text = 'sanction' LIMIT 1;

-- bear (2개의 의미)
INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '참다, 견디다', 'verb', 'I can''t bear this pain any longer.' FROM word WHERE text = 'bear' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '(열매를) 맺다', 'verb', 'The apple trees bear fruit every autumn.' FROM word WHERE text = 'bear' LIMIT 1;

-- contract (2개의 의미)
INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '계약하다', 'verb', 'They contracted a supplier for materials.' FROM word WHERE text = 'contract' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '수축하다', 'verb', 'Metals contract in cold temperatures.' FROM word WHERE text = 'contract' LIMIT 1;

-- subject (2개의 의미)
INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '주제, 피실험자', 'noun', 'The subject of today''s lecture is climate change.' FROM word WHERE text = 'subject' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '지배를 받는', 'adjective', 'All citizens are subject to the law.' FROM word WHERE text = 'subject' LIMIT 1;

-- 나머지 단어들 (각 1개씩 의미)
INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '하나의, 어떤', 'noun', 'I saw a bird.' FROM word WHERE text = 'a' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '능력', 'noun', 'She has the ability to speak three languages.' FROM word WHERE text = 'ability' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '약, 대략', 'preposition', 'The meeting is about to start.' FROM word WHERE text = 'about' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '부재, 결석', 'noun', 'His absence was noticed.' FROM word WHERE text = 'absence' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '학문의, 학술의', 'adjective', 'She has an academic degree.' FROM word WHERE text = 'academic' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '받아들이다', 'verb', 'I accept your apology.' FROM word WHERE text = 'accept' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '받아들여진', 'adjective', 'This is the accepted standard.' FROM word WHERE text = 'accepted' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '받아들이는', 'adjective', 'He has an accepting attitude.' FROM word WHERE text = 'accepting' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '접근, 접근권', 'noun', 'You need access to enter.' FROM word WHERE text = 'access' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '사고', 'noun', 'There was a car accident.' FROM word WHERE text = 'accident' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '성취하다', 'verb', 'We can accomplish this task.' FROM word WHERE text = 'accomplish' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '계정, 설명', 'noun', 'I have a bank account.' FROM word WHERE text = 'account' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '정확한', 'adjective', 'This is an accurate measurement.' FROM word WHERE text = 'accurate' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '달성하다', 'verb', 'We will achieve our goals.' FROM word WHERE text = 'achieve' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '달성된', 'adjective', 'He achieved his dream.' FROM word WHERE text = 'achieved' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '달성하는', 'adjective', 'She is achieving success.' FROM word WHERE text = 'achieving' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '인정하다', 'verb', 'I acknowledge your help.' FROM word WHERE text = 'acknowledge' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '획득하다', 'verb', 'We need to acquire more skills.' FROM word WHERE text = 'acquire' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '행동하다, 행동', 'noun', 'We must act now.' FROM word WHERE text = 'act' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '행동, 조치', 'noun', 'We need to take action.' FROM word WHERE text = 'action' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '활동적인', 'adjective', 'She is very active.' FROM word WHERE text = 'active' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '활동', 'noun', 'This is a fun activity.' FROM word WHERE text = 'activity' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '배우', 'noun', 'He is a famous actor.' FROM word WHERE text = 'actor' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '실제의', 'adjective', 'This is the actual price.' FROM word WHERE text = 'actual' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '실제로', 'adverb', 'Actually, I agree with you.' FROM word WHERE text = 'actually' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '더하다', 'verb', 'Please add some sugar.' FROM word WHERE text = 'add' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '추가된', 'adjective', 'Added information is important.' FROM word WHERE text = 'added' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '추가하는', 'verb', 'Adding salt to the soup.' FROM word WHERE text = 'adding' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '추가, 덧셈', 'noun', 'In addition, we need more time.' FROM word WHERE text = 'addition' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '주소, 연설하다', 'noun', 'What is your address?' FROM word WHERE text = 'address' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '충분한', 'adjective', 'This is adequate for our needs.' FROM word WHERE text = 'adequate' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '조정하다', 'verb', 'Please adjust the settings.' FROM word WHERE text = 'adjust' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '행정, 관리', 'noun', 'The administration is efficient.' FROM word WHERE text = 'administration' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '인정하다, 입장을 허용하다', 'verb', 'I admit I was wrong.' FROM word WHERE text = 'admit' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '성인', 'noun', 'He is an adult now.' FROM word WHERE text = 'adult' LIMIT 1;

INSERT INTO word_meaning (word_id, meaning_ko, part_of_speech, example)
SELECT id, '이득, 장점', 'noun', 'The advantage of this method is its simplicity.' FROM word WHERE text = 'advantage' LIMIT 1;

-- 5. 일일 문장 데이터 삽입
INSERT INTO daily_sentence (sentence, meaning_ko, explanation, category, example_dialogue, date, created_at, updated_at)
VALUES
    ('How''s it going?', '어떻게 지내?', '친한 사이에서 사용하는 일상적인 인사 표현입니다.', '일상', 'A: How''s it going?\nB: Pretty good, thanks!', CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('What''s up?', '무슨 일이야?', '매우 캐주얼한 인사 표현입니다.', '일상', 'A: What''s up?\nB: Not much!', CURRENT_DATE + INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Long time no see!', '오랜만이야!', '오랫동안 만나지 못했을 때 사용합니다.', '일상', 'A: Long time no see!\nB: I''ve been great!', CURRENT_DATE + INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('I''m looking forward to it.', '기대하고 있어요.', '앞으로 일어날 일에 대해 기대감을 표현합니다.', '일상', 'A: The concert is next week!\nB: I''m looking forward to it!', CURRENT_DATE + INTERVAL '3 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('That sounds great!', '좋은 것 같아요!', '상대방의 제안에 긍정적으로 반응합니다.', '일상', 'A: How about we go to the beach?\nB: That sounds great!', CURRENT_DATE + INTERVAL '4 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (date) DO NOTHING;











