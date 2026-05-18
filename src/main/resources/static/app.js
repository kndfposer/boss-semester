let token = localStorage.getItem('token') || '';
let currentUser = localStorage.getItem('username') || '';
let currentRole = localStorage.getItem('role') || '';
let collectionsCache = [];

function headers(){
    return {'Content-Type':'application/json','Authorization':'Bearer '+token};
}

function showTab(id){
    if (!token) {
        authStatus.textContent = 'Сначала войдите в аккаунт';
        authStatus.className = 'status err';
        return;
    }
    document.querySelectorAll('.tab').forEach(x => x.classList.add('hidden'));
    document.getElementById(id).classList.remove('hidden');
}

function updateAuthUi(message = ''){
    const logged = Boolean(token);
    mainNav.classList.toggle('hidden', !logged);
    welcome.classList.toggle('hidden', logged);
    document.querySelectorAll('.tab').forEach(x => x.classList.add('hidden'));
    adminNavButton.classList.toggle('hidden', currentRole !== 'ADMIN');

    if (logged) {
        authStatus.textContent = message || `Вы вошли как ${currentUser}. Теперь можно генерировать и сохранять боссов.`;
        authStatus.className = 'status ok';
        showTab('gen');
    } else {
        authStatus.textContent = message || 'Вы не вошли';
        authStatus.className = 'status';
    }
}

updateAuthUi();

async function api(url, opts={}){
    const r = await fetch(url, opts);
    const t = await r.text();
    let data;
    try { data = JSON.parse(t); } catch { data = t; }
    if(!r.ok) throw new Error(data.error || t || 'Ошибка');
    return data;
}

async function register(){
    try{
        const d = await api('/api/auth/register',{
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify({username:username.value,password:password.value})
        });
        setSession(d);
        updateAuthUi('Регистрация прошла успешно. Вы вошли в систему.');
        await loadCollectionsSilent();
    }catch(e){
        authStatus.textContent = e.message;
        authStatus.className = 'status err';
    }
}

async function login(){
    try{
        const d = await api('/api/auth/login',{
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify({username:username.value,password:password.value})
        });
        setSession(d);
        updateAuthUi('Вход выполнен успешно. Теперь можно создавать генерации.');
        await loadCollectionsSilent();
    }catch(e){
        authStatus.textContent = e.message;
        authStatus.className = 'status err';
    }
}

function setSession(d){
    token = d.token;
    currentUser = d.username || username.value;
    currentRole = d.role || 'USER';
    localStorage.setItem('token', token);
    localStorage.setItem('username', currentUser);
    localStorage.setItem('role', currentRole);
}

function logout(){
    token = '';
    currentUser = '';
    currentRole = '';
    collectionsCache = [];
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    updateAuthUi('Вы вышли из аккаунта');
}

function subjectsEl(){ return document.getElementById('subjects'); }
function subjectLines(){ return subjectsEl().value.split('\n').map(s => s.trim()).filter(Boolean); }

function renderSubjectSliders(defaultValues = []){
    const subjects = subjectLines();
    subjectSliders.innerHTML = subjects.map((s, i) => {
        const safe = escapeHtml(s);
        const value = defaultValues[i] || 5;
        return `<div class="sliderItem">
            <div class="row between"><span>${safe}</span><b><span id="subDiffLabel${i}">${value}</span>/10</b></div>
            <input class="subjectDifficulty" data-index="${i}" type="range" min="1" max="10" value="${value}" oninput="updateSubjectDifficultyLabels()">
        </div>`;
    }).join('');
    updateSubjectDifficultyLabels();
}

function updateSubjectDifficultyLabels(){
    const values = [...document.querySelectorAll('.subjectDifficulty')].map(x => +x.value);
    values.forEach((v, i) => {
        const label = document.getElementById('subDiffLabel' + i);
        if (label) label.textContent = v;
    });
    const avg = values.length ? Math.round(values.reduce((a,b) => a+b, 0) / values.length) : 5;
    avgDifficulty.textContent = avg;
}

function subjectDifficultyValues(){
    const values = [...document.querySelectorAll('.subjectDifficulty')].map(x => +x.value);
    return values.length ? values : subjectLines().map(() => 5);
}

