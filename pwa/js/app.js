/**
 * VitalPet PWA — Core Engine & Clinical Management
 * Universal Progressive Web App supporting all 8 official languages.
 */

// --------------------------------------------------------------------------
// 1. Language & Localization System (8 Languages)
// --------------------------------------------------------------------------
const LANGUAGES = [
  { code: 'pt', name: 'Português (PT)', flag: '🇵🇹' },
  { code: 'en', name: 'English (US)', flag: '🇺🇸' },
  { code: 'es', name: 'Español', flag: '🇪🇸' },
  { code: 'fr', name: 'Français', flag: '🇫🇷' },
  { code: 'de', name: 'Deutsch', flag: '🇩🇪' },
  { code: 'it', name: 'Italiano', flag: '🇮🇹' },
  { code: 'nl', name: 'Nederlands', flag: '🇳🇱' },
  { code: 'lb', name: 'Lëtzebuergesch', flag: '🇱🇺' }
];

let currentLang = localStorage.getItem('vitalpet_lang') || 'pt';
let translations = {};

async function loadTranslations() {
  // Try multiple paths to support both local dev and GitHub Pages deployment
  const paths = ['./translations.json', '../translations.json', '/VitalPet/pwa/translations.json'];
  for (const path of paths) {
    try {
      const res = await fetch(path, { cache: 'no-store' });
      if (res.ok) {
        translations = await res.json();
        console.log(`[VitalPet] Loaded ${Object.keys(translations).length} translation keys from: ${path}`);
        break;
      }
    } catch (e) {
      // try next path
    }
  }
  if (!Object.keys(translations).length) {
    console.warn('[VitalPet] Could not load translations.json from any path. Using key fallback.');
  }
  applyTranslations();
}

function t(key) {
  if (translations[key] && translations[key][currentLang]) {
    return translations[key][currentLang];
  }
  if (translations[key] && translations[key]['pt']) {
    return translations[key]['pt'];
  }
  return key;
}

function setLanguage(langCode) {
  currentLang = langCode;
  localStorage.setItem('vitalpet_lang', langCode);
  applyTranslations();
  renderAllViews();
}

function applyTranslations() {
  // Update HTML lang attribute for accessibility
  document.documentElement.lang = currentLang;

  document.querySelectorAll('[data-i18n]').forEach(el => {
    const key = el.getAttribute('data-i18n');
    const translated = t(key);
    // For option elements, update the text content
    if (el.tagName === 'OPTION') {
      el.textContent = translated;
    } else {
      el.textContent = translated;
    }
  });
  document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
    const key = el.getAttribute('data-i18n-placeholder');
    el.placeholder = t(key);
  });
  document.querySelectorAll('[data-i18n-title]').forEach(el => {
    const key = el.getAttribute('data-i18n-title');
    el.title = t(key);
  });
}

// --------------------------------------------------------------------------
// 2. Data Store & Mobile Compatibility
// --------------------------------------------------------------------------
const STORAGE_KEY = 'vitalpet_pets_data';

