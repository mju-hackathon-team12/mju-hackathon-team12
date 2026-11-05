import { API } from './api.js';
import { GU } from './map-data.js';
const S={ratings:Object.create(null),mood:'',gu:'서울 전체',stack:[],seen:[],pos:0,place:null,letter:null,atSite:false,preferenceId:null,temporaryContents:true,zone:'intro',ready:false};
const NEED=3;
const ACTS={film:['오늘 상영작 중 모르는 영화 한 편 보기','나오는 길에 기억나는 대사 적기'],ansan:['숲길을 이어폰 없이 걷기','메타세쿼이아 숲에서 잠깐 앉아 있기'],falls:['물가에 앉아 물소리 듣기','물길을 따라 안산 쪽으로 걷기'],prison:['역사관 전시를 천천히 보기','오래 남은 이름 하나 기억하기'],gate:['독립공원 한 바퀴 걷기','벤치에서 지나가는 사람 보기'],market:['시장 음식 하나 맛보기','끝까지 천천히 걸어보기']};
const PS=['여기까지 와줘서 고마워요. 물소리가 들리면 잠깐만 눈을 감아봐요.','이 길을 걷는 동안만큼은 아무 말도 안 해도 돼요.','이 극장 불이 켜질 때까지 앉아 있어 보세요.','오늘 본 영화의 마지막 장면이 뭐였는지 알려주고 싶었어요.','여기서는 천천히 걸어주세요.','올려다봐줘서 고마워요.','당신도 누군가에게 고개를 끄덕여 주면 좋겠어요.','시장 끝까지 걸으면 제일 맛있는 냄새가 나요.','사장님께 먼저 인사해 보세요.','여기서 보면 다 작아 보여요. 그래도 괜찮아요.'];
const YEOUN={comfort:'말없이 곁에 있어주길 바라는 마음',rest:'잠깐 쉬어가고 싶은 마음',miss:'지나간 시간을 돌아보는 마음',youth:'서툴지만 시작하고 싶은 마음',courage:'조금 더 버텨보려는 마음'};

function upsertLetter(letter){
  const cut=letter.text.search(/[.?!]\s/),view={...letter,place:letter.placeId,tags:[letter.tag],first:cut>0?letter.text.slice(0,cut+1):letter.text,body:cut>0?letter.text.slice(cut+1).trim():'',taste:letter.contents.map(c=>c.title)};
  const i=LETTERS.findIndex(item=>item.id===letter.id);
  if(i>=0){
    LETTERS[i]=view;
    return i
  }
  LETTERS.push(view);
  return LETTERS.length-1;
}
const CATALOG={music:{name:'음악',items:[]},film:{name:'영화',items:[]},drama:{name:'드라마',items:[]}};
const PLACES={};
const LETTERS=[];
const PLACE_MOOD={};

function contentKey(cat,title){
  return JSON.stringify([cat,title.trim().toLowerCase()]);
}
// Safe for HTML text and quoted attributes. Set form values via DOM properties.

function esc(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({
  '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
  })[char]);
}
let revision = 0;
const timers = new Set();

function invalidateScreen() {
  revision++;
  timers.forEach(clearTimeout);
  timers.clear();
}
// Capture before awaiting. Returning to the same view still creates a new scope.

function currentScreen() {
  const captured = revision;
  return () => captured === revision;
}

function screenDelay(callback, delay) {
  const active = currentScreen();
  const timer = setTimeout(() => {
    timers.delete(timer);
    if (active()) callback();
  }, delay);
  timers.add(timer);
  return timer;
}
const $ = id => document.getElementById(id);
const ui = $('ui');
const stage = $('stage');
const cv = $('cloud');
const reduced = matchMedia('(prefers-reduced-motion: reduce)').matches;

function enterScreen(name) {
  invalidateScreen();
  S.zone = name;
  cv.hidden = true;
  $('space').hidden = true;
  stage.innerHTML = '';
  stage.style.transform = '';
}
const INK='#2c2c30',TINT='#6f86c9',WASH='#dfe4f3';

/* ---------- 손그림 도구 ---------- */
const NS='http://www.w3.org/2000/svg';

function svg(w,h,cls){
  const s=document.createElementNS(NS,'svg');
  s.setAttribute('width',w);
  s.setAttribute('height',h);
  s.setAttribute('viewBox',`0 0 ${w} ${h}`);
  if(cls)s.setAttribute('class',cls);
  s.setAttribute('aria-hidden','true');
  return s
}

function box(el,o={}){
  const old=el.querySelector(':scope > .sk');
  if(old)old.remove();
  const w=el.offsetWidth,h=el.offsetHeight;
  if(!w||!h)return;
  const s=svg(w,h,'sk'),rc=rough.svg(s);
  s.append(rc.rectangle(3,3,w-6,h-6,{roughness:1.3,bowing:1.8,stroke:INK,strokeWidth:1.4,seed:o.seed||(w*7+h|0)||1,...(o.fill?{fill:o.fill,fillStyle:'solid'}:{})}));
  el.prepend(s)
}

function sketch(root){
  root.querySelectorAll('[data-sk]').forEach(e=>box(e,{fill:e.dataset.sk||undefined}))
}
const stars=n=>'★'.repeat(n);
const FLOW={get:['취향 입력','레터 고르기','우체통 찾기','읽기','따라 하기'],put:['쓰기','장소 정하기','AI 확인']};

function steps(kind,i){
  return `<ol class="steps" aria-label="진행 단계">${FLOW[kind].map((t,k)=>`<li class="${k===i?'now':k<i?'done':''}"${k===i?' aria-current="step"':''}><i>${k<i?'✓':k+1}</i><span>${t}</span></li>`).join('')}</ol>`
}
let seed=11062026;
const rand=()=>(seed=(seed*16807)%2147483647)/2147483647;

/* ---------- 사진 구름 ---------- */
const cg=cv.getContext('2d');

const tiles=[];
let form='cloud',rotA=0,tw=0,th=0,hover=null;

