# VitalPet Android — Regras do Repositório e Diretrizes para Agentes (AGENTS.md)

Este repositório contém a versão Android da aplicação **VitalPet**, desenvolvida em Kotlin, Jetpack Compose e Room/DataStore para Gestão Nutricional e Clínica Veterinária.

---

## 🌐 REGRA DE OURO: Suporte Obrigatório aos 8 Idiomas

> **REGRA MANDATÓRIA PERMANENTE**:
> **Qualquer texto, funcionalidade, modelo, enum, tela/ecrã, botão, mensagem de erro, notificação, relatório ou utilitário que seja adicionado ou editado no projeto DEVE OBRIGATORIAMENTE conter traduções completas para todos os 8 idiomas suportados.**
>
> **NUNCA adicionar texto em formato hardcoded (literal no código) sem registo no sistema de idiomas.**

### Os 8 Idiomas Oficiais do Projeto:
1. **`pt`** — Português (PT) *(Idioma base / padrão)*
2. **`en`** — English (US)
3. **`es`** — Español
4. **`fr`** — Français
5. **`de`** — Deutsch
6. **`it`** — Italiano
7. **`nl`** — Nederlands
8. **`lb`** — Lëtzebuergesch

---

## 🛠️ Onde e Como Atualizar as Traduções

Ao adicionar ou editar qualquer string visível para o utilizador:

1. **`app/src/main/assets/translations.json`**:
   - Manter o ficheiro mestre atualizado com todas as 8 traduções por chave.
   - Utilizado pelo `LanguageManager` em tempo real para alternância dinâmica de idioma sem reinicialização forçada da aplicação.

2. **Ficheiros de Recursos XML (`app/src/main/res/values-*/strings.xml`)**:
   - Manter os 8 ficheiros sincronizados com as mesmas chaves:
     - `res/values/strings.xml` (Português - Padrão)
     - `res/values-en/strings.xml` (Inglês)
     - `res/values-es/strings.xml` (Espanhol)
     - `res/values-fr/strings.xml` (Francês)
     - `res/values-de/strings.xml` (Alemão)
     - `res/values-it/strings.xml` (Italiano)
     - `res/values-nl/strings.xml` (Holandês)
     - `res/values-lb/strings.xml` (Luxemburguês)

3. **Utilização no Código Kotlin / Jetpack Compose**:
   - Para strings com `LanguageManager`:
     ```kotlin
     Text(text = "Meus Pets".localized())
     // ou com suporte ao idioma selecionado no estado:
     Text(text = LanguageManager.get("Meus Pets", currentLang))
     ```
   - Ou via `stringResource(R.string.key_name)` nos layouts padrão do Android.

4. **Validação Obrigatória Automatizada**:
   - Antes de concluir qualquer tarefa ou pull request, execute o script de validação de idiomas:
     ```bash
     python3 scripts/validate_languages.py
     ```
     ou o teste unitário:
     ```bash
     ./gradlew testDebugUnitTest --tests "com.inforeis.vitalpet.LanguageValidationTest"
     ```
   - O teste falhará imediatamente se faltar alguma tradução em qualquer um dos 8 idiomas!

---

## 🧱 Arquitetura e Boas Práticas do VitalPet Android
- **UI Framework**: Jetpack Compose com Material Design 3 (Material3).
- **Linguagem**: Kotlin 2.x+.
- **Persistência & Estado**: Room Database / SharedPreferences / StateFlow.
- **Cálculos Clínicos**: Diretrizes oficiais WSAVA (World Small Animal Veterinary Association) e NRC:
  - Fórmulas de RER (Resting Energy Requirement): `70 * (peso ^ 0.75)`
  - Fatores DER (Daily Energy Requirement) por espécie, estado reprodutivo e nível de atividade
  - Avaliação de Condição Corporal (BCS escala 1 a 9)
  - Cálculos de Fluidoterapia e reposição de desidratação
  - Cálculos de Dosagem Farmacológica (mg/kg)
  - Temporizador de Jejum Pré-Operatório com estágios fisiológicos
  - Proporções BARF canina e felina
  - Analisador de Rótulos de ração (Matéria Seca e NFE / Carboidratos Ocultos)
- **Identidade Visual**: Tema premium escuro e claro, paleta de cores teal/blue/amber clínica, cantos suaves e micro-interações responsivas.