const DEFAULT_SAMPLE_PETS = [
  {
    id: 'sample-thor',
    name: 'Thor',
    species: 'DOG',
    breed: 'Golden Retriever',
    birthDate: '2021-04-12',
    currentWeightKg: 31.8,
    targetWeightKg: 30.0,
    neutered: true,
    activityLevel: 'ACTIVE',
    weightLogs: [
      { id: 'w1', date: new Date(Date.now() - 45 * 86400000).toISOString(), weightKg: 33.2, notes: 'Consulta inicial' },
      { id: 'w2', date: new Date(Date.now() - 30 * 86400000).toISOString(), weightKg: 32.5, notes: 'Início da ração light' },
      { id: 'w3', date: new Date(Date.now() - 15 * 86400000).toISOString(), weightKg: 32.0, notes: 'Progresso constante' },
      { id: 'w4', date: new Date().toISOString(), weightKg: 31.8, notes: 'Ótima condição muscular' }
    ],
    symptomLogs: [
      { id: 's1', date: new Date(Date.now() - 2 * 86400000).toISOString(), fecalScore: 2, vomitingEpisodes: 0, appetite: 'NORMAL', energyLevel: 'NORMAL', waterIntake: 'NORMAL', notes: 'Fezes normais, muito ativo.' }
    ],
    walkSessions: [
      { id: 'wk1', date: new Date(Date.now() - 86400000).toISOString(), durationSeconds: 2400, distanceMeters: 2800, caloriesBurned: 168.0, intensity: 'MODERATE' }
    ],
    vaccineLogs: [
      { id: 'v1', vaccineName: 'Raiva', applicationDate: '2025-05-10', nextDueDate: '2026-05-10' },
      { id: 'v2', vaccineName: 'DHPPI (Polivalente)', applicationDate: '2025-06-15', nextDueDate: '2026-06-15' }
    ]
  },
  {
    id: 'sample-mia',
    name: 'Mia',
    species: 'CAT',
    breed: 'Europeu Comum',
    birthDate: '2022-09-01',
    currentWeightKg: 4.2,
    targetWeightKg: 4.0,
    neutered: true,
    activityLevel: 'MODERATE',
    weightLogs: [
      { id: 'mw1', date: new Date(Date.now() - 60 * 86400000).toISOString(), weightKg: 4.5, notes: 'Controlo anual' },
      { id: 'mw2', date: new Date().toISOString(), weightKg: 4.2, notes: 'Peso estável' }
    ],
    symptomLogs: [],
    walkSessions: [],
    vaccineLogs: [
      { id: 'mv1', vaccineName: 'Tríplice Felina (RCP)', applicationDate: '2025-08-20', nextDueDate: '2026-08-20' }
    ]
  }
];

const PetStore = {
  getPets() {
    try {
      const data = localStorage.getItem(STORAGE_KEY);
      if (!data) {
        this.savePets(DEFAULT_SAMPLE_PETS);
        return DEFAULT_SAMPLE_PETS;
      }
      const parsed = JSON.parse(data);
      if (!Array.isArray(parsed) || parsed.length === 0) {
        this.savePets(DEFAULT_SAMPLE_PETS);
        return DEFAULT_SAMPLE_PETS;
      }
      return parsed;
    } catch (e) {
      console.error('[PetStore] Error reading pets', e);
      return DEFAULT_SAMPLE_PETS;
    }
  },

  savePets(pets) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(pets));
  },

  getPet(id) {
    return this.getPets().find(p => p.id === id);
  },

  updatePet(updatedPet) {
    const pets = this.getPets().map(p => p.id === updatedPet.id ? updatedPet : p);
    this.savePets(pets);
    renderAllViews();
  },

  addPet(newPet) {
    const pets = this.getPets();
    pets.push(newPet);
    this.savePets(pets);
    selectedPetId = newPet.id;
    renderAllViews();
  },

  deletePet(id) {
    const pets = this.getPets().filter(p => p.id !== id);
    this.savePets(pets);
    if (selectedPetId === id) {
      selectedPetId = pets.length > 0 ? pets[0].id : null;
    }
    renderAllViews();
  },

  exportBackup() {
    const data = {
      version: '1.0',
      exportDate: new Date().toISOString(),
      appName: 'VitalPet Universal',
      pets: this.getPets()
    };
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `vitalpet_backup_${new Date().toISOString().slice(0, 10)}.json`;
    a.click();
    URL.revokeObjectURL(url);
  },

  importBackup(jsonString) {
    try {
      const parsed = JSON.parse(jsonString);
      if (parsed.pets && Array.isArray(parsed.pets)) {
        this.savePets(parsed.pets);
        selectedPetId = parsed.pets[0]?.id || null;
        renderAllViews();
        alert(t('Importação concluída com sucesso!'));
      } else {
        alert(t('Ficheiro de backup inválido.'));
      }
    } catch (e) {
      alert(t('Erro ao ler o ficheiro JSON de backup.'));
    }
  }
};