function paper(t){
  const c=document.createElement('canvas');
  c.width=260;
  c.height=320;
  const g=c.getContext('2d'),r=S.ratings[contentKey(t.cat,t.title)];
  g.fillStyle=r?WASH:'#fbfaf6';
  g.fillRect(0,0,260,320);
  g.strokeStyle=INK;
  g.lineWidth=5;
  g.strokeRect(5,5,250,310);
  g.fillStyle='#74747a';
  g.font='500 24px Pretendard,"Noto Sans KR",sans-serif';
  g.fillText(CATALOG[t.cat].name,22,48);
  g.fillStyle=INK;
  g.font='600 36px Pretendard,"Noto Sans KR",sans-serif';
  const w=[...t.title];
  let line='',y=250-(t.title.length>8?44:0);
  for(const ch
  of w){
    if(g.measureText(line+ch).width>214){
      g.fillText(line,22,y);
      y+=44;
      line=ch
    } else line+=ch
  }
  g.fillText(line,22,y);
  if(r){
    g.fillStyle=TINT;
    g.font='36px sans-serif';
    g.fillText(stars(r.score),22,110)
  }
  t.img=c
}

function addWork(cat,title,tag){
  const t=tiles.find(x=>contentKey(x.cat,x.title)===contentKey(cat,title));
  if(t)return t;
  const u=rand()*2-1,a=rand()*6.283,rr=120+rand()*240,q=Math.sqrt(1-u*u),b=[rr*q*Math.cos(a)*1.5,rr*u*.8,rr*q*Math.sin(a)*.8];
  const o={b,p:[...b],t:[...b],k:tiles.length,cat,title,tag,s:2.5,r:(rand()-.5)*.3};
  paper(o);
  tiles.push(o);
  return o
}

function buildTiles(){
  tiles.length=0;
  Object.entries(CATALOG).forEach(([cat,c])=>c.items.forEach(([t,tag])=>addWork(cat,t,tag)));
  Object.values(S.ratings).forEach(r=>addWork(r.cat,r.title,r.tag||'comfort'));
  formTiles()
}

function formTiles(){
  const n=tiles.length;
  tiles.forEach((o,k)=>{
    const i=(k*97)%n;
    if(form==='helix'){
      const u=i/n,a=u*6.283*2.6;
      o.t=[Math.cos(a)*340,(u-.5)*560,Math.sin(a)*340]
    } else if(form==='orb'){
      const y=1-2*(i+.5)/n,rr=Math.sqrt(1-y*y),a=i*2.39996;
      o.t=[Math.cos(a)*rr*320,y*320,Math.sin(a)*rr*320]
    } else o.t=[...o.b]
  })
}

function setForm(f){
  form=f;
  formTiles();
  document.querySelectorAll('[data-f]').forEach(b=>b.setAttribute('aria-pressed',b.dataset.f===f))
}

function sizeCloud(){
  const d=Math.min(devicePixelRatio||1,2);
  tw=innerWidth;
  th=innerHeight;
  cv.width=tw*d;
  cv.height=th*d;
  cg.setTransform(d,0,0,d,0,0)
}

function drawCloud(){
  requestAnimationFrame(drawCloud);
  if(S.zone!=='rate')return;
  const n=tw<=820,paused=$('rm');
  if(!reduced&&!paused)rotA+=hover?.0004:(form==='helix'?.006:.0022);
  const sc=n?Math.min(tw,th*.56)/960:Math.min(tw-340,th-90)/900,cx=n?tw/2:(tw-340)/2,cy=n?th*.33:th*.53;
  cg.clearRect(0,0,tw,th);
  const c=Math.cos(rotA),s=Math.sin(rotA);
  for(const o
  of tiles){
    for(let i=0
    ;i<3;i++)o.p[i]+=(o.t[i]-o.p[i])*(reduced?1:.05);
    const X=o.p[0]*c+o.p[2]*s,Z=-o.p[0]*s+o.p[2]*c,f=860/(860+Z);
    o.z=Z;
    o.f=f;
    o.x=cx+X*f*sc;
    o.y=cy+o.p[1]*f*sc;
    o.w=(o.title?Math.max(n?54:76,30*o.s*f*sc):30*o.s*f*sc)*(o===hover?1.25:1);
    o.h=o.w*1.25
  }
  const list=[...tiles].sort((a,b)=>(a.title?1:0)-(b.title?1:0)||b.z-a.z);
  for(const o
  of list){
    cg.save();
    cg.translate(o.x,o.y);
    cg.rotate(o===hover?0:o.r*.3);
    cg.globalAlpha=Math.max(.6,Math.min(1,(o.f-.5)/.45));
    cg.drawImage(o.img,-o.w/2,-o.h/2,o.w,o.h);
    cg.restore()
  }
}

function hit(x,y){
  let best=null;
  for(const o
  of tiles){
    if(o.title&&Math.abs(x-o.x)<o.w/2+3&&Math.abs(y-o.y)<o.h/2+3&&(!best||o.z<best.z))best=o
  }
  return best
}
cv.addEventListener('pointermove',e=>{
  hover=hit(e.clientX,e.clientY);
  cv.style.cursor=hover?'pointer':'default'
});
cv.addEventListener('pointerleave',()=>hover=null);
cv.addEventListener('click',e=>{
  const o=hit(e.clientX,e.clientY);
  if(o)rateModal(o)
});

function drawEnv(){
  const s=svg(300,200),rc=rough.svg(s),o={roughness:1.4,stroke:INK,strokeWidth:1.7,seed:9};
  s.removeAttribute('width');
  s.removeAttribute('height');
  s.append(rc.rectangle(6,18,288,172,{...o,fill:'#fbfaf6',fillStyle:'solid'}),rc.line(6,190,120,104,o),rc.line(294,190,180,104,o));
  const f=rc.path('M6 18L150 122L294 18',{...o,fill:'#fbfaf6',fillStyle:'solid'});
  f.setAttribute('class','flap');
  s.append(f,rc.circle(150,120,22,{...o,fill:TINT,fillStyle:'hachure',hachureGap:3.5,stroke:TINT}));
  return s
}

