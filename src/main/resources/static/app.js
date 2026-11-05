import { MUSIC_ARTISTS } from './music-artists.js';
import { API } from './api.js';
import { GU } from './map-data.js';
const CATALOG={music:{name:'음악',items:[]},film:{name:'영화',items:[]},drama:{name:'드라마',items:[]}};
const PLACES={};
const LETTERS=[];
const PLACE_MOOD={};

function contentKey(cat,title){
  return JSON.stringify([cat,title.trim().toLowerCase()]);
}
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
/* Dear, Seoul UI. API data and persistence are provided by the same-origin server. */
gsap.registerPlugin(DrawSVGPlugin);
const M=window.MOTION,DEFAULTS={...M};
try{Object.assign(M,JSON.parse(localStorage.getItem('dear-motion')||'{}'))}catch(e){}
const $=id=>document.getElementById(id);
function esc(value) {
  return String(value ?? '').replace(/[&<>"']/g, char => ({
  '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
  })[char]);
}

const REDUCED=matchMedia('(prefers-reduced-motion: reduce)').matches;
const applySpeed=()=>gsap.globalTimeline.timeScale(REDUCED?30:M.speed);applySpeed();
const NARROW=()=>innerWidth<=820,A=f=>(window.ASSET&&ASSET[f])||'assets/'+f;
const S={ratings:Object.create(null),stack:[],seen:[],letter:null,place:null,opened:false};
const ACTS={film:['오늘 상영작 중 모르는 영화 한 편 보기','나오는 길에 기억나는 대사 적기'],ansan:['숲길을 이어폰 없이 걷기','메타세쿼이아 숲에서 잠깐 앉아 있기'],falls:['물가에 앉아 물소리 듣기','물길을 따라 안산 쪽으로 걷기'],prison:['역사관 전시를 천천히 보기','오래 남은 이름 하나 기억하기'],gate:['독립공원 한 바퀴 걷기','벤치에서 지나가는 사람 보기'],market:['시장 음식 하나 맛보기','끝까지 천천히 걸어보기']};
const MIND={comfort:'말없이 곁에 있어주길 바라는 마음',rest:'잠깐 쉬어가고 싶은 마음',miss:'지나간 시간을 돌아보는 마음',youth:'서툴지만 시작하고 싶은 마음',courage:'조금 더 버텨보려는 마음'};
/* Figma [Process1] 편지 선택의 종이, 크기(1728 기준 폭), 중심 위치, 회전 */
const PAPERS=[['paper-lace.png',.642,356,480,627,-6.33],['paper-stamp.png',.69,346,798,518,.15],['paper-crumple.jpg',.667,290,1157,476,3.08],['paper-stamp.png',.69,0,0,0,-2],['paper-lace.png',.642,0,0,0,2]];
let idle=[],replay=()=>{};
const stopIdle=()=>{idle.forEach(t=>t.kill());idle=[]};
const STEPS={read:['① 취향 선택','② 편지 고르기','③ 위치 확인하기','④ 읽기'],write:['① 편지지 선택','② 편지 쓰기','③ 편지 보내기']};
function setSteps(k){if(S.steps===k)return;S.steps=k;$('steps').innerHTML=STEPS[k].map(x=>`<li>${x}</li>`).join('')}
function scene(id,step){setSteps(['s-paper','s-compose','s-send','s-done'].includes(id)?'write':'read');document.querySelectorAll('.scene').forEach(s=>s.hidden=s.id!==id);$('top').hidden=id==='s-start';[...$('steps').children].forEach((l,i)=>l.classList.toggle('on',i===step))}

/* ---------- 시작 ---------- */
function intro(skip){replay=()=>intro();$('lt').hidden=true;scene('s-start',-1);stopIdle();S.opened=false;S.started=false;
  gsap.set('#door',{rotationY:0});gsap.set('#folder',{autoAlpha:0,y:0,scale:1});gsap.set('#peek',{autoAlpha:0,y:0});$('folderHit').hidden=true;$('key').style.pointerEvents='';
  $('hKey').innerHTML='<b>열쇠 구멍</b>을 눌러 당신을 위한 편지를 받아보세요.';
  const ready=()=>{S.started=true};
  if(skip===true){$('begin').hidden=true;gsap.set('.word',{xPercent:0,yPercent:0,scale:1,y:0,autoAlpha:1});gsap.set('#mb,.hint',{y:0,autoAlpha:1});return ready()}
  /* 타이틀: 두 글자가 가운데 모여 있다가, 시작하면 벌어지고 그 사이로 우체통이 올라온다 */
  const n=NARROW();$('begin').hidden=false;gsap.set('#mb,.hint',{autoAlpha:0});
  gsap.set('.dear',{xPercent:n?-7:40,yPercent:n?190:0,scale:1.1});gsap.set('.seoul',{xPercent:n?6:-34,yPercent:n?190:0,scale:1.1});
  gsap.timeline({defaults:{ease:'power2.out'}}).fromTo('.word',{y:12,autoAlpha:0},{y:0,autoAlpha:1,duration:1,stagger:.2}).fromTo('#begin',{autoAlpha:0},{autoAlpha:1,duration:.5},'-=.3');
  $('begin').onclick=()=>gsap.timeline({onComplete:ready}).to('#begin',{autoAlpha:0,duration:.25}).add(()=>{$('begin').hidden=true}).to('.word',{xPercent:0,yPercent:0,scale:1,duration:M.titleMove,ease:'power3.inOut'}).fromTo('#mb',{y:innerHeight*.55,autoAlpha:0},{y:0,autoAlpha:1,duration:M.titleMove+.15,ease:'power3.out'},`<${M.titleMove*.2}`).fromTo('.hint',{autoAlpha:0},{autoAlpha:1,duration:.45},'-=.15')}