// --------------------------------------------------------------------------
// 3. Clinical WSAVA Nutritional Calculator
// --------------------------------------------------------------------------
const Clinical = {
  calculateRER(weightKg) {
    if (!weightKg || weightKg <= 0) return 0;
    return Math.round(70 * Math.pow(weightKg, 0.75));
  },

  calculateDER(weightKg, species, lifeStage, neutered) {
    const rer = this.calculateRER(weightKg);
    let factor = 1.6;

    if (species === 'DOG') {
      if (lifeStage === 'PUPPY') factor = 3.0;
      else if (lifeStage === 'WEIGHT_LOSS') factor = 1.0;
      else if (lifeStage === 'SENIOR') factor = 1.4;
      else factor = neutered ? 1.6 : 1.8;
    } else {
      if (lifeStage === 'KITTEN') factor = 2.5;
      else if (lifeStage === 'WEIGHT_LOSS') factor = 0.8;
      else factor = neutered ? 1.2 : 1.4;
    }

    return {
      rer: rer,
      factor: factor,
      der: Math.round(rer * factor)
    };
  },

  calculateFoodGrams(der, kcalPerKg) {
    if (!kcalPerKg || kcalPerKg <= 0) return 0;
    return Math.round((der / kcalPerKg) * 1000);
  },

  fecalScoreDescription(score) {
    switch (Number(score)) {
      case 1: return t('Score 1: Muito dura e seca (obstipação)');
      case 2: return t('Score 2: Firme e bem formada (ideal)');
      case 3: return t('Score 3: Húmida com forma definida');
      case 4: return t('Score 4: Muito húmida e pastosa');
      case 5: return t('Score 5: Semiformada e viscosa');
      case 6: return t('Score 6: Pastosa sem consistência');
      case 7: return t('Score 7: Diarreia aquosa líquida');
      default: return t('Não avaliado');
    }
  }
};

// --------------------------------------------------------------------------
// 4. View Rendering & UI Controllers
// --------------------------------------------------------------------------
let selectedPetId = null;

function getActivePet() {
  const pets = PetStore.getPets();
  if (!selectedPetId && pets.length > 0) {
    selectedPetId = pets[0].id;
  }
  return pets.find(p => p.id === selectedPetId) || pets[0] || null;
}

function renderAllViews() {
  const activePet = getActivePet();
  renderDashboard();
  renderPetsView();
  renderWeightView(activePet);
  renderSymptomsView(activePet);
  renderWalksView(activePet);
  renderCalculatorView(activePet);
  applyTranslations();
}