function intro(){
  enterScreen('intro');
  ui.innerHTML=`<div class="center"><div class="start"><div class="env" id="env"></div><p class="lead">작품이 남긴 마음을,<br>이름 없이 서울에 맡기는 곳</p><div class="choose"><button class="door" data-sk="#fbfaf6" id="get" type="button"><b>추천 받기</b><span>좋아하는 작품에 별점을 남기면, AI가 어울리는 레터를 찾아줘요.</span></button><button class="door" data-sk="#fbfaf6" id="put" type="button"><b>레터 남기기</b><span>작품을 보고 남은 마음을 서울의 한 장소에 맡겨요.</span></button></div><p class="small">가입 없이 바로 쓸 수 있어요.</p></div></div>`;
  $('env').append(drawEnv());
  sketch(ui);
  const go=f=>{
    $('env').classList.add('open');
    screenDelay(f,reduced?0:520)
  };
  $('get').onclick=()=>go(()=>rate());
  $('put').onclick=()=>go(()=>write())
}

/* ---------- 평점 ---------- */

function rate(){
  enterScreen('rate');
  cv.hidden=false;
  sizeCloud();
  buildTiles();
  ui.innerHTML=`${steps('get',0)}<div class="forms" aria-label="사진 배치">${[['cloud','1'],['helix','2'],['orb','3']].map(([f,n])=>`<button type="button" data-f="${f}" aria-pressed="${form===f}" aria-label="배치 ${n}">${n}</button>`).join('')}</div>
  <aside class="dock3" data-sk="#f5f4ef"><p class="tip">${S.temporaryContents?'예시 작품':'작품'} 카드를 눌러 별점을 남겨요.</p><button class="text" id="lt" type="button">작품 목록으로 보기</button><div class="picked" id="pk"></div>
  <form id="af" class="field"><label for="aw">찾는 작품이 없다면</label><div class="row1"><select id="ac" aria-label="작품 분류">${Object.entries(CATALOG).map(([k,c])=>`<option value="${k}">${c.name}</option>`).join('')}</select><input id="aw" maxlength="40" placeholder="작품 이름"><button class="text" type="submit">추가</button></div></form>
  <div class="field"><label for="gu">레터를 찾을 지역 (선택)</label><select id="gu"><option>서울 전체</option>${GU.map(g=>g.name).sort().map(n=>`<option ${S.gu===n?'selected':''}>${n}</option>`).join('')}</select></div>
  <div class="field"><label for="md">오늘의 기분, 하고 싶은 이야기 (선택)</label><input id="md" maxlength="120" placeholder="쓰지 않아도 돼요"></div>
  <div class="row1 end"><span class="small" id="cn"></span><button class="btn main" id="rd" type="button">레터 추천 받기</button></div></aside>`;
  $('md').value=S.mood;
  sketch(ui);
  document.querySelectorAll('[data-f]').forEach(b=>b.onclick=()=>setForm(b.dataset.f));
  $('af').onsubmit=e=>{
    e.preventDefault();
    const t=$('aw').value.trim();
    if(!t)return;
    $('aw').value='';
    const o=addWork($('ac').value,t,'comfort');
    formTiles();
    rateModal(o)
  };
  $('gu').onchange=()=>S.gu=$('gu').value;
  $('md').oninput=()=>S.mood=$('md').value;
  $('rd').onclick=()=>read();
  $('lt').onclick=listView;
  count()
}

function count(){
  const e=Object.values(S.ratings),n=e.length,ok=n>=NEED;
  $('cn').textContent=ok?`작품 ${n}개 평가`:`작품 ${n}/${NEED}개 평가`;
  $('rd').disabled=!ok;
  $('pk').innerHTML=e.map(r=>`<span>${esc(r.title)} <em>${stars(r.score)}</em></span>`).join('');
  requestAnimationFrame(()=>{
    const k=document.querySelector('.dock3');
    if(k)box(k,{fill:'#f5f4ef',seed:8})
  })
}

function listView(){
  const d=document.createElement('div');
  d.id='rl';
  d.className='modal';
  d.innerHTML=`<section class="sheet wide2" data-sk="#fbfaf6"><h1>작품 목록</h1>${Object.entries(CATALOG).map(([k,c])=>`<div class="sec"><b>${c.name}</b><div class="chips">${tiles.filter(t=>t.cat===k).map(t=>`<button class="text" type="button" data-w="${t.k}">${esc(t.title)}${S.ratings[contentKey(t.cat,t.title)]?' '+stars(S.ratings[contentKey(t.cat,t.title)].score):''}</button>`).join('')}</div></div>`).join('')}<div><button class="btn" data-sk id="cl" type="button">닫기</button></div></section>`;
  ui.append(d);
  sketch(d);
  $('cl').onclick=()=>d.remove();
  d.querySelectorAll('[data-w]').forEach(b=>b.onclick=()=>{
    d.remove();
    rateModal(tiles[Number(b.dataset.w)])
  })
}

function starSvg(on,i){
  const s=svg(42,42),rc=rough.svg(s),d='M21 4L26 16L39 17L29 26L32 38L21 32L10 38L13 26L3 17L16 16Z';
  s.append(rc.path(d,{roughness:1,stroke:INK,strokeWidth:1.2,seed:i+2}));
  if(on)s.append(rc.path(d,{roughness:.6,stroke:'none',fill:TINT,fillStyle:'zigzag',hachureGap:2.6,fillWeight:1.3,seed:i+2}));
  return s
}