const px=[gsap.quickTo('.dear','x',{duration:.9,ease:'power3'}),gsap.quickTo('.seoul','x',{duration:.9,ease:'power3'})];
addEventListener('pointermove',e=>{if($('s-start').hidden||NARROW()||!S.started)return;const x=e.clientX/innerWidth-.5;px[0](x*-M.parallax);px[1](x*-M.parallax*1.2)});
$('key').onpointerenter=()=>{if(!S.opened)gsap.to('#door',{rotationY:-M.doorHover,duration:.4,ease:'power2.out'})};
$('key').onpointerleave=()=>{if(!S.opened)gsap.to('#door',{rotationY:0,duration:.4,ease:'power2.out'})};
$('slot').onpointerenter=()=>gsap.to('#peek',{autoAlpha:1,y:-70,duration:.4,ease:'power2.out'});
$('slot').onpointerleave=()=>gsap.to('#peek',{autoAlpha:0,y:0,duration:.3});
$('key').onclick=()=>{if(S.opened)return;S.opened=true;$('key').style.pointerEvents='none';stopIdle();
  gsap.timeline().to('#door',{rotationY:-M.doorOpen,duration:M.doorTime,ease:'power2.inOut'}).fromTo('#folder',{autoAlpha:0,y:50},{autoAlpha:1,y:0,duration:.55,ease:'power2.out'},'-=.3').add(()=>{$('folderHit').hidden=false;$('hKey').innerHTML='<b>편지</b>를 눌러 당신을 위한 마음을 받아보세요.'})};
$('folderHit').onclick=()=>gsap.to('#folder',{y:-90,autoAlpha:0,duration:.4,ease:'power2.in',onComplete:()=>openLetter(welcome())});
$('slot').onclick=()=>gsap.to('#peek',{y:-130,autoAlpha:0,duration:.35,ease:'power2.in',onComplete:()=>write()});

/* ---------- 편지 읽기 ---------- */
function welcome(){return{place:'서울',body:'안녕하세요.\n아직 서로를 모르는 당신에게 씁니다.\n\n서울 어딘가에 누군가 마음을 두고 갔어요.\n좋아하는 작품을 알려주시면,\n그 마음과 닮은 편지를 찾아드릴게요.',close:()=>intro(true),foot:'<button id="f1" type="button">이 마음 간직하기</button>',bind(){$('f1').onclick=()=>closeLetter(rate)}}}
let ltl=null;
function openLetter(d){replay=()=>openLetter(d);setSteps('read');document.querySelectorAll('.scene').forEach(s=>s.hidden=true);$('top').hidden=false;$('lt').hidden=false;$('lt').scrollTop=0;
  $('artB').hidden=false;$('sheet').querySelector('[data-spoiler]')?.remove();$('rdP').textContent=d.place;$('artB').innerHTML=d.body.split('\n\n').map(x=>`<p>${esc(x).replace(/\n/g,'<br>')}</p>`).join('');
  $('rdMore').hidden=!d.course;if(d.course){$('rdC').textContent=d.course;$('rdW').innerHTML=d.works.map(w=>`<div>${esc(w)}</div>`).join('')}
  $('ltFoot').innerHTML=d.foot;d.bind();
  if(ltl)ltl.kill();
  /* 종이가 위에서 아래로 펼쳐지고 글이 나타난다. 이게 움직임의 전부다 */
  ltl=gsap.timeline().fromTo('#lt',{autoAlpha:0},{autoAlpha:1,duration:.25}).fromTo('#sheet',{clipPath:'inset(-20px -20px 100% -20px)'},{clipPath:'inset(-20px -20px -20px -20px)',duration:M.letterTime,ease:'power2.inOut',clearProps:'clipPath'},'<').fromTo('#sheet>*',{autoAlpha:0},{autoAlpha:1,duration:.45,stagger:.08},`<${M.letterTime*.35}`);
  $('ltX').onclick=()=>closeLetter(d.close||intro)}
function closeLetter(next){gsap.to('#lt',{autoAlpha:0,duration:.25,onComplete(){$('lt').hidden=true;next&&next()}})}

/* ---------- 취향 선택: 넓은 캔버스 위의 작품 태그 30장 ---------- */
const WORLD={w:2560,h:1560},TG={cols:8,rows:4};
const V={k:1,x:0,y:0,mx:-9999,my:-9999,sx:0,sy:0,on:false,energy:0,paused:false};
let WTAGS=null,netCtx=null;
function makeTags(){const all=[];for(const k in CATALOG)for(const[title,tag,artist]of CATALOG[k].items)all.push({title,tag,artist,cat:k});
  TG.rows=Math.max(4,Math.ceil(all.length/TG.cols));
  const cells=gsap.utils.shuffle([...Array(TG.cols*TG.rows).keys()]),w=(WORLD.w-160)/TG.cols,h=(WORLD.h-140)/TG.rows;
  WTAGS=gsap.utils.shuffle(all).map((t,i)=>({...t,bx:80+(cells[i]%TG.cols+.5)*w+(Math.random()-.5)*w*.34,by:70+(Math.floor(cells[i]/TG.cols)+.5)*h+(Math.random()-.5)*h*.3,r:Math.random()*80-40,ph:Math.random()*6.28,ox:0,oy:0,tilt:0,hover:false,cx:0,cy:0}))}