function renderDashboard() {
  const pets = PetStore.getPets();
  
  // Estatísticas Globais
  const statTotalPets = document.getElementById('stat-total-pets');
  if (statTotalPets) statTotalPets.textContent = pets.length;
  
  const totalLogs = pets.reduce((sum, p) => sum + (p.weightLogs?.length || 0) + (p.symptomLogs?.length || 0), 0);
  const statTotalLogs = document.getElementById('stat-total-logs');
  if (statTotalLogs) statTotalLogs.textContent = totalLogs;

  const totalWalks = pets.reduce((sum, p) => sum + (p.walkSessions?.length || 0), 0);
  const statTotalWalks = document.getElementById('stat-total-walks');
  if (statTotalWalks) statTotalWalks.textContent = totalWalks;

  // Alertas Clínicos
  const alertsContainer = document.getElementById('dash-alerts-container');
  if (alertsContainer) {
    alertsContainer.innerHTML = '';
    const now = new Date();
    const sevenDaysAgo = new Date(Date.now() - 7 * 86400000);
    const threeDaysAgo = new Date(Date.now() - 3 * 86400000);

    let alertCount = 0;
    pets.forEach(pet => {
      const recentSymptoms = (pet.symptomLogs || []).filter(s => new Date(s.date) >= sevenDaysAgo);
      const vomitingCount = recentSymptoms.reduce((acc, s) => acc + (s.vomitingEpisodes || 0), 0);
      if (vomitingCount >= 3) {
        alertsContainer.innerHTML += `
          <div class="alert-banner alert-high">
            <span>⚠️</span>
            <div><strong>${pet.name}</strong>: ${t('Vómitos recorrentes nos últimos 7 dias.')} (${vomitingCount}x)</div>
          </div>`;
        alertCount++;
      }

      const diarrhea = (pet.symptomLogs || []).filter(s => new Date(s.date) >= threeDaysAgo).some(s => s.fecalScore >= 6);
      if (diarrhea) {
        alertsContainer.innerHTML += `
          <div class="alert-banner alert-high">
            <span>⚠️</span>
            <div><strong>${pet.name}</strong>: ${t('Score fecal elevado nos últimos 3 dias.')}</div>
          </div>`;
        alertCount++;
      }

      (pet.vaccineLogs || []).forEach(vac => {
        if (vac.nextDueDate && new Date(vac.nextDueDate) < now) {
          alertsContainer.innerHTML += `
            <div class="alert-banner alert-medium">
              <span>💉</span>
              <div><strong>${pet.name}</strong>: ${t('Vacina')} "${vac.vaccineName}" (${t('Em atraso')})</div>
            </div>`;
          alertCount++;
        }
      });
    });

    if (alertCount === 0) {
      alertsContainer.innerHTML = `
        <div style="padding: 12px; background: rgba(102, 187, 106, 0.1); border-radius: var(--radius-md); color: #81C784; font-size: 13px;">
          ✓ ${t('Sem alertas clínicos críticos no momento.')}
        </div>`;
    }
  }

  // Lista de Pets no Dashboard
  const dashPetsList = document.getElementById('dash-pets-grid');
  if (dashPetsList) {
    dashPetsList.innerHTML = '';
    pets.forEach(pet => {
      const rer = Clinical.calculateRER(pet.currentWeightKg);
      dashPetsList.innerHTML += `
        <div class="stat-card" style="cursor:pointer;" onclick="selectPet('${pet.id}'); switchTab('weight');">
          <div class="stat-icon" style="background: rgba(0, 137, 123, 0.2); color: var(--primary-light); font-size: 20px;">
            ${pet.species === 'CAT' ? '🐱' : '🐶'}
          </div>
          <div class="stat-info" style="flex:1;">
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <h3 style="font-size: 16px; font-weight: 700;">${pet.name}</h3>
              <span class="tag-badge" style="background: var(--primary-soft); color: var(--primary-light);">${pet.breed || pet.species}</span>
            </div>
            <div style="font-size: 13px; color: var(--text-muted); margin-top: 4px;">
              ${t('Peso Atual')}: <strong>${pet.currentWeightKg.toFixed(1)} kg</strong> | RER: <strong>${rer} kcal</strong>
            </div>
          </div>
        </div>`;
    });
  }
}

function selectPet(id) {
  selectedPetId = id;
  renderAllViews();
}

function renderPetsView() {
  const pets = PetStore.getPets();
  const container = document.getElementById('pets-list-container');
  if (!container) return;
  container.innerHTML = '';

  if (pets.length === 0) {
    container.innerHTML = `
      <div class="panel" style="text-align:center; padding:48px;">
        <div style="font-size:40px; margin-bottom:12px;">🐾</div>
        <h3 style="font-size:18px; font-weight:700; margin-bottom:8px;">${t('Registe primeiro um pet.')}</h3>
        <p style="color:var(--text-muted); font-size:14px; margin-bottom:20px;">${t('Adicione o seu cão ou gato para começar a acompanhar.')}</p>
        <button class="btn btn-primary" onclick="openModal('modal-add-pet')">
          <span>+</span> ${t('Adicionar Pet')}
        </button>
      </div>`;
    return;
  }

  pets.forEach(pet => {
    container.innerHTML += `
      <div class="panel" style="margin-bottom: 16px;">
        <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:16px;">
          <div style="display:flex; gap:16px; align-items:center;">
            <div class="pet-chip-avatar" style="width:52px; height:52px; font-size:24px;">
              ${pet.species === 'CAT' ? '🐱' : '🐶'}
            </div>
            <div>
              <div style="display:flex; align-items:center; gap:8px;">
                <h2 style="font-size:20px; font-weight:800;">${pet.name}</h2>
                <span class="tag-badge" style="background:var(--primary-soft); color:var(--primary-light);">${pet.breed || pet.species}</span>
              </div>
              <div style="color:var(--text-muted); font-size:13px; margin-top:2px;">
                ${pet.neutered ? t('Esterilizado') : t('Inteiro')} • ${pet.activityLevel || 'Normal'}
              </div>
              <div style="margin-top:6px; font-size:13.5px;">
                ${t('Peso Atual')}: <strong>${pet.currentWeightKg} kg</strong> | ${t('Peso Alvo')}: <strong>${pet.targetWeightKg || '--'} kg</strong>
              </div>
            </div>
          </div>
          <div style="display:flex; gap:8px;">
            <button class="btn btn-outline" onclick="selectPet('${pet.id}'); switchTab('weight');">
              📈 ${t('Curva de Peso')}
            </button>
            <button class="btn btn-danger" onclick="if(confirm('${t('Eliminar')} ${pet.name}?')) PetStore.deletePet('${pet.id}')">
              ${t('Eliminar')}
            </button>
          </div>
        </div>
      </div>`;
  });
}