function rateModal(c){
  if(S.zone!=='rate')return;
  const old=$('rm');
  if(old)old.remove();
  let v=S.ratings[contentKey(c.cat,c.title)]?.score||0;
  const d=document.createElement('div');
  d.id='rm';
  d.className='modal';
  d.innerHTML=`<section class="sheet" data-sk="#fbfaf6"><p class="small">${CATALOG[c.cat].name}</p><h1>${esc(c.title)}</h1><div class="rate" role="group" aria-label="별점">${[1,2,3,4,5].map(n=>`<div class="star"><button class="one" type="button" data-v="${n}" aria-label="${n}점"></button></div>`).join('')}</div><p class="small" id="sv"></p><div class="row"><button class="btn main" id="ok" type="button">별점 남기기</button><button class="text" id="sk" type="button">${S.ratings[contentKey(c.cat,c.title)]?'평가 지우기':'닫기'}</button></div></section>`;
  ui.append(d);
  sketch(d);
  const draw=()=>{
    d.querySelectorAll('.star').forEach((e,i)=>{
      e.querySelector('svg')?.remove();
      e.prepend(starSvg(i<v,i))
    });
    $('sv').textContent=v?`${v}점`:'별을 눌러주세요';
    $('ok').disabled=!v
  };
  d.querySelectorAll('[data-v]').forEach(b=>b.onclick=()=>{
    v=+b.dataset.v;
    draw()
  });
  const done=()=>{
    paper(c);
    count();
    d.remove();
    const n=Object.keys(S.ratings).length;
    setForm(['helix','orb','cloud'][(n+2)%3])
  };
  $('ok').onclick=()=>{
    S.ratings[contentKey(c.cat,c.title)]={title:c.title,score:v,cat:c.cat,tag:c.tag};
    done()
  };
  $('sk').onclick=()=>{
    if(S.ratings[contentKey(c.cat,c.title)]){
      delete S.ratings[contentKey(c.cat,c.title)];
      done()
    } else d.remove()
  };
  d.addEventListener('click',e=>{
    if(e.target===d)d.remove()
  });
  draw()
}

/* ---------- AI ---------- */

function showError(message){
  let node=$('server-error');
  if(!node){
    node=document.createElement('p');
    node.id='server-error';
    node.className='warn';
    node.setAttribute('role','alert');
    (document.querySelector('.dock3')||ui).append(node)
  }
  node.textContent=message
}

async function loadRecommendations(active){
  const result=await API.recommend(S.preferenceId);
  if(!active())return;
  S.stack=result.letters.map(item=>({i:upsertLetter(item.letter),why:item.why}));
  pile()
}

async function read(){
  const b=$('rd');
  if(b.disabled)return;
  const active=currentScreen();
  b.disabled=true;
  b.textContent='레터를 찾는 중';
  setForm('helix');
  S.mood=$('md').value.trim();
  try{
    const saved=await API.preference({ratings:Object.values(S.ratings).map(r=>({title:r.title,cat:r.cat,score:r.score})),mood:S.mood,gu:S.gu});
    if(!active())return;
    S.preferenceId=saved.preferenceId;
    await loadRecommendations(active)
  } catch(error){
    if(active())showError(error.message)
  } finally{
    if(b.isConnected){
      b.disabled=false;
      b.textContent='레터 추천 받기'
    }
  }
}

async function recommendAgain(){
  if(!S.preferenceId)return rate();
  enterScreen('loading');
  ui.innerHTML='<div class="center"><p role="status">새 레터 3통을 찾고 있어요.</p></div>';
  const active=currentScreen();
  try{
    await loadRecommendations(active)
  } catch(error){
    if(!active())return;
    ui.innerHTML=`<div class="center"><section class="sheet"><p class="warn" role="alert">${esc(error.message)}</p><button class="btn main" id="new-taste" type="button">취향 입력으로 돌아가기</button></section></div>`;
    $('new-taste').onclick=()=>rate()
  }
}

/* ---------- 레터 고르기 ---------- */

function pile(startIndex=0){
  enterScreen('pile');
  $('space').hidden=false;
  S.pos=startIndex;
  S.stack.forEach((s,i)=>{
    const l=LETTERS[s.i],el=document.createElement('button');
    el.type='button';
    el.className='c big';
    el.innerHTML=`<small>서대문구 어딘가</small><span class="hintb"><b>${YEOUN[l.tags[0]]}</b>${l.taste[0]?`<small>함께한 작품 〈${esc(l.taste[0])}〉</small>`:''}</span>`;
    el.setAttribute('aria-label',`레터 ${i+1}`);
    el.onclick=()=>{
      if(Math.round(S.pos)===i)choose(s);
      else{
        S.pos=i;
        place2()
      }
    };
    stage.append(el);
    s.el=el;
    box(el,{fill:i%2?'#fbfaf6':WASH,seed:i+4})
  });
  ui.innerHTML=`${steps('get',1)}<div class="under"><div style="max-width:440px"><p class="dots" id="ds" aria-hidden="true"></p><p class="small" id="n"></p><p id="w"></p></div><div class="row"><button class="btn" data-sk id="pv" type="button" aria-label="이전 레터">←</button><button class="btn" data-sk id="nx" type="button" aria-label="다음 레터">→</button><button class="btn main" id="op" type="button">이 레터 찾아가기</button></div></div>`;
  sketch(ui);
  $('pv').onclick=()=>move(-1);
  $('nx').onclick=()=>move(1);
  $('op').onclick=()=>choose(S.stack[Math.round(S.pos)]);
  place2()
}

function move(d){
  S.pos=Math.max(0,Math.min(S.stack.length-1,Math.round(S.pos)+d));
  place2()
}

function place2(){
  const n=innerWidth<=820,dx=n?44:116,dy=n?50:62,dz=n?140:200;
  S.stack.forEach((s,i)=>{
    const k=i-S.pos,g=k<-.5;
    s.el.style.opacity=g?0:Math.max(.1,1-k*.2);
    s.el.style.pointerEvents=g?'none':'auto';
    s.el.tabIndex=Math.round(k)===0?0:-1;
    s.el.style.transform=`translate3d(${k*dx-(n?10:60)+(g?-260:0)}px,${-k*dy+(n?-40:0)+(g?220:0)}px,${-k*dz+(g?260:0)}px) rotateY(-22deg) rotateX(7deg) rotateZ(2deg)`
  });
  const s=S.stack[Math.round(S.pos)],cur=Math.round(S.pos);
  $('ds').textContent=S.stack.map((_,i)=>i===cur?'●':'○').join(' ');
  $('n').textContent=`${S.stack.length}통 중 ${cur+1}번째 · AI 추천 이유`;
  $('w').textContent=s.why;
  $('pv').disabled=cur===0;
  $('nx').disabled=cur===S.stack.length-1
}
let lock=0;
addEventListener('wheel',e=>{
  if(S.zone!=='pile'||Date.now()<lock)return;
  lock=Date.now()+320;
  move(e.deltaY>0?1:-1)
},{passive:true});
let ty=null;
addEventListener('touchstart',e=>{
  ty=e.touches[0].clientY
},{passive:true});
addEventListener('touchend',e=>{
  if(S.zone!=='pile'||ty===null)return;
  const d=ty-e.changedTouches[0].clientY;
  if(Math.abs(d)>40)move(d>0?1:-1);
  ty=null
},{passive:true});
addEventListener('keydown',e=>{
  if(S.zone==='pile'){
    if(e.key==='ArrowDown'||e.key==='ArrowRight')move(1);
    if(e.key==='ArrowUp'||e.key==='ArrowLeft')move(-1)
  }
});

