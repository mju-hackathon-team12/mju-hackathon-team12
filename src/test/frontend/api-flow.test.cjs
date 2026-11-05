const fs = require('node:fs');
const vm = require('node:vm');
const assert = require('node:assert/strict');
const {webcrypto} = require('node:crypto');
const nodes = new Map();
function node(){return {value:'',hidden:false,style:{setProperty(){}},children:[],classList:{add(){},remove(){},toggle(){}},querySelectorAll:()=>[],querySelector:()=>null,append(){},before(){},remove(){},focus(){},reset(){},setAttribute(){},addEventListener(){},getContext:()=>({})};}
const get=id=>{if(!nodes.has(id))nodes.set(id,node());return nodes.get(id)};
const chain=new Proxy(()=>{}, {get:()=>chain,apply:()=>chain});
let calls=[];
const letter={id:'l1',text:'서버 본문',placeId:'p1',tag:'rest',contents:[{type:'music',title:'노래'}],spoiler:false,routine:''};
const API={preference:async x=>{calls.push(['preference',x]);return {preferenceId:'pref'}},recommend:async x=>{calls.push(['recommend',x]);return {letters:[{letter,why:'추천 이유'}]}},save:async x=>{calls.push(['save',x]);return letter},review:async x=>{calls.push(['review',x]);return {reviewId:'review',text:x.text,note:'확인',places:[{id:'p1',why:'이유'}]}},bootstrap:async()=>({contents:[{id:'song-id',type:'music',title:'노래',tag:'rest'}],places:[{id:'p1',name:'장소'}],letters:[letter]})};
const context=vm.createContext({API,MUSIC_ARTISTS:{'song-id':'테스트 가수'},GU:[{name:'서대문구',ring:[]}],window:{MOTION:{speed:1}},localStorage:{getItem:()=>null},matchMedia:()=>({matches:false}),gsap:new Proxy(chain,{get:(t,k)=>k==='utils'?{clamp:(min,max,v)=>Math.max(min,Math.min(max,v))}:chain}),DrawSVGPlugin:{},document:{getElementById:get,querySelectorAll:()=>[],querySelector:()=>({value:'p1'}),createElement:node,addEventListener(){}},addEventListener(){},innerWidth:1200,innerHeight:900,location:{search:''},navigator:{},crypto:webcrypto,console,alert(){}});
let code=fs.readFileSync('src/main/resources/static/app.js','utf8').replace(/^import .*;\r?\n/gm,'').replace(/start\(\);\s*$/,'');
vm.runInContext(code,context);
const run=code=>vm.runInContext(code,context);
(async()=>{
 run("intro=()=>{};pick=()=>{};PLACES.p1={name:'장소'}");
 await run('start()');assert.equal(run('LETTERS[0].text'),'서버 본문');
 run("S.ratings={a:{title:'동명',cat:'music',score:5},b:{title:'동명',cat:'film',score:4},c:{title:'세번째',cat:'drama',score:3}};");
 await run('read()');assert.equal(calls[0][0],'preference');assert.equal(calls[1][0],'recommend');assert.equal(run('S.stack[0].why'),'추천 이유');assert.notEqual(run("contentKey('music','동명')"),run("contentKey('film','동명')"));
 run("S.w={requestId:crypto.randomUUID(),review:{reviewId:'r'}}");
 API.save=async x=>{calls.push(['save',x]);throw Error('연결 실패')};await run('busy(saveLetter)');const id=calls.at(-1)[1].requestId;
 API.save=async x=>{calls.push(['save',x]);return letter};await run('busy(saveLetter)');assert.equal(calls.at(-1)[1].requestId,id);assert.equal(get('sentT').textContent,'장소 우체통에 편지를 맡겼습니다.');
 run("S.w={pre:{sourceLetterId:'source'}}");get('wBody').value='새 편지';get('wWorks').value='작품';get('wCat').value='film';get('wPlace').value='p1';
 await get('wform').onsubmit({preventDefault(){}});assert.equal(calls.at(-1)[0],'review');assert.equal(calls.at(-1)[1].sourceLetterId,'source');assert.equal(run('S.w.review.reviewId'),'review');
 API.review=async()=>({block:true,note:'수정해주세요'});const before=calls.filter(x=>x[0]==='save').length;await get('wform').onsubmit({preventDefault(){}});assert.equal(calls.filter(x=>x[0]==='save').length,before);assert.equal(get('status').textContent,'수정해주세요');assert.equal(get('loading').hidden,true);assert.equal(get('status').hidden,false);
 get('wBody').value='x'.repeat(401);await get('wform').onsubmit({preventDefault(){}});assert.equal(get('status').textContent,'편지는 1~400자로 입력해주세요.');
 for(const value of ['010-1234-5678','hello@example.com']){get('wBody').value=value;const count=calls.length;await get('wform').onsubmit({preventDefault(){}});assert.equal(calls.length,count);assert.equal(get('wm').hidden,false);assert.match(get('wm').textContent,/전화번호·이메일/)}
 get('wBody').value='안전한 본문';assert.equal(run('validateContact()'),true);assert.equal(get('wm').hidden,true);
 API.review=async()=>({reviewId:'corrected',corrected:true,text:'순화한 본문',note:'표현을 수정했어요.',places:[{id:'p1',why:'추천'}]});const saves=calls.filter(x=>x[0]==='save').length;await get('wform').onsubmit({preventDefault(){}});assert.equal(calls.filter(x=>x[0]==='save').length,saves);assert.match(get('reviewResult').innerHTML,/AI가 수정한 편지/);assert.match(get('reviewResult').innerHTML,/순화한 본문/);
 assert.equal(run('CATALOG.music.items[0][2]'),'테스트 가수');assert.match(run("tagInner({title:'노래',cat:'music',artist:'테스트 가수',r:0})"),/테스트 가수/);
 get('s-taste').hidden=false;run('V.y=0;V.k=1;V.paused=false');context.wheel={deltaY:100,deltaX:0,deltaMode:0,target:{closest:()=>null},preventDefault(){}};run('tasteWheel(wheel)');assert.equal(run('V.y'),-100);context.wheel.deltaY=-50;run('tasteWheel(wheel)');assert.equal(run('V.y'),-50);run('V.paused=true');run('tasteWheel(wheel)');assert.equal(run('V.y'),-50);
 let release;API.recommend=()=>new Promise(r=>release=r);const pending=run('recommendAgain()');assert.equal(get('loading').hidden,false);assert.match(get('loadingTitle').textContent,/편지를 찾고/);await run('recommendAgain()');assert.equal(get('loading').hidden,false);run('revision++');const stack=run('S.stack');release({letters:[{letter,why:'늦은 응답'}]});await pending;assert.equal(run('S.stack'),stack);assert.equal(get('loading').hidden,true);assert.equal(run('pending'),false);
 run("PLACES.p2={name:'두번째',lon:126.9,lat:37.6};PLACES.p3={name:'세번째',lon:126.91,lat:37.61};PLACES.hidden={name:'제외 장소',lon:126.92,lat:37.62};PLACES.p1.lon=126.93;PLACES.p1.lat=37.63;LETTERS.push({...LETTERS[0],id:'l2',place:'p2'},{...LETTERS[0],id:'l3',place:'p3'});S.stack=[{i:0},{i:1},{i:2}];S.place='p1';drawSvg()");
 const map=get('map').innerHTML;assert.equal((map.match(/class="spot"/g)||[]).length,3);assert.equal((map.match(/class="mailbox-icon"/g)||[]).length,3);assert.ok(!map.includes('data-k="hidden"'));assert.ok(!map.includes('<circle'));
 // Every literal DOM reference exists in the shipped HTML (dynamic elements are excluded).
 const html=fs.readFileSync('src/main/resources/static/index.html','utf8');for(const id of ['wBody','wCat','wWorks','wPlace','reviewResult','status','sGo'])assert.ok(html.includes(`id="${id}"`),id);
 console.log('PASS: bootstrap, recommendation payload, category identity, save retry UUID, stale response, contact validation, corrected preview, artists, wheel movement, form elements');
})().catch(e=>{console.error(e);process.exitCode=1});