function renderWeightView(pet) {
  const nameEl = document.getElementById('weight-pet-name');
  const chips = document.getElementById('weight-pet-chips');
  const svg = document.getElementById('weight-chart-svg');
  const list = document.getElementById('weight-logs-list');

  if (!pet) {
    if (nameEl) nameEl.textContent = t('Meus Pets');
    if (chips) chips.innerHTML = `<div style="color:var(--text-muted); font-size:13px;">${t('Registe primeiro um pet.')}</div>`;
    if (svg) svg.innerHTML = `<text x="50%" y="50%" fill="#94A3B8" text-anchor="middle">${t('Registe primeiro um pet.')}</text>`;
    if (list) list.innerHTML = '';
    return;
  }

  if (nameEl) nameEl.textContent = pet.name;
  
  if (chips) {
    chips.innerHTML = '';
    PetStore.getPets().forEach(p => {
      chips.innerHTML += `
        <div class="pet-chip-item ${p.id === pet.id ? 'active' : ''}" onclick="selectPet('${p.id}')">
          <span>${p.species === 'CAT' ? '🐱' : '🐶'}</span>
          <span style="font-weight:600;">${p.name}</span>
        </div>`;
    });
  }

  const logs = (pet.weightLogs || []).sort((a, b) => new Date(a.date) - new Date(b.date));
  
  if (svg) {
    if (logs.length >= 2) {
      const minW = Math.min(...logs.map(l => l.weightKg)) - 0.5;
      const maxW = Math.max(...logs.map(l => l.weightKg)) + 0.5;
      const range = (maxW - minW) || 1;
      const w = 600;
      const h = 200;

      let points = logs.map((l, i) => {
        const x = 30 + (i / (logs.length - 1)) * (w - 60);
        const y = h - 20 - ((l.weightKg - minW) / range) * (h - 40);
        return `${x},${y}`;
      }).join(' ');

      svg.innerHTML = `
        <polyline fill="none" stroke="rgba(77, 182, 172, 0.2)" stroke-width="2" points="${points}" />
        <polyline fill="none" stroke="#00897B" stroke-width="3" points="${points}" />
        ${logs.map((l, i) => {
          const x = 30 + (i / (logs.length - 1)) * (w - 60);
          const y = h - 20 - ((l.weightKg - minW) / range) * (h - 40);
          return `<circle cx="${x}" cy="${y}" r="4" fill="#4DB6AC" stroke="#FFFFFF" stroke-width="2" />`;
        }).join('')}
      `;
    } else {
      svg.innerHTML = `<text x="50%" y="50%" fill="#94A3B8" text-anchor="middle">${t('Mínimo 2 registos de peso para visualizar gráfico.')}</text>`;
    }
  }

  if (list) {
    list.innerHTML = '';
    logs.slice().reverse().forEach(log => {
      const d = new Date(log.date).toLocaleDateString();
      list.innerHTML += `
        <div class="log-card">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div>
              <span style="font-size:16px; font-weight:800; color:var(--primary-light);">${log.weightKg.toFixed(2)} kg</span>
              <span style="font-size:12px; color:var(--text-muted); margin-left:12px;">${d}</span>
            </div>
            <div style="font-size:12px; color:var(--text-muted);">${log.notes || ''}</div>
          </div>
        </div>`;
    });
  }
}