function tagInner(t){const r=S.ratings[contentKey(t.cat,t.title)];return `<span class="tcopy${t.r<0?' flip':''}"><span class="tcat">${CATALOG[t.cat].name}</span><span class="ttitle">${esc(t.title)}</span>${t.cat==='music'&&t.artist?`<span class="tartist">${esc(t.artist)}</span>`:''}${r?`<span class="tstar">${'★'.repeat(r.score)}</span>`:''}</span>`}
function addTagEl(t){const s=document.createElement('div');s.className='tslot';s.innerHTML=`<button class="tag c-${t.cat}" type="button">${tagInner(t)}</button>`;$('tworld').append(s);t.el=s;t.btn=s.firstElementChild;t.btn.onpointerenter=()=>t.hover=true;t.btn.onpointerleave=()=>t.hover=false;t.btn.onclick=()=>openRate(t);return s}
function fitTaste(){const W=innerWidth,H=innerHeight,k=Math.min(W/1728,H/1117);if($('tstage'))$('tstage').style.setProperty('--k',NARROW()?1:k);if(NARROW())return;V.k=Math.max(k,.82);const c=$('tnet'),d=Math.min(devicePixelRatio||1,2);c.width=W*d;c.height=H*d;netCtx=c.getContext('2d');netCtx.setTransform(d,0,0,d,0,0);$('tworld').style.width=WORLD.w+'px';$('tworld').style.height=WORLD.h+'px';clampCam()}
function clampCam(){const W=innerWidth,H=innerHeight,top=70,bot=H-130;V.x=gsap.utils.clamp(Math.min(0,W-WORLD.w*V.k),0,V.x);V.y=gsap.utils.clamp(Math.min(top,bot-WORLD.h*V.k),top,V.y)}
addEventListener('resize',()=>{if(!$('s-taste').hidden)fitTaste()});
addEventListener('pointermove',e=>{V.sx=e.clientX;V.sy=e.clientY;V.on=true});
document.addEventListener('pointerleave',()=>V.on=false);
function tasteLoop(){if($('s-taste').hidden||NARROW()){gsap.ticker.remove(tasteLoop);return}
  const W=innerWidth,H=innerHeight,now=gsap.ticker.time;
  /* 가장자리에 가까울수록 캔버스가 천천히 움직인다. 가운데에서는 멈춰 있어서 누르기 쉽다 */
  if(V.on&&!V.paused&&!REDUCED&&Date.now()-lastWheel>700){const z=W*.14,zy=H*.15,top=70+zy,bot=H-130-zy;let vx=0,vy=0;if(V.sx<z)vx=1-V.sx/z;else if(V.sx>W-z)vx=-(1-(W-V.sx)/z);if(V.sy>70&&V.sy<top)vy=1-(V.sy-70)/zy;else if(V.sy>bot&&V.sy<H-130)vy=-(V.sy-bot)/zy;V.x+=vx*vx*Math.sign(vx)*M.edgeSpeed;V.y+=vy*vy*Math.sign(vy)*M.edgeSpeed;clampCam()}
  gsap.set('#tworld',{x:V.x,y:V.y,scale:V.k});
  const mx=(V.sx-V.x)/V.k,my=(V.sy-V.y)/V.k,act=V.on&&!V.paused;V.energy+=((act?1:0)-V.energy)*.08;
  const near=[];
  for(const t of WTAGS){const dx=t.bx-mx,dy=t.by-my,d=Math.hypot(dx,dy)||1,R=M.netRange;let p=0;
    if(act&&d<R){const f=1-d/R,g=gsap.utils.clamp(0,1,(d-70)/80);p=f*f*M.netPush*g}
    t.ox+=(dx/d*p-t.ox)*.09;t.oy+=(dy/d*p-t.oy)*.09;t.tilt+=((t.hover?M.tagTilt:0)-t.tilt)*.14;
    const fl=REDUCED?0:M.netFloat,x=t.bx+t.ox+Math.sin(now*.45+t.ph)*fl,y=t.by+t.oy+Math.cos(now*.37+t.ph)*fl,rot=t.r+t.tilt+t.ox*.05;
    t.el.style.transform=`translate3d(${x}px,${y}px,0) rotate(${rot}deg)`;t.cx=x*V.k+V.x;t.cy=y*V.k+V.y;if(d<R)near.push([t,1-d/R])}
  /* 파티클 네트워크: 마우스 주변의 태그끼리만 얇은 선으로 잇는다 */
  const g=netCtx;g.clearRect(0,0,W,H);if(M.netLine>0&&V.energy>.02){const L=280*V.k;g.lineWidth=1.2;
    for(let i=0;i<near.length;i++){const[a,fa]=near[i];
      g.strokeStyle=`rgba(162,89,105,${(fa*M.netLine*V.energy).toFixed(3)})`;g.beginPath();g.moveTo(V.sx,V.sy);g.lineTo(a.cx,a.cy);g.stroke();
      for(let j=i+1;j<near.length;j++){const[b,fb]=near[j],q=Math.hypot(a.cx-b.cx,a.cy-b.cy);if(q<L){g.strokeStyle=`rgba(162,89,105,${(Math.min(fa,fb)*(1-q/L)*M.netLine*V.energy).toFixed(3)})`;g.beginPath();g.moveTo(a.cx,a.cy);g.lineTo(b.cx,b.cy);g.stroke()}}}}}
function rate(){replay=rate;scene('s-taste',0);stopIdle();$('gu').innerHTML='<option>서울 전체</option>'+GU.map(g=>g.name).sort().map(n=>`<option>${n}</option>`).join('');
  if(!WTAGS)makeTags();$('tworld').innerHTML='';WTAGS.forEach(addTagEl);fitTaste();
  if(!NARROW()){V.x=(innerWidth-WORLD.w*V.k)/2;V.y=(innerHeight-WORLD.h*V.k)/2;clampCam();gsap.ticker.remove(tasteLoop);gsap.ticker.add(tasteLoop)}
  gsap.fromTo('.tag',{autoAlpha:0},{autoAlpha:1,duration:M.tagTime,ease:'power2.out',stagger:{amount:M.tagSpread,from:'random'}});gsap.fromTo('.thint,#dock',{autoAlpha:0},{autoAlpha:1,duration:.4});
  $('go').onclick=read;$('addBtn').onclick=()=>openRate(null);count()}