function choose(s){
  S.letter=s;
  S.atSite=false;
  seoul(LETTERS[s.i].place,true)
}

/* ---------- 우체통 지도 ---------- */
const wx=(lon,lat)=>[(lon-126.75)*4500,(37.72-lat)*5675];
const FULL=[0,0,1990,1720];
const HAN=[[126.79,37.60],[126.86,37.565],[126.93,37.535],[126.99,37.515],[127.06,37.522],[127.11,37.545],[127.18,37.572]];

function bboxOf(ring,pad){
  const p=ring.map(q=>wx(...q)),xs=p.map(q=>q[0]),ys=p.map(q=>q[1]);
  return [Math.min(...xs)-pad,Math.min(...ys)-pad,Math.max(...xs)-Math.min(...xs)+pad*2,Math.max(...ys)-Math.min(...ys)+pad*2]
}
const SDM=GU.find(g=>g.name==='서대문구');

function fit(b,el){
  const r=el.getBoundingClientRect(),ar=(r.width||1)/(r.height||1);
  let[x,y,w,h]=b;
  if(w/h<ar){
    const W=h*ar;
    x-=(W-w)/2;
    w=W
  } else{
    const H=w/ar;
    y-=(H-h)/2;
    h=H
  }
  return[x,y,w,h]
}
let vb=null,raf=0;

function view(to,ms){
  const s=$('map');
  if(!s)return;
  to=fit(to,$('mb'));
  cancelAnimationFrame(raf);
  if(!vb||reduced||!ms){
    vb=to;
    s.setAttribute('viewBox',to.join(' '));
    return
  }
  const from=vb,t0=performance.now(),active=currentScreen();
  const step=t=>{
    if(!active())return;
    const k=Math.min(1,(t-t0)/ms),e=k<.5?2*k*k:1-Math.pow(-2*k+2,2)/2;
    vb=from.map((v,i)=>v+(to[i]-v)*e);
    s.setAttribute('viewBox',vb.join(' '));
    if(k<1)raf=requestAnimationFrame(step)
  };
  raf=requestAnimationFrame(step)
}

function drawMap(){
  const s=document.createElementNS(NS,'svg');
  s.id='map';
  s.setAttribute('role','img');
  s.setAttribute('aria-label','서울 25개 구 손그림 지도');
  const rc=rough.svg(s);
  const T=(x,y,t,c,fs,cls)=>{
    const e=document.createElementNS(NS,'text');
    e.setAttribute('x',x);
    e.setAttribute('y',y);
    e.setAttribute('font-size',fs);
    e.setAttribute('fill',c||INK);
    if(cls)e.setAttribute('class',cls);
    e.textContent=t;
    return e
  };
  GU.forEach((g,i)=>{
    const on=g===SDM,p=g.ring.map(q=>wx(...q)),el=document.createElementNS(NS,'g');
    el.setAttribute('class','gu');
    el.setAttribute('tabindex','0');
    el.setAttribute('role','button');
    el.setAttribute('aria-label',on?'서대문구 보기':`${g.name} 레터 없음`);
    el.append(rc.polygon(p,{stroke:INK,strokeWidth:2.2,roughness:1.6,bowing:1.2,seed:i+3,fill:on?WASH:'#fbfaf6',fillStyle:'solid'}));
    const cx=p.reduce((a,q)=>a+q[0],0)/p.length,cy=p.reduce((a,q)=>a+q[1],0)/p.length,t=T(cx,cy,g.name,on?INK:'#74747a',on?42:34,'gl');
    t.setAttribute('text-anchor','middle');
    if(on)t.setAttribute('font-weight',600);
    el.append(t);
    el.onclick=()=>pickGu(g);
    el.onkeydown=e=>{
      if(e.key==='Enter')pickGu(g)
    };
    s.append(el)
  });
  s.append(rc.curve(HAN.map(q=>wx(...q)),{stroke:TINT,strokeWidth:9,roughness:1,seed:7}));
  const near=document.createElementNS(NS,'g');
  near.setAttribute('class','near');
  const rec=[...new Set(S.stack.map(x=>LETTERS[x.i].place))];
  Object.keys(PLACES).forEach((k,i)=>{
    const P=PLACES[k],[x,y]=wx(P.lon,P.lat),g=document.createElementNS(NS,'g'),on=k===S.place,r=rec.includes(k);
    g.setAttribute('opacity',r?1:.3);
    g.append(rc.circle(x,y,on?13:r?10:7,{stroke:INK,strokeWidth:1.3,fill:on?TINT:r?'#fff':'#fbfaf6',fillStyle:'solid',roughness:1,seed:i+50}));
    if(r){
      const lb={falls:[11,3,0],ansan:[-10,4,1],prison:[-2,-9,0],gate:[11,-2,0],market:[-10,7,1],film:[12,3,0]}[k]||[11,3,0],t=T(x+lb[0],y+lb[1],on?P.name:'우체통',on?TINT:INK,on?10:8.5);
      if(lb[2])t.setAttribute('text-anchor','end');
      if(on)t.setAttribute('font-weight',600);
      g.append(t);
      g.setAttribute('class','spot');
      g.setAttribute('tabindex','0');
      g.setAttribute('role','button');
      g.setAttribute('aria-label',`추천 우체통 ${on?P.name:''}`);
      const pick=()=>{
        const s2=S.stack.find(x=>LETTERS[x.i].place===k);
        if(s2&&k!==S.place){
          S.letter=s2;
          S.atSite=false;
          seoul(k)
        }
      };
      g.onclick=pick;
      g.onkeydown=e=>{
        if(e.key==='Enter')pick()
      }
    }
    near.append(g)
  });
  s.append(near);
  return s
}

