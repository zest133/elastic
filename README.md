# elastic
Lucene3.6 을 elasticsearch 1.7로 변경. 
*환경 구성.
java 1.7 
spring 3.x
elasticsearch 1.7.1

*cluster
shard-5 , replaca-5

*구현 방식. 
es api 모듈을 이용한 구현방식. 
*입력방식
초기 data는 bulk를 이용해서 index 

HTML 파일 검색 엔진... 
* 2015.9.21변경 사항.(lucene-> es)
1. custom analyzer를 es custom_analyzer로 변경. 
  lucene은 standard가 lowcase가 없으므로 standard+lowcase 를 하였으나, es는 기본이 lowcase포함. 
  
2. text 와 text.html을  text는 html content를 저장. text.html 원본 데이터 저장(not_analyzed)
  es 기본 stadard는 255token으로 제한 이로 인해 field("ignore_above", 256) 추가 
3. 검색 highlight는 js로 처리 한것을 es highlight로 변경. 


*변경 사항.
1. 기존 Tika를 이용한 content 저장을. html_strip으로 저장.
    text와 text.html로 만든 필드를 html로 변경. field("ignore_above", 256) 속성 삭제. 
2. 검색 방식 and 추가. 

*변경 사항.

1.구글과 같은 고급 검색엔진 구현. 기존 lucene을 es로 변경 
  porter stemming 방식으로 검색 입력값을 es로 변경하려 했으나 rest api를 사용해서 검색 결과를 가져와야함. 그래서 걍. lucene으로 
  구현하여, english analyzer의 porter stemming 값을 가져옴. 
2. 검색 방식은 porter stemming 값에 + prefix로 검색. 
3. and , or, span near query, not and 추가. 

*변경 사항. 
1. Filter 추가. Lucene의 Chain 방식 같이 여러개의 필터를 추가. 

*변경사항
1. 웹부분을 위해 branch 분리. 
2. tiles 모듈 추가. 
3. category, title, locale 검색 정보 추가 (sorting 도 같이)

*변경 사항.
1. 검색 추가 - AND
2. 고급 검색 추가 - AND, OR, Spannear, not and - 단 and가 무조건 있어야함. 버그 -_-;; 귀찮니즘... 
3. stopword checking 추가. (StopAnalyzer의 term을 비교. 더 좋은 방법 없나 ... es에선 stop list를 제공 안해주는듯 )
4. 인덱스 컬럼 변경 - 기존 html만 index을 했으나 검색 결과를 찾을때는 strips html이 필요 이로 인해 tika를 이용해 plain text 추출 후 text, text.html로 분리. 
5. 하이라이트 추가. setHighlighterPreTags, setHighlighterPostTags 를 이용. (lucene에서 없던거라 좋구만.기존 tika에선 좀 헤멧는데.. 루씬에도 있던건 당최 어떻게 쓰는지 몰랐지만.)
6. 필터 추가. 체이닝 방식 - 카테고리 제목, 브레드크럼, locale정보. 로 체이닝 방식 있는것만 적용. 


아직 html 불러오는건 안했는데 여기서 끝.... 