function count(){const n=Object.keys(S.ratings).length;$('count').textContent=`${Math.min(n,3)}/3`;$('go').disabled=n<3||n>50}
function openRate(t){const add=!t,d=$('detail');let cat=t?.cat||'music',v=S.ratings[contentKey(t?.cat,t?.title||'')]?.score||1;
  const cats=()=>{$('dImg').src=A(`tag-${cat}.png`);$('dCat').innerHTML=add?Object.entries(CATALOG).map(([k,c])=>`<button type="button" data-k="${k}" class="${k===cat?'on':''}">${c.name}</button>`).join(''):esc(CATALOG[cat].name);if(add)$('dCat').querySelectorAll('button').forEach(b=>b.onclick=()=>{cat=b.dataset.k;cats()})};cats();
  $('dName').innerHTML=add?'<input id="nw" maxlength="24" placeholder="작품 이름" aria-label="작품 이름">':esc(t.title)+(t.cat==='music'&&t.artist?`<small class="detail-artist">${esc(t.artist)}</small>`:'');
  $('dStars').innerHTML=[1,2,3,4,5].map(i=>`<button type="button" data-v="${i}" aria-label="${i}점"></button>`).join('');
  const nw=$('nw'),paint=()=>{$('dStars').querySelectorAll('button').forEach((b,i)=>b.textContent=i<v?'★':'☆');$('ok').disabled=add&&!nw.value.trim()};paint();if(nw)nw.oninput=paint;
  $('dStars').querySelectorAll('button').forEach(b=>b.onclick=()=>{v=+b.dataset.v;paint()});
  $('rm').hidden=add||!S.ratings[contentKey(t.cat,t.title)];if(t)t.el.style.visibility='hidden';$('tworld').classList.add('dim');V.paused=true;d.hidden=false;gsap.fromTo(d,{autoAlpha:0,scale:NARROW()?.72:.97},{autoAlpha:1,scale:NARROW()?.76:1,duration:.22,ease:'power2.out'});(nw||d).focus({preventScroll:true});
  const done=()=>{d.hidden=true;if(t){t.el.style.visibility='';t.btn.innerHTML=tagInner(t)}$('tworld').classList.remove('dim');V.paused=false;d.onkeydown=null;count()};
  d.onkeydown=e=>{if(e.key==='Escape')done()};$('cl').onclick=done;
  $('rm').onclick=()=>{delete S.ratings[contentKey(t.cat,t.title)];done()};
  $('ok').onclick=()=>{if(add){const title=nw.value.trim();let hit=WTAGS.find(x=>x.title===title&&x.cat===cat);if(hit)S.ratings[contentKey(cat,title)]={title,score:v,cat:hit.cat,tag:hit.tag};else{S.ratings[contentKey(cat,title)]={title,score:v,cat,tag:''};hit={title,cat,tag:'',bx:(innerWidth/2-V.x)/V.k+(Math.random()-.5)*260,by:(innerHeight/2-V.y)/V.k+(Math.random()-.5)*160,r:Math.random()*40-20,ph:Math.random()*6.28,ox:0,oy:0,tilt:0,hover:false,cx:0,cy:0};WTAGS.push(hit);addTagEl(hit)}hit.btn.innerHTML=tagInner(hit)}else S.ratings[contentKey(t.cat,t.title)]={title:t.title,score:v,cat,tag:t.tag};done()}}
async function read(){if(Object.keys(S.ratings).length<3||Object.keys(S.ratings).length>50)return;await busy(async active=>{const p=await API.preference({ratings:Object.values(S.ratings).map(({title,cat,score})=>({title,cat,score})),mood:$('mood').value,gu:$('gu').value});if(!active())return;S.preferenceId=p.preferenceId;const r=await API.recommend(p.preferenceId);if(!active())return;S.stack=r.letters.map(x=>({i:upsertLetter(x.letter),why:x.why}));pick()},'당신에게 어울리는 편지를 찾고 있어요.',true)}
async function recommendAgain(){if(!S.preferenceId)return rate();await busy(async active=>{const r=await API.recommend(S.preferenceId);if(!active())return;S.stack=r.letters.map(x=>({i:upsertLetter(x.letter),why:x.why}));pick()},'당신에게 어울리는 편지를 찾고 있어요.',true)}
/* ---------- 편지 고르기 ---------- */
function pick(){replay=pick;scene('s-pick',1);const P=$('papers');P.innerHTML='';const n=S.stack.length,narrow=NARROW(),H=innerHeight,W=innerWidth;
  const uw=new Set;S.stack.forEach((s,i)=>{const l=LETTERS[s.i],[img,ar,fw,fx,fy,fr]=PAPERS[i%PAPERS.length],x={kind:'',note:l.spoiler?'스포일러가 포함된 편지예요.':s.why},wk=l.taste.find(t=>!uw.has(t))||l.taste[0];uw.add(wk);
    const k=Math.min(W/1728,H/1117),w=n===3?fw*k:Math.min(W*.186,H*.62*ar),hh=w/ar,b=document.createElement('button');b.type='button';b.className='paper'+(narrow?' list':'');b.dataset.paper=img;b.style.width=w+'px';b.style.height=hh+'px';b.style.margin=`${-hh/2}px 0 0 ${-w/2}px`;
    b.innerHTML=`<img src="${A(img)}" alt=""><i class="tape"></i><span class="tx"><b>${esc(x.note)}</b><span>${wk?`〈${esc(wk)}〉와 함께`:''}</span></span>`;b.setAttribute('aria-label',`편지 ${i+1}: ${x.note}${wk?' · '+wk:''}`);b.title=x.note;P.append(b);s.el=b;
    const u=n>1?i/(n-1)-.5:0,tx=narrow?0:n===3?(fx/1728-.5)*W:u*W*.77,ty=narrow?0:n===3?(fy/1117-.5)*H:(i%2?18:-14)-H*.03,rot=narrow?0:fr;s.home={x:tx,y:ty,rotation:rot};
    b.onpointerenter=()=>{gsap.to(b,{y:ty-M.paperLift,zIndex:6,duration:.3,ease:'power2.out',overwrite:'auto'})};b.onpointerleave=()=>gsap.to(b,{y:ty,zIndex:1,duration:.4,ease:'power2.out',overwrite:'auto'});b.onclick=()=>choose(s)});
  gsap.fromTo(S.stack.map(s=>s.el),{x:i=>S.stack[i].home.x,y:i=>S.stack[i].home.y+28,rotation:i=>S.stack[i].home.rotation,autoAlpha:0},{y:i=>S.stack[i].home.y,autoAlpha:1,duration:M.paperTime,ease:'power2.out',stagger:.08});
  $('pickCap').textContent='누군가 남긴 글의 한 구절이에요. 마음이 머무는 글을 눌러 이어 읽어보세요.'}