async function createBoss(){
    genStatus.textContent = 'Выполняется генерация...';
    genStatus.className = '';
    result.innerHTML = '';

    const subjects = subjectLines();
    const subjectDifficulties = subjectDifficultyValues();
    const difficulty = subjectDifficulties.length
        ? Math.round(subjectDifficulties.reduce((a,b) => a+b, 0) / subjectDifficulties.length)
        : 5;

    try{
        const d = await api('/api/boss',{
            method:'POST',
            headers:headers(),
            body:JSON.stringify({subjects, subjectDifficulties, difficulty, emotionalBackground:emotion.value, style:style.value})
        });
        genStatus.textContent = 'Статус: ' + d.status + (d.errorMessage ? '\nОшибка: ' + d.errorMessage : '');
        genStatus.className = d.status === 'COMPLETED' ? 'ok' : (d.status === 'ERROR' ? 'err' : '');
        result.innerHTML = renderBoss(d, 'last');
        await loadCollectionsSilent();
    }catch(e){
        genStatus.textContent = e.message;
        genStatus.className = 'err';
    }
}

function renderBoss(d, place='history'){
    const collectionOptions = collectionsCache.length
        ? collectionsCache.map(c => `<option value="${c.id}">${escapeHtml(c.title)}</option>`).join('')
        : '<option value="">Сначала создайте коллекцию</option>';

    const badges = `<div class="badges">
        <span class="badge ${d.status === 'COMPLETED' ? 'good' : d.status === 'ERROR' ? 'bad' : ''}">${d.status}</span>
        ${d.saved ? '<span class="badge saved">СОХРАНЁН</span>' : '<span class="badge mutedBadge">НЕ СОХРАНЁН</span>'}
        ${d.favorite ? '<span class="badge fav">ИЗБРАННОЕ</span>' : ''}
    </div>`;

    return `<div class="item bossCard">
        <h3>#${d.id} — ${d.style}</h3>
        ${badges}
        <p><b>Предметы:</b> ${d.subjects.map(escapeHtml).join(', ')}</p>
        <p><b>Сложность по предметам:</b> ${renderDiffPairs(d)}</p>
        <p><b>Средняя сложность:</b> ${d.difficulty}/10</p>
        <p><b>Эмоция:</b> ${escapeHtml(d.emotionalBackground || '')}</p>
        ${d.errorMessage ? `<p class="err">${escapeHtml(d.errorMessage)}</p>` : ''}
        ${d.imageUrl ? `<img src="${d.imageUrl}" alt="Босс семестра">` : ''}
        <div class="row wrap">
            <button onclick="saveBoss(${d.id}, ${!d.saved})">${d.saved ? 'Убрать из сохранённых' : 'Сохранить результат'}</button>
            <button onclick="fav(${d.id}, ${!d.favorite})">${d.favorite ? 'Убрать из избранного' : 'Добавить в избранное'}</button>
            <button onclick="prepareClone(${d.id})">Изменить параметры</button>
            <button onclick="cloneBoss(${d.id})">Быстро изменить и сгенерировать</button>
            <button onclick="submitShowcase(${d.id})">Добавить на общую витрину</button>
        </div>
        <div class="collectionAdd">
            <select id="collectionSelect-${place}-${d.id}">${collectionOptions}</select>
            <button onclick="addToCollection(${d.id}, 'collectionSelect-${place}-${d.id}')">Добавить в коллекцию семестра</button>
        </div>
    </div>`;
}

function renderDiffPairs(d){
    const values = d.subjectDifficulties || [];
    if (!values.length) return 'не указано отдельно';
    return d.subjects.map((s, i) => `${escapeHtml(s)} — ${values[i] || d.difficulty}/10`).join('; ');
}

async function loadHistory(){
    try{
        await loadCollectionsSilent();
        const arr = await api('/api/boss',{headers:headers()});
        historyList.innerHTML = renderBossGrid(arr, 'history');
    }catch(e){ historyList.textContent = e.message; }
}

async function loadFavorites(){
    try{
        await loadCollectionsSilent();
        const arr = await api('/api/boss/favorites',{headers:headers()});
        favoritesList.innerHTML = renderBossGrid(arr, 'favorites') || '<p class="muted">В избранном пока ничего нет.</p>';
    }catch(e){ favoritesList.textContent = e.message; }
}

