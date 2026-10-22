# VitalPet Android 🐾

Versão nativa Android da aplicação **VitalPet** para Gestão Nutricional e Clínica Veterinária, construída com **Kotlin**, **Jetpack Compose**, **Material 3** e padrões clínicos oficiais **WSAVA / NRC**.

---

## 🌐 Suporte Obrigatório aos 8 Idiomas Oficiais

> **REGRA MANDATÓRIA PERMANENTE**:
> Todas as funcionalidades, interfaces, botões e mensagens do VitalPet devem ser fornecidas e mantidas nos 8 idiomas oficiais:
> 1. 🇵🇹 **`pt`** — Português (PT) *(Padrão)*
> 2. 🇺🇸 **`en`** — English (US)
> 3. 🇪🇸 **`es`** — Español
> 4. 🇫🇷 **`fr`** — Français
> 5. 🇩🇪 **`de`** — Deutsch
> 6. 🇮🇹 **`it`** — Italiano
> 7. 🇳🇱 **`nl`** — Nederlands
> 8. 🇱🇺 **`lb`** — Lëtzebuergesch

### Validação de Idiomas
Para verificar a integridade e conformidade de traduções em todo o projeto:
```bash
python3 scripts/validate_languages.py
```
ou via testes unitários:
```bash
./gradlew testDebugUnitTest
```

---

## 🏥 Funcionalidades Principais

- **Gestão de Animais de Companhia**:
  - Cadastro completo de cães e gatos (raça, peso atual, peso alvo, sexo, esterilização, microchip, alergias e condições crónicas).
  - Cálculo instantâneo de **RER** (Taxa Metabólica Basal) e **DER** (Necessidade Calórica Diária) segundo fórmulas WSAVA.
  - Conversão de idade para anos humanos e determinação de estágios de vida (AAHA).
- **Central de Utilitários Veterinários**:
  - **Calculadora BARF**: Distribuição biológica de carnes, ossos carnudos, vísceras e vegetais.
  - **Analisador de Rótulos**: Cálculo de matéria seca (DM), carboidratos ocultos (NFE) e densidade energética (kcal/100g).
  - **Calculadora Farmacológica**: Conversão precisa de mg/kg para volume oral ou comprimidos.
  - **Calculadora de Fluídos**: Reposição de desidratação, manutenção e taxa horária de infusão.
  - **Temporizador de Jejum**: Prazos pré-operatórios de sólidos e líquidos para procedimentos anestésicos.
  - **Passeio & Atividade**: Cronómetro de exercício com cálculo de queima calórica via MET canino.
  - **Avaliador BCS (1-9)**: Guia visual interativo de condição corporal.
  - **Base de Dados de Raças**: Catálogo com pesos e predisposições genéticas.
- **Emergência & Primeiros Socorros**:
  - Protocolos de RCP, asfixia, choque térmico e intoxicações agudas.
  - Enciclopédia de substâncias, alimentos e plantas tóxicas com sintomas e antídotos.
  - Acesso direto a chamadas de socorro e localização de hospitais 24h via GPS/Maps.
- **Cartão Digital SOS**:
  - QR Code e identificação rápida para partilha médica ou perda do animal.
- **Relatório Clínico**:
  - Exportação e partilha de sumários clínicos completos em formato texto / PDF.

## 💻 Versão Windows Desktop (`:desktop`) & Sincronização Multiplataforma

A aplicação **VitalPet** dispõe agora de suporte nativo para **Windows (Desktop)**, desenvolvido em **Compose Multiplatform (JetBrains)**, compartilhando os mesmos modelos de dados clínicos e sistema de 8 idiomas.

### Sincronização em Tempo Real (Windows ⇄ Android / iOS)
- **Sincronização Local P2P Wi-Fi (Sem Nuvem / Sem Custos)**:
  - O Windows inicia um servidor local seguro na rede Wi-Fi (porta `8920`).
  - O ecrã do Windows gera um **QR Code com PIN de segurança**.
  - No telemóvel Android (e iOS), basta abrir **Utilitários > Conectividade & Multiplataforma > Sincronizar com Computador** e introduzir o IP ou ler o QR Code.
  - O motor de mescla (`PetSyncEngine`) sincroniza registos de peso, vacinas, desparasitações, consultas médicas e dietas sem perda de dados.

### Como Compilar e Executar a Versão Desktop:
- **Executar a aplicação Desktop imediatamente**:
  ```bash
  ./gradlew :desktop:run
  ```
- **Criar o pacote distribuível da aplicação**:
  ```bash
  ./gradlew :desktop:createDistributable
  ```
- **Gerar instalador `.msi` para Windows**:
  ```bash
  ./gradlew :desktop:packageMsi
  ```
- **Gerar executável `.exe` portátil**:
  ```bash
  ./gradlew :desktop:packageExe
  ```

### Pacote Oficial da Microsoft Store (MSIX / APPX):
Para publicar na **Microsoft Store** ou instalar com um clique no Windows:
```bash
python3 scripts/package_windows_store.py
```
Gera os pacotes em `desktop/build/windows-store/VitalPet.msix` e `VitalPet.appx` com manifesto `AppxManifest.xml` e todos os 9 logótipos oficiais da store.

---

## 🌐 Versão Web Universal & PWA (Progressive Web App)

A pasta `/pwa` contém a aplicação Web Progressiva completa do **VitalPet**, compatível com qualquer navegador em Windows, Mac, Linux, iOS e Android:
- **Funcionamento Offline**: Service Worker dedicado (`sw.js`) com cache prioritária.
- **Instalável**: Suporte a *Add to Home Screen* e instalação no desktop.
- **Calculadora WSAVA & Diário Clínico**: Avaliação de RER/DER, Score Fecal WSAVA (1 a 7) e alertas clínicos.
- **Sincronização**: Importação e exportação de backups JSON compartilhados com as versões Android e Desktop.
- **Publicação Automática**: Integrada com o **GitHub Pages** através da GitHub Action `.github/workflows/deploy-pwa.yml`.

---

## 🤖 GitHub Actions & Integração Contínua (CI/CD)

O repositório inclui três workflows prontos para execução automática no GitHub:
1. **`deploy-pwa.yml`**: Faz a validação dos 8 idiomas e o deploy automático da PWA no **GitHub Pages** a cada push no branch `main`.
2. **`windows-store-package.yml`**: Compila a aplicação nativa num runner Windows 11, empacota em **MSIX/APPX**, assina digitalmente e disponibiliza o artefacto para download.
3. **`android-ci.yml`**: Valida a conformidade das 2.143 chaves nos 8 idiomas e compila o APK de depuração do Android.

---

## 🚀 Como Compilar e Executar o Android

1. Abrir a pasta `VitalPetAndroid` no **Android Studio**.
2. Sincronizar o projeto com os ficheiros Gradle.
3. Executar num dispositivo físico ou emulador com Android 8.0+ (API 26 a 36).