function choose(s){S.letter=s;S.place=LETTERS[s.i].place;const rest=S.stack.filter(x=>x!==s).map(x=>x.el);gsap.timeline({onComplete:()=>mapScene(true)}).to(rest,{autoAlpha:0,duration:.3}).to(s.el,{x:0,y:0,rotation:0,duration:.5,ease:'power2.inOut',overwrite:true},'<').to(s.el,{autoAlpha:0,duration:.3},'+=.1')}

/* ---------- 서울 지도 ---------- */
const wx=(lon,lat)=>[(lon-126.75)*4500,(37.72-lat)*5675],SDM=GU.find(g=>g.name==='서대문구'),FULL='0 0 1990 1720',HAN=[[126.79,37.60],[126.86,37.565],[126.93,37.535],[126.99,37.515],[127.06,37.522],[127.11,37.545],[127.18,37.572]];
function boxOf(g,p){const q=g.ring.map(c=>wx(...c)),x=q.map(c=>c[0]),y=q.map(c=>c[1]),a=Math.min(...x)-p,b=Math.min(...y)-p;return `${a} ${b} ${Math.max(...x)-a+p} ${Math.max(...y)-b+p}`}
const ptsPath=p=>'M'+p.map(c=>c.join(' ')).join('L')+'Z',curve=p=>{let d=`M${p[0].join(' ')}`;for(let i=1;i<p.length;i++){const[a,b]=p[i-1],[c,e]=p[i];d+=`Q${(a+c)/2+(e-b)*.18} ${(b+e)/2-(c-a)*.18} ${c} ${e}`}return d};
function drawSvg(){const rec=[...new Set(S.stack.map(x=>LETTERS[x.i].place))];const gu=GU.map(g=>{const on=g===SDM,p=g.ring.map(c=>wx(...c)),cx=p.reduce((a,c)=>a+c[0],0)/p.length,cy=p.reduce((a,c)=>a+c[1],0)/p.length;return `<g class="gu" data-g="${g.name}" style="cursor:pointer"><path class="gl" d="${ptsPath(p)}" fill="${on?'#f0cbd4aa':'#fbf6ef66'}" stroke="#8a5a66" stroke-width="${on?5:3}"/><text class="gt" x="${cx}" y="${cy}" text-anchor="middle" font-size="${on?44:34}" fill="${on?'#513842':'#8e7470'}">${g.name}</text></g>`}).join('');
  const spots=rec.filter(k=>PLACES[k]).map(k=>{
    const P=PLACES[k],[x,y]=wx(P.lon,P.lat),on=k===S.place;
    return `<g class="spot" data-k="${esc(k)}" role="button" tabindex="0" aria-label="${esc(P.name)} 우체통" aria-pressed="${on}" style="cursor:pointer" transform="translate(${x} ${y})"><title>${esc(P.name)}의 편지 보기</title><rect x="-13" y="-16" width="26" height="34" fill="transparent"/><g class="mailbox-icon" fill="${on?'#aa596b':'#fff8ef'}" stroke="#513842" stroke-width="1.2"><path d="M-9 10V-7Q-9 -13 0 -13Q9 -13 9 -7V10Z"/><path d="M-5 -5H5" stroke-width="2" stroke-linecap="round"/><rect x="-5" y="1" width="10" height="5" rx="1" fill="${on?'#fff8ef':'#e8b8c3'}"/><path d="M-5 10V15M5 10V15"/></g><text x="15" y="3" font-size="${on?10:8}" fill="#513842">${on?esc(P.name):'우체통'}</text></g>`;
  }).join('');
  $('map').innerHTML=`${gu}<path id="han" d="${curve(HAN.map(c=>wx(...c)))}" fill="none" stroke="#7da5b2" stroke-width="9"/><g id="near">${spots}</g>`;
  document.querySelectorAll('#map .gu').forEach(g=>g.onclick=()=>{const n=g.dataset.g;if(n==='서대문구')return zoomIn();const t=$('toast');t.hidden=false;t.textContent=`${n}에는 아직 편지가 없어요.`;gsap.delayedCall(2.2,()=>t.hidden=true)});
  document.querySelectorAll('#map .spot').forEach(g=>{g.onkeydown=e=>{if(e.key==='Enter'||e.key===' '){e.preventDefault();g.onclick()}};g.onclick=()=>{const s=S.stack.find(x=>LETTERS[x.i].place===g.dataset.k);if(s&&g.dataset.k!==S.place){S.letter=s;S.place=g.dataset.k;mapScene(false)}}})}
