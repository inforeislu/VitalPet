---
description: Obrigatoriedade estrita de suporte aos 8 idiomas em todo o código VitalPet
globs: ["**/*.kt", "**/*.xml", "**/*.json"]
alwaysApply: true
---

# Regra de Ouro: Suporte Obrigatório aos 8 Idiomas

Qualquer novo texto, tela, mensagem de erro, diálogo ou funcionalidade adicionada ao VitalPet Android DEVE conter traduções para todos os 8 idiomas suportados:
1. `pt` — Português (PT) [Padrão]
2. `en` — English (US)
3. `es` — Español
4. `fr` — Français
5. `de` — Deutsch
6. `it` — Italiano
7. `nl` — Nederlands
8. `lb` — Lëtzebuergesch

Sempre verificar e atualizar:
- `app/src/main/assets/translations.json`
- `app/src/main/res/values-*/strings.xml`

Execute sempre `python3 scripts/validate_languages.py` para validar conformidade.