function pickGu(g){
  if(g===SDM){
    $('mb').classList.remove('overall');
    view(bboxOf(SDM.ring,14),800);
    return
  }
  const t=$('toast');
  t.hidden=false;
  t.textContent=`${g.name}에는 아직 레터가 없어요.`;
  clearTimeout(pickGu.t);
  pickGu.t=screenDelay(()=>t.hidden=true,2400)
}

function seoul(place,reveal){
  enterScreen('seoul');
  S.place=place;
  vb=null;
  ui.innerHTML=`<div class="seoul"><div class="mapbox" id="mb"><button class="chip" id="all" type="button">서울 전체 보기</button><p class="toast" id="toast" hidden></p><div class="legend"><span><i class="k on"></i>고른 레터의 우체통</span><span><i class="k rec"></i>다른 추천 우체통</span><span><i class="k off"></i>그 외 우체통 (선택 불가)</span></div></div><aside class="side" id="sd"></aside></div>${steps('get',2)}`;
  $('mb').prepend(drawMap());
  $('all').onclick=()=>{
    $('mb').classList.add('overall');
    view(FULL,800)
  };
  const z=bboxOf(SDM.ring,14);
  if(reveal){
    $('mb').classList.add('overall');
    view(FULL,0);
    screenDelay(()=>{
      $('mb').classList.remove('overall');
      view(z,1400)
    },reduced?0:650)
  } else view(z,0);
  side()
}
const dist=(a,b,c,d)=>{
  const E=6371000,r=x=>x*Math.PI/180,x=r(c-a),y=r(d-b),h=Math.sin(x/2)**2+Math.cos(r(a))*Math.cos(r(c))*Math.sin(y/2)**2;
  return 2*E*Math.asin(Math.sqrt(h))
};

function side(go){
  const k=S.place,P=PLACES[k],d=$('sd');
  const gate=go?`<div class="gate" data-sk="#dfe4f3"><b>${esc(P.name)}에 도착하면 열려요</b><p class="small">근처 200m 안에서 열 수 있어요. 현장에서 열면 글쓴이의 추신을 볼 수 있어요. 위치는 확인할 때만 쓰고 저장하지 않아요.</p><p class="warn" id="gm" hidden></p><button class="btn main" id="loc" type="button">도착했어요, 열기</button><button class="text" id="demo" type="button">시연: 도착한 것으로 열기</button><button class="text" id="bk" type="button">돌아가기</button></div>`:`<div class="gate" data-sk="#dfe4f3"><b>이 우체통에 고른 레터가 있어요</b><p class="small">어떻게 읽을지 골라주세요.</p><div class="row"><button class="btn main" id="now" type="button">지금 바로 읽기</button><button class="btn" data-sk id="site" type="button">장소에 가서 열기</button></div></div>`;
  d.innerHTML=`<p class="small">서울 서대문구</p><h1>${esc(P.name)}</h1>${gate}<div class="sec"><b>왜 어울리나요</b><p>${esc(S.letter?S.letter.why:PLACE_MOOD[k])}</p><p class="small">실제 촬영지 여부는 확인하지 않은 분위기 추천이에요.</p></div><div class="sec"><b>이곳에서 할 수 있는 것</b>${(ACTS[k]||['주변을 방해하지 않는 곳에서 오늘의 마음을 한 줄 적기']).map(a=>`<div class="item"><span>${a}</span></div>`).join('')}<p class="small">운영 시간과 일정은 방문 전에 확인해주세요.</p></div><div class="sec"><b>위치</b><p>${esc(P.info)}</p><a class="small" href="${esc(P.src)}" target="_blank" rel="noopener">출처</a></div><div><button class="text" id="nx" type="button">다른 레터 고르기</button></div>`;
  sketch(d);
  d.scrollTop=0;
  if(document.fonts)document.fonts.ready.then(()=>{
    if(d.isConnected)sketch(d)
  });
  $('nx').onclick=()=>{
    S.letter=null;
    pile()
  };
  const on=(id,f)=>{
    const e=$(id);
    if(e)e.onclick=f
  };
  on('now',()=>letter(S.letter));
  on('site',()=>side(true));
  on('bk',()=>side());
  on('demo',()=>{
    S.atSite=true;
    letter(S.letter)
  });
  on('loc',()=>{
    const message=$('gm'),screenActive=currentScreen(),active=()=>screenActive()&&message.isConnected;
    const say=t=>{
      if(!active())return;
      message.hidden=false;
      message.textContent=t
    };
    if(!navigator.geolocation)return say('이 브라우저에서는 위치를 확인할 수 없어요.');
    say('위치를 확인하고 있어요.');
    navigator.geolocation.getCurrentPosition(p=>{
      if(!active())return;
      const m=dist(p.coords.latitude,p.coords.longitude,P.lat,P.lon);
      if(m<=200){
        S.atSite=true;
        letter(S.letter)
      } else say(`아직 약 ${m>=1000?(m/1000).toFixed(1)+'km':Math.round(m)+'m'} 떨어져 있어요.`)
    },()=>say('위치를 확인하지 못했어요. 위치 권한을 허용해주세요.'),{enableHighAccuracy:true,timeout:8000})
  })
}

function resizeMap(){
  const mb=$('mb');
  if(mb)view(mb.classList.contains('overall')?FULL:bboxOf(SDM.ring,14),0)
}

/* ---------- 읽기와 따라 하기 ---------- */