function zoomIn(d=M.mapZoom){gsap.to('#map',{attr:{viewBox:boxOf(SDM,16)},duration:d,ease:'power2.inOut'});gsap.to('.gl',{attr:{'stroke-width':.9},duration:d});gsap.to('#han',{attr:{'stroke-width':3},duration:d});gsap.to('.gt',{autoAlpha:0,duration:.3});gsap.to('#near',{autoAlpha:1,duration:.4,delay:d*.7})}
function mapScene(reveal){replay=()=>mapScene(true);scene('s-map',2);drawSvg();slip();
  $('allBtn').onclick=()=>{gsap.to('#map',{attr:{viewBox:FULL},duration:M.mapZoom*.8,ease:'power2.inOut'});gsap.to('.gl',{attr:{'stroke-width':3},duration:M.mapZoom*.8});gsap.to('#han',{attr:{'stroke-width':9},duration:M.mapZoom*.8});gsap.to('.gt',{autoAlpha:1,duration:.4,delay:.4});gsap.to('#near',{autoAlpha:0,duration:.25})};
  if(!reveal){$('map').setAttribute('viewBox',boxOf(SDM,16));gsap.set('.gl',{attr:{'stroke-width':.9}});gsap.set('#han',{attr:{'stroke-width':3}});gsap.set('.gt',{autoAlpha:0});gsap.fromTo('#slip',{autoAlpha:0},{autoAlpha:1,duration:.3});return}
  $('map').setAttribute('viewBox',FULL);gsap.set('#near',{autoAlpha:0});
  gsap.timeline().fromTo('#mapwrap,#slip',{autoAlpha:0},{autoAlpha:1,duration:.4}).fromTo('.gl',{drawSVG:'0%',fillOpacity:0},{drawSVG:'100%',fillOpacity:1,duration:M.mapDraw,ease:'power1.inOut'},'<').fromTo('#han',{drawSVG:'0%'},{drawSVG:'100%',duration:M.mapDraw},'<').fromTo('.gt',{autoAlpha:0},{autoAlpha:1,duration:.3},'-=.3').add(()=>zoomIn(),'+=.25')}
function slip(go){const k=S.place,P=PLACES[k],d=$('slip');
  d.innerHTML=`<div class="hd"><div><small>서울 서대문구</small><h2>${esc(P.name)}</h2></div><div class="stamp"><img src="${A(`stamp-${ACTS[k]?k:"gate"}.jpg`)}" alt=""></div></div>${go?`<div class="gate"><b>${esc(P.name)}에 도착하면 열려요</b><p class="tiny">근처 200m 안에서 열 수 있어요. 현장에서 열면 추신이 함께 보여요.</p><div class="pad-row"><button class="seal-btn" id="demo" type="button">도착했어요, 위치 확인</button><button class="plain" id="gb" type="button">돌아가기</button></div></div>`:`<div class="gate"><b>이 우체통에 고른 편지가 있어요</b><div class="pad-row"><button class="seal-btn" id="now" type="button">지금 바로 읽기</button><button class="plain" id="site" type="button">장소에 가서 열기</button></div></div>`}<div class="sec"><b>왜 어울리나요</b><p>${esc(S.letter.why)}</p></div><div class="sec"><b>이곳에서 할 수 있는 것</b>${(ACTS[k]||['잠시 쉬며 오늘의 마음을 적어보세요.']).map(a=>`<p>${a}</p>`).join('')}</div><div class="sec"><b>위치</b><p>${esc(P.info)}</p></div><button class="plain" id="other" type="button">다른 편지 고르기</button>`;
  const on=(i,f)=>{const e=$(i);if(e)e.onclick=f};on('now',()=>readLetter(false));on('site',()=>slip(true));on('gb',()=>slip());on('demo',()=>locate());on('other',()=>{S.letter=null;pick()},'당신에게 어울리는 편지를 찾고 있어요.',true)}
function readLetter(site){const s=S.letter,l=LETTERS[s.i],P=PLACES[l.place],x={kind:'',note:l.spoiler?'스포일러가 포함된 편지예요.':s.why};if(!S.seen.includes(s.i))S.seen.push(s.i);
  openLetter({place:P.name,body:l.text+(site?'\n\n추신. 여기까지 와줘서 고마워요.':''),course:l.routine||'잠시 쉬며 오늘의 마음을 돌아보세요.',works:l.taste,close:()=>mapScene(false),foot:'<button id="f2" type="button">해봤어요, 편지 남기기</button><button id="f3" type="button">다른 편지 받기</button>',bind(){$('f2').onclick=()=>closeLetter(()=>write({place:l.place,work:l.taste[0]||'',cat:l.contents[0]?.type||'music',sourceLetterId:l.id}));$('f3').onclick=()=>closeLetter(()=>{S.letter=null;const r=S.stack.filter(x=>!S.seen.includes(x.i));if(r.length){S.stack=r;pick()}else recommendAgain()})}});
  if(l.spoiler){$('artB').hidden=true;const b=document.createElement('button');b.textContent='스포일러가 있어요. 그래도 읽기';b.dataset.spoiler='';$('artB').before(b);b.onclick=()=>{$('artB').hidden=false;b.remove()}}
  [...$('steps').children].forEach((e,i)=>e.classList.toggle('on',i===3))}

/* ---------- 편지 쓰기: Figma [Process2]. 받기 흐름과 별개로 편지함에서 시작한다 ---------- */
const WPAPERS=[
 {id:'paper-4',name:'포근한 흰 편지지',img:'paper-4.webp',x:1022,y:198,w:523.561,h:488.957,pw:290.535,ph:435.802,r:54.7},
 {id:'paper-3',name:'우표 테두리 편지지',img:'paper-3.webp',x:459.58,y:345.99,w:570.291,h:457.684,pw:346.007,ph:501.079,r:-75.9},
 {id:'paper-2',name:'천사 편지지',img:'paper-2.webp',wimg:'paper-3.webp',x:742,y:209,w:489.453,h:406.59,pw:418.831,ph:279.22,r:20.18},
 {id:'paper-1',name:'레이스 편지지',img:'paper-1.webp',x:199,y:102.21,w:556.073,h:652.918,pw:355.98,ph:554.691,r:24.84}];

