package com.inforeis.vitalpet.desktop.services

import com.inforeis.vitalpet.models.Pet
import java.awt.Desktop
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DesktopReportService {

    fun generateAndOpenClinicalReport(pet: Pet): File {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val nowFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val vaccineRows = if (pet.vaccineLogs.isEmpty()) {
            "<tr><td colspan='3' style='text-align:center; color:#64748b;'>Sem registos de vacinas</td></tr>"
        } else {
            pet.vaccineLogs.sortedByDescending { it.applicationDate }.joinToString("") { v ->
                val nextDate = if (v.nextDueDate != null) dateFormat.format(v.nextDueDate) else "N/A"
                "<tr><td><strong>${v.vaccineName}</strong></td><td>${dateFormat.format(v.applicationDate)}</td><td>$nextDate</td></tr>"
            }
        }

        val visitRows = if (pet.medicalVisits.isEmpty()) {
            "<tr><td colspan='4' style='text-align:center; color:#64748b;'>Sem registos de consultas médicas</td></tr>"
        } else {
            pet.medicalVisits.sortedByDescending { it.date }.joinToString("") { m ->
                val diag = m.diagnosis ?: "-"
                val vet = m.veterinarianName ?: m.clinicName
                "<tr><td>${dateFormat.format(m.date)}</td><td><strong>${m.reason}</strong></td><td>$diag</td><td>$vet</td></tr>"
            }
        }

        val weightRows = pet.weightLogs.sortedByDescending { it.date }.take(8).joinToString("") { w ->
            "<tr><td>${dateFormat.format(w.date)}</td><td><strong>${w.weightKg} kg</strong></td></tr>"
        }

        val htmlContent = """
            <!DOCTYPE html>
            <html lang="pt">
            <head>
                <meta charset="UTF-8">
                <title>Prontuário Clínico - ${pet.name} (VitalPet)</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                        color: #0f172a;
                        background: #fff;
                        margin: 0;
                        padding: 30px;
                    }
                    .header {
                        border-bottom: 2px solid #0d9488;
                        padding-bottom: 16px;
                        margin-bottom: 24px;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }
                    .brand {
                        font-size: 24px;
                        font-weight: 800;
                        color: #0d9488;
                    }
                    .subtitle {
                        font-size: 12px;
                        color: #64748b;
                    }
                    .badge {
                        background: #ccfbf1;
                        color: #0f766e;
                        font-weight: bold;
                        padding: 6px 14px;
                        border-radius: 20px;
                        font-size: 13px;
                    }
                    .grid {
                        display: grid;
                        grid-template-columns: repeat(4, 1fr);
                        gap: 14px;
                        margin-bottom: 24px;
                    }
                    .metric-box {
                        background: #f8fafc;
                        border: 1px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 12px;
                    }
                    .metric-label {
                        font-size: 11px;
                        color: #64748b;
                        text-transform: uppercase;
                        font-weight: 600;
                    }
                    .metric-val {
                        font-size: 18px;
                        font-weight: 700;
                        color: #0d9488;
                        margin-top: 4px;
                    }
                    h2 {
                        font-size: 15px;
                        color: #1e293b;
                        border-left: 4px solid #0d9488;
                        padding-left: 8px;
                        margin-top: 24px;
                        margin-bottom: 12px;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 13px;
                        margin-bottom: 20px;
                    }
                    th, td {
                        padding: 8px 12px;
                        border-bottom: 1px solid #e2e8f0;
                        text-align: left;
                    }
                    th {
                        background: #f1f5f9;
                        color: #475569;
                    }
                    .footer {
                        margin-top: 40px;
                        border-top: 1px solid #e2e8f0;
                        padding-top: 12px;
                        font-size: 11px;
                        color: #94a3b8;
                        display: flex;
                        justify-content: space-between;
                    }
                    @media print {
                        body { padding: 0; }
                        .no-print { display: none; }
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <div>
                        <div class="brand">🐾 VitalPet — Prontuário Veterinário</div>
                        <div class="subtitle">Documento Oficial Clínico & Nutricional</div>
                    </div>
                    <div>
                        <span class="badge">Emissão: $nowFormatted</span>
                    </div>
                </div>

                <div class="grid">
                    <div class="metric-box">
                        <div class="metric-label">Paciente / Espécie</div>
                        <div class="metric-val">${pet.name} (${pet.species.displayName})</div>
                    </div>
                    <div class="metric-box">
                        <div class="metric-label">Raça & Idade</div>
                        <div class="metric-val">${pet.breed} • ${pet.ageInYears} anos</div>
                    </div>
                    <div class="metric-box">
                        <div class="metric-label">Peso Atual / Meta</div>
                        <div class="metric-val">${pet.currentWeightKg} kg</div>
                    </div>
                    <div class="metric-box">
                        <div class="metric-label">Condição Corporal (BCS)</div>
                        <div class="metric-val">BCS ${pet.bcs}/9</div>
                    </div>
                </div>

                <div class="grid">
                    <div class="metric-box">
                        <div class="metric-label">RER (Metabolismo Basal)</div>
                        <div class="metric-val">${pet.rer.toInt()} kcal/dia</div>
                    </div>
                    <div class="metric-box">
                        <div class="metric-label">DER (Necessidade Total)</div>
                        <div class="metric-val">${pet.der.toInt()} kcal/dia</div>
                    </div>
                    <div class="metric-box">
                        <div class="metric-label">Nível de Atividade</div>
                        <div class="metric-val">${pet.activityLevel.displayName}</div>
                    </div>
                    <div class="metric-box">
                        <div class="metric-label">Microchip</div>
                        <div class="metric-val">${pet.microchipNumber ?: "Não registado"}</div>
                    </div>
                </div>

                <h2>📋 Histórico Vacinal</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Vacina</th>
                            <th>Data de Aplicação</th>
                            <th>Próximo Reforço</th>
                        </tr>
                    </thead>
                    <tbody>
                        $vaccineRows
                    </tbody>
                </table>

                <h2>🩺 Consultas e Atendimentos Clínicos</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Data</th>
                            <th>Motivo da Consulta</th>
                            <th>Diagnóstico</th>
                            <th>Médico / Clínica</th>
                        </tr>
                    </thead>
                    <tbody>
                        $visitRows
                    </tbody>
                </table>

                <h2>⚖️ Últimas Pesagens Registadas</h2>
                <table style="width: 50%;">
                    <thead>
                        <tr>
                            <th>Data</th>
                            <th>Peso (kg)</th>
                        </tr>
                    </thead>
                    <tbody>
                        $weightRows
                    </tbody>
                </table>

                <div class="footer">
                    <div>VitalPet Medical Edition — Padrão WSAVA (World Small Animal Veterinary Association)</div>
                    <div>Página 1 de 1</div>
                </div>

                <script>
                    window.onload = function() {
                        // Opcional: abrir impressão automática se aberto pelo utilizador
                    };
                </script>
            </body>
            </html>
        """.trimIndent()

        val tempFile = File.createTempFile("vitalpet_prontuario_${pet.name.lowercase()}_", ".html")
        tempFile.writeText(htmlContent, Charsets.UTF_8)

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(tempFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    fun generateAndOpenDigitalPassport(pet: Pet): File {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthStr = if (pet.birthDate != null) dateFormat.format(pet.birthDate) else "N/A"
        val rabiesVaccine = pet.vaccineLogs.find { it.vaccineName.contains("rábica", ignoreCase = true) || it.vaccineName.contains("rabies", ignoreCase = true) }
        val rabiesStatus = if (rabiesVaccine != null) "Válida (Aplicada em ${dateFormat.format(rabiesVaccine.applicationDate)})" else "Não Registada"

        val htmlContent = """
            <!DOCTYPE html>
            <html lang="pt">
            <head>
                <meta charset="UTF-8">
                <title>Passaporte Veterinário - ${pet.name} (VitalPet)</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                        color: #0f172a;
                        background: #f8fafc;
                        margin: 0;
                        padding: 40px;
                        display: flex;
                        justify-content: center;
                    }
                    .passport-container {
                        width: 600px;
                        background: white;
                        border-radius: 20px;
                        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
                        border: 2px solid #0d9488;
                        overflow: hidden;
                    }
                    .passport-header {
                        background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%);
                        color: white;
                        padding: 24px;
                        text-align: center;
                    }
                    .passport-header h1 {
                        margin: 0;
                        font-size: 22px;
                        letter-spacing: 1px;
                        text-transform: uppercase;
                    }
                    .passport-header .subtitle {
                        margin-top: 4px;
                        font-size: 12px;
                        opacity: 0.9;
                    }
                    .passport-body {
                        padding: 28px;
                    }
                    .pet-hero {
                        display: flex;
                        gap: 20px;
                        align-items: center;
                        margin-bottom: 24px;
                        border-bottom: 1px solid #e2e8f0;
                        padding-bottom: 20px;
                    }
                    .pet-avatar {
                        width: 80px;
                        height: 80px;
                        background: #ccfbf1;
                        border-radius: 16px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 40px;
                        border: 2px solid #0d9488;
                    }
                    .pet-title h2 {
                        margin: 0 0 6px 0;
                        font-size: 24px;
                        color: #0f172a;
                    }
                    .pet-title .species-badge {
                        background: #0d9488;
                        color: white;
                        padding: 3px 10px;
                        border-radius: 9999px;
                        font-size: 11px;
                        font-weight: bold;
                        text-transform: uppercase;
                    }
                    .grid-data {
                        display: grid;
                        grid-template-columns: repeat(2, 1fr);
                        gap: 14px;
                        margin-bottom: 24px;
                    }
                    .data-item {
                        background: #f8fafc;
                        padding: 10px 14px;
                        border-radius: 10px;
                        border: 1px solid #e2e8f0;
                    }
                    .data-label {
                        font-size: 10px;
                        font-weight: 700;
                        text-transform: uppercase;
                        color: #64748b;
                        margin-bottom: 3px;
                    }
                    .data-value {
                        font-size: 14px;
                        font-weight: 600;
                        color: #0f172a;
                    }
                    .clinical-banner {
                        background: #f0fdf4;
                        border: 1px solid #86efac;
                        border-radius: 12px;
                        padding: 14px;
                        margin-bottom: 20px;
                    }
                    .clinical-banner h3 {
                        margin: 0 0 6px 0;
                        font-size: 13px;
                        color: #166534;
                    }
                    .clinical-banner p {
                        margin: 0;
                        font-size: 12px;
                        color: #15803d;
                    }
                    .passport-footer {
                        text-align: center;
                        font-size: 11px;
                        color: #94a3b8;
                        border-top: 1px dashed #cbd5e1;
                        padding-top: 14px;
                    }
                    @media print {
                        body {
                            background: white;
                            padding: 0;
                        }
                        .passport-container {
                            box-shadow: none;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="passport-container">
                    <div class="passport-header">
                        <h1>Passaporte Veterinário & ID</h1>
                        <div class="subtitle">Documento de Identificação Clínica — VitalPet Universal</div>
                    </div>
                    <div class="passport-body">
                        <div class="pet-hero">
                            <div class="pet-avatar">
                                ${if (pet.species.name == "CAT") "🐱" else "🐶"}
                            </div>
                            <div class="pet-title">
                                <h2>${pet.name}</h2>
                                <span class="species-badge">${pet.species.displayName} • ${pet.breed}</span>
                            </div>
                        </div>

                        <div class="grid-data">
                            <div class="data-item">
                                <div class="data-label">Número de Microchip</div>
                                <div class="data-value">${pet.microchipNumber?.ifBlank { "Sem registo" } ?: "Sem registo"}</div>
                            </div>
                            <div class="data-item">
                                <div class="data-label">Data de Nascimento</div>
                                <div class="data-value">$birthStr</div>
                            </div>
                            <div class="data-item">
                                <div class="data-label">Sexo & Esterilização</div>
                                <div class="data-value">${pet.gender.displayName} • ${pet.neuterStatus.displayName}</div>
                            </div>
                            <div class="data-item">
                                <div class="data-label">Peso Registado</div>
                                <div class="data-value">${pet.currentWeightKg} kg</div>
                            </div>
                            <div class="data-item">
                                <div class="data-label">Condição Corporal (BCS)</div>
                                <div class="data-value">${pet.bcsCategory}</div>
                            </div>
                            <div class="data-item">
                                <div class="data-label">Necessidade Diária (DER)</div>
                                <div class="data-value">${String.format("%.0f", pet.der)} kcal/dia</div>
                            </div>
                        </div>

                        <div class="clinical-banner">
                            <h3>💉 Situação Antirrábica</h3>
                            <p>$rabiesStatus</p>
                        </div>

                        <div class="passport-footer">
                            Documento emitido via VitalPet Desktop. Válido para controle clínico e identificação de rotina.
                        </div>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val tempFile = File.createTempFile("vitalpet_passaporte_${pet.name.lowercase()}_", ".html")
        tempFile.writeText(htmlContent, Charsets.UTF_8)

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(tempFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }
}

