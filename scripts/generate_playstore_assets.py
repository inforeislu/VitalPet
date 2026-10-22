#!/usr/bin/env python3
import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

def create_playstore_assets():
    os.makedirs("playstore_assets", exist_ok=True)
    
    app_icon_src = "app/src/main/res/drawable/appicon.png"
    logo_src = "app/src/main/res/drawable/logo.png"
    
    # 1. Generate 512x512 Icon
    print("Gerando icone 512x512...")
    icon_img = Image.open(app_icon_src).convert("RGBA")
    icon_512 = icon_img.resize((512, 512), Image.Resampling.LANCZOS)
    icon_dest = "playstore_assets/icon_512x512.png"
    icon_512.save(icon_dest, "PNG", optimize=True)
    print(f"Salvo: {icon_dest} ({os.path.getsize(icon_dest) / 1024:.1f} KB)")
    
    # 2. Generate 1024x500 Feature Graphic
    print("Gerando grafico de funcionalidades 1024x500...")
    width, height = 1024, 500
    feature_img = Image.new("RGBA", (width, height), (15, 23, 42, 255)) # Dark slate
    
    # Create smooth radial/linear gradient in Teal & Dark Navy
    draw = ImageDraw.Draw(feature_img)
    for y in range(height):
        ratio = y / height
        # Gradient from dark teal (15, 118, 110) to dark slate (15, 23, 42)
        r = int(15 * (1 - ratio) + 15 * ratio)
        g = int(118 * (1 - ratio) + 23 * ratio)
        b = int(110 * (1 - ratio) + 42 * ratio)
        draw.line([(0, y), (width, y)], fill=(r, g, b, 255))
        
    # Draw soft glowing circles for depth
    glow_layer = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow_layer)
    glow_draw.ellipse([(-100, -100), (450, 450)], fill=(45, 212, 191, 50))
    glow_draw.ellipse([(650, 150), (1150, 650)], fill=(13, 148, 136, 60))
    glow_layer = glow_layer.filter(ImageFilter.GaussianBlur(50))
    feature_img = Image.alpha_composite(feature_img, glow_layer)
    
    # Add Logo
    logo = Image.open(logo_src).convert("RGBA")
    # Resize logo to fit nicely on the left / center
    logo_size = 280
    logo_resized = logo.resize((logo_size, logo_size), Image.Resampling.LANCZOS)
    
    # Paste logo at (100, 110)
    logo_x = 90
    logo_y = (height - logo_size) // 2
    feature_img.paste(logo_resized, (logo_x, logo_y), logo_resized)
    
    # Draw Typography
    text_draw = ImageDraw.Draw(feature_img)
    
    # Load system font
    title_font = None
    sub_font = None
    tag_font = None
    
    font_paths = [
        "/System/Library/Fonts/SFPro.ttf",
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
        "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/Library/Fonts/Arial.ttf"
    ]
    for fp in font_paths:
        if os.path.exists(fp):
            try:
                title_font = ImageFont.truetype(fp, 64)
                sub_font = ImageFont.truetype(fp, 26)
                tag_font = ImageFont.truetype(fp, 18)
                break
            except Exception:
                continue
                
    if title_font is None:
        title_font = ImageFont.load_default()
        sub_font = ImageFont.load_default()
        tag_font = ImageFont.load_default()
        
    # Text coordinates
    tx = logo_x + logo_size + 50
    ty_title = 150
    ty_sub = ty_title + 75
    ty_tags = ty_sub + 45
    
    # Draw Title: VitalPet
    text_draw.text((tx, ty_title), "VitalPet", fill=(255, 255, 255, 255), font=title_font)
    
    # Draw Subtitle
    text_draw.text((tx, ty_sub), "Saúde, Nutrição WSAVA & Cuidados Clínicos", fill=(204, 251, 241, 240), font=sub_font)
    
    # Draw badges
    badges = ["🐾 Cães & Gatos", "🥗 Calculadoras BARF", "🚨 Hospitais 24h", "📄 Relatórios PDF"]
    bx = tx
    by = ty_tags + 15
    for b in badges:
        # Measure text
        bbox = tag_font.getbbox(b)
        bw = (bbox[2] - bbox[0]) + 20
        bh = (bbox[3] - bbox[1]) + 14
        
        # Draw pill badge
        text_draw.rounded_rectangle([bx, by, bx + bw, by + bh], radius=8, fill=(15, 118, 110, 160), outline=(45, 212, 191, 180), width=1)
        text_draw.text((bx + 10, by + 4), b, fill=(240, 253, 250, 255), font=tag_font)
        bx += bw + 12
        if bx > width - 50:
            break
            
    feature_dest = "playstore_assets/feature_graphic_1024x500.png"
    # Convert RGBA to RGB for feature graphic without alpha issues
    feature_final = feature_img.convert("RGB")
    feature_final.save(feature_dest, "PNG", optimize=True)
    print(f"Salvo: {feature_dest} ({os.path.getsize(feature_dest) / 1024:.1f} KB)")
    print("Assets gerados com sucesso!")

if __name__ == "__main__":
    create_playstore_assets()