function renderSymptomsView(pet) {
  const nameEl = document.getElementById('symptoms-pet-name');
  const chips = document.getElementById('symptoms-pet-chips');
  const list = document.getElementById('symptoms-logs-list');

  if (!pet) {
    if (nameEl) nameEl.textContent = t('Meus Pets');
    if (chips) chips.innerHTML = `<div style="color:var(--text-muted); font-size:13px;">${t('Registe primeiro um pet.')}</div>`;
    if (list) list.innerHTML = `<div style="text-align:center; padding:40px; color:var(--text-dim);">${t('Sem registos')}</div>`;
    return;
  }

  if (nameEl) nameEl.textContent = pet.name;
  
  if (chips) {
    chips.innerHTML = '';
    PetStore.getPets().forEach(p => {
      chips.innerHTML += `
        <div class="pet-chip-item ${p.id === pet.id ? 'active' : ''}" onclick="selectPet('${p.id}')">
          <span>${p.species === 'CAT' ? '🐱' : '🐶'}</span>
          <span style="font-weight:600;">${p.name}</span>
        </div>`;
    });
  }

  if (list) {
    list.innerHTML = '';
    const symptoms = (pet.symptomLogs || []).sort((a, b) => new Date(b.date) - new Date(a.date));

    if (symptoms.length === 0) {
      list.innerHTML = `<div style="text-align:center; padding:40px; color:var(--text-dim);">${t('Sem registos')}</div>`;
      return;
    }

    symptoms.forEach(log => {
      const d = new Date(log.date).toLocaleString();
      const scoreDesc = Clinical.fecalScoreDescription(log.fecalScore);
      const badgeColor = log.fecalScore >= 6 ? 'var(--danger-soft)' : log.fecalScore >= 4 ? 'var(--warning-soft)' : 'var(--success-soft)';
      const textColor = log.fecalScore >= 6 ? 'var(--danger)' : log.fecalScore >= 4 ? 'var(--warning)' : 'var(--success)';

      list.innerHTML += `
        <div class="log-card">
          <div class="log-card-header">
            <span style="font-size:12px; color:var(--text-muted);">${d}</span>
            <span class="tag-badge" style="background:${badgeColor}; color:${textColor};">
              ${t('Score Fecal WSAVA')}: ${log.fecalScore}/7
            </span>
          </div>
          <div style="font-size:13px; font-weight:600; margin-bottom:6px;">${scoreDesc}</div>
          <div style="display:flex; gap:8px; font-size:12px; color:var(--text-muted); margin-bottom:8px;">
            <span>${t('Apetite')}: <strong>${t(log.appetite || 'NORMAL')}</strong></span> •
            <span>${t('Energia')}: <strong>${t(log.energyLevel || 'NORMAL')}</strong></span> •
            <span>${t('Consumo de Água')}: <strong>${t(log.waterIntake || 'NORMAL')}</strong></span>
            ${log.vomitingEpisodes > 0 ? `• <span style="color:var(--danger); font-weight:bold;">${t('Vómito')}: ${log.vomitingEpisodes}x</span>` : ''}
          </div>
          ${log.notes ? `<div style="font-size:12px; background:rgba(255,255,255,0.03); padding:8px 12px; border-radius:var(--radius-sm);">📝 ${log.notes}</div>` : ''}
        </div>`;
    });
  }
}