function letter(s){
  enterScreen('letter');
  const l=LETTERS[s.i],P=PLACES[l.place];
  if(!S.seen.includes(s.i))S.seen.push(s.i);
  const ps=S.atSite&&l.sample&&PS[s.i%PS.length];
  ui.innerHTML=`<div class="scroll"><div class="col">${steps('get',3)}<span class="small">${esc(P.name)}의 우체통 · 이름 모를 사람${l.lang?` · ${l.lang}에서 번역`:''}${l.sample?' · 예시 레터':''}</span>
  ${l.spoiler?`<p class="warn">스포일러가 있을 수 있어요. <button class="text" id="sp" type="button">그래도 읽기</button></p>`:''}<p class="pen ${l.spoiler?'blur':''}" id="bd">${esc(l.first)} ${esc(l.body)}</p>${ps?`<div class="gate" data-sk="#dfe4f3"><b>현장 읽기 예시 추신</b><p class="pen">${esc(ps)}</p></div>`:''}
  <div class="sec"><b>함께한 콘텐츠</b><div class="chips">${l.contents.map(c=>`<span class="${S.ratings[contentKey(c.type,c.title)]?'same':''}">${esc(c.title)}</span>`).join('')}</div></div><div class="sec"><b>장소</b><p>${esc(P.name)}</p></div>
  <div class="next"><div class="row"><button class="btn main" id="fw" type="button">따라 해보기</button><button class="text" id="nx" type="button">다른 레터 받기</button></div></div></div></div>`;
  sketch(ui);
  const sp=$('sp');
  if(sp)sp.onclick=()=>{
    $('bd').classList.remove('blur');
    sp.parentElement.remove()
  };
  $('fw').onclick=()=>follow(s);
  $('nx').onclick=()=>next()
}

function follow(s){
  enterScreen('follow');
  const l=LETTERS[s.i],P=PLACES[l.place],todo=l.routine||ACTS[l.place]?.[0]||'주변을 방해하지 않는 곳에서 잠시 쉬며 오늘의 마음을 한 줄 적어보세요.';
  ui.innerHTML=`<div class="scroll"><div class="col">${steps('get',4)}<h1>이 사람처럼 해볼까요?</h1><div class="gate" data-sk="#fbfaf6"><p class="pen">${esc(todo)}</p><p class="small">${esc(P.name)}에서. 그대로 해도 되고, 내 방식대로 해도 돼요.</p></div><div class="row"><button class="btn main" id="dn" type="button">해봤어요, 레터 남기기</button><button class="text" id="nx" type="button">다른 레터 받기</button></div></div></div>`;
  sketch(ui);
  $('dn').onclick=()=>write({place:l.place,work:l.taste[0]||'',cat:l.contents[0]?.type||'music',sourceLetterId:l.id});
  $('nx').onclick=()=>next()
}

function next(){
  S.letter=null;
  const unread=S.stack.findIndex(x=>!S.seen.includes(x.i));
  if(unread>=0)pile(unread);
  else recommendAgain()
}

/* ---------- 레터 남기기 ---------- */
const CONTACT_INPUT=/(https?:\/\/|www\.|[\w.+-]+@[\w.-]+\.[a-z]{2,}|(?:\+82[- .]?0?|0)(?:1[016789]|2|[3-6][1-5]|70)[- .]?\d{3,4}[- .]?\d{4}|\d{6}[- ]?[1-4]\d{6})/i;

function write(pre={}){
  enterScreen('write');
  const places=Object.entries(PLACES);
  ui.innerHTML=`<div class="scroll"><form class="col" id="wf">${steps('put',0)}<h1>어떤 마음이 남았나요?</h1><div class="sec"><label class="small" for="wk">함께한 콘텐츠 (선택)</label><select id="wc" aria-label="콘텐츠 종류">${Object.entries(CATALOG).map(([id,c])=>`<option value="${id}" ${id===(pre.cat||'music')?'selected':''}>${c.name}</option>`).join('')}</select><input class="line" id="wk" maxlength="120" placeholder="방금 본 영화, 듣던 노래"></div>
  <div class="sec"><label class="small" for="tx">이름 없이 남길 레터</label><textarea id="tx" data-sk maxlength="400" required style="min-height:170px" placeholder="다 보고 나니까 오래 연락 못 한 사람이 생각났어요.">${esc(pre.text||'')}</textarea></div>
  <fieldset class="sec pick"><legend class="small">어느 우체통에 맡길까요</legend><label><input type="radio" name="how" value="ai" ${pre.place?'':'checked'}> AI에게 맡기기</label><label><input type="radio" name="how" value="me" ${pre.place?'checked':''}> 직접 고르기</label><select id="wp" ${pre.place?'':'hidden'} aria-label="장소 선택">${places.map(([k,p])=>`<option value="${k}" ${k===pre.place?'selected':''}>${esc(p.name)}</option>`).join('')}</select><p class="small">집이나 직장이 드러나지 않도록 공공장소만 고를 수 있어요.</p></fieldset>
  <p class="warn" id="wm" hidden></p><div class="row"><button class="btn main" id="ck" type="submit">AI 확인 받기</button><button class="text" id="bk" type="button">처음으로</button></div></form></div>`;
  $('wk').value=pre.work||'';
  sketch(ui);
  $('bk').onclick=()=>intro();
  document.querySelectorAll('[name=how]').forEach(r=>r.onchange=()=>{
    $('wp').hidden=r.value!=='me'||!r.checked
  });
  $('wf').onsubmit=async e=>{
    e.preventDefault();
    const t=$('tx').value.trim(),w=$('wk').value.trim(),m=$('wm'),me=document.querySelector('[name=how]:checked').value==='me';
    if(!t)return;
    if(CONTACT_INPUT.test(t)||CONTACT_INPUT.test(w)){
      m.hidden=false;
      m.textContent='전화번호·이메일·연락처·개인정보를 지우고 다시 확인해주세요.';
      return;
    }
    const b=$('ck');
    if(b.disabled)return;
    const chosen=me?$('wp').value:null,cat=$('wc').value,active=currentScreen();
    b.disabled=true;
    b.textContent='읽는 중';
    try{
      const result=await API.review({work:w,cat,text:t,placeId:chosen,sourceLetterId:pre.sourceLetterId||null});
      if(!active())return;
      review(result,chosen,{...pre,work:w,text:t,cat,place:chosen})
    } catch(error){
      if(active()){
        m.hidden=false;
        m.textContent=error.message
      }
    } finally{
      if(b.isConnected){
        b.disabled=false;
        b.textContent='AI 확인 받기'
      }
    }
  }
}