async function loadSaved(){
    try{
        await loadCollectionsSilent();
        const arr = await api('/api/boss/saved',{headers:headers()});
        savedList.innerHTML = renderBossGrid(arr, 'saved') || '<p class="muted">Сохранённых результатов пока нет.</p>';
    }catch(e){ savedList.textContent = e.message; }
}

function renderBossGrid(arr, prefix){
    if (!arr || !arr.length) return '';
    return '<div class="grid">' + arr.map((x, i) => renderBoss(x, prefix + i)).join('') + '</div>';
}

async function fav(id,value){
    try{
        const d = await api(`/api/boss/${id}/favorite?value=${value}`,{method:'POST',headers:headers()});
        result.innerHTML = renderBoss(d, 'fav');
        await loadCollectionsSilent();
    }catch(e){ alert(e.message); }
}

async function saveBoss(id,value){
    try{
        const d = await api(`/api/boss/${id}/saved?value=${value}`,{method:'POST',headers:headers()});
        result.innerHTML = renderBoss(d, 'savedAction');
        await loadCollectionsSilent();
    }catch(e){ alert(e.message); }
}

function prepareClone(id){
    api(`/api/boss/${id}`, {headers:headers()}).then(d => {
        fillGenerationForm(d);
        genStatus.textContent = `Параметры запроса #${id} перенесены в форму. Измени их и нажми «Сгенерировать».`;
        genStatus.className = 'ok';
    }).catch(e => alert(e.message));
}

function fillGenerationForm(d){
    showTab('gen');
    subjects.value = (d.subjects || []).join('\n');
    emotion.value = d.emotionalBackground || '';
    style.value = d.style || 'FANTASY';
    renderSubjectSliders(d.subjectDifficulties && d.subjectDifficulties.length ? d.subjectDifficulties : (d.subjects || []).map(() => d.difficulty || 5));
}

async function cloneBoss(id){
    const subs = prompt('Новые предметы через запятую', subjectLines().join(', '));
    if(!subs) return;
    const subjects = subs.split(',').map(s=>s.trim()).filter(Boolean);
    const subjectDifficulties = subjects.map(() => 5);
    const difficulty = Math.round(subjectDifficulties.reduce((a,b) => a+b, 0) / subjectDifficulties.length);

    try{
        showTab('gen');
        genStatus.textContent = 'Выполняется новая генерация на основе старого запроса...';
        result.innerHTML = '';
        const d = await api(`/api/boss/${id}/clone`,{
            method:'POST',
            headers:headers(),
            body:JSON.stringify({subjects, subjectDifficulties, difficulty, emotionalBackground:emotion.value || 'нейтральный фон', style:style.value})
        });
        genStatus.textContent = 'Статус: ' + d.status + (d.errorMessage ? '\nОшибка: ' + d.errorMessage : '');
        genStatus.className = d.status === 'COMPLETED' ? 'ok' : (d.status === 'ERROR' ? 'err' : '');
        result.innerHTML = renderBoss(d, 'clone');
    }catch(e){
        genStatus.textContent = e.message;
        genStatus.className = 'err';
    }
}

async function createCollection(){
    try{
        await api('/api/collections',{
            method:'POST',
            headers:headers(),
            body:JSON.stringify({title:collectionTitle.value,description:'Коллекция для просмотра эволюции сложности обучения'})
        });
        collectionTitle.value = '';
        await loadCollections();
    }catch(e){ alert(e.message); }
}

async function loadCollectionsSilent(){
    if (!token) return;
    try{ collectionsCache = await api('/api/collections',{headers:headers()}); }
    catch{ collectionsCache = []; }
}

async function loadCollections(){
    try{
        await loadCollectionsSilent();
        collectionsList.innerHTML = collectionsCache.map(renderCollection).join('') || '<p class="muted">Коллекций пока нет.</p>';
    }catch(e){ collectionsList.textContent = e.message; }
}