function renderWalksView(pet) {
  const nameEl = document.getElementById('walks-pet-name');
  const chips = document.getElementById('walks-pet-chips');
  const list = document.getElementById('walks-logs-list');

  if (!pet) {
    if (nameEl) nameEl.textContent = t('Meus Pets');
    if (chips) chips.innerHTML = `<div style="color:var(--text-muted); font-size:13px;">${t('Registe primeiro um pet.')}</div>`;
    if (list) list.innerHTML = `<div style="text-align:center; padding:40px; color:var(--text-dim);">${t('Sem registos')}</div>`;
    return;
  }

  if (nameEl) nameEl.textContent = pet.name;
  
  if (chips) {
    chips.innerHTML = '';
    PetStore.getPets().forEach(p => {
      chips.innerHTML += `
        <div class="pet-chip-item ${p.id === pet.id ? 'active' : ''}" onclick="selectPet('${p.id}')">
          <span>${p.species === 'CAT' ? '🐱' : '🐶'}</span>
          <span style="font-weight:600;">${p.name}</span>
        </div>`;
    });
  }

  if (list) {
    list.innerHTML = '';
    const walks = (pet.walkSessions || []).sort((a, b) => new Date(b.date) - new Date(a.date));

    if (walks.length === 0) {
      list.innerHTML = `<div style="text-align:center; padding:40px; color:var(--text-dim);">${t('Sem registos')}</div>`;
      return;
    }

    walks.forEach(wk => {
      const mins = Math.round(wk.durationSeconds / 60);
      const d = new Date(wk.date).toLocaleDateString();
      list.innerHTML += `
        <div class="log-card">
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div>
              <div style="font-size:15px; font-weight:700;">🐾 ${mins} ${t('minutos')} (${t(wk.intensity) || wk.intensity || 'MODERATE'})</div>
              <div style="font-size:12px; color:var(--text-muted);">${d} • ${(wk.distanceMeters || 0) / 1000} km</div>
            </div>
            <div style="font-size:16px; font-weight:800; color:var(--accent);">🔥 ${Math.round(wk.caloriesBurned)} kcal</div>
          </div>
        </div>`;
    });
  }
}

function renderCalculatorView(pet) {
  const weightInput = document.getElementById('calc-weight');
  if (weightInput && pet && (!weightInput.value || weightInput.value === '10')) {
    weightInput.value = pet.currentWeightKg;
  }
  recomputeNutrition();
}

function recomputeNutrition() {
  const weightInput = document.getElementById('calc-weight');
  const weight = parseFloat(weightInput?.value) || 10;
  const species = document.getElementById('calc-species')?.value || 'DOG';
  const stage = document.getElementById('calc-stage')?.value || 'MAINTENANCE';
  const neutered = document.getElementById('calc-neutered')?.value === 'true';
  const kcalPerKg = parseFloat(document.getElementById('calc-kcal')?.value) || 3750;

  const result = Clinical.calculateDER(weight, species, stage, neutered);
  const grams = Clinical.calculateFoodGrams(result.der, kcalPerKg);

  const rerEl = document.getElementById('calc-rer-val');
  const factorEl = document.getElementById('calc-factor-val');
  const derEl = document.getElementById('calc-der-val');
  const gramsEl = document.getElementById('calc-grams-val');

  if (rerEl) rerEl.textContent = `${result.rer} kcal/dia`;
  if (factorEl) factorEl.textContent = `${result.factor}x`;
  if (derEl) derEl.textContent = `${result.der} kcal/dia`;
  if (gramsEl) gramsEl.textContent = `${grams} g/dia`;
}

// --------------------------------------------------------------------------
// 5. Navigation Tab Switcher
// --------------------------------------------------------------------------
function switchTab(tabId) {
  document.querySelectorAll('.tab-page').forEach(page => page.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));
  document.querySelectorAll('.mobile-nav-item').forEach(item => item.classList.remove('active'));

  const targetPage = document.getElementById(`tab-${tabId}`);
  if (targetPage) targetPage.classList.add('active');

  const navLink = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
  if (navLink) navLink.classList.add('active');

  const mobileNavLink = document.querySelector(`.mobile-nav-item[data-tab="${tabId}"]`);
  if (mobileNavLink) mobileNavLink.classList.add('active');

  renderAllViews();
}

// --------------------------------------------------------------------------
// 6. Modal Dialogs & Actions
// --------------------------------------------------------------------------
function openModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.add('open');
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.remove('open');
}

