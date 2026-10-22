#!/usr/bin/env python3
import zlib
import struct
import math
import os

def unpaeth(a, b, c):
    p = a + b - c
    pa = abs(p - a)
    pb = abs(p - b)
    pc = abs(p - c)
    if pa <= pb and pa <= pc: return a
    elif pb <= pc: return b
    else: return c

def decode_png(path):
    with open(path, "rb") as f:
        data = f.read()
    idx = 8
    idat = b""
    w, h = 0, 0
    while idx < len(data):
        length = struct.unpack(">I", data[idx:idx+4])[0]
        type_ = data[idx+4:idx+8]
        if type_ == b"IHDR":
            w, h = struct.unpack(">II", data[idx+8:idx+16])
        elif type_ == b"IDAT":
            idat += data[idx+8:idx+8+length]
        idx += 12 + length

    decompressed = zlib.decompress(idat)
    bpp = 4
    stride = w * bpp
    pixels = bytearray(w * h * bpp)
    raw_idx = 0
    for y in range(h):
        filter_type = decompressed[raw_idx]
        raw_idx += 1
        line = decompressed[raw_idx : raw_idx + stride]
        raw_idx += stride
        for x in range(stride):
            filt = line[x]
            recon_x = pixels[(y * stride) + x - bpp] if x >= bpp else 0
            recon_y = pixels[((y - 1) * stride) + x] if y > 0 else 0
            recon_xy = pixels[((y - 1) * stride) + x - bpp] if (y > 0 and x >= bpp) else 0
            if filter_type == 0: val = filt
            elif filter_type == 1: val = (filt + recon_x) & 0xFF
            elif filter_type == 2: val = (filt + recon_y) & 0xFF
            elif filter_type == 3: val = (filt + ((recon_x + recon_y) // 2)) & 0xFF
            elif filter_type == 4: val = (filt + unpaeth(recon_x, recon_y, recon_xy)) & 0xFF
            pixels[(y * stride) + x] = val
    return w, h, pixels

def make_png_chunk(chunk_type, data):
    crc = zlib.crc32(chunk_type + data) & 0xffffffff
    return struct.pack(">I", len(data)) + chunk_type + data + struct.pack(">I", crc)

def save_png(path, w, h, pixels):
    # Prepare RGBA raw data with filter byte 0 (None) per line
    raw_data = bytearray()
    stride = w * 4
    for y in range(h):
        raw_data.append(0) # filter type 0
        raw_data.extend(pixels[y * stride : (y + 1) * stride])

    compressed = zlib.compress(raw_data, level=9)

    header = b"\x89PNG\r\n\x1a\n"
    ihdr_data = struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0) # 8-bit RGBA
    ihdr_chunk = make_png_chunk(b"IHDR", ihdr_data)
    idat_chunk = make_png_chunk(b"IDAT", compressed)
    iend_chunk = make_png_chunk(b"IEND", b"")

    with open(path, "wb") as f:
        f.write(header + ihdr_chunk + idat_chunk + iend_chunk)

def rescale_and_pad(w, h, src_pixels, scale_factor=0.72):
    new_w = int(w * scale_factor)
    new_h = int(h * scale_factor)
    offset_x = (w - new_w) // 2
    offset_y = (h - new_h) // 2

    out_pixels = bytearray(w * h * 4) # transparent RGBA

    for dst_y in range(new_h):
        src_y = dst_y / scale_factor
        y0 = int(math.floor(src_y))
        y1 = min(y0 + 1, h - 1)
        wy = src_y - y0

        target_y = offset_y + dst_y

        for dst_x in range(new_w):
            src_x = dst_x / scale_factor
            x0 = int(math.floor(src_x))
            x1 = min(x0 + 1, w - 1)
            wx = src_x - x0

            target_x = offset_x + dst_x

            # Bilinear interpolation for RGBA
            idx00 = (y0 * w + x0) * 4
            idx01 = (y0 * w + x1) * 4
            idx10 = (y1 * w + x0) * 4
            idx11 = (y1 * w + x1) * 4

            target_idx = (target_y * w + target_x) * 4

            for c in range(4):
                val = (1 - wx) * (1 - wy) * src_pixels[idx00 + c] + \
                      wx * (1 - wy) * src_pixels[idx01 + c] + \
                      (1 - wx) * wy * src_pixels[idx10 + c] + \
                      wx * wy * src_pixels[idx11 + c]
                out_pixels[target_idx + c] = int(round(val))

    return out_pixels

target_files = [
    "app/src/main/res/drawable/logo.png",
    "app/src/main/res/drawable/appicon.png",
    "app/src/main/res/mipmap-xxxhdpi/ic_launcher.png",
    "app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"
]

for file_path in target_files:
    if os.path.exists(file_path):
        w, h, src_pix = decode_png(file_path)
        padded_pix = rescale_and_pad(w, h, src_pix, scale_factor=0.72)
        save_png(file_path, w, h, padded_pix)
        print(f"Processed and padded: {file_path}")

print("Icon padding successfully applied to all icon drawables!")