function renderCollection(c){
    const items = c.items || [];
    const timeline = items.length ? `<div class="timeline">${items.map((b, i) => `
        <div class="timelinePoint">
            <span>${i + 1}</span>
            <b>${b.difficulty}/10</b>
            <small>#${b.id}</small>
        </div>`).join('')}</div>` : '<p class="muted">Добавь боссов из истории, сохранённых или избранного.</p>';

    const cards = items.length ? `<div class="horizontalScroll">${items.map((b, i) => `
        <div class="miniBoss">
            <b>#${b.id} — ${b.style}</b>
            ${b.imageUrl ? `<img src="${b.imageUrl}" alt="Босс из коллекции">` : ''}
            <p>${escapeHtml((b.subjects || []).join(', '))}</p>
            <p>Сложность: ${b.difficulty}/10</p>
            <button onclick="removeFromCollection(${c.id}, ${b.id})">Убрать</button>
        </div>`).join('')}</div>` : '';

    return `<div class="item collectionCard">
        <h3>${escapeHtml(c.title)}</h3>
        <p>${escapeHtml(c.description || '')}</p>
        <p><b>Образов:</b> ${c.size}</p>
        <h4>Эволюция сложности</h4>
        ${timeline}
        ${cards}
    </div>`;
}

async function addToCollection(requestId, selectId){
    const select = document.getElementById(selectId);
    if (!select || !select.value) {
        alert('Сначала создайте коллекцию');
        return;
    }
    try{
        await api(`/api/collections/${select.value}/items`,{
            method:'POST',
            headers:headers(),
            body:JSON.stringify({requestId})
        });
        alert('Босс добавлен в коллекцию');
        await loadCollectionsSilent();
    }catch(e){ alert(e.message); }
}

async function removeFromCollection(collectionId, requestId){
    try{
        await api(`/api/collections/${collectionId}/items/${requestId}`,{method:'DELETE',headers:headers()});
        await loadCollections();
    }catch(e){ alert(e.message); }
}

async function submitShowcase(id){
    try{
        await api(`/api/showcase/request/${id}`,{
            method:'POST',
            headers:headers(),
            body:JSON.stringify({})
        });
        alert('Босс отправлен на модерацию. После одобрения он появится в общей витрине у всех пользователей.');
    }catch(e){ alert(e.message); }
}

async function loadShowcase(){
    try{
        const arr = await api('/api/showcase');
        showcaseList.innerHTML = '<div class="grid">' + arr.map(x => `
            <div class="item showcaseCard">
                <h3>${escapeHtml(x.title)}</h3>
                <p class="muted">Создано по образцу пользователя: ${escapeHtml(x.owner || '')}</p>
                <p><b>Предметы:</b> ${(x.subjects || []).map(escapeHtml).join(', ')}</p>
                <p><b>Сложность:</b> ${x.difficulty}/10</p>
                ${x.imageUrl?`<img src="${x.imageUrl}">`:''}
                <button onclick='useShowcaseAsTemplate(${JSON.stringify(x).replaceAll("'", "&#039;")})'>Создать похожего босса</button>
            </div>`).join('') + '</div>';
    }catch(e){ showcaseList.textContent = e.message; }
}

function useShowcaseAsTemplate(x){
    fillGenerationForm(x);
    genStatus.textContent = `Форма заполнена по публичному образцу пользователя ${x.owner}. Исходная работа не меняется.`;
    genStatus.className = 'ok';
}

async function loadModeration(){
    try{
        const arr = await api('/api/showcase/moderation',{headers:headers()});
        moderationList.innerHTML = arr.map(x => `
            <div class="item">
                <b>${escapeHtml(x.title)}</b>
                <p>${x.status}</p>
                <p class="muted">Автор: ${escapeHtml(x.owner || '')}</p>
                ${x.imageUrl?`<img src="${x.imageUrl}">`:''}
                <button onclick="moderate(${x.id},true)">Одобрить</button>
                <button onclick="moderate(${x.id},false)">Отклонить</button>
            </div>`).join('');
    }catch(e){ moderationList.textContent = e.message; }
}

async function moderate(id,approve){
    try{
        await api(`/api/showcase/moderation/${id}`,{
            method:'POST',
            headers:headers(),
            body:JSON.stringify({approve,comment: approve ? 'Одобрено' : 'Отклонено'})
        });
        loadModeration();
    }catch(e){ alert(e.message); }
}

function escapeHtml(value){
    return String(value ?? '')
        .replaceAll('&','&amp;')
        .replaceAll('<','&lt;')
        .replaceAll('>','&gt;')
        .replaceAll('"','&quot;')
        .replaceAll("'",'&#039;');
}