function showSavedLetter(saved,corrected){
  upsertLetter(saved);
  S.stack=[];
  enterScreen('saved');
  ui.innerHTML=`<div class="center ${corrected?'review-result':''}"><section class="sheet" data-sk="#fbfaf6"><p class="note">✓ 맡기기 완료</p><h1>${esc(PLACES[saved.placeId].name)}의 우체통에 레터를 맡겼어요</h1>${corrected?`<div class="gate" data-sk="#fbfaf6"><b>비속어를 순화해 저장한 레터</b><p class="pen">${esc(saved.text)}</p></div>`:''}<p class="small">레터가 저장되었어요. 다른 사람에게도 추천될 수 있어요.</p><div class="row"><button class="btn main" id="gt" type="button">레터 추천 받기</button><button class="text" id="ho" type="button">처음으로</button></div></section></div>`;
  sketch(ui);
  $('gt').onclick=()=>recommendAgain();
  $('ho').onclick=()=>intro();
}

function review(r,chosen,pre){
  if(r.block){
    const m=$('wm');
    m.hidden=false;
    m.textContent=r.note||'이 내용은 저장할 수 없어요. 표현을 바꿔 다시 작성해주세요.';
    return;
  }
  enterScreen('review');
  const opts=r.places,requestId=crypto.randomUUID();
  let saving=false;
  async function saveTo(placeId){
    if(saving)return;
    saving=true;
    const active=currentScreen(),buttons=[...ui.querySelectorAll('button')];
    buttons.forEach(button=>button.disabled=true);
    const message=$('save-error'),status=$('save-status'),retry=$('retry-save');
    if(message)message.hidden=true;
    if(status)status.textContent='수정된 문장으로 저장하고 있어요.';
    try{
      const saved=await API.save({requestId,reviewId:r.reviewId,placeId});
      if(!active())return;
      showSavedLetter(saved,r.corrected);
    }catch(error){
      if(!active())return;
      if(message){message.hidden=false;message.textContent=error.message;}
      if(status)status.textContent='저장하지 못했어요. 아래 버튼으로 다시 시도해주세요.';
      if(retry)retry.hidden=false;
    }finally{
      saving=false;
      buttons.forEach(button=>{if(button.isConnected)button.disabled=false;});
    }
  }
  if(r.corrected){
    const placeId=chosen||opts[0].id;
    ui.innerHTML=`${steps('put',2)}<div class="center review-result"><section class="sheet" data-sk="#fbfaf6"><h1>비속어를 순화했어요</h1><div class="gate" data-sk="#fbfaf6"><p class="pen">${esc(r.text)}</p></div><p class="small">${esc(PLACES[placeId].name)}의 우체통에 맡겨요.</p><p class="small" id="save-status" role="status">수정된 문장으로 저장하고 있어요.</p><p class="warn" id="save-error" role="alert" hidden></p><button class="btn main" id="retry-save" type="button" hidden>저장 다시 시도</button></section></div>`;
    sketch(ui);
    $('retry-save').onclick=()=>saveTo(placeId);
    void saveTo(placeId);
    return;
  }
  ui.innerHTML=`${steps('put',2)}<div class="center review-result"><section class="sheet" data-sk="#fbfaf6"><p class="small">AI 확인 결과</p><ul class="checks"><li class="ok">✓ 레터 검수를 통과했어요</li><li class="${r.spoiler?'bad':'ok'}">${r.spoiler?'! 스포일러가 있어 가려서 맡겨요':'✓ 스포일러를 찾지 못했어요'}</li></ul><p class="small">${esc(r.note)}</p><div class="gate" data-sk="#fbfaf6"><b>저장할 레터</b><p class="pen">${esc(r.text)}</p></div><h1>${chosen?'이 우체통에 맡길까요?':'어디에 맡길까요?'}</h1>${opts.map((p,i)=>`<button class="acct" data-sk type="button" data-p="${i}"><span>${esc(PLACES[p.id].name)}<br><span class="small">${esc(p.why)}</span></span></button>`).join('')}<button class="text" id="bk" type="button">다시 쓰기</button><p class="warn" id="save-error" role="alert" hidden></p></section></div>`;
  sketch(ui);
  $('bk').onclick=()=>write({...pre,text:r.text});
  ui.querySelectorAll('[data-p]').forEach(button=>button.onclick=()=>saveTo(opts[+button.dataset.p].id));
}
$('mark').onclick=()=>{
  if(S.ready)intro()
};
addEventListener('resize',()=>{
  sizeCloud();
  if(S.zone==='pile')place2();
  if(S.zone==='seoul')resizeMap()
});

async function start(){
  enterScreen('loading');
  const active=currentScreen();
  ui.innerHTML='<div class="center"><p role="status">서울의 우체통을 준비하고 있어요.</p></div>';
  try{
    const data=await API.bootstrap();
    if(!active())return;
    S.ready=true;
    S.temporaryContents=data.temporaryContents;
    Object.values(CATALOG).forEach(category=>category.items.length=0);
    data.contents.forEach(item=>CATALOG[item.type].items.push([item.title,item.tag]));
    Object.keys(PLACES).forEach(key=>delete PLACES[key]);
    data.places.forEach(place=>{
      PLACES[place.id]=place;
      PLACE_MOOD[place.id]=place.mood
    });
    LETTERS.length=0;
    data.letters.forEach(upsertLetter);
    intro()
  } catch(error){
    if(!active())return;
    ui.innerHTML=`<div class="center"><section class="sheet"><p class="warn" role="alert">${esc(error.message)}</p><button class="btn main" id="retry-bootstrap" type="button">다시 연결하기</button></section></div>`;
    $('retry-bootstrap').onclick=start
  }
}
// Bootstrap after all screen functions are defined.
sizeCloud();
start();
requestAnimationFrame(drawCloud);
