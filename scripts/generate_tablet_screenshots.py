#!/usr/bin/env python3
import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

def generate_tablet_screenshots():
    out_dir = "playstore_assets/tablet_screenshots"
    os.makedirs(out_dir, exist_ok=True)
    
    src_dir = "/Users/renatoreis/Desktop/VitalPet - Special/AppStore_Mac_Screenshots_2880x1800"
    
    items = [
        ("01_Central_Utilitarios.png", "Central de Utilitários Clínicos", "Calculadoras WSAVA, BARF e Ferramentas Veterinárias"),
        ("02_Ficha_Clinica_Geral.png", "Ficha Clínica & Gestão de Pets", "Acompanhamento Biométrico, Curvas de Peso e Histórico"),
        ("03_Guia_SOS_Toxinas.png", "Guia SOS de Toxinas & Plantas", "Primeiros Socorros Imediatos e Sintomatologia"),
        ("04_Enciclopedia_Racas.png", "Enciclopédia de Raças & Quiz", "Predisposições Genéticas e Compatibilidade"),
        ("05_Analisador_Rotulos.png", "Analisador Nutricional de Rótulos", "Cálculo de Matéria Seca (DM) e Carboidratos Ocultos"),
        ("06_Jejum_Clinico.png", "Temporizador de Jejum Cirúrgico", "Controlo Pré-Operatório com Prazos Seguros")
    ]
    
    canvas_w = 1920
    canvas_h = 1080
    
    # Load fonts
    font_paths = [
        "/System/Library/Fonts/SFPro.ttf",
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
        "/System/Library/Fonts/Supplemental/Arial.ttf"
    ]
    title_font = None
    sub_font = None
    for fp in font_paths:
        if os.path.exists(fp):
            try:
                title_font = ImageFont.truetype(fp, 38)
                sub_font = ImageFont.truetype(fp, 22)
                break
            except Exception:
                continue
    if title_font is None:
        title_font = ImageFont.load_default()
        sub_font = ImageFont.load_default()
        
    for idx, (filename, title, subtitle) in enumerate(items, 1):
        src_path = os.path.join(src_dir, filename)
        if not os.path.exists(src_path):
            print(f"Aviso: {src_path} não encontrado, pulando...")
            continue
            
        screenshot = Image.open(src_path).convert("RGBA")
        
        # Create base background (rich dark clinical slate/teal)
        bg = Image.new("RGBA", (canvas_w, canvas_h), (11, 19, 32, 255))
        draw = ImageDraw.Draw(bg)
        
        # Subtle top-to-bottom background gradient
        for y in range(canvas_h):
            ratio = y / canvas_h
            r = int(15 * (1 - ratio) + 11 * ratio)
            g = int(35 * (1 - ratio) + 19 * ratio)
            b = int(45 * (1 - ratio) + 32 * ratio)
            draw.line([(0, y), (canvas_w, y)], fill=(r, g, b, 255))
            
        # Glowing accent circles in top corners
        glow = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        gdraw = ImageDraw.Draw(glow)
        gdraw.ellipse([(canvas_w // 2 - 400, -200), (canvas_w // 2 + 400, 300)], fill=(20, 184, 166, 35))
        glow = glow.filter(ImageFilter.GaussianBlur(60))
        bg = Image.alpha_composite(bg, glow)
        
        # Draw Title & Subtitle at top
        tdraw = ImageDraw.Draw(bg)
        
        # Title centering
        t_bbox = title_font.getbbox(title)
        tw = t_bbox[2] - t_bbox[0]
        tdraw.text(((canvas_w - tw) // 2, 45), title, fill=(255, 255, 255, 255), font=title_font)
        
        s_bbox = sub_font.getbbox(subtitle)
        sw = s_bbox[2] - s_bbox[0]
        tdraw.text(((canvas_w - sw) // 2, 95), subtitle, fill=(153, 246, 228, 220), font=sub_font)
        
        # Tablet Frame dimensions
        # Target area for tablet: y from 155 to 1040 (height ~885)
        max_scr_w = 1600
        max_scr_h = 860
        
        orig_w, orig_h = screenshot.size
        scale = min(max_scr_w / orig_w, max_scr_h / orig_h)
        new_w = int(orig_w * scale)
        new_h = int(orig_h * scale)
        
        screenshot_resized = screenshot.resize((new_w, new_h), Image.Resampling.LANCZOS)
        
        # Draw tablet bezel / shadow
        bezel_padding = 14
        bx0 = (canvas_w - new_w) // 2 - bezel_padding
        by0 = 150 - bezel_padding
        bx1 = bx0 + new_w + bezel_padding * 2
        by1 = by0 + new_h + bezel_padding * 2
        
        # Shadow
        shadow = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        sdraw = ImageDraw.Draw(shadow)
        sdraw.rounded_rectangle([bx0 - 15, by0 - 5, bx1 + 15, by1 + 25], radius=28, fill=(0, 0, 0, 140))
        shadow = shadow.filter(ImageFilter.GaussianBlur(25))
        bg = Image.alpha_composite(bg, shadow)
        
        # Draw sleek tablet bezel (dark graphite with teal border)
        bdraw = ImageDraw.Draw(bg)
        bdraw.rounded_rectangle([bx0, by0, bx1, by1], radius=22, fill=(30, 41, 59, 255), outline=(51, 65, 85, 255), width=2)
        
        # Paste real app screenshot
        bg.paste(screenshot_resized, (bx0 + bezel_padding, by0 + bezel_padding), screenshot_resized)
        
        # Save final 16:9 tablet screenshot
        out_file = os.path.join(out_dir, f"tablet_screenshot_{idx}.png")
        final_img = bg.convert("RGB")
        final_img.save(out_file, "PNG", optimize=True)
        print(f"Salvo: {out_file} ({canvas_w}x{canvas_h})")

if __name__ == "__main__":
    generate_tablet_screenshots()