function saveNewPet() {
  const name = document.getElementById('new-pet-name')?.value.trim();
  const species = document.getElementById('new-pet-species')?.value || 'DOG';
  const breed = document.getElementById('new-pet-breed')?.value.trim() || (species === 'CAT' ? 'Gato' : 'Cão');
  const weight = parseFloat(document.getElementById('new-pet-weight')?.value) || 5.0;
  const targetWeight = parseFloat(document.getElementById('new-pet-target-weight')?.value) || weight;
  const neutered = document.getElementById('new-pet-neutered')?.value === 'true';

  if (!name) {
    alert(t('Por favor, insira o nome do pet.'));
    return;
  }

  const newPet = {
    id: 'pet_' + Date.now(),
    name: name,
    species: species,
    breed: breed,
    birthDate: new Date().toISOString().slice(0, 10),
    currentWeightKg: weight,
    targetWeightKg: targetWeight,
    neutered: neutered,
    activityLevel: 'MODERATE',
    weightLogs: [
      { id: 'w_' + Date.now(), date: new Date().toISOString(), weightKg: weight, notes: t('Registo inicial') }
    ],
    symptomLogs: [],
    walkSessions: [],
    vaccineLogs: []
  };

  PetStore.addPet(newPet);
  closeModal('modal-add-pet');
}

function saveWeightLog() {
  const pet = getActivePet();
  if (!pet) return;
  const weight = parseFloat(document.getElementById('new-weight-val').value);
  const note = document.getElementById('new-weight-note').value;
  if (!weight || weight <= 0) return alert(t('Peso inválido.'));

  const log = { id: 'w_' + Date.now(), date: new Date().toISOString(), weightKg: weight, notes: note };
  pet.weightLogs = pet.weightLogs || [];
  pet.weightLogs.push(log);
  pet.currentWeightKg = weight;
  PetStore.updatePet(pet);
  closeModal('modal-add-weight');
}

function saveSymptomLog() {
  const pet = getActivePet();
  if (!pet) return;
  const fecalScore = parseInt(document.getElementById('new-fecal-score').value, 10);
  const vomiting = parseInt(document.getElementById('new-vomiting-val').value, 10) || 0;
  const appetite = document.getElementById('new-appetite-val').value;
  const energy = document.getElementById('new-energy-val').value;
  const water = document.getElementById('new-water-val').value;
  const notes = document.getElementById('new-symptom-notes').value;

  const log = {
    id: 's_' + Date.now(),
    date: new Date().toISOString(),
    fecalScore: fecalScore,
    vomitingEpisodes: vomiting,
    appetite: appetite,
    energyLevel: energy,
    waterIntake: water,
    notes: notes
  };

  pet.symptomLogs = pet.symptomLogs || [];
  pet.symptomLogs.push(log);
  PetStore.updatePet(pet);
  closeModal('modal-add-symptom');
}

// --------------------------------------------------------------------------
// 7. Initialization & PWA Support
// --------------------------------------------------------------------------
function init() {
  const selector = document.getElementById('language-select');
  if (selector) {
    selector.innerHTML = LANGUAGES.map(l => 
      `<option value="${l.code}" ${l.code === currentLang ? 'selected' : ''}>${l.flag} ${l.name}</option>`
    ).join('');
    selector.addEventListener('change', (e) => setLanguage(e.target.value));
  }

  loadTranslations().then(() => {
    renderAllViews();
  });

  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js')
      .then(reg => console.log('[PWA] Service Worker registered:', reg.scope))
      .catch(err => console.warn('[PWA] Service Worker registration failed:', err));
  }

  let deferredPrompt;
  window.addEventListener('beforeinstallprompt', (e) => {
    e.preventDefault();
    deferredPrompt = e;
    const installBtn = document.getElementById('pwa-install-btn');
    if (installBtn) {
      installBtn.style.display = 'inline-flex';
      installBtn.addEventListener('click', () => {
        deferredPrompt.prompt();
        deferredPrompt.userChoice.then(() => {
          installBtn.style.display = 'none';
          deferredPrompt = null;
        });
      });
    }
  });
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', init);
} else {
  init();
}