const grow=t=>{t.style.height='auto';t.style.height=t.scrollHeight+'px'};
function fitP(){$('pstage').style.setProperty('--k',NARROW()?1:Math.min(innerWidth/1728,innerHeight/1117))}
addEventListener('resize',()=>{if(!$('s-paper').hidden)fitP()});
function write(pre={}){replay=()=>write(pre);$('lt').hidden=true;$('wform').reset();$('sform').reset();$('wm').hidden=true;$('wWorks').value=pre.work||'';S.w={paper:null,pre};$('wCat').value=pre.cat||'music';$('wPlace').innerHTML='<option value="">AI에게 맡기기</option>'+Object.values(PLACES).map(p=>'<option value="'+esc(p.id)+'">'+esc(p.name)+'</option>').join('');$('wPlace').value=pre.place||'';scene('s-paper',0);$('top').hidden=false;fitP();
  $('ppapers').innerHTML=WPAPERS.map((p,i)=>`<div class="pslot" style="left:${p.x}px;top:${p.y}px;width:${p.w}px;height:${p.h}px;z-index:${i+1}"><button class="poption" type="button" data-i="${i}" aria-label="${p.name}" style="width:${p.pw}px;height:${p.ph}px;transform:rotate(${p.r}deg)"><img src="${A(p.img)}" alt=""></button></div>`).join('');
  document.querySelectorAll('.poption').forEach(b=>b.onclick=()=>compose(WPAPERS[+b.dataset.i]));
  gsap.timeline({defaults:{ease:'power2.out'}}).fromTo('.lfolder',{autoAlpha:0},{autoAlpha:1,duration:.35}).fromTo('.pslot',{autoAlpha:0,y:24},{autoAlpha:1,y:0,duration:M.paperTime,stagger:.08},'<.1').fromTo('.phint',{autoAlpha:0},{autoAlpha:1,duration:.3},'-=.3')}
function compose(p){if(p)S.w.paper=p;p=S.w.paper;replay=()=>compose();scene('s-compose',1);$('s-compose').scrollTop=0;const src=A(p.wimg||p.img);$('wImg').src=src;$('sImg').src=src;const lace=p.id==='paper-1';$('wform').classList.toggle('lace',lace);$('sform').classList.toggle('lace',lace);document.querySelectorAll('#wform textarea').forEach(grow);
  gsap.fromTo('#wform',{autoAlpha:0,y:18},{autoAlpha:1,y:0,duration:.45,ease:'power2.out'});$('wBody').focus({preventScroll:true})}
document.querySelectorAll('#wform textarea').forEach(t=>t.oninput=()=>{grow(t);$('wm').hidden=true});
$('wBack').onclick=()=>scene('s-paper',0);
$('wform').onsubmit=e=>{e.preventDefault();return busy(async active=>{const text=$('wBody').value.trim(),work=$('wWorks').value.trim();if(!text||text.length>400)throw new Error('편지는 1~400자로 입력해주세요.');if(!validateContact(true))throw new Error('전화번호·이메일·개인정보는 편지에 입력할 수 없어요. 해당 내용을 지운 뒤 다시 시도해주세요.');const r=await API.review({text,work,cat:$('wCat').value,placeId:$('wPlace').value||null,sourceLetterId:S.w.pre.sourceLetterId||null});if(!active())return;if(r.block)throw new Error(r.note);S.w.review=r;S.w.requestId=crypto.randomUUID();sendScreen();},'편지 내용을 확인하고 있어요.',true)};
function sendScreen(){scene('s-send',2);const r=S.w.review;$('sTo').textContent='익명';$('reviewResult').innerHTML='<h2>'+(r.corrected?'AI가 수정한 편지':'저장할 편지')+'</h2><p>'+esc(r.note||'내용을 확인하고 보내주세요.')+'</p>'+(r.spoiler?'<p>스포일러가 있어 가려서 저장돼요.</p>':'')+'<div class="review-body">'+esc(r.text)+'</div>';$('mails').innerHTML=r.places.map((p,i)=>'<label class="mail"><input type="radio" name="mail" value="'+esc(p.id)+'" '+(i===0?'checked':'')+'>'+esc(PLACES[p.id].name)+' — '+esc(p.why)+'</label>').join('');$('sGo').disabled=false;}
async function saveLetter(active){const placeId=document.querySelector('[name=mail]:checked')?.value;if(!placeId)return;const saved=await API.save({requestId:S.w.requestId,reviewId:S.w.review.reviewId,placeId});if(!active())return;upsertLetter(saved);S.stack=[];$('sentT').textContent=PLACES[saved.placeId].name+' 우체통에 편지를 맡겼습니다.';scene('s-done',2)}
$('sform').onsubmit=e=>{e.preventDefault();return busy(saveLetter,'편지를 우체통에 맡기고 있어요.')};$('sBack').onclick=()=>{$('wBody').value=S.w.review.text;S.w.review=null;compose()};
$('sentNew').onclick=()=>write();$('sentGo').onclick=rate;$('sentHome').onclick=()=>intro();$('home').onclick=()=>intro();

/* ---------- 움직임 조절 패널: T 키로 열고 닫는다 ---------- */
function tune(){const old=$('tune');if(old)return old.remove();const p=document.createElement('aside');p.id='tune';
  p.innerHTML='<h2>움직임 조절</h2><p class="tiny">바꾼 뒤 "이 장면 다시"를 누르면 바로 보여요.</p>'+MOTION_UI.map(([k,n,a,b,s])=>`<label>${n}<output id="o-${k}">${M[k]}</output><input type="range" min="${a}" max="${b}" step="${s}" value="${M[k]}" data-k="${k}"></label>`).join('')+'<div class="pad-row"><button class="seal-btn" id="tRe" type="button">이 장면 다시</button><button class="plain" id="tCp" type="button">값 복사</button><button class="plain" id="tDf" type="button">기본값</button></div>';
  document.body.append(p);const save=()=>{try{localStorage.setItem('dear-motion',JSON.stringify(M))}catch(e){}};
  p.querySelectorAll('input').forEach(i=>i.oninput=()=>{M[i.dataset.k]=+i.value;$('o-'+i.dataset.k).textContent=i.value;applySpeed();save()});
  $('tRe').onclick=()=>replay();$('tCp').onclick=async()=>{const t=JSON.stringify(M,null,2);try{await navigator.clipboard.writeText(t);$('tCp').textContent='복사됨'}catch(e){prompt('아래 값을 복사하세요',t)}};
  $('tDf').onclick=()=>{Object.assign(M,DEFAULTS);save();p.remove();applySpeed();tune()}}
addEventListener('keydown',e=>{if((e.key==='t'||e.key==='T')&&!/INPUT|TEXTAREA|SELECT/.test(e.target.tagName))tune()});
if(/[?&]tune/.test(location.search))tune();


function validateContact(focus=false){
 const fields=[$('wBody'),$('wWorks')],invalid=fields.filter(field=>CONTACT_INPUT.test(field.value));
 fields.forEach(field=>field.setAttribute('aria-invalid',invalid.includes(field)?'true':'false'));
 $('wm').hidden=!invalid.length;
 $('wm').textContent=invalid.length?'전화번호·이메일·개인정보는 입력할 수 없어요. 해당 내용을 지워주세요.':'';
 if(focus&&invalid.length)invalid[0].focus();return !invalid.length;
}
['wBody','wWorks'].forEach(id=>$(id).addEventListener('input',()=>validateContact()));
let lastWheel=0;
function tasteWheel(e){
 if($('s-taste').hidden||NARROW()||V.paused||e.ctrlKey||e.target.closest('input,textarea,select,#detail,#dock'))return;
 e.preventDefault();const unit=e.deltaMode===1?16:e.deltaMode===2?innerHeight:1;
 V.y-=e.deltaY*unit;V.x-=e.deltaX*unit;clampCam();lastWheel=Date.now();
 gsap.set('#tworld',{x:V.x,y:V.y,scale:V.k});
}
addEventListener('wheel',tasteWheel,{passive:false});

let revision=0,pending=false;
const originalScene=scene;scene=function(...args){revision++;originalScene(...args)};
async function busy(task,message='요청을 처리하고 있어요.',ai=false){
 if(pending)return;
 pending=true;
 const rev=revision,active=()=>revision===rev,status=$('status'),loading=$('loading');
 const previousFocus=document.activeElement;
 const background=[...document.querySelectorAll('body > :not(#loading):not(#status):not(script)')];
 const previousInert=background.map(node=>node.inert);
 status.hidden=true;status.textContent='';
 $('loadingTitle').textContent=message;
 $('loadingHint').textContent=ai?'AI가 내용을 살펴보고 있어요. 최대 30초 정도 걸릴 수 있어요.':'잠시만 기다려주세요.';
 loading.hidden=false;
 background.forEach(node=>{node.inert=true});loading.focus({preventScroll:true});
 try{await task(active)}
 catch(e){if(active()){
   status.textContent=e.message||'요청을 처리하지 못했어요. 다시 시도해주세요.';status.hidden=false;
   const b=document.createElement('button');b.textContent='닫기';b.onclick=()=>status.hidden=true;status.append(b);
 }}finally{
   loading.hidden=true;background.forEach((node,i)=>{node.inert=previousInert[i]});pending=false;
   if(active()&&previousFocus?.isConnected)previousFocus.focus({preventScroll:true});
 }
}
const CONTACT_INPUT=/(https?:\/\/|www\.|[\w.+-]+@[\w.-]+\.[a-z]{2,}|(?:\+82[- .]?0?|0)(?:1[016789]|2|[3-6][1-5]|70)[- .]?\d{3,4}[- .]?\d{4}|\d{6}[- ]?[1-4]\d{6})/i;const dist=(a,b,c,d)=>{
  const E=6371000,r=x=>x*Math.PI/180,x=r(c-a),y=r(d-b),h=Math.sin(x/2)**2+Math.cos(r(a))*Math.cos(r(c))*Math.sin(y/2)**2;
  return 2*E*Math.asin(Math.sqrt(h))
};
function locate(){const p=PLACES[S.place],rev=revision;if(!navigator.geolocation){alert('위치 확인을 지원하지 않아요.');return}navigator.geolocation.getCurrentPosition(pos=>{if(rev!==revision)return;const m=dist(pos.coords.latitude,pos.coords.longitude,p.lat,p.lon);if(m<=200)readLetter(true);else alert('아직 약 '+Math.round(m)+'m 떨어져 있어요.')},()=>{if(rev===revision)alert('위치 권한을 확인해주세요.')},{enableHighAccuracy:true,timeout:8000})}
async function start(){await busy(async active=>{const d=await API.bootstrap();if(!active())return;Object.values(CATALOG).forEach(c=>c.items=[]);d.contents.forEach(c=>CATALOG[c.type].items.push([c.title,c.tag,c.artist||MUSIC_ARTISTS[c.id]||'']));d.places.forEach(p=>PLACES[p.id]=p);d.letters.forEach(upsertLetter);intro()},'서울의 우체통을 준비하고 있어요.');if(!Object.keys(PLACES).length){const b=document.createElement('button');b.textContent='다시 연결';b.onclick=()=>{b.remove();start()};$('status').append(b)}}
start();